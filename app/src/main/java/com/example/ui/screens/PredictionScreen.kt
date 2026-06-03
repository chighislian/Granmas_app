package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionScreen(
    viewModel: HealthViewModel,
    onBack: () -> Unit
) {
    val warnings by viewModel.predictionWarnings.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Sickness Prediction 🔮",
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
            // 1. HELP EXPLANATION CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Help icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "How do predictions work?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Gran, this screen reviews your local logs from previous weeks. It checks if headaches or fatigue usually appear after days with high activities, skipped pills, or blood pressure changes, so we can suggest extra care before you feel unwell! 🌸",
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 26.sp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // 2. ACTIVE WARNINGS TITLE
            item {
                Text(
                    "Safety Warnings & Active Patterns",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // 3. SHOW PREDICTIONS
            if (warnings.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            "Calculating health history parameters. Please log some readings to display patterns.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            } else {
                items(warnings) { item ->
                    val isActionable = item.startsWith("⚠️")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("prediction_warning_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActionable) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = if (isActionable) 2.dp else 1.dp,
                            color = if (isActionable) Color(0xFFFFB74D) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (isActionable) Icons.Filled.Shield else Icons.Filled.Lightbulb,
                                contentDescription = "Alert Symbol",
                                tint = if (isActionable) Color(0xFFE65100) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 19.sp,
                                    lineHeight = 28.sp,
                                    fontWeight = if (isActionable) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                color = if (isActionable) Color(0xFF5D4037) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 4. HEARTWARMING ADVICE ROW FOR SENIORS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Gran's Healthy Living Motto ❤️",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val mottos = listOf(
                            "🌸 Rest is as important as activity. Pace yourself!",
                            "🥤 Drink plenty of warm water today.",
                            "💊 Taking medications at the same time daily keeps you steady.",
                            "🧘‍♀️ 10 deep, calming breaths can naturally soothe stress and BP.",
                            "💤 A 20-minute afternoon nap keeps dizziness away."
                        )

                        mottos.forEach { motto ->
                            Text(
                                motto,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 26.sp
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
