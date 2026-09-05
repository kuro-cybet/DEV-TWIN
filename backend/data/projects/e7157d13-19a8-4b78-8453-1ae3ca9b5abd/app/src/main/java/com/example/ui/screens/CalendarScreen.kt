package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.TerraceCard
import com.example.ui.theme.Canopy
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.HuskBorder
import com.example.ui.theme.HuskCard
import com.example.ui.theme.Sprout
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.KrishiViewModel

@Composable
fun CalendarScreen(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    var isAddTaskOpen by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(bottom = 90.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Agro Task Calendar",
                        style = MaterialTheme.typography.displaySmall,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Weather-smart schedule for irrigation, fertilizer & foliar sprays",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    onClick = { isAddTaskOpen = true },
                    shape = RoundedCornerShape(12.dp),
                    color = Sprout,
                    modifier = Modifier.testTag("add_task_fab")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Task", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // 7-Day Date Ribbon
        item {
            val days = listOf("Mon\n18", "Tue\n19", "Wed\n20", "Thu\n21", "Fri\n22", "Sat\n23", "Sun\n24")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(days) { dayText ->
                    val isToday = dayText.startsWith("Tue")
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isToday) HarvestGold else HuskCard,
                        border = BorderStroke(1.dp, if (isToday) HarvestGold else HuskBorder),
                        modifier = Modifier.width(52.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = dayText,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isToday) Canopy else MaterialTheme.colorScheme.onBackground,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Weather Deferral Alert Banner
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = StatusWarning,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Weather Intelligence Auto-Adjustment",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Canopy
                        )
                        Text(
                            text = "Irrigation task #2 deferred due to 80% heavy rain forecast on Thursday. Saving 45,000L water.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Canopy.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Tasks List
        item {
            SectionHeader(
                title = "Scheduled Farm Activities",
                badgeText = "${tasks.size} Tasks"
            )
        }

        items(tasks) { task ->
            val priorityColor = when (task.priority) {
                "Urgent" -> StatusWarning
                "High" -> HarvestGold
                else -> Sprout
            }

            TerraceCard(
                accentColor = if (task.isCompleted) Sprout else priorityColor,
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.toggleTaskCompletion(task.id, task.isCompleted) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (task.isCompleted) Sprout else Canopy.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (task.isCompleted) Canopy.copy(alpha = 0.5f) else Canopy
                            )
                            Text(
                                text = "${task.category} · Due: ${task.dueDate}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Canopy.copy(alpha = 0.65f)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = priorityColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, priorityColor)
                        ) {
                            Text(
                                text = task.priority,
                                style = MaterialTheme.typography.labelMedium,
                                color = priorityColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Canopy.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }

                    if (task.isWeatherDeferred) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "⚠️ ${task.weatherNote}",
                            style = MaterialTheme.typography.labelMedium,
                            color = StatusWarning,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    if (isAddTaskOpen) {
        AddTaskDialog(
            onDismiss = { isAddTaskOpen = false },
            onConfirm = { title, desc, cat, due, prio ->
                viewModel.addTask(title, desc, cat, due, prio)
                isAddTaskOpen = false
            }
        )
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, cat: String, due: String, prio: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Spray") }
    var dueDate by remember { mutableStateOf("Tomorrow, 7:00 AM") }
    var priority by remember { mutableStateOf("High") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Scheduled Farm Task", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title (e.g. Zinc spray)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Details & Dosage") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Timing (e.g. Tomorrow morning)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, desc, category, dueDate, priority)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Sprout)
            ) {
                Text("Schedule Task", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
