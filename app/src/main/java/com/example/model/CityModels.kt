package com.example.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

enum class ProtagonistId {
    KAI, VEGA, DEX
}

data class CharacterStats(
    val stamina: Float,
    val driving: Float,
    val flying: Float,
    val stealth: Float,
    val hacking: Float,
    val special: Float
)

data class ProtagonistProfile(
    val id: ProtagonistId,
    val firstName: String,
    val fullName: String,
    val role: String,
    val specialName: String,
    val specialDesc: String,
    val homeDistrict: String,
    val signatureColor: Color,
    @DrawableRes val portraitRes: Int,
    val defaultVehicleId: String,
    val spawnX: Float,
    val spawnY: Float,
    val stats: CharacterStats
)

data class CharacterRuntimeState(
    val x: Float,
    val y: Float,
    val angleDeg: Float,
    val cash: Long,
    val health: Float,
    val armor: Float,
    val specialMeter: Float,
    val vehicleId: String
)

enum class CityDistrict(
    val displayName: String,
    val subtitle: String,
    val accentColor: Color
) {
    ALKALINE_PIER("ALKALINE PIER", "WEST COAST BOARDWALK", Color(0xFF00E5FF)),
    DOWNTOWN_CHROME("DOWNTOWN CHROME", "FINANCIAL CORE", Color(0xFF3498DB)),
    NEON_BOULEVARD("NEON BOULEVARD", "MIDTOWN STRIP", Color(0xFF2ECC71)),
    INDUSTRIAL_DOCKS("TERMINAL DOCKS", "SOUTH HARBOR", Color(0xFFE67E22)),
    ALKALINE_HILLS("ALKALINE HILLS", "HIGHLAND CANYON", Color(0xFFB366FF))
}

data class CityBuilding(
    val id: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val elevation: Float,
    val wallColor: Color,
    val roofColor: Color,
    val neonTrim: Color? = null,
    val hasHelipad: Boolean = false
)

data class CityRoad(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val horizontal: Boolean,
    val lanes: Int = 2
)

data class CityVehicle(
    val id: String,
    val name: String,
    val category: String,
    val topSpeed: Float,
    val acceleration: Float,
    val handling: Float,
    val bodyColor: Color,
    val accentColor: Color,
    val length: Float,
    val width: Float
)

data class TrafficCar(
    val id: Int,
    val x: Float,
    val y: Float,
    val angleDeg: Float,
    val speed: Float,
    val color: Color,
    val horizontal: Boolean,
    val direction: Float
)

data class PatrolCar(
    val id: Int,
    val x: Float,
    val y: Float,
    val angleDeg: Float,
    val speed: Float
)

data class CashDrop(
    val id: Int,
    val x: Float,
    val y: Float,
    val amount: Long,
    val collected: Boolean = false
)

data class MissionDef(
    val id: String,
    val codeLetter: String,
    val title: String,
    val giver: String,
    val district: CityDistrict,
    val description: String,
    val objectiveText: String,
    val rewardCash: Long,
    val startX: Float,
    val startY: Float,
    val targetX: Float,
    val targetY: Float,
    val accentColor: Color
)

data class RadioStation(
    val id: String,
    val name: String,
    val frequency: String,
    val genre: String,
    val tracks: List<String>,
    val accentColor: Color
)

enum class GearType {
    NITRO_CELL,
    EMP_PULSE,
    ARMOR_PLATE,
    CASH_SCANNER,
    DECOY_FLARE,
    OVERCLOCK,
    REPAIR_KIT,
    UNARMED
}

data class GearGadget(
    val type: GearType,
    val name: String,
    val shortLabel: String,
    val description: String,
    val accentColor: Color
)

data class CityStock(
    val ticker: String,
    val company: String,
    val price: Double,
    val deltaPercent: Double,
    val sharesOwned: Int = 0
)

data class PhoneContact(
    val id: String,
    val name: String,
    val role: String,
    val accentColor: Color,
    val actionLabel: String
)

data class PhoneMessage(
    val id: String,
    val sender: String,
    val body: String,
    val timestamp: String,
    val unread: Boolean = true
)

data class BriefEntry(
    val id: Long,
    val title: String,
    val detail: String,
    val accentColor: Color
)

data class Waypoint(
    val x: Float,
    val y: Float,
    val label: String
)

enum class OverlayType {
    NONE,
    CHARACTER_WHEEL,
    GEAR_WHEEL,
    RADIO_WHEEL,
    PAUSE_MENU,
    IFRUIT_PHONE
}

enum class PauseTab(val title: String) {
    MAP("MAP"),
    MISSIONS("MISSIONS"),
    GARAGE("GARAGE"),
    STATS("STATS"),
    BRIEF("BRIEF")
}

enum class IFruitApp {
    HOME,
    CONTACTS,
    MESSAGES,
    BAWSAQ,
    SNAPMATIC,
    SETTINGS
}

data class UserSettings(
    val hudOpacity: Float = 0.92f,
    val showDistrictBanners: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val radioStationId: String = "alkaline_fm"
)
