from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from ..database import get_db
from ..models import Driver, Trip, Route, Vehicle
from ..schemas import DriverLoginRequest, DriverProfile, ShiftStatusResponse

router = APIRouter(prefix="/api/driver", tags=["Driver Auth & Shift"])

@router.post("/login", response_model=DriverProfile)
def driver_login(req: DriverLoginRequest, db: Session = Depends(get_db)):
    driver = db.query(Driver).filter(Driver.username == req.username).first()
    if not driver:
        # Fallback to default driver
        driver = db.query(Driver).first()
        if not driver:
            raise HTTPException(status_code=404, detail="Driver not found")

    return DriverProfile(
        id=driver.id,
        username=driver.username,
        full_name=driver.full_name,
        license_number=driver.license_number,
        assigned_route_id="route-24",
        assigned_route_name="Line 24",
        assigned_vehicle_id="AT-204",
        shift_hours="06:00 - 14:00"
    )

@router.get("/shift", response_model=ShiftStatusResponse)
def get_shift_status(driver_id: int = 1, db: Session = Depends(get_db)):
    driver = db.query(Driver).filter(Driver.id == driver_id).first()
    driver_name = driver.full_name.split()[0] if driver else "Arjun"

    return ShiftStatusResponse(
        driver_name=driver_name,
        route_id="route-24",
        route_number="Line 24",
        route_name="Line 24",
        origin="Central Station",
        destination="Riverside Park",
        vehicle_id="AT-204",
        shift_hours="06:00 - 14:00",
        gps_accuracy_m=3.0,
        gps_status="Ready",
        lte_status="Ready",
        checklist_total=3,
        checklist_completed=3
    )
