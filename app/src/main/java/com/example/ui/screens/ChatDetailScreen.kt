package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatEntity
import com.example.data.model.MessageEntity
import com.example.data.model.NotificationTheme
import com.example.ui.components.AttachmentSheet
import com.example.ui.components.StickerEmojiPickerSheet
import com.example.ui.components.TelegramChatBackground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chat: ChatEntity,
    messages: List<MessageEntity>,
    isAiGenerating: Boolean,
    enableHighThinking: Boolean,
    enableSearchGrounding: Boolean,
    botRole: String,
    smartReplies: List<String>,
    isRecordingVoice: Boolean,
    voiceDurationSec: Int,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSendMediaMessage: (text: String, mediaType: String, stickerEmoji: String?) -> Unit,
    onOpenChatProfile: () -> Unit,
    onToggleHighThinking: () -> Unit,
    onToggleSearchGrounding: () -> Unit,
    onSelectBotRole: (String) -> Unit,
    onOpenFingerprint: () -> Unit,
    onOpenSelfDestruct: () -> Unit,
    onSummarizeChat: () -> Unit,
    onStartVoiceRecord: () -> Unit,
    onStopVoiceRecord: () -> Unit,
    onCancelVoiceRecord: () -> Unit,
    onReactToMessage: (String, String) -> Unit,
    onSelectNotificationTheme: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showThemeMenu by remember { mutableStateOf(false) }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showStickerSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(onClick = onOpenChatProfile)
                            .testTag("chat_header_profile_click")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    runCatching { Color(android.graphics.Color.parseColor(chat.avatarColorHex)) }.getOrDefault(Color(0xFF229ED9)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (chat.isSecretChat) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            } else if (chat.isBot) {
                                Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text(
                                    text = chat.title.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (chat.isSecretChat) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Secret",
                                        tint = Color(0xFF4FAE4E),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = chat.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (chat.isSecretChat) Color(0xFF4FAE4E) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = when {
                                    chat.isSecretChat -> "🔒 Secret Chat • ${if (chat.selfDestructSeconds > 0) "${chat.selfDestructSeconds}s timer" else "timer off"}"
                                    chat.isBot -> "Bot • ${if (enableHighThinking) "High Thinking (Pro)" else if (enableSearchGrounding) "Google Search Grounded" else "Gemini Flash"}"
                                    chat.isChannel -> "Official Channel"
                                    chat.isGroup -> "Group • ${if (chat.memberCount > 0) "${chat.memberCount} members" else "1,842 members"}"
                                    chat.isSavedMessages -> "Cloud Storage"
                                    else -> "online"
                                },
                                fontSize = 11.sp,
                                color = if (chat.isSecretChat) Color(0xFF4FAE4E).copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (chat.isSecretChat) {
                        IconButton(onClick = onOpenFingerprint, modifier = Modifier.testTag("chat_fingerprint_button")) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Visual Key", tint = Color(0xFF4FAE4E))
                        }
                        IconButton(onClick = onOpenSelfDestruct, modifier = Modifier.testTag("chat_timer_button")) {
                            Icon(imageVector = Icons.Default.Timer, contentDescription = "Timer", tint = Color(0xFF4FAE4E))
                        }
                    }

                    if (chat.isBot) {
                        IconButton(onClick = onToggleHighThinking, modifier = Modifier.testTag("toggle_high_thinking_button")) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "High Thinking",
                                tint = if (enableHighThinking) Color(0xFFE040FB) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onToggleSearchGrounding, modifier = Modifier.testTag("toggle_search_button")) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Grounding",
                                tint = if (enableSearchGrounding) Color(0xFF229ED9) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onSummarizeChat, modifier = Modifier.testTag("summarize_chat_button")) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Summarize Chat")
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("View Profile / Info") },
                                onClick = {
                                    showMenu = false
                                    onOpenChatProfile()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Custom Notification Theme") },
                                onClick = {
                                    showMenu = false
                                    showThemeMenu = true
                                },
                                leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null) }
                            )
                            if (chat.isBot) {
                                DropdownMenuItem(
                                    text = { Text("Role: MTProto Security") },
                                    onClick = {
                                        onSelectBotRole("MTProto Security Analyst")
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Role: Executive Assistant") },
                                    onClick = {
                                        onSelectBotRole("Executive Assistant")
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Role: Tech & Code") },
                                    onClick = {
                                        onSelectBotRole("Tech & Code")
                                        showMenu = false
                                    }
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showThemeMenu,
                            onDismissRequest = { showThemeMenu = false }
                        ) {
                            NotificationTheme.PRESETS.forEach { theme ->
                                DropdownMenuItem(
                                    text = { Text(theme.name) },
                                    onClick = {
                                        onSelectNotificationTheme(theme.id)
                                        showThemeMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Smart Quick Reply suggestions
                if (smartReplies.isNotEmpty() && !chat.isChannel) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(smartReplies) { reply ->
                            FilterChip(
                                selected = false,
                                onClick = { onSendMessage(reply) },
                                label = { Text(reply, fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF229ED9),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                // AI Generating progress indicator
                AnimatedVisibility(visible = isAiGenerating) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            color = if (enableHighThinking) Color(0xFFE040FB) else Color(0xFF229ED9),
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (enableHighThinking) "Gemini reasoning with High Thinking depth..." else if (enableSearchGrounding) "Searching Google live sources..." else "Gemini is typing...",
                            fontSize = 12.sp,
                            color = if (enableHighThinking) Color(0xFFE040FB) else Color(0xFF229ED9),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }

                // Input bar or Voice Recording bar
                if (isRecordingVoice) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color.Red, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Recording audio... ${voiceDurationSec}s",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Row {
                            IconButton(onClick = onCancelVoiceRecord) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(
                                onClick = onStopVoiceRecord,
                                modifier = Modifier
                                    .background(Color(0xFF229ED9), CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Voice", tint = Color.White)
                            }
                        }
                    }
                } else if (!chat.isChannel) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        // Emoji & Sticker button
                        IconButton(onClick = { showStickerSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.Mood,
                                contentDescription = "Emoji & Stickers",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text(if (chat.isSecretChat) "Encrypted message..." else "Message...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_message_input"),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )

                        // Attachment button
                        IconButton(onClick = { showAttachmentSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Attach",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        if (inputText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    val textToSend = inputText.trim()
                                    inputText = ""
                                    onSendMessage(textToSend)
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        if (chat.isSecretChat) Color(0xFF4FAE4E) else Color(0xFF229ED9),
                                        CircleShape
                                    )
                                    .testTag("send_message_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = onStartVoiceRecord,
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                    .testTag("record_voice_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Note",
                                    tint = Color(0xFF229ED9),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Only channel administrators can post messages.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Authentic Telegram Wallpaper Background
        TelegramChatBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("messages_list_view")
            ) {
                // Telegram Date Badge ("Today")
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.35f)
                        ) {
                            Text(
                                text = "Today",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    MessageBubble(
                        message = msg,
                        isSecretChat = chat.isSecretChat,
                        onReact = { emoji -> onReactToMessage(msg.id, emoji) }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }

    // Attachment Sheet
    if (showAttachmentSheet) {
        AttachmentSheet(
            onSendPhoto = { onSendMediaMessage("📷 Photo (1920x1080) [MTProto Encrypted JPEG]", "photo", null) },
            onSendFile = { onSendMediaMessage("📁 Document.pdf (2.4 MB) [MTProto Document]", "doc", null) },
            onSendLocation = { onSendMediaMessage("📍 Live Location: Amsterdam, NL (52.3676° N, 4.9041° E)", "location", null) },
            onSendPoll = { onSendMediaMessage("📊 Poll: How secure is MTProto 2.0?\n1. 200% Secure 🔒\n2. Unbreakable 🛡️\n3. Best Protocol ⚡", "poll", null) },
            onSendContact = { onSendMediaMessage("👤 Contact Card: Pavel Durov (+1 555-019-2834)", "contact", null) },
            onSendAudio = { onSendMediaMessage("🎵 Audio Track: Telegram Anthem (320kbps MP3)", "audio", null) },
            onDismiss = { showAttachmentSheet = false }
        )
    }

    // Sticker & Emoji Picker Sheet
    if (showStickerSheet) {
        StickerEmojiPickerSheet(
            onSelectEmoji = { emoji -> inputText += emoji },
            onSelectSticker = { sticker ->
                onSendMediaMessage("${sticker.emoji} ${sticker.name} [${sticker.packName}]", "sticker", sticker.emoji)
            },
            onDismiss = { showStickerSheet = false }
        )
    }
}

@Composable
private fun MessageBubble(
    message: MessageEntity,
    isSecretChat: Boolean,
    onReact: (String) -> Unit
) {
    val isOut = message.isOutgoing
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    var showThinking by remember { mutableStateOf(false) }
    var showReactionMenu by remember { mutableStateOf(false) }
    var isVoicePlaying by remember { mutableStateOf(false) }

    val bubbleBg = if (isOut) {
        if (isSecretChat) Color(0xFF2E6930) else Color(0xFF2B5278)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isOut) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (isOut) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isOut) 16.dp else 4.dp,
                    bottomEnd = if (isOut) 4.dp else 16.dp
                ),
                color = if (message.mediaType == "sticker") Color.Transparent else bubbleBg,
                tonalElevation = if (message.mediaType == "sticker") 0.dp else 2.dp,
                modifier = Modifier
                    .clickable { showReactionMenu = !showReactionMenu }
                    .testTag("message_bubble_${message.id}")
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    if (!isOut && message.mediaType != "sticker") {
                        Text(
                            text = message.senderName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSecretChat) Color(0xFF4FAE4E) else Color(0xFF50B9EC)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    // Content based on mediaType
                    when (message.mediaType) {
                        "voice" -> {
                            // Official Telegram Voice Message Player
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(Color(0xFF229ED9), CircleShape)
                                        .clickable { isVoicePlaying = !isVoicePlaying },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isVoicePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play voice",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Audio Waveform Bars
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        modifier = Modifier.height(22.dp)
                                    ) {
                                        val waveHeights = listOf(6, 12, 18, 8, 14, 20, 10, 16, 22, 12, 8, 16, 10, 18, 14, 20, 8, 12)
                                        waveHeights.forEachIndexed { idx, h ->
                                            Box(
                                                modifier = Modifier
                                                    .width(3.dp)
                                                    .height(h.dp)
                                                    .background(
                                                        if (isVoicePlaying && idx < 10) Color(0xFF229ED9) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                        RoundedCornerShape(2.dp)
                                                    )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isVoicePlaying) "0:04 / 0:15" else "0:15 • 24.8 KB",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        "sticker" -> {
                            // Large Telegram Sticker
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = message.stickerEmoji ?: "⭐️",
                                    fontSize = 64.sp
                                )
                                Text(
                                    text = message.text,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        "photo" -> {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .background(Color(0xFF1E2833), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF229ED9), modifier = Modifier.size(44.dp))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Encrypted Media Preview", fontSize = 12.sp, color = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = message.text, style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        "doc" -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(Color(0xFF229ED9), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(message.text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("Verified MTProto Document", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        "location" -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(Color(0xFF00E676), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(message.text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("Open in Google Maps", fontSize = 11.sp, color = Color(0xFF00E676))
                                }
                            }
                        }

                        "poll" -> {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Poll, contentDescription = null, tint = Color(0xFFFF4081), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Anonymous Poll", fontSize = 11.sp, color = Color(0xFFFF4081), fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(message.text, style = MaterialTheme.typography.bodyMedium, lineHeight = 19.sp)
                            }
                        }

                        else -> {
                            // Standard Text
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    // Expandable Thinking Trace (High Thinking mode)
                    if (!message.thinkingText.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { showThinking = !showThinking }
                                .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Reasoning",
                                tint = Color(0xFFE040FB),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "High Thinking Reasoning Chain",
                                fontSize = 11.sp,
                                color = Color(0xFFE040FB),
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = if (showThinking) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color(0xFFE040FB),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        if (showThinking) {
                            Text(
                                text = message.thinkingText,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Search citations
                    if (!message.searchSourcesJson.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Grounding Sources:\n${message.searchSourcesJson}",
                            fontSize = 10.sp,
                            color = Color(0xFF229ED9),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Bottom metadata (Timestamp, lock, timer, checks)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        if (message.isEncrypted) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Encrypted",
                                tint = Color(0xFF4FAE4E),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        if (message.selfDestructSeconds > 0) {
                            val remainingSec = message.destructAtTimestamp?.let {
                                val diff = (it - System.currentTimeMillis()) / 1000
                                if (diff > 0) diff else 0
                            } ?: message.selfDestructSeconds

                            Text(
                                text = "⏱ ${remainingSec}s",
                                fontSize = 10.sp,
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        Text(
                            text = timeFormat.format(Date(message.timestamp)),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )

                        if (isOut) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Read",
                                tint = if (isSecretChat) Color(0xFF4FAE4E) else Color(0xFF229ED9),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Reaction chip if active
            if (!message.reaction.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = message.reaction,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Quick Emoji Reaction Bar
            AnimatedVisibility(visible = showReactionMenu) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    listOf("👍", "❤️", "🔥", "🔒", "⚡", "👏").forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .clickable {
                                    onReact(emoji)
                                    showReactionMenu = false
                                }
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
