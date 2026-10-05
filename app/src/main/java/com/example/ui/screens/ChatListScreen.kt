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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatEntity
import com.example.data.model.MTProtoSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    chats: List<ChatEntity>,
    session: MTProtoSession,
    packetCount: Int,
    onOpenDrawer: () -> Unit,
    onSelectChat: (ChatEntity) -> Unit,
    onOpenInspector: () -> Unit,
    onOpenNotificationThemes: () -> Unit,
    onNewSecretChat: () -> Unit,
    onNewGroup: () -> Unit,
    onNewChannel: () -> Unit
) {
    var selectedFolderIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showFabMenu by remember { mutableStateOf(false) }

    val folders = listOf("All", "Secret 🔒", "Bots 🤖", "Channels 📢")

    val filteredChats = chats.filter { chat ->
        val matchesFolder = when (selectedFolderIndex) {
            1 -> chat.isSecretChat
            2 -> chat.isBot
            3 -> chat.isChannel
            else -> true
        }
        val matchesSearch = if (searchQuery.isBlank()) true else {
            chat.title.contains(searchQuery, ignoreCase = true) ||
            chat.username.contains(searchQuery, ignoreCase = true) ||
            chat.lastMessage.contains(searchQuery, ignoreCase = true)
        }
        matchesFolder && matchesSearch
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "TeleProto",
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp
                            )
                            Text(
                                text = "DC${session.serverDc} • MTProto 2.0 Encrypted",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onOpenDrawer, modifier = Modifier.testTag("open_drawer_button")) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }

                        IconButton(onClick = onOpenInspector, modifier = Modifier.testTag("open_inspector_button")) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = Color(0xFF229ED9)) {
                                        Text(packetCount.toString(), fontSize = 9.sp)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Protocol Inspector",
                                    tint = Color(0xFF229ED9)
                                )
                            }
                        }

                        IconButton(onClick = onOpenNotificationThemes, modifier = Modifier.testTag("open_notifications_button")) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notification Themes"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Search field if active
                if (isSearchActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search chats, messages, @users...") },
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("chat_search_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }

                // Telegram Folder Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedFolderIndex,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Color(0xFF229ED9),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedFolderIndex]),
                            color = Color(0xFF229ED9)
                        )
                    }
                ) {
                    folders.forEachIndexed { index, folderName ->
                        Tab(
                            selected = selectedFolderIndex == index,
                            onClick = { selectedFolderIndex = index },
                            text = {
                                Text(
                                    text = folderName,
                                    fontWeight = if (selectedFolderIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = { showFabMenu = true },
                    containerColor = Color(0xFF229ED9),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.testTag("fab_new_chat_menu")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "New Message",
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = showFabMenu,
                    onDismissRequest = { showFabMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("New Secret Chat") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF4FAE4E)) },
                        onClick = {
                            showFabMenu = false
                            onNewSecretChat()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("New Group") },
                        leadingIcon = { Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color(0xFF229ED9)) },
                        onClick = {
                            showFabMenu = false
                            onNewGroup()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("New Channel") },
                        leadingIcon = { Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFFF9800)) },
                        onClick = {
                            showFabMenu = false
                            onNewChannel()
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("chat_list_view")
        ) {
            items(filteredChats, key = { it.id }) { chat ->
                ChatItem(chat = chat, onClick = { onSelectChat(chat) })
            }

            if (filteredChats.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No chats found in this folder",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatItem(
    chat: ChatEntity,
    onClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val avatarBg = runCatching { Color(android.graphics.Color.parseColor(chat.avatarColorHex)) }.getOrDefault(Color(0xFF229ED9))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("chat_item_${chat.id}")
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(avatarBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            when {
                chat.isSavedMessages -> Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Saved Messages",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
                chat.isSecretChat -> Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Secret",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
                chat.isBot -> Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Bot",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
                chat.isChannel -> Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = "Channel",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
                chat.isGroup -> Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Group",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
                else -> Text(
                    text = chat.title.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (chat.isSecretChat) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secret",
                            tint = Color(0xFF4FAE4E),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = chat.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (chat.isSecretChat) Color(0xFF4FAE4E) else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = timeFormat.format(Date(chat.lastMessageTimestamp)),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (chat.isSecretChat) {
                        Text(
                            text = "🔒 ",
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = chat.lastMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (chat.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (chat.isSecretChat) Color(0xFF4FAE4E) else Color(0xFF229ED9),
                                shape = CircleShape
                            )
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chat.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
