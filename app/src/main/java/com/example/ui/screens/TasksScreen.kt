package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.ui.EarnMateViewModel
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun TasksScreen(viewModel: EarnMateViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val submissions by viewModel.submissions.collectAsState()
    val selectedTask by viewModel.selectedTask.collectAsState()

    var selectedCategory by remember { mutableStateOf<TaskCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredTasks = tasks.filter { task ->
        val matchesCategory = selectedCategory == null || task.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = ModuleColors.TasksAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, tint = ModuleColors.TasksAccent)
                    }
                }
                Text(
                    text = "Task Marketplace 💼",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search surveys, testing, AI tasks...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("task_search_bar"),
                shape = RoundedCornerShape(14.dp)
            )

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All Categories") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ModuleColors.TasksAccent,
                            selectedLabelColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
                items(TaskCategory.values()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ModuleColors.TasksAccent,
                            selectedLabelColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            if (filteredTasks.isEmpty()) {
                EmptyStateCard(
                    title = "No Tasks Found",
                    description = "No matching tasks available under this filter right now. Try selecting another category or check back soon!",
                    icon = Icons.Default.Task,
                    accentColor = ModuleColors.TasksAccent
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredTasks) { task ->
                        val sub = submissions.find { it.taskId == task.id }
                        TaskCardItem(
                            task = task,
                            hasSubmission = sub != null,
                            submissionStatus = sub?.status?.name,
                            onSelect = {
                                viewModel.checkAndRunAdGate(
                                    targetType = "Task",
                                    targetId = task.id,
                                    targetTitle = task.title
                                ) {
                                    viewModel.selectTask(task)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Task Detail & Submission Dialog
        if (selectedTask != null) {
            TaskDetailDialog(
                task = selectedTask!!,
                onDismiss = { viewModel.clearSelectedTask() },
                onSubmitProof = { proof ->
                    viewModel.submitTaskProof(selectedTask!!.id, proof)
                }
            )
        }
    }
}

@Composable
fun TaskCardItem(
    task: TaskItem,
    hasSubmission: Boolean,
    submissionStatus: String?,
    onSelect: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("task_card_${task.id}"),
        borderColor = ModuleColors.TasksAccent.copy(alpha = 0.3f)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(task.category.label, ModuleColors.TasksAccent)

                if (hasSubmission) {
                    StatusBadge(submissionStatus ?: "UNDER REVIEW", StatusPending)
                } else {
                    Text(
                        text = "₹${task.rewardRupees.toInt()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ModuleColors.WalletAccent
                    )
                }
            }

            Text(
                text = task.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = task.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${task.estimatedMinutes}m", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(task.difficulty.label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Button(
                    onClick = onSelect,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (hasSubmission) MaterialTheme.colorScheme.surfaceVariant else ModuleColors.TasksAccent),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (hasSubmission) "View Status" else "Start Task",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasSubmission) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

@Composable
fun TaskDetailDialog(
    task: TaskItem,
    onDismiss: () -> Unit,
    onSubmitProof: (String) -> Unit
) {
    var proofText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                StatusBadge(task.category.label, ModuleColors.TasksAccent)
                Spacer(modifier = Modifier.height(6.dp))
                Text(task.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Reward: ₹${task.rewardRupees.toInt()}", fontSize = 14.sp, color = ModuleColors.WalletAccent, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(task.description, fontSize = 13.sp)

                HorizontalDivider()

                Text("Instructions:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                task.instructions.forEachIndexed { idx, step ->
                    Text("${idx + 1}. $step", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                HorizontalDivider()

                Text("Required Proof (${task.requiredProofType.label}):", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = proofText,
                    onValueChange = { proofText = it },
                    placeholder = { Text("Enter verification code, answer or screenshot URL here...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("proof_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = "🔒 Proofs are manually verified by admins within 24 hours before crediting rewards.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitProof(proofText) },
                enabled = proofText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.TasksAccent)
            ) {
                Text("Submit Proof", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
