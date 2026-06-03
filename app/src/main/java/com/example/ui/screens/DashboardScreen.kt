package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HealthViewModel
import com.example.ui.components.BottomNavBar
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HealthViewModel,
    onNavigate: (String) -> Unit
) {
    val bpLogs by viewModel.bpLogs.collectAsStateWithLifecycle()
    val sugarLogs by viewModel.sugarLogs.collectAsStateWithLifecycle()
    val todaySymptom by viewModel.todaySymptomLog.collectAsStateWithLifecycle()
    val medications by viewModel.medications.collectAsStateWithLifecycle()
    val medsTakenToday by viewModel.medsTakenToday.collectAsStateWithLifecycle()

    // Determine today's entries
    val todayBP = bpLogs.firstOrNull {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.format(Date(it.timestamp)) == viewModel.todayDateString
    }
    val todaySugar = sugarLogs.firstOrNull {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.format(Date(it.timestamp)) == viewModel.todayDateString
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF7FAF7))
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Gran’s Health",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Good morning, Ma Rose",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                color = Color(0xFF2E7D32).copy(alpha = 0.8f)
                            )
                        )
                    }

                    // Avatar Circle matching spec
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE8F5E9), CircleShape)
                            .border(2.dp, Color(0xFFA5D6A7), CircleShape)
                            .clickable { onNavigate("emergency") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👵", fontSize = 24.sp)
                    }
                }
            }
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = "dashboard",
                onNavigate = onNavigate
            )
        },
        containerColor = Color(0xFFF7FAF7) // Premium Soft Herbal Cream Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
        ) {
            // 1. TODAY'S TELEMETRY GRID (BP & Sugar side-by-side with 2.5rem/40dp rounding)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Blood Pressure Card
                    Box(modifier = Modifier.weight(1f)) {
                        TelemetryCard(
                            title = "BP Today",
                            value = if (todayBP != null) "${todayBP.systolic}/${todayBP.diastolic}" else "--/--",
                            unit = "mmHg",
                            subValue = if (todayBP != null) "Pulse: ${todayBP.pulse}" else "No log today",
                            onClick = { onNavigate("log_bp") }
                        )
                    }

                    // Sugar Levels Card
                    Box(modifier = Modifier.weight(1f)) {
                        TelemetryCard(
                            title = "Sugar",
                            value = if (todaySugar != null) String.format(Locale.getDefault(), "%.1f", todaySugar.level) else "--.-",
                            unit = "mmol/L",
                            subValue = if (todaySugar != null) {
                                if (todaySugar.isBeforeMeal) "Before Meal" else "After Meal"
                            } else "No log today",
                            onClick = { onNavigate("log_sugar") }
                        )
                    }
                }
            }

            // 2. WELLNESS CHECK-IN CONTAINER (2.5rem / 40dp rounded filled with #E8F5E9)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wellness_checkin_container"),
                    shape = RoundedCornerShape(40.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = BorderStroke(2.dp, Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "How do you feel?",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color(0xFF1B5E20),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tap to log your mood & symptoms",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF2E7D32),
                                        fontSize = 14.sp
                                    )
                                )
                            }
                            Text("😊", fontSize = 36.sp)
                        }

                        // Mood selector badges embedded inside checkin box
                        val checkingMood = todaySymptom?.notes ?: ""
                        if (checkingMood.startsWith("Feeling:")) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFFA5D6A7).copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "Checked-in: ${checkingMood.substringAfter("Feeling: ").trim()}",
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1B5E20)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MoodSelectionButton(emoji = "🌸", label = "Great") {
                                viewModel.logSymptoms(
                                    headache = false, dizziness = false, fatigue = false, nausea = false, chestTightness = false, blurredVision = false, swollenFeet = false,
                                    notes = "Feeling: Great 🌸", activities = "Rested well"
                                )
                            }
                            MoodSelectionButton(emoji = "😊", label = "Good") {
                                viewModel.logSymptoms(
                                    headache = false, dizziness = false, fatigue = false, nausea = false, chestTightness = false, blurredVision = false, swollenFeet = false,
                                    notes = "Feeling: Good 😊", activities = "Rested"
                                )
                            }
                            MoodSelectionButton(emoji = "🩺", label = "Tired") {
                                viewModel.logSymptoms(
                                    headache = false, dizziness = false, fatigue = true, nausea = false, chestTightness = false, blurredVision = false, swollenFeet = false,
                                    notes = "Feeling: Tired 🩺", activities = "Slept a bit late"
                                )
                            }
                            MoodSelectionButton(emoji = "⚠️", label = "Sick") {
                                viewModel.logSymptoms(
                                    headache = true, dizziness = true, fatigue = true, nausea = false, chestTightness = false, blurredVision = false, swollenFeet = false,
                                    notes = "Feeling: Sick ⚠️", activities = "Skipped rest"
                                )
                            }
                        }
                    }
                }
            }

            // 3. CARING PREDICTION BANNER (💡 A Gentle Reminder)
            item {
                val summaryText = when {
                    todayBP != null && todaySugar != null -> {
                        "Excellent! You've logged both your Blood Pressure and Sugar readings for today. You are doing an amazing job taking care of yourself!"
                    }
                    todayBP != null -> {
                        "You've checked your Blood Pressure. Don’t forget to check your Sugar levels after you eat!"
                    }
                    todaySugar != null -> {
                        "Your Blood Sugar is recorded. Please remember to check your Blood Pressure as well when you are resting."
                    }
                    else -> {
                        "Good morning, Gran! Let’s start today's care by checking your Blood Pressure and blood Sugar."
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(40.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        // Thick border-l-8 effect on left edge
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .fillMaxHeight()
                                .background(Color(0xFF81C784))
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("💡", fontSize = 28.sp, modifier = Modifier.padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    "A Gentle Reminder",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20),
                                        fontSize = 18.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    summaryText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF475569), // slate-600
                                        lineHeight = 24.sp
                                    )
                                )

                                // Medications subview
                                val medsTaken = medsTakenToday.filter { it.taken }.size
                                val totalMeds = medications.size
                                if (totalMeds > 0) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Medications taken today: $medsTaken out of $totalMeds 💊",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B5E20)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. QUICK ACTION PILLS ROW FROM DESIGN SPEC
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { onNavigate("log_bp") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF0F172A)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("shortcut_log_bp"),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🩸", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log BP", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                        }
                    }

                    Button(
                        onClick = { onNavigate("log_sugar") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF0F172A)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("shortcut_log_sugar"),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🍯", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Sugar", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                        }
                    }
                }
            }

            // 5. QUICK ACCESS CHANNELS TITLE & CARDS
            item {
                Text(
                    "Quick Access Channels",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Log Symptoms
                    MenuRow(
                        title = "Log Daily Symptoms",
                        description = "Check off headaches, tiredness, or dizziness",
                        icon = Icons.Filled.ListAlt,
                        badgeText = if (todaySymptom != null) "Logged ✅" else null,
                        color = Color(0xFFE8F5E9),
                        onClick = { onNavigate("log_symptoms") }
                    )

                    // Medications Checklist
                    MenuRow(
                        title = "Medication Reminder",
                        description = "See which medicines are left to take today",
                        icon = Icons.Filled.Schedule,
                        badgeText = "${medsTakenToday.size}/${medications.size} taken",
                        color = Color(0xFFE8F5E9),
                        onClick = { onNavigate("medications") }
                    )

                    // History Card
                    MenuRow(
                        title = "History & Charts",
                        description = "Scroll back and see weekly/monthly trends",
                        icon = Icons.Filled.BarChart,
                        color = Color(0xFFE8F5E9),
                        onClick = { onNavigate("history") }
                    )

                    // Sickness Predictor Card
                    MenuRow(
                        title = "Sickness Prediction Warnings",
                        description = "Learn how rest and routines prevent sick days",
                        icon = Icons.Filled.Insights,
                        color = Color(0xFFE8F5E9),
                        onClick = { onNavigate("prediction") }
                    )
                }
            }

            // 6. EMERGENCY RED RECTANGLE BANNER
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate("emergency") }
                        .testTag("emergency_banner_btn")
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(40.dp),
                    border = BorderStroke(2.dp, Color(0xFFFFCDD2)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🚨", fontSize = 36.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Emergency Help Card",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626) // red-600
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Tap here to display Gran's conditions, doctor list, and SOS dial keys instantly.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF9E1C1C),
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TelemetryCard(
    title: String,
    value: String,
    unit: String,
    subValue: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(40.dp), // rounded-[2.5rem] matching design
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE8F5E9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    letterSpacing = 1.2.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    fontSize = 28.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                unit,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF64748B), // Slate text color
                    fontSize = 12.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                subValue,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF64748B).copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MoodSelectionButton(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    var isClicked by remember { mutableStateOf(false) }

    // Auto-reset highlight
    LaunchedEffect(isClicked) {
        if (isClicked) {
            delay(1000)
            isClicked = false
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                isClicked = true
                onClick()
            }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    if (isClicked) Color(0xFFC8E6C9)
                    else Color.White,
                    CircleShape
                )
                .border(
                    width = 2.dp,
                    color = if (isClicked) Color(0xFF1B5E20) else Color(0xFFE8F5E9),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 32.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1B5E20)
        )
    }
}

@Composable
fun MenuRow(
    title: String,
    description: String,
    icon: ImageVector,
    badgeText: String? = null,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE8F5E9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(color, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF1B5E20),
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    )
                    if (badgeText != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                badgeText,
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = Color(0xFF475569) // slate-600
                )
            }
        }
    }
}
