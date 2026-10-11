from typing import List, Optional
from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session
from ..database import get_db
from ..models import Route, RouteStop, Stop, Trip
from ..schemas import JourneySummary, RouteTimelineResponse, TimelineStop

router = APIRouter(prefix="/api/routes", tags=["Routes & Passenger Search"])

@router.get("/search", response_model=List[JourneySummary])
def search_routes(
    origin: str = Query("Central Station"),
    destination: str = Query("Riverside Park"),
    departure_time: Optional[str] = Query(None),
    sort_by: Optional[str] = Query("fastest"), # fastest, cheapest, fewest_transfers
    db: Session = Depends(get_db)
):
    # Journeys matching the UI design:
    journeys = [
        JourneySummary(
            id="journey-24-01",
            route_number="24",
            name="Line 24",
            origin="Central Station",
            destination="Riverside Park",
            departure_time="09:48",
            arrival_time="10:12",
            duration_min=24,
            fare_rs=30.0,
            is_live_gps=True,
            stops_count=5,
            transfers_count=0,
            transfer_station=None,
            is_direct=True,
            vehicle_plate="KA 01 F 2040",
            vehicle_id="AT-204"
        ),
        JourneySummary(
            id="journey-18-6-01",
            route_number="18 → 6",
            name="Line 18 to 6",
            origin="Central Station",
            destination="Riverside Park",
            departure_time="09:52",
            arrival_time="10:23",
            duration_min=31,
            fare_rs=25.0,
            is_live_gps=False,
            stops_count=7,
            transfers_count=1,
            transfer_station="Market Square",
            is_direct=False,
            vehicle_plate="KA 01 E 8832",
            vehicle_id="AT-115"
        ),
        JourneySummary(
            id="journey-24-02",
            route_number="24",
            name="Line 24",
            origin="Central Station",
            destination="Riverside Park",
            departure_time="10:05",
            arrival_time="10:34",
            duration_min=29,
            fare_rs=30.0,
            is_live_gps=True,
            stops_count=5,
            transfers_count=0,
            transfer_station=None,
            is_direct=True,
            vehicle_plate="KA 01 F 2048",
            vehicle_id="AT-212"
        )
    ]

    # Handle genuine sorting
    if sort_by == "cheapest":
        journeys.sort(key=lambda j: j.fare_rs)
    elif sort_by == "fewest_transfers":
        journeys.sort(key=lambda j: (j.transfers_count, j.duration_min))
    else: # fastest
        journeys.sort(key=lambda j: j.duration_min)

    return journeys

@router.get("/{route_id}/timeline", response_model=RouteTimelineResponse)
def get_route_timeline(route_id: str = "route-24", db: Session = Depends(get_db)):
    route = db.query(Route).filter(Route.id == route_id).first()
    active_trip = db.query(Trip).filter(Trip.route_id == route_id, Trip.status == "ACTIVE").first()

    route_stops = db.query(RouteStop).filter(RouteStop.route_id == route_id).order_by(RouteStop.stop_sequence).all()

    current_seq = active_trip.current_stop_sequence if active_trip else 3
    live_speed = active_trip.speed_kmh if active_trip else 28.0
    passengers = active_trip.passengers_count if active_trip else 18

    timeline_stops: List[TimelineStop] = []
    for rs in route_stops:
        stop = rs.stop
        # Status calculation based on progression
        if rs.stop_sequence < current_seq:
            status = "departed"
        elif rs.stop_sequence == current_seq:
            status = "boarding"
        else:
            status = "upcoming"

        timeline_stops.append(TimelineStop(
            sequence=rs.stop_sequence,
            stop_id=stop.id,
            name=stop.name,
            code=stop.code,
            scheduled_time=rs.scheduled_time_str,
            status=status,
            is_boarding_stop=(rs.stop_sequence == 3), # Central Station
            latitude=stop.latitude,
            longitude=stop.longitude
        ))

    return RouteTimelineResponse(
        route_id=route.id if route else "route-24",
        route_number="Route 24",
        route_name="Line 24",
        origin="Central Station",
        destination="Riverside Park",
        live_speed_kmh=live_speed,
        passengers_count=passengers,
        total_capacity=40,
        service_status="On time",
        boarding_stop_name="Central Station",
        boarding_stop_code="Stop B",
        eta_to_boarding_minutes=4,
        stops=timeline_stops
    )
