package com.aura.transit.passenger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.transit.passenger.ui.components.AuraBottomNavigation
import com.aura.transit.passenger.ui.theme.*

@Composable
fun HomeScreen(
    onSearchBuses: (origin: String, destination: String) -> Unit = { _, _ -> },
    onSelectNearbyStop: (String) -> Unit = {},
    onOpenAccount: () -> Unit = {},
    onNavigateTab: (String) -> Unit = {}
) {
    var origin by remember { mutableStateOf("Central Station") }
    var destination by remember { mutableStateOf("Riverside Park") }
    var selectedTimeMode by remember { mutableStateOf("Leave now") }
    var showScheduleDialog by remember { mutableStateOf(false) }

    var recentSearches by remember {
        mutableStateOf(
            listOf(
                Pair("Central Station", "Riverside Park"),
                Pair("Market Square", "University Gate")
            )
        )
    }

    val popularOriginStops = listOf("Central Station", "Depot Road", "Market Square", "City Library")
    val popularDestStops = listOf("Riverside Park", "University Gate", "Museum Road", "Market Square")

    Scaffold(
        containerColor = MainBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AuraBottomNavigation(
                selectedTab = "home",
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
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.aura.transit.passenger.R.drawable.app_icon),
                        contentDescription = "Moviq Icon",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Moviq",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Where are you heading?",
                            fontSize = 13.sp,
                            color = SecondaryText
                        )
                    }
                }

                // Profile Avatar "TS" (Clickable to open Account)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PaleBlueSurface)
                        .border(1.dp, BorderColor, CircleShape)
                        .clickable(onClick = onOpenAccount),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Search Card
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
                        .padding(18.dp)
                ) {
                    // Origin and Swap Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Origin icon (hollow circle)
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .border(2.dp, SecondaryText, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "From",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicTextField(
                                    value = origin,
                                    onValueChange = { origin = it },
                                    textStyle = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryText
                                    ),
                                    cursorBrush = SolidColor(PrimaryBlue),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    decorationBox = { innerTextField ->
                                        if (origin.isEmpty()) {
                                            Text(
                                                text = "Enter origin stop...",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = SecondaryText.copy(alpha = 0.6f)
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                                if (origin.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear origin",
                                        tint = SecondaryText,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { origin = "" }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Swap Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MainBackground)
                                .clickable {
                                    val temp = origin
                                    origin = destination
                                    destination = temp
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap Origin and Destination",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Quick Origin Suggestion Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 26.dp, top = 6.dp, bottom = 4.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        popularOriginStops.forEach { stop ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (origin == stop) PaleBlueSurface else Color(0xFFF1F5F9))
                                    .clickable { origin = stop }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = stop,
                                    fontSize = 11.sp,
                                    fontWeight = if (origin == stop) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (origin == stop) PrimaryBlue else SecondaryText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = BorderColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Destination Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Destination",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "To",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicTextField(
                                    value = destination,
                                    onValueChange = { destination = it },
                                    textStyle = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryText
                                    ),
                                    cursorBrush = SolidColor(PrimaryBlue),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    decorationBox = { innerTextField ->
                                        if (destination.isEmpty()) {
                                            Text(
                                                text = "Enter destination stop...",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = SecondaryText.copy(alpha = 0.6f)
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                                if (destination.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear destination",
                                        tint = SecondaryText,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { destination = "" }
                                    )
                                }
                            }
                        }
                    }

                    // Quick Destination Suggestion Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 28.dp, top = 6.dp, bottom = 4.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        popularDestStops.forEach { stop ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (destination == stop) PaleBlueSurface else Color(0xFFF1F5F9))
                                    .clickable { destination = stop }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = stop,
                                    fontSize = 11.sp,
                                    fontWeight = if (destination == stop) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (destination == stop) PrimaryBlue else SecondaryText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = BorderColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Time Row: "Leave now" and "Schedule"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showScheduleDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = SecondaryText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedTimeMode,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryText
                            )
                        }

                        Text(
                            text = "Schedule",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // "Search Buses" Button
                    Button(
                        onClick = {
                            val activeOrigin = origin.ifBlank { "Central Station" }
                            val activeDest = destination.ifBlank { "Riverside Park" }
                            origin = activeOrigin
                            destination = activeDest

                            // Update recent searches
                            recentSearches = (listOf(Pair(activeOrigin, activeDest)) + recentSearches)
                                .distinctBy { "${it.first}->${it.second}" }
                                .take(4)

                            onSearchBuses(activeOrigin, activeDest)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text(
                            text = "Search Buses",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Recent Searches Section
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Recent searches",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(12.dp))

                recentSearches.forEach { (searchOrigin, searchDest) ->
                    RecentSearchItem(
                        title = "$searchOrigin →",
                        subtitle = searchDest,
                        onClick = {
                            origin = searchOrigin
                            destination = searchDest
                            onSearchBuses(searchOrigin, searchDest)
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nearby Stops Section
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Nearby stops",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(12.dp))

                NearbyStopItem(
                    title = "AIT Pune · Stop B",
                    subtitle = "180 m · 3 min walk",
                    onClick = {
                        origin = "AIT Pune"
                        onSelectNearbyStop("AIT Pune · Stop B")
                        onSearchBuses("AIT Pune", destination.ifBlank { "Riverside Park" })
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Schedule Time Selection Dialog
        if (showScheduleDialog) {
            AlertDialog(
                onDismissRequest = { showScheduleDialog = false },
                title = {
                    Text(
                        text = "Select Departure Time",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "Leave now",
                            "In 15 minutes",
                            "In 30 minutes",
                            "Today 10:30 AM",
                            "Today 05:30 PM (Evening commute)",
                            "Tomorrow 08:30 AM (Morning rush)"
                        ).forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedTimeMode == option) PaleBlueSurface else Color.Transparent)
                                    .clickable {
                                        selectedTimeMode = option
                                        showScheduleDialog = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedTimeMode == option,
                                    onClick = {
                                        selectedTimeMode = option
                                        showScheduleDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = option,
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedTimeMode == option) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (selectedTimeMode == option) PrimaryBlue else PrimaryText
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showScheduleDialog = false }) {
                        Text(text = "Close", color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }
    }
}

@Composable
private fun RecentSearchItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = SecondaryText,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = "$title $subtitle",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryText,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SecondaryText,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun NearbyStopItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PaleBlueSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.DirectionsBus,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = SecondaryText
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SecondaryText,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    AuraTransitTheme {
        HomeScreen()
    }
}
