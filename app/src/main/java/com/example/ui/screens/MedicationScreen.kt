package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Medication
import com.example.ui.HealthViewModel
import com.example.ui.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScreen(
    viewModel: HealthViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val medications by viewModel.medications.collectAsStateWithLifecycle()
    val medsTakenToday by viewModel.medsTakenToday.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog form states
    var medName by remember { mutableStateOf("") }
    var medDosage by remember { mutableStateOf("") }
    var medTimeHour by remember { mutableStateOf("08") }
    var medTimeMin by remember { mutableStateOf("00") }
    var dialogError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Medication Checklist 💊",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Go Back",
                            tint = Color(0xFF1B5E20)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7FAF7)
                ),
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_medication_icon_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add medicine",
                            tint = Color(0xFF1B5E20),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF1B5E20),
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .testTag("add_medication_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Medicine", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = "medications",
                onNavigate = onNavigate
            )
        },
        containerColor = Color(0xFFF7FAF7)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp) // extra padding for FAB
        ) {
            // Introductory Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    shape = RoundedCornerShape(40.dp), // 2.5rem corner radius
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = BorderStroke(2.dp, Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Text(
                        text = "Keep track of your daily medicine below, Gran! Simply tap a pill card to check it off as 'Taken'. If you stop taking a medication, tap the bin icon to delete it.",
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 26.sp),
                        color = Color(0xFF1B5E20)
                    )
                }
            }

            // Checklist section
            item {
                Text(
                    text = "Today's Intake Checklist",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (medications.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Text(
                            text = "No medications registered. Click 'Add Medicine' below to log your daily treatment program! 🩺",
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp),
                            color = Color(0xFF64748B)
                        )
                    }
                }
            } else {
                // Render medications
                items(medications) { med ->
                    val isTaken = medsTakenToday.any { it.medicationId == med.id && it.taken }
                    MedicationAdherenceCard(
                        medication = med,
                        isTaken = isTaken,
                        onToggle = { viewModel.toggleMedicationTaken(med.id, !isTaken) },
                        onDelete = { viewModel.deleteMedication(med) }
                    )
                }
            }
        }

        // Add Medication Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    dialogError = ""
                },
                title = {
                    Text(
                        "Add Daily Medication 💊",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 22.sp),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1B5E20)
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Medicine Name
                        Column {
                            Text("Medicine Name", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                            Spacer(Modifier.height(4.dp))
                            OutlinedTextField(
                                value = medName,
                                onValueChange = { medName = it },
                                placeholder = { Text("e.g. Metformin") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dialog_med_name"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1B5E20),
                                    unfocusedBorderColor = Color(0xFFE8F5E9)
                                )
                            )
                        }

                        // Dosage
                        Column {
                            Text("Dosage / Instructions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                            Spacer(Modifier.height(4.dp))
                            OutlinedTextField(
                                value = medDosage,
                                onValueChange = { medDosage = it },
                                placeholder = { Text("e.g. 500 mg (1 pill)") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dialog_med_dosage"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1B5E20),
                                    unfocusedBorderColor = Color(0xFFE8F5E9)
                                )
                            )
                        }

                        // Time Hour & Min
                        Column {
                            Text("Time (HH:MM - 24 hour)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = medTimeHour,
                                    onValueChange = { if (it.length <= 2) medTimeHour = it },
                                    placeholder = { Text("08") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("dialog_med_hour"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF1B5E20),
                                        unfocusedBorderColor = Color(0xFFE8F5E9)
                                    )
                                )
                                Text(
                                    ":",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                                OutlinedTextField(
                                    value = medTimeMin,
                                    onValueChange = { if (it.length <= 2) medTimeMin = it },
                                    placeholder = { Text("30") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("dialog_med_min"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF1B5E20),
                                        unfocusedBorderColor = Color(0xFFE8F5E9)
                                    )
                                )
                            }
                        }

                        if (dialogError.isNotEmpty()) {
                            Text(
                                dialogError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val nameStr = medName.trim()
                            val doseStr = medDosage.trim()
                            val hourVal = medTimeHour.trim().toIntOrNull()
                            val minVal = medTimeMin.trim().toIntOrNull()

                            if (nameStr.isEmpty() || doseStr.isEmpty() || hourVal == null || minVal == null) {
                                dialogError = "⚠️ Please fill in all boxes correctly."
                            } else if (hourVal < 0 || hourVal > 23 || minVal < 0 || minVal > 59) {
                                dialogError = "⚠️ Enter a valid 24h limit (Hour: 0-23, Minute: 0-59)."
                            } else {
                                val hrPad = medTimeHour.padStart(2, '0')
                                val mnPad = medTimeMin.padStart(2, '0')
                                viewModel.addMedication(nameStr, doseStr, "$hrPad:$mnPad")

                                // Reset form
                                medName = ""
                                medDosage = ""
                                medTimeHour = "08"
                                medTimeMin = "00"
                                dialogError = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dialog_submit_med_btn")
                    ) {
                        Text("Add to Checklist ✅", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAddDialog = false
                            dialogError = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", style = MaterialTheme.typography.labelLarge, color = Color(0xFF475569))
                    }
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun MedicationAdherenceCard(
    medication: Medication,
    isTaken: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val containerBg by animateColorAsState(
        targetValue = if (isTaken) Color(0xFFE8F5E9) else Color.White,
        label = "BgAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("med_card_${medication.id}"),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(
            width = if (isTaken) 2.dp else 1.dp,
            color = if (isTaken) Color(0xFF1B5E20) else Color(0xFFE8F5E9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tick Icon Area (Huge and obvious tap target!)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .testTag("checkbox_toggle_${medication.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (isTaken) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Taken status",
                        tint = Color(0xFF1B5E20),
                        modifier = Modifier.size(38.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.RadioButtonUnchecked,
                        contentDescription = "Not taken yet status",
                        tint = Color(0xFF1B5E20).copy(alpha = 0.3f),
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (isTaken) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    ),
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Dosage: ${medication.dosage}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.MedicalServices,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Take at: ${medication.timeString}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1B5E20)
                    )
                }
            }

            // Delete bin action (stops taking it completely)
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_medication_btn")
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete medication",
                    tint = Color(0xFFDC2626).copy(alpha = 0.8f) // Red-600 with clean styling
                )
            }
        }
    }
}
