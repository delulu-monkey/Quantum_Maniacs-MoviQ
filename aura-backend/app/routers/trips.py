from datetime import datetime
from typing import List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from ..database import get_db
from ..models import Trip, RouteStop, Route, PreTripChecklist
from ..schemas import StartTripRequest, EndTripRequest, ActiveTripConsoleResponse, TimelineStop

router = APIRouter(prefix="/api/trips", tags=["Trips"])

@router.post("/start")
def start_trip(req: StartTripRequest, db: Session = Depends(get_db)):
    trip = db.query(Trip).filter(Trip.id == req.trip_id).first()
    if not trip:
        trip = Trip(
            id=req.trip_id,
            route_id="route-24",
            vehicle_id=req.vehicle_id,
            driver_id=req.driver_id,
            status="ACTIVE",
            current_stop_sequence=3,
            passengers_count=18,
            speed_kmh=28.0,
            started_at=datetime.utcnow()
        )
        db.add(trip)
    else:
        trip.status = "ACTIVE"
        trip.started_at = datetime.utcnow()

    # Save checklist
    checklist = PreTripChecklist(
        trip_id=req.trip_id,
        driver_id=req.driver_id,
        brakes_lights=req.brakes_lights,
        doors_exits=req.doors_exits,
        mirrors_cabin=req.mirrors_cabin
    )
    db.add(checklist)
    db.commit()

    return {"status": "success", "message": f"Trip {req.trip_id} started successfully", "trip_id": req.trip_id}

@router.post("/end")
def end_trip(req: EndTripRequest, db: Session = Depends(get_db)):
    trip = db.query(Trip).filter(Trip.id == req.trip_id).first()
    if not trip:
        raise HTTPException(status_code=404, detail="Trip not found")

    trip.status = "COMPLETED"
    trip.ended_at = datetime.utcnow()
    db.commit()

    return {"status": "success", "message": f"Trip {req.trip_id} ended"}

@router.get("/{trip_id}/console", response_model=ActiveTripConsoleResponse)
def get_active_trip_console(trip_id: str = "trip-line-24-001", db: Session = Depends(get_db)):
    trip = db.query(Trip).filter(Trip.id == trip_id).first()
    if not trip:
        trip = db.query(Trip).first()

    route_stops = db.query(RouteStop).filter(RouteStop.route_id == "route-24").order_by(RouteStop.stop_sequence).all()

    timeline_stops: List[TimelineStop] = []
    for rs in route_stops:
        s = rs.stop
        timeline_stops.append(TimelineStop(
            sequence=rs.stop_sequence,
            stop_id=s.id,
            name=s.name,
            code=s.code,
            scheduled_time=rs.scheduled_time_str,
            status="upcoming",
            is_boarding_stop=(rs.stop_sequence == 3),
            latitude=s.latitude,
            longitude=s.longitude
        ))

    return ActiveTripConsoleResponse(
        trip_id=trip.id if trip else "trip-line-24-001",
        route_number="Line 24",
        vehicle_id=trip.vehicle_id if trip else "AT-204",
        is_gps_live=True,
        next_stop_name="Central Station",
        next_stop_platform="Stop B · Boarding on the left",
        distance_to_stop_m=450,
        eta_minutes=2,
        current_sequence=3,
        total_stops=6,
        passengers_on_board=18,
        total_seats=40,
        available_seats=22,
        stops=timeline_stops
    )
