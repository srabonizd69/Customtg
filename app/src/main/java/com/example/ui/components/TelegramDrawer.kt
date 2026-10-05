package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.TelegramUser
import com.example.viewmodel.Screen

@Composable
fun TelegramDrawer(
    currentScreen: Screen,
    currentUser: TelegramUser,
    savedAccounts: List<TelegramUser>,
    darkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onNewSecretChat: () -> Unit,
    onNewGroup: () -> Unit,
    onNewChannel: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenSavedMessages: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenInspector: () -> Unit,
    onSwitchAccount: (TelegramUser) -> Unit,
    onAddAccount: () -> Unit,
    onLogoutClick: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    var accountsExpanded by remember { mutableStateOf(false) }

    val userBgColor = runCatching {
        Color(android.graphics.Color.parseColor(currentUser.avatarColorHex))
    }.getOrDefault(Color(0xFF229ED9))

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .width(310.dp)
            .fillMaxHeight()
            .testTag("telegram_drawer")
    ) {
        // User Profile Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(userBgColor)
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser.initials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }

                    IconButton(
                        onClick = { accountsExpanded = !accountsExpanded },
                        modifier = Modifier.testTag("toggle_accounts_dropdown")
                    ) {
                        Icon(
                            imageVector = if (accountsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Switch Account",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentUser.fullName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Text(
                    text = "${currentUser.phone}  •  @${currentUser.username}",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
        }

        // Expandable Account Switcher section
        AnimatedVisibility(visible = accountsExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(vertical = 4.dp)
            ) {
                savedAccounts.forEach { acc ->
                    val isCurrent = acc.id == currentUser.id
                    val accColor = runCatching { Color(android.graphics.Color.parseColor(acc.avatarColorHex)) }.getOrDefault(Color(0xFF229ED9))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSwitchAccount(acc)
                                accountsExpanded = false
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(accColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = acc.initials,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = acc.fullName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = acc.phone,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isCurrent) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = Color(0xFF229ED9),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Add Account row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            accountsExpanded = false
                            onCloseDrawer()
                            onAddAccount()
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("add_account_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Account",
                            tint = Color(0xFF229ED9),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Add Account",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF229ED9),
                        fontSize = 14.sp
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Navigation Menu Items
        DrawerItem(
            icon = Icons.AutoMirrored.Filled.Chat,
            title = "All Chats",
            isSelected = currentScreen == Screen.CHAT_LIST,
            onClick = {
                onNavigate(Screen.CHAT_LIST)
                onCloseDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.People,
            title = "Contacts",
            isSelected = currentScreen == Screen.CONTACTS,
            onClick = {
                onCloseDrawer()
                onOpenContacts()
            }
        )

        DrawerItem(
            icon = Icons.Default.Bookmark,
            title = "Saved Messages",
            onClick = {
                onCloseDrawer()
                onOpenSavedMessages()
            }
        )

        DrawerItem(
            icon = Icons.Default.Lock,
            title = "New Secret Chat",
            iconTint = Color(0xFF4FAE4E),
            onClick = {
                onCloseDrawer()
                onNewSecretChat()
            }
        )

        DrawerItem(
            icon = Icons.Default.Group,
            title = "New Group",
            onClick = {
                onCloseDrawer()
                onNewGroup()
            }
        )

        DrawerItem(
            icon = Icons.Default.Campaign,
            title = "New Channel",
            onClick = {
                onCloseDrawer()
                onNewChannel()
            }
        )

        DrawerItem(
            icon = Icons.Default.Notifications,
            title = "Notification Themes",
            isSelected = currentScreen == Screen.NOTIFICATION_THEMES,
            onClick = {
                onNavigate(Screen.NOTIFICATION_THEMES)
                onCloseDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.Security,
            title = "MTProto Protocol & Servers",
            isSelected = currentScreen == Screen.MTPROTO_SETTINGS,
            onClick = {
                onNavigate(Screen.MTPROTO_SETTINGS)
                onCloseDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.Settings,
            title = "Settings",
            isSelected = currentScreen == Screen.SETTINGS,
            onClick = {
                onCloseDrawer()
                onOpenSettings()
            }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // Quick MTProto Inspector Sheet toggle
        DrawerItem(
            icon = Icons.Default.Security,
            title = "Live Packet Inspector",
            onClick = {
                onCloseDrawer()
                onOpenInspector()
            }
        )

        // Dark Theme Switch
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = "Night Mode",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "Night Mode",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = darkTheme,
                onCheckedChange = { onToggleDarkTheme() }
            )
        }

        // Log Out button
        DrawerItem(
            icon = Icons.AutoMirrored.Filled.Logout,
            title = "Log Out",
            iconTint = Color(0xFFE53935),
            textColor = Color(0xFFE53935),
            onClick = {
                onCloseDrawer()
                onLogoutClick()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Footer version info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "TeleProto Messenger for Android",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "MTProto 2.0 • SHA-256 / AES-IGE Compliant",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    title: String,
    isSelected: Boolean = false,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textColor: Color? = null,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val effectiveTextColor = textColor ?: if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .background(bgColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = effectiveTextColor
        )
    }
}
