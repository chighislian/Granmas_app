package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.example.data.SugarLog
import com.example.ui.HealthViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogSugarScreen(
    viewModel: HealthViewModel,
    onBack: () -> Unit
) {
    var sugarInput by remember { mutableStateOf("") }
    var isBeforeMeal by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val sugarLogs by viewModel.sugarLogs.collectAsStateWithLifecycle()

    val currentTimestamp = remember { System.currentTimeMillis() }
    val formattedDate = remember(currentTimestamp) {
        SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(currentTimestamp))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Log Blood Sugar 🩸",
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
                            "New Sugar Reading",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        // 1. Automatic time indicator
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

                        // 2. Sugar Input box
                        Column {
                            Text(
                                "Blood Glucose Level (mmol/L)",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = sugarInput,
                                onValueChange = { sugarInput = it },
                                placeholder = { Text("e.g. 6.5", fontSize = 18.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("sugar_input_field"),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // 3. Before vs After meal selector (Big, easy to tap buttons)
                        Column {
                            Text(
                                "When did you take this reading?",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Before Meal Button
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isBeforeMeal = true }
                                        .testTag("before_meal_toggle"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isBeforeMeal) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(
                                        width = if (isBeforeMeal) 2.dp else 1.dp,
                                        color = if (isBeforeMeal) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("🌸 Before Meal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                            Text("Target: 4.0 - 7.0 mmol/L", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                        }
                                    }
                                }

                                // After Meal Button
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isBeforeMeal = false }
                                        .testTag("after_meal_toggle"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (!isBeforeMeal) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(
                                        width = if (!isBeforeMeal) 2.dp else 1.dp,
                                        color = if (!isBeforeMeal) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("🍽️ After Meal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                            Text("Target: under 8.5 mmol/L", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                        }
                                    }
                                }
                            }
                        }

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.testTag("err_msg_sugar_fields")
                            )
                        }

                        // Large save button
                        Button(
                            onClick = {
                                val level = sugarInput.toDoubleOrNull()
                                if (level == null) {
                                    errorMessage = "⚠️ Please enter a number in the box. Use a dot (.) for decimals (e.g. 6.4)."
                                } else if (level <= 1.0 || level >= 35.0) {
                                    errorMessage = "⚠️ Please double check your reading level."
                                } else {
                                    viewModel.logSugar(level, isBeforeMeal, currentTimestamp)
                                    sugarInput = ""
                                    errorMessage = ""
                                    onBack() // Go back home immediately after saving
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .testTag("save_sugar_log_btn"),
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                "Save Sugar level ✅",
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Recent Logs Section Title
            item {
                Text(
                    "Your Recent Blood Sugar Logs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Recent items list
            if (sugarLogs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            "No sugar logs found. Add one above!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(sugarLogs.take(5)) { log ->
                    RecentSugarItem(log = log, onDelete = { viewModel.deleteSugarLog(log) })
                }
            }
        }
    }
}

@Composable
fun RecentSugarItem(
    log: SugarLog,
    onDelete: () -> Unit
) {
    val dateText = remember(log.timestamp) {
        SimpleDateFormat("EEE, MMM dd 'at' hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
    }

    // Determine status (Normal/High/Low)
    val evaluation = remember(log.level, log.isBeforeMeal) {
        if (log.isBeforeMeal) {
            when {
                log.level < 4.0 -> Triple("Low 📉", Color(0xFF1565C0), Color(0xFFE3F2FD))
                log.level <= 7.0 -> Triple("Normal 🌸", Color(0xFF2E7D32), Color(0xFFE8F5E9))
                else -> Triple("High 📈", Color(0xFFD84315), Color(0xFFFBE9E7))
            }
        } else {
            when {
                log.level < 4.5 -> Triple("Low 📉", Color(0xFF1565C0), Color(0xFFE3F2FD))
                log.level <= 8.5 -> Triple("Normal 🌸", Color(0xFF2E7D32), Color(0xFFE8F5E9))
                else -> Triple("High 📈", Color(0xFFD84315), Color(0xFFFBE9E7))
            }
        }
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
            // Visual Level Display Circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(evaluation.third, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        String.format(Locale.getDefault(), "%.1f", log.level),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = evaluation.second
                    )
                    Text(
                        "mmol/L",
                        fontSize = 11.sp,
                        color = evaluation.second.copy(alpha = 0.8f)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (log.isBeforeMeal) "🌸 Before Meal" else "🍽️ After Meal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = evaluation.second.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            evaluation.first,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = evaluation.second,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Undo Control Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_sugar_log_btn")
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete entry",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
