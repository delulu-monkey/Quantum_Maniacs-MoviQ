package com.aura.transit.passenger.data.model

data class Journey(
    val id: String,
    val route_number: String,
    val name: String,
    val origin: String,
    val destination: String,
    val departure_time: String,
    val arrival_time: String,
    val duration_min: Int = 0,
    val fare_rs: Double,
    val is_live_gps: Boolean,
    val stops_count: Int,
    val transfers_count: Int,
    val transfer_station: String? = null,
    val is_direct: Boolean = true,
    val vehicle_plate: String = "KA 01 F 2040",
    val vehicle_id: String = "AT-204"
)

data class TimelineStop(
    val sequence: Int,
    val stop_id: String,
    val name: String,
    val code: String,
    val scheduled_time: String,
    val status: String, // "departed", "boarding", "upcoming"
    val is_boarding_stop: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class RouteTimeline(
    val route_id: String,
    val route_number: String,
    val route_name: String,
    val origin: String,
    val destination: String,
    val live_speed_kmh: Double,
    val passengers_count: Int,
    val total_capacity: Int,
    val service_status: String,
    val boarding_stop_name: String,
    val boarding_stop_code: String,
    val eta_to_boarding_minutes: Int,
    val stops: List<TimelineStop>
)

data class LiveBusLocation(
    val trip_id: String,
    val vehicle_id: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Double,
    val bearing: Double,
    val accuracy: Double,
    val last_updated_epoch_ms: Long,
    val is_live: Boolean,
    val next_stop_name: String,
    val eta_minutes: Int,
    val route_number: String,
    val destination: String
)

data class RecentSearch(
    val origin: String,
    val destination: String
)

data class NearbyStop(
    val name: String,
    val code: String,
    val distanceMeters: Int,
    val walkMinutes: Int
)
