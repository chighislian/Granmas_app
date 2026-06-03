package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {

    // --- Blood Pressure ---
    @Query("SELECT * FROM blood_pressure_logs ORDER BY timestamp DESC")
    fun getAllBPLogs(): Flow<List<BloodPressureLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBPLog(log: BloodPressureLog)

    @Delete
    suspend fun deleteBPLog(log: BloodPressureLog)

    // --- Sugar Logs ---
    @Query("SELECT * FROM sugar_logs ORDER BY timestamp DESC")
    fun getAllSugarLogs(): Flow<List<SugarLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSugarLog(log: SugarLog)

    @Delete
    suspend fun deleteSugarLog(log: SugarLog)

    // --- Symptom Logs ---
    @Query("SELECT * FROM symptom_logs ORDER BY timestamp DESC")
    fun getAllSymptomLogs(): Flow<List<SymptomLog>>

    @Query("SELECT * FROM symptom_logs WHERE dateString = :dateString LIMIT 1")
    fun getSymptomLogForDate(dateString: String): Flow<SymptomLog?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptomLog(log: SymptomLog)

    // --- Medications ---
    @Query("SELECT * FROM medications ORDER BY id DESC")
    fun getAllMedications(): Flow<List<Medication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(med: Medication)

    @Delete
    suspend fun deleteMedication(med: Medication)

    // --- Medication Taken Logs ---
    @Query("SELECT * FROM medication_take_logs WHERE dateString = :dateString")
    fun getTakeLogsForDate(dateString: String): Flow<List<MedicationTakeLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicationTakeLog(log: MedicationTakeLog)

    @Query("DELETE FROM medication_take_logs WHERE medicationId = :medicationId AND dateString = :dateString")
    suspend fun deleteMedicationTakeLog(medicationId: Long, dateString: String)

    // --- Emergency Info ---
    @Query("SELECT * FROM emergency_info WHERE id = 1 LIMIT 1")
    fun getEmergencyInfo(): Flow<EmergencyInfo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyInfo(info: EmergencyInfo)
}
