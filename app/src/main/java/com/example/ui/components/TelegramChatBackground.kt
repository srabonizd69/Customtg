package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun TelegramChatBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.run { red * 0.299 + green * 0.587 + blue * 0.114 < 0.5 }
    val patternColor = if (isDark) Color(0xFF1E2833).copy(alpha = 0.45f) else Color(0xFFDCE2E6).copy(alpha = 0.55f)
    val bgColor = if (isDark) Color(0xFF0E1621) else Color(0xFFEFEFF4)

    Box(modifier = modifier.fillMaxSize().background(bgColor)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 110f
            var y = 30f
            while (y < size.height + step) {
                var x = 30f
                var rowIdx = (y / step).toInt()
                while (x < size.width + step) {
                    val offsetX = if (rowIdx % 2 == 1) x + (step / 2f) else x
                    when ((rowIdx + (x / step).toInt()) % 4) {
                        0 -> {
                            // Paper plane outline
                            val path = Path().apply {
                                moveTo(offsetX, y)
                                lineTo(offsetX + 18f, y - 8f)
                                lineTo(offsetX + 10f, y + 14f)
                                lineTo(offsetX + 6f, y + 4f)
                                close()
                            }
                            drawPath(path, color = patternColor, style = Stroke(width = 1.5f))
                        }
                        1 -> {
                            // Padlock symbol
                            drawCircle(color = patternColor, radius = 5f, center = Offset(offsetX, y), style = Stroke(width = 1.5f))
                            drawRect(
                                color = patternColor,
                                topLeft = Offset(offsetX - 6f, y + 3f),
                                size = androidx.compose.ui.geometry.Size(12f, 10f),
                                style = Stroke(width = 1.5f)
                            )
                        }
                        2 -> {
                            // Telegram Star
                            drawCircle(color = patternColor, radius = 3.5f, center = Offset(offsetX, y))
                        }
                        else -> {
                            // Wave / Cloud arc
                            drawArc(
                                color = patternColor,
                                startAngle = 0f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(offsetX - 7f, y - 4f),
                                size = androidx.compose.ui.geometry.Size(14f, 8f),
                                style = Stroke(width = 1.5f)
                            )
                        }
                    }
                    x += step
                }
                y += step
            }
        }
        content()
    }
}
