package com.example.data.model

data class TelegramUser(
    val id: Long,
    val firstName: String,
    val lastName: String = "",
    val username: String = "",
    val phone: String,
    val avatarColorHex: String = "#229ED9",
    val bio: String = "Digital nomad & MTProto privacy advocate",
    val dcId: Int = 2
) {
    val fullName: String
        get() = if (lastName.isBlank()) firstName else "$firstName $lastName"

    val initials: String
        get() {
            val first = firstName.take(1).uppercase()
            val second = lastName.take(1).uppercase()
            return if (second.isBlank()) first else "$first$second"
        }

    companion object {
        val DEFAULT_USER = TelegramUser(
            id = 739104829L,
            firstName = "Alex",
            lastName = "Rivers",
            username = "alex_teleproto",
            phone = "+1 (555) 789-2048",
            avatarColorHex = "#229ED9",
            bio = "Encrypted messaging with MTProto 2.0 & Gemini AI",
            dcId = 2
        )

        val SECOND_ACCOUNT = TelegramUser(
            id = 849201948L,
            firstName = "Satoshi",
            lastName = "Work",
            username = "satoshi_work",
            phone = "+44 7911 123456",
            avatarColorHex = "#4FAE4E",
            bio = "Decentralized cryptographic systems",
            dcId = 4
        )
    }
}
