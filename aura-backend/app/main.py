from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from .database import engine, Base, SessionLocal
from .seed_data import seed_database
from .routers import auth, routes, trips, location

# Create tables
Base.metadata.create_all(bind=engine)

# Seed initial data
with SessionLocal() as db:
    seed_database(db)

app = FastAPI(
    title="Aura Transit Shared Backend",
    description="Real-time public bus transportation platform backend powering Passenger and Driver apps",
    version="1.0.0"
)

# CORS middleware for local development and Android emulators/devices
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(auth.router)
app.include_router(routes.router)
app.include_router(trips.router)
app.include_router(location.router)

@app.get("/")
def root():
    return {
        "platform": "Aura Transit",
        "status": "online",
        "docs": "/docs",
        "endpoints": {
            "routes_search": "/api/routes/search",
            "route_timeline": "/api/routes/route-24/timeline",
            "live_location": "/api/trips/trip-line-24-001/location",
            "live_websocket": "/ws/trips/{trip_id}/live",
            "driver_shift": "/api/driver/shift",
            "trip_console": "/api/trips/trip-line-24-001/console"
        }
    }
