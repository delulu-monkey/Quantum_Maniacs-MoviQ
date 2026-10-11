package com.aura.transit.passenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aura.transit.passenger.data.repository.TransitRepository
import com.aura.transit.passenger.ui.screens.AccountScreen
import com.aura.transit.passenger.ui.screens.HomeScreen
import com.aura.transit.passenger.ui.screens.LiveBusMapScreen
import com.aura.transit.passenger.ui.screens.RouteTimelineScreen
import com.aura.transit.passenger.ui.screens.SearchResultsScreen
import com.aura.transit.passenger.ui.screens.TripsScreen
import com.aura.transit.passenger.ui.theme.AuraTransitTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val repository = TransitRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuraTransitTheme {
                val navController = rememberNavController()

                var originParam by remember { mutableStateOf("Central Station") }
                var destinationParam by remember { mutableStateOf("Riverside Park") }
                var liveLat by remember { mutableStateOf(12.9745) }
                var liveLng by remember { mutableStateOf(77.5925) }

                // Helper to switch bottom navigation tabs
                fun navigateToTab(tab: String) {
                    when (tab) {
                        "home" -> {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                        "trips" -> {
                            navController.navigate("trips") {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                        "account" -> {
                            navController.navigate("account") {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    }
                }

                // Observe live WebSocket updates for Bus 24
                LaunchedEffect(Unit) {
                    try {
                        repository.observeLiveLocation("trip-line-24-001").collectLatest { loc ->
                            liveLat = loc.latitude
                            liveLng = loc.longitude
                        }
                    } catch (e: Exception) {
                        // Keep initial GPS
                    }
                }

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onSearchBuses = { origin, dest ->
                                originParam = origin
                                destinationParam = dest
                                navController.navigate("search_results")
                            },
                            onSelectNearbyStop = { stop ->
                                originParam = "Central Station"
                                navController.navigate("search_results")
                            },
                            onOpenAccount = {
                                navController.navigate("account")
                            },
                            onNavigateTab = { tab ->
                                navigateToTab(tab)
                            }
                        )
                    }

                    composable("trips") {
                        TripsScreen(
                            onTrackLiveMap = {
                                navController.navigate("live_bus_map")
                            },
                            onViewTimeline = {
                                navController.navigate("route_timeline")
                            },
                            onRebookTrip = { origin, dest ->
                                originParam = origin
                                destinationParam = dest
                                navController.navigate("search_results")
                            },
                            onNavigateTab = { tab ->
                                navigateToTab(tab)
                            }
                        )
                    }

                    composable("account") {
                        AccountScreen(
                            onSelectSavedPlace = { origin, dest ->
                                originParam = origin
                                destinationParam = dest
                                navController.navigate("search_results")
                            },
                            onNavigateTab = { tab ->
                                navigateToTab(tab)
                            }
                        )
                    }

                    composable("search_results") {
                        SearchResultsScreen(
                            origin = originParam,
                            destination = destinationParam,
                            onBack = { navController.popBackStack() },
                            onEditSearch = { navController.popBackStack() },
                            onSelectJourney = { journey ->
                                navController.navigate("live_bus_map")
                            },
                            onNavigateTab = { tab ->
                                navigateToTab(tab)
                            }
                        )
                    }

                    composable("live_bus_map") {
                        LiveBusMapScreen(
                            busLat = liveLat,
                            busLng = liveLng,
                            onBack = { navController.popBackStack() },
                            onViewFullRoute = { navController.navigate("route_timeline") },
                            onBoardAtStop = { navController.navigate("route_timeline") }
                        )
                    }

                    composable("route_timeline") {
                        RouteTimelineScreen(
                            onBack = { navController.popBackStack() },
                            onNavigateTab = { tab ->
                                navigateToTab(tab)
                            }
                        )
                    }
                }
            }
        }
    }
}
