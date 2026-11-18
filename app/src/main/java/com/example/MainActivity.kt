package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.OverlayType
import com.example.ui.components.CharacterSwitchWheelOverlay
import com.example.ui.components.CityWorldCanvas
import com.example.ui.components.GtaHudOverlay
import com.example.ui.components.IFruitPhoneOverlay
import com.example.ui.components.PauseMenuOverlay
import com.example.ui.components.RadialWheelOverlay
import com.example.ui.components.SplashLoadingScreen
import com.example.ui.theme.AlkalineCityTheme
import com.example.ui.theme.AlkalineObsidian
import com.example.viewmodel.AlkalineViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            AlkalineCityTheme {
                AlkalineCityApp()
            }
        }
    }
}

@Composable
fun AlkalineCityApp(
    viewModel: AlkalineViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current

    var joyX by remember { mutableFloatStateOf(0f) }
    var joyY by remember { mutableFloatStateOf(0f) }
    var nitroHeld by remember { mutableStateOf(false) }
    var elapsedSec by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { frameNanos ->
                if (lastNanos != 0L) {
                    val dt = (frameNanos - lastNanos) / 1_000_000_000f
                    elapsedSec += dt
                    viewModel.stepSimulation(
                        dt = dt,
                        joyX = joyX,
                        joyY = joyY,
                        nitroHeld = nitroHeld,
                        brakeHeld = false
                    )
                }
                lastNanos = frameNanos
            }
        }
    }

    fun buzz() {
        if (state.settings.hapticsEnabled) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AlkalineObsidian)
    ) {
        if (state.isSplashVisible) {
            SplashLoadingScreen(
                onEnterCity = {
                    buzz()
                    viewModel.dismissSplash()
                }
            )
        } else {
            // 1. Live 2.5D Open-World Alkaline City Canvas
            CityWorldCanvas(
                state = state,
                timeSec = elapsedSec,
                modifier = Modifier.fillMaxSize()
            )

            // 2. GTA V HUD + Radar + Analog Stick Controls
            GtaHudOverlay(
                state = state,
                timeSec = elapsedSec,
                onJoystickChange = { x, y ->
                    joyX = x
                    joyY = y
                },
                onNitroHoldChange = { held -> nitroHeld = held },
                onOpenOverlay = { overlay ->
                    buzz()
                    viewModel.openOverlay(overlay)
                },
                onToggleVehicle = {
                    buzz()
                    viewModel.toggleVehicle()
                },
                onActivateSpecial = {
                    buzz()
                    viewModel.activateSpecial()
                },
                onUseGear = {
                    buzz()
                    viewModel.useSelectedGear()
                },
                onTriggerWanted = {
                    buzz()
                    viewModel.triggerWantedStar()
                }
            )

            // 3. Overlays: Character Wheel, Gear/Radio Wheel, Pause Menu, iFruit Phone
            when (state.overlay) {
                OverlayType.CHARACTER_WHEEL -> CharacterSwitchWheelOverlay(
                    state = state,
                    onSelectCharacter = { id ->
                        buzz()
                        viewModel.switchCharacter(id)
                    },
                    onDismiss = { viewModel.closeOverlay() }
                )

                OverlayType.GEAR_WHEEL, OverlayType.RADIO_WHEEL -> RadialWheelOverlay(
                    state = state,
                    isRadioMode = state.overlay == OverlayType.RADIO_WHEEL,
                    onSwitchWheelMode = { mode -> viewModel.openOverlay(mode) },
                    onSelectGear = { gear ->
                        buzz()
                        viewModel.selectGear(gear)
                    },
                    onSelectRadio = { stationId ->
                        buzz()
                        viewModel.selectRadioStation(stationId)
                    },
                    onDismiss = { viewModel.closeOverlay() }
                )

                OverlayType.PAUSE_MENU -> PauseMenuOverlay(
                    state = state,
                    onSelectTab = { viewModel.selectPauseTab(it) },
                    onSetWaypoint = { x, y, label ->
                        buzz()
                        viewModel.setWaypoint(x, y, label)
                    },
                    onClearWaypoint = { viewModel.clearWaypoint() },
                    onLaunchMission = { mission ->
                        buzz()
                        viewModel.launchMission(mission)
                    },
                    onDeliverVehicle = { vehicle ->
                        buzz()
                        viewModel.deliverVehicle(vehicle)
                    },
                    onResetSession = {
                        buzz()
                        viewModel.resetSession()
                    },
                    onDismiss = { viewModel.closeOverlay() }
                )

                OverlayType.IFRUIT_PHONE -> IFruitPhoneOverlay(
                    state = state,
                    onSelectApp = { viewModel.openIFruitApp(it) },
                    onCallContact = { id ->
                        buzz()
                        viewModel.callContact(id)
                    },
                    onBuyStock = { ticker ->
                        buzz()
                        viewModel.buyStock(ticker)
                    },
                    onSellStock = { ticker ->
                        buzz()
                        viewModel.sellStock(ticker)
                    },
                    onTakeSnapmatic = {
                        buzz()
                        viewModel.takeSnapmatic()
                    },
                    onHudOpacityChange = { viewModel.setHudOpacity(it) },
                    onDistrictBannersChange = { viewModel.setDistrictBanners(it) },
                    onHapticsChange = { viewModel.setHaptics(it) },
                    onDismiss = { viewModel.closeOverlay() }
                )

                OverlayType.NONE -> Unit
            }

            // 4. Snapmatic Camera Shutter Flash
            AnimatedVisibility(
                visible = state.snapmaticFlash,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.72f))
                )
            }

            // 5. Character Switch GTA V Camera Flash
            AnimatedVisibility(
                visible = state.switchFlash,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x552ECC71))
                )
            }
        }
    }
}
