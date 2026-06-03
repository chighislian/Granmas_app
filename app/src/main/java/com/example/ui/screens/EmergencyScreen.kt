package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.EmergencyInfo
import com.example.ui.HealthViewModel
import com.example.ui.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    viewModel: HealthViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val emergencyInfoState by viewModel.emergencyInfo.collectAsStateWithLifecycle()
    val emergencyInfo = emergencyInfoState ?: com.example.data.EmergencyInfo()
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }

    // Form inputs for editing
    var nameInput by remember { mutableStateOf("") }
    var codInput by remember { mutableStateOf("") }
    var medInput by remember { mutableStateOf("") }
    var docNameInput by remember { mutableStateOf("") }
    var docPhoneInput by remember { mutableStateOf("") }
    var contactNameInput by remember { mutableStateOf("") }
    var contactPhoneInput by remember { mutableStateOf("") }

    // Populate editing fields when editing completes
    LaunchedEffect(isEditing, emergencyInfo) {
        if (isEditing) {
            nameInput = emergencyInfo.fullName
            codInput = emergencyInfo.conditions
            medInput = emergencyInfo.medications
            docNameInput = emergencyInfo.doctorName
            docPhoneInput = emergencyInfo.doctorPhone
            contactNameInput = emergencyInfo.contactName
            contactPhoneInput = emergencyInfo.contactPhone
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Emergency Help Card 🚨",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Go Back",
                            tint = Color(0xFFDC2626)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isEditing) {
                                // Save
                                viewModel.saveEmergencyInfo(
                                    EmergencyInfo(
                                        fullName = nameInput.trim(),
                                        conditions = codInput.trim(),
                                        medications = medInput.trim(),
                                        doctorName = docNameInput.trim(),
                                        doctorPhone = docPhoneInput.trim(),
                                        contactName = contactNameInput.trim(),
                                        contactPhone = contactPhoneInput.trim()
                                    )
                                )
                                isEditing = false
                            } else {
                                isEditing = true
                            }
                        },
                        modifier = Modifier.testTag("emergency_edit_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Filled.Save else Icons.Filled.Edit,
                            contentDescription = if (isEditing) "Save profile" else "Edit details",
                            tint = Color(0xFFDC2626)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7FAF7)
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = "emergency",
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
            contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
        ) {
            if (isEditing) {
                // EDIT MODE FORM
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        shape = RoundedCornerShape(40.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Modify Emergency Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )

                            // Name
                            Column {
                                Text("Patient Full Name", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it },
                                    modifier = Modifier.fillMaxWidth().testTag("edit_fullName_field"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF1B5E20),
                                        unfocusedBorderColor = Color(0xFFE8F5E9)
                                    )
                                )
                            }

                            // Conditions
                            Column {
                                Text("Medical Conditions", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = codInput,
                                    onValueChange = { codInput = it },
                                    modifier = Modifier.fillMaxWidth().testTag("edit_conditions_field"),
                                    maxLines = 3,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF1B5E20),
                                        unfocusedBorderColor = Color(0xFFE8F5E9)
                                    )
                                )
                            }

                            // Medications
                            Column {
                                Text("Adhered Medications", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = medInput,
                                    onValueChange = { medInput = it },
                                    modifier = Modifier.fillMaxWidth().testTag("edit_medications_field"),
                                    maxLines = 3,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF1B5E20),
                                        unfocusedBorderColor = Color(0xFFE8F5E9)
                                    )
                                )
                            }

                            // Doctor Name
                            Column {
                                Text("Primary Doctor Name", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = docNameInput,
                                    onValueChange = { docNameInput = it },
                                    modifier = Modifier.fillMaxWidth().testTag("edit_doctorName_field"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF1B5E20),
                                        unfocusedBorderColor = Color(0xFFE8F5E9)
                                    )
                                )
                            }

                            // Doctor Phone
                            Column {
                                Text("Doctor Phone Number", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = docPhoneInput,
                                    onValueChange = { docPhoneInput = it },
                                    modifier = Modifier.fillMaxWidth().testTag("edit_doctorPhone_field"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF1B5E20),
                                        unfocusedBorderColor = Color(0xFFE8F5E9)
                                    )
                                )
                            }

                            // Contact Name
                            Column {
                                Text("Emergency Contact Label", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = contactNameInput,
                                    onValueChange = { contactNameInput = it },
                                    modifier = Modifier.fillMaxWidth().testTag("edit_contactName_field"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF1B5E20),
                                        unfocusedBorderColor = Color(0xFFE8F5E9)
                                    )
                                )
                            }

                            // Contact Phone
                            Column {
                                Text("Emergency Contact Phone", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Spacer(Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = contactPhoneInput,
                                    onValueChange = { contactPhoneInput = it },
                                    modifier = Modifier.fillMaxWidth().testTag("edit_contactPhone_field"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF1B5E20),
                                        unfocusedBorderColor = Color(0xFFE8F5E9)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    viewModel.saveEmergencyInfo(
                                        EmergencyInfo(
                                            fullName = nameInput.trim(),
                                            conditions = codInput.trim(),
                                            medications = medInput.trim(),
                                            doctorName = docNameInput.trim(),
                                            doctorPhone = docPhoneInput.trim(),
                                            contactName = contactNameInput.trim(),
                                            contactPhone = contactPhoneInput.trim()
                                        )
                                    )
                                    isEditing = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("save_emergency_form_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Save Emergency Record ✅", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }
                }
            } else {
                // DISPLAY MODE
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = emergencyInfo.fullName.ifEmpty { "Mary" },
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFDC2626),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gran's Secure Emergency Card",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF0F172A).copy(alpha = 0.6f)
                        )
                    }
                }

                // 2. HEALTH SPECIFICS CARD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(40.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(2.dp, Color(0xFFFFCDD2)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Conditions Row
                            Column {
                                Text(
                                    "🚨 CHRONIC HEALTH CONDITIONS:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFDC2626)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = emergencyInfo.conditions.ifEmpty { "Diabetes (sugar), high blood pressure (BP)" },
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.testTag("display_conditions")
                                )
                            }

                            HorizontalDivider(color = Color(0xFFE8F5E9), thickness = 1.dp)

                            // Medications Row
                            Column {
                                Text(
                                    "💊 ACTIVE DAILY MEDICATIONS:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF1B5E20)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = emergencyInfo.medications.ifEmpty { "Metformin (500mg), Ramipril (5mg)" },
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, lineHeight = 26.sp),
                                    color = Color(0xFF475569),
                                    modifier = Modifier.testTag("display_medications")
                                )
                            }
                        }
                    }
                }

                // 3. CONTACT KEYS (DOCTOR & FAMILY SOS WITH ONE-CLICK ACTION)
                item {
                    Text(
                        "Tappable Emergency Contacts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Doctor Call Card
                item {
                    EmergencyCallRow(
                        contactLabel = "Primary Care Physician",
                        nameValue = emergencyInfo.doctorName.ifEmpty { "Dr. Smith" },
                        phoneValue = emergencyInfo.doctorPhone.ifEmpty { "555-0199" },
                        tag = "call_doctor_btn",
                        onCallClick = {
                            val phone = emergencyInfo.doctorPhone.ifEmpty { "555-0199" }
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        }
                    )
                }

                // Family Contact Call Card
                item {
                    EmergencyCallRow(
                        contactLabel = "Family emergency contact",
                        nameValue = emergencyInfo.contactName.ifEmpty { "Child / Spouse" },
                        phoneValue = emergencyInfo.contactPhone.ifEmpty { "555-0122" },
                        tag = "call_family_btn",
                        onCallClick = {
                            val phone = emergencyInfo.contactPhone.ifEmpty { "555-0122" }
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        }
                    )
                }

                // Friendly advice for responders
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(32.dp),
                        border = BorderStroke(2.dp, Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Text(
                            text = "First Responders: All listed clinical conditions, doctor telephone hotkeys, and family identities on this device are accessible locally without need for password entry or active internet connection.",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyCallRow(
    contactLabel: String,
    nameValue: String,
    phoneValue: String,
    tag: String,
    onCallClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contactLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                    color = Color(0xFF1B5E20)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = nameValue,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Phone: $phoneValue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            }

            // Big Dial Button
            Button(
                onClick = onCallClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)), // red-600
                modifier = Modifier
                    .size(56.dp)
                    .testTag(tag),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Contact call dialer",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
