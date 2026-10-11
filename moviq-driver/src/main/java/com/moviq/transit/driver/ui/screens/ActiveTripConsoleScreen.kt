package com.moviq.transit.driver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.moviq.transit.driver.ui.theme.*

@Composable
fun ActiveTripConsoleScreen(
    lineName: String = "Line 24",
    vehicleId: String = "AT-204",
    nextStopName: String = "Central Station",
    nextStopPlatform: String = "Stop B · Boarding on the left",
    distanceM: Int = 450,
    etaMin: Int = 2,
    passengers: Int = 18,
    totalSeats: Int = 40,
    onEndService: () -> Unit = {}
) {
    var showEndDialog by remember { mutableStateOf(false) }

    if (showEndDialog) {
        AlertDialog(
            onDismissRequest = { showEndDialog = false },
            title = { Text("End Service?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to end this trip and stop active GPS transmission for $vehicleId?") },
            confirmButton = {
                Button(
                    onClick = {
                        showEndDialog = false
                        onEndService()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("End Service", color = White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDialog = false }) {
                    Text("Cancel", color = SecondaryText)
                }
            }
        )
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Header: Line 24 · In service and GPS live
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.DirectionsBus,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$lineName · In service",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                    }

                    // Live GPS Indicator
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "GPS live",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Vehicle $vehicleId",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // NEXT STOP Section
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "NEXT STOP",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryText,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = nextStopName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = nextStopPlatform,
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Key Metrics Row: Distance to stop & Estimated arrival
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    mainValue = "$distanceM",
                    unit = "m",
                    subtitle = "Distance to stop"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    mainValue = "$etaMin",
                    unit = "min",
                    subtitle = "Estimated arrival"
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Stop Sequence Section
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stop sequence",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Text(
                        text = "3 / 6",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stop 1: Central Station (Next)
                SequenceRow(
                    name = "Central Station",
                    time = "Next: 09:48",
                    isActive = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Stop 2: Museum Road
                SequenceRow(
                    name = "Museum Road",
                    time = "09:54",
                    isActive = false
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Stop 3: Market Square
                SequenceRow(
                    name = "Market Square",
                    time = "10:01",
                    isActive = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Passengers on Board Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Passengers on board",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                        Text(
                            text = "${totalSeats - passengers} available",
                            fontSize = 11.sp,
                            color = SecondaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$passengers",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = "  / $totalSeats seats",
                            fontSize = 13.sp,
                            color = SecondaryText,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Hold to End Service Button (Outlined matching design)
            OutlinedButton(
                onClick = { showEndDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryText)
            ) {
                Text(
                    text = "Hold to End Service",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
            }

            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    mainValue: String,
    unit: String,
    subtitle: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = mainValue,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    fontSize = 14.sp,
                    color = SecondaryText,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun SequenceRow(
    name: String,
    time: String,
    isActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .border(2.dp, BorderColor, CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                color = PrimaryText
            )
            Text(
                text = time,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isActive) PrimaryBlue else SecondaryText
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ActiveTripConsoleScreenPreview() {
    MoviQDriverTheme {
        ActiveTripConsoleScreen()
    }
}
