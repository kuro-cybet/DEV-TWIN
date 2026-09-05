package com.example.service

import com.example.data.models.CropScan

object AgroKnowledgeBase {

    data class DiagnosticPreset(
        val key: String,
        val cropName: String,
        val diseaseName: String,
        val confidencePercent: Int,
        val severity: String,
        val symptoms: String,
        val chemicalTreatment: String,
        val organicRemedy: String,
        val dosageGuideline: String,
        val preventativeMeasures: String
    )

    val diagnosticPresets = listOf(
        DiagnosticPreset(
            key = "tomato_blight",
            cropName = "Tomato (टमाटर)",
            diseaseName = "Early Blight (Alternaria solani)",
            confidencePercent = 96,
            severity = "Moderate",
            symptoms = "Concentric dark brown rings on lower leaves with yellow chlorotic halos. Stem dark lesions and premature leaf defoliation.",
            chemicalTreatment = "Mancozeb 75% WP @ 2.5g/L or Azoxystrobin 18.2% + Difenoconazole 11.4% SC @ 1ml/L water.",
            organicRemedy = "Spray 5% Neem Seed Kernel Extract (NSKE) or Copper Oxychloride 50% WP @ 3g/L combined with Trichoderma harzianum soil drenching.",
            dosageGuideline = "Apply 200 Litres spray solution per acre in the morning when dew has dried.",
            preventativeMeasures = "Maintain 60cm plant spacing for adequate aeration, avoid overhead flood splashing, stake plants."
        ),
        DiagnosticPreset(
            key = "cotton_curl",
            cropName = "Cotton (कपास)",
            diseaseName = "Cotton Leaf Curl Virus (CLCuD)",
            confidencePercent = 93,
            severity = "Severe",
            symptoms = "Upward/downward leaf curling, thickening of veins, leaf enations (cup-shaped outgrowth) beneath leaves, stunted bolls.",
            chemicalTreatment = "Control whitefly vector: Diafenthiuron 50% WP @ 1.2g/L or Pyriproxyfen 10% EC @ 2ml/L.",
            organicRemedy = "Neem Oil 10,000 ppm @ 3ml/L water + yellow sticky traps (15 per acre) to trap whitefly vectors.",
            dosageGuideline = "Alternate chemical groups every 12 days to prevent whitefly pesticide resistance.",
            preventativeMeasures = "Uproot alternate weed hosts (Kanghi booti, Peeli booti), grow tolerant hybrids like Bt-RCH."
        ),
        DiagnosticPreset(
            key = "rice_blast",
            cropName = "Paddy / Rice (धान)",
            diseaseName = "Rice Blast (Magnaporthe oryzae)",
            confidencePercent = 91,
            severity = "High",
            symptoms = "Spindle-shaped elliptical lesions with greyish centres and brown margins on leaves; rotting of panicle neck (neck blast).",
            chemicalTreatment = "Tricyclazole 75% WP @ 0.6g/L or Isoprothiolane 40% EC @ 1.5ml/L water.",
            organicRemedy = "Pseudomonas fluorescens @ 10g/L foliar spray + Panchagavya 3% spray at 15-day intervals.",
            dosageGuideline = "Apply first prophylactic spray at active tillering and second spray at boot leaf stage.",
            preventativeMeasures = "Avoid excessive split application of Nitrogen fertilizer; maintain intermittent field drying."
        ),
        DiagnosticPreset(
            key = "wheat_rust",
            cropName = "Wheat (गेहूं)",
            diseaseName = "Yellow Stripe Rust (Puccinia striiformis)",
            confidencePercent = 95,
            severity = "Mild",
            symptoms = "Bright yellow-orange linear powdery pustules arranged in parallel stripes along leaf veins.",
            chemicalTreatment = "Propiconazole 25% EC (Tilt) @ 1ml/L or Tebuconazole 25.9% EC @ 1.25ml/L water.",
            organicRemedy = "Trichoderma viride 1% WP @ 5g/L water + Cow urine 10% solution as anti-fungal spray.",
            dosageGuideline = "Spray 150-200 litres per acre using hollow cone nozzle immediately on first sighting.",
            preventativeMeasures = "Cultivate resistant varieties (HD-3086, DBW-187, PBW-725); avoid late sowing."
        ),
        DiagnosticPreset(
            key = "healthy_crop",
            cropName = "Maize (मक्का)",
            diseaseName = "Healthy Crop - No Pathogen Detected",
            confidencePercent = 98,
            severity = "Healthy",
            symptoms = "Vibrant deep green foliage, robust stalk girth, healthy silk emergence, no visible chlorosis or fungal spots.",
            chemicalTreatment = "No chemical intervention needed. Maintain balanced NPK nutrition.",
            organicRemedy = "Apply Jeevamrutha or Vermicompost tea to strengthen plant immunity.",
            dosageGuideline = "Continue scheduled moisture and micronutrient (Zinc 0.5%) foliar maintenance.",
            preventativeMeasures = "Keep monitoring weekly for Fall Armyworm (FAW) egg masses in central whorl."
        )
    )

    fun calculateFertilizerDose(crop: String, stage: String, nLevel: String, pLevel: String, kLevel: String): String {
        return when (crop.lowercase()) {
            "wheat", "wheat (hd-2967)" -> when (stage) {
                "Sowing" -> "Base: 50 kg DAP + 25 kg MOP + 10 kg Zinc Sulphate (21%) per acre at seed drilling."
                "Vegetative" -> "1st Top Dressing: 45 kg Urea with 1st irrigation (CRI stage, 21 days after sowing)."
                "Flowering", "Grain Filling" -> "Foliar Nutrition: 2% Potassium Nitrate (13-0-45) @ 10g/L to enhance grain weight."
                else -> "Apply balanced 19:19:19 NPK @ 5g/L water during vegetative growth."
            }
            "cotton", "cotton (bt-ii)" -> when (stage) {
                "Vegetative" -> "Apply 35 kg Urea + 10 kg Magnesium Sulphate per acre to prevent leaf reddening."
                "Flowering" -> "Foliar spray: 1% Urea + 1% Potassium Nitrate at peak squaring and flowering stage."
                else -> "Apply 50 kg SSP + 20 kg MOP as basal dose."
            }
            else -> "Apply 40 kg Nitrogen (Urea) + 20 kg Phosphorus (DAP) per acre tailored to soil test values."
        }
    }

    val stateTips = mapOf(
        "Punjab" to "Wheat grain filling in progress. High night-time temperature advisory: ensure light irrigation during windless evenings to protect grain weight from terminal heat.",
        "Maharashtra" to "Late Kharif Onion harvest underway in Nashik. For Rabi crops, monitor gram pod borer with pheromone traps (5 traps/acre).",
        "Haryana" to "Mustard crop in pod development stage. Watch out for white rust and aphid colonies on top twigs; spray Thiamethoxam if ETL crossed.",
        "Uttar Pradesh" to "Sugarcane spring planting window open. Treat setts with Carbendazim 0.1% for 15 mins before trench planting.",
        "Tamil Nadu" to "Samba paddy harvest in Cauvery delta. Prepare field for summer pulse (blackgram) as relay crop.",
        "Andhra Pradesh" to "Chilli crop picking active in Guntur. Control thrips and mites with Spinosad 45% SC @ 0.3ml/L.",
        "Karnataka" to "Rabi Jowar grain maturation stage. Protect crop from bird damage and monitor for charcoal rot.",
        "Gujarat" to "Groundnut summer sowing in Saurashtra. Seed treatment with Trichoderma @ 10g/kg seed is recommended."
    )

    val governmentSchemes = listOf(
        Scheme(
            title = "PM-KUSUM (Solar Agriculture Pumps)",
            subsidy = "Up to 60% Subsidy",
            description = "Get solar pumps (3HP to 7.5HP) installed with 60% government subsidy and 30% bank loan. Reduces grid power dependency and diesel expenses to zero.",
            eligibility = "Individual farmers, water user associations, FPOs with tubewell or open well.",
            howToApply = "Apply online on state renewable energy portal (e.g. PEDA, MEDA) with Khasra/Khatauni & Aadhaar."
        ),
        Scheme(
            title = "PM Krishi Sinchayee Yojana (Micro-Irrigation)",
            subsidy = "55% for Small Farmers, 45% for Others",
            description = "Subsidy on Drip and Sprinkler irrigation systems. Saves 40-50% water and increases crop yield by 25-30%.",
            eligibility = "Farmers with cultivable land and an assured water source.",
            howToApply = "Register on the PMKSY state portal or through the Block Agriculture Horticulture Officer."
        ),
        Scheme(
            title = "Soil Health Card Scheme",
            subsidy = "100% Free Testing",
            description = "Get your soil tested free of charge every 2 years with complete 12-parameter nutrient profile (N, P, K, S, Zn, Fe, Cu, Mn, Bo, pH, EC, OC).",
            eligibility = "All landholding farmers across India.",
            howToApply = "Collect soil samples using V-shape trench method and deposit at nearest KVK or Block Krishi Bhavan."
        ),
        Scheme(
            title = "Paramparagat Krishi Vikas Yojana (PKVY)",
            subsidy = "₹50,000 / hectare over 3 years",
            description = "Financial assistance for organic farming, certification, bio-fertilizers, vermicompost, and cluster-based PGS certification.",
            eligibility = "Farmers forming groups of 20 or more (50-acre cluster).",
            howToApply = "Submit cluster proposal to District Agricultural Officer / ATMA."
        )
    )

    data class Scheme(
        val title: String,
        val subsidy: String,
        val description: String,
        val eligibility: String,
        val howToApply: String
    )
}
