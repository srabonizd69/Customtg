package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY pinned DESC, lastMessageTimestamp DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :id")
    fun getChatById(id: String): Flow<ChatEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<ChatEntity>)

    @Update
    suspend fun updateChat(chat: ChatEntity)

    @Query("UPDATE chats SET lastMessage = :lastMsg, lastMessageTimestamp = :timestamp WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: String, lastMsg: String, timestamp: Long)

    @Query("UPDATE chats SET selfDestructSeconds = :seconds WHERE id = :chatId")
    suspend fun updateSelfDestruct(chatId: String, seconds: Int)

    @Query("UPDATE chats SET customNotificationThemeId = :themeId WHERE id = :chatId")
    suspend fun updateNotificationTheme(chatId: String, themeId: String)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChatById(chatId: String)
}
