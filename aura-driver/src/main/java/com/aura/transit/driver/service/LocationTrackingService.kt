package com.aura.transit.driver.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.aura.transit.driver.data.model.GPSLocationUpload
import com.aura.transit.driver.data.repository.DriverRepository
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class LocationTrackingService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val repository = DriverRepository()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var activeTripId: String = "trip-line-24-001"
    private var activeVehicleId: String = "AT-204"

    companion object {
        const val CHANNEL_ID = "aura_transit_driver_tracking"
        const val NOTIFICATION_ID = 2024
        const val ACTION_START_TRACKING = "com.aura.transit.driver.START_TRACKING"
        const val ACTION_STOP_TRACKING = "com.aura.transit.driver.STOP_TRACKING"
        const val EXTRA_TRIP_ID = "extra_trip_id"
        const val EXTRA_VEHICLE_ID = "extra_vehicle_id"

        fun start(context: Context, tripId: String, vehicleId: String) {
            val intent = Intent(context, LocationTrackingService::class.java).apply {
                action = ACTION_START_TRACKING
                putExtra(EXTRA_TRIP_ID, tripId)
                putExtra(EXTRA_VEHICLE_ID, vehicleId)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, LocationTrackingService::class.java).apply {
                action = ACTION_STOP_TRACKING
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val upload = GPSLocationUpload(
                        trip_id = activeTripId,
                        vehicle_id = activeVehicleId,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        speed = if (location.hasSpeed()) (location.speed * 3.6) else 28.0, // km/h
                        bearing = if (location.hasBearing()) location.bearing.toDouble() else 0.0,
                        accuracy = if (location.hasAccuracy()) location.accuracy.toDouble() else 3.0,
                        timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date(location.time))
                    )

                    serviceScope.launch {
                        repository.uploadGps(upload)
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_TRACKING -> {
                stopTracking()
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_START_TRACKING -> {
                activeTripId = intent.getStringExtra(EXTRA_TRIP_ID) ?: "trip-line-24-001"
                activeVehicleId = intent.getStringExtra(EXTRA_VEHICLE_ID) ?: "AT-204"

                val notification = buildNotification("Live GPS Active · Transmitting coordinates")
                startForeground(NOTIFICATION_ID, notification)
                startLocationUpdates()
            }
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
            .setMinUpdateIntervalMillis(2000)
            .setWaitForAccurateLocation(false)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Permission check fallback handled in UI
        }
    }

    private fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun buildNotification(statusText: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Aura Driver — $activeVehicleId (Line 24)")
            .setContentText(statusText)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Aura Driver Trip Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Foreground GPS collection service during active transit trip"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTracking()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
