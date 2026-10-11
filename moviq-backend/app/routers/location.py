import asyncio
import json
import time
from typing import Dict, List
from fastapi import APIRouter, Depends, WebSocket, WebSocketDisconnect, HTTPException
from sqlalchemy.orm import Session
from datetime import datetime
from ..database import get_db
from ..models import LocationUpdate, Trip
from ..schemas import GPSLocationUpload, LiveBusLocation

router = APIRouter(prefix="", tags=["Real-time GPS Tracking"])

# Connection manager for WebSockets
class ConnectionManager:
    def __init__(self):
        # trip_id -> list of active WebSocket connections
        self.active_connections: Dict[str, List[WebSocket]] = {}

    async def connect(self, trip_id: str, websocket: WebSocket):
        await websocket.accept()
        if trip_id not in self.active_connections:
            self.active_connections[trip_id] = []
        self.active_connections[trip_id].append(websocket)

    def disconnect(self, trip_id: str, websocket: WebSocket):
        if trip_id in self.active_connections:
            if websocket in self.active_connections[trip_id]:
                self.active_connections[trip_id].remove(websocket)
            if not self.active_connections[trip_id]:
                del self.active_connections[trip_id]

    async def broadcast_location(self, trip_id: str, message: dict):
        if trip_id in self.active_connections:
            for connection in list(self.active_connections[trip_id]):
                try:
                    await connection.send_text(json.dumps(message))
                except Exception:
                    self.disconnect(trip_id, connection)

manager = ConnectionManager()

@router.post("/api/location/update")
async def update_location(data: GPSLocationUpload, db: Session = Depends(get_db)):
    # Validate active trip
    trip = db.query(Trip).filter(Trip.id == data.trip_id).first()
    if not trip:
        # Auto-create or fetch active trip if in demo mode
        trip = db.query(Trip).first()

    rec_time = data.timestamp or datetime.utcnow()

    # Store in database
    loc_entry = LocationUpdate(
        trip_id=data.trip_id,
        vehicle_id=data.vehicle_id,
        latitude=data.latitude,
        longitude=data.longitude,
        speed=data.speed or 28.0,
        bearing=data.bearing or 0.0,
        accuracy=data.accuracy or 3.0,
        recorded_at=rec_time,
        created_at=datetime.utcnow()
    )
    db.add(loc_entry)
    
    if trip:
        trip.speed_kmh = data.speed or 28.0
    
    db.commit()

    # Prepare broadcast payload
    payload = {
        "trip_id": data.trip_id,
        "vehicle_id": data.vehicle_id,
        "latitude": data.latitude,
        "longitude": data.longitude,
        "speed": data.speed or 28.0,
        "bearing": data.bearing or 0.0,
        "accuracy": data.accuracy or 3.0,
        "last_updated_epoch_ms": int(time.time() * 1000),
        "is_live": True,
        "next_stop_name": "Central Station",
        "eta_minutes": 4,
        "route_number": "24",
        "destination": "Riverside Park"
    }

    # Broadcast to all live passenger listeners on WebSocket
    await manager.broadcast_location(data.trip_id, payload)

    return {"status": "success", "received_at": rec_time.isoformat()}

@router.get("/api/trips/{trip_id}/location", response_model=LiveBusLocation)
def get_trip_location(trip_id: str = "trip-line-24-001", db: Session = Depends(get_db)):
    latest = db.query(LocationUpdate).filter(
        LocationUpdate.trip_id == trip_id
    ).order_by(LocationUpdate.recorded_at.desc()).first()

    if not latest:
        # Default mock location near Central Station (lat: 12.9740, lng: 77.5920)
        return LiveBusLocation(
            trip_id=trip_id,
            vehicle_id="AT-204",
            latitude=12.9740,
            longitude=77.5920,
            speed=28.0,
            bearing=55.0,
            accuracy=3.0,
            last_updated_epoch_ms=int(time.time() * 1000),
            is_live=True,
            next_stop_name="Central Station",
            eta_minutes=4,
            route_number="24",
            destination="Riverside Park"
        )

    # Check freshness (within last 120 seconds)
    age_seconds = (datetime.utcnow() - latest.recorded_at).total_seconds()
    is_live = age_seconds < 120

    return LiveBusLocation(
        trip_id=trip_id,
        vehicle_id=latest.vehicle_id,
        latitude=latest.latitude,
        longitude=latest.longitude,
        speed=latest.speed,
        bearing=latest.bearing,
        accuracy=latest.accuracy,
        last_updated_epoch_ms=int(latest.recorded_at.timestamp() * 1000),
        is_live=is_live,
        next_stop_name="Central Station",
        eta_minutes=4,
        route_number="24",
        destination="Riverside Park"
    )

@router.websocket("/ws/trips/{trip_id}/live")
async def websocket_trip_live(websocket: WebSocket, trip_id: str):
    await manager.connect(trip_id, websocket)
    try:
        # Send initial latest location upon subscription
        db = next(get_db())
        try:
            latest = db.query(LocationUpdate).filter(
                LocationUpdate.trip_id == trip_id
            ).order_by(LocationUpdate.recorded_at.desc()).first()
            if latest:
                init_msg = {
                    "trip_id": trip_id,
                    "vehicle_id": latest.vehicle_id,
                    "latitude": latest.latitude,
                    "longitude": latest.longitude,
                    "speed": latest.speed,
                    "bearing": latest.bearing,
                    "accuracy": latest.accuracy,
                    "last_updated_epoch_ms": int(latest.recorded_at.timestamp() * 1000),
                    "is_live": True,
                    "next_stop_name": "Central Station",
                    "eta_minutes": 4,
                    "route_number": "24",
                    "destination": "Riverside Park"
                }
                await websocket.send_text(json.dumps(init_msg))
        finally:
            db.close()

        # Keep connection open waiting for updates or client heartbeats
        while True:
            data = await websocket.receive_text()
            if data == "ping":
                await websocket.send_text("pong")
    except WebSocketDisconnect:
        manager.disconnect(trip_id, websocket)
    except Exception:
        manager.disconnect(trip_id, websocket)
