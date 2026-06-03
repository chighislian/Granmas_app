package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HealthViewModel(application: Application, private val repository: HealthRepository) : AndroidViewModel(application) {

    // Helper to get today's date string formatted as yyyy-MM-dd
    val todayDateString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // UI States
    val bpLogs = repository.bpLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sugarLogs = repository.sugarLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val symptomLogs = repository.symptomLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val medications = repository.medications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val emergencyInfo = repository.emergencyInfo.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EmergencyInfo())

    // Tracks selected history filter: 7, 14, or 30 days
    private val _historyDays = MutableStateFlow(7)
    val historyDays: StateFlow<Int> = _historyDays.asStateFlow()

    fun setHistoryDays(days: Int) {
        _historyDays.value = days
    }

    // List of medications taken today
    private val _medsTakenToday = MutableStateFlow<List<MedicationTakeLog>>(emptyList())
    val medsTakenToday: StateFlow<List<MedicationTakeLog>> = _medsTakenToday.asStateFlow()

    // Current symptom log for today
    private val _todaySymptomLog = MutableStateFlow<SymptomLog?>(null)
    val todaySymptomLog: StateFlow<SymptomLog?> = _todaySymptomLog.asStateFlow()

    // Prediction Warnings State
    private val _predictionWarnings = MutableStateFlow<List<String>>(emptyList())
    val predictionWarnings: StateFlow<List<String>> = _predictionWarnings.asStateFlow()

    init {
        // Initial setup and DB prepopulation
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
            // Observe today's taken medications and symptoms
            combine(
                repository.getTakeLogsForDate(todayDateString),
                repository.getSymptomLogForDate(todayDateString)
            ) { takeLogs, todaySymptoms ->
                _medsTakenToday.value = takeLogs
                _todaySymptomLog.value = todaySymptoms
            }.collect()
        }

        // Generate prediction warnings whenever data changes
        viewModelScope.launch {
            combine(bpLogs, sugarLogs, symptomLogs) { bps, sugars, symptoms ->
                analyzePatterns(bps, sugars, symptoms)
            }.collect { warnings ->
                _predictionWarnings.value = warnings
            }
        }
    }

    // --- Logging Functions ---

    fun logBloodPressure(systolic: Int, diastolic: Int, pulse: Int, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            repository.insertBPLog(BloodPressureLog(systolic = systolic, diastolic = diastolic, pulse = pulse, timestamp = timestamp))
        }
    }

    fun deleteBPLog(log: BloodPressureLog) = viewModelScope.launch {
        repository.deleteBPLog(log)
    }

    fun logSugar(level: Double, isBeforeMeal: Boolean, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            repository.insertSugarLog(SugarLog(level = level, isBeforeMeal = isBeforeMeal, timestamp = timestamp))
        }
    }

    fun deleteSugarLog(log: SugarLog) = viewModelScope.launch {
        repository.deleteSugarLog(log)
    }

    fun logSymptoms(
        headache: Boolean,
        dizziness: Boolean,
        fatigue: Boolean,
        nausea: Boolean,
        chestTightness: Boolean,
        blurredVision: Boolean,
        swollenFeet: Boolean,
        notes: String,
        activities: String,
        dateString: String = todayDateString
    ) {
        viewModelScope.launch {
            repository.insertSymptomLog(
                SymptomLog(
                    dateString = dateString,
                    headache = headache,
                    dizziness = dizziness,
                    fatigue = fatigue,
                    nausea = nausea,
                    chestTightness = chestTightness,
                    blurredVision = blurredVision,
                    swollenFeet = swollenFeet,
                    notes = notes,
                    activities = activities,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // --- Medication Functions ---

    fun addMedication(name: String, dosage: String, timeString: String) {
        viewModelScope.launch {
            repository.insertMedication(Medication(name = name, dosage = dosage, timeString = timeString))
        }
    }

    fun deleteMedication(med: Medication) {
        viewModelScope.launch {
            repository.deleteMedication(med)
        }
    }

    fun toggleMedicationTaken(medicationId: Long, isTaken: Boolean) {
        viewModelScope.launch {
            if (isTaken) {
                repository.insertMedicationTakeLog(
                    MedicationTakeLog(medicationId = medicationId, dateString = todayDateString, taken = true)
                )
            } else {
                repository.deleteMedicationTakeLog(medicationId, todayDateString)
            }
        }
    }

    // --- Emergency Functions ---

    fun saveEmergencyInfo(info: EmergencyInfo) {
        viewModelScope.launch {
            repository.insertEmergencyInfo(info)
        }
    }

    // --- Sickness Pattern Predictor (Rule-Based Engine) ---

    private fun analyzePatterns(
        bps: List<BloodPressureLog>,
        sugars: List<SugarLog>,
        symptoms: List<SymptomLog>
    ): List<String> {
        val warnings = mutableListOf<String>()

        // Rule-Based Checkers
        val sickDays = symptoms.filter {
            it.headache || it.dizziness || it.fatigue || it.nausea || it.chestTightness || it.blurredVision || it.swollenFeet
        }

        if (sickDays.isEmpty()) {
            return listOf("Gran has been feeling great recently! No bad symptom patterns detected so far.")
        }

        // Match 1: Symptoms appearing after high physical activity
        val activeKeywords = listOf("walk", "garden", "laundry", "clean", "busy", "exert", "shop", "exertion", "work")
        val activeSickDays = sickDays.filter { log ->
            val act = log.activities.lowercase()
            activeKeywords.any { kw -> act.contains(kw) }
        }

        if (activeSickDays.isNotEmpty()) {
            val activitiesCited = activeSickDays.map { it.activities.trim() }.distinct().take(2).joinToString(", ")
            warnings.add(
                "⚠️ Gran tends to feel unwell (headache, dizziness, or fatigue) on days with high activity (such as: $activitiesCited). The next active days will require careful pacing and extra rest."
            )
        }

        // Match 2: Blood Pressure spikes correlating with symptoms the next day
        // Find if on day of symptoms (T), or the evening before (T-1), BP was elevated
        val elevatedBpSpikes = bps.filter { it.systolic >= 140 || it.diastolic >= 90 }
        if (elevatedBpSpikes.isNotEmpty() && sickDays.isNotEmpty()) {
            var correlations = 0
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            for (sick in sickDays) {
                // Approximate timestamp for the day
                val sickTimeMs = sick.timestamp
                val priorTimeMs = sickTimeMs - (24 * 60 * 60 * 1000)

                // Check if any spike matches this window (+/- 12 hrs from T-1/T)
                val correlatedSpikes = elevatedBpSpikes.any { bp ->
                    Math.abs(bp.timestamp - priorTimeMs) < (18 * 60 * 60 * 1000)
                }
                if (correlatedSpikes) {
                    correlations++
                }
            }
            if (correlations >= 1) {
                warnings.add(
                    "⚠️ BP Spike Warning: On key occasions, symptoms like dizziness or headache occurred post a spike in systolic BP above 140 mmHg. Extra BP logs should be observed during stress."
                )
            }
        }

        // Match 3: Blood sugar spike / dips correlating with symptoms
        val unstableGlucose = sugars.filter { it.level > 10.0 || it.level < 4.5 }
        if (unstableGlucose.isNotEmpty()) {
            warnings.add(
                "⚠️ Glucose Variation: Sugar readings outside safe bounds (high over 10.0 mmol/L or low below 4.5 mmol/L) are recorded. High levels correlate with tiredness. Keep to regular meals."
            )
        }

        // Match 4: Day of the Week pattern
        val dayOfWeekCounts = IntArray(8) // index 1-7 for SUNDAY to SATURDAY
        val calendar = Calendar.getInstance()
        var dayPatternDetected = false
        var preferredDayName = ""

        for (sick in sickDays) {
            calendar.timeInMillis = sick.timestamp
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            dayOfWeekCounts[dayOfWeek]++
        }

        for (i in 1..7) {
            if (dayOfWeekCounts[i] >= 2) { // occurred on the same day in multiple weeks
                dayPatternDetected = true
                preferredDayName = when(i) {
                    Calendar.SUNDAY -> "Sundays"
                    Calendar.MONDAY -> "Mondays"
                    Calendar.TUESDAY -> "Tuesdays"
                    Calendar.WEDNESDAY -> "Wednesdays"
                    Calendar.THURSDAY -> "Thursdays"
                    Calendar.FRIDAY -> "Fridays"
                    Calendar.SATURDAY -> "Saturdays"
                    else -> ""
                }
                break
            }
        }

        if (dayPatternDetected) {
            warnings.add(
                "⚠️ Recurring Schedule: Gran tends to feel more fatigued on $preferredDayName. Check if she is skipping medication doses or over-exercising on these specific days."
            )
        }

        // Final generic safety fallback advice
        if (warnings.isEmpty()) {
            warnings.add(
                "💡 Safe Patterns: Symptoms are sporadic and do not follow regular activity spikes. Continue logging symptoms, activities, and medication adherence to keep Gran's predictions accurate."
            )
        }

        return warnings
    }
}

// Custom Factory for creation
class HealthViewModelFactory(
    private val application: Application,
    private val repository: HealthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
