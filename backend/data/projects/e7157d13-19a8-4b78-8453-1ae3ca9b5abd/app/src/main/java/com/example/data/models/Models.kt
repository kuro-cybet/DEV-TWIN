package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farm_fields")
data class FarmField(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val areaAcres: Double,
    val cropType: String,
    val sowingDate: String,
    val stage: String, // "Sowing", "Vegetative", "Flowering", "Grain Filling", "Harvesting"
    val stageProgress: Float, // 0.0 to 1.0
    val soilPh: Double,
    val nitrogenLevel: String, // "Low", "Optimal", "High"
    val phosphorusLevel: String, // "Low", "Optimal", "High"
    val potassiumLevel: String, // "Low", "Optimal", "High"
    val organicCarbonPercent: Double,
    val moisturePercent: Int
)

@Entity(tableName = "crop_scans")
data class CropScan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cropName: String,
    val diseaseName: String,
    val confidencePercent: Int,
    val severity: String, // "Mild", "Moderate", "Severe", "Healthy"
    val symptoms: String,
    val chemicalTreatment: String,
    val organicRemedy: String,
    val dosageGuideline: String,
    val preventativeMeasures: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imagePresetKey: String = "tomato_blight",
    val isResolved: Boolean = false
)

@Entity(tableName = "farm_tasks")
data class FarmTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fieldId: Long = 1,
    val title: String,
    val description: String,
    val category: String, // "Fertilizer", "Irrigation", "Spray", "Harvest", "Soil"
    val dueDate: String,
    val priority: String, // "Urgent", "High", "Normal"
    val isCompleted: Boolean = false,
    val isWeatherDeferred: Boolean = false,
    val weatherNote: String = ""
)

@Entity(tableName = "mandi_commodities")
data class MandiCommodity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val commodity: String,
    val mandiName: String,
    val district: String,
    val state: String,
    val currentPrice: Int, // ₹ per quintal
    val minPrice: Int,
    val maxPrice: Int,
    val priceChange: Int, // e.g. +65 or -30
    val sparklineValues: String, // Comma-separated list of 7 daily prices: "2200,2220,2250,2240,2280,2310,2350"
    val trendSuggestion: String, // "SELL NOW", "HOLD (RISING)", "WAIT & WATCH"
    val trendReason: String,
    val distanceKm: Int
)

@Entity(tableName = "farmer_profile")
data class FarmerProfile(
    @PrimaryKey val id: Long = 1,
    val name: String = "Rajesh Kumar",
    val phone: String = "+91 98765 43210",
    val village: String = "Kisanpur",
    val district: String = "Ludhiana",
    val state: String = "Punjab",
    val totalAcres: Double = 6.5,
    val primaryCrops: String = "Wheat, Cotton, Mustard",
    val languageCode: String = "en", // "en", "hi", "ta", "te", "pa", "mr", "kn", "bn"
    val sustainabilityScore: Int = 84,
    val isOnboarded: Boolean = true
)

@Entity(tableName = "voice_messages")
data class VoiceMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "en",
    val actionLabel: String? = null,
    val actionType: String? = null // "CALENDAR", "SCAN", "MARKET", "WEATHER"
)
