package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.CropScan
import com.example.data.models.FarmField
import com.example.data.models.FarmTask
import com.example.data.models.FarmerProfile
import com.example.data.models.MandiCommodity
import com.example.data.models.VoiceMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FarmField::class,
        CropScan::class,
        FarmTask::class,
        MandiCommodity::class,
        FarmerProfile::class,
        VoiceMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KrishiDatabase : RoomDatabase() {
    abstract fun farmFieldDao(): FarmFieldDao
    abstract fun cropScanDao(): CropScanDao
    abstract fun farmTaskDao(): FarmTaskDao
    abstract fun mandiDao(): MandiDao
    abstract fun farmerProfileDao(): FarmerProfileDao
    abstract fun voiceMessageDao(): VoiceMessageDao

    companion object {
        @Volatile
        private var INSTANCE: KrishiDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): KrishiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KrishiDatabase::class.java,
                    "krishi_mitra_db"
                ).addCallback(DatabaseCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: KrishiDatabase) {
            // Initial Farmer Profile
            database.farmerProfileDao().insertOrUpdate(
                FarmerProfile(
                    id = 1,
                    name = "Rajesh Kumar",
                    phone = "+91 98765 43210",
                    village = "Kisanpur",
                    district = "Ludhiana",
                    state = "Punjab",
                    totalAcres = 7.5,
                    primaryCrops = "Wheat, Cotton, Mustard",
                    languageCode = "en",
                    sustainabilityScore = 86,
                    isOnboarded = true
                )
            )

            // Initial Fields
            database.farmFieldDao().insertAll(
                listOf(
                    FarmField(
                        id = 1,
                        name = "North Canal Plot",
                        areaAcres = 3.5,
                        cropType = "Wheat (HD-2967)",
                        sowingDate = "15 Nov",
                        stage = "Grain Filling",
                        stageProgress = 0.78f,
                        soilPh = 7.1,
                        nitrogenLevel = "Optimal",
                        phosphorusLevel = "High",
                        potassiumLevel = "Optimal",
                        organicCarbonPercent = 0.65,
                        moisturePercent = 42
                    ),
                    FarmField(
                        id = 2,
                        name = "East Well Farm",
                        areaAcres = 2.5,
                        cropType = "Cotton (Bt-II)",
                        sowingDate = "28 Apr",
                        stage = "Vegetative",
                        stageProgress = 0.35f,
                        soilPh = 7.6,
                        nitrogenLevel = "Low",
                        phosphorusLevel = "Optimal",
                        potassiumLevel = "Optimal",
                        organicCarbonPercent = 0.48,
                        moisturePercent = 35
                    ),
                    FarmField(
                        id = 3,
                        name = "Tubewell Orchard",
                        areaAcres = 1.5,
                        cropType = "Mustard (Pusa Bold)",
                        sowingDate = "20 Oct",
                        stage = "Flowering",
                        stageProgress = 0.60f,
                        soilPh = 6.8,
                        nitrogenLevel = "Optimal",
                        phosphorusLevel = "Optimal",
                        potassiumLevel = "High",
                        organicCarbonPercent = 0.72,
                        moisturePercent = 48
                    )
                )
            )

            // Initial Tasks
            database.farmTaskDao().insertAll(
                listOf(
                    FarmTask(
                        id = 1,
                        fieldId = 1,
                        title = "Apply Potassium Foliar Spray",
                        description = "SOP (0-0-50) spray @ 10g/L water during grain filling stage to boost kernel weight",
                        category = "Spray",
                        dueDate = "Tomorrow, 7:00 AM",
                        priority = "High",
                        isCompleted = false,
                        isWeatherDeferred = false,
                        weatherNote = "Optimal window: low wind speed (7 km/h)"
                    ),
                    FarmTask(
                        id = 2,
                        fieldId = 2,
                        title = "Irrigation Cycle - East Well",
                        description = "Deferred due to 80% rain forecast. Re-evaluate moisture after rain.",
                        category = "Irrigation",
                        dueDate = "Thu, 6:00 AM",
                        priority = "Urgent",
                        isCompleted = false,
                        isWeatherDeferred = true,
                        weatherNote = "Heavy rain expected tomorrow: saving ~45,000L water"
                    ),
                    FarmTask(
                        id = 3,
                        fieldId = 3,
                        title = "Aphid Scouting & Yellow Sticky Traps",
                        description = "Install 12 yellow sticky traps per acre to monitor mustard aphid population",
                        category = "Spray",
                        dueDate = "Friday",
                        priority = "Normal",
                        isCompleted = false,
                        isWeatherDeferred = false
                    ),
                    FarmTask(
                        id = 4,
                        fieldId = 1,
                        title = "First Top Dressing with Urea",
                        description = "45 kg Urea applied along irrigation channel",
                        category = "Fertilizer",
                        dueDate = "10 Dec",
                        priority = "Normal",
                        isCompleted = true,
                        isWeatherDeferred = false
                    )
                )
            )

            // Initial Scans
            database.cropScanDao().insertAll(
                listOf(
                    CropScan(
                        id = 1,
                        cropName = "Tomato",
                        diseaseName = "Early Blight (Alternaria solani)",
                        confidencePercent = 94,
                        severity = "Moderate",
                        symptoms = "Concentric brown-black rings with yellow halos on lower leaves and stems.",
                        chemicalTreatment = "Spray Mancozeb 75% WP @ 2.5g/L or Azoxystrobin 23% SC @ 1ml/L water.",
                        organicRemedy = "Spray 5% Neem seed kernel extract (NSKE) or Copper Oxychloride 50% WP @ 3g/L.",
                        dosageGuideline = "500L spray volume per acre in clear morning sunlight.",
                        preventativeMeasures = "Maintain spacing for airflow, prune lower infected foliage, avoid overhead sprinkler watering.",
                        timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 18),
                        imagePresetKey = "tomato_blight",
                        isResolved = false
                    ),
                    CropScan(
                        id = 2,
                        cropName = "Wheat",
                        diseaseName = "Yellow Rust (Puccinia striiformis)",
                        confidencePercent = 91,
                        severity = "Mild",
                        symptoms = "Linear yellow-orange powdery pustules forming stripes on upper leaf surface.",
                        chemicalTreatment = "Propiconazole 25% EC @ 1ml/L or Tebuconazole 25.9% EC @ 1.25ml/L water.",
                        organicRemedy = "Bio-fungicide Trichoderma viride @ 5g/L as preventive foliar spray.",
                        dosageGuideline = "Spray within 48 hours to arrest fungal spore germination.",
                        preventativeMeasures = "Plant resistant cultivars, monitor field border rows regularly during cool humid weather.",
                        timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * 52),
                        imagePresetKey = "wheat_rust",
                        isResolved = true
                    )
                )
            )

            // Initial Mandi APMC Prices
            database.mandiDao().insertAll(
                listOf(
                    MandiCommodity(
                        id = 1,
                        commodity = "Wheat (Sharbati / HD)",
                        mandiName = "Khanna APMC",
                        district = "Ludhiana",
                        state = "Punjab",
                        currentPrice = 2480,
                        minPrice = 2390,
                        maxPrice = 2540,
                        priceChange = 45,
                        sparklineValues = "2380,2400,2420,2410,2450,2465,2480",
                        trendSuggestion = "HOLD (RISING)",
                        trendReason = "Strong procurement demand from southern flour mills; prices expected to gain +₹80/q.",
                        distanceKm = 18
                    ),
                    MandiCommodity(
                        id = 2,
                        commodity = "Cotton (Medium Staple)",
                        mandiName = "Abohar APMC",
                        district = "Fazilka",
                        state = "Punjab",
                        currentPrice = 7350,
                        minPrice = 7100,
                        maxPrice = 7520,
                        priceChange = -60,
                        sparklineValues = "7500,7480,7420,7400,7380,7370,7350",
                        trendSuggestion = "WAIT & WATCH",
                        trendReason = "Global export market is subdued; steady arrivals in northern ginning mills.",
                        distanceKm = 42
                    ),
                    MandiCommodity(
                        id = 3,
                        commodity = "Mustard (Sarson)",
                        mandiName = "Kotkapura APMC",
                        district = "Faridkot",
                        state = "Punjab",
                        currentPrice = 5620,
                        minPrice = 5450,
                        maxPrice = 5750,
                        priceChange = 110,
                        sparklineValues = "5380,5420,5490,5510,5560,5590,5620",
                        trendSuggestion = "SELL NOW",
                        trendReason = "Near seasonal peak high; oil mill crushing demand strong before fresh arrivals.",
                        distanceKm = 28
                    ),
                    MandiCommodity(
                        id = 4,
                        commodity = "Basmati Paddy (PB 1121)",
                        mandiName = "Karnal APMC",
                        district = "Karnal",
                        state = "Haryana",
                        currentPrice = 3850,
                        minPrice = 3680,
                        maxPrice = 4020,
                        priceChange = 75,
                        sparklineValues = "3700,3720,3750,3790,3810,3830,3850",
                        trendSuggestion = "HOLD (RISING)",
                        trendReason = "Export quota expansion and Middle East festive demand lifting mill bids.",
                        distanceKm = 65
                    ),
                    MandiCommodity(
                        id = 5,
                        commodity = "Onion (Nashik Red)",
                        mandiName = "Lasalgaon APMC",
                        district = "Nashik",
                        state = "Maharashtra",
                        currentPrice = 2150,
                        minPrice = 1850,
                        maxPrice = 2320,
                        priceChange = -40,
                        sparklineValues = "2350,2300,2260,2220,2200,2180,2150",
                        trendSuggestion = "WAIT & WATCH",
                        trendReason = "Late Kharif harvest supplies entering wholesale yards.",
                        distanceKm = 1100
                    )
                )
            )

            // Initial Voice Messages
            database.voiceMessageDao().insert(
                VoiceMessage(
                    id = 1,
                    text = "Namaste Rajesh ji! I am KrishiMitra, your AI agricultural companion. How are your Wheat and Cotton crops doing today?",
                    isUser = false,
                    timestamp = System.currentTimeMillis() - 60000,
                    language = "en",
                    actionLabel = "Ask about pest control",
                    actionType = "SCAN"
                )
            )
        }
    }
}
