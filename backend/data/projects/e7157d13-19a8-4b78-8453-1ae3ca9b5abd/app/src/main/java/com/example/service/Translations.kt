package com.example.service

object Translations {
    data class LangOption(val code: String, val name: String, val nativeName: String)

    val supportedLanguages = listOf(
        LangOption("en", "English", "English"),
        LangOption("hi", "Hindi", "हिन्दी"),
        LangOption("pa", "Punjabi", "ਪੰਜਾਬੀ"),
        LangOption("mr", "Marathi", "मराठी"),
        LangOption("te", "Telugu", "తెలుగు"),
        LangOption("ta", "Tamil", "தமிழ்"),
        LangOption("kn", "Kannada", "ಕನ್ನಡ"),
        LangOption("bn", "Bengali", "বাংলা")
    )

    private val translations = mapOf(
        "app_title" to mapOf(
            "en" to "KrishiMitra",
            "hi" to "कृषि मित्र",
            "pa" to "ਕ੍ਰਿਸ਼ੀ ਮਿੱਤਰ",
            "mr" to "कृषी मित्र",
            "te" to "కృషి మిత్ర",
            "ta" to "கிருஷி மித்ரா",
            "kn" to "ಕೃಷಿ ಮಿತ್ರ",
            "bn" to "কৃষি মিত্র"
        ),
        "greeting_morning" to mapOf(
            "en" to "Good Morning",
            "hi" to "शुभ प्रभात",
            "pa" to "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ",
            "mr" to "शुभ प्रभात",
            "te" to "శుభోదయం",
            "ta" to "காலை வணக்கம்",
            "kn" to "ಶುಭೋದಯ",
            "bn" to "সুপ্রভাত"
        ),
        "nav_home" to mapOf(
            "en" to "Home",
            "hi" to "होम",
            "pa" to "ਘਰ",
            "mr" to "मुख्य",
            "te" to "హోమ్",
            "ta" to "முகப்பு",
            "kn" to "ಮುಖಪುಟ",
            "bn" to "হোম"
        ),
        "nav_voice" to mapOf(
            "en" to "Voice",
            "hi" to "आवाज़",
            "pa" to "ਅਵਾਜ਼",
            "mr" to "आवाज",
            "te" to "వాయిస్",
            "ta" to "குரல்",
            "kn" to "ಧ್ವನಿ",
            "bn" to "ভয়েস"
        ),
        "nav_scan" to mapOf(
            "en" to "Scan",
            "hi" to "स्कैन",
            "pa" to "ਸਕੈਨ",
            "mr" to "स्कॅन",
            "te" to "స్కాన్",
            "ta" to "ஸ்கேன்",
            "kn" to "ಸ್ಕ್ಯಾನ್",
            "bn" to "স্ক্যান"
        ),
        "nav_crop_soil" to mapOf(
            "en" to "Crop & Soil",
            "hi" to "फसल व मिट्टी",
            "pa" to "ਫ਼ਸਲ ਅਤੇ ਮਿੱਟੀ",
            "mr" to "पीक व माती",
            "te" to "పంట & మట్టి",
            "ta" to "பயிர் & மண்",
            "kn" to "ಬೆಳೆ & ಮಣ್ಣು",
            "bn" to "ফসল ও মাটি"
        ),
        "nav_more" to mapOf(
            "en" to "More",
            "hi" to "अन्य",
            "pa" to "ਹੋਰ",
            "mr" to "अधिक",
            "te" to "మరిన్ని",
            "ta" to "மேலும்",
            "kn" to "ಹೆಚ್ಚು",
            "bn" to "আরও"
        ),
        "quick_voice" to mapOf(
            "en" to "Voice Assistant",
            "hi" to "आवाज़ सहायक",
            "pa" to "ਵੌਇਸ ਸਹਾਇਕ",
            "mr" to "व्हॉइस असिस्टंट",
            "te" to "వాయిస్ అసిస్టెంట్",
            "ta" to "குரல் உதவியாளர்",
            "kn" to "ಧ್ವನಿ ಸಹಾಯಕ",
            "bn" to "ভয়েস সহকারী"
        ),
        "quick_scan" to mapOf(
            "en" to "AI Crop Doctor",
            "hi" to "एआई फसल डॉक्टर",
            "pa" to "AI ਫ਼ਸਲ ਡਾਕਟਰ",
            "mr" to "एआय पीक डॉक्टर",
            "te" to "AI పంట డాక్టర్",
            "ta" to "AI பயிர் மருத்துவர்",
            "kn" to "AI ಬೆಳೆ ವೈದ್ಯ",
            "bn" to "এআই ফসল ডাক্তার"
        ),
        "quick_weather" to mapOf(
            "en" to "Agro Weather",
            "hi" to "कृषि मौसम",
            "pa" to "ਖੇਤੀ ਮੌਸਮ",
            "mr" to "कृषी हवामान",
            "te" to "వ్యవసాయ వాతావరణం",
            "ta" to "வேளாண் வானிலை",
            "kn" to "ಕೃಷಿ ಹವಾಮಾನ",
            "bn" to "কৃষি আবহাওয়া"
        ),
        "quick_market" to mapOf(
            "en" to "Mandi Prices",
            "hi" to "मंडी भाव",
            "pa" to "ਮੰਡੀ ਦੇ ਭਾਅ",
            "mr" to "बाजार भाव",
            "te" to "మార్కెట్ ధరలు",
            "ta" to "சந்தை விலைகள்",
            "kn" to "ಮಾರುಕಟ್ಟೆ ದರಗಳು",
            "bn" to "মণ্ডির দর"
        ),
        "spray_safe" to mapOf(
            "en" to "Safe for Spraying",
            "hi" to "छिड़काव के लिए सुरक्षित",
            "pa" to "ਸਪਰੇਅ ਲਈ ਸੁਰੱਖਿਅਤ",
            "mr" to "फवारणीसाठी सुरक्षित",
            "te" to "స్ప్రే చేయడానికి సురక్షితం",
            "ta" to "தெளிக்க பாதுகாப்பானது",
            "kn" to "ಸಿಂಪಡಿಸಲು ಸೂಕ್ತ",
            "bn" to "স্প্রে করার জন্য নিরাপদ"
        ),
        "spray_unsafe" to mapOf(
            "en" to "Unsafe for Spraying (Wind/Rain)",
            "hi" to "छिड़काव के लिए असुरक्षित (हवा/बारिश)",
            "pa" to "ਸਪਰੇਅ ਲਈ ਅਸੁਰੱਖਿਅਤ",
            "mr" to "फवारणीसाठी असुरक्षित",
            "te" to "స్ప్రేకి సురక్షితం కాదు",
            "ta" to "தெளிக்க பாதுகாப்பற்றது",
            "kn" to "ಸಿಂಪಡಿಸಲು ಸೂಕ್ತವಲ್ಲ",
            "bn" to "স্প্রে করার পক্ষে অনুপযুক্ত"
        ),
        "water_saved" to mapOf(
            "en" to "Water Saved",
            "hi" to "बचाया गया पानी",
            "pa" to "ਬਚਾਇਆ ਪਾਣੀ",
            "mr" to "पाण्याची बचत",
            "te" to "ఆదా చేసిన నీరు",
            "ta" to "சேமிக்கப்பட்ட நீர்",
            "kn" to "ಉಳಿಸಿದ ನೀರು",
            "bn" to "সংরক্ষিত জল"
        ),
        "sustainability_score" to mapOf(
            "en" to "Sustainability Score",
            "hi" to "सतत कृषि स्कोर",
            "pa" to "ਟਿਕਾਊ ਖੇਤੀ ਸਕੋਰ",
            "mr" to "शाश्वत शेती स्कोअर",
            "te" to "సుస్థిరత స్కోర్",
            "ta" to "நிலையான வேளாண்மை மதிப்பீடு",
            "kn" to "ಸುಸ್ಥಿರ ಕೃಷಿ ಅಂಕ",
            "bn" to "টেকসই স্কোর"
        ),
        "hold_rising" to mapOf(
            "en" to "HOLD (RISING)",
            "hi" to "रोकें (दाम बढ़ेंगे)",
            "pa" to "ਰੋਕੋ (ਭਾਅ ਵਧੇਗਾ)",
            "mr" to "थांबा (भाव वाढेल)",
            "te" to "వేచి చూడండి (ధర పెరుగుతుంది)",
            "ta" to "பொறுத்திருங்கள் (விலை ஏறும்)",
            "kn" to "ಕಾಯಿರಿ (ದರ ಏರಿಕೆ)",
            "bn" to "ধরে রাখুন (দাম বাড়বে)"
        ),
        "sell_now" to mapOf(
            "en" to "SELL NOW",
            "hi" to "अभी बेचें (उच्चतम भाव)",
            "pa" to "ਹੁਣੇ ਵੇਚੋ",
            "mr" to "आता विका",
            "te" to "ఇప్పుడే అమ్మండి",
            "ta" to "இப்போதே விற்கவும்",
            "kn" to "ಈಗಲೇ ಮಾರಿ",
            "bn" to "এখনই বিক্রি করুন"
        ),
        "offline_indicator" to mapOf(
            "en" to "Offline Mode Active (Cached Data)",
            "hi" to "ऑफलाइन मोड सक्रिय (कैश डेटा)",
            "pa" to "ਆਫਲਾਈਨ ਮੋਡ ਚਾਲੂ ਹੈ",
            "mr" to "ऑफलाइन मोड सक्रिय",
            "te" to "ఆఫ్‌లైన్ మోడ్ సక్రియంగా ఉంది",
            "ta" to "ஆஃப்லைன் பயன்முறை",
            "kn" to "ಆಫ್‌ಲೈನ್ ಮೋಡ್ ಸಕ್ರಿಯ",
            "bn" to "অফলাইন মোড সক্রিয়"
        )
    )

    fun get(key: String, lang: String = "en"): String {
        return translations[key]?.get(lang)
            ?: translations[key]?.get("en")
            ?: key
    }
}
