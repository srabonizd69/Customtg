package com.example.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationThemesScreen(
    activeTheme: NotificationTheme,
    onBack: () -> Unit,
    onSelectTheme: (NotificationTheme) -> Unit,
    onTestNotification: (NotificationTheme) -> Unit
) {
    var currentEditingTheme by remember { mutableStateOf(activeTheme) }

    val ledColorOptions = listOf(
        "#0088CC" to "Cyan",
        "#00E676" to "Emerald",
        "#FF007F" to "Magenta",
        "#FFB300" to "Amber",
        "#7C4DFF" to "Violet",
        "#FF1744" to "Crimson"
    )

    val vibrationOptions = listOf("Double Pulse", "Heartbeat", "Morse SOS", "Gentle Tick", "Single Long")
    val bannerStyleOptions = listOf(
        "DYNAMIC_CAPSULE" to "Dynamic Capsule",
        "FULL_BANNER" to "Full Glass Banner",
        "MINIMAL_PILL" to "Minimal Stealth Pill"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        text = "Custom Notification Themes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("notification_themes_screen")
        ) {
            // Intro
            Text(
                text = "Personalize how notifications appear and feel across different types of Telegram chats (Direct, Secret, and Channels).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Presets
            Text(
                text = "Official Theme Presets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            NotificationTheme.PRESETS.forEach { theme ->
                val isSelected = currentEditingTheme.id == theme.id
                val themeColor = runCatching { Color(android.graphics.Color.parseColor(theme.previewColorHex)) }.getOrDefault(Color(0xFF229ED9))
                val ledColor = runCatching { Color(android.graphics.Color.parseColor(theme.ledColorHex)) }.getOrDefault(Color(0xFF229ED9))

                Card(
                    onClick = {
                        currentEditingTheme = theme
                        onSelectTheme(theme)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("theme_preset_${theme.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(themeColor.copy(alpha = 0.2f), CircleShape)
                                .border(2.dp, ledColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(ledColor, CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = theme.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (theme.id == "secret_stealth") {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Encrypted",
                                        tint = Color(0xFF4FAE4E),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = theme.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🔊 ${theme.soundName}  •  📳 ${theme.vibrationPatternName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = Color(0xFF229ED9)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Test Notification Action
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Live Theme Experience",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Test the system notification channel, LED flash, and in-app heads-up banner for '${currentEditingTheme.name}'.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onTestNotification(currentEditingTheme) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF229ED9)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trigger_test_notification_button")
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Trigger Test Notification")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Customize Theme
            Text(
                text = "Customize Active Theme Elements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            // LED Color
            Text("Notification LED / Edge Light Color:", style = MaterialTheme.typography.labelMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                ledColorOptions.forEach { (hex, name) ->
                    val color = Color(android.graphics.Color.parseColor(hex))
                    val isPicked = currentEditingTheme.ledColorHex == hex
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(color, CircleShape)
                            .border(
                                width = if (isPicked) 3.dp else 1.dp,
                                color = if (isPicked) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                currentEditingTheme = currentEditingTheme.copy(ledColorHex = hex)
                                onSelectTheme(currentEditingTheme)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPicked) {
                            Icon(Icons.Default.Check, contentDescription = name, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Banner Style
            Text("In-App Notification Banner Style:", style = MaterialTheme.typography.labelMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                bannerStyleOptions.forEach { (styleKey, label) ->
                    FilterChip(
                        selected = currentEditingTheme.bannerStyle == styleKey,
                        onClick = {
                            currentEditingTheme = currentEditingTheme.copy(bannerStyle = styleKey)
                            onSelectTheme(currentEditingTheme)
                        },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vibration pattern
            Text("Vibration / Haptic Pattern:", style = MaterialTheme.typography.labelMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                vibrationOptions.take(3).forEach { pattern ->
                    FilterChip(
                        selected = currentEditingTheme.vibrationPatternName == pattern,
                        onClick = {
                            currentEditingTheme = currentEditingTheme.copy(vibrationPatternName = pattern)
                            onSelectTheme(currentEditingTheme)
                        },
                        label = { Text(pattern, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
