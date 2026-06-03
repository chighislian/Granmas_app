package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(width = 1.dp, color = Color(0xFFF1F5F9)) // border-t border-slate-100
            .navigationBarsPadding() // respect bottom gestural bar safe areas
            .padding(top = 10.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Tab 1: Home (Dashboard)
        NavBarItem(
            emoji = "🏠",
            label = "Home",
            isActive = currentRoute == "dashboard",
            tag = "nav_home",
            onClick = { onNavigate("dashboard") }
        )

        // Tab 2: History
        NavBarItem(
            emoji = "📊",
            label = "History",
            isActive = currentRoute == "history",
            tag = "nav_history",
            onClick = { onNavigate("history") }
        )

        // Tab 3: Meds
        NavBarItem(
            emoji = "💊",
            label = "Meds",
            isActive = currentRoute == "medications",
            tag = "nav_medications",
            onClick = { onNavigate("medications") }
        )

        // Tab 4: SOS (Emergency)
        NavBarSOSItem(
            isActive = currentRoute == "emergency",
            onClick = { onNavigate("emergency") }
        )
    }
}

@Composable
fun RowScope.NavBarItem(
    emoji: String,
    label: String,
    isActive: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
            .alpha(if (isActive) 1f else 0.45f)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 24.sp)
            }
        } else {
            Box(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 24.sp)
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) Color(0xFF2E7D32) else Color(0xFF0F172A)
        )
    }
}

@Composable
fun RowScope.NavBarSOSItem(
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
            .testTag("nav_sos"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFFEBEE))
                .border(2.dp, Color(0xFFFFCDD2), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("🚨", fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "SOS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFDC2626) // red-600
        )
    }
}
