from datetime import datetime
from typing import List, Optional
from pydantic import BaseModel, Field

# Auth
class DriverLoginRequest(BaseModel):
    username: str
    password: Optional[str] = "password123"

class DriverProfile(BaseModel):
    id: int
    username: str
    full_name: str
    license_number: str
    assigned_route_id: str
    assigned_route_name: str
    assigned_vehicle_id: str
    shift_hours: str

# Journey / Routes
class JourneySummary(BaseModel):
    id: str
    route_number: str
    name: str
    origin: str
    destination: str
    departure_time: str
    arrival_time: str
    duration_min: int
    fare_rs: float
    is_live_gps: bool
    stops_count: int
    transfers_count: int
    transfer_station: Optional[str] = None
    is_direct: bool = True
    vehicle_plate: Optional[str] = "KA 01 F 2040"
    vehicle_id: Optional[str] = "AT-204"

class TimelineStop(BaseModel):
    sequence: int
    stop_id: str
    name: str
    code: str
    scheduled_time: str
    status: str # "departed", "boarding", "upcoming"
    is_boarding_stop: bool = False
    latitude: float
    longitude: float

class RouteTimelineResponse(BaseModel):
    route_id: str
    route_number: str
    route_name: str
    origin: str
    destination: str
    live_speed_kmh: float
    passengers_count: int
    total_capacity: int
    service_status: str
    boarding_stop_name: str
    boarding_stop_code: str
    eta_to_boarding_minutes: int
    stops: List[TimelineStop]

# Location / Tracking
class GPSLocationUpload(BaseModel):
    trip_id: str
    vehicle_id: str
    latitude: float
    longitude: float
    speed: Optional[float] = 0.0
    bearing: Optional[float] = 0.0
    accuracy: Optional[float] = 3.0
    timestamp: Optional[datetime] = None

class LiveBusLocation(BaseModel):
    trip_id: str
    vehicle_id: str
    latitude: float
    longitude: float
    speed: float
    bearing: float
    accuracy: float
    last_updated_epoch_ms: int
    is_live: bool
    next_stop_name: str
    eta_minutes: int
    route_number: str
    destination: str

# Driver Shift & Console
class ShiftStatusResponse(BaseModel):
    driver_name: str
    route_id: str
    route_number: str
    route_name: str
    origin: str
    destination: str
    vehicle_id: str
    shift_hours: str
    gps_accuracy_m: float
    gps_status: str
    lte_status: str
    checklist_total: int
    checklist_completed: int

class StartTripRequest(BaseModel):
    trip_id: str
    driver_id: int
    vehicle_id: str
    brakes_lights: bool
    doors_exits: bool
    mirrors_cabin: bool

class EndTripRequest(BaseModel):
    trip_id: str
    driver_id: int

class ActiveTripConsoleResponse(BaseModel):
    trip_id: str
    route_number: str
    vehicle_id: str
    is_gps_live: bool
    next_stop_name: str
    next_stop_platform: str
    distance_to_stop_m: int
    eta_minutes: int
    current_sequence: int
    total_stops: int
    passengers_on_board: int
    total_seats: int
    available_seats: int
    stops: List[TimelineStop]
