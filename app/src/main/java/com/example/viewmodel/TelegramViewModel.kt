package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.crypto.MTProtoEngine
import com.example.data.model.ChatEntity
import com.example.data.model.MTProtoPacket
import com.example.data.model.MTProtoSession
import com.example.data.model.MessageEntity
import com.example.data.model.NotificationTheme
import com.example.data.model.TelegramContact
import com.example.data.model.TelegramUser
import com.example.data.repository.TelegramRepository
import com.example.gemini.GeminiChatTurn
import com.example.gemini.GeminiService
import com.example.notification.TelegramNotificationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    LOGIN,
    CHAT_LIST,
    CHAT_DETAIL,
    CONTACTS,
    SETTINGS,
    CHAT_PROFILE,
    MTPROTO_SETTINGS,
    NOTIFICATION_THEMES
}

data class InAppNotificationState(
    val isVisible: Boolean = false,
    val title: String = "",
    val message: String = "",
    val theme: NotificationTheme = NotificationTheme.PRESETS[0]
)

class TelegramViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TelegramRepository(application)

    val allChats: StateFlow<List<ChatEntity>> = repository.allChats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mtprotoSession: StateFlow<MTProtoSession> = repository.mtprotoSession
    val livePackets: StateFlow<List<MTProtoPacket>> = repository.livePackets
    val activeNotificationTheme: StateFlow<NotificationTheme> = repository.activeNotificationTheme

    private val _currentScreen = MutableStateFlow(Screen.CHAT_LIST)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedChat = MutableStateFlow<ChatEntity?>(null)
    val selectedChat: StateFlow<ChatEntity?> = _selectedChat.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val chatMessages: StateFlow<List<MessageEntity>> = _chatMessages.asStateFlow()

    // Gemini Settings
    val enableHighThinking = MutableStateFlow(false)
    val enableSearchGrounding = MutableStateFlow(false)
    val botRole = MutableStateFlow("MTProto Security Analyst")

    // In-Chat AI features
    val isAiGenerating = MutableStateFlow(false)
    val smartReplySuggestions = MutableStateFlow<List<String>>(emptyList())
    val chatSummaryText = MutableStateFlow<String?>(null)
    val isSummarizing = MutableStateFlow(false)

    // User Authentication & Multi-Account
    val isLoggedIn = MutableStateFlow(true)
    val currentUser = MutableStateFlow(TelegramUser.DEFAULT_USER)
    val savedAccounts = MutableStateFlow(listOf(TelegramUser.DEFAULT_USER, TelegramUser.SECOND_ACCOUNT))
    val showLogoutDialog = MutableStateFlow(false)

    // Contacts
    val contacts = MutableStateFlow(TelegramContact.DEFAULT_CONTACTS)

    // UI Sheets / Dialogs
    val showInspectorSheet = MutableStateFlow(false)
    val showFingerprintSheet = MutableStateFlow(false)
    val showNewSecretChatDialog = MutableStateFlow(false)
    val showSelfDestructPicker = MutableStateFlow(false)
    val showSummaryDialog = MutableStateFlow(false)
    val inAppNotification = MutableStateFlow(InAppNotificationState())

    // Active voice recording simulation
    val isRecordingVoice = MutableStateFlow(false)
    val voiceRecordDurationSec = MutableStateFlow(0)
    private var voiceTimerJob: Job? = null

    private var messageCollectionJob: Job? = null

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun openChat(chat: ChatEntity) {
        _selectedChat.value = chat
        _currentScreen.value = Screen.CHAT_DETAIL
        observeMessagesForChat(chat.id)

        // If it's a bot or secret chat, fetch smart replies
        viewModelScope.launch {
            smartReplySuggestions.value = emptyList()
            chatSummaryText.value = null
            if (!chat.isChannel) {
                val lastMsg = chat.lastMessage
                if (lastMsg.isNotBlank()) {
                    val replies = GeminiService.generateSmartReplies(lastMsg)
                    smartReplySuggestions.value = replies
                }
            }
        }
    }

    private fun observeMessagesForChat(chatId: String) {
        messageCollectionJob?.cancel()
        messageCollectionJob = viewModelScope.launch {
            repository.getMessagesForChat(chatId).collect { messages ->
                _chatMessages.value = messages
            }
        }
    }

    fun backToChatList() {
        _selectedChat.value = null
        messageCollectionJob?.cancel()
        _currentScreen.value = Screen.CHAT_LIST
    }

    fun sendMessage(text: String) {
        val chat = _selectedChat.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            repository.sendMessage(chat, text, isOutgoing = true)

            // If it's the Gemini bot, trigger AI answer
            if (chat.isBot) {
                isAiGenerating.value = true
                val history = _chatMessages.value.takeLast(10).map { msg ->
                    GeminiChatTurn(
                        role = if (msg.isOutgoing) "user" else "model",
                        text = msg.text
                    )
                } + GeminiChatTurn(role = "user", text = text)

                val sysInstruction = when (botRole.value) {
                    "MTProto Security Analyst" -> "You are the Telegram MTProto Cryptographic Security Analyst. Answer with deep expertise on MTProto 2.0, Diffie-Hellman key exchanges, AES-IGE encryption, client-server security, and privacy."
                    "Executive Assistant" -> "You are an executive Telegram assistant. Answer concisely, format messages clearly with Telegram formatting, and organize details efficiently."
                    "Tech & Code" -> "You are a software engineer bot for Telegram Bot API and MTProto client developers. Provide accurate Kotlin, Python, and API code snippets."
                    else -> "You are Gemini Intelligence, a helpful Telegram AI assistant."
                }

                val aiResp = GeminiService.generateResponse(
                    conversationHistory = history,
                    systemInstruction = sysInstruction,
                    enableHighThinking = enableHighThinking.value,
                    enableSearchGrounding = enableSearchGrounding.value
                )

                isAiGenerating.value = false

                repository.sendMessage(
                    chat = chat,
                    text = aiResp.replyText,
                    isOutgoing = false,
                    thinkingText = aiResp.thinkingTrace,
                    searchSourcesJson = if (aiResp.searchSources.isNotEmpty()) aiResp.searchSources.joinToString("\n") else null
                )

                // Trigger custom notification for reply
                val theme = NotificationTheme.getById(chat.customNotificationThemeId)
                triggerNotificationEvent(theme, chat.title, aiResp.replyText, chat.isSecretChat)

                // Refresh quick replies
                val newReplies = GeminiService.generateSmartReplies(aiResp.replyText)
                smartReplySuggestions.value = newReplies
            } else if (chat.isSecretChat) {
                // In Secret Chat, simulate automated peer encrypted response after a short delay
                delay(1500)
                val secretReplies = listOf(
                    "🔒 Received and decrypted via MTProto 2.0 AES key.",
                    "Fingerprint verified on local device. Safe to proceed.",
                    "Self-destruct timer synchronized. Message will wipe accordingly.",
                    "End-to-end secret channel secure."
                )
                val peerText = secretReplies.random()
                repository.sendMessage(chat, peerText, isOutgoing = false)

                val theme = NotificationTheme.getById(chat.customNotificationThemeId)
                triggerNotificationEvent(theme, chat.title, peerText, isSecret = true)
            }
        }
    }

    fun startVoiceRecording() {
        isRecordingVoice.value = true
        voiceRecordDurationSec.value = 0
        voiceTimerJob?.cancel()
        voiceTimerJob = viewModelScope.launch {
            while (isRecordingVoice.value) {
                delay(1000)
                voiceRecordDurationSec.value += 1
            }
        }
    }

    fun stopAndSendVoiceRecording() {
        val duration = voiceRecordDurationSec.value
        isRecordingVoice.value = false
        voiceTimerJob?.cancel()
        val chat = _selectedChat.value ?: return

        viewModelScope.launch {
            val noteText = "🎤 Voice message (${duration}s) [MTProto Encrypted Audio Opus 48kHz]"
            repository.sendMessage(chat, noteText, isOutgoing = true, mediaType = "voice")
        }
    }

    fun cancelVoiceRecording() {
        isRecordingVoice.value = false
        voiceTimerJob?.cancel()
        voiceRecordDurationSec.value = 0
    }

    fun createNewSecretChat(contactName: String, username: String) {
        viewModelScope.launch {
            val newChat = repository.createNewSecretChat(contactName, username)
            showNewSecretChatDialog.value = false
            openChat(newChat)
        }
    }

    fun setSelfDestructSeconds(seconds: Int) {
        val chat = _selectedChat.value ?: return
        viewModelScope.launch {
            repository.updateSelfDestruct(chat.id, seconds)
            _selectedChat.value = chat.copy(selfDestructSeconds = seconds)
            showSelfDestructPicker.value = false
            val text = if (seconds == 0) "⏱ Self-destruct timer disabled" else "⏱ Self-destruct timer set to ${seconds}s"
            repository.sendMessage(chat, text, isOutgoing = true)
        }
    }

    fun openChatProfile() {
        _currentScreen.value = Screen.CHAT_PROFILE
    }

    fun openChatByContact(contact: TelegramContact) {
        val existingChat = allChats.value.firstOrNull { it.username == contact.username || it.title == contact.name }
        if (existingChat != null) {
            openChat(existingChat)
        } else {
            val newChat = ChatEntity(
                id = "chat_${contact.id}",
                title = contact.name,
                username = contact.username,
                avatarColorHex = contact.avatarColorHex,
                isSecretChat = false,
                isBot = false,
                isChannel = false,
                lastMessage = "Started direct chat",
                lastMessageTimestamp = System.currentTimeMillis(),
                dcId = 2
            )
            viewModelScope.launch {
                repository.sendMessage(newChat, "Hello ${contact.name}! Connected via MTProto DC2.", isOutgoing = true)
                openChat(newChat)
            }
        }
    }

    fun sendMediaMessage(text: String, mediaType: String, stickerEmoji: String? = null) {
        val chat = _selectedChat.value ?: return
        viewModelScope.launch {
            repository.sendMessage(
                chat = chat,
                text = text,
                isOutgoing = true,
                mediaType = mediaType,
                stickerEmoji = stickerEmoji
            )
        }
    }

    fun setChatNotificationTheme(themeId: String) {
        val chat = _selectedChat.value ?: return
        viewModelScope.launch {
            repository.updateNotificationTheme(chat.id, themeId)
            _selectedChat.value = chat.copy(customNotificationThemeId = themeId)
        }
    }

    fun selectGlobalNotificationTheme(theme: NotificationTheme) {
        repository.setActiveNotificationTheme(theme)
    }

    fun triggerTestNotification(theme: NotificationTheme) {
        triggerNotificationEvent(
            theme = theme,
            title = "TeleProto Test",
            message = "Testing custom notification theme: ${theme.name} (${theme.soundName}, ${theme.vibrationPatternName})",
            isSecret = theme.id == "secret_stealth"
        )
    }

    private fun triggerNotificationEvent(
        theme: NotificationTheme,
        title: String,
        message: String,
        isSecret: Boolean = false
    ) {
        val context = getApplication<Application>().applicationContext
        TelegramNotificationManager.triggerNotification(context, theme, title, message, isSecret)

        // Show in-app heads-up notification simulator
        viewModelScope.launch {
            inAppNotification.value = InAppNotificationState(
                isVisible = true,
                title = title,
                message = message,
                theme = theme
            )
            delay(4000)
            inAppNotification.value = inAppNotification.value.copy(isVisible = false)
        }
    }

    fun summarizeCurrentChat() {
        val messages = _chatMessages.value.map { "${it.senderName}: ${it.text}" }
        if (messages.isEmpty()) return

        isSummarizing.value = true
        showSummaryDialog.value = true
        viewModelScope.launch {
            val summary = GeminiService.summarizeChat(messages)
            chatSummaryText.value = summary
            isSummarizing.value = false
        }
    }

    fun setMessageReaction(messageId: String, emoji: String) {
        viewModelScope.launch {
            repository.updateReaction(messageId, emoji)
        }
    }

    fun selectDC(dcId: Int) {
        repository.updateDC(dcId)
    }

    fun toggleTestNet(isTest: Boolean) {
        repository.toggleTestNet(isTest)
    }

    fun toggleProxy(enabled: Boolean) {
        repository.toggleProxy(enabled)
    }

    fun clearCurrentChat() {
        val chat = _selectedChat.value ?: return
        viewModelScope.launch {
            repository.clearChat(chat.id)
        }
    }

    fun login(phone: String, firstName: String, lastName: String = "") {
        val newUser = TelegramUser(
            id = System.currentTimeMillis(),
            firstName = firstName.ifBlank { "Telegram" },
            lastName = lastName.ifBlank { "User" },
            username = (firstName + "_" + phone.takeLast(4)).lowercase().replace(" ", ""),
            phone = phone,
            avatarColorHex = "#229ED9",
            bio = "Encrypted messaging with MTProto 2.0",
            dcId = 2
        )
        currentUser.value = newUser
        val existing = savedAccounts.value.filter { it.phone != phone }
        savedAccounts.value = listOf(newUser) + existing
        isLoggedIn.value = true
        _currentScreen.value = Screen.CHAT_LIST

        viewModelScope.launch {
            // Log MTProto authentication packet
            repository.sendMessage(
                chat = repository.allChats.firstOrNull()?.firstOrNull() ?: return@launch,
                text = "📱 Account logged in: ${newUser.fullName} ($phone) via MTProto 2.0 auth.signIn.",
                isOutgoing = false
            )
        }
    }

    fun logout() {
        showLogoutDialog.value = false
        isLoggedIn.value = false
        _selectedChat.value = null
        _currentScreen.value = Screen.LOGIN
    }

    fun switchAccount(user: TelegramUser) {
        currentUser.value = user
        viewModelScope.launch {
            val theme = activeNotificationTheme.value
            TelegramNotificationManager.triggerNotification(
                getApplication<Application>().applicationContext,
                theme,
                "Switched Account",
                "Now active as ${user.fullName} (${user.phone})"
            )
        }
    }
}
