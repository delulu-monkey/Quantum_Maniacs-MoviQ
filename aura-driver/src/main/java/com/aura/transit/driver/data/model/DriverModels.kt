package com.aura.transit.driver.data.model

data class DriverProfile(
    val id: Int,
    val username: String,
    val full_name: String,
    val license_number: String,
    val assigned_route_id: String,
    val assigned_route_name: String,
    val assigned_vehicle_id: String,
    val shift_hours: String
)

data class ShiftStatus(
    val driver_name: String = "Arjun",
    val route_id: String = "route-24",
    val route_number: String = "Line 24",
    val route_name: String = "Line 24",
    val origin: String = "Central Station",
    val destination: String = "Riverside Park",
    val vehicle_id: String = "AT-204",
    val shift_hours: String = "06:00 - 14:00",
    val gps_accuracy_m: Double = 3.0,
    val gps_status: String = "Ready",
    val lte_status: String = "Ready",
    val checklist_total: Int = 3,
    val checklist_completed: Int = 3
)

data class StartTripRequest(
    val trip_id: String,
    val driver_id: Int,
    val vehicle_id: String,
    val brakes_lights: Boolean,
    val doors_exits: Boolean,
    val mirrors_cabin: Boolean
)

data class EndTripRequest(
    val trip_id: String,
    val driver_id: Int
)

data class GPSLocationUpload(
    val trip_id: String,
    val vehicle_id: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Double = 0.0,
    val bearing: Double = 0.0,
    val accuracy: Double = 3.0,
    val timestamp: String? = null
)

data class StopSequenceItem(
    val sequence: Int,
    val name: String,
    val timeInfo: String,
    val isCurrentNext: Boolean
)

data class ActiveTripConsole(
    val trip_id: String,
    val route_number: String = "Line 24",
    val vehicle_id: String = "AT-204",
    val is_gps_live: Boolean = true,
    val next_stop_name: String = "Central Station",
    val next_stop_platform: String = "Stop B · Boarding on the left",
    val distance_to_stop_m: Int = 450,
    val eta_minutes: Int = 2,
    val current_sequence: Int = 3,
    val total_stops: Int = 6,
    val passengers_on_board: Int = 18,
    val total_seats: Int = 40,
    val available_seats: Int = 22,
    val stops: List<StopSequenceItem> = emptyList()
)
