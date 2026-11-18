package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.data.AlkalineCityWorld
import com.example.ui.theme.AlkalineTeal
import com.example.ui.theme.CashGreen
import com.example.ui.theme.SpecialGold
import com.example.ui.theme.VegaEmerald
import com.example.ui.theme.WantedPoliceBlue
import com.example.ui.theme.WantedPoliceRed
import com.example.ui.theme.WaypointPurple
import com.example.viewmodel.AlkalineUiState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CityWorldCanvas(
    state: AlkalineUiState,
    timeSec: Float,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.fillMaxSize()) {
        val camZoom = when {
            state.switchFlash -> 0.55f
            state.inVehicle -> 0.92f
            else -> 1.15f
        }

        val cx = size.width / 2f
        val cy = size.height / 2f
        val camX = state.playerX
        val camY = state.playerY

        fun toScreen(wx: Float, wy: Float): Offset {
            return Offset(
                x = cx + (wx - camX) * camZoom,
                y = cy + (wy - camY) * camZoom
            )
        }

        // 1. Base City Terrain
        drawRect(
            color = Color(0xFF151C24),
            size = size
        )

        // 2. Ocean & Beach along West Coast
        val beachTopLeft = toScreen(0f, 0f)
        val beachRightX = toScreen(AlkalineCityWorld.BEACH_EDGE_X, 0f).x
        val oceanRightX = toScreen(AlkalineCityWorld.OCEAN_EDGE_X, 0f).x
        val worldBottomY = toScreen(0f, AlkalineCityWorld.WORLD_HEIGHT).y

        // Sand strip
        if (beachRightX > 0f) {
            drawRect(
                color = Color(0xFFC2A878),
                topLeft = Offset(oceanRightX, beachTopLeft.y),
                size = Size((beachRightX - oceanRightX).coerceAtLeast(0f), worldBottomY - beachTopLeft.y)
            )
        }
        // Ocean water
        if (oceanRightX > 0f) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF062338), Color(0xFF0A4D68), Color(0xFF14748F)),
                    startX = 0f,
                    endX = oceanRightX.coerceAtLeast(10f)
                ),
                topLeft = Offset(0f, 0f),
                size = Size(oceanRightX, size.height)
            )
            // Animated shoreline waves
            val waveShift = sin(timeSec * 2.2f) * 6f * camZoom
            drawLine(
                color = Color(0x9980DEEA),
                start = Offset(oceanRightX + waveShift, 0f),
                end = Offset(oceanRightX + waveShift, size.height),
                strokeWidth = 4f * camZoom
            )
        }

        // 3. Alkaline Pier Wooden Deck & Ferris Wheel landmark
        val pierPos = toScreen(40f, 1004f)
        drawRect(
            color = Color(0xFF5D4633),
            topLeft = pierPos,
            size = Size(290f * camZoom, 98f * camZoom)
        )
        // Ferris Wheel silhouette on the Pier
        val ferrisCenter = toScreen(110f, 1054f)
        drawCircle(
            color = AlkalineTeal.copy(alpha = 0.7f),
            radius = 34f * camZoom,
            center = ferrisCenter,
            style = Stroke(width = 3f * camZoom)
        )
        for (spoke in 0 until 8) {
            val a = spoke * (Math.PI / 4.0) + timeSec * 0.45f
            drawLine(
                color = VegaEmerald.copy(alpha = 0.65f),
                start = ferrisCenter,
                end = Offset(
                    ferrisCenter.x + (cos(a) * 34f * camZoom).toFloat(),
                    ferrisCenter.y + (sin(a) * 34f * camZoom).toFloat()
                ),
                strokeWidth = 1.6f * camZoom
            )
        }

        // 4. Roads & Lane Markings
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(22f * camZoom, 18f * camZoom), 0f)
        for (road in AlkalineCityWorld.roads) {
            val topLeft = toScreen(road.x, road.y)
            val rw = road.width * camZoom
            val rh = road.height * camZoom
            if (topLeft.x + rw < -50f || topLeft.x > size.width + 50f ||
                topLeft.y + rh < -50f || topLeft.y > size.height + 50f
            ) continue

            // Sidewalk curb
            drawRect(
                color = Color(0xFF2B3440),
                topLeft = Offset(topLeft.x - 6f * camZoom, topLeft.y - 6f * camZoom),
                size = Size(rw + 12f * camZoom, rh + 12f * camZoom)
            )
            // Asphalt
            drawRect(
                color = Color(0xFF1D222B),
                topLeft = topLeft,
                size = Size(rw, rh)
            )
            // Yellow dashed center divider
            if (road.horizontal) {
                val midY = topLeft.y + rh / 2f
                drawLine(
                    color = Color(0xFFD4AC0D),
                    start = Offset(topLeft.x, midY),
                    end = Offset(topLeft.x + rw, midY),
                    strokeWidth = 2.5f * camZoom,
                    pathEffect = dashEffect
                )
            } else {
                val midX = topLeft.x + rw / 2f
                drawLine(
                    color = Color(0xFFD4AC0D),
                    start = Offset(midX, topLeft.y),
                    end = Offset(midX, topLeft.y + rh),
                    strokeWidth = 2.5f * camZoom,
                    pathEffect = dashEffect
                )
            }
        }

        // 5. Palm Trees along Neon Boulevard & Coastal Highway
        val palmSpots = listOf(
            330f to 420f, 330f to 620f, 330f to 860f, 330f to 1240f, 330f to 1620f,
            710f to 460f, 710f to 860f, 710f to 1240f, 710f to 1660f,
            835f to 990f, 1020f to 990f, 1340f to 990f, 1520f to 990f
        )
        for ((px, py) in palmSpots) {
            val p = toScreen(px, py)
            if (p.x < -60f || p.x > size.width + 60f || p.y < -60f || p.y > size.height + 60f) continue
            drawCircle(
                color = Color(0x44000000),
                radius = 18f * camZoom,
                center = Offset(p.x + 6f * camZoom, p.y + 6f * camZoom)
            )
            drawCircle(
                color = Color(0xFF1E7E48),
                radius = 16f * camZoom,
                center = p
            )
            drawCircle(
                color = Color(0xFF2ECC71),
                radius = 9f * camZoom,
                center = p
            )
        }

        // 6. Purple GPS Waypoint Route Line
        state.waypoint?.let { wp ->
            val pScreen = toScreen(state.playerX, state.playerY)
            val wpScreen = toScreen(wp.x, wp.y)
            val elbowScreen = toScreen(wp.x, state.playerY)
            val routePath = Path().apply {
                moveTo(pScreen.x, pScreen.y)
                lineTo(elbowScreen.x, elbowScreen.y)
                lineTo(wpScreen.x, wpScreen.y)
            }
            drawPath(
                path = routePath,
                color = WaypointPurple.copy(alpha = 0.78f),
                style = Stroke(width = 7f * camZoom)
            )
            val pulse = 16f + sin(timeSec * 5f) * 5f
            drawCircle(
                color = WaypointPurple.copy(alpha = 0.35f),
                radius = pulse * camZoom,
                center = wpScreen
            )
            drawCircle(
                color = WaypointPurple,
                radius = 9f * camZoom,
                center = wpScreen
            )
        }

        // 7. Cash Drops ($)
        for (drop in state.cashDrops) {
            if (drop.collected) continue
            val dp = toScreen(drop.x, drop.y)
            if (dp.x < -40f || dp.x > size.width + 40f || dp.y < -40f || dp.y > size.height + 40f) continue
            val pulse = 14f + sin(timeSec * 4.2f + drop.id) * 3f
            drawCircle(
                color = CashGreen.copy(alpha = 0.25f),
                radius = pulse * camZoom,
                center = dp
            )
            drawCircle(
                color = CashGreen,
                radius = 10f * camZoom,
                center = dp
            )
            drawLabel(textMeasurer, "$", dp, Color.White, (11f * camZoom).coerceAtLeast(8f))
        }

        // 8. Mission Markers (K, V, D, H)
        for (mission in AlkalineCityWorld.missions) {
            if (mission.id in state.completedMissionIds) continue
            val mp = toScreen(mission.startX, mission.startY)
            if (mp.x < -50f || mp.x > size.width + 50f || mp.y < -50f || mp.y > size.height + 50f) continue
            val ringRadius = (20f + sin(timeSec * 3.8f) * 4f) * camZoom
            drawCircle(
                color = mission.accentColor.copy(alpha = 0.28f),
                radius = ringRadius,
                center = mp
            )
            drawCircle(
                color = Color(0xFF0D1117),
                radius = 14f * camZoom,
                center = mp
            )
            drawCircle(
                color = mission.accentColor,
                radius = 14f * camZoom,
                center = mp,
                style = Stroke(width = 2.5f * camZoom)
            )
            drawLabel(textMeasurer, mission.codeLetter, mp, mission.accentColor, (12f * camZoom).coerceAtLeast(9f))
        }

        // 9. NPC Traffic Cars
        for (car in state.trafficCars) {
            val cp = toScreen(car.x, car.y)
            if (cp.x < -60f || cp.x > size.width + 60f || cp.y < -60f || cp.y > size.height + 60f) continue
            drawTopDownCar(
                center = cp,
                angleDeg = car.angleDeg,
                length = 44f * camZoom,
                width = 22f * camZoom,
                bodyColor = car.color,
                accentColor = Color(0xFFCBD5E1),
                headlightsOn = true,
                sirenPhase = null
            )
        }

        // 10. Alkaline PD Patrol Cars
        for (patrol in state.patrolCars) {
            val pp = toScreen(patrol.x, patrol.y)
            if (pp.x < -80f || pp.x > size.width + 80f || pp.y < -80f || pp.y > size.height + 80f) continue
            val siren = ((timeSec * 8f).toInt() % 2 == 0)
            drawTopDownCar(
                center = pp,
                angleDeg = patrol.angleDeg,
                length = 50f * camZoom,
                width = 25f * camZoom,
                bodyColor = Color(0xFF0F172A),
                accentColor = Color.White,
                headlightsOn = true,
                sirenPhase = siren
            )
        }

        // 11. Player (Car or On-Foot)
        val playerScreen = toScreen(state.playerX, state.playerY)
        val activeProfile = AlkalineCityWorld.protagonists[state.activeCharacter]!!
        if (state.inVehicle) {
            if (state.nitroTimer > 0f || state.isSpecialActive) {
                drawCircle(
                    color = (if (state.isSpecialActive) SpecialGold else AlkalineTeal).copy(alpha = 0.28f),
                    radius = 40f * camZoom,
                    center = playerScreen
                )
            }
            drawTopDownCar(
                center = playerScreen,
                angleDeg = state.playerAngleDeg,
                length = state.currentVehicle.length * camZoom,
                width = state.currentVehicle.width * camZoom,
                bodyColor = state.currentVehicle.bodyColor,
                accentColor = state.currentVehicle.accentColor,
                headlightsOn = true,
                sirenPhase = null
            )
        } else {
            // Parked personal vehicle nearby indicator + player dot
            rotate(degrees = state.playerAngleDeg, pivot = playerScreen) {
                drawCircle(
                    color = activeProfile.signatureColor.copy(alpha = 0.25f),
                    radius = 18f * camZoom,
                    center = playerScreen
                )
                drawCircle(
                    color = activeProfile.signatureColor,
                    radius = 10f * camZoom,
                    center = playerScreen
                )
                drawCircle(
                    color = Color.White,
                    radius = 10f * camZoom,
                    center = playerScreen,
                    style = Stroke(width = 2f)
                )
            }
        }

        // 12. 2.5D Parallax-Extruded Skyscrapers (drawn over streets for authentic GTA 2.5D depth!)
        for (b in AlkalineCityWorld.buildings) {
            val baseTL = toScreen(b.x, b.y)
            val bw = b.width * camZoom
            val bh = b.height * camZoom
            if (baseTL.x + bw < -120f || baseTL.x > size.width + 120f ||
                baseTL.y + bh < -120f || baseTL.y > size.height + 120f
            ) continue

            val bCenter = Offset(baseTL.x + bw / 2f, baseTL.y + bh / 2f)
            val parallaxX = ((bCenter.x - cx) / size.width.coerceAtLeast(1f)) * b.elevation * 2.2f * camZoom
            val parallaxY = ((bCenter.y - cy) / size.height.coerceAtLeast(1f)) * b.elevation * 2.2f * camZoom
            val roofTL = Offset(baseTL.x + parallaxX, baseTL.y + parallaxY)

            // Drop shadow
            drawRect(
                color = Color(0x55000000),
                topLeft = Offset(baseTL.x + 12f * camZoom, baseTL.y + 14f * camZoom),
                size = Size(bw, bh)
            )
            // Extruded side walls connecting base footprint and roof
            drawRect(
                color = b.wallColor,
                topLeft = baseTL,
                size = Size(bw, bh)
            )
            // Roof face
            drawRect(
                color = b.roofColor,
                topLeft = roofTL,
                size = Size(bw, bh)
            )
            // Neon roof trim
            b.neonTrim?.let { trim ->
                drawRect(
                    color = trim.copy(alpha = 0.72f),
                    topLeft = roofTL,
                    size = Size(bw, bh),
                    style = Stroke(width = 2.5f * camZoom)
                )
            }
            if (b.hasHelipad) {
                val heliCenter = Offset(roofTL.x + bw / 2f, roofTL.y + bh / 2f)
                drawCircle(
                    color = SpecialGold.copy(alpha = 0.65f),
                    radius = 24f * camZoom,
                    center = heliCenter,
                    style = Stroke(width = 2.5f * camZoom)
                )
                drawLabel(textMeasurer, "H", heliCenter, SpecialGold, (14f * camZoom).coerceAtLeast(9f))
            }
        }
    }
}

private fun DrawScope.drawTopDownCar(
    center: Offset,
    angleDeg: Float,
    length: Float,
    width: Float,
    bodyColor: Color,
    accentColor: Color,
    headlightsOn: Boolean,
    sirenPhase: Boolean?
) {
    rotate(degrees = angleDeg, pivot = center) {
        val left = center.x - length / 2f
        val top = center.y - width / 2f

        // Shadow
        drawRoundRect(
            color = Color(0x66000000),
            topLeft = Offset(left + 3f, top + 4f),
            size = Size(length, width),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Body
        drawRoundRect(
            color = bodyColor,
            topLeft = Offset(left, top),
            size = Size(length, width),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Racing stripe / trim
        drawRect(
            color = accentColor,
            topLeft = Offset(left + 4f, center.y - 2f),
            size = Size(length - 8f, 4f)
        )
        // Windshield
        drawRoundRect(
            color = Color(0xFF0B1320),
            topLeft = Offset(center.x - length * 0.14f, top + 3f),
            size = Size(length * 0.36f, width - 6f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        // Headlight beams
        if (headlightsOn) {
            drawCircle(
                color = Color(0x28FFF9C4),
                radius = width * 0.8f,
                center = Offset(center.x + length * 0.65f, center.y)
            )
            drawRect(
                color = Color(0xFFFFF59D),
                topLeft = Offset(left + length - 3f, top + 2f),
                size = Size(3f, 4f)
            )
            drawRect(
                color = Color(0xFFFFF59D),
                topLeft = Offset(left + length - 3f, top + width - 6f),
                size = Size(3f, 4f)
            )
        }
        // Police Lightbar
        sirenPhase?.let { redLeft ->
            drawRect(
                color = if (redLeft) WantedPoliceRed else WantedPoliceBlue,
                topLeft = Offset(center.x - 3f, top + 2f),
                size = Size(6f, width / 2f - 2f)
            )
            drawRect(
                color = if (redLeft) WantedPoliceBlue else WantedPoliceRed,
                topLeft = Offset(center.x - 3f, center.y),
                size = Size(6f, width / 2f - 2f)
            )
            drawCircle(
                color = (if (redLeft) WantedPoliceRed else WantedPoliceBlue).copy(alpha = 0.28f),
                radius = width * 1.15f,
                center = center
            )
        }
    }
}

private fun DrawScope.drawLabel(
    measurer: TextMeasurer,
    text: String,
    center: Offset,
    color: Color,
    fontSizeSp: Float
) {
    val result = measurer.measure(
        text = text,
        style = TextStyle(
            color = color,
            fontSize = fontSizeSp.sp,
            fontWeight = FontWeight.ExtraBold
        )
    )
    drawText(
        textLayoutResult = result,
        topLeft = Offset(
            center.x - result.size.width / 2f,
            center.y - result.size.height / 2f
        )
    )
}
