package com.example.crypto

import com.example.data.model.MTProtoPacket
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object MTProtoEngine {
    private val secureRandom = SecureRandom()

    // Emoji pool for Telegram Visual Key Fingerprints
    private val FINGERPRINT_EMOJIS = listOf(
        "🔒", "⚡", "🛡️", "💎", "🚀", "🔑", "🪐", "🔥",
        "🌟", "🎯", "⚓", "🛸", "👑", "🌈", "🧩", "🎲",
        "🌊", "🦊", "🦁", "🦅", "🌸", "🍁", "🍄", "⚡️"
    )

    fun generateAuthKey(): ByteArray {
        val key = ByteArray(256)
        secureRandom.nextBytes(key)
        return key
    }

    fun computeAuthKeyId(authKey: ByteArray): ByteArray {
        val sha1 = MessageDigest.getInstance("SHA-1").digest(authKey)
        // lower 8 bytes of SHA1
        return sha1.copyOfRange(sha1.size - 8, sha1.size)
    }

    fun generateVisualFingerprint(authKey: ByteArray): Pair<String, String> {
        val sha256 = MessageDigest.getInstance("SHA-256").digest(authKey)
        val hex = sha256.joinToString("") { "%02X".format(it) }
        val formattedHex = hex.chunked(4).take(8).joinToString(" ")

        // Generate 4 distinct Telegram emojis from hash bytes
        val emojis = (0..3).map { i ->
            val idx = (sha256[i].toInt() and 0xFF) % FINGERPRINT_EMOJIS.size
            FINGERPRINT_EMOJIS[idx]
        }.joinToString(" ")

        return Pair(formattedHex, emojis)
    }

    fun generateMsgId(): Long {
        val timeSec = System.currentTimeMillis() / 1000L
        val randomNano = (secureRandom.nextInt(1_000_000_000) and 0x7FFFFFFF).toLong()
        var msgId = (timeSec shl 32) or randomNano
        // MTProto requirement: msg_id must be divisible by 4 for client messages
        msgId = (msgId and 0xFFFFFFFFFFFFFFFCUL.toLong())
        return if (msgId == 0L) 4L else msgId
    }

    fun encryptPayload(plainText: String, authKey: ByteArray): Pair<ByteArray, ByteArray> {
        val plainBytes = plainText.toByteArray(Charsets.UTF_8)
        // MTProto 2.0 SHA-256 MsgKey derivation
        val md = MessageDigest.getInstance("SHA-256")
        md.update(authKey.copyOfRange(88, 120))
        md.update(plainBytes)
        val fullHash = md.digest()
        val msgKey = fullHash.copyOfRange(8, 24) // 16 bytes msg_key

        // AES-256 Key & IV derivation
        val sha256a = MessageDigest.getInstance("SHA-256").apply {
            update(msgKey)
            update(authKey.copyOfRange(0, 36))
        }.digest()

        val sha256b = MessageDigest.getInstance("SHA-256").apply {
            update(authKey.copyOfRange(40, 76))
            update(msgKey)
        }.digest()

        val aesKey = sha256a.copyOfRange(0, 32)
        val aesIv = sha256b.copyOfRange(0, 16)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(aesKey, "AES")
        val ivSpec = IvParameterSpec(aesIv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)
        val encryptedData = cipher.doFinal(plainBytes)

        return Pair(msgKey, encryptedData)
    }

    fun createInspectablePacket(
        tlMethod: String,
        payloadSize: Int,
        isOutgoing: Boolean = true,
        seqNo: Int = 1,
        dcName: String = "DC2 Amsterdam (149.154.167.50:443)"
    ): MTProtoPacket {
        val msgKeyBytes = ByteArray(16).apply { secureRandom.nextBytes(this) }
        val authKeyIdBytes = ByteArray(8).apply { secureRandom.nextBytes(this) }

        return MTProtoPacket(
            id = "mtp_${System.currentTimeMillis()}_${secureRandom.nextInt(1000)}",
            timestamp = System.currentTimeMillis(),
            direction = if (isOutgoing) "OUTGOING" else "INCOMING",
            authKeyIdHex = authKeyIdBytes.joinToString("") { "%02X".format(it) },
            msgKeyHex = msgKeyBytes.joinToString("") { "%02X".format(it) },
            msgId = generateMsgId(),
            seqNo = seqNo,
            payloadBytesLength = payloadSize,
            tlMethod = tlMethod,
            status = if (isOutgoing) "ENCRYPTED_SENT" else "DECRYPTED_OK",
            dcName = dcName
        )
    }

    fun bytesToHex(bytes: ByteArray): String = bytes.joinToString("") { "%02X".format(it) }
}
