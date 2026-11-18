package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AlkalineCityWorld
import com.example.model.OverlayType
import com.example.ui.theme.AlkalineObsidian
import com.example.ui.theme.AlkalinePanel
import com.example.ui.theme.AlkalineTeal
import com.example.ui.theme.ArmorBlue
import com.example.ui.theme.CashGreen
import com.example.ui.theme.HealthGreen
import com.example.ui.theme.SpecialGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WantedPoliceBlue
import com.example.ui.theme.WantedPoliceRed
import com.example.ui.theme.WantedStarWhite
import com.example.ui.theme.WaypointPurple
import com.example.viewmodel.AlkalineUiState
import com.example.viewmodel.AlkalineViewModel
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.roundToInt

@Composable
fun GtaHudOverlay(
    state: AlkalineUiState,
    timeSec: Float,
    onJoystickChange: (Float, Float) -> Unit,
    onNitroHoldChange: (Boolean) -> Unit,
    onOpenOverlay: (OverlayType) -> Unit,
    onToggleVehicle: () -> Unit,
    onActivateSpecial: () -> Unit,
    onUseGear: () -> Unit,
    onTriggerWanted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = AlkalineCityWorld.protagonists[state.activeCharacter]!!
    val runtime = state.activeRuntime
    val hudAlpha = state.settings.hudOpacity
    val sirenRed = ((timeSec * 5f).toInt() % 2 == 0)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .alpha(hudAlpha)
    ) {
        val compactWidth = maxWidth < 420.dp
        val radarWidth = if (compactWidth) 136.dp else 164.dp
        val radarHeight = if (compactWidth) 90.dp else 108.dp

        // 1. TOP-LEFT: Pause/Map + Radio Pill + Active Mission Tracker
        Column(
            modifier = Modifier.align(Alignment.TopStart),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onOpenOverlay(OverlayType.PAUSE_MENU) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AlkalinePanel)
                        .border(1.5.dp, profile.signatureColor, RoundedCornerShape(6.dp))
                        .testTag("pause_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(R.string.cd_pause_menu),
                        tint = TextPrimary
                    )
                }

                // Active Radio Pill
                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AlkalinePanel)
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp))
                        .clickable { onOpenOverlay(OverlayType.RADIO_WHEEL) }
                        .padding(horizontal = 10.dp)
                        .testTag("radio_pill_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = "Radio station",
                        tint = state.activeStation.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = state.activeStation.name.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.activeStation.tracks.getOrElse(state.radioTrackIndex) { "" }.take(18),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = TextSecondary
                        )
                    }
                }
            }

            // Active Mission Objective Banner
            state.activeMission?.let { mission ->
                Row(
                    modifier = Modifier
                        .width(if (compactWidth) 196.dp else 230.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xD911161F))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(30.dp)
                            .background(mission.accentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = mission.title.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = mission.accentColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = mission.objectiveText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // 2. TOP-RIGHT: Wanted Stars + Cash Counter + Gear + iFruit Phone
        Column(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Wanted Stars Row (5 stars)
            val showStars = state.wantedLevel > 0
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (showStars) Color(0xCC0D1117) else Color.Transparent)
                    .then(
                        if (showStars && !state.wantedEvading) {
                            Modifier.border(
                                1.5.dp,
                                if (sirenRed) WantedPoliceRed else WantedPoliceBlue,
                                RoundedCornerShape(4.dp)
                            )
                        } else Modifier
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val starVisible = !state.wantedEvading || ((timeSec * 4f).toInt() % 2 == 0)
                for (i in 1..5) {
                    val active = i <= state.wantedLevel
                    Icon(
                        imageVector = if (active) Icons.Default.Star else Icons.Outlined.StarOutline,
                        contentDescription = stringResource(R.string.cd_wanted_star),
                        tint = when {
                            active && starVisible -> WantedStarWhite
                            active -> WantedStarWhite.copy(alpha = 0.25f)
                            else -> Color(0x44FFFFFF)
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Cash Readout (GTA V Anton Green)
            Text(
                text = "$${AlkalineViewModel.formatCash(runtime.cash)}",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 26.sp,
                    letterSpacing = 0.5.sp
                ),
                color = CashGreen,
                modifier = Modifier.testTag("cash_counter_text")
            )

            AnimatedVisibility(
                visible = state.cashDeltaText != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = state.cashDeltaText.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            }

            // Quick Utility Row: iFruit Phone + Gear Wheel + Wanted Test Star
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Active Gear Button (tap to trigger gear!)
                val gearMeta = AlkalineCityWorld.gearGadgets.find { it.type == state.selectedGear }
                    ?: AlkalineCityWorld.gearGadgets.first()
                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AlkalinePanel)
                        .border(1.dp, gearMeta.accentColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .clickable(onClick = onUseGear)
                        .padding(horizontal = 10.dp)
                        .testTag("use_gear_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = stringResource(R.string.cd_use_gear),
                        tint = gearMeta.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = gearMeta.shortLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary
                    )
                }

                // Trigger Wanted Patrol Action (for instant police chase fun!)
                IconButton(
                    onClick = onTriggerWanted,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AlkalinePanel)
                        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(6.dp))
                        .testTag("trigger_wanted_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalPolice,
                        contentDescription = "Raise wanted star",
                        tint = if (state.wantedLevel > 0) WantedPoliceRed else TextSecondary
                    )
                }

                // iFruit Phone Button
                val unreadCount = state.messages.count { it.unread }
                Box {
                    IconButton(
                        onClick = { onOpenOverlay(OverlayType.IFRUIT_PHONE) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(AlkalinePanel)
                            .border(1.5.dp, AlkalineTeal, RoundedCornerShape(6.dp))
                            .testTag("ifruit_phone_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneIphone,
                            contentDescription = stringResource(R.string.cd_ifruit_phone),
                            tint = AlkalineTeal
                        )
                    }
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(WantedPoliceRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$unreadCount",
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 3. CENTER BANNER (Mission Passed / Gear Trigger / Wanted Evaded)
        AnimatedVisibility(
            visible = state.centerBannerTitle != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 86.dp)
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xE60A0E14))
                    .border(1.5.dp, state.centerBannerColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.centerBannerTitle.orEmpty(),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 22.sp),
                    color = state.centerBannerColor
                )
                state.centerBannerSubtitle?.let { sub ->
                    Text(
                        text = sub,
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary
                    )
                }
            }
        }

        // 4. BOTTOM-LEFT: Authentic GTA V Rectangular Minimap + 3 Status Bars
        Column(
            modifier = Modifier.align(Alignment.BottomStart),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Speed & Vehicle HUD pill right above radar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xCC0D1117))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    val mph = (abs(state.playerSpeed) * 0.28f).roundToInt()
                    Text(
                        text = if (state.inVehicle) "$mph MPH • ${state.currentVehicle.name.uppercase()}" else "ON FOOT",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextPrimary
                    )
                }
            }

            // Minimap Radar Frame (tap to open full Pause Map!)
            Box(
                modifier = Modifier
                    .size(width = radarWidth, height = radarHeight)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xE60D141D))
                    .border(
                        width = 2.dp,
                        color = when {
                            state.wantedLevel > 0 && !state.wantedEvading ->
                                if (sirenRed) WantedPoliceRed else WantedPoliceBlue
                            else -> Color(0xFF334155)
                        },
                        shape = RoundedCornerShape(3.dp)
                    )
                    .clickable { onOpenOverlay(OverlayType.PAUSE_MENU) }
                    .testTag("minimap_radar")
            ) {
                MinimapRadarCanvas(state = state, timeSec = timeSec)
            }

            // 3 GTA V Segmented Status Bars: Health | Armor | Special
            Row(
                modifier = Modifier.width(radarWidth),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                StatusSegmentBar(
                    fraction = runtime.health,
                    fillColor = HealthGreen,
                    modifier = Modifier.weight(2f)
                )
                StatusSegmentBar(
                    fraction = runtime.armor,
                    fillColor = ArmorBlue,
                    modifier = Modifier.weight(1f)
                )
                StatusSegmentBar(
                    fraction = runtime.specialMeter,
                    fillColor = SpecialGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 5. BOTTOM-RIGHT: District Banner + Character Wheel + Gear Wheel + Analog Joystick + Action Buttons
        Column(
            modifier = Modifier.align(Alignment.BottomEnd),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sliding GTA V District Banner
            AnimatedVisibility(
                visible = state.districtBannerVisible,
                enter = slideInHorizontally { it } + fadeIn(),
                exit = slideOutHorizontally { it } + fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xBA090D12))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = state.currentDistrict.displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = state.currentDistrict.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = state.currentDistrict.accentColor
                    )
                }
            }

            // Wheel Triggers + Vehicle / Special Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gear Radial Wheel Trigger
                IconButton(
                    onClick = { onOpenOverlay(OverlayType.GEAR_WHEEL) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AlkalinePanel)
                        .border(1.5.dp, Color(0x66FFFFFF), CircleShape)
                        .testTag("gear_wheel_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = stringResource(R.string.cd_gear_wheel),
                        tint = TextPrimary
                    )
                }

                // Special Ability Button
                IconButton(
                    onClick = onActivateSpecial,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (state.isSpecialActive) SpecialGold else AlkalinePanel)
                        .border(1.5.dp, SpecialGold, CircleShape)
                        .testTag("special_ability_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = stringResource(R.string.cd_special_ability),
                        tint = if (state.isSpecialActive) AlkalineObsidian else SpecialGold
                    )
                }

                // Enter/Exit Vehicle Button
                IconButton(
                    onClick = onToggleVehicle,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AlkalinePanel)
                        .border(1.5.dp, AlkalineTeal, CircleShape)
                        .testTag("vehicle_toggle_button")
                ) {
                    Icon(
                        imageVector = if (state.inVehicle) Icons.Default.DirectionsWalk else Icons.Default.DirectionsCar,
                        contentDescription = stringResource(R.string.cd_vehicle_toggle),
                        tint = AlkalineTeal
                    )
                }

                // Character Switch Wheel Trigger (with portrait!)
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(2.dp, profile.signatureColor, CircleShape)
                        .clickable { onOpenOverlay(OverlayType.CHARACTER_WHEEL) }
                        .testTag("character_wheel_button")
                ) {
                    Image(
                        painter = painterResource(id = profile.portraitRes),
                        contentDescription = stringResource(R.string.cd_character_wheel),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Virtual Analog Thumbstick + Boost Grip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Boost / Nitro touch button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC141B24))
                        .border(1.5.dp, profile.signatureColor, CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { onNitroHoldChange(true) },
                                onDragEnd = { onNitroHoldChange(false) },
                                onDragCancel = { onNitroHoldChange(false) },
                                onDrag = { _, _ -> }
                            )
                        }
                        .clickable { onUseGear() }
                        .testTag("nitro_boost_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.inVehicle) "NITRO" else "SPRINT",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = profile.signatureColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                VirtualThumbstick(
                    accentColor = profile.signatureColor,
                    onVectorChange = onJoystickChange
                )
            }
        }
    }
}

@Composable
private fun StatusSegmentBar(
    fraction: Float,
    fillColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(8.dp)
            .clip(RoundedCornerShape(1.5.dp))
            .background(Color(0xAA0A0D12))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(8.dp)
                .background(fillColor)
        )
    }
}

@Composable
private fun MinimapRadarCanvas(
    state: AlkalineUiState,
    timeSec: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val scale = 0.22f
        val cx = size.width / 2f
        val cy = size.height / 2f

        fun toRadar(wx: Float, wy: Float): Offset {
            return Offset(
                cx + (wx - state.playerX) * scale,
                cy + (wy - state.playerY) * scale
            )
        }

        // Ocean edge on radar
        val oceanX = toRadar(AlkalineCityWorld.OCEAN_EDGE_X, 0f).x
        if (oceanX > 0f) {
            drawRect(
                color = Color(0xFF0A3D56),
                topLeft = Offset.Zero,
                size = Size(oceanX.coerceAtMost(size.width), size.height)
            )
        }

        // Roads
        for (road in AlkalineCityWorld.roads) {
            val tl = toRadar(road.x, road.y)
            drawRect(
                color = Color(0xFF3A4656),
                topLeft = tl,
                size = Size(road.width * scale, road.height * scale)
            )
        }

        // Buildings
        for (b in AlkalineCityWorld.buildings) {
            val tl = toRadar(b.x, b.y)
            drawRect(
                color = Color(0xFF1E2631),
                topLeft = tl,
                size = Size(b.width * scale, b.height * scale)
            )
        }

        // GPS Route Line
        state.waypoint?.let { wp ->
            val wpRadar = toRadar(wp.x, wp.y)
            val elbowRadar = toRadar(wp.x, state.playerY)
            val route = Path().apply {
                moveTo(cx, cy)
                lineTo(elbowRadar.x, elbowRadar.y)
                lineTo(wpRadar.x, wpRadar.y)
            }
            drawPath(
                path = route,
                color = WaypointPurple,
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
            drawCircle(
                color = WaypointPurple,
                radius = 4.5f,
                center = wpRadar
            )
        }

        // Mission blips
        for (m in AlkalineCityWorld.missions) {
            if (m.id in state.completedMissionIds) continue
            val mp = toRadar(m.startX, m.startY)
            drawCircle(
                color = m.accentColor,
                radius = 4f,
                center = mp
            )
        }

        // Cash drops
        for (d in state.cashDrops) {
            if (d.collected) continue
            val dp = toRadar(d.x, d.y)
            drawCircle(
                color = CashGreen,
                radius = 2.6f,
                center = dp
            )
        }

        // Patrol blips (flashing red/blue)
        val sirenBlue = ((timeSec * 6f).toInt() % 2 == 0)
        for (p in state.patrolCars) {
            val pp = toRadar(p.x, p.y)
            drawCircle(
                color = if (sirenBlue) WantedPoliceBlue else WantedPoliceRed,
                radius = 4f,
                center = pp
            )
        }

        // Player directional arrow at center
        rotate(degrees = state.playerAngleDeg + 90f, pivot = Offset(cx, cy)) {
            val arrow = Path().apply {
                moveTo(cx, cy - 7f)
                lineTo(cx + 5f, cy + 5f)
                lineTo(cx, cy + 2f)
                lineTo(cx - 5f, cy + 5f)
                close()
            }
            drawPath(arrow, color = Color.White)
        }
    }
}

@Composable
private fun VirtualThumbstick(
    accentColor: Color,
    onVectorChange: (Float, Float) -> Unit
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }
    val maxRadiusPx = 85f

    Box(
        modifier = Modifier
            .size(104.dp)
            .clip(CircleShape)
            .background(Color(0x8810161F))
            .border(1.5.dp, Color(0x55FFFFFF), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { },
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onVectorChange(0f, 0f)
                    },
                    onDragCancel = {
                        knobOffset = Offset.Zero
                        onVectorChange(0f, 0f)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val raw = knobOffset + dragAmount
                        val dist = hypot(raw.x, raw.y)
                        val clamped = if (dist > maxRadiusPx) {
                            Offset(raw.x / dist * maxRadiusPx, raw.y / dist * maxRadiusPx)
                        } else raw
                        knobOffset = clamped
                        onVectorChange(clamped.x / maxRadiusPx, clamped.y / maxRadiusPx)
                    }
                )
            }
            .testTag("virtual_thumbstick"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(knobOffset.x.roundToInt(), knobOffset.y.roundToInt()) }
                .size(44.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.85f))
                .border(2.dp, Color.White, CircleShape)
        )
    }
}
