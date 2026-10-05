package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isOutgoing: Boolean,
    val isEncrypted: Boolean = false,
    val selfDestructSeconds: Int = 0,
    val destructAtTimestamp: Long? = null,
    val status: String = "READ", // SENDING, SENT, DELIVERED, READ
    val mtprotoMsgId: Long = 0L,
    val mtprotoSeqNo: Int = 0,
    val mediaType: String? = null, // null, "voice", "photo", "doc", "sticker", "location", "poll"
    val voiceDurationSec: Int = 0,
    val stickerEmoji: String? = null,
    val mediaCaption: String? = null,
    val reaction: String? = null,
    val thinkingText: String? = null,
    val searchSourcesJson: String? = null
)
