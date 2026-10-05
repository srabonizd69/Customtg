package com.example

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.crypto.MTProtoEngine
import com.example.data.model.NotificationTheme
import com.example.data.model.TelegramContact
import com.example.data.model.TelegramUser
import com.example.notification.TelegramNotificationManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TelegramAppTest {

    @Test
    fun `appName string matches TeleProto Messenger`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TeleProto Messenger", appName)
    }

    @Test
    fun `MTProtoEngine generates valid 256-byte AuthKey and 8-byte AuthKeyId`() {
        val authKey = MTProtoEngine.generateAuthKey()
        assertEquals(256, authKey.size)

        val authKeyId = MTProtoEngine.computeAuthKeyId(authKey)
        assertEquals(8, authKeyId.size)
    }

    @Test
    fun `MTProtoEngine generates valid Visual Fingerprint with emojis and hex`() {
        val authKey = MTProtoEngine.generateAuthKey()
        val (hex, emojis) = MTProtoEngine.generateVisualFingerprint(authKey)

        assertNotNull(hex)
        assertFalse(hex.isBlank())
        assertNotNull(emojis)
        assertFalse(emojis.isBlank())

        // Fingerprint emojis should contain spaces separating 4 visual emojis
        val emojiList = emojis.split(" ").filter { it.isNotBlank() }
        assertEquals(4, emojiList.size)
    }

    @Test
    fun `MTProtoEngine generates msg_id divisible by 4`() {
        for (i in 1..20) {
            val msgId = MTProtoEngine.generateMsgId()
            assertTrue("msg_id must be greater than 0", msgId > 0)
            assertEquals("msg_id must be divisible by 4 for client messages", 0L, msgId % 4L)
        }
    }

    @Test
    fun `MTProtoEngine encrypts payload and returns valid 16-byte msg_key`() {
        val authKey = MTProtoEngine.generateAuthKey()
        val plainText = "Secret Telegram payload via MTProto 2.0"
        val (msgKey, encryptedBytes) = MTProtoEngine.encryptPayload(plainText, authKey)

        assertEquals(16, msgKey.size)
        assertTrue(encryptedBytes.isNotEmpty())
    }

    @Test
    fun `NotificationTheme presets are correctly configured`() {
        val presets = NotificationTheme.PRESETS
        assertTrue(presets.isNotEmpty())

        val defaultTheme = NotificationTheme.getById("classic_cyan")
        assertEquals("Telegram Cyan", defaultTheme.name)
        assertEquals("#0088CC", defaultTheme.ledColorHex)

        val stealthTheme = NotificationTheme.getById("secret_stealth")
        assertEquals("Secret Stealth (Encrypted)", stealthTheme.name)
        assertEquals("MINIMAL_PILL", stealthTheme.bannerStyle)
    }

    @Test
    fun `TelegramNotificationManager creates valid NotificationChannel`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val theme = NotificationTheme.getById("classic_cyan")
        val channelId = TelegramNotificationManager.createChannelForTheme(context, theme)

        assertEquals("channel_classic_cyan", channelId)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = notificationManager.getNotificationChannel(channelId)
        assertNotNull(channel)
        assertEquals("TeleProto: Telegram Cyan", channel.name)
    }

    @Test
    fun `TelegramUser model generates correct initials and full name`() {
        val user = TelegramUser(
            id = 123L,
            firstName = "Pavel",
            lastName = "Durov",
            username = "durov",
            phone = "+1 555 123 4567"
        )
        assertEquals("Pavel Durov", user.fullName)
        assertEquals("PD", user.initials)
    }

    @Test
    fun `TelegramContact default list contains contacts with valid initials`() {
        val contacts = TelegramContact.DEFAULT_CONTACTS
        assertTrue(contacts.size >= 5)

        val durov = contacts.first { it.username == "@durov" }
        assertEquals("PD", durov.initials)
        assertTrue(durov.isOnline)
    }
}
