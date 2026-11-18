package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "alkaline_settings")

class SettingsRepository(private val context: Context) {
    private object Keys {
        val HUD_OPACITY = floatPreferencesKey("hud_opacity")
        val SHOW_DISTRICT_BANNERS = booleanPreferencesKey("show_district_banners")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val RADIO_STATION_ID = stringPreferencesKey("radio_station_id")
    }

    val settingsFlow: Flow<UserSettings> = context.settingsDataStore.data.map { prefs ->
        UserSettings(
            hudOpacity = prefs[Keys.HUD_OPACITY] ?: 0.92f,
            showDistrictBanners = prefs[Keys.SHOW_DISTRICT_BANNERS] ?: true,
            hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
            radioStationId = prefs[Keys.RADIO_STATION_ID] ?: "alkaline_fm"
        )
    }

    suspend fun updateHudOpacity(opacity: Float) {
        context.settingsDataStore.edit { it[Keys.HUD_OPACITY] = opacity.coerceIn(0.45f, 1.0f) }
    }

    suspend fun updateDistrictBanners(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.SHOW_DISTRICT_BANNERS] = enabled }
    }

    suspend fun updateHaptics(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun updateRadioStation(stationId: String) {
        context.settingsDataStore.edit { it[Keys.RADIO_STATION_ID] = stationId }
    }
}
