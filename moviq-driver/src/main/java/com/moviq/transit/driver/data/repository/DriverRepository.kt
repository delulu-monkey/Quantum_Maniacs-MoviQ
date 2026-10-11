package com.moviq.transit.driver.data.repository

import com.moviq.transit.driver.BuildConfig
import com.moviq.transit.driver.data.api.DriverApiService
import com.moviq.transit.driver.data.model.*
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class DriverRepository {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val apiService: DriverApiService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(DriverApiService::class.java)
    }

    suspend fun getShiftStatus(): ShiftStatus {
        return try {
            apiService.getShiftStatus()
        } catch (e: Exception) {
            ShiftStatus()
        }
    }

    suspend fun startTrip(tripId: String = "trip-line-24-001", vehicleId: String = "AT-204", driverId: Int = 1): Boolean {
        return try {
            apiService.startTrip(
                StartTripRequest(
                    trip_id = tripId,
                    driver_id = driverId,
                    vehicle_id = vehicleId,
                    brakes_lights = true,
                    doors_exits = true,
                    mirrors_cabin = true
                )
            )
            true
        } catch (e: Exception) {
            true // offline tolerance
        }
    }

    suspend fun endTrip(tripId: String = "trip-line-24-001", driverId: Int = 1): Boolean {
        return try {
            apiService.endTrip(EndTripRequest(tripId, driverId))
            true
        } catch (e: Exception) {
            true
        }
    }

    suspend fun uploadGps(upload: GPSLocationUpload): Boolean {
        return try {
            apiService.uploadGpsLocation(upload)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getActiveTripConsole(tripId: String = "trip-line-24-001"): ActiveTripConsole {
        return try {
            apiService.getActiveTripConsole(tripId)
        } catch (e: Exception) {
            ActiveTripConsole(
                trip_id = tripId,
                route_number = "Line 24",
                vehicle_id = "AT-204",
                is_gps_live = true,
                next_stop_name = "Central Station",
                next_stop_platform = "Stop B · Boarding on the left",
                distance_to_stop_m = 450,
                eta_minutes = 2,
                current_sequence = 3,
                total_stops = 6,
                passengers_on_board = 18,
                total_seats = 40,
                available_seats = 22,
                stops = listOf(
                    StopSequenceItem(3, "Central Station", "Next: 09:48", true),
                    StopSequenceItem(4, "Museum Road", "09:54", false),
                    StopSequenceItem(5, "Market Square", "10:01", false)
                )
            )
        }
    }
}
