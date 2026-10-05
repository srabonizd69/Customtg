package com.example.data.model

data class TelegramContact(
    val id: String,
    val name: String,
    val username: String,
    val phone: String,
    val status: String,
    val isOnline: Boolean,
    val avatarColorHex: String,
    val bio: String = ""
) {
    val initials: String
        get() {
            val parts = name.split(" ").filter { it.isNotBlank() }
            return when {
                parts.size >= 2 -> "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
                parts.isNotEmpty() -> parts[0].take(2).uppercase()
                else -> "TG"
            }
        }

    companion object {
        val DEFAULT_CONTACTS = listOf(
            TelegramContact(
                id = "c_durov",
                name = "Pavel Durov",
                username = "@durov",
                phone = "+1 555 019 2834",
                status = "online",
                isOnline = true,
                avatarColorHex = "#4FAE4E",
                bio = "Building Telegram since 2013"
            ),
            TelegramContact(
                id = "c_nikolai",
                name = "Nikolai Durov",
                username = "@ndurov",
                phone = "+1 555 019 9922",
                status = "last seen 15 minutes ago",
                isOnline = false,
                avatarColorHex = "#229ED9",
                bio = "MTProto 2.0 Architect & Cryptographer"
            ),
            TelegramContact(
                id = "c_satoshi",
                name = "Satoshi Nakamoto",
                username = "@satoshi_privacy",
                phone = "+44 7911 204958",
                status = "online",
                isOnline = true,
                avatarColorHex = "#FF9800",
                bio = "Vires in Numeris. Decentralized & sovereign."
            ),
            TelegramContact(
                id = "c_elena",
                name = "Elena Rostova",
                username = "@elena_sec",
                phone = "+49 151 23456789",
                status = "last seen 2 hours ago",
                isOnline = false,
                avatarColorHex = "#E040FB",
                bio = "Security Engineer & Zero-Knowledge researcher"
            ),
            TelegramContact(
                id = "c_vitalik",
                name = "Vitalik Buterin",
                username = "@vitalik_eth",
                phone = "+1 415 555 8831",
                status = "last seen recently",
                isOnline = false,
                avatarColorHex = "#7C4DFF",
                bio = "Cryptography, economics, and protocols"
            ),
            TelegramContact(
                id = "c_mark",
                name = "Markus Lindner",
                username = "@markus_tg",
                phone = "+49 30 901820",
                status = "online",
                isOnline = true,
                avatarColorHex = "#00BCD4",
                bio = "Telegram Client Developer"
            )
        )
    }
}
