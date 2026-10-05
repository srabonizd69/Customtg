package com.example.data.model

data class MTProtoPacket(
    val id: String,
    val timestamp: Long,
    val direction: String, // "OUTGOING", "INCOMING"
    val authKeyIdHex: String,
    val msgKeyHex: String,
    val msgId: Long,
    val seqNo: Int,
    val payloadBytesLength: Int,
    val tlMethod: String,
    val status: String, // "SENT", "ACKNOWLEDGED", "DECRYPTED_OK"
    val dcName: String
)

data class MTProtoSession(
    val serverDc: Int = 2,
    val serverHost: String = "149.154.167.50:443",
    val isTestNet: Boolean = false,
    val authKeyHex: String = "4f8a3c9b2e1d7a6e0c5b8d9f1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b",
    val authKeyIdHex: String = "7a8b9c0d1e2f3a4b",
    val sessionIdHex: String = "d83e29f10a27cb45",
    val serverSaltHex: String = "89ac23dfb01e48ca",
    val isConnected: Boolean = true,
    val latencyMs: Long = 42,
    val useProxy: Boolean = false,
    val proxySecret: String = "ee000000000000000000000000000000007777772e676f6f676c652e636f6d",
    val totalPacketsSent: Int = 124,
    val totalPacketsReceived: Int = 189
)
