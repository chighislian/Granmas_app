package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
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
import com.example.data.BloodPressureLog
import com.example.ui.HealthViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogBPScreen(
    viewModel: HealthViewModel,
    onBack: () -> Unit
) {
    var systolicInput by remember { mutableStateOf("") }
    var diastolicInput by remember { mutableStateOf("") }
    var pulseInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val bpLogs by viewModel.bpLogs.collectAsStateWithLifecycle()

    val currentTimestamp = remember { System.currentTimeMillis() }
    val formattedDate = remember(currentTimestamp) {
        SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(currentTimestamp))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Log Blood Pressure 🩺",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            // Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "New Reading Entry",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Display Autofilled Timestamp
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "📅 Time auto-filled: ",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    formattedDate,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // Systolic Input
                        Column {
                            Text(
                                "Systolic (Top Number) - mmHg",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = systolicInput,
                                onValueChange = { systolicInput = it },
                                placeholder = { Text("e.g. 120", fontSize = 18.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("sys_input_field"),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Diastolic Input
                        Column {
                            Text(
                                "Diastolic (Bottom Number) - mmHg",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = diastolicInput,
                                onValueChange = { diastolicInput = it },
                                placeholder = { Text("e.g. 80", fontSize = 18.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dia_input_field"),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Pulse Input
                        Column {
                            Text(
                                "Pulse Rate - Beats per Minute (BPM)",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = pulseInput,
                                onValueChange = { pulseInput = it },
                                placeholder = { Text("e.g. 72", fontSize = 18.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pulse_input_field"),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.testTag("err_msg_fields")
                            )
                        }

                        // Giant save button
                        Button(
                            onClick = {
                                val sys = systolicInput.toIntOrNull()
                                val dia = diastolicInput.toIntOrNull()
                                val pul = pulseInput.toIntOrNull()

                                if (sys == null || dia == null || pul == null) {
                                    errorMessage = "⚠️ Please enter valid whole numbers in all boxes."
                                } else if (sys <= 40 || sys >= 250 || dia <= 30 || dia >= 150 || pul <= 30 || pul >= 200) {
                                    errorMessage = "⚠️ Please double check your reading values."
                                } else {
                                    viewModel.logBloodPressure(sys, dia, pul, currentTimestamp)
                                    systolicInput = ""
                                    diastolicInput = ""
                                    pulseInput = ""
                                    errorMessage = ""
                                    onBack() // Go back home immediately after saving
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .testTag("save_bp_log_btn"),
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                "Save Reading ✅",
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Recent Logs Section Title
            item {
                Text(
                    "Your Recent Blood Pressure Logs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Recent items list
            if (bpLogs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            "No logs recorded yet. Log your first reading above!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(bpLogs.take(5)) { log ->
                    RecentBPItem(log = log, onDelete = { viewModel.deleteBPLog(log) })
                }
            }
        }
    }
}

@Composable
fun RecentBPItem(
    log: BloodPressureLog,
    onDelete: () -> Unit
) {
    val dateText = remember(log.timestamp) {
        SimpleDateFormat("EEE, MMM dd 'at' hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
    }

    // Determine status color for blood pressure
    // Normal: Sys < 120 and Dia < 80 (green)
    // Prehypertension/Elevated: Sys 120-139 or Dia 80-89 (orange)
    // Hypertension: Sys >= 140 or Dia >= 90 (red)
    val statusColors = when {
        log.systolic >= 140 || log.diastolic >= 90 -> Pair(Color(0xFFD84315), Color(0xFFFBE9E7))
        log.systolic >= 120 || log.diastolic >= 80 -> Pair(Color(0xFFE65100), Color(0xFFFFF3E0))
        else -> Pair(Color(0xFF2E7D32), Color(0xFFE8F5E9))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored Status Circle with BP Readings
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(statusColors.second, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${log.systolic}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColors.first
                    )
                    Divider(color = statusColors.first.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.width(32.dp))
                    Text(
                        "${log.diastolic}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColors.first
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    dateText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Pulse rate: ${log.pulse} bpm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Delete action (extremely comforting to have undo controls!)
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_bp_log_btn")
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete log",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
