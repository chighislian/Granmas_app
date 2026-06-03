package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class HealthRepository(private val healthDao: HealthDao) {

    val bpLogs: Flow<List<BloodPressureLog>> = healthDao.getAllBPLogs()
    val sugarLogs: Flow<List<SugarLog>> = healthDao.getAllSugarLogs()
    val symptomLogs: Flow<List<SymptomLog>> = healthDao.getAllSymptomLogs()
    val medications: Flow<List<Medication>> = healthDao.getAllMedications()
    val emergencyInfo: Flow<EmergencyInfo?> = healthDao.getEmergencyInfo()

    fun getSymptomLogForDate(dateString: String): Flow<SymptomLog?> {
        return healthDao.getSymptomLogForDate(dateString)
    }

    fun getTakeLogsForDate(dateString: String): Flow<List<MedicationTakeLog>> {
        return healthDao.getTakeLogsForDate(dateString)
    }

    suspend fun insertBPLog(log: BloodPressureLog) = healthDao.insertBPLog(log)
    suspend fun deleteBPLog(log: BloodPressureLog) = healthDao.deleteBPLog(log)

    suspend fun insertSugarLog(log: SugarLog) = healthDao.insertSugarLog(log)
    suspend fun deleteSugarLog(log: SugarLog) = healthDao.deleteSugarLog(log)

    suspend fun insertSymptomLog(log: SymptomLog) = healthDao.insertSymptomLog(log)

    suspend fun insertMedication(med: Medication) = healthDao.insertMedication(med)
    suspend fun deleteMedication(med: Medication) = healthDao.deleteMedication(med)

    suspend fun insertMedicationTakeLog(log: MedicationTakeLog) = healthDao.insertMedicationTakeLog(log)
    suspend fun deleteMedicationTakeLog(medicationId: Long, dateString: String) =
        healthDao.deleteMedicationTakeLog(medicationId, dateString)

    suspend fun insertEmergencyInfo(info: EmergencyInfo) = healthDao.insertEmergencyInfo(info)

    suspend fun prepopulateIfEmpty() {
        // Prepopulate Emergency Info if empty
        val currentEmergency = emergencyInfo.first()
        if (currentEmergency == null) {
            healthDao.insertEmergencyInfo(EmergencyInfo())
        }

        // Prepopulate Medications if empty
        val currentMeds = medications.first()
        if (currentMeds.isEmpty()) {
            healthDao.insertMedication(Medication(name = "Metformin", dosage = "500 mg", timeString = "08:00"))
            healthDao.insertMedication(Medication(name = "Lisinopril", dosage = "10 mg", timeString = "08:30"))
            healthDao.insertMedication(Medication(name = "Metformin", dosage = "500 mg", timeString = "20:00"))
        }

        // Check if there are symptom or reading logs. If empty, seed historical logs
        val bpList = bpLogs.first()
        if (bpList.isEmpty()) {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Define custom days offset
            val daysData = listOf(
                // (systolic, diastolic, pulse, sugarLevel, isBeforeMeal, symptoms, activity)
                RecordSeed(-7, 138, 85, 75, 5.8, true, false, false, "Walked in park", false),
                RecordSeed(-6, 142, 88, 78, 8.2, false, true, false, "Did laundry, busy day", true), // dizziness due to overexertion
                RecordSeed(-5, 128, 78, 70, 5.5, true, false, false, "Rested, simple stretching", false),
                RecordSeed(-4, 134, 82, 72, 7.9, false, false, false, "Walked briefly", false),
                RecordSeed(-3, 145, 90, 80, 6.4, true, true, true, "Slept late, active gardening", true), // headache, dizziness
                RecordSeed(-2, 130, 80, 72, 8.5, false, false, false, "Rested, watched TV", false),
                RecordSeed(-1, 132, 81, 74, 5.9, true, false, false, "Short walk", false)
            )

            for (seed in daysData) {
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_YEAR, seed.dayOffset)
                val targetDate = calendar.time
                val dateStr = sdf.format(targetDate)
                val timeMs = targetDate.time

                // Insert blood pressure
                healthDao.insertBPLog(BloodPressureLog(systolic = seed.systolic, diastolic = seed.diastolic, pulse = seed.pulse, timestamp = timeMs))
                // Insert sugar
                healthDao.insertSugarLog(SugarLog(level = seed.sugarLevel, isBeforeMeal = seed.isBeforeMeal, timestamp = timeMs))
                // Insert symptoms & activities
                healthDao.insertSymptomLog(
                    SymptomLog(
                        dateString = dateStr,
                        headache = seed.headache,
                        dizziness = seed.dizziness,
                        fatigue = seed.headache || seed.dizziness, // simple fill
                        notes = if (seed.headache) "Drank low water today, felt a bit tired." else "Feeling okay",
                        activities = seed.activity,
                        timestamp = timeMs
                    )
                )
            }
        }
    }

    private data class RecordSeed(
        val dayOffset: Int,
        val systolic: Int,
        val diastolic: Int,
        val pulse: Int,
        val sugarLevel: Double,
        val isBeforeMeal: Boolean,
        val headache: Boolean,
        val dizziness: Boolean,
        val activity: String,
        val hadSymptoms: Boolean
    )
}
