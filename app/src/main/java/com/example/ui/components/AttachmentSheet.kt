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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentSheet(
    onSendPhoto: () -> Unit,
    onSendFile: () -> Unit,
    onSendLocation: () -> Unit,
    onSendPoll: () -> Unit,
    onSendContact: () -> Unit,
    onSendAudio: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("attachment_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Share Content",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Row 1: Gallery, File, Location
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                AttachmentOptionItem(
                    icon = Icons.Default.Image,
                    label = "Gallery",
                    color = Color(0xFF00B0FF),
                    onClick = {
                        onDismiss()
                        onSendPhoto()
                    }
                )
                AttachmentOptionItem(
                    icon = Icons.Default.Description,
                    label = "File",
                    color = Color(0xFFFFB300),
                    onClick = {
                        onDismiss()
                        onSendFile()
                    }
                )
                AttachmentOptionItem(
                    icon = Icons.Default.LocationOn,
                    label = "Location",
                    color = Color(0xFF00E676),
                    onClick = {
                        onDismiss()
                        onSendLocation()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Row 2: Poll, Contact, Music
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                AttachmentOptionItem(
                    icon = Icons.Default.Poll,
                    label = "Poll",
                    color = Color(0xFFFF4081),
                    onClick = {
                        onDismiss()
                        onSendPoll()
                    }
                )
                AttachmentOptionItem(
                    icon = Icons.Default.Person,
                    label = "Contact",
                    color = Color(0xFF7C4DFF),
                    onClick = {
                        onDismiss()
                        onSendContact()
                    }
                )
                AttachmentOptionItem(
                    icon = Icons.Default.AudioFile,
                    label = "Music",
                    color = Color(0xFFFF5252),
                    onClick = {
                        onDismiss()
                        onSendAudio()
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun AttachmentOptionItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
