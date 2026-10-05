package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CountryData
import com.example.data.model.CountryInfo
import com.example.ui.components.CountryPickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (phone: String, firstName: String, lastName: String) -> Unit,
    onCancel: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Phone, 1: QR Code
    var loginStep by remember { mutableIntStateOf(1) } // 1: Enter Phone, 2: Enter Code

    val defaultCountry = remember {
        CountryData.ALL_COUNTRIES.firstOrNull { it.code == "+880" } ?: CountryData.ALL_COUNTRIES.first()
    }
    var selectedCountry by remember { mutableStateOf(defaultCountry) }
    var countryCodeInput by remember { mutableStateOf(defaultCountry.code) }
    var showCountryPicker by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("1712 345678") }
    var verificationCode by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    CountryPickerDialog(
        visible = showCountryPicker,
        onDismiss = { showCountryPicker = false },
        onSelectCountry = { country ->
            selectedCountry = country
            countryCodeInput = country.code
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (loginStep == 2) {
                        IconButton(onClick = { loginStep = 1 }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else if (onCancel != null) {
                        IconButton(onClick = onCancel) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancel")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .testTag("login_screen")
        ) {
            // Telegram Logo Header
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(Color(0xFF229ED9), CircleShape)
                    .border(3.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Telegram Logo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "TeleProto Messenger",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Official MTProto 2.0 Encrypted Network",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tab Selector (Phone Login vs QR Code)
            if (loginStep == 1) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Phone Number", fontSize = 13.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Quick QR Login", fontSize = 13.sp)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            AnimatedContent(
                targetState = if (selectedTab == 1 && loginStep == 1) 3 else loginStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "login_flow_transition"
            ) { step ->
                when (step) {
                    1 -> {
                        // Phone Number Input Step
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Please confirm your country code and enter your phone number.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Country Selector (Tappable field opening search picker)
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = "${selectedCountry.flag}  ${selectedCountry.name}",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Country") },
                                    trailingIcon = {
                                        TextButton(onClick = { showCountryPicker = true }) {
                                            Text("Change")
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showCountryPicker = true },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Phone Number Field with manually editable country code
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = countryCodeInput,
                                    onValueChange = { input ->
                                        // Allow typing country code manually
                                        val digits = input.filter { it.isDigit() }
                                        val formatted = if (input.startsWith("+")) {
                                            "+" + digits
                                        } else if (digits.isNotEmpty()) {
                                            "+" + digits
                                        } else {
                                            "+"
                                        }
                                        if (formatted.length <= 6) {
                                            countryCodeInput = formatted
                                            val matched = CountryData.findByCode(formatted)
                                            if (matched != null) {
                                                selectedCountry = matched
                                            } else if (formatted.length > 1) {
                                                selectedCountry = CountryInfo("Custom / Unknown", formatted, "🌐")
                                            }
                                        }
                                    },
                                    label = { Text("Code") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.width(100.dp),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { phoneNumber = it },
                                    label = { Text("Phone Number") },
                                    placeholder = { Text("1712 345678") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("login_phone_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            // Next Button
                            Button(
                                onClick = {
                                    if (phoneNumber.isNotBlank()) {
                                        loginStep = 2
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF229ED9)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("login_next_button")
                            ) {
                                Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Quick Demo Logins
                            OutlinedButton(
                                onClick = {
                                    onLoginSuccess("+880 1712-345678", "Rahim", "Chowdhury")
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("fast_demo_bd_login_button")
                            ) {
                                Text("🇧🇩 Quick Demo Login: Bangladesh (+880)", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    onLoginSuccess("+1 (555) 789-2048", "Alex", "Rivers")
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("fast_demo_login_button")
                            ) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFFFF9800))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("⚡ Quick Demo Login: Alex Rivers (+1)", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    2 -> {
                        // Verification Code Step
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "We've sent the code to Telegram",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "$countryCodeInput $phoneNumber",
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit phone",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { loginStep = 1 }
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Verification Code Input
                            OutlinedTextField(
                                value = verificationCode,
                                onValueChange = {
                                    if (it.length <= 5) verificationCode = it
                                },
                                label = { Text("5-Digit Code") },
                                placeholder = { Text("2 2 9 3 8") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.headlineSmall.copy(
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 8.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .testTag("login_code_input")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Autofill code helper chip
                            Button(
                                onClick = {
                                    verificationCode = "22938"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                contentPadding = ButtonDefaults.TextButtonContentPadding,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Autofill Demo Code: 22938", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    isSubmitting = true
                                    val fullPhone = "$countryCodeInput $phoneNumber"
                                    onLoginSuccess(fullPhone, "Telegram", "User")
                                },
                                enabled = verificationCode.length >= 3 && !isSubmitting,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF229ED9)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("login_submit_code_button")
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Sign In & Connect MTProto", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Haven't received the code? Send via SMS in 0:45",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    3 -> {
                        // QR Code Login Step
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Scan from Mobile Telegram",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "1. Open Telegram on your phone\n2. Go to Settings > Devices > Link Desktop\n3. Point your camera at this code",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Stylized QR Matrix Card
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier
                                    .size(200.dp)
                                    .border(2.dp, Color(0xFF229ED9), RoundedCornerShape(20.dp))
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    // Visual QR Pattern
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "██ ▀ ▄ ██ █ ▀\n█ █ █ █ ▄ █ ▄\n██ ▄ ▀ ██ █ █\n▄ ▀ █ ▄ ▀ █ ▀\n██ ▀ ▄ ██ ▄ █",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 18.sp,
                                            lineHeight = 18.sp,
                                            color = Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    // Center Telegram icon
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFF229ED9), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    onLoginSuccess("+1 (555) 789-2048", "Alex", "Rivers")
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF229ED9)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("simulate_qr_scan_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Simulate QR Scan & Authorize")
                            }
                        }
                    }
                }
            }
        }
    }
}
