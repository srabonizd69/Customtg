package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChatEntity
import com.example.ui.components.ChatSummaryDialog
import com.example.ui.components.InAppNotificationBanner
import com.example.ui.components.LogoutDialog
import com.example.ui.components.MTProtoInspectorSheet
import com.example.ui.components.NewChannelDialog
import com.example.ui.components.NewGroupDialog
import com.example.ui.components.NewSecretChatDialog
import com.example.ui.components.SelfDestructDialog
import com.example.ui.components.TelegramDrawer
import com.example.ui.components.VisualFingerprintSheet
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.ChatProfileScreen
import com.example.ui.screens.ContactsScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MTProtoSettingsScreen
import com.example.ui.screens.NotificationThemesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.TeleProtoTheme
import com.example.viewmodel.Screen
import com.example.viewmodel.TelegramViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: TelegramViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemDark = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(systemDark) }

            // Permission requester for Android 13+ POST_NOTIFICATIONS
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* Handled gracefully */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            TeleProtoTheme(darkTheme = darkTheme) {
                MainContent(
                    viewModel = viewModel,
                    darkTheme = darkTheme,
                    onToggleDarkTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
}

@Composable
fun MainContent(
    viewModel: TelegramViewModel,
    darkTheme: Boolean,
    onToggleDarkTheme: () -> Unit
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val savedAccounts by viewModel.savedAccounts.collectAsStateWithLifecycle()
    val showLogoutDialog by viewModel.showLogoutDialog.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()

    val chats by viewModel.allChats.collectAsStateWithLifecycle()
    val session by viewModel.mtprotoSession.collectAsStateWithLifecycle()
    val livePackets by viewModel.livePackets.collectAsStateWithLifecycle()
    val activeNotificationTheme by viewModel.activeNotificationTheme.collectAsStateWithLifecycle()
    val selectedChat by viewModel.selectedChat.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    val enableHighThinking by viewModel.enableHighThinking.collectAsStateWithLifecycle()
    val enableSearchGrounding by viewModel.enableSearchGrounding.collectAsStateWithLifecycle()
    val botRole by viewModel.botRole.collectAsStateWithLifecycle()
    val isAiGenerating by viewModel.isAiGenerating.collectAsStateWithLifecycle()
    val smartReplies by viewModel.smartReplySuggestions.collectAsStateWithLifecycle()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsStateWithLifecycle()
    val voiceDurationSec by viewModel.voiceRecordDurationSec.collectAsStateWithLifecycle()

    val showInspectorSheet by viewModel.showInspectorSheet.collectAsStateWithLifecycle()
    val showFingerprintSheet by viewModel.showFingerprintSheet.collectAsStateWithLifecycle()
    val showNewSecretChatDialog by viewModel.showNewSecretChatDialog.collectAsStateWithLifecycle()
    val showSelfDestructPicker by viewModel.showSelfDestructPicker.collectAsStateWithLifecycle()
    val showSummaryDialog by viewModel.showSummaryDialog.collectAsStateWithLifecycle()
    val chatSummaryText by viewModel.chatSummaryText.collectAsStateWithLifecycle()
    val isSummarizing by viewModel.isSummarizing.collectAsStateWithLifecycle()
    val inAppNotification by viewModel.inAppNotification.collectAsStateWithLifecycle()

    var showNewGroupDialog by remember { mutableStateOf(false) }
    var showNewChannelDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Handle Android system back gestures
    BackHandler(enabled = (currentScreen != Screen.CHAT_LIST && currentScreen != Screen.LOGIN) || drawerState.isOpen) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (currentScreen == Screen.CHAT_DETAIL) {
            viewModel.backToChatList()
        } else if (currentScreen == Screen.CHAT_PROFILE) {
            viewModel.navigateTo(Screen.CHAT_DETAIL)
        } else if (currentScreen == Screen.LOGIN && isLoggedIn) {
            viewModel.navigateTo(Screen.CHAT_LIST)
        } else {
            viewModel.navigateTo(Screen.CHAT_LIST)
        }
    }

    // If not logged in or in LOGIN screen, show LoginScreen directly
    if (!isLoggedIn || currentScreen == Screen.LOGIN) {
        LoginScreen(
            onLoginSuccess = { phone, firstName, lastName ->
                viewModel.login(phone, firstName, lastName)
            },
            onCancel = if (isLoggedIn) {
                { viewModel.navigateTo(Screen.CHAT_LIST) }
            } else null
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = currentScreen == Screen.CHAT_LIST,
            drawerContent = {
                TelegramDrawer(
                    currentScreen = currentScreen,
                    currentUser = currentUser,
                    savedAccounts = savedAccounts,
                    darkTheme = darkTheme,
                    onToggleDarkTheme = onToggleDarkTheme,
                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                    onNewSecretChat = { viewModel.showNewSecretChatDialog.value = true },
                    onNewGroup = { showNewGroupDialog = true },
                    onNewChannel = { showNewChannelDialog = true },
                    onOpenContacts = { viewModel.navigateTo(Screen.CONTACTS) },
                    onOpenSavedMessages = {
                        val savedChat = chats.firstOrNull { it.isSavedMessages || it.id == "chat_saved_messages" }
                        if (savedChat != null) {
                            viewModel.openChat(savedChat)
                        } else {
                            val newSaved = ChatEntity(
                                id = "chat_saved_messages",
                                title = "Saved Messages",
                                username = "@saved_messages",
                                avatarColorHex = "#229ED9",
                                isSavedMessages = true,
                                lastMessage = "Your cloud storage",
                                lastMessageTimestamp = System.currentTimeMillis()
                            )
                            viewModel.openChat(newSaved)
                        }
                    },
                    onOpenSettings = { viewModel.navigateTo(Screen.SETTINGS) },
                    onOpenInspector = { viewModel.showInspectorSheet.value = true },
                    onSwitchAccount = { user -> viewModel.switchAccount(user) },
                    onAddAccount = { viewModel.navigateTo(Screen.LOGIN) },
                    onLogoutClick = { viewModel.showLogoutDialog.value = true },
                    onCloseDrawer = { scope.launch { drawerState.close() } }
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    Screen.CHAT_LIST -> {
                        ChatListScreen(
                            chats = chats,
                            session = session,
                            packetCount = livePackets.size,
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onSelectChat = { chat -> viewModel.openChat(chat) },
                            onOpenInspector = { viewModel.showInspectorSheet.value = true },
                            onOpenNotificationThemes = { viewModel.navigateTo(Screen.NOTIFICATION_THEMES) },
                            onNewSecretChat = { viewModel.showNewSecretChatDialog.value = true },
                            onNewGroup = { showNewGroupDialog = true },
                            onNewChannel = { showNewChannelDialog = true }
                        )
                    }

                    Screen.CHAT_DETAIL -> {
                        selectedChat?.let { chat ->
                            ChatDetailScreen(
                                chat = chat,
                                messages = chatMessages,
                                isAiGenerating = isAiGenerating,
                                enableHighThinking = enableHighThinking,
                                enableSearchGrounding = enableSearchGrounding,
                                botRole = botRole,
                                smartReplies = smartReplies,
                                isRecordingVoice = isRecordingVoice,
                                voiceDurationSec = voiceDurationSec,
                                onBack = { viewModel.backToChatList() },
                                onSendMessage = { text -> viewModel.sendMessage(text) },
                                onSendMediaMessage = { text, mediaType, stickerEmoji ->
                                    viewModel.sendMediaMessage(text, mediaType, stickerEmoji)
                                },
                                onOpenChatProfile = { viewModel.openChatProfile() },
                                onToggleHighThinking = { viewModel.enableHighThinking.value = !enableHighThinking },
                                onToggleSearchGrounding = { viewModel.enableSearchGrounding.value = !enableSearchGrounding },
                                onSelectBotRole = { role -> viewModel.botRole.value = role },
                                onOpenFingerprint = { viewModel.showFingerprintSheet.value = true },
                                onOpenSelfDestruct = { viewModel.showSelfDestructPicker.value = true },
                                onSummarizeChat = { viewModel.summarizeCurrentChat() },
                                onStartVoiceRecord = { viewModel.startVoiceRecording() },
                                onStopVoiceRecord = { viewModel.stopAndSendVoiceRecording() },
                                onCancelVoiceRecord = { viewModel.cancelVoiceRecording() },
                                onReactToMessage = { msgId, emoji -> viewModel.setMessageReaction(msgId, emoji) },
                                onSelectNotificationTheme = { themeId -> viewModel.setChatNotificationTheme(themeId) }
                            )
                        }
                    }

                    Screen.CONTACTS -> {
                        ContactsScreen(
                            contacts = contacts,
                            onBack = { viewModel.navigateTo(Screen.CHAT_LIST) },
                            onSelectContact = { contact -> viewModel.openChatByContact(contact) }
                        )
                    }

                    Screen.SETTINGS -> {
                        SettingsScreen(
                            user = currentUser,
                            darkTheme = darkTheme,
                            onToggleDarkTheme = onToggleDarkTheme,
                            onBack = { viewModel.navigateTo(Screen.CHAT_LIST) },
                            onOpenNotificationThemes = { viewModel.navigateTo(Screen.NOTIFICATION_THEMES) },
                            onOpenMTProtoSettings = { viewModel.navigateTo(Screen.MTPROTO_SETTINGS) },
                            onLogoutClick = { viewModel.showLogoutDialog.value = true }
                        )
                    }

                    Screen.CHAT_PROFILE -> {
                        selectedChat?.let { chat ->
                            ChatProfileScreen(
                                chat = chat,
                                onBack = { viewModel.navigateTo(Screen.CHAT_DETAIL) },
                                onStartSecretChat = {
                                    viewModel.createNewSecretChat(chat.title, chat.username)
                                },
                                onOpenFingerprint = { viewModel.showFingerprintSheet.value = true }
                            )
                        }
                    }

                    Screen.MTPROTO_SETTINGS -> {
                        MTProtoSettingsScreen(
                            session = session,
                            onBack = { viewModel.navigateTo(Screen.CHAT_LIST) },
                            onSelectDC = { dcId -> viewModel.selectDC(dcId) },
                            onToggleTestNet = { isTest -> viewModel.toggleTestNet(isTest) },
                            onToggleProxy = { proxy -> viewModel.toggleProxy(proxy) },
                            onOpenInspector = { viewModel.showInspectorSheet.value = true }
                        )
                    }

                    Screen.NOTIFICATION_THEMES -> {
                        NotificationThemesScreen(
                            activeTheme = activeNotificationTheme,
                            onBack = { viewModel.navigateTo(Screen.CHAT_LIST) },
                            onSelectTheme = { theme -> viewModel.selectGlobalNotificationTheme(theme) },
                            onTestNotification = { theme -> viewModel.triggerTestNotification(theme) }
                        )
                    }

                    Screen.LOGIN -> {
                        // Handled above
                    }
                }

                // In-App Notification Banner floating overlay
                InAppNotificationBanner(state = inAppNotification)
            }
        }
    }

    // Modal Bottom Sheets and Dialogs
    if (showInspectorSheet) {
        MTProtoInspectorSheet(
            session = session,
            packets = livePackets,
            onSelectDC = { dcId -> viewModel.selectDC(dcId) },
            onDismiss = { viewModel.showInspectorSheet.value = false }
        )
    }

    if (showFingerprintSheet) {
        selectedChat?.let { chat ->
            VisualFingerprintSheet(
                chat = chat,
                onDismiss = { viewModel.showFingerprintSheet.value = false }
            )
        }
    }

    if (showNewSecretChatDialog) {
        NewSecretChatDialog(
            onConfirm = { name, username ->
                viewModel.createNewSecretChat(name, username)
            },
            onDismiss = { viewModel.showNewSecretChatDialog.value = false }
        )
    }

    if (showNewGroupDialog) {
        NewGroupDialog(
            contacts = contacts,
            onCreateGroup = { groupTitle, memberIds ->
                val newGroup = ChatEntity(
                    id = "group_${System.currentTimeMillis()}",
                    title = groupTitle,
                    username = "@group_${System.currentTimeMillis() % 10000}",
                    avatarColorHex = "#00BCD4",
                    isGroup = true,
                    memberCount = memberIds.size + 1,
                    lastMessage = "You created the group \"$groupTitle\"",
                    lastMessageTimestamp = System.currentTimeMillis()
                )
                showNewGroupDialog = false
                viewModel.openChat(newGroup)
            },
            onDismiss = { showNewGroupDialog = false }
        )
    }

    if (showNewChannelDialog) {
        NewChannelDialog(
            onCreateChannel = { channelName, description ->
                val newChannel = ChatEntity(
                    id = "channel_${System.currentTimeMillis()}",
                    title = channelName,
                    username = "@${channelName.lowercase().replace(" ", "_")}",
                    avatarColorHex = "#FF9800",
                    isChannel = true,
                    bio = description,
                    lastMessage = "Channel created",
                    lastMessageTimestamp = System.currentTimeMillis()
                )
                showNewChannelDialog = false
                viewModel.openChat(newChannel)
            },
            onDismiss = { showNewChannelDialog = false }
        )
    }

    if (showSelfDestructPicker) {
        selectedChat?.let { chat ->
            SelfDestructDialog(
                currentSeconds = chat.selfDestructSeconds,
                onSelectSeconds = { sec -> viewModel.setSelfDestructSeconds(sec) },
                onDismiss = { viewModel.showSelfDestructPicker.value = false }
            )
        }
    }

    if (showSummaryDialog) {
        ChatSummaryDialog(
            isLoading = isSummarizing,
            summaryText = chatSummaryText,
            onDismiss = { viewModel.showSummaryDialog.value = false }
        )
    }

    if (showLogoutDialog) {
        LogoutDialog(
            user = currentUser,
            onConfirmLogout = { viewModel.logout() },
            onAddAccount = {
                viewModel.showLogoutDialog.value = false
                viewModel.navigateTo(Screen.LOGIN)
            },
            onDismiss = { viewModel.showLogoutDialog.value = false }
        )
    }
}
