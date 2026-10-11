from datetime import datetime
from sqlalchemy import Column, Integer, String, Float, Boolean, DateTime, ForeignKey, Text
from sqlalchemy.orm import relationship
from .database import Base

class Driver(Base):
    __tablename__ = "drivers"

    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(50), unique=True, index=True, nullable=False)
    full_name = Column(String(100), nullable=False)
    license_number = Column(String(50), nullable=False)
    is_active = Column(Boolean, default=True)

    trips = relationship("Trip", back_populates="driver")

class Vehicle(Base):
    __tablename__ = "vehicles"

    id = Column(String(50), primary_key=True, index=True) # e.g. "AT-204"
    registration_number = Column(String(50), unique=True, nullable=False)
    model = Column(String(100), default="Standard City Bus")
    total_seats = Column(Integer, default=40)
    is_active = Column(Boolean, default=True)

    trips = relationship("Trip", back_populates="vehicle")

class Route(Base):
    __tablename__ = "routes"

    id = Column(String(50), primary_key=True, index=True) # e.g. "route-24"
    route_number = Column(String(20), nullable=False, index=True) # e.g. "24"
    name = Column(String(100), nullable=False) # e.g. "Line 24"
    origin = Column(String(100), nullable=False) # "Central Station"
    destination = Column(String(100), nullable=False) # "Riverside Park"
    fare = Column(Float, default=30.0) # Rs 30
    is_active = Column(Boolean, default=True)

    stops = relationship("RouteStop", back_populates="route", order_by="RouteStop.stop_sequence")
    trips = relationship("Trip", back_populates="route")

class Stop(Base):
    __tablename__ = "stops"

    id = Column(String(50), primary_key=True, index=True) # e.g. "stop-central-station"
    name = Column(String(100), nullable=False) # "Central Station"
    code = Column(String(20), default="Stop B") # "Stop B"
    latitude = Column(Float, nullable=False)
    longitude = Column(Float, nullable=False)
    platform_info = Column(String(100), default="Boarding on the left")

    route_stops = relationship("RouteStop", back_populates="stop")

class RouteStop(Base):
    __tablename__ = "route_stops"

    id = Column(Integer, primary_key=True, index=True)
    route_id = Column(String(50), ForeignKey("routes.id"), nullable=False)
    stop_id = Column(String(50), ForeignKey("stops.id"), nullable=False)
    stop_sequence = Column(Integer, nullable=False) # 1, 2, 3...
    scheduled_offset_minutes = Column(Integer, default=0)
    scheduled_time_str = Column(String(20), default="09:48")

    route = relationship("Route", back_populates="stops")
    stop = relationship("Stop", back_populates="route_stops")

class Trip(Base):
    __tablename__ = "trips"

    id = Column(String(50), primary_key=True, index=True) # e.g. "trip-line-24-001"
    route_id = Column(String(50), ForeignKey("routes.id"), nullable=False)
    vehicle_id = Column(String(50), ForeignKey("vehicles.id"), nullable=False)
    driver_id = Column(Integer, ForeignKey("drivers.id"), nullable=False)
    
    scheduled_departure = Column(String(20), default="09:48")
    scheduled_arrival = Column(String(20), default="10:12")
    status = Column(String(20), default="ACTIVE") # "SCHEDULED", "ACTIVE", "COMPLETED"
    
    current_stop_sequence = Column(Integer, default=3) # Stop 3: Central Station
    passengers_count = Column(Integer, default=18)
    speed_kmh = Column(Float, default=28.0)
    
    started_at = Column(DateTime, nullable=True)
    ended_at = Column(DateTime, nullable=True)

    route = relationship("Route", back_populates="trips")
    vehicle = relationship("Vehicle", back_populates="trips")
    driver = relationship("Driver", back_populates="trips")
    locations = relationship("LocationUpdate", back_populates="trip", order_by="LocationUpdate.recorded_at.desc()")

class LocationUpdate(Base):
    __tablename__ = "location_updates"

    id = Column(Integer, primary_key=True, index=True)
    trip_id = Column(String(50), ForeignKey("trips.id"), nullable=False, index=True)
    vehicle_id = Column(String(50), nullable=False)
    latitude = Column(Float, nullable=False)
    longitude = Column(Float, nullable=False)
    speed = Column(Float, default=0.0)
    bearing = Column(Float, default=0.0)
    accuracy = Column(Float, default=3.0)
    recorded_at = Column(DateTime, default=datetime.utcnow)
    created_at = Column(DateTime, default=datetime.utcnow)

    trip = relationship("Trip", back_populates="locations")

class PreTripChecklist(Base):
    __tablename__ = "pretrip_checklists"

    id = Column(Integer, primary_key=True, index=True)
    trip_id = Column(String(50), nullable=False)
    driver_id = Column(Integer, nullable=False)
    brakes_lights = Column(Boolean, default=True)
    doors_exits = Column(Boolean, default=True)
    mirrors_cabin = Column(Boolean, default=True)
    completed_at = Column(DateTime, default=datetime.utcnow)
