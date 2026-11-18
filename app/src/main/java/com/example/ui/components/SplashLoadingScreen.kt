package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AlkalineObsidian
import com.example.ui.theme.AlkalineTeal
import com.example.ui.theme.GtaGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VegaEmerald
import kotlinx.coroutines.delay

@Composable
fun SplashLoadingScreen(
    onEnterCity: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_zoom")
    val zoomScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ken_burns"
    )

    val tips = remember {
        listOf(
            "TIP: Open the Character Wheel to switch between Kai, Vega, and Dex anywhere in Alkaline City.",
            "TIP: Tap anywhere on the Pause Map to plot a purple GPS route on your radar.",
            "TIP: Use your iFruit phone to call Oracle and scrub active Wanted stars.",
            "TIP: Trade ACX / BAWSAQ shares on your iFruit phone while cruising Downtown Chrome."
        )
    }
    var tipIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2400L)
            tipIndex = (tipIndex + 1) % tips.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AlkalineObsidian)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onEnterCity
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_alkaline_hero),
            contentDescription = "Alkaline City artwork",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoomScale
                    scaleY = zoomScale
                }
        )

        // Cinematic dark gradient scrims for legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0xAA07090D),
                        0.38f to Color(0x2207090D),
                        0.72f to Color(0xBB07090D),
                        1.0f to Color(0xF507090D)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top GTA V-styled Alkaline City Lockup
            Column(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(GtaGreen)
                            .border(1.5.dp, Color.White, RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "EDITION V",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            letterSpacing = 1.6.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "WEST COAST SANDBOX",
                        style = MaterialTheme.typography.labelLarge,
                        color = AlkalineTeal,
                        letterSpacing = 1.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ALKALINE",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 54.sp, lineHeight = 52.sp),
                    color = TextPrimary
                )
                Text(
                    text = "CITY",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 54.sp, lineHeight = 50.sp),
                    color = VegaEmerald
                )
            }

            // Bottom Loading Bar + Enter Action
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xB30F141C))
                        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(6.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = tips[tipIndex],
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = VegaEmerald,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.loading_city),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                    }

                    Button(
                        onClick = onEnterCity,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("enter_city_button"),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VegaEmerald,
                            contentColor = AlkalineObsidian
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.enter_city),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }
                }
            }
        }
    }
}
