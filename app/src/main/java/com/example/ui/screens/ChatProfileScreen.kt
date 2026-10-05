package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatProfileScreen(
    chat: ChatEntity,
    onBack: () -> Unit,
    onStartSecretChat: () -> Unit,
    onOpenFingerprint: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedMediaTab by remember { mutableIntStateOf(0) }
    val avatarBg = runCatching { Color(android.graphics.Color.parseColor(chat.avatarColorHex)) }.getOrDefault(Color(0xFF229ED9))

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {},
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }
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
                .testTag("chat_profile_screen")
        ) {
            // Profile Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(avatarBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (chat.isSecretChat) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                        } else {
                            Text(
                                text = chat.title.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    Column {
                        Text(
                            text = chat.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = if (chat.isSecretChat) Color(0xFF4FAE4E) else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (chat.isSecretChat) "🔒 End-to-End Encrypted" else if (chat.isBot) "Bot service" else if (chat.isChannel) "Channel" else "online",
                            fontSize = 13.sp,
                            color = if (chat.isSecretChat) Color(0xFF4FAE4E) else Color(0xFF229ED9)
                        )
                    }
                }
            }

            // Quick Contact Action Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                ProfileActionButton(icon = Icons.AutoMirrored.Filled.Chat, label = "Message", onClick = onBack)
                ProfileActionButton(icon = Icons.Default.Call, label = "Call", onClick = {})
                if (chat.isSecretChat) {
                    ProfileActionButton(icon = Icons.Default.Lock, label = "Key Hash", color = Color(0xFF4FAE4E), onClick = onOpenFingerprint)
                } else {
                    ProfileActionButton(icon = Icons.Default.Lock, label = "Secret Chat", color = Color(0xFF4FAE4E), onClick = onStartSecretChat)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Info Section
            Text(
                text = "Info",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = chat.username, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(text = "Username", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (chat.bio.isNotBlank()) chat.bio else "Official Telegram verified contact on MTProto DC${chat.dcId}.",
                    fontSize = 14.sp
                )
                Text(text = "Bio", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Notifications Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Notifications", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("Sound, vibration and LED", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))

            // Shared Media Tabs
            TabRow(
                selectedTabIndex = selectedMediaTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Media", "Files", "Voice", "Links").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedMediaTab == index,
                        onClick = { selectedMediaTab = index },
                        text = { Text(title, fontSize = 12.sp) }
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Text(
                    text = "No shared media yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Danger actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFE53935))
                Spacer(modifier = Modifier.width(16.dp))
                Text("Block User", color = Color(0xFFE53935), fontWeight = FontWeight.SemiBold)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFE53935))
                Spacer(modifier = Modifier.width(16.dp))
                Text("Delete Chat", color = Color(0xFFE53935), fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ProfileActionButton(
    icon: ImageVector,
    label: String,
    color: Color = Color(0xFF229ED9),
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(color.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = color)
    }
}
