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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TelegramUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    user: TelegramUser,
    darkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onBack: () -> Unit,
    onOpenNotificationThemes: () -> Unit,
    onOpenMTProtoSettings: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val userColor = runCatching { Color(android.graphics.Color.parseColor(user.avatarColorHex)) }.getOrDefault(Color(0xFF229ED9))

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .testTag("settings_screen")
        ) {
            // User Profile Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(userColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.initials,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFF229ED9), CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = user.fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                        Text(
                            text = user.phone,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "@${user.username}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Account details section
            SectionHeader(title = "Account")

            SettingsRow(
                title = user.phone,
                subtitle = "Tap to change phone number",
                onClick = {}
            )
            SettingsRow(
                title = "@${user.username}",
                subtitle = "Username",
                onClick = {}
            )
            SettingsRow(
                title = user.bio,
                subtitle = "Bio",
                onClick = {}
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 6.dp))

            // Settings Section
            SectionHeader(title = "Settings")

            SettingsActionRow(
                icon = Icons.Default.Notifications,
                title = "Notifications and Sounds",
                subtitle = "Custom LED, Haptics & Banners",
                iconColor = Color(0xFFE53935),
                onClick = onOpenNotificationThemes
            )

            SettingsActionRow(
                icon = Icons.Default.Security,
                title = "MTProto Protocol & Servers",
                subtitle = "Data Centers, AuthKey, TLS Proxy",
                iconColor = Color(0xFF229ED9),
                onClick = onOpenMTProtoSettings
            )

            SettingsActionRow(
                icon = Icons.Default.Lock,
                title = "Privacy and Security",
                subtitle = "Passcode, 2-Step Verification",
                iconColor = Color(0xFF4FAE4E),
                onClick = {}
            )

            SettingsActionRow(
                icon = Icons.Default.DataUsage,
                title = "Data and Storage",
                subtitle = "Network usage, Cache storage",
                iconColor = Color(0xFF00B0FF),
                onClick = {}
            )

            SettingsActionRow(
                icon = Icons.Default.Psychology,
                title = "Gemini AI Intelligence",
                subtitle = "High Thinking, Google Search Grounding",
                iconColor = Color(0xFFE040FB),
                onClick = {}
            )

            // Night Mode Switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleDarkTheme() }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF7C4DFF).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Night Mode", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("Switch between Telegram Dark and Light theme", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = darkTheme, onCheckedChange = { onToggleDarkTheme() })
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 6.dp))

            // Logout row
            SettingsActionRow(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "Log Out",
                subtitle = "End active Telegram session on this device",
                iconColor = Color(0xFFE53935),
                textColor = Color(0xFFE53935),
                onClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsRow(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    textColor: Color? = null,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = textColor ?: MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
