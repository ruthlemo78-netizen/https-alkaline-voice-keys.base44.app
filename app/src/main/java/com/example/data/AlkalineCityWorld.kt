package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.model.CashDrop
import com.example.model.CharacterStats
import com.example.model.CityBuilding
import com.example.model.CityDistrict
import com.example.model.CityRoad
import com.example.model.CityStock
import com.example.model.CityVehicle
import com.example.model.GearGadget
import com.example.model.GearType
import com.example.model.MissionDef
import com.example.model.PhoneContact
import com.example.model.PhoneMessage
import com.example.model.ProtagonistId
import com.example.model.ProtagonistProfile
import com.example.model.RadioStation
import com.example.model.TrafficCar
import com.example.ui.theme.AlkalineTeal
import com.example.ui.theme.ArmorBlue
import com.example.ui.theme.CashGreen
import com.example.ui.theme.DexAmber
import com.example.ui.theme.KaiCyan
import com.example.ui.theme.MissionGold
import com.example.ui.theme.SpecialGold
import com.example.ui.theme.VegaEmerald
import com.example.ui.theme.WaypointPurple

object AlkalineCityWorld {
    const val WORLD_WIDTH = 2400f
    const val WORLD_HEIGHT = 2400f
    const val BEACH_EDGE_X = 320f
    const val OCEAN_EDGE_X = 210f

    val protagonists: Map<ProtagonistId, ProtagonistProfile> = mapOf(
        ProtagonistId.KAI to ProtagonistProfile(
            id = ProtagonistId.KAI,
            firstName = "KAI",
            fullName = "Kai Vance",
            role = "Master Strategist",
            specialName = "TACTICAL FOCUS",
            specialDesc = "Sharpens handling & slows street chaos",
            homeDistrict = "Downtown Chrome",
            signatureColor = KaiCyan,
            portraitRes = R.drawable.img_char_kai,
            defaultVehicleId = "solaris_gt",
            spawnX = 1180f,
            spawnY = 1040f,
            stats = CharacterStats(
                stamina = 0.72f,
                driving = 0.80f,
                flying = 0.68f,
                stealth = 0.90f,
                hacking = 0.94f,
                special = 0.85f
            )
        ),
        ProtagonistId.VEGA to ProtagonistProfile(
            id = ProtagonistId.VEGA,
            firstName = "VEGA",
            fullName = "Vega Morales",
            role = "Precision Wheelman",
            specialName = "APEX OVERDRIVE",
            specialDesc = "Surges top speed & razor-sharp cornering",
            homeDistrict = "Neon Boulevard",
            signatureColor = VegaEmerald,
            portraitRes = R.drawable.img_char_vega,
            defaultVehicleId = "vapor_gt",
            spawnX = 760f,
            spawnY = 1380f,
            stats = CharacterStats(
                stamina = 0.88f,
                driving = 0.96f,
                flying = 0.62f,
                stealth = 0.74f,
                hacking = 0.70f,
                special = 0.92f
            )
        ),
        ProtagonistId.DEX to ProtagonistProfile(
            id = ProtagonistId.DEX,
            firstName = "DEX",
            fullName = "Dex Mercer",
            role = "Canyon Wildcard",
            specialName = "IRON RAGE",
            specialDesc = "Reinforces chassis & shrugs off collisions",
            homeDistrict = "Alkaline Hills",
            signatureColor = DexAmber,
            portraitRes = R.drawable.img_char_dex,
            defaultVehicleId = "apex_trophy",
            spawnX = 1760f,
            spawnY = 620f,
            stats = CharacterStats(
                stamina = 0.95f,
                driving = 0.82f,
                flying = 0.93f,
                stealth = 0.45f,
                hacking = 0.40f,
                special = 0.90f
            )
        )
    )

    val vehicles: List<CityVehicle> = listOf(
        CityVehicle(
            id = "vapor_gt",
            name = "Vapor GT",
            category = "Super Coupe",
            topSpeed = 480f,
            acceleration = 360f,
            handling = 0.92f,
            bodyColor = Color(0xFF1F8A5B),
            accentColor = Color(0xFF00E5FF),
            length = 54f,
            width = 28f
        ),
        CityVehicle(
            id = "solaris_gt",
            name = "Solaris Executive",
            category = "Sports Sedan",
            topSpeed = 435f,
            acceleration = 320f,
            handling = 0.88f,
            bodyColor = Color(0xFF1C2833),
            accentColor = Color(0xFF3498DB),
            length = 56f,
            width = 29f
        ),
        CityVehicle(
            id = "interceptor",
            name = "Alkaline Interceptor",
            category = "Hyper Interceptor",
            topSpeed = 525f,
            acceleration = 410f,
            handling = 0.95f,
            bodyColor = Color(0xFF11161F),
            accentColor = Color(0xFFB366FF),
            length = 56f,
            width = 27f
        ),
        CityVehicle(
            id = "neon_phantom",
            name = "Neon Phantom",
            category = "Street Moto",
            topSpeed = 505f,
            acceleration = 440f,
            handling = 1.0f,
            bodyColor = Color(0xFF0F2027),
            accentColor = Color(0xFF2ECC71),
            length = 40f,
            width = 16f
        ),
        CityVehicle(
            id = "apex_trophy",
            name = "Apex Trophy",
            category = "Off-Road Baja",
            topSpeed = 420f,
            acceleration = 345f,
            handling = 0.84f,
            bodyColor = Color(0xFFB3541E),
            accentColor = Color(0xFFF5B041),
            length = 58f,
            width = 33f
        )
    )

    val roads: List<CityRoad> = listOf(
        // Major North-South Avenues
        CityRoad(x = 340f, y = 140f, width = 92f, height = 2120f, horizontal = false, lanes = 2), // Coastal Hwy
        CityRoad(x = 720f, y = 140f, width = 104f, height = 2120f, horizontal = false, lanes = 2), // Neon Blvd
        CityRoad(x = 1140f, y = 140f, width = 112f, height = 2120f, horizontal = false, lanes = 2), // Chrome Ave
        CityRoad(x = 1580f, y = 140f, width = 96f, height = 2120f, horizontal = false, lanes = 2), // East Loop
        CityRoad(x = 2020f, y = 220f, width = 88f, height = 1940f, horizontal = false, lanes = 2), // Canyon Pass

        // Major East-West Boulevards
        CityRoad(x = 340f, y = 220f, width = 1768f, height = 88f, horizontal = true, lanes = 2), // Highland Dr
        CityRoad(x = 340f, y = 600f, width = 1768f, height = 96f, horizontal = true, lanes = 2), // Observatory Way
        CityRoad(x = 340f, y = 1000f, width = 1768f, height = 108f, horizontal = true, lanes = 2), // Central Chrome Blvd
        CityRoad(x = 340f, y = 1420f, width = 1768f, height = 96f, horizontal = true, lanes = 2), // Neon Strip
        CityRoad(x = 340f, y = 1860f, width = 1768f, height = 96f, horizontal = true, lanes = 2), // Harbor Express

        // Alkaline Pier Road extending out over the ocean!
        CityRoad(x = 40f, y = 1020f, width = 320f, height = 68f, horizontal = true, lanes = 1)
    )

    val buildings: List<CityBuilding> = listOf(
        // Downtown Chrome Skyscrapers (center block)
        CityBuilding("b1", 860f, 730f, 240f, 230f, 34f, Color(0xFF1F2A38), Color(0xFF2B3A4D), KaiCyan, hasHelipad = true),
        CityBuilding("b2", 1290f, 730f, 250f, 230f, 38f, Color(0xFF1A2433), Color(0xFF263547), AlkalineTeal, hasHelipad = true),
        CityBuilding("b3", 860f, 1140f, 240f, 240f, 30f, Color(0xFF212B3B), Color(0xFF2D3B50), VegaEmerald),
        CityBuilding("b4", 1290f, 1140f, 250f, 240f, 40f, Color(0xFF19222E), Color(0xFF253244), WaypointPurple, hasHelipad = true),

        // Alkaline Pier & Beachfront Towers
        CityBuilding("b5", 460f, 730f, 220f, 220f, 20f, Color(0xFF24303F), Color(0xFF334257), AlkalineTeal),
        CityBuilding("b6", 460f, 1140f, 220f, 240f, 22f, Color(0xFF222E3D), Color(0xFF2E3D52), VegaEmerald),
        CityBuilding("b7", 460f, 1550f, 220f, 250f, 18f, Color(0xFF28323F), Color(0xFF374454), MissionGold),

        // Neon Boulevard Commercial Blocks
        CityBuilding("b8", 860f, 1550f, 240f, 260f, 24f, Color(0xFF1F2633), Color(0xFF2C3547), VegaEmerald),
        CityBuilding("b9", 1290f, 1550f, 250f, 260f, 26f, Color(0xFF1D2532), Color(0xFF2A3548), AlkalineTeal),

        // Alkaline Hills Villas & Observatory
        CityBuilding("b10", 860f, 340f, 240f, 220f, 16f, Color(0xFF2B313C), Color(0xFF3A4250), null),
        CityBuilding("b11", 1290f, 340f, 250f, 220f, 20f, Color(0xFF252D3A), Color(0xFF343F52), KaiCyan),
        CityBuilding("b12", 1710f, 340f, 270f, 220f, 24f, Color(0xFF2D2722), Color(0xFF3F362E), DexAmber, hasHelipad = true),
        CityBuilding("b13", 1710f, 730f, 270f, 230f, 18f, Color(0xFF2A292E), Color(0xFF38373E), DexAmber),

        // Terminal Docks Warehouses & Cargo
        CityBuilding("b14", 1710f, 1140f, 270f, 240f, 16f, Color(0xFF2C2621), Color(0xFF3C342C), DexAmber),
        CityBuilding("b15", 1710f, 1550f, 270f, 260f, 14f, Color(0xFF242A2F), Color(0xFF333B42), MissionGold),
        CityBuilding("b16", 860f, 1990f, 240f, 210f, 15f, Color(0xFF252930), Color(0xFF343A44), null),
        CityBuilding("b17", 1290f, 1990f, 250f, 210f, 16f, Color(0xFF222830), Color(0xFF303945), ArmorBlue),
        CityBuilding("b18", 1710f, 1990f, 270f, 210f, 18f, Color(0xFF2D2520), Color(0xFF3E332B), DexAmber)
    )

    val missions: List<MissionDef> = listOf(
        MissionDef(
            id = "m_pier_run",
            codeLetter = "V",
            title = "Pier Midnight Run",
            giver = "Vega",
            district = CityDistrict.ALKALINE_PIER,
            description = "Thread the coastal highway from Alkaline Pier to the Neon Strip checkpoint before the grid locks down.",
            objectiveText = "Drive to the Neon Strip Dropoff",
            rewardCash = 18500L,
            startX = 240f,
            startY = 1054f,
            targetX = 772f,
            targetY = 1468f,
            accentColor = VegaEmerald
        ),
        MissionDef(
            id = "m_chrome_vault",
            codeLetter = "K",
            title = "Chrome Vault Recon",
            giver = "Kai",
            district = CityDistrict.DOWNTOWN_CHROME,
            description = "Collect encrypted vault keys near Downtown Chrome towers and extract safely to Alkaline Hills.",
            objectiveText = "Extract data to Highland Lookout",
            rewardCash = 35000L,
            startX = 1196f,
            startY = 1054f,
            targetX = 1630f,
            targetY = 264f,
            accentColor = KaiCyan
        ),
        MissionDef(
            id = "m_canyon_drop",
            codeLetter = "D",
            title = "Canyon Air Drop",
            giver = "Dex",
            district = CityDistrict.ALKALINE_HILLS,
            description = "Intercept the supply crate dropped in Alkaline Hills and haul it down to Terminal Docks.",
            objectiveText = "Deliver crate to Terminal Docks",
            rewardCash = 27500L,
            startX = 2064f,
            startY = 648f,
            targetX = 1628f,
            targetY = 1908f,
            accentColor = DexAmber
        ),
        MissionDef(
            id = "m_alkaline_blitz",
            codeLetter = "H",
            title = "The Alkaline Heist",
            giver = "Crew",
            district = CityDistrict.DOWNTOWN_CHROME,
            description = "All-crew armored transport intercept across Chrome Avenue ending at the Pier extraction boat.",
            objectiveText = "Escape to Alkaline Pier Dock",
            rewardCash = 75000L,
            startX = 1196f,
            startY = 1468f,
            targetX = 95f,
            targetY = 1054f,
            accentColor = MissionGold
        )
    )

    val radioStations: List<RadioStation> = listOf(
        RadioStation(
            id = "alkaline_fm",
            name = "Alkaline FM",
            frequency = "104.5",
            genre = "West Coast Electro",
            tracks = listOf("Midnight Coastline — Neon Palms", "Chrome Horizon — Vex", "Sunset Boulevardier — K-Wave"),
            accentColor = AlkalineTeal
        ),
        RadioStation(
            id = "neon_pulse",
            name = "Neon Pulse",
            frequency = "98.1",
            genre = "Synthwave & Drift",
            tracks = listOf("Turbo Overdrive — Nightcallers", "Vapor Trail — Solaris", "808 Pacific — Drift Unit"),
            accentColor = VegaEmerald
        ),
        RadioStation(
            id = "los_alkaline",
            name = "94.7 Los Alkaline",
            frequency = "94.7",
            genre = "Hip-Hop & Street",
            tracks = listOf("Top Down — Vega & Crew", "Chrome Dreams — Westside", "Highland Cash — Street Prophet"),
            accentColor = SpecialGold
        ),
        RadioStation(
            id = "canyon_rock",
            name = "Canyon Rock",
            frequency = "101.3",
            genre = "Desert Alt-Rock",
            tracks = listOf("Dust & Gasoline — The Outlaws", "Highway 99 — Red Mesa", "Iron Engine — Dex Trio"),
            accentColor = DexAmber
        ),
        RadioStation(
            id = "talk_radio",
            name = "Alkaline Talk",
            frequency = "88.9",
            genre = "City Talk & News",
            tracks = listOf("Live: High-Speed Sightings on Chrome Ave", "BAWSAQ Market Surge Report", "Caller Line: Pier Traffic"),
            accentColor = KaiCyan
        ),
        RadioStation(
            id = "radio_off",
            name = "Radio Off",
            frequency = "--.-",
            genre = "Engine & Street Audio",
            tracks = listOf("Ambient Alkaline City"),
            accentColor = Color(0xFF6E7A8A)
        )
    )

    val gearGadgets: List<GearGadget> = listOf(
        GearGadget(GearType.NITRO_CELL, "Nitro Cell", "NITRO", "Instant burst speed boost", SpecialGold),
        GearGadget(GearType.EMP_PULSE, "EMP Pulse", "EMP", "Disables nearby patrol pursuit & drops 1 star", AlkalineTeal),
        GearGadget(GearType.ARMOR_PLATE, "Kevlar Plate", "ARMOR", "Restores +50 tactical armor", ArmorBlue),
        GearGadget(GearType.CASH_SCANNER, "Cash Pulse", "SCAN", "Collects nearby street cash drops", CashGreen),
        GearGadget(GearType.DECOY_FLARE, "Chaff Decoy", "DECOY", "Clears active wanted pursuit", WaypointPurple),
        GearGadget(GearType.OVERCLOCK, "Overclock", "BOOST", "Fills Special Ability bar", VegaEmerald),
        GearGadget(GearType.REPAIR_KIT, "Chassis Kit", "REPAIR", "Restores full health & chassis", DexAmber),
        GearGadget(GearType.UNARMED, "Street Stance", "FREE", "Standard exploration mode", Color(0xFF94A3B8))
    )

    val initialStocks: List<CityStock> = listOf(
        CityStock("ALK", "Alkaline Energy", 142.50, +3.4),
        CityStock("CHR", "Chrome Motors", 88.20, -1.2),
        CityStock("PIER", "Pier Resorts", 54.80, +5.1),
        CityStock("VPR", "Vapor Tech", 214.00, +2.7),
        CityStock("DOC", "Harbor Freight", 39.40, -0.8)
    )

    val phoneContacts: List<PhoneContact> = listOf(
        PhoneContact("oracle", "Oracle (Fixer)", "Clears Wanted Level ($1,500)", WaypointPurple, "CLEAR WANTED"),
        PhoneContact("clutch", "Clutch (Mechanic)", "Delivers Hyper Interceptor", VegaEmerald, "DELIVER RIDE"),
        PhoneContact("kai", "Kai Vance", "Downtown Strategist", KaiCyan, "PING CREW"),
        PhoneContact("vega", "Vega Morales", "Neon Blvd Wheelman", VegaEmerald, "PING CREW"),
        PhoneContact("dex", "Dex Mercer", "Alkaline Hills Wildcard", DexAmber, "PING CREW")
    )

    val initialMessages: List<PhoneMessage> = listOf(
        PhoneMessage(
            id = "msg1",
            sender = "Oracle",
            body = "Welcome to Alkaline City. Keep your radar sharp—patrols escalate fast if you tear past intersections above 350.",
            timestamp = "09:42",
            unread = true
        ),
        PhoneMessage(
            id = "msg2",
            sender = "Clutch",
            body = "Got your Vapor GT tuned in the Garage. Open Pause Menu -> Garage or call me anytime for roadside drop.",
            timestamp = "09:15",
            unread = false
        ),
        PhoneMessage(
            id = "msg3",
            sender = "Vega",
            body = "Pier Midnight Run pays $18,500 clean. Head to the green V marker on the Pier boardwalk when ready.",
            timestamp = "08:50",
            unread = false
        )
    )

    fun initialCashDrops(): List<CashDrop> = listOf(
        CashDrop(1, 386f, 450f, 1500L),
        CashDrop(2, 772f, 820f, 2500L),
        CashDrop(3, 1196f, 648f, 3000L),
        CashDrop(4, 1628f, 1054f, 2000L),
        CashDrop(5, 1196f, 1468f, 3500L),
        CashDrop(6, 772f, 1908f, 2200L),
        CashDrop(7, 160f, 1054f, 4000L),
        CashDrop(8, 2064f, 1200f, 2800L),
        CashDrop(9, 1628f, 264f, 3200L),
        CashDrop(10, 386f, 1680f, 1800L)
    )

    fun initialTraffic(): List<TrafficCar> {
        val colors = listOf(
            Color(0xFF64748B), Color(0xFF94A3B8), Color(0xFF3B82F6),
            Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444)
        )
        return listOf(
            TrafficCar(1, 386f, 500f, 90f, 130f, colors[0], horizontal = false, direction = 1f),
            TrafficCar(2, 772f, 1600f, 270f, 155f, colors[1], horizontal = false, direction = -1f),
            TrafficCar(3, 1196f, 400f, 90f, 145f, colors[2], horizontal = false, direction = 1f),
            TrafficCar(4, 1628f, 1800f, 270f, 140f, colors[3], horizontal = false, direction = -1f),
            TrafficCar(5, 600f, 1054f, 0f, 165f, colors[4], horizontal = true, direction = 1f),
            TrafficCar(6, 1500f, 648f, 180f, 150f, colors[5], horizontal = true, direction = -1f),
            TrafficCar(7, 900f, 1468f, 0f, 175f, colors[2], horizontal = true, direction = 1f),
            TrafficCar(8, 1700f, 1908f, 180f, 135f, colors[0], horizontal = true, direction = -1f)
        )
    }

    fun districtFor(x: Float, y: Float): CityDistrict {
        return when {
            x < 460f -> CityDistrict.ALKALINE_PIER
            y < 700f && x > 1050f -> CityDistrict.ALKALINE_HILLS
            x > 1450f && y > 1100f -> CityDistrict.INDUSTRIAL_DOCKS
            x in 800f..1560f && y in 650f..1450f -> CityDistrict.DOWNTOWN_CHROME
            else -> CityDistrict.NEON_BOULEVARD
        }
    }
}
