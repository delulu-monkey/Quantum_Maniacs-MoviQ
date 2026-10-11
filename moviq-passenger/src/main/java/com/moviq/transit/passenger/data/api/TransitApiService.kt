package com.moviq.transit.passenger.data.api

import com.moviq.transit.passenger.data.model.Journey
import com.moviq.transit.passenger.data.model.LiveBusLocation
import com.moviq.transit.passenger.data.model.RouteTimeline
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TransitApiService {
    @GET("api/routes/search")
    suspend fun searchRoutes(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("departure_time") departureTime: String? = null,
        @Query("sort_by") sortBy: String = "fastest"
    ): List<Journey>

    @GET("api/routes/{route_id}/timeline")
    suspend fun getRouteTimeline(
        @Path("route_id") routeId: String
    ): RouteTimeline

    @GET("api/trips/{trip_id}/location")
    suspend fun getLiveLocation(
        @Path("trip_id") tripId: String
    ): LiveBusLocation
}
