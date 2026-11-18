package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AlkalineCityWorld
import com.example.data.SettingsRepository
import com.example.model.BriefEntry
import com.example.model.CashDrop
import com.example.model.CharacterRuntimeState
import com.example.model.CityBuilding
import com.example.model.CityDistrict
import com.example.model.CityStock
import com.example.model.CityVehicle
import com.example.model.GearType
import com.example.model.IFruitApp
import com.example.model.MissionDef
import com.example.model.OverlayType
import com.example.model.PatrolCar
import com.example.model.PauseTab
import com.example.model.PhoneMessage
import com.example.model.ProtagonistId
import com.example.model.RadioStation
import com.example.model.TrafficCar
import com.example.model.UserSettings
import com.example.model.Waypoint
import com.example.ui.theme.AlkalineTeal
import com.example.ui.theme.CashGreen
import com.example.ui.theme.SpecialGold
import com.example.ui.theme.VegaEmerald
import com.example.ui.theme.WantedPoliceRed
import com.example.ui.theme.WaypointPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

data class AlkalineUiState(
    val isSplashVisible: Boolean = true,
    val activeCharacter: ProtagonistId = ProtagonistId.VEGA,
    val characterStates: Map<ProtagonistId, CharacterRuntimeState> = emptyMap(),
    val playerX: Float = 760f,
    val playerY: Float = 1380f,
    val playerAngleDeg: Float = -90f,
    val playerSpeed: Float = 0f,
    val inVehicle: Boolean = true,
    val currentVehicle: CityVehicle = AlkalineCityWorld.vehicles.first(),
    val wantedLevel: Int = 0,
    val wantedEvading: Boolean = false,
    val evadeProgress: Float = 0f,
    val patrolCars: List<PatrolCar> = emptyList(),
    val trafficCars: List<TrafficCar> = AlkalineCityWorld.initialTraffic(),
    val cashDrops: List<CashDrop> = AlkalineCityWorld.initialCashDrops(),
    val activeMission: MissionDef? = null,
    val completedMissionIds: Set<String> = emptySet(),
    val waypoint: Waypoint? = null,
    val currentDistrict: CityDistrict = CityDistrict.NEON_BOULEVARD,
    val districtBannerVisible: Boolean = false,
    val isSpecialActive: Boolean = false,
    val selectedGear: GearType = GearType.NITRO_CELL,
    val activeRadioId: String = "alkaline_fm",
    val radioTrackIndex: Int = 0,
    val overlay: OverlayType = OverlayType.NONE,
    val pauseTab: PauseTab = PauseTab.MAP,
    val iFruitApp: IFruitApp = IFruitApp.HOME,
    val stocks: List<CityStock> = AlkalineCityWorld.initialStocks,
    val messages: List<PhoneMessage> = AlkalineCityWorld.initialMessages,
    val briefLog: List<BriefEntry> = emptyList(),
    val cashDeltaText: String? = null,
    val centerBannerTitle: String? = null,
    val centerBannerSubtitle: String? = null,
    val centerBannerColor: Color = VegaEmerald,
    val snapmaticCount: Int = 0,
    val snapmaticFlash: Boolean = false,
    val switchFlash: Boolean = false,
    val nitroTimer: Float = 0f,
    val settings: UserSettings = UserSettings()
) {
    val activeRuntime: CharacterRuntimeState
        get() = characterStates[activeCharacter] ?: CharacterRuntimeState(
            x = playerX,
            y = playerY,
            angleDeg = playerAngleDeg,
            cash = 1245800L,
            health = 1f,
            armor = 0.78f,
            specialMeter = 0.85f,
            vehicleId = currentVehicle.id
        )

    val activeStation: RadioStation
        get() = AlkalineCityWorld.radioStations.find { it.id == activeRadioId }
            ?: AlkalineCityWorld.radioStations.first()
}

class AlkalineViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsRepo = SettingsRepository(application.applicationContext)

    private val initialCharStates = mapOf(
        ProtagonistId.KAI to CharacterRuntimeState(
            x = 1196f, y = 1054f, angleDeg = 0f,
            cash = 2480500L, health = 1f, armor = 0.90f, specialMeter = 0.90f,
            vehicleId = "solaris_gt"
        ),
        ProtagonistId.VEGA to CharacterRuntimeState(
            x = 772f, y = 1380f, angleDeg = -90f,
            cash = 842350L, health = 1f, armor = 0.76f, specialMeter = 0.88f,
            vehicleId = "vapor_gt"
        ),
        ProtagonistId.DEX to CharacterRuntimeState(
            x = 2064f, y = 648f, angleDeg = 180f,
            cash = 315400L, health = 1f, armor = 0.65f, specialMeter = 0.95f,
            vehicleId = "apex_trophy"
        )
    )

    private val _uiState = MutableStateFlow(
        AlkalineUiState(
            characterStates = initialCharStates,
            playerX = 772f,
            playerY = 1380f,
            playerAngleDeg = -90f,
            currentVehicle = AlkalineCityWorld.vehicles.first(),
            briefLog = listOf(
                BriefEntry(1L, "Arrived in Alkaline City", "Explore Neon Boulevard or open the Pause Map to plot a GPS route.", VegaEmerald),
                BriefEntry(2L, "iFruit Connected", "Use the smartphone HUD button to call Oracle, trade BAWSAQ stocks, or order rides.", AlkalineTeal)
            )
        )
    )
    val uiState: StateFlow<AlkalineUiState> = _uiState.asStateFlow()

    private var evadeClock = 0f
    private var bannerJobVersion = 0
    private var cashDeltaVersion = 0
    private var districtBannerVersion = 0
    private var briefCounter = 10L
    private var collisionCooldown = 0f

    init {
        viewModelScope.launch {
            settingsRepo.settingsFlow.collect { loaded ->
                _uiState.update {
                    it.copy(settings = loaded, activeRadioId = loaded.radioStationId)
                }
            }
        }
        viewModelScope.launch {
            while (true) {
                delay(4500L)
                nudgeStockMarket()
            }
        }
        viewModelScope.launch {
            while (true) {
                delay(14000L)
                _uiState.update { state ->
                    val trackCount = state.activeStation.tracks.size.coerceAtLeast(1)
                    state.copy(radioTrackIndex = (state.radioTrackIndex + 1) % trackCount)
                }
            }
        }
    }

    fun dismissSplash() {
        _uiState.update { it.copy(isSplashVisible = false) }
        showDistrictBanner(CityDistrict.NEON_BOULEVARD)
    }

    fun openOverlay(type: OverlayType) {
        _uiState.update { it.copy(overlay = type) }
    }

    fun closeOverlay() {
        _uiState.update { it.copy(overlay = OverlayType.NONE) }
    }

    fun selectPauseTab(tab: PauseTab) {
        _uiState.update { it.copy(pauseTab = tab) }
    }

    fun openIFruitApp(app: IFruitApp) {
        _uiState.update { state ->
            val updatedMessages = if (app == IFruitApp.MESSAGES) {
                state.messages.map { it.copy(unread = false) }
            } else {
                state.messages
            }
            state.copy(iFruitApp = app, messages = updatedMessages)
        }
    }

    fun switchCharacter(target: ProtagonistId) {
        val current = _uiState.value
        if (target == current.activeCharacter) {
            closeOverlay()
            return
        }
        val savedMap = current.characterStates.toMutableMap()
        val prevRuntime = current.activeRuntime.copy(
            x = current.playerX,
            y = current.playerY,
            angleDeg = current.playerAngleDeg,
            vehicleId = current.currentVehicle.id
        )
        savedMap[current.activeCharacter] = prevRuntime

        val nextRuntime = savedMap[target] ?: initialCharStates[target]!!
        val nextVehicle = AlkalineCityWorld.vehicles.find { it.id == nextRuntime.vehicleId }
            ?: AlkalineCityWorld.vehicles.first()
        val nextDistrict = AlkalineCityWorld.districtFor(nextRuntime.x, nextRuntime.y)
        val profile = AlkalineCityWorld.protagonists[target]!!

        _uiState.update {
            it.copy(
                activeCharacter = target,
                characterStates = savedMap,
                playerX = nextRuntime.x,
                playerY = nextRuntime.y,
                playerAngleDeg = nextRuntime.angleDeg,
                playerSpeed = 0f,
                currentVehicle = nextVehicle,
                currentDistrict = nextDistrict,
                wantedLevel = 0,
                patrolCars = emptyList(),
                isSpecialActive = false,
                overlay = OverlayType.NONE,
                switchFlash = true
            )
        }
        addBrief("Switched to ${profile.fullName}", "${profile.role} — ${profile.homeDistrict}", profile.signatureColor)
        showDistrictBanner(nextDistrict)
        viewModelScope.launch {
            delay(360L)
            _uiState.update { it.copy(switchFlash = false) }
        }
    }

    fun toggleVehicle() {
        _uiState.update { state ->
            val entering = !state.inVehicle
            state.copy(
                inVehicle = entering,
                playerSpeed = if (entering) state.playerSpeed else 0f
            )
        }
    }

    fun activateSpecial() {
        val state = _uiState.value
        val runtime = state.activeRuntime
        if (state.isSpecialActive) {
            _uiState.update { it.copy(isSpecialActive = false) }
            return
        }
        if (runtime.specialMeter < 0.2f) {
            showCenterBanner("SPECIAL DEPLETED", "Drive fast or pull stunts to recharge", SpecialGold)
            return
        }
        val profile = AlkalineCityWorld.protagonists[state.activeCharacter]!!
        _uiState.update { it.copy(isSpecialActive = true) }
        showCenterBanner(profile.specialName, profile.specialDesc, SpecialGold)
    }

    fun selectGear(type: GearType) {
        _uiState.update { it.copy(selectedGear = type, overlay = OverlayType.NONE) }
    }

    fun useSelectedGear() {
        val state = _uiState.value
        val runtime = state.activeRuntime
        when (state.selectedGear) {
            GearType.NITRO_CELL -> {
                _uiState.update { it.copy(nitroTimer = 2.6f, inVehicle = true) }
                showCenterBanner("NITRO ENGAGED", "Top speed surged!", SpecialGold)
            }
            GearType.EMP_PULSE -> {
                val newWanted = (state.wantedLevel - 1).coerceAtLeast(0)
                _uiState.update {
                    it.copy(
                        wantedLevel = newWanted,
                        patrolCars = if (newWanted == 0) emptyList() else it.patrolCars.dropLast(1)
                    )
                }
                showCenterBanner("EMP PULSE FIRED", "Patrol grid disrupted", AlkalineTeal)
            }
            GearType.ARMOR_PLATE -> {
                updateActiveRuntime { it.copy(armor = (it.armor + 0.5f).coerceAtMost(1f)) }
                showCenterBanner("ARMOR REPLENISHED", "+50% Ballistic Plate Equipped", Color(0xFF4B89DC))
            }
            GearType.CASH_SCANNER -> {
                val uncollected = state.cashDrops.filter { !it.collected }
                val nearby = uncollected.filter {
                    hypot(it.x - state.playerX, it.y - state.playerY) < 650f
                }
                val gain = nearby.sumOf { it.amount }
                if (gain > 0) {
                    val nearbyIds = nearby.map { it.id }.toSet()
                    _uiState.update { s ->
                        s.copy(cashDrops = s.cashDrops.map { if (it.id in nearbyIds) it.copy(collected = true) else it })
                    }
                    awardCash(gain, "CASH PULSE")
                } else {
                    showCenterBanner("SCANNER CLEAR", "No street drops within 650m", CashGreen)
                }
            }
            GearType.DECOY_FLARE -> {
                _uiState.update { it.copy(wantedLevel = 0, patrolCars = emptyList(), wantedEvading = false) }
                showCenterBanner("DECOY DEPLOYED", "Alkaline PD lost your signal", WaypointPurple)
            }
            GearType.OVERCLOCK -> {
                updateActiveRuntime { it.copy(specialMeter = 1f) }
                showCenterBanner("SPECIAL CHARGED", "Ability meter at 100%", VegaEmerald)
            }
            GearType.REPAIR_KIT -> {
                updateActiveRuntime { it.copy(health = 1f, armor = (it.armor + 0.25f).coerceAtMost(1f)) }
                showCenterBanner("CHASSIS RESTORED", "Health 100%", Color(0xFFE67E22))
            }
            GearType.UNARMED -> {
                showCenterBanner("STREET STANCE", "Switch gear via the radial wheel", Color(0xFF94A3B8))
            }
        }
    }

    fun selectRadioStation(stationId: String) {
        _uiState.update { it.copy(activeRadioId = stationId, radioTrackIndex = 0, overlay = OverlayType.NONE) }
        viewModelScope.launch {
            settingsRepo.updateRadioStation(stationId)
        }
    }

    fun setWaypoint(x: Float, y: Float, label: String = "GPS Waypoint") {
        _uiState.update { it.copy(waypoint = Waypoint(x, y, label)) }
        addBrief("GPS Route Set", label, WaypointPurple)
    }

    fun clearWaypoint() {
        _uiState.update { it.copy(waypoint = null) }
    }

    fun launchMission(mission: MissionDef) {
        _uiState.update {
            it.copy(
                activeMission = mission,
                waypoint = Waypoint(mission.targetX, mission.targetY, mission.objectiveText),
                overlay = OverlayType.NONE
            )
        }
        showCenterBanner("MISSION ACTIVE", "${mission.title} — ${mission.objectiveText}", mission.accentColor)
        addBrief("Mission: ${mission.title}", mission.objectiveText, mission.accentColor)
    }

    fun deliverVehicle(vehicle: CityVehicle) {
        updateActiveRuntime { it.copy(vehicleId = vehicle.id) }
        _uiState.update {
            it.copy(
                currentVehicle = vehicle,
                inVehicle = true,
                overlay = OverlayType.NONE
            )
        }
        showCenterBanner("VEHICLE DELIVERED", "${vehicle.name} (${vehicle.category})", VegaEmerald)
        addBrief("Garage Delivery", "Clutch dropped off your ${vehicle.name}.", VegaEmerald)
    }

    fun triggerWantedStar() {
        val current = _uiState.value.wantedLevel
        val next = (current + 1).coerceAtMost(5)
        escalateWantedTo(next)
    }

    fun callContact(contactId: String) {
        val state = _uiState.value
        when (contactId) {
            "oracle" -> {
                if (state.wantedLevel == 0) {
                    appendMessage("Oracle", "Clean slate already—no heat on you right now.")
                } else if (state.activeRuntime.cash >= 1500L) {
                    updateActiveRuntime { it.copy(cash = it.cash - 1500L) }
                    _uiState.update {
                        it.copy(
                            wantedLevel = 0,
                            patrolCars = emptyList(),
                            wantedEvading = false,
                            overlay = OverlayType.NONE
                        )
                    }
                    showCenterBanner("WANTED CLEARED", "Oracle scrubbed the APB (-$1,500)", WaypointPurple)
                } else {
                    appendMessage("Oracle", "Need at least $1,500 to wipe the police grid.")
                }
            }
            "clutch" -> {
                val interceptor = AlkalineCityWorld.vehicles.first { it.id == "interceptor" }
                deliverVehicle(interceptor)
            }
            else -> {
                val prof = AlkalineCityWorld.protagonists.values.find { it.id.name.equals(contactId, ignoreCase = true) }
                if (prof != null) {
                    appendMessage(prof.firstName, "Cruising around ${prof.homeDistrict}. Catch up at the Chrome Ave junction.")
                    setWaypoint(prof.spawnX, prof.spawnY, "${prof.firstName}'s Location")
                }
            }
        }
    }

    fun buyStock(ticker: String) {
        val state = _uiState.value
        val stock = state.stocks.find { it.ticker == ticker } ?: return
        val cost = stock.price.toLong().coerceAtLeast(1L)
        if (state.activeRuntime.cash < cost) return
        updateActiveRuntime { it.copy(cash = it.cash - cost) }
        _uiState.update { s ->
            s.copy(stocks = s.stocks.map {
                if (it.ticker == ticker) it.copy(sharesOwned = it.sharesOwned + 1) else it
            })
        }
    }

    fun sellStock(ticker: String) {
        val state = _uiState.value
        val stock = state.stocks.find { it.ticker == ticker } ?: return
        if (stock.sharesOwned <= 0) return
        val payout = stock.price.toLong().coerceAtLeast(1L)
        updateActiveRuntime { it.copy(cash = it.cash + payout) }
        _uiState.update { s ->
            s.copy(stocks = s.stocks.map {
                if (it.ticker == ticker) it.copy(sharesOwned = it.sharesOwned - 1) else it
            })
        }
    }

    fun takeSnapmatic() {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                snapmaticCount = it.snapmaticCount + 1,
                snapmaticFlash = true
            )
        }
        addBrief("Snapmatic Saved", "Captured ${state.currentDistrict.displayName}", AlkalineTeal)
        viewModelScope.launch {
            delay(260L)
            _uiState.update { it.copy(snapmaticFlash = false) }
        }
    }

    fun setHudOpacity(value: Float) {
        viewModelScope.launch { settingsRepo.updateHudOpacity(value) }
    }

    fun setDistrictBanners(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.updateDistrictBanners(enabled) }
    }

    fun setHaptics(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.updateHaptics(enabled) }
    }

    fun resetSession() {
        _uiState.update {
            AlkalineUiState(
                isSplashVisible = false,
                characterStates = initialCharStates,
                playerX = 772f,
                playerY = 1380f,
                playerAngleDeg = -90f,
                currentVehicle = AlkalineCityWorld.vehicles.first(),
                settings = it.settings,
                activeRadioId = it.settings.radioStationId
            )
        }
        showCenterBanner("SESSION RESET", "Welcome back to Alkaline City", VegaEmerald)
    }

    fun stepSimulation(dt: Float, joyX: Float, joyY: Float, nitroHeld: Boolean, brakeHeld: Boolean) {
        val state = _uiState.value
        if (state.isSplashVisible || state.overlay == OverlayType.PAUSE_MENU) return

        val clampedDt = dt.coerceIn(0.001f, 0.05f)
        if (collisionCooldown > 0f) collisionCooldown -= clampedDt

        val timeScale = if (state.isSpecialActive && state.activeCharacter == ProtagonistId.KAI) 0.55f else 1f
        val worldDt = clampedDt * timeScale

        // 1. Update player movement
        var x = state.playerX
        var y = state.playerY
        var angle = state.playerAngleDeg
        var speed = state.playerSpeed
        val joyMag = hypot(joyX, joyY).coerceIn(0f, 1f)

        val nitroMult = if (state.nitroTimer > 0f || nitroHeld) 1.38f else 1f
        val specialMult = if (state.isSpecialActive && state.activeCharacter == ProtagonistId.VEGA) 1.30f else 1f

        if (state.inVehicle) {
            val maxSpd = state.currentVehicle.topSpeed * nitroMult * specialMult
            val accel = state.currentVehicle.acceleration * nitroMult
            if (brakeHeld) {
                speed = (speed - accel * 1.8f * clampedDt).coerceAtLeast(-110f)
            } else if (joyMag > 0.08f) {
                val targetAngle = Math.toDegrees(atan2(joyY.toDouble(), joyX.toDouble())).toFloat()
                val diff = normalizeAngle(targetAngle - angle)
                val turnRate = 210f * state.currentVehicle.handling * (0.45f + 0.55f * (abs(speed) / maxSpd).coerceIn(0f, 1f))
                val maxStep = turnRate * clampedDt
                angle += diff.coerceIn(-maxStep, maxStep)
                val desiredSpeed = maxSpd * joyMag
                speed = if (speed < desiredSpeed) {
                    (speed + accel * clampedDt).coerceAtMost(desiredSpeed)
                } else {
                    (speed - accel * 0.5f * clampedDt).coerceAtLeast(desiredSpeed)
                }
            } else {
                // Natural rolling drag
                speed *= (1f - 1.8f * clampedDt).coerceAtLeast(0f)
                if (abs(speed) < 4f) speed = 0f
            }
        } else {
            // On foot movement
            val footMax = 145f * (if (nitroHeld) 1.35f else 1f)
            if (joyMag > 0.08f) {
                angle = Math.toDegrees(atan2(joyY.toDouble(), joyX.toDouble())).toFloat()
                speed = footMax * joyMag
            } else {
                speed = 0f
            }
        }

        val rad = Math.toRadians(angle.toDouble())
        var nextX = x + (cos(rad) * speed * clampedDt).toFloat()
        var nextY = y + (sin(rad) * speed * clampedDt).toFloat()

        // World bounds & Ocean boundary (pier allowed between y 1005..1095)
        val onPier = nextY in 1010f..1098f && nextX >= 45f
        val minX = if (onPier) 50f else AlkalineCityWorld.BEACH_EDGE_X
        nextX = nextX.coerceIn(minX, AlkalineCityWorld.WORLD_WIDTH - 40f)
        nextY = nextY.coerceIn(40f, AlkalineCityWorld.WORLD_HEIGHT - 40f)

        // Building AABB collision sliding
        val radius = if (state.inVehicle) 18f else 10f
        var hitBuilding = false
        for (b in AlkalineCityWorld.buildings) {
            val resolved = resolveBuildingCollision(nextX, nextY, radius, b)
            if (resolved.first != nextX || resolved.second != nextY) {
                nextX = resolved.first
                nextY = resolved.second
                hitBuilding = true
            }
        }
        if (hitBuilding && abs(speed) > 150f) {
            speed *= 0.45f
        }

        // 2. Update traffic cars
        val updatedTraffic = state.trafficCars.map { car ->
            var cx = car.x
            var cy = car.y
            if (car.horizontal) {
                cx += car.speed * car.direction * worldDt
                if (cx > AlkalineCityWorld.WORLD_WIDTH - 220f) cx = 360f
                if (cx < 360f) cx = AlkalineCityWorld.WORLD_WIDTH - 220f
            } else {
                cy += car.speed * car.direction * worldDt
                if (cy > AlkalineCityWorld.WORLD_HEIGHT - 160f) cy = 180f
                if (cy < 180f) cy = AlkalineCityWorld.WORLD_HEIGHT - 160f
            }
            car.copy(x = cx, y = cy)
        }

        // Traffic collision check -> damages armor/health slightly & may trigger 1 wanted star!
        var newWanted = state.wantedLevel
        var healthDelta = 0f
        var armorDelta = 0f
        if (collisionCooldown <= 0f) {
            for (car in updatedTraffic) {
                if (hypot(nextX - car.x, nextY - car.y) < 34f && abs(speed) > 140f) {
                    collisionCooldown = 1.4f
                    speed *= -0.25f
                    if (!(state.isSpecialActive && state.activeCharacter == ProtagonistId.DEX)) {
                        if (state.activeRuntime.armor > 0f) armorDelta -= 0.08f else healthDelta -= 0.08f
                    }
                    if (newWanted < 2) {
                        newWanted = (newWanted + 1).coerceAtMost(5)
                    }
                    break
                }
            }
        }

        // 3. Patrol Cars AI
        var updatedPatrols = state.patrolCars
        if (newWanted > 0 && updatedPatrols.size < newWanted.coerceAtMost(4)) {
            val spawnAngle = Random.nextFloat() * 6.28f
            val sx = (nextX + cos(spawnAngle) * 520f).coerceIn(360f, 2100f)
            val sy = (nextY + sin(spawnAngle) * 520f).coerceIn(220f, 2100f)
            updatedPatrols = updatedPatrols + PatrolCar(
                id = (updatedPatrols.maxOfOrNull { it.id } ?: 0) + 1,
                x = sx,
                y = sy,
                angleDeg = 0f,
                speed = 230f + newWanted * 22f
            )
        }

        var nearestPatrolDist = Float.MAX_VALUE
        updatedPatrols = updatedPatrols.map { p ->
            val dx = nextX - p.x
            val dy = nextY - p.y
            val dist = hypot(dx, dy).coerceAtLeast(1f)
            if (dist < nearestPatrolDist) nearestPatrolDist = dist
            val pAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
            val pSpeed = (225f + newWanted * 28f).coerceAtMost(380f)
            var px = p.x + (dx / dist) * pSpeed * worldDt
            var py = p.y + (dy / dist) * pSpeed * worldDt
            for (b in AlkalineCityWorld.buildings) {
                val res = resolveBuildingCollision(px, py, 16f, b)
                px = res.first
                py = res.second
            }
            p.copy(x = px, y = py, angleDeg = pAngle, speed = pSpeed)
        }

        // Evasion logic
        var evading = false
        var evadeProgress = 0f
        if (newWanted > 0) {
            if (nearestPatrolDist > 420f) {
                evading = true
                evadeClock += clampedDt
                evadeProgress = (evadeClock / 5.5f).coerceIn(0f, 1f)
                if (evadeClock >= 5.5f) {
                    newWanted = 0
                    updatedPatrols = emptyList()
                    evadeClock = 0f
                    evading = false
                    evadeProgress = 0f
                    showCenterBanner("WANTED EVADED", "+250 RP — Clean Escape", CashGreen)
                }
            } else {
                evadeClock = (evadeClock - clampedDt * 0.5f).coerceAtLeast(0f)
            }
        } else {
            evadeClock = 0f
        }

        // 4. Cash drop pickups
        var cashEarned = 0L
        val updatedDrops = state.cashDrops.map { drop ->
            if (!drop.collected && hypot(nextX - drop.x, nextY - drop.y) < 36f) {
                cashEarned += drop.amount
                drop.copy(collected = true)
            } else {
                drop
            }
        }

        // 5. Waypoint & Mission target check
        var nextWaypoint = state.waypoint
        if (nextWaypoint != null && hypot(nextX - nextWaypoint.x, nextY - nextWaypoint.y) < 46f) {
            if (state.activeMission == null) {
                nextWaypoint = null
                showCenterBanner("ARRIVED AT WAYPOINT", nextWaypoint?.label ?: "Destination Reached", WaypointPurple)
            }
        }

        var nextMission = state.activeMission
        var completedMissions = state.completedMissionIds
        if (nextMission != null && hypot(nextX - nextMission.targetX, nextY - nextMission.targetY) < 52f) {
            val finished = nextMission
            nextMission = null
            nextWaypoint = null
            completedMissions = completedMissions + finished.id
            cashEarned += finished.rewardCash
            showCenterBanner("MISSION PASSED", "${finished.title}  +$${formatCash(finished.rewardCash)}", SpecialGold)
            addBrief("Mission Passed: ${finished.title}", "Earned $${formatCash(finished.rewardCash)}", SpecialGold)
        } else if (nextMission == null) {
            // Auto-start uncompleted mission if player drives right onto its marker
            for (m in AlkalineCityWorld.missions) {
                if (m.id !in completedMissions && hypot(nextX - m.startX, nextY - m.startY) < 36f) {
                    nextMission = m
                    nextWaypoint = Waypoint(m.targetX, m.targetY, m.objectiveText)
                    showCenterBanner("MISSION STARTED", "${m.title} — ${m.objectiveText}", m.accentColor)
                    break
                }
            }
        }

        // 6. Special & Nitro timers
        val nextNitroTimer = (state.nitroTimer - clampedDt).coerceAtLeast(0f)
        val runtime = state.activeRuntime
        var nextSpecialMeter = runtime.specialMeter
        var nextSpecialActive = state.isSpecialActive
        if (nextSpecialActive) {
            nextSpecialMeter = (nextSpecialMeter - clampedDt * 0.16f).coerceAtLeast(0f)
            if (nextSpecialMeter <= 0f) nextSpecialActive = false
        } else if (abs(speed) > 260f) {
            nextSpecialMeter = (nextSpecialMeter + clampedDt * 0.04f).coerceAtMost(1f)
        }

        val nextDistrict = AlkalineCityWorld.districtFor(nextX, nextY)
        val districtChanged = nextDistrict != state.currentDistrict

        val updatedRuntime = runtime.copy(
            x = nextX,
            y = nextY,
            angleDeg = angle,
            cash = runtime.cash + cashEarned,
            health = (runtime.health + healthDelta).coerceIn(0.12f, 1f),
            armor = (runtime.armor + armorDelta).coerceIn(0f, 1f),
            specialMeter = nextSpecialMeter,
            vehicleId = state.currentVehicle.id
        )

        val updatedMap = state.characterStates.toMutableMap()
        updatedMap[state.activeCharacter] = updatedRuntime

        _uiState.update {
            it.copy(
                characterStates = updatedMap,
                playerX = nextX,
                playerY = nextY,
                playerAngleDeg = angle,
                playerSpeed = speed,
                wantedLevel = newWanted,
                wantedEvading = evading,
                evadeProgress = evadeProgress,
                patrolCars = updatedPatrols,
                trafficCars = updatedTraffic,
                cashDrops = updatedDrops,
                activeMission = nextMission,
                completedMissionIds = completedMissions,
                waypoint = nextWaypoint,
                currentDistrict = nextDistrict,
                isSpecialActive = nextSpecialActive,
                nitroTimer = nextNitroTimer
            )
        }

        if (cashEarned > 0L) {
            showCashDelta(cashEarned)
        }
        if (districtChanged) {
            showDistrictBanner(nextDistrict)
        }
    }

    private fun escalateWantedTo(level: Int) {
        _uiState.update { it.copy(wantedLevel = level, wantedEvading = false) }
        if (level > 0) {
            addBrief("Wanted Level: $level Star${if (level > 1) "s" else ""}", "Alkaline PD units dispatched.", WantedPoliceRed)
        }
    }

    private fun awardCash(amount: Long, label: String) {
        updateActiveRuntime { it.copy(cash = it.cash + amount) }
        showCashDelta(amount)
        showCenterBanner(label, "+$${formatCash(amount)}", CashGreen)
    }

    private fun showCashDelta(amount: Long) {
        cashDeltaVersion++
        val ver = cashDeltaVersion
        _uiState.update { it.copy(cashDeltaText = "+$${formatCash(amount)}") }
        viewModelScope.launch {
            delay(2200L)
            if (cashDeltaVersion == ver) {
                _uiState.update { it.copy(cashDeltaText = null) }
            }
        }
    }

    private fun showDistrictBanner(district: CityDistrict) {
        if (!_uiState.value.settings.showDistrictBanners) return
        districtBannerVersion++
        val ver = districtBannerVersion
        _uiState.update { it.copy(currentDistrict = district, districtBannerVisible = true) }
        viewModelScope.launch {
            delay(2800L)
            if (districtBannerVersion == ver) {
                _uiState.update { it.copy(districtBannerVisible = false) }
            }
        }
    }

    private fun showCenterBanner(title: String, subtitle: String, color: Color) {
        bannerJobVersion++
        val ver = bannerJobVersion
        _uiState.update {
            it.copy(
                centerBannerTitle = title,
                centerBannerSubtitle = subtitle,
                centerBannerColor = color
            )
        }
        viewModelScope.launch {
            delay(2400L)
            if (bannerJobVersion == ver) {
                _uiState.update { it.copy(centerBannerTitle = null, centerBannerSubtitle = null) }
            }
        }
    }

    private fun addBrief(title: String, detail: String, color: Color) {
        briefCounter++
        val entry = BriefEntry(briefCounter, title, detail, color)
        _uiState.update { it.copy(briefLog = (listOf(entry) + it.briefLog).take(20)) }
    }

    private fun appendMessage(sender: String, body: String) {
        val msg = PhoneMessage(
            id = "msg_${System.currentTimeMillis()}",
            sender = sender,
            body = body,
            timestamp = "NOW",
            unread = true
        )
        _uiState.update { it.copy(messages = listOf(msg) + it.messages) }
    }

    private fun updateActiveRuntime(transform: (CharacterRuntimeState) -> CharacterRuntimeState) {
        _uiState.update { state ->
            val updated = transform(state.activeRuntime)
            val map = state.characterStates.toMutableMap()
            map[state.activeCharacter] = updated
            state.copy(characterStates = map)
        }
    }

    private fun nudgeStockMarket() {
        _uiState.update { state ->
            val updated = state.stocks.map { stock ->
                val pct = (Random.nextDouble() - 0.48) * 3.6
                val nextPrice = (stock.price * (1.0 + pct / 100.0)).coerceIn(12.0, 480.0)
                stock.copy(
                    price = (nextPrice * 100).toInt() / 100.0,
                    deltaPercent = ((pct * 10).toInt() / 10.0)
                )
            }
            state.copy(stocks = updated)
        }
    }

    private fun resolveBuildingCollision(x: Float, y: Float, radius: Float, b: CityBuilding): Pair<Float, Float> {
        val closestX = x.coerceIn(b.x, b.x + b.width)
        val closestY = y.coerceIn(b.y, b.y + b.height)
        val dx = x - closestX
        val dy = y - closestY
        val distSq = dx * dx + dy * dy
        if (distSq >= radius * radius) return x to y

        val leftDist = abs(x - b.x)
        val rightDist = abs((b.x + b.width) - x)
        val topDist = abs(y - b.y)
        val bottomDist = abs((b.y + b.height) - y)
        val minEdge = minOf(leftDist, rightDist, topDist, bottomDist)
        return when (minEdge) {
            leftDist -> (b.x - radius) to y
            rightDist -> (b.x + b.width + radius) to y
            topDist -> x to (b.y - radius)
            else -> x to (b.y + b.height + radius)
        }
    }

    private fun normalizeAngle(angle: Float): Float {
        var a = angle % 360f
        if (a > 180f) a -= 360f
        if (a < -180f) a += 360f
        return a
    }

    companion object {
        fun formatCash(amount: Long): String {
            return "%,d".format(amount)
        }
    }
}
