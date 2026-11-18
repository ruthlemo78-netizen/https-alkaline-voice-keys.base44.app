package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AlkalineCityWorld
import com.example.model.CityVehicle
import com.example.model.MissionDef
import com.example.model.PauseTab
import com.example.ui.theme.AlkalineCharcoal
import com.example.ui.theme.AlkalineObsidian
import com.example.ui.theme.AlkalinePanelSolid
import com.example.ui.theme.CashGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WaypointPurple
import com.example.viewmodel.AlkalineUiState
import com.example.viewmodel.AlkalineViewModel

@Composable
fun PauseMenuOverlay(
    state: AlkalineUiState,
    onSelectTab: (PauseTab) -> Unit,
    onSetWaypoint: (Float, Float, String) -> Unit,
    onClearWaypoint: () -> Unit,
    onLaunchMission: (MissionDef) -> Unit,
    onDeliverVehicle: (CityVehicle) -> Unit,
    onResetSession: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)
    val profile = AlkalineCityWorld.protagonists[state.activeCharacter]!!
    val runtime = state.activeRuntime

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF0080B10))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top GTA V Pause Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(AlkalinePanelSolid)
                            .testTag("pause_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.pause_title),
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 26.sp),
                            color = TextPrimary
                        )
                        Text(
                            text = "${state.currentDistrict.displayName} • ${profile.fullName.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = profile.signatureColor
                        )
                    }
                }

                // Active Character Badge + Cash
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = profile.firstName,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "$${AlkalineViewModel.formatCash(runtime.cash)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = CashGreen
                        )
                    }
                    Image(
                        painter = painterResource(id = profile.portraitRes),
                        contentDescription = profile.fullName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .border(1.5.dp, profile.signatureColor, RoundedCornerShape(4.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal GTA V Pause Tab Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PauseTab.entries.forEach { tab ->
                    val selected = tab == state.pauseTab
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(if (selected) AlkalinePanelSolid else Color(0xFF10151E))
                            .clickable { onSelectTab(tab) }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("pause_tab_${tab.name.lowercase()}"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) TextPrimary else TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(38.dp)
                                .height(3.dp)
                                .background(if (selected) profile.signatureColor else Color.Transparent)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x33FFFFFF))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Active Tab Body
            Box(modifier = Modifier.weight(1f)) {
                when (state.pauseTab) {
                    PauseTab.MAP -> PauseMapTab(
                        state = state,
                        accentColor = profile.signatureColor,
                        onSetWaypoint = onSetWaypoint,
                        onClearWaypoint = onClearWaypoint
                    )
                    PauseTab.MISSIONS -> PauseMissionsTab(
                        state = state,
                        onLaunchMission = onLaunchMission
                    )
                    PauseTab.GARAGE -> PauseGarageTab(
                        state = state,
                        accentColor = profile.signatureColor,
                        onDeliverVehicle = onDeliverVehicle
                    )
                    PauseTab.STATS -> PauseStatsTab(
                        state = state,
                        accentColor = profile.signatureColor
                    )
                    PauseTab.BRIEF -> PauseBriefTab(
                        state = state,
                        onResetSession = onResetSession
                    )
                }
            }
        }
    }
}

@Composable
private fun PauseMapTab(
    state: AlkalineUiState,
    accentColor: Color,
    onSetWaypoint: (Float, Float, String) -> Unit,
    onClearWaypoint: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TAP ANYWHERE ON MAP TO SET GPS WAYPOINT",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            if (state.waypoint != null) {
                OutlinedButton(
                    onClick = onClearWaypoint,
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("CLEAR GPS", style = MaterialTheme.typography.labelSmall, color = WaypointPurple)
                }
            }
        }

        // Full Interactive Alkaline City Map Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF0C1520))
                .border(1.5.dp, Color(0xFF2D3748), RoundedCornerShape(6.dp))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val wx = (offset.x / size.width) * AlkalineCityWorld.WORLD_WIDTH
                        val wy = (offset.y / size.height) * AlkalineCityWorld.WORLD_HEIGHT
                        val district = AlkalineCityWorld.districtFor(wx, wy)
                        onSetWaypoint(wx, wy, "${district.displayName} Pin")
                    }
                }
                .testTag("pause_full_map")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val sx = size.width / AlkalineCityWorld.WORLD_WIDTH
                val sy = size.height / AlkalineCityWorld.WORLD_HEIGHT

                // Ocean & Sand
                drawRect(
                    color = Color(0xFF0A3950),
                    topLeft = Offset.Zero,
                    size = Size(AlkalineCityWorld.OCEAN_EDGE_X * sx, size.height)
                )
                drawRect(
                    color = Color(0xFF9F8962),
                    topLeft = Offset(AlkalineCityWorld.OCEAN_EDGE_X * sx, 0f),
                    size = Size((AlkalineCityWorld.BEACH_EDGE_X - AlkalineCityWorld.OCEAN_EDGE_X) * sx, size.height)
                )

                // Roads
                for (r in AlkalineCityWorld.roads) {
                    drawRect(
                        color = Color(0xFF394454),
                        topLeft = Offset(r.x * sx, r.y * sy),
                        size = Size(r.width * sx, r.height * sy)
                    )
                }

                // Buildings
                for (b in AlkalineCityWorld.buildings) {
                    drawRect(
                        color = Color(0xFF1D2532),
                        topLeft = Offset(b.x * sx, b.y * sy),
                        size = Size(b.width * sx, b.height * sy)
                    )
                }

                // GPS Route
                state.waypoint?.let { wp ->
                    val px = state.playerX * sx
                    val py = state.playerY * sy
                    val wx = wp.x * sx
                    val wy = wp.y * sy
                    val route = Path().apply {
                        moveTo(px, py)
                        lineTo(wx, py)
                        lineTo(wx, wy)
                    }
                    drawPath(route, color = WaypointPurple, style = Stroke(width = 4f))
                    drawCircle(color = WaypointPurple, radius = 9f, center = Offset(wx, wy))
                    drawCircle(color = Color.White, radius = 9f, center = Offset(wx, wy), style = Stroke(2f))
                }

                // Missions
                for (m in AlkalineCityWorld.missions) {
                    if (m.id in state.completedMissionIds) continue
                    drawCircle(
                        color = m.accentColor,
                        radius = 7.5f,
                        center = Offset(m.startX * sx, m.startY * sy)
                    )
                }

                // Cash drops
                for (d in state.cashDrops) {
                    if (d.collected) continue
                    drawCircle(
                        color = CashGreen,
                        radius = 4.5f,
                        center = Offset(d.x * sx, d.y * sy)
                    )
                }

                // Player Marker
                val pCenter = Offset(state.playerX * sx, state.playerY * sy)
                drawCircle(color = accentColor.copy(alpha = 0.35f), radius = 14f, center = pCenter)
                drawCircle(color = Color.White, radius = 7f, center = pCenter)
            }
        }

        // Map Legend Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(Color.White, "You")
            LegendItem(WaypointPurple, "GPS Pin")
            LegendItem(CashGreen, "Cash ($)")
            LegendItem(accentColor, "Missions")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun PauseMissionsTab(
    state: AlkalineUiState,
    onLaunchMission: (MissionDef) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(AlkalineCityWorld.missions, key = { it.id }) { mission ->
            val completed = mission.id in state.completedMissionIds
            val active = state.activeMission?.id == mission.id

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(AlkalineCharcoal)
                    .border(
                        1.5.dp,
                        if (active) mission.accentColor else Color(0xFF283140),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(mission.accentColor.copy(alpha = 0.2f))
                                .border(1.5.dp, mission.accentColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mission.codeLetter,
                                style = MaterialTheme.typography.titleMedium,
                                color = mission.accentColor
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = mission.title.uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "${mission.district.displayName} • Giver: ${mission.giver}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Text(
                        text = "+$${AlkalineViewModel.formatCash(mission.rewardCash)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = CashGreen
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mission.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (completed) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CashGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("COMPLETED", style = MaterialTheme.typography.labelLarge, color = CashGreen)
                        }
                    } else {
                        Button(
                            onClick = { onLaunchMission(mission) },
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("launch_mission_${mission.id}"),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = mission.accentColor,
                                contentColor = AlkalineObsidian
                            )
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (active) "WAYPOINT ACTIVE" else "SET GPS & START",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PauseGarageTab(
    state: AlkalineUiState,
    accentColor: Color,
    onDeliverVehicle: (CityVehicle) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(AlkalineCityWorld.vehicles, key = { it.id }) { vehicle ->
            val current = state.currentVehicle.id == vehicle.id
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(AlkalineCharcoal)
                    .border(
                        1.5.dp,
                        if (current) accentColor else Color(0xFF283140),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(vehicle.bodyColor)
                                .border(1.5.dp, vehicle.accentColor, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = vehicle.accentColor
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = vehicle.name.uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = vehicle.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = vehicle.accentColor
                            )
                        }
                    }

                    Button(
                        onClick = { onDeliverVehicle(vehicle) },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("deliver_vehicle_${vehicle.id}"),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (current) CashGreen else accentColor,
                            contentColor = AlkalineObsidian
                        )
                    ) {
                        Text(
                            text = if (current) "ACTIVE RIDE" else "DELIVER",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                StatRow("TOP SPEED", (vehicle.topSpeed / 540f).coerceIn(0f, 1f), vehicle.accentColor)
                Spacer(modifier = Modifier.height(4.dp))
                StatRow("ACCELERATION", (vehicle.acceleration / 460f).coerceIn(0f, 1f), vehicle.accentColor)
                Spacer(modifier = Modifier.height(4.dp))
                StatRow("HANDLING", vehicle.handling, vehicle.accentColor)
            }
        }
    }
}

@Composable
private fun PauseStatsTab(
    state: AlkalineUiState,
    accentColor: Color
) {
    val profile = AlkalineCityWorld.protagonists[state.activeCharacter]!!
    val stats = profile.stats

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(AlkalineCharcoal)
            .border(1.dp, Color(0xFF283140), RoundedCornerShape(6.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "${profile.fullName.uppercase()} — ATTRIBUTES",
            style = MaterialTheme.typography.headlineMedium,
            color = accentColor
        )
        StatRow("STAMINA", stats.stamina, accentColor)
        StatRow("DRIVING", stats.driving, accentColor)
        StatRow("FLYING", stats.flying, accentColor)
        StatRow("STEALTH", stats.stealth, accentColor)
        StatRow("HACKING", stats.hacking, accentColor)
        StatRow("SPECIAL ABILITY", stats.special, accentColor)

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("MISSIONS COMPLETED", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
            Text(
                "${state.completedMissionIds.size} / ${AlkalineCityWorld.missions.size}",
                style = MaterialTheme.typography.titleMedium,
                color = CashGreen
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("SNAPMATIC PHOTOS", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
            Text("${state.snapmaticCount}", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        }
    }
}

@Composable
private fun PauseBriefTab(
    state: AlkalineUiState,
    onResetSession: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("RECENT CITY FEED", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            OutlinedButton(
                onClick = onResetSession,
                modifier = Modifier.height(48.dp).testTag("reset_session_button"),
                shape = RoundedCornerShape(4.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("RESET CITY", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(state.briefLog, key = { it.id }) { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(AlkalineCharcoal)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(34.dp)
                            .background(entry.accentColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(entry.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text(entry.detail, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: Float,
    fillColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            for (seg in 0 until 5) {
                val segStart = seg * 0.2f
                val segFill = ((value - segStart) / 0.2f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .background(Color(0xFF232B38))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(segFill)
                            .height(7.dp)
                            .background(fillColor)
                    )
                }
            }
        }
    }
}
