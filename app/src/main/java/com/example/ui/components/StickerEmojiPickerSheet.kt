package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TelegramSticker(val id: String, val name: String, val emoji: String, val packName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerEmojiPickerSheet(
    onSelectEmoji: (String) -> Unit,
    onSelectSticker: (TelegramSticker) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Stickers, 1: Emojis

    val stickers = listOf(
        TelegramSticker("s1", "Happy Dog", "🐶", "Durov's Dogs"),
        TelegramSticker("s2", "Cool Dog", "😎", "Durov's Dogs"),
        TelegramSticker("s3", "Love Dog", "😍", "Durov's Dogs"),
        TelegramSticker("s4", "Party Duck", "🦆", "Telegram Duck"),
        TelegramSticker("s5", "Cryptic Pepe", "🐸", "Crypto Guild"),
        TelegramSticker("s6", "Rocket Soar", "🚀", "Telegram Space"),
        TelegramSticker("s7", "Encrypted Lock", "🔒", "MTProto Shield"),
        TelegramSticker("s8", "Lightning Fast", "⚡", "MTProto Speed"),
        TelegramSticker("s9", "Thinking AI", "🧠", "Gemini Intelligence"),
        TelegramSticker("s10", "Diamond Hands", "💎", "Crypto Guild"),
        TelegramSticker("s11", "Telegram Star", "⭐️", "Telegram Stars"),
        TelegramSticker("s12", "Origami Plane", "✈️", "TeleProto Official")
    )

    val emojis = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣",
        "😊", "😇", "🙂", "😉", "😌", "😍", "🥰", "😘",
        "😋", "😛", "😜", "🤪", "🤨", "🧐", "🤓", "😎",
        "🤩", "🥳", "😏", "😒", "😞", "😔", "😟", "😕",
        "🙁", "☹️", "😣", "😖", "😫", "😩", "🥺", "😢",
        "😭", "😤", "😠", "😡", "🤬", "🤯", "😳", "🥵",
        "🥶", "😱", "😨", "😰", "😥", "😓", "🤗", "🤔",
        "🤭", "🤫", "🤥", "😶", "😐", "😑", "😬", "🙄",
        "👍", "👎", "👌", "✌️", "🤞", "🤟", "🤘", "🤙",
        "👋", "🤚", "🖐", "✋", "🖖", "👏", "🙌", "👐",
        "🤲", "🤝", "🙏", "💪", "🦾", "🧠", "👀", "👁",
        "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍",
        "🔥", "⚡️", "✨", "🌟", "💫", "💥", "🔒", "🔑"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("sticker_emoji_picker_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Stickers ⭐️", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Emoji 😀", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                Text(
                    text = "Featured Telegram Sticker Packs",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    items(stickers) { sticker ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(70.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                                .clickable {
                                    onDismiss()
                                    onSelectSticker(sticker)
                                }
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(sticker.emoji, fontSize = 34.sp)
                                Text(sticker.name, fontSize = 9.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    items(emojis) { emoji ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    onSelectEmoji(emoji)
                                }
                        ) {
                            Text(emoji, fontSize = 22.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
