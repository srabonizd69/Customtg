package com.example.data.model

data class NotificationTheme(
    val id: String,
    val name: String,
    val description: String,
    val ledColorHex: String,
    val soundName: String,
    val vibrationPatternName: String,
    val bannerStyle: String, // "DYNAMIC_CAPSULE", "FULL_BANNER", "MINIMAL_PILL"
    val previewColorHex: String,
    val edgeGlow: Boolean = true,
    val priorityLevel: String = "HIGH", // "HIGH", "URGENT", "SILENT"
    val isDefault: Boolean = false
) {
    companion object {
        val PRESETS = listOf(
            NotificationTheme(
                id = "classic_cyan",
                name = "Telegram Cyan",
                description = "Iconic Telegram cyan LED with balanced dual-pulse haptic",
                ledColorHex = "#0088CC",
                soundName = "Telegram Crisp",
                vibrationPatternName = "Double Pulse",
                bannerStyle = "DYNAMIC_CAPSULE",
                previewColorHex = "#229ED9",
                edgeGlow = true,
                priorityLevel = "HIGH",
                isDefault = true
            ),
            NotificationTheme(
                id = "secret_stealth",
                name = "Secret Stealth (Encrypted)",
                description = "Zero sound, disguised preview, emerald security pulse",
                ledColorHex = "#00E676",
                soundName = "Silent",
                vibrationPatternName = "Gentle Tick",
                bannerStyle = "MINIMAL_PILL",
                previewColorHex = "#4FAE4E",
                edgeGlow = true,
                priorityLevel = "HIGH"
            ),
            NotificationTheme(
                id = "cyber_neon",
                name = "Cyberpunk Neon",
                description = "Electric Magenta glow with high-priority sci-fi sound",
                ledColorHex = "#FF007F",
                soundName = "Cyber Synth",
                vibrationPatternName = "Heartbeat",
                bannerStyle = "FULL_BANNER",
                previewColorHex = "#E040FB",
                edgeGlow = true,
                priorityLevel = "URGENT"
            ),
            NotificationTheme(
                id = "midnight_gold",
                name = "Midnight AMOLED",
                description = "Deep dark mode styling with warm amber notification halo",
                ledColorHex = "#FFB300",
                soundName = "Sub Bass",
                vibrationPatternName = "Single Long",
                bannerStyle = "DYNAMIC_CAPSULE",
                previewColorHex = "#FF9800",
                edgeGlow = false,
                priorityLevel = "HIGH"
            ),
            NotificationTheme(
                id = "quantum_violet",
                name = "Quantum MTProto",
                description = "Deep violet protocol theme for dev channels and server pings",
                ledColorHex = "#7C4DFF",
                soundName = "Harmonic Bell",
                vibrationPatternName = "Morse SOS",
                bannerStyle = "FULL_BANNER",
                previewColorHex = "#7C4DFF",
                edgeGlow = true,
                priorityLevel = "HIGH"
            )
        )

        fun getById(id: String): NotificationTheme {
            return PRESETS.firstOrNull { it.id == id } ?: PRESETS[0]
        }
    }
}
