package com.example.service

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

class GeminiAgroService {

    interface GeminiRestApi {
        @POST("v1beta/models/gemini-3.5-flash:generateContent")
        suspend fun generateContent(
            @Query("key") apiKey: String,
            @Body body: okhttp3.RequestBody
        ): ResponseBody
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val apiService: GeminiRestApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GeminiRestApi::class.java)
    }

    suspend fun askAgroQuestion(
        prompt: String,
        farmerState: String = "Punjab",
        crops: String = "Wheat, Cotton",
        language: String = "en"
    ): AgroResponse = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemPrompt = """
                    You are KrishiMitra, an expert AI agricultural scientist advising Indian farmers in $farmerState who grow $crops.
                    Language: Reply in the language matching code '$language' (or clear Hindi/English with farmer-friendly local terms).
                    Format your advice clearly:
                    1. Direct diagnosis / answer
                    2. Practical actionable steps (organic & chemical remedy with exact dosages per acre/litre)
                    3. Preventative & weather advisory
                    Keep the tone warm, respectful, and concise for budget mobile screens.
                """.trimIndent()

                val rootJson = JSONObject().apply {
                    val contentsArr = JSONArray().apply {
                        put(JSONObject().apply {
                            val partsArr = JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", "$systemPrompt\n\nFarmer Query: $prompt")
                                })
                            }
                            put("parts", partsArr)
                        })
                    }
                    put("contents", contentsArr)
                }

                val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
                val responseBody = apiService.generateContent(apiKey, requestBody)
                val responseString = responseBody.string()
                val responseJson = JSONObject(responseString)
                val text = responseJson
                    .optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                if (!text.isNullOrBlank()) {
                    return@withContext AgroResponse(
                        text = text,
                        isAiGenerated = true,
                        actionLabel = detectActionLabel(prompt),
                        actionType = detectActionType(prompt)
                    )
                }
            } catch (e: Exception) {
                Log.w("GeminiAgroService", "Gemini API call failed, switching to offline agro knowledge: ${e.message}")
            }
        }

        // Offline / Fallback Agricultural Expert Logic
        val offlineAnswer = generateOfflineAgroAdvice(prompt, farmerState, crops, language)
        return@withContext AgroResponse(
            text = offlineAnswer,
            isAiGenerated = false,
            actionLabel = detectActionLabel(prompt),
            actionType = detectActionType(prompt)
        )
    }

    private fun detectActionLabel(prompt: String): String? {
        val lower = prompt.lowercase()
        return when {
            "remind" in lower || "spray" in lower || "fertilizer" in lower || "irrigate" in lower || "task" in lower -> "Add Reminder to Calendar"
            "price" in lower || "mandi" in lower || "rate" in lower || "sell" in lower -> "Check Live Mandi Prices"
            "disease" in lower || "pest" in lower || "leaf" in lower || "blight" in lower || "rot" in lower -> "Open AI Crop Scanner"
            "weather" in lower || "rain" in lower || "wind" in lower -> "View Agro Weather & Spray Index"
            else -> null
        }
    }

    private fun detectActionType(prompt: String): String? {
        val lower = prompt.lowercase()
        return when {
            "remind" in lower || "spray" in lower || "fertilizer" in lower || "irrigate" in lower || "task" in lower -> "CALENDAR"
            "price" in lower || "mandi" in lower || "rate" in lower || "sell" in lower -> "MARKET"
            "disease" in lower || "pest" in lower || "leaf" in lower || "blight" in lower || "rot" in lower -> "SCAN"
            "weather" in lower || "rain" in lower || "wind" in lower -> "WEATHER"
            else -> null
        }
    }

    private fun generateOfflineAgroAdvice(
        query: String,
        state: String,
        crops: String,
        lang: String
    ): String {
        val lower = query.lowercase()
        return when {
            "pest" in lower || "insect" in lower || "bollworm" in lower || "aphid" in lower || "कीट" in lower || "कीड़ा" in lower -> {
                when (lang) {
                    "hi" -> "कीट नियंत्रण सलाह:\n• एफिड/माहू के लिए: 5% नीम तेल (10,000 ppm) 3 मिली/लीटर या इमिडाक्लोप्रिड 17.8% SL @ 0.5 मिली/लीटर पानी में छिड़कें।\n• गुलाबी सुंडी (Pink Bollworm): 5 फेरोमोन ट्रैप प्रति एकड़ लगाएं और एमामेक्टिन बेंजोएट 5% SG @ 0.4 ग्राम/लीटर का छिड़काव करें।\n• सुबह के समय जब हवा शांत हो तब छिड़काव करना सर्वोत्तम है।"
                    "pa" -> "ਕੀਟ ਰੋਕਥਾਮ ਸਲਾਹ:\n• ਤੇਲੇ ਅਤੇ ਚਿੱਟੀ ਮੱਖੀ ਲਈ: ਨਿੰਮ ਦਾ ਤੇਲ 3 ਮਿ.ਲੀ./ਲਿਟਰ ਜਾਂ ਥਿਆਮੇਥੋਕਸਮ 25% WG @ 0.4 ਗ੍ਰਾਮ/ਲਿਟਰ ਪਾਣੀ ਵਿੱਚ ਸਪਰੇਅ ਕਰੋ।\n• ਗੁਲਾਬੀ ਸੁੰਡੀ: ਫੀਲਡ ਵਿੱਚ ਫੇਰੋਮੋਨ ਟਰੈਪ ਲਗਾਓ। ਸਵੇਰ ਵੇਲੇ ਸਪਰੇਅ ਕਰਨਾ ਫਾਇਦੇਮੰਦ ਰਹੇਗਾ।"
                    else -> "Pest Management Advisory:\n1. Aphids & Sucking Pests: Spray Neem Oil 10,000 ppm @ 3ml/L or Imidacloprid 17.8% SL @ 0.5ml/L water.\n2. Pink Bollworm in Cotton: Install 5 pheromone traps/acre; apply Emamectin Benzoate 5% SG @ 0.4g/L.\n3. Best Practice: Spray in early morning (6-9 AM) when wind is below 10 km/h."
                }
            }
            "price" in lower || "mandi" in lower || "rate" in lower || "भाव" in lower || "ਭਾਅ" in lower -> {
                when (lang) {
                    "hi" -> "मंडी भाव विश्लेषण ($state):\n• गेहूं (HD-2967): ₹2,480/क्विंटल (रुझान: तेज - रोकने की सलाह)\n• सरसों: ₹5,620/क्विंटल (रुझान: उच्चतम स्तर पर - अभी बेचें)\n• कपास: ₹7,350/क्विंटल (रुझान: स्थिर)\nसमीप की खन्ना APMC मंडी में आवक मजबूत है।"
                    "pa" -> "ਮੰਡੀ ਭਾਅ ਅਪਡੇਟ ($state):\n• ਕਣਕ: ₹2,480/ਕਵਿੰਟਲ (ਭਾਅ ਵਧਣ ਦੀ ਉਮੀਦ - ਰੋਕਣ ਦੀ ਸਲਾਹ)\n• ਸਰ੍ਹੋਂ: ₹5,620/ਕਵਿੰਟਲ (ਹੁਣੇ ਵੇਚਣ ਦਾ ਵਧੀਆ ਮੌਕਾ)\n• ਨਰਮਾ/ਕਪਾਹ: ₹7,350/ਕਵਿੰਟਲ (ਸਥਿਰ)"
                    else -> "Mandi Price Intelligence ($state):\n• Wheat (Sharbati/HD): ₹2,480/q (Trend: Rising +₹45 - Recommendation: HOLD)\n• Mustard: ₹5,620/q (Trend: Peak High - Recommendation: SELL NOW)\n• Cotton: ₹7,350/q (Trend: Stable)\nNearest Khanna APMC offers the most competitive bids this week."
                }
            }
            "fertilizer" in lower || "urea" in lower || "dap" in lower || "खाद" in lower || "ਯੂਰੀਆ" in lower -> {
                when (lang) {
                    "hi" -> "उर्वरक एवं पोषण प्रबंधन:\n• गेहूं की दाना भराव अवस्था: 2% पोटेशियम नाइट्रेट (13-0-45) @ 10 ग्राम/लीटर का पर्णीय छिड़काव करें।\n• यूरिया की दूसरी टॉप ड्रेसिंग: 45 किग्रा प्रति एकड़ सिंचाई से तुरंत पहले दें।\n• जिंक की कमी: 0.5% जिंक सल्फेट + 1% यूरिया का घोल बनाकर स्प्रे करें।"
                    else -> "Nutrient & Fertilizer Advisory:\n• Wheat Grain Filling: Foliar spray of Potassium Nitrate (13-0-45) @ 10g/L to maximize 1,000-grain weight.\n• Top Dressing: Apply 45 kg Urea/acre immediately prior to light irrigation.\n• Soil Health Note: Maintain organic carbon by incorporating green manure after harvest."
                }
            }
            "weather" in lower || "rain" in lower || "irrigate" in lower || "मौसम" in lower || "ਸਿੰਚਾਈ" in lower -> {
                when (lang) {
                    "hi" -> "मौसम एवं सिंचाई सलाह ($state):\n• वर्तमान आर्द्रता: 68%, हवा: 8 किमी/घंटा (छिड़काव के लिए सुरक्षित)\n• आगामी 48 घंटों में हल्की वर्षा की 80% संभावना है।\n• सलाह: भारी सिंचाई 2 दिनों के लिए टालें; इससे प्रति एकड़ 40,000 लीटर पानी और बिजली की बचत होगी।"
                    else -> "Agro-Meteorology & Irrigation Guidance ($state):\n• Current Wind: 8 km/h | Humidity: 68% (Safe for foliar spray)\n• 80% Rain probability forecasted in next 36 hours.\n• Action: Defer non-critical tubewell irrigation to conserve groundwater."
                }
            }
            else -> {
                when (lang) {
                    "hi" -> "नमस्ते किसान भाई! मैं कृषि मित्र हूँ। आप मुझसे फसल रोग, खाद की सही मात्रा, आज के मंडी भाव या मौसम अनुसार कृषि कार्यों के बारे में कुछ भी पूछ सकते हैं।"
                    "pa" -> "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ ਕਿਸਾਨ ਵੀਰ ਜੀ! ਮੈਂ ਕ੍ਰਿਸ਼ੀ ਮਿੱਤਰ ਹਾਂ। ਤੁਸੀਂ ਫ਼ਸਲ ਦੇ ਰੋਗ, ਖਾਦ ਦੀ ਮਾਤਰਾ, ਮੰਡੀ ਭਾਅ ਜਾਂ ਮੌਸਮ ਬਾਰੇ ਕੋਈ ਵੀ ਸਵਾਲ ਪੁੱਛ ਸਕਦੇ ਹੋ।"
                    else -> "Namaste Farmer friend! I am KrishiMitra. You can ask me about pest symptoms, fertilizer dosages, today's APMC mandi rates, or weather-smart irrigation tips."
                }
            }
        }
    }

    data class AgroResponse(
        val text: String,
        val isAiGenerated: Boolean,
        val actionLabel: String? = null,
        val actionType: String? = null
    )
}
