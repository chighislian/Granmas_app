package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SymptomLog
import com.example.ui.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SymptomsScreen(
    viewModel: HealthViewModel,
    onBack: () -> Unit
) {
    val todaySymptomLog by viewModel.todaySymptomLog.collectAsStateWithLifecycle()

    // Form states
    var headache by remember { mutableStateOf(false) }
    var dizziness by remember { mutableStateOf(false) }
    var fatigue by remember { mutableStateOf(false) }
    var nausea by remember { mutableStateOf(false) }
    var chestTightness by remember { mutableStateOf(false) }
    var blurredVision by remember { mutableStateOf(false) }
    var swollenFeet by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var activities by remember { mutableStateOf("") }

    // Sync state with existing daily log on open
    LaunchedEffect(todaySymptomLog) {
        todaySymptomLog?.let { log ->
            headache = log.headache
            dizziness = log.dizziness
            fatigue = log.fatigue
            nausea = log.nausea
            chestTightness = log.chestTightness
            blurredVision = log.blurredVision
            swollenFeet = log.swollenFeet
            notes = log.notes
            activities = log.activities
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Daily Care Log 📝",
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
            // Introductory helpful card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Text(
                        "Hi Gran! Let’s note down how you’ve felt today. Check off any symptoms and select what activities you did. This helps us see if certain routines cause fatigue or headaches! 🌸",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 26.sp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // 1. SYMPTOMS CHECKLIST (Large tactile cards)
            item {
                Text(
                    "Are you experiencing any of these?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SymptomTactileCard(label = "Headache 🤕", isChecked = headache, onCheckedChange = { headache = it }, tag = "headache_symptom")
                    SymptomTactileCard(label = "Dizziness / Lightheadedness 🌀", isChecked = dizziness, onCheckedChange = { dizziness = it }, tag = "dizziness_symptom")
                    SymptomTactileCard(label = "Severe Fatigue / Tiredness 🩺", isChecked = fatigue, onCheckedChange = { fatigue = it }, tag = "fatigue_symptom")
                    SymptomTactileCard(label = "Nausea or Sick Stomach 🤢", isChecked = nausea, onCheckedChange = { nausea = it }, tag = "nausea_symptom")
                    SymptomTactileCard(label = "Chest Tightness / Pain ⚠️", isChecked = chestTightness, onCheckedChange = { chestTightness = it }, tag = "chest_tightness_symptom")
                    SymptomTactileCard(label = "Blurred or Hazey Vision 👁️‍🗨️", isChecked = blurredVision, onCheckedChange = { blurredVision = it }, tag = "blurred_vision_symptom")
                    SymptomTactileCard(label = "Swollen Feet / Ankles 🦶", isChecked = swollenFeet, onCheckedChange = { swollenFeet = it }, tag = "swollen_feet_symptom")
                }
            }

            // 2. ACTIVITY CHIPS LOGGING
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Today’s Activity Log 🚶‍♀️",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        "What did you do today? (Tap tags below to auto-fill, or write your own)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )

                    // Helper tags
                    val prefilledActivities = listOf("walked", "rested", "ate late", "skipped medication", "gardened", "busy day", "slept well", "did laundry")
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (act in prefilledActivities) {
                            val isActive = activities.lowercase().contains(act)
                            FilterChip(
                                selected = isActive,
                                onClick = {
                                    activities = if (isActive) {
                                        // Remove tag
                                        activities.split(",")
                                            .map { it.trim() }
                                            .filter { it.lowercase() != act }
                                            .joinToString(", ")
                                    } else {
                                        // Add tag
                                        if (activities.trim().isEmpty()) act
                                        else "${activities.trim()}, $act"
                                    }
                                },
                                label = { Text(act.replaceFirstChar { it.uppercase() }) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = activities,
                        onValueChange = { activities = it },
                        placeholder = { Text("e.g. rested with tea, walked 20 mins") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("symptom_activities_input"),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 3. FREE TEXT NOTES FIELD
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Additional Feeling Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("Write about your appetite, mood, or anything else...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("symptom_notes_input"),
                        maxLines = 5,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 4. BIG SAVE LOG BUTTON
            item {
                Button(
                    onClick = {
                        viewModel.logSymptoms(
                            headache = headache,
                            dizziness = dizziness,
                            fatigue = fatigue,
                            nausea = nausea,
                            chestTightness = chestTightness,
                            blurredVision = blurredVision,
                            swollenFeet = swollenFeet,
                            notes = notes,
                            activities = activities
                        )
                        onBack() // Redirect back home after logging
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .testTag("save_symptoms_log_btn"),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "Save Today’s Logs ✅",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun SymptomTactileCard(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    val cardColor by animateColorAsState(
        targetValue = if (isChecked) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
        label = "BgColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .testTag(tag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(
            width = if (isChecked) 2.dp else 1.dp,
            color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isChecked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )

            // Large checkmark indicator
            if (isChecked) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.Transparent)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                )
            }
        }
    }
}
