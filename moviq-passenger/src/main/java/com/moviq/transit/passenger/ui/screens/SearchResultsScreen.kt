package com.aura.transit.passenger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.transit.passenger.data.model.Journey
import com.aura.transit.passenger.ui.components.AuraBottomNavigation
import com.aura.transit.passenger.ui.theme.*

@Composable
fun SearchResultsScreen(
    origin: String = "Central Station",
    destination: String = "Riverside Park",
    onBack: () -> Unit = {},
    onEditSearch: () -> Unit = {},
    onSelectJourney: (Journey) -> Unit = {},
    onNavigateTab: (String) -> Unit = {}
) {
    var selectedSort by remember { mutableStateOf("Fastest") }

    // Seed mock journeys matching the design
    val allJourneys = remember {
        listOf(
            Journey(
                id = "journey-24-01",
                route_number = "24",
                name = "Line 24",
                origin = origin,
                destination = destination,
                departure_time = "09:48",
                arrival_time = "10:12",
                duration_min = 24,
                fare_rs = 30.0,
                is_live_gps = true,
                stops_count = 5,
                transfers_count = 0,
                is_direct = true,
                vehicle_plate = "KA 01 F 2040",
                vehicle_id = "AT-204"
            ),
            Journey(
                id = "journey-18-6-01",
                route_number = "18 → 6",
                name = "Line 18 to 6",
                origin = origin,
                destination = destination,
                departure_time = "09:52",
                arrival_time = "10:23",
                duration_min = 31,
                fare_rs = 25.0,
                is_live_gps = false,
                stops_count = 7,
                transfers_count = 1,
                transfer_station = "Market Square",
                is_direct = false,
                vehicle_plate = "KA 01 E 8832",
                vehicle_id = "AT-115"
            ),
            Journey(
                id = "journey-24-02",
                route_number = "24",
                name = "Line 24",
                origin = origin,
                destination = destination,
                departure_time = "10:05",
                arrival_time = "10:34",
                duration_min = 29,
                fare_rs = 30.0,
                is_live_gps = true,
                stops_count = 5,
                transfers_count = 0,
                is_direct = true,
                vehicle_plate = "KA 01 F 2048",
                vehicle_id = "AT-212"
            )
        )
    }

    val sortedJourneys = remember(selectedSort, allJourneys) {
        when (selectedSort) {
            "Cheapest" -> allJourneys.sortedBy { it.fare_rs }
            "Fewest transfers" -> allJourneys.sortedWith(compareBy({ it.transfers_count }, { it.duration_min }))
            else -> allJourneys.sortedBy { it.duration_min }
        }
    }

    Scaffold(
        containerColor = MainBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AuraBottomNavigation(
                selectedTab = "trips",
                onSelectTab = onNavigateTab
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimaryText
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Your journey",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Text(
                        text = "$origin → $destination",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
            }

            // Subheader: Date & Edit search
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today, leaving now",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
                Text(
                    text = "Edit search",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue,
                    modifier = Modifier.clickable(onClick = onEditSearch)
                )
            }

            // Sorting Tabs (Fastest, Cheapest, Fewest transfers)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .background(Color(0xFFEEF2F6), shape = RoundedCornerShape(8.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Fastest", "Cheapest", "Fewest transfers").forEach { tab ->
                    val isSelected = selectedSort == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) White else Color.Transparent)
                            .clickable { selectedSort = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) PrimaryText else SecondaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Journeys count
            Text(
                text = "${sortedJourneys.size} journeys available",
                fontSize = 12.sp,
                color = SecondaryText,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            // Journey List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(sortedJourneys) { journey ->
                    JourneyResultCard(
                        journey = journey,
                        onClick = { onSelectJourney(journey) }
                    )
                }
            }
        }
    }
}

@Composable
fun JourneyResultCard(
    journey: Journey,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Time & Fare Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = journey.departure_time,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Text(
                        text = "  •  ",
                        fontSize = 16.sp,
                        color = SecondaryText
                    )
                    Text(
                        text = journey.arrival_time,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                }

                Text(
                    text = "Rs ${journey.fare_rs.toInt()}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            }

            // Labels row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = "Departure",
                        fontSize = 10.sp,
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.width(36.dp))
                    Text(
                        text = "Arrival",
                        fontSize = 10.sp,
                        color = SecondaryText
                    )
                }

                Text(
                    text = "${journey.duration_min} min",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bus badge, Vehicle number plate and live GPS tag row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.DirectionsBus,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = journey.route_number,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    // Vehicle Number Plate Badge
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "IND",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = journey.vehicle_plate,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = PrimaryText
                            )
                        }
                    }
                }

                if (journey.is_live_gps) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Live GPS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                    }
                } else {
                    Text(
                        text = "Scheduled",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stops info
            Text(
                text = if (journey.is_direct) "Direct · ${journey.stops_count} stops" else "${journey.transfers_count} transfer · ${journey.transfer_station}",
                fontSize = 11.sp,
                color = SecondaryText
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchResultsScreenPreview() {
    AuraTransitTheme {
        SearchResultsScreen()
    }
}
