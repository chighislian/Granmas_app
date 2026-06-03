package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BloodPressureLog
import com.example.data.SugarLog
import com.example.ui.HealthViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HealthViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val bpLogs by viewModel.bpLogs.collectAsStateWithLifecycle()
    val sugarLogs by viewModel.sugarLogs.collectAsStateWithLifecycle()
    val selectedPeriodDays by viewModel.historyDays.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("bp") } // "bp" or "sugar"

    // Filter data based on selected period
    val filteredBP = remember(bpLogs, selectedPeriodDays) {
        val cutoffMs = System.currentTimeMillis() - (selectedPeriodDays * 24L * 60L * 60L * 1000L)
        bpLogs.filter { it.timestamp >= cutoffMs }.sortedBy { it.timestamp }
    }

    val filteredSugar = remember(sugarLogs, selectedPeriodDays) {
        val cutoffMs = System.currentTimeMillis() - (selectedPeriodDays * 24L * 60L * 60L * 1000L)
        sugarLogs.filter { it.timestamp >= cutoffMs }.sortedBy { it.timestamp }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "History & Charts 📊",
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
                )
            )
        },
        bottomBar = {
            com.example.ui.components.BottomNavBar(
                currentRoute = "history",
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
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
        ) {
            // 1. SELECT TIME FRAME (7, 14, 30 Days)
            item {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        "Select Time Frame",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val periods = listOf(7, 14, 30)
                        for (p in periods) {
                            val isSelected = selectedPeriodDays == p
                            Button(
                                onClick = { viewModel.setHistoryDays(p) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF1B5E20) else Color.White,
                                    contentColor = if (isSelected) Color.White else Color(0xFF0F172A)
                                ),
                                border = BorderStroke(1.dp, if (isSelected) Color.Transparent else Color(0xFFE8F5E9)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(48.dp)
                                    .testTag("period_tab_$p"),
                                contentPadding = PaddingValues(0.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                            ) {
                                Text("$p Days", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }

            // 2. SUB-TABS: BLOOD PRESSURE VS. BLOOD SUGAR
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isBp = activeTab == "bp"
                    Button(
                        onClick = { activeTab = "bp" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBp) Color.White else Color.Transparent,
                            contentColor = if (isBp) Color(0xFF1B5E20) else Color(0xFF0F172A).copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1.1f)
                            .height(44.dp)
                            .testTag("subtab_bp"),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isBp) 1.dp else 0.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Favorite, contentDescription = "", modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("BP Readings", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    val isSugar = activeTab == "sugar"
                    Button(
                        onClick = { activeTab = "sugar" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSugar) Color.White else Color.Transparent,
                            contentColor = if (isSugar) Color(0xFF1B5E20) else Color(0xFF0F172A).copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1.1f)
                            .height(44.dp)
                            .testTag("subtab_sugar"),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isSugar) 1.dp else 0.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Bloodtype, contentDescription = "", modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Sugar Logs", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            // 3. GRAPH CARD (CUSTOM JETPACK COMPOSE CANVAS CHART)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().animateContentSize(),
                    shape = RoundedCornerShape(40.dp), // 2.5rem corner radius
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = if (activeTab == "bp") "Blood Pressure Trend Line" else "Blood Sugar Level Plot",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        if (activeTab == "bp") {
                            if (filteredBP.isEmpty()) {
                                EmptyChartDisplay()
                            } else {
                                BPLineChart(readings = filteredBP)
                            }
                        } else {
                            if (filteredSugar.isEmpty()) {
                                EmptyChartDisplay()
                            } else {
                                SugarBarChart(readings = filteredSugar)
                            }
                        }
                    }
                }
            }

            // 4. HISTORICAL LOG LIST TITLE
            item {
                Text(
                    text = if (activeTab == "bp") "Historical Blood Pressure Logs" else "Historical Blood Sugar Logs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 5. HISTORICAL LIST ITEMS
            if (activeTab == "bp") {
                if (filteredBP.isEmpty()) {
                    item {
                        Text(
                            "No BP logs found in this time frame.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    // Show all matching logs (newest first)
                    items(filteredBP.reversed()) { log ->
                        RecentBPItem(log = log, onDelete = { viewModel.deleteBPLog(log) })
                    }
                }
            } else {
                if (filteredSugar.isEmpty()) {
                    item {
                        Text(
                            "No sugar logs found in this time frame.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    // Show all matching logs (newest first)
                    items(filteredSugar.reversed()) { log ->
                        RecentSugarItem(log = log, onDelete = { viewModel.deleteSugarLog(log) })
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyChartDisplay() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Waiting for more data... 📈\nBegin keeping entries to see a progress trend chart here.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun BPLineChart(readings: List<BloodPressureLog>) {
    // Collect coordinates metrics
    val maxSys = (readings.maxOfOrNull { it.systolic } ?: 150).toFloat().coerceAtLeast(140f)
    val minDia = (readings.minOfOrNull { it.diastolic } ?: 70).toFloat().coerceAtMost(60f)
    val valueRange = (maxSys - minDia).coerceAtLeast(40f)

    // Colors
    val sysLineColor = Color(0xFF1565C0) // Deep Blue
    val diaLineColor = Color(0xFF42A5F5) // Soft Light Blue

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val width = size.width
            val height = size.height

            // 1. Draw Grid lines and Y Axes indicator labels
            val dashSteps = 4
            for (i in 0..dashSteps) {
                val yVal = maxSys - (i * valueRange / dashSteps)
                val targetY = i * height / dashSteps
                
                // Horizontal guideline
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    start = Offset(0f, targetY),
                    end = Offset(width, targetY),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // 2. Map coordinates & draw Systolic and Diastolic paths
            if (readings.isNotEmpty()) {
                val sysPoints = mutableListOf<Offset>()
                val diaPoints = mutableListOf<Offset>()
                val stepX = width / (readings.size + 1).coerceAtLeast(2)

                readings.forEachIndexed { idx, point ->
                    val x = (idx + 1) * stepX
                    
                    // Systolic y
                    val sysPerc = (point.systolic - minDia) / valueRange
                    val sysY = height - (sysPerc * height)
                    sysPoints.add(Offset(x, sysY))

                    // Diastolic y
                    val diaPerc = (point.diastolic - minDia) / valueRange
                    val diaY = height - (diaPerc * height)
                    diaPoints.add(Offset(x, diaY))

                    // Draw singular circles
                    drawCircle(color = sysLineColor, radius = 5.dp.toPx(), center = Offset(x, sysY))
                    drawCircle(color = diaLineColor, radius = 5.dp.toPx(), center = Offset(x, diaY))
                }

                // Connect dots with paths
                val sysPath = Path().apply {
                    if (sysPoints.size > 0) {
                        moveTo(sysPoints[0].x, sysPoints[0].y)
                        for (i in 1 until sysPoints.size) {
                            lineTo(sysPoints[i].x, sysPoints[i].y)
                        }
                    }
                }
                val diaPath = Path().apply {
                    if (diaPoints.size > 0) {
                        moveTo(diaPoints[0].x, diaPoints[0].y)
                        for (i in 1 until diaPoints.size) {
                            lineTo(diaPoints[i].x, diaPoints[i].y)
                        }
                    }
                }

                drawPath(path = sysPath, color = sysLineColor, style = Stroke(width = 3.dp.toPx()))
                drawPath(path = diaPath, color = diaLineColor, style = Stroke(width = 3.dp.toPx()))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Custom Legend with Contrast info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(16.dp).background(sysLineColor, RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Systolic (Top number)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = sysLineColor)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(16.dp).background(diaLineColor, RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Diastolic (Bottom)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = diaLineColor)
            }
        }
    }
}

@Composable
fun SugarBarChart(readings: List<SugarLog>) {
    val maxSugar = (readings.maxOfOrNull { it.level } ?: 10.0).toFloat().coerceAtLeast(8.0f)
    val sugarRange = maxSugar.coerceAtLeast(5.0f)

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val width = size.width
            val height = size.height
            val stepWidth = width / (readings.size + 1).coerceAtLeast(2)

            // Horizontal Grid line
            drawLine(
                color = Color.LightGray.copy(alpha = 0.4f),
                start = Offset(0f, height * 0.5f),
                end = Offset(width, height * 0.5f),
                strokeWidth = 1.dp.toPx()
            )

            readings.forEachIndexed { idx, point ->
                val x = (idx + 1) * stepWidth
                val heightPerc = point.level.toFloat() / sugarRange
                val barHeight = heightPerc * height
                val y = height - barHeight

                // Determine bar color (Normal green vs High orange/red)
                val testColor = if (point.isBeforeMeal) {
                    if (point.level > 7.0 || point.level < 4.0) Color(0xFFD84315) else Color(0xFF2E7D32)
                } else {
                    if (point.level > 8.5 || point.level < 4.5) Color(0xFFD84315) else Color(0xFF2E7D32)
                }

                // Draw gorgeous bar
                drawRect(
                    color = testColor,
                    topLeft = Offset(x - 10.dp.toPx(), y),
                    size = Size(20.dp.toPx(), barHeight.coerceAtLeast(4f))
                )

                // Draw tiny meal badge indicators above the bar
                val badgeText = if (point.isBeforeMeal) "B" else "A"
                // Draw circle marker at top
                drawCircle(
                    color = if (point.isBeforeMeal) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    radius = 3.dp.toPx(),
                    center = Offset(x, y - 8.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Custom Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(16.dp).background(Color(0xFF2E7D32), RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Normal levels", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(16.dp).background(Color(0xFFD84315), RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Out of bound logs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84315))
            }
        }
    }
}
