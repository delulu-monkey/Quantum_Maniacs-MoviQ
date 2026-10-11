package com.aura.transit.driver.data.api

import com.aura.transit.driver.data.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DriverApiService {
    @GET("api/driver/shift")
    suspend fun getShiftStatus(): ShiftStatus

    @POST("api/trips/start")
    suspend fun startTrip(@Body req: StartTripRequest): Map<String, Any>

    @POST("api/trips/end")
    suspend fun endTrip(@Body req: EndTripRequest): Map<String, Any>

    @POST("api/location/update")
    suspend fun uploadGpsLocation(@Body data: GPSLocationUpload): Map<String, Any>

    @GET("api/trips/{trip_id}/console")
    suspend fun getActiveTripConsole(@Path("trip_id") tripId: String): ActiveTripConsole
}
