package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_pressure_logs")
data class BloodPressureLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sugar_logs")
data class SugarLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val level: Double,
    val isBeforeMeal: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "symptom_logs")
data class SymptomLog(
    @PrimaryKey(autoGenerate = false) val dateString: String, // format: yyyy-MM-dd
    val headache: Boolean = false,
    val dizziness: Boolean = false,
    val fatigue: Boolean = false,
    val nausea: Boolean = false,
    val chestTightness: Boolean = false,
    val blurredVision: Boolean = false,
    val swollenFeet: Boolean = false,
    val notes: String = "",
    val activities: String = "", // comma-separated or similar
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String,
    val timeString: String // format: HH:mm
)

@Entity(tableName = "medication_take_logs")
data class MedicationTakeLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicationId: Long,
    val dateString: String, // format: yyyy-MM-dd
    val taken: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "emergency_info")
data class EmergencyInfo(
    @PrimaryKey val id: Int = 1, // Single-row database entry
    val fullName: String = "Gran (Mary)",
    val conditions: String = "Type 2 Diabetes, High Blood Pressure (Hypertension)",
    val medications: String = "Metformin 500mg (Twice daily), Lisinopril 10mg (Once daily in the morning)",
    val doctorName: String = "Dr. Robert Carter",
    val doctorPhone: String = "555-0177",
    val contactName: String = "Sarah (Daughter)",
    val contactPhone: String = "555-0144"
)
