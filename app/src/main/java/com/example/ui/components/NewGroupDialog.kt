package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TelegramContact

@Composable
fun NewGroupDialog(
    contacts: List<TelegramContact>,
    onCreateGroup: (title: String, memberNames: List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    val selectedMemberIds = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF229ED9))
                Spacer(modifier = Modifier.width(10.dp))
                Text("New Group", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Group Name") },
                    placeholder = { Text("e.g. Telegram Enthusiasts") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Add Members (${selectedMemberIds.size} selected):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                contacts.take(5).forEach { contact ->
                    val isChecked = selectedMemberIds.contains(contact.id)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isChecked) selectedMemberIds.remove(contact.id)
                                else selectedMemberIds.add(contact.id)
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { check ->
                                if (check) selectedMemberIds.add(contact.id)
                                else selectedMemberIds.remove(contact.id)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(contact.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreateGroup(title.trim(), selectedMemberIds.toList())
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Create", fontWeight = FontWeight.Bold, color = Color(0xFF229ED9))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
