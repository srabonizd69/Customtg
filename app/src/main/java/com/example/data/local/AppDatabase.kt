package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ChatEntity
import com.example.data.model.MessageEntity

@Database(entities = [ChatEntity::class, MessageEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS `chats`")
                db.execSQL("DROP TABLE IF EXISTS `messages`")
                db.execSQL("CREATE TABLE IF NOT EXISTS `chats` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `username` TEXT NOT NULL, `avatarColorHex` TEXT NOT NULL, `isSecretChat` INTEGER NOT NULL, `isBot` INTEGER NOT NULL, `isChannel` INTEGER NOT NULL, `isGroup` INTEGER NOT NULL, `isSavedMessages` INTEGER NOT NULL, `memberCount` INTEGER NOT NULL, `bio` TEXT NOT NULL, `lastMessage` TEXT NOT NULL, `lastMessageTimestamp` INTEGER NOT NULL, `unreadCount` INTEGER NOT NULL, `selfDestructSeconds` INTEGER NOT NULL, `pinned` INTEGER NOT NULL, `customNotificationThemeId` TEXT NOT NULL, `e2eFingerprintHex` TEXT NOT NULL, `e2eEmojiFingerprint` TEXT NOT NULL, `dcId` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `messages` (`id` TEXT NOT NULL, `chatId` TEXT NOT NULL, `senderId` TEXT NOT NULL, `senderName` TEXT NOT NULL, `text` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `isOutgoing` INTEGER NOT NULL, `isEncrypted` INTEGER NOT NULL, `selfDestructSeconds` INTEGER NOT NULL, `destructAtTimestamp` INTEGER, `status` TEXT NOT NULL, `mtprotoMsgId` INTEGER NOT NULL, `mtprotoSeqNo` INTEGER NOT NULL, `mediaType` TEXT, `voiceDurationSec` INTEGER NOT NULL, `stickerEmoji` TEXT, `mediaCaption` TEXT, `reaction` TEXT, `thinkingText` TEXT, `searchSourcesJson` TEXT, PRIMARY KEY(`id`))")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "teleproto_v2.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration(true)
                    .fallbackToDestructiveMigrationOnDowngrade(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
