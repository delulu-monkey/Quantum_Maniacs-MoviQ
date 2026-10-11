package com.aura.transit.passenger.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.transit.passenger.BuildConfig
import com.aura.transit.passenger.R
import com.aura.transit.passenger.ui.theme.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun LiveBusMapScreen(
    busTitle: String = "Bus 24 · Riverside Park",
    nextStopName: String = "AIT Pune",
    nextStopCode: String = "Stop B",
    etaMinutes: Int = 4,
    busLat: Double = 12.9745,
    busLng: Double = 77.5925,
    onBack: () -> Unit = {},
    onViewFullRoute: () -> Unit = {},
    onBoardAtStop: () -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current
    val hasCustomKey = BuildConfig.MAPS_API_KEY.isNotBlank() &&
            !BuildConfig.MAPS_API_KEY.contains("DEMO") &&
            !BuildConfig.MAPS_API_KEY.contains("REPLACE")

    var useGoogleMap by remember { mutableStateOf(hasCustomKey && !isPreview) }

    val centralStation = LatLng(12.9755, 77.5935)
    val busLocation = LatLng(busLat, busLng)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(busLocation, 15f)
    }

    Scaffold(
        containerColor = MainBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // Header Top Bar - Aligned cleanly to top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimaryText
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = busTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    modifier = Modifier.weight(1f)
                )
            }

            // Map Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF131A24))
            ) {
                if (useGoogleMap && !isPreview) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false
                        )
                    ) {
                        Polyline(
                            points = listOf(
                                LatLng(12.9690, 77.5840),
                                LatLng(12.9720, 77.5880),
                                centralStation,
                                LatLng(12.9785, 77.5995),
                                LatLng(12.9815, 77.6050),
                                LatLng(12.9870, 77.6110)
                            ),
                            color = PrimaryBlue,
                            width = 12f
                        )
                        Marker(
                            state = MarkerState(position = busLocation),
                            title = "Bus 24 (Live)",
                            snippet = "Arriving in $etaMinutes min"
                        )
                        Marker(
                            state = MarkerState(position = centralStation),
                            title = "Central Station · Stop B"
                        )
                    }
                } else {
                    // Display the provided transit map image
                    Image(
                        painter = painterResource(id = R.drawable.ait_pune_transit_map),
                        contentDescription = "Live Transit Route Map",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Map notification pill if using demo key
                if (!hasCustomKey && useGoogleMap) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = White.copy(alpha = 0.95f),
                        shadowElevation = 2.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                    ) {
                        Text(
                            text = "⚡ Local dev key active · Add Google Maps key to local.properties",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = SecondaryText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Floating Recenter Pill Button at bottom right of map area
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                        .clickable {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(busLocation, 15f)
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Text(
                        text = "Recenter",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryText,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            // Bottom Information Card / Sheet
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Drag handle indicator
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(BorderColor)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Arrival Header
                    Text(
                        text = "Arriving in $etaMinutes min",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$nextStopName · $nextStopCode",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Next stops header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next stops",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = "Full route",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue,
                            modifier = Modifier.clickable(onClick = onViewFullRoute)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stop Item 1: AIT Pune
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .border(2.dp, PrimaryBlue, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "AIT Pune",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryText
                            )
                        }
                        Text(
                            text = "09:48",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stop Item 2: Deccan Collage
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .border(2.dp, PrimaryBlue, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Deccan Collage",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryText
                            )
                        }
                        Text(
                            text = "09:54",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Button
                    Button(
                        onClick = onBoardAtStop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text(
                            text = "Board at $nextStopName",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LiveBusMapScreenPreview() {
    AuraTransitTheme {
        LiveBusMapScreen()
    }
}
