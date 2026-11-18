package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AlkalineCityWorld
import com.example.model.IFruitApp
import com.example.ui.theme.AlkalineObsidian
import com.example.ui.theme.AlkalineTeal
import com.example.ui.theme.CashGreen
import com.example.ui.theme.DexAmber
import com.example.ui.theme.SpecialGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VegaEmerald
import com.example.ui.theme.WantedPoliceRed
import com.example.ui.theme.WaypointPurple
import com.example.viewmodel.AlkalineUiState
import com.example.viewmodel.AlkalineViewModel

@Composable
fun IFruitPhoneOverlay(
    state: AlkalineUiState,
    onSelectApp: (IFruitApp) -> Unit,
    onCallContact: (String) -> Unit,
    onBuyStock: (String) -> Unit,
    onSellStock: (String) -> Unit,
    onTakeSnapmatic: () -> Unit,
    onHudOpacityChange: (Float) -> Unit,
    onDistrictBannersChange: (Boolean) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler {
        if (state.iFruitApp != IFruitApp.HOME) {
            onSelectApp(IFruitApp.HOME)
        } else {
            onDismiss()
        }
    }

    val profile = AlkalineCityWorld.protagonists[state.activeCharacter]!!

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000))
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Sleek iFruit Smartphone Bezel
        Column(
            modifier = Modifier
                .width(284.dp)
                .height(470.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF0A0E14))
                .border(3.dp, Color(0xFF475569), RoundedCornerShape(28.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(10.dp)
                .testTag("ifruit_phone_frame")
        ) {
            // Top Speaker Notch & Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("iFruit • 5G", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Box(
                    modifier = Modifier
                        .width(46.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF334155))
                )
                Text("09:49", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
            }

            // Screen Body
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                profile.signatureColor.copy(alpha = 0.28f),
                                Color(0xFF0E141E),
                                Color(0xFF0A0D12)
                            )
                        )
                    )
                    .padding(10.dp)
            ) {
                when (state.iFruitApp) {
                    IFruitApp.HOME -> PhoneHomeGrid(state = state, onSelectApp = onSelectApp)
                    IFruitApp.CONTACTS -> PhoneContactsView(
                        state = state,
                        onBack = { onSelectApp(IFruitApp.HOME) },
                        onCallContact = onCallContact
                    )
                    IFruitApp.MESSAGES -> PhoneMessagesView(
                        state = state,
                        onBack = { onSelectApp(IFruitApp.HOME) }
                    )
                    IFruitApp.BAWSAQ -> PhoneBawsaqView(
                        state = state,
                        onBack = { onSelectApp(IFruitApp.HOME) },
                        onBuy = onBuyStock,
                        onSell = onSellStock
                    )
                    IFruitApp.SNAPMATIC -> PhoneSnapmaticView(
                        state = state,
                        onBack = { onSelectApp(IFruitApp.HOME) },
                        onCapture = onTakeSnapmatic
                    )
                    IFruitApp.SETTINGS -> PhoneSettingsView(
                        state = state,
                        onBack = { onSelectApp(IFruitApp.HOME) },
                        onHudOpacityChange = onHudOpacityChange,
                        onDistrictBannersChange = onDistrictBannersChange,
                        onHapticsChange = onHapticsChange
                    )
                }
            }

            // Bottom Home / Close Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (state.iFruitApp != IFruitApp.HOME) onSelectApp(IFruitApp.HOME) else onDismiss()
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF475569), CircleShape)
                        .clickable { onSelectApp(IFruitApp.HOME) }
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(48.dp).testTag("close_ifruit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close phone",
                        tint = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoneHomeGrid(
    state: AlkalineUiState,
    onSelectApp: (IFruitApp) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = state.currentDistrict.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Text(
                text = "$${AlkalineViewModel.formatCash(state.activeRuntime.cash)}",
                style = MaterialTheme.typography.labelLarge,
                color = CashGreen
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PhoneAppIcon("Messages", Icons.Default.Email, VegaEmerald, "ifruit_app_messages") {
                    onSelectApp(IFruitApp.MESSAGES)
                }
                PhoneAppIcon("Contacts", Icons.Default.Contacts, AlkalineTeal, "ifruit_app_contacts") {
                    onSelectApp(IFruitApp.CONTACTS)
                }
                PhoneAppIcon("BAWSAQ", Icons.Default.TrendingUp, SpecialGold, "ifruit_app_bawsaq") {
                    onSelectApp(IFruitApp.BAWSAQ)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PhoneAppIcon("Snapmatic", Icons.Default.CameraAlt, DexAmber, "ifruit_app_snapmatic") {
                    onSelectApp(IFruitApp.SNAPMATIC)
                }
                PhoneAppIcon("Settings", Icons.Default.Settings, Color(0xFF94A3B8), "ifruit_app_settings") {
                    onSelectApp(IFruitApp.SETTINGS)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun PhoneAppIcon(
    label: String,
    icon: ImageVector,
    color: Color,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = AlkalineObsidian, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextPrimary)
    }
}

@Composable
private fun PhoneSubHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
    }
}

@Composable
private fun PhoneContactsView(
    state: AlkalineUiState,
    onBack: () -> Unit,
    onCallContact: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PhoneSubHeader("CONTACTS", onBack)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 6.dp)
        ) {
            items(AlkalineCityWorld.phoneContacts, key = { it.id }) { contact ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF171F2C))
                        .clickable { onCallContact(contact.id) }
                        .padding(10.dp)
                        .testTag("contact_${contact.id}"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(contact.name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text(contact.role, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary)
                    }
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = contact.actionLabel,
                        tint = contact.accentColor
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoneMessagesView(
    state: AlkalineUiState,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PhoneSubHeader("MESSAGES", onBack)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 6.dp)
        ) {
            items(state.messages, key = { it.id }) { msg ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF171F2C))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(msg.sender, style = MaterialTheme.typography.labelLarge, color = AlkalineTeal)
                        Text(msg.timestamp, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(msg.body, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp), color = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun PhoneBawsaqView(
    state: AlkalineUiState,
    onBack: () -> Unit,
    onBuy: (String) -> Unit,
    onSell: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PhoneSubHeader("BAWSAQ MARKETS", onBack)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 6.dp)
        ) {
            items(state.stocks, key = { it.ticker }) { stock ->
                val up = stock.deltaPercent >= 0
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF171F2C))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("${stock.ticker} • ${stock.company}", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                            Text("Owned: ${stock.sharesOwned}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("$${stock.price}", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                            Text(
                                text = "${if (up) "+" else ""}${stock.deltaPercent}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (up) CashGreen else WantedPoliceRed
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onBuy(stock.ticker) },
                            modifier = Modifier.weight(1f).height(36.dp).testTag("buy_${stock.ticker}"),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CashGreen, contentColor = AlkalineObsidian)
                        ) {
                            Text("BUY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { onSell(stock.ticker) },
                            modifier = Modifier.weight(1f).height(36.dp).testTag("sell_${stock.ticker}"),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155), contentColor = TextPrimary)
                        ) {
                            Text("SELL", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhoneSnapmaticView(
    state: AlkalineUiState,
    onBack: () -> Unit,
    onCapture: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PhoneSubHeader("SNAPMATIC", onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF171F2C))
                .border(1.dp, AlkalineTeal, RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = AlkalineTeal, modifier = Modifier.size(38.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(state.currentDistrict.displayName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text("Gallery Photos: ${state.snapmaticCount}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        Button(
            onClick = onCapture,
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("snapmatic_capture_button"),
            colors = ButtonDefaults.buttonColors(containerColor = AlkalineTeal, contentColor = AlkalineObsidian)
        ) {
            Text("TAKE SNAPSHOT", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun PhoneSettingsView(
    state: AlkalineUiState,
    onBack: () -> Unit,
    onHudOpacityChange: (Float) -> Unit,
    onDistrictBannersChange: (Boolean) -> Unit,
    onHapticsChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PhoneSubHeader("SETTINGS", onBack)
        Column {
            Text(
                text = "HUD OPACITY (${(state.settings.hudOpacity * 100).toInt()}%)",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Slider(
                value = state.settings.hudOpacity,
                onValueChange = onHudOpacityChange,
                valueRange = 0.5f..1f,
                colors = SliderDefaults.colors(thumbColor = WaypointPurple, activeTrackColor = WaypointPurple)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("District Banners", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            Switch(checked = state.settings.showDistrictBanners, onCheckedChange = onDistrictBannersChange)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Vibration / Haptics", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            Switch(checked = state.settings.hapticsEnabled, onCheckedChange = onHapticsChange)
        }
    }
}
