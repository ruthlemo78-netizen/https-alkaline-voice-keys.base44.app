package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AlkalineCityWorld
import com.example.model.GearType
import com.example.model.OverlayType
import com.example.model.ProtagonistId
import com.example.ui.theme.AlkalineObsidian
import com.example.ui.theme.AlkalineTeal
import com.example.ui.theme.CashGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AlkalineUiState
import com.example.viewmodel.AlkalineViewModel
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun CharacterSwitchWheelOverlay(
    state: AlkalineUiState,
    onSelectCharacter: (ProtagonistId) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)
    val ordered = listOf(ProtagonistId.KAI, ProtagonistId.VEGA, ProtagonistId.DEX)
    val angles = listOf(-90f, 30f, 150f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xB805070A))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
        ) {
            Text(
                text = "SWITCH PROTAGONIST",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )

            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val outerRadius = size.minDimension / 2f - 8f
                    ordered.forEachIndexed { index, id ->
                        val prof = AlkalineCityWorld.protagonists[id]!!
                        val isActive = id == state.activeCharacter
                        val startDeg = -150f + index * 120f
                        drawArc(
                            color = if (isActive) prof.signatureColor.copy(alpha = 0.36f) else Color(0xAA161C26),
                            startAngle = startDeg,
                            sweepAngle = 114f,
                            useCenter = true,
                            topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                            size = Size(outerRadius * 2f, outerRadius * 2f)
                        )
                        drawArc(
                            color = if (isActive) prof.signatureColor else Color(0x55FFFFFF),
                            startAngle = startDeg,
                            sweepAngle = 114f,
                            useCenter = false,
                            topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                            size = Size(outerRadius * 2f, outerRadius * 2f),
                            style = Stroke(width = if (isActive) 5f else 2f)
                        )
                    }
                    drawCircle(
                        color = AlkalineObsidian,
                        radius = 48f,
                        center = center
                    )
                }

                // Close center hub
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF141A24))
                        .border(1.dp, Color(0x55FFFFFF), CircleShape)
                        .testTag("close_character_wheel")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = TextPrimary
                    )
                }

                // 3 Protagonist Portrait Cards positioned radially
                val radiusPx = with(LocalDensity.current) { 94.dp.toPx() }
                ordered.forEachIndexed { index, id ->
                    val prof = AlkalineCityWorld.protagonists[id]!!
                    val runtime = state.characterStates[id]
                    val isActive = id == state.activeCharacter
                    val rad = Math.toRadians(angles[index].toDouble())
                    val ox = (cos(rad) * radiusPx).roundToInt()
                    val oy = (sin(rad) * radiusPx).roundToInt()

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .offset { IntOffset(ox, oy) }
                            .clickable { onSelectCharacter(id) }
                            .testTag("switch_char_${id.name.lowercase()}")
                    ) {
                        Image(
                            painter = painterResource(id = prof.portraitRes),
                            contentDescription = prof.fullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isActive) 3.dp else 1.5.dp,
                                    color = if (isActive) prof.signatureColor else Color.White.copy(alpha = 0.7f),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = prof.firstName,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isActive) prof.signatureColor else TextPrimary
                        )
                        Text(
                            text = "$${AlkalineViewModel.formatCash(runtime?.cash ?: 0L)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = CashGreen
                        )
                    }
                }
            }

            // Active Character Detail Strip
            val currentProf = AlkalineCityWorld.protagonists[state.activeCharacter]!!
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xE6121720))
                    .border(1.dp, currentProf.signatureColor.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${currentProf.fullName.uppercase()} • ${currentProf.role.uppercase()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = currentProf.signatureColor
                )
                Text(
                    text = "${currentProf.specialName}: ${currentProf.specialDesc}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RadialWheelOverlay(
    state: AlkalineUiState,
    isRadioMode: Boolean,
    onSwitchWheelMode: (OverlayType) -> Unit,
    onSelectGear: (GearType) -> Unit,
    onSelectRadio: (String) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xC006080C))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
        ) {
            // Mode Switch Pill: GEAR | ALKALINE RADIO
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF151B25))
                    .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (!isRadioMode) AlkalineTeal else Color.Transparent)
                        .clickable { onSwitchWheelMode(OverlayType.GEAR_WHEEL) }
                        .padding(horizontal = 16.dp)
                        .testTag("tab_gear_wheel"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = if (!isRadioMode) AlkalineObsidian else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TACTICAL GEAR",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (!isRadioMode) AlkalineObsidian else TextSecondary
                    )
                }

                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isRadioMode) AlkalineTeal else Color.Transparent)
                        .clickable { onSwitchWheelMode(OverlayType.RADIO_WHEEL) }
                        .padding(horizontal = 16.dp)
                        .testTag("tab_radio_wheel"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = null,
                        tint = if (isRadioMode) AlkalineObsidian else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ALKALINE RADIO",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isRadioMode) AlkalineObsidian else TextSecondary
                    )
                }
            }

            if (!isRadioMode) {
                GearWheelContent(state = state, onSelectGear = onSelectGear, onDismiss = onDismiss)
            } else {
                RadioWheelContent(state = state, onSelectRadio = onSelectRadio, onDismiss = onDismiss)
            }
        }
    }
}

@Composable
private fun GearWheelContent(
    state: AlkalineUiState,
    onSelectGear: (GearType) -> Unit,
    onDismiss: () -> Unit
) {
    val gadgets = AlkalineCityWorld.gearGadgets
    val selectedMeta = gadgets.find { it.type == state.selectedGear } ?: gadgets.first()
    val radiusPx = with(LocalDensity.current) { 112.dp.toPx() }

    Box(
        modifier = Modifier.size(310.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 4f
            val count = gadgets.size
            val sweep = 360f / count
            gadgets.forEachIndexed { idx, g ->
                val active = g.type == state.selectedGear
                val startAngle = -90f - sweep / 2f + idx * sweep
                drawArc(
                    color = if (active) g.accentColor.copy(alpha = 0.35f) else Color(0xB8141A24),
                    startAngle = startAngle,
                    sweepAngle = sweep - 3f,
                    useCenter = true,
                    topLeft = Offset(center.x - r, center.y - r),
                    size = Size(r * 2f, r * 2f)
                )
                drawArc(
                    color = if (active) g.accentColor else Color(0x44FFFFFF),
                    startAngle = startAngle,
                    sweepAngle = sweep - 3f,
                    useCenter = false,
                    topLeft = Offset(center.x - r, center.y - r),
                    size = Size(r * 2f, r * 2f),
                    style = Stroke(width = if (active) 4f else 1.5f)
                )
            }
            drawCircle(color = AlkalineObsidian, radius = 64f, center = center)
        }

        // Center Hub Info
        Column(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFF0E131B))
                .border(1.5.dp, selectedMeta.accentColor, CircleShape)
                .clickable(onClick = onDismiss)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = selectedMeta.name.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                color = selectedMeta.accentColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = selectedMeta.description,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }

        // 8 Gear Nodes
        gadgets.forEachIndexed { idx, gadget ->
            val deg = -90.0 + idx * (360.0 / gadgets.size)
            val rad = Math.toRadians(deg)
            val ox = (cos(rad) * radiusPx).roundToInt()
            val oy = (sin(rad) * radiusPx).roundToInt()
            val isSelected = gadget.type == state.selectedGear

            Box(
                modifier = Modifier
                    .offset { IntOffset(ox, oy) }
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) gadget.accentColor else Color(0xDD1A222E))
                    .border(1.5.dp, gadget.accentColor, CircleShape)
                    .clickable { onSelectGear(gadget.type) }
                    .testTag("gear_item_${gadget.type.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = gadget.shortLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (isSelected) AlkalineObsidian else TextPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun RadioWheelContent(
    state: AlkalineUiState,
    onSelectRadio: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val stations = AlkalineCityWorld.radioStations
    val active = state.activeStation
    val radiusPx = with(LocalDensity.current) { 108.dp.toPx() }

    Box(
        modifier = Modifier.size(310.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f - 4f
            val sweep = 360f / stations.size
            stations.forEachIndexed { idx, st ->
                val isSelected = st.id == active.id
                val startAngle = -90f - sweep / 2f + idx * sweep
                drawArc(
                    color = if (isSelected) st.accentColor.copy(alpha = 0.34f) else Color(0xB8141A24),
                    startAngle = startAngle,
                    sweepAngle = sweep - 4f,
                    useCenter = true,
                    topLeft = Offset(center.x - r, center.y - r),
                    size = Size(r * 2f, r * 2f)
                )
                drawArc(
                    color = if (isSelected) st.accentColor else Color(0x44FFFFFF),
                    startAngle = startAngle,
                    sweepAngle = sweep - 4f,
                    useCenter = false,
                    topLeft = Offset(center.x - r, center.y - r),
                    size = Size(r * 2f, r * 2f),
                    style = Stroke(width = if (isSelected) 4f else 1.5f)
                )
            }
            drawCircle(color = AlkalineObsidian, radius = 64f, center = center)
        }

        Column(
            modifier = Modifier
                .size(116.dp)
                .clip(CircleShape)
                .background(Color(0xFF0E131B))
                .border(1.5.dp, active.accentColor, CircleShape)
                .clickable(onClick = onDismiss)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = active.frequency,
                style = MaterialTheme.typography.titleMedium,
                color = active.accentColor
            )
            Text(
                text = active.name.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = active.genre,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        stations.forEachIndexed { idx, st ->
            val deg = -90.0 + idx * (360.0 / stations.size)
            val rad = Math.toRadians(deg)
            val ox = (cos(rad) * radiusPx).roundToInt()
            val oy = (sin(rad) * radiusPx).roundToInt()
            val isSelected = st.id == active.id

            Column(
                modifier = Modifier
                    .offset { IntOffset(ox, oy) }
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) st.accentColor else Color(0xDD1A222E))
                    .border(1.5.dp, st.accentColor, CircleShape)
                    .clickable { onSelectRadio(st.id) }
                    .testTag("radio_station_${st.id}"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = st.frequency,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (isSelected) AlkalineObsidian else st.accentColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = st.name.take(7).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = if (isSelected) AlkalineObsidian else TextPrimary
                )
            }
        }
    }
}
