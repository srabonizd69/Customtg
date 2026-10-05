package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("UPDATE messages SET reaction = :reaction WHERE id = :messageId")
    suspend fun updateReaction(messageId: String, reaction: String?)

    @Query("DELETE FROM messages WHERE destructAtTimestamp IS NOT NULL AND destructAtTimestamp <= :currentTime")
    suspend fun deleteExpiredMessages(currentTime: Long): Int

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: String)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun clearMessagesForChat(chatId: String)
}
