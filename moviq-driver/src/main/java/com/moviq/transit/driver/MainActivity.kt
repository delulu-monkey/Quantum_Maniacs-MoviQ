package com.moviq.transit.driver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviq.transit.driver.data.repository.DriverRepository
import com.moviq.transit.driver.service.LocationTrackingService
import com.moviq.transit.driver.ui.screens.ActiveTripConsoleScreen
import com.moviq.transit.driver.ui.screens.ShiftPreTripScreen
import com.moviq.transit.driver.ui.theme.MoviQDriverTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val repository = DriverRepository()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (!fineLocationGranted) {
            Toast.makeText(this, "GPS location permission is required for active bus route tracking", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRequiredPermissions()

        setContent {
            MoviQDriverTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "shift_pre_trip") {
                    composable("shift_pre_trip") {
                        ShiftPreTripScreen(
                            onStartTrip = {
                                // 1. Start real foreground GPS tracking
                                LocationTrackingService.start(
                                    context = this@MainActivity,
                                    tripId = "trip-line-24-001",
                                    vehicleId = "AT-204"
                                )

                                // 2. Notify backend of trip start
                                lifecycleScope.launch {
                                    repository.startTrip("trip-line-24-001", "AT-204", 1)
                                }

                                // 3. Navigate to Active Trip Console
                                navController.navigate("active_trip_console")
                            }
                        )
                    }

                    composable("active_trip_console") {
                        ActiveTripConsoleScreen(
                            onEndService = {
                                // 1. Stop GPS foreground tracking
                                LocationTrackingService.stop(this@MainActivity)

                                // 2. Notify backend of trip completion
                                lifecycleScope.launch {
                                    repository.endTrip("trip-line-24-001", 1)
                                }

                                // 3. Return to shift & pre-trip
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            permissionLauncher.launch(missing.toTypedArray())
        }
    }
}
