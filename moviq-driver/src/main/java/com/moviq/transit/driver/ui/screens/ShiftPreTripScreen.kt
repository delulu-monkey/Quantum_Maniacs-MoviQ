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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
fun ShiftPreTripScreen(
    driverName: String = "Arjun",
    lineName: String = "Line 24",
    routeSubtitle: String = "Central Station → Riverside Park",
    vehicleId: String = "AT-204",
    shiftHours: String = "06:00 - 14:00",
    onStartTrip: () -> Unit = {}
) {
    var checkBrakes by remember { mutableStateOf(true) }
    var checkDoors by remember { mutableStateOf(true) }
    var checkMirrors by remember { mutableStateOf(true) }

    val checkedCount = (if (checkBrakes) 1 else 0) + (if (checkDoors) 1 else 0) + (if (checkMirrors) 1 else 0)

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

            // Driver Greeting
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Ready for your shift",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Good morning, $driverName",
                    fontSize = 14.sp,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // TODAY'S ASSIGNMENT Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "TODAY'S ASSIGNMENT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lineName,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = routeSubtitle,
                                fontSize = 13.sp,
                                color = SecondaryText
                            )
                        }

                        // Bus Icon Box
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PaleBlueSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DirectionsBus,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = BorderColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Card Footer: Vehicle AT-204 and Shift
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Vehicle $vehicleId",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                        Text(
                            text = shiftHours,
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Vehicle connection Section
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Vehicle connection",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(14.dp))

                ConnectionStatusRow(
                    title = "GPS lock",
                    subtitle = "Accuracy: 3 m",
                    statusText = "Ready"
                )
                Spacer(modifier = Modifier.height(12.dp))
                ConnectionStatusRow(
                    title = "LTE sync",
                    subtitle = "Updated just now",
                    statusText = "Ready"
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Pre-trip check Section
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pre-trip check",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Text(
                        text = "$checkedCount of 3",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                ChecklistItemRow(
                    label = "Brakes & lights",
                    isChecked = checkBrakes,
                    onToggle = { checkBrakes = !checkBrakes }
                )
                Spacer(modifier = Modifier.height(10.dp))
                ChecklistItemRow(
                    label = "Doors & emergency exits",
                    isChecked = checkDoors,
                    onToggle = { checkDoors = !checkDoors }
                )
                Spacer(modifier = Modifier.height(10.dp))
                ChecklistItemRow(
                    label = "Mirrors & cabin",
                    isChecked = checkMirrors,
                    onToggle = { checkMirrors = !checkMirrors }
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Start Trip Primary Button
            Button(
                onClick = onStartTrip,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = "Start Trip",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ConnectionStatusRow(
    title: String,
    subtitle: String,
    statusText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(PaleBlueSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
        }

        Text(
            text = statusText,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryBlue
        )
    }
}

@Composable
private fun ChecklistItemRow(
    label: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isChecked) PrimaryBlue else Color.Transparent)
                .border(1.5.dp, if (isChecked) PrimaryBlue else BorderColor, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryText
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShiftPreTripScreenPreview() {
    MoviQDriverTheme {
        ShiftPreTripScreen()
    }
}
