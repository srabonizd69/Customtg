package com.example

import com.example.crypto.MTProtoEngine
import com.example.data.model.NotificationTheme
import com.example.data.model.TelegramContact
import com.example.data.model.TelegramUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TelegramUnitTest {

    @Test
    fun testMTProtoAuthKey() {
        val authKey = MTProtoEngine.generateAuthKey()
        assertEquals(256, authKey.size)

        val authKeyId = MTProtoEngine.computeAuthKeyId(authKey)
        assertEquals(8, authKeyId.size)
    }

    @Test
    fun testMTProtoFingerprint() {
        val authKey = MTProtoEngine.generateAuthKey()
        val (hex, emojis) = MTProtoEngine.generateVisualFingerprint(authKey)

        assertNotNull(hex)
        assertFalse(hex.isBlank())
        val parts = emojis.split(" ").filter { it.isNotBlank() }
        assertEquals(4, parts.size)
    }

    @Test
    fun testMTProtoMsgIdAlignment() {
        for (i in 1..20) {
            val msgId = MTProtoEngine.generateMsgId()
            assertTrue(msgId > 0)
            assertEquals(0L, msgId % 4L)
        }
    }

    @Test
    fun testMTProtoPayloadEncryption() {
        val authKey = MTProtoEngine.generateAuthKey()
        val (msgKey, encrypted) = MTProtoEngine.encryptPayload("Test MTProto Payload", authKey)
        assertEquals(16, msgKey.size)
        assertTrue(encrypted.isNotEmpty())
    }

    @Test
    fun testNotificationThemes() {
        val presets = NotificationTheme.PRESETS
        assertEquals(5, presets.size)

        val cyan = NotificationTheme.getById("classic_cyan")
        assertEquals("Telegram Cyan", cyan.name)

        val stealth = NotificationTheme.getById("secret_stealth")
        assertEquals("Secret Stealth (Encrypted)", stealth.name)
    }

    @Test
    fun testTelegramUserModel() {
        val user = TelegramUser(
            id = 1L,
            firstName = "Nikolai",
            lastName = "Durov",
            username = "ndurov",
            phone = "+1 555 999 8888"
        )
        assertEquals("Nikolai Durov", user.fullName)
        assertEquals("ND", user.initials)
    }

    @Test
    fun testTelegramContactModel() {
        val contacts = TelegramContact.DEFAULT_CONTACTS
        assertTrue(contacts.isNotEmpty())
        val durov = contacts.first { it.username == "@durov" }
        assertEquals("PD", durov.initials)
    }

    @Test
    fun testCountryDataAndBangladeshCode() {
        val countries = com.example.data.model.CountryData.ALL_COUNTRIES
        assertTrue("Country list should contain over 50 countries", countries.size >= 50)

        val bd = countries.firstOrNull { it.code == "+880" }
        assertNotNull("Bangladesh (+880) must be present", bd)
        assertEquals("Bangladesh", bd?.name)
        assertEquals("🇧🇩", bd?.flag)

        val lookupWithPlus = com.example.data.model.CountryData.findByCode("+880")
        assertEquals("Bangladesh", lookupWithPlus?.name)

        val lookupWithoutPlus = com.example.data.model.CountryData.findByCode("880")
        assertEquals("Bangladesh", lookupWithoutPlus?.name)
    }
}
