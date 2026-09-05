package com.example.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.KrishiDatabase
import com.example.data.models.CropScan
import com.example.data.models.FarmField
import com.example.data.models.FarmTask
import com.example.data.models.FarmerProfile
import com.example.data.models.MandiCommodity
import com.example.data.models.VoiceMessage
import com.example.data.repository.KrishiRepository
import com.example.service.AgroKnowledgeBase
import com.example.service.GeminiAgroService
import com.example.service.Translations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

sealed class AppDestination {
    object Home : AppDestination()
    object Voice : AppDestination()
    object Scan : AppDestination()
    object CropSoil : AppDestination()
    object Calendar : AppDestination()
    object Sustainability : AppDestination()
    object Weather : AppDestination()
    object Market : AppDestination()
    object Profile : AppDestination()
    object Onboarding : AppDestination()
}

class KrishiViewModel(application: Application) : AndroidViewModel(application) {

    private val database = KrishiDatabase.getInstance(application, viewModelScope)
    private val repository = KrishiRepository(database)
    private val geminiService = GeminiAgroService()

    private var tts: TextToSpeech? = null
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady = _isTtsReady.asStateFlow()

    init {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ENGLISH
                _isTtsReady.value = true
            }
        }
    }

    // Navigation Destination State
    private val _currentDestination = MutableStateFlow<AppDestination>(AppDestination.Home)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    // More Sheet visibility
    private val _isMoreSheetOpen = MutableStateFlow(false)
    val isMoreSheetOpen: StateFlow<Boolean> = _isMoreSheetOpen.asStateFlow()

    // Language Selector Dialog
    private val _isLanguageDialogOpen = MutableStateFlow(false)
    val isLanguageDialogOpen: StateFlow<Boolean> = _isLanguageDialogOpen.asStateFlow()

    // Offline / Sync Status
    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("Just now")
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    private val _offlineQueueCount = MutableStateFlow(0)
    val offlineQueueCount: StateFlow<Int> = _offlineQueueCount.asStateFlow()

    // Active Field for Soil & Crop details
    private val _selectedFieldId = MutableStateFlow<Long>(1)
    val selectedFieldId: StateFlow<Long> = _selectedFieldId.asStateFlow()

    // Active Diagnosis result for Scan Screen
    private val _activeDiagnosis = MutableStateFlow<CropScan?>(null)
    val activeDiagnosis: StateFlow<CropScan?> = _activeDiagnosis.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // Voice Chat states
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Selected Mandi filter
    private val _selectedMandiName = MutableStateFlow<String>("All Mandis")
    val selectedMandiName: StateFlow<String> = _selectedMandiName.asStateFlow()

    // Data streams from Room
    val fields: StateFlow<List<FarmField>> = repository.allFields
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scans: StateFlow<List<CropScan>> = repository.allScans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<FarmTask>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val commodities: StateFlow<List<MandiCommodity>> = repository.allCommodities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profile: StateFlow<FarmerProfile?> = repository.profile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val voiceMessages: StateFlow<List<VoiceMessage>> = repository.voiceMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(dest: AppDestination) {
        _currentDestination.value = dest
        _isMoreSheetOpen.value = false
    }

    fun setMoreSheetOpen(open: Boolean) {
        _isMoreSheetOpen.value = open
    }

    fun setLanguageDialogOpen(open: Boolean) {
        _isLanguageDialogOpen.value = open
    }

    fun selectField(fieldId: Long) {
        _selectedFieldId.value = fieldId
    }

    fun selectMandi(mandiName: String) {
        _selectedMandiName.value = mandiName
    }

    fun toggleOfflineMode() {
        _isOffline.value = !_isOffline.value
    }

    fun forceSync() {
        viewModelScope.launch {
            _lastSyncTime.value = "Synced 1m ago"
            _offlineQueueCount.value = 0
        }
    }

    fun setLanguage(langCode: String) {
        viewModelScope.launch {
            val current = profile.value ?: FarmerProfile()
            repository.updateProfile(current.copy(languageCode = langCode))
            _isLanguageDialogOpen.value = false
        }
    }

    fun completeOnboarding(state: String, crops: String, languageCode: String) {
        viewModelScope.launch {
            val current = profile.value ?: FarmerProfile()
            repository.updateProfile(
                current.copy(
                    state = state,
                    primaryCrops = crops,
                    languageCode = languageCode,
                    isOnboarded = true
                )
            )
            _currentDestination.value = AppDestination.Home
        }
    }

    // Voice queries & Gemini AI
    fun sendVoiceQuery(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            val lang = profile.value?.languageCode ?: "en"
            val state = profile.value?.state ?: "Punjab"
            val crops = profile.value?.primaryCrops ?: "Wheat, Cotton"

            // Save user message
            repository.addVoiceMessage(
                VoiceMessage(
                    text = query,
                    isUser = true,
                    language = lang
                )
            )

            _isAiThinking.value = true

            val response = geminiService.askAgroQuestion(
                prompt = query,
                farmerState = state,
                crops = crops,
                language = lang
            )

            // Save AI reply
            repository.addVoiceMessage(
                VoiceMessage(
                    text = response.text,
                    isUser = false,
                    language = lang,
                    actionLabel = response.actionLabel,
                    actionType = response.actionType
                )
            )

            _isAiThinking.value = false

            // Optional speech readout
            speakText(response.text)
        }
    }

    fun toggleListening() {
        _isListening.value = !_isListening.value
    }

    fun clearVoiceHistory() {
        viewModelScope.launch {
            repository.clearVoiceHistory()
        }
    }

    fun speakText(text: String) {
        if (_isTtsReady.value) {
            try {
                tts?.speak(text.take(200), TextToSpeech.QUEUE_FLUSH, null, "UtteranceId")
            } catch (e: Exception) {
                // Handled gracefully
            }
        }
    }

    // Crop Diagnostic Scanner
    fun performScan(presetKey: String = "tomato_blight") {
        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.value = true
            kotlinx.coroutines.delay(1200) // Realistic AI inference delay

            val preset = AgroKnowledgeBase.diagnosticPresets.find { it.key == presetKey }
                ?: AgroKnowledgeBase.diagnosticPresets.first()

            val scan = CropScan(
                cropName = preset.cropName,
                diseaseName = preset.diseaseName,
                confidencePercent = preset.confidencePercent,
                severity = preset.severity,
                symptoms = preset.symptoms,
                chemicalTreatment = preset.chemicalTreatment,
                organicRemedy = preset.organicRemedy,
                dosageGuideline = preset.dosageGuideline,
                preventativeMeasures = preset.preventativeMeasures,
                imagePresetKey = preset.key,
                timestamp = System.currentTimeMillis()
            )

            val id = repository.addScan(scan)
            _activeDiagnosis.value = scan.copy(id = id)
            _isScanning.value = false
        }
    }

    fun clearActiveDiagnosis() {
        _activeDiagnosis.value = null
    }

    fun markScanResolved(scanId: Long, resolved: Boolean) {
        viewModelScope.launch {
            repository.setScanResolved(scanId, resolved)
        }
    }

    // Task Management
    fun toggleTaskCompletion(taskId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleTask(taskId, !currentStatus)
        }
    }

    fun addTask(title: String, description: String, category: String, dueDate: String, priority: String) {
        viewModelScope.launch {
            repository.addTask(
                FarmTask(
                    title = title,
                    description = description,
                    category = category,
                    dueDate = dueDate,
                    priority = priority,
                    fieldId = _selectedFieldId.value
                )
            )
        }
    }

    fun addField(name: String, areaAcres: Double, cropType: String, stage: String) {
        viewModelScope.launch {
            repository.addField(
                FarmField(
                    name = name,
                    areaAcres = areaAcres,
                    cropType = cropType,
                    sowingDate = "Just added",
                    stage = stage,
                    stageProgress = 0.2f,
                    soilPh = 7.0,
                    nitrogenLevel = "Optimal",
                    phosphorusLevel = "Optimal",
                    potassiumLevel = "Optimal",
                    organicCarbonPercent = 0.60,
                    moisturePercent = 45
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
