from datetime import datetime
from sqlalchemy.orm import Session
from .models import Driver, Vehicle, Route, Stop, RouteStop, Trip, LocationUpdate

def seed_database(db: Session):
    # Check if already seeded
    if db.query(Driver).first():
        return

    # 1. Driver
    driver = Driver(
        id=1,
        username="arjun",
        full_name="Arjun Sharma",
        license_number="DL-2024-998811",
        is_active=True
    )
    db.add(driver)

    # 2. Vehicle
    vehicle = Vehicle(
        id="AT-204",
        registration_number="KA-01-F-2040",
        model="Tata Starbus EV",
        total_seats=40,
        is_active=True
    )
    db.add(vehicle)

    # 3. Routes
    route24 = Route(
        id="route-24",
        route_number="24",
        name="Line 24",
        origin="Central Station",
        destination="Riverside Park",
        fare=30.0,
        is_active=True
    )
    db.add(route24)

    route18 = Route(
        id="route-18-6",
        route_number="18 → 6",
        name="Line 18 to 6",
        origin="Central Station",
        destination="Riverside Park",
        fare=25.0,
        is_active=True
    )
    db.add(route18)

    # 4. Stops
    stops_data = [
        {"id": "stop-depot-road", "name": "Depot Road", "code": "Stop A", "lat": 12.9690, "lng": 77.5840, "platform": "Platform 1", "seq": 1, "sched": "09:36"},
        {"id": "stop-city-library", "name": "City Library", "code": "Stop C", "lat": 12.9720, "lng": 77.5880, "platform": "Platform 2", "seq": 2, "sched": "09:40"},
        {"id": "stop-central-station", "name": "Central Station", "code": "Stop B", "lat": 12.9755, "lng": 77.5935, "platform": "Stop B · Boarding on the left", "seq": 3, "sched": "09:48"},
        {"id": "stop-museum-road", "name": "Museum Road", "code": "Stop D", "lat": 12.9785, "lng": 77.5995, "platform": "Stop D · Eastbound", "seq": 4, "sched": "09:54"},
        {"id": "stop-market-square", "name": "Market Square", "code": "Stop E", "lat": 12.9815, "lng": 77.6050, "platform": "Stop E · North side", "seq": 5, "sched": "10:01"},
        {"id": "stop-riverside-park", "name": "Riverside Park", "code": "Stop F", "lat": 12.9870, "lng": 77.6110, "platform": "Stop F · Terminal", "seq": 6, "sched": "10:12"},
    ]

    for s in stops_data:
        stop = Stop(
            id=s["id"],
            name=s["name"],
            code=s["code"],
            latitude=s["lat"],
            longitude=s["lng"],
            platform_info=s["platform"]
        )
        db.add(stop)

        route_stop = RouteStop(
            route_id="route-24",
            stop_id=s["id"],
            stop_sequence=s["seq"],
            scheduled_time_str=s["sched"]
        )
        db.add(route_stop)

    # 5. Active Trip
    trip = Trip(
        id="trip-line-24-001",
        route_id="route-24",
        vehicle_id="AT-204",
        driver_id=1,
        scheduled_departure="09:48",
        scheduled_arrival="10:12",
        status="ACTIVE",
        current_stop_sequence=3,
        passengers_count=18,
        speed_kmh=28.0,
        started_at=datetime.utcnow()
    )
    db.add(trip)

    # 6. Initial Location (just approaching Central Station)
    init_loc = LocationUpdate(
        trip_id="trip-line-24-001",
        vehicle_id="AT-204",
        latitude=12.9740,
        longitude=77.5920,
        speed=28.0,
        bearing=55.0,
        accuracy=3.0,
        recorded_at=datetime.utcnow(),
        created_at=datetime.utcnow()
    )
    db.add(init_loc)

    db.commit()
