package com.aura.transit.passenger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.transit.passenger.ui.components.AuraBottomNavigation
import com.aura.transit.passenger.ui.theme.*

data class TimelineStopData(
    val name: String,
    val info: String,
    val isCompleted: Boolean,
    val isCurrentBoarding: Boolean,
    val isUpcoming: Boolean
)

@Composable
fun RouteTimelineScreen(
    routeNumber: String = "Route 24",
    routeSubtitle: String = "AIT Pune → Pune Railway Station",
    onBack: () -> Unit = {},
    onNavigateTab: (String) -> Unit = {}
) {
    val stops = listOf(
        TimelineStopData("Alandi", "Departed 09:36", isCompleted = true, isCurrentBoarding = false, isUpcoming = false),
        TimelineStopData("Dighi", "Departed 09:40", isCompleted = true, isCurrentBoarding = false, isUpcoming = false),
        TimelineStopData("AIT Pune", "Board here · 09:48", isCompleted = false, isCurrentBoarding = true, isUpcoming = false),
        TimelineStopData("Deccan Collage", "09:54", isCompleted = false, isCurrentBoarding = false, isUpcoming = true),
        TimelineStopData("Vishrantwadi", "10:01", isCompleted = false, isCurrentBoarding = false, isUpcoming = true),
        TimelineStopData("Pune Railway Station", "10:12", isCompleted = false, isCurrentBoarding = false, isUpcoming = true)
    )

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
                .verticalScroll(rememberScrollState())
        ) {
            // Header Top Bar
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
                        text = routeNumber,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Text(
                        text = routeSubtitle,
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Stats Metrics Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSummaryCard(
                    modifier = Modifier.weight(1f),
                    value = "28 km/h",
                    label = "Live speed"
                )
                MetricSummaryCard(
                    modifier = Modifier.weight(1f),
                    value = "18 / 40",
                    label = "Passengers"
                )
                MetricSummaryCard(
                    modifier = Modifier.weight(1f),
                    value = "On time",
                    label = "Service"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Boarding Stop Banner (Pale Blue Surface #EAF2FF)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PaleBlueSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Your boarding stop",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                            Text(
                                text = "AIT Pune · Stop B",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                        }
                    }

                    Text(
                        text = "4 min",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Stop progression
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Stop progression",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Timeline Items
                stops.forEachIndexed { index, stop ->
                    TimelineItemRow(
                        stop = stop,
                        isLast = index == stops.lastIndex
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricSummaryCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun TimelineItemRow(
    stop: TimelineStopData,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Icon and vertical connector
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            when {
                stop.isCompleted -> {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                stop.isCurrentBoarding -> {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue)
                            .border(3.dp, PaleBlueSurface, CircleShape)
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .border(2.dp, BorderColor, CircleShape)
                    )
                }
            }

            if (!isLast) {
                val lineColor = if (stop.isCompleted) PrimaryBlue else BorderColor
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(lineColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Stop name and time
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 24.dp)
        ) {
            Text(
                text = stop.name,
                fontSize = 14.sp,
                fontWeight = if (stop.isCurrentBoarding) FontWeight.Bold else FontWeight.SemiBold,
                color = if (stop.isCompleted) SecondaryText else PrimaryText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stop.info,
                fontSize = 12.sp,
                fontWeight = if (stop.isCurrentBoarding) FontWeight.SemiBold else FontWeight.Normal,
                color = if (stop.isCurrentBoarding) PrimaryBlue else SecondaryText
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RouteTimelineScreenPreview() {
    AuraTransitTheme {
        RouteTimelineScreen()
    }
}
