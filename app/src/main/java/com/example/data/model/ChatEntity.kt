package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val title: String,
    val username: String,
    val avatarColorHex: String,
    val isSecretChat: Boolean = false,
    val isBot: Boolean = false,
    val isChannel: Boolean = false,
    val isGroup: Boolean = false,
    val isSavedMessages: Boolean = false,
    val memberCount: Int = 0,
    val bio: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val selfDestructSeconds: Int = 0,
    val pinned: Boolean = false,
    val customNotificationThemeId: String = "classic_cyan",
    val e2eFingerprintHex: String = "",
    val e2eEmojiFingerprint: String = "",
    val dcId: Int = 2
)
