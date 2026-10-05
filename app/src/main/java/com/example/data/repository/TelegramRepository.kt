package com.example.data.repository

import android.content.Context
import com.example.crypto.MTProtoEngine
import com.example.data.local.AppDatabase
import com.example.data.model.ChatEntity
import com.example.data.model.MTProtoPacket
import com.example.data.model.MTProtoSession
import com.example.data.model.MessageEntity
import com.example.data.model.NotificationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class TelegramRepository(context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val chatDao = database.chatDao()
    private val messageDao = database.messageDao()
    private val repoScope = CoroutineScope(Dispatchers.IO)

    private val _mtprotoSession = MutableStateFlow(MTProtoSession())
    val mtprotoSession = _mtprotoSession.asStateFlow()

    private val _livePackets = MutableStateFlow<List<MTProtoPacket>>(emptyList())
    val livePackets = _livePackets.asStateFlow()

    private val _activeNotificationTheme = MutableStateFlow(NotificationTheme.PRESETS[0])
    val activeNotificationTheme = _activeNotificationTheme.asStateFlow()

    val allChats: Flow<List<ChatEntity>> = chatDao.getAllChats()

    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> = messageDao.getMessagesForChat(chatId)

    init {
        repoScope.launch {
            seedInitialDataIfEmpty()
            startSelfDestructCleaner()
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        val existing = chatDao.getAllChats().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val keyA = MTProtoEngine.generateAuthKey()
            val (hexA, emojiA) = MTProtoEngine.generateVisualFingerprint(keyA)

            val keyB = MTProtoEngine.generateAuthKey()
            val (hexB, emojiB) = MTProtoEngine.generateVisualFingerprint(keyB)

            val initialChats = listOf(
                ChatEntity(
                    id = "chat_saved_messages",
                    title = "Saved Messages",
                    username = "@saved_messages",
                    avatarColorHex = "#229ED9",
                    isSecretChat = false,
                    isBot = false,
                    isChannel = false,
                    isSavedMessages = true,
                    lastMessage = "Your cloud storage for notes, files, and bookmarks.",
                    lastMessageTimestamp = System.currentTimeMillis() - 10_000,
                    unreadCount = 0,
                    selfDestructSeconds = 0,
                    pinned = true,
                    customNotificationThemeId = "classic_cyan"
                ),
                ChatEntity(
                    id = "chat_gemini_bot",
                    title = "Gemini Intelligence",
                    username = "@GeminiAIBot",
                    avatarColorHex = "#229ED9",
                    isSecretChat = false,
                    isBot = true,
                    isChannel = false,
                    lastMessage = "Ready to assist with High Thinking reasoning, Search Grounding, and MTProto analysis.",
                    lastMessageTimestamp = System.currentTimeMillis() - 60_000,
                    unreadCount = 1,
                    selfDestructSeconds = 0,
                    pinned = true,
                    customNotificationThemeId = "classic_cyan"
                ),
                ChatEntity(
                    id = "chat_pavel_durov",
                    title = "Pavel Durov",
                    username = "@durov",
                    avatarColorHex = "#4FAE4E",
                    isSecretChat = true,
                    isBot = false,
                    isChannel = false,
                    lastMessage = "🔒 Welcome to Secret Chat! End-to-end encrypted via MTProto 2.0 with PFS enabled.",
                    lastMessageTimestamp = System.currentTimeMillis() - 120_000,
                    unreadCount = 0,
                    selfDestructSeconds = 10,
                    pinned = true,
                    customNotificationThemeId = "secret_stealth",
                    e2eFingerprintHex = hexA,
                    e2eEmojiFingerprint = emojiA
                ),
                ChatEntity(
                    id = "chat_mtproto_channel",
                    title = "Telegram MTProto Core",
                    username = "@mtproto_core",
                    avatarColorHex = "#7C4DFF",
                    isSecretChat = false,
                    isBot = false,
                    isChannel = true,
                    lastMessage = "📢 MTProto v2.0 status: All 5 Data Centers operating normally with zero-knowledge encryption.",
                    lastMessageTimestamp = System.currentTimeMillis() - 360_000,
                    unreadCount = 2,
                    selfDestructSeconds = 0,
                    pinned = false,
                    customNotificationThemeId = "quantum_violet"
                ),
                ChatEntity(
                    id = "chat_elena",
                    title = "Elena Rostova",
                    username = "@elena_sec",
                    avatarColorHex = "#E040FB",
                    isSecretChat = true,
                    isBot = false,
                    isChannel = false,
                    lastMessage = "Sent an encrypted verification token.",
                    lastMessageTimestamp = System.currentTimeMillis() - 720_000,
                    unreadCount = 0,
                    selfDestructSeconds = 30,
                    pinned = false,
                    customNotificationThemeId = "cyber_neon",
                    e2eFingerprintHex = hexB,
                    e2eEmojiFingerprint = emojiB
                ),
                ChatEntity(
                    id = "chat_crypto_guild",
                    title = "Crypto & Privacy Guild",
                    username = "@cryptoprivacy",
                    avatarColorHex = "#FF9800",
                    isSecretChat = false,
                    isBot = false,
                    isChannel = false,
                    lastMessage = "Alex: Perfect forward secrecy rekeys every 100 messages or 7 days.",
                    lastMessageTimestamp = System.currentTimeMillis() - 1_200_000,
                    unreadCount = 0,
                    selfDestructSeconds = 0,
                    pinned = false,
                    customNotificationThemeId = "midnight_gold"
                )
            )

            chatDao.insertChats(initialChats)

            // Initial Messages for Gemini Bot
            val geminiMessages = listOf(
                MessageEntity(
                    id = "m_g_1",
                    chatId = "chat_gemini_bot",
                    senderId = "bot",
                    senderName = "Gemini Bot",
                    text = "Hello! I am your Telegram Gemini Assistant.\n\n✨ Features available:\n• 🧠 High Thinking Mode (`gemini-3.1-pro-preview` with deep reasoning)\n• 🔍 Google Search Grounding for live internet updates (`gemini-3.5-flash`)\n• ⚡ Fast Smart Replies (`gemini-3.1-flash-lite-preview`)\n• 🛡️ MTProto 2.0 Security & Protocol analysis\n\nHow can I help you today?",
                    timestamp = System.currentTimeMillis() - 60_000,
                    isOutgoing = false
                )
            )

            // Initial Messages for Pavel Durov Secret Chat
            val pavelMessages = listOf(
                MessageEntity(
                    id = "m_p_1",
                    chatId = "chat_pavel_durov",
                    senderId = "durov",
                    senderName = "Pavel Durov",
                    text = "🔒 You created a Secret Chat with Pavel Durov.\n\n• End-to-end encryption with MTProto 2.0\n• Visual Key Fingerprint verification\n• Self-destruct timer enabled\n• Zero trace on Telegram cloud servers",
                    timestamp = System.currentTimeMillis() - 240_000,
                    isOutgoing = false,
                    isEncrypted = true
                ),
                MessageEntity(
                    id = "m_p_2",
                    chatId = "chat_pavel_durov",
                    senderId = "user",
                    senderName = "Me",
                    text = "Verified! The cryptographic fingerprint matches.",
                    timestamp = System.currentTimeMillis() - 180_000,
                    isOutgoing = true,
                    isEncrypted = true,
                    status = "READ"
                ),
                MessageEntity(
                    id = "m_p_3",
                    chatId = "chat_pavel_durov",
                    senderId = "durov",
                    senderName = "Pavel Durov",
                    text = "🔒 Welcome to Secret Chat! End-to-end encrypted via MTProto 2.0 with PFS enabled.",
                    timestamp = System.currentTimeMillis() - 120_000,
                    isOutgoing = false,
                    isEncrypted = true
                )
            )

            // Initial Messages for Channel
            val channelMessages = listOf(
                MessageEntity(
                    id = "m_c_1",
                    chatId = "chat_mtproto_channel",
                    senderId = "channel",
                    senderName = "Telegram MTProto Core",
                    text = "📢 MTProto v2.0 status: All 5 Data Centers operating normally with zero-knowledge encryption.\n\nServer DCs:\n• DC 1: Miami (149.154.175.50:443)\n• DC 2: Amsterdam (149.154.167.50:443)\n• DC 4: Amsterdam Media (149.154.167.91:443)\n• DC 5: Singapore (91.108.56.165:443)",
                    timestamp = System.currentTimeMillis() - 360_000,
                    isOutgoing = false
                )
            )

            messageDao.insertMessages(geminiMessages + pavelMessages + channelMessages)

            // Seed initial packets in inspector
            val initPackets = listOf(
                MTProtoEngine.createInspectablePacket("help.getNearestDc#1fb33027", 64, isOutgoing = false, seqNo = 1),
                MTProtoEngine.createInspectablePacket("auth.sendCode#a677244f", 128, isOutgoing = true, seqNo = 2),
                MTProtoEngine.createInspectablePacket("messages.sendEncrypted#a9776773", 256, isOutgoing = true, seqNo = 3)
            )
            _livePackets.value = initPackets
        }
    }

    suspend fun sendMessage(
        chat: ChatEntity,
        text: String,
        isOutgoing: Boolean = true,
        thinkingText: String? = null,
        searchSourcesJson: String? = null,
        mediaType: String? = null,
        stickerEmoji: String? = null,
        voiceDurationSec: Int = 0
    ): MessageEntity {
        val now = System.currentTimeMillis()
        val msgId = MTProtoEngine.generateMsgId()
        val seqNo = (_livePackets.value.size + 1) * 2

        val destructAt = if (chat.selfDestructSeconds > 0) {
            now + (chat.selfDestructSeconds * 1000L)
        } else null

        val message = MessageEntity(
            id = "msg_${System.currentTimeMillis()}_${(100..999).random()}",
            chatId = chat.id,
            senderId = if (isOutgoing) "user" else chat.id,
            senderName = if (isOutgoing) "Me" else chat.title,
            text = text,
            timestamp = now,
            isOutgoing = isOutgoing,
            isEncrypted = chat.isSecretChat,
            selfDestructSeconds = chat.selfDestructSeconds,
            destructAtTimestamp = destructAt,
            status = if (isOutgoing) "READ" else "SENT",
            mtprotoMsgId = msgId,
            mtprotoSeqNo = seqNo,
            mediaType = mediaType,
            voiceDurationSec = voiceDurationSec,
            stickerEmoji = stickerEmoji,
            thinkingText = thinkingText,
            searchSourcesJson = searchSourcesJson
        )

        messageDao.insertMessage(message)
        chatDao.updateLastMessage(chat.id, text, now)

        // Log MTProto packet in live protocol inspector
        val tlMethod = if (chat.isSecretChat) "messages.sendEncrypted#a9776773" else "messages.sendMessage#0280d00e"
        val packet = MTProtoEngine.createInspectablePacket(
            tlMethod = tlMethod,
            payloadSize = text.toByteArray().size + 32,
            isOutgoing = isOutgoing,
            seqNo = seqNo,
            dcName = "DC${chat.dcId} (Telegram Production)"
        )
        _livePackets.value = listOf(packet) + _livePackets.value.take(25)
        _mtprotoSession.value = _mtprotoSession.value.copy(
            totalPacketsSent = _mtprotoSession.value.totalPacketsSent + if (isOutgoing) 1 else 0,
            totalPacketsReceived = _mtprotoSession.value.totalPacketsReceived + if (!isOutgoing) 1 else 0
        )

        return message
    }

    suspend fun createNewSecretChat(contactName: String, username: String): ChatEntity {
        val authKey = MTProtoEngine.generateAuthKey()
        val (hexFingerprint, emojiFingerprint) = MTProtoEngine.generateVisualFingerprint(authKey)

        val newChat = ChatEntity(
            id = "secret_${System.currentTimeMillis()}",
            title = contactName,
            username = if (username.startsWith("@")) username else "@$username",
            avatarColorHex = "#4FAE4E",
            isSecretChat = true,
            isBot = false,
            isChannel = false,
            lastMessage = "🔒 Secret chat created. End-to-end encryption active.",
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 0,
            selfDestructSeconds = 10,
            pinned = false,
            customNotificationThemeId = "secret_stealth",
            e2eFingerprintHex = hexFingerprint,
            e2eEmojiFingerprint = emojiFingerprint,
            dcId = 2
        )

        chatDao.insertChat(newChat)

        // First message
        val handshakeMsg = MessageEntity(
            id = "m_handshake_${System.currentTimeMillis()}",
            chatId = newChat.id,
            senderId = "system",
            senderName = "Telegram MTProto",
            text = "🔒 Secret Chat initialized with $contactName.\n\n• End-to-End Encryption enabled via Diffie-Hellman exchange\n• Visual Key: $emojiFingerprint\n• Self-Destruct Timer set to 10 seconds",
            timestamp = System.currentTimeMillis(),
            isOutgoing = false,
            isEncrypted = true
        )
        messageDao.insertMessage(handshakeMsg)

        // Log packet
        val packet = MTProtoEngine.createInspectablePacket("messages.requestEncryption#f64daf8f", 144, true)
        _livePackets.value = listOf(packet) + _livePackets.value.take(25)

        return newChat
    }

    suspend fun updateSelfDestruct(chatId: String, seconds: Int) {
        chatDao.updateSelfDestruct(chatId, seconds)
    }

    suspend fun updateNotificationTheme(chatId: String, themeId: String) {
        chatDao.updateNotificationTheme(chatId, themeId)
    }

    fun setActiveNotificationTheme(theme: NotificationTheme) {
        _activeNotificationTheme.value = theme
    }

    suspend fun updateReaction(messageId: String, reaction: String?) {
        messageDao.updateReaction(messageId, reaction)
    }

    suspend fun clearChat(chatId: String) {
        messageDao.clearMessagesForChat(chatId)
    }

    suspend fun deleteChat(chatId: String) {
        chatDao.deleteChatById(chatId)
        messageDao.clearMessagesForChat(chatId)
    }

    fun updateDC(dcId: Int) {
        val host = when (dcId) {
            1 -> "149.154.175.50:443 (Miami)"
            2 -> "149.154.167.50:443 (Amsterdam)"
            4 -> "149.154.167.91:443 (Amsterdam Media)"
            5 -> "91.108.56.165:443 (Singapore)"
            else -> "149.154.167.50:443 (Amsterdam)"
        }
        _mtprotoSession.value = _mtprotoSession.value.copy(
            serverDc = dcId,
            serverHost = host
        )
        // Add ping packet
        val pingPacket = MTProtoEngine.createInspectablePacket("ping_delay_disconnect#f3427b8c", 32, isOutgoing = true)
        _livePackets.value = listOf(pingPacket) + _livePackets.value.take(25)
    }

    fun toggleTestNet(isTest: Boolean) {
        _mtprotoSession.value = _mtprotoSession.value.copy(
            isTestNet = isTest,
            serverHost = if (isTest) "149.154.175.100:443 (Testnet DC2)" else "149.154.167.50:443 (Amsterdam)"
        )
    }

    fun toggleProxy(enabled: Boolean) {
        _mtprotoSession.value = _mtprotoSession.value.copy(useProxy = enabled)
    }

    private fun startSelfDestructCleaner() {
        repoScope.launch {
            while (true) {
                delay(1000)
                val count = messageDao.deleteExpiredMessages(System.currentTimeMillis())
                if (count > 0) {
                    // Log protocol destruction event
                    val packet = MTProtoEngine.createInspectablePacket("messages.readEncryptedHistory#7f4b690a", 32, isOutgoing = true)
                    _livePackets.value = listOf(packet) + _livePackets.value.take(25)
                }
            }
        }
    }
}
