package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.EarnMateViewModel
import com.example.ui.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AdminGuard(
    viewModel: EarnMateViewModel,
    content: @Composable () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    if (currentUser?.isAdmin != true) {
        LaunchedEffect(Unit) {
            viewModel.showSnackbar("Access Denied: Admin privileges required.")
            viewModel.navigateTo(Screen.Dashboard)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Access Denied",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Access Denied",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "You do not have permission to view the Admin Console.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { viewModel.navigateTo(Screen.Dashboard) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Return to Dashboard")
                }
            }
        }
    } else {
        content()
    }
}

@Composable
fun AdminDashboardScreen(viewModel: EarnMateViewModel) {
    AdminGuard(viewModel = viewModel) {
        val users by viewModel.allUsers.collectAsState()
        val submissions by viewModel.submissions.collectAsState()
        val withdrawals by viewModel.withdrawals.collectAsState()
        val tasks by viewModel.tasks.collectAsState()

        val pendingSubmissions = submissions.count { it.status == SubmissionStatus.PENDING }
        val pendingWithdrawals = withdrawals.count { it.status == WithdrawalStatus.REQUESTED || it.status == WithdrawalStatus.PROCESSING }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = ModuleColors.AdminAccent.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = ModuleColors.AdminAccent)
                    }
                }
                Column {
                    Text("Admin Console ⚡", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Backend Moderation & Verification Hub", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Button(
                onClick = { viewModel.toggleAdminMode(false) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text("Exit Admin", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Metrics Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GlassCard(
                modifier = Modifier.weight(1f).clickable { viewModel.navigateTo(Screen.AdminSubmissions) },
                borderColor = ModuleColors.TasksAccent.copy(alpha = 0.4f)
            ) {
                Text("Pending Proofs", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$pendingSubmissions", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ModuleColors.TasksAccent)
            }

            GlassCard(
                modifier = Modifier.weight(1f).clickable { viewModel.navigateTo(Screen.AdminWithdrawals) },
                borderColor = ModuleColors.WalletAccent.copy(alpha = 0.4f)
            ) {
                Text("Pending Payouts", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$pendingWithdrawals", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ModuleColors.WalletAccent)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GlassCard(modifier = Modifier.weight(1f), borderColor = ModuleColors.AdminAccent.copy(alpha = 0.3f)) {
                Text("Total Users", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${users.size}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }

            GlassCard(modifier = Modifier.weight(1f), borderColor = ModuleColors.HomeAccent.copy(alpha = 0.3f)) {
                Text("Active Tasks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${tasks.size}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ModuleColors.HomeAccent)
            }
        }

        // Admin Action Links
        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = ModuleColors.AdminAccent.copy(alpha = 0.3f)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("EarnMate Admin Operations Center ⚡", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ModuleColors.AdminAccent)

                AdminActionRow(Icons.Default.Group, "User Management & Accounts") {
                    viewModel.navigateTo(Screen.AdminUsers)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.AddTask, "Task Management & Submissions") {
                    viewModel.navigateTo(Screen.AdminTasks)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.CardGiftcard, "Reward Rules & Daily Bonus Config") {
                    viewModel.navigateTo(Screen.AdminRewards)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.SportsEsports, "Games & Luckydraw Rules") {
                    viewModel.navigateTo(Screen.AdminGames)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.VerifiedUser, "Withdrawal Payout Approvals ($pendingWithdrawals)") {
                    viewModel.navigateTo(Screen.AdminWithdrawals)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.BusinessCenter, "Freelancers & Service Gigs") {
                    viewModel.navigateTo(Screen.AdminFreelancer)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.Work, "Job Moderation & Listings") {
                    viewModel.navigateTo(Screen.AdminJobs)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.RateReview, "Reel Video Moderation") {
                    viewModel.navigateTo(Screen.AdminReels)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.Report, "Central Reports Console") {
                    viewModel.navigateTo(Screen.AdminReports)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.WorkspacePremium, "Premium Membership System 👑") {
                    viewModel.navigateTo(Screen.AdminPremium)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.Campaign, "Ads & AdMob Settings 📺") {
                    viewModel.navigateTo(Screen.AdminAds)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.NotificationsActive, "Push Broadcast Notifications 🔔") {
                    viewModel.navigateTo(Screen.AdminNotifications)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.Tune, "System Settings & Maintenance Mode") {
                    viewModel.navigateTo(Screen.AdminSettings)
                }
                HorizontalDivider()
                AdminActionRow(Icons.Default.History, "Audit Activity Log 📜") {
                    viewModel.navigateTo(Screen.AdminActivity)
                }
            }
        }
    }
}
}

@Composable
fun AdminActionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = ModuleColors.AdminAccent)
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null)
    }
}

@Composable
fun AdminSubmissionsScreen(viewModel: EarnMateViewModel) {
    AdminGuard(viewModel = viewModel) {
        val submissions by viewModel.submissions.collectAsState()
        val pendingSubmissions = submissions.filter { it.status == SubmissionStatus.PENDING }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                Text("Verify Task Proofs 🔍", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            if (pendingSubmissions.isEmpty()) {
                EmptyStateCard("All Proofs Verified", "There are no pending task proof submissions waiting for admin approval.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                    items(pendingSubmissions) { sub ->
                        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = ModuleColors.AdminAccent.copy(alpha = 0.3f)) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(sub.taskTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text("₹${sub.rewardRupees.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = ModuleColors.WalletAccent)
                                }

                                Text("User: ${sub.userName} (${sub.userEmail})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Submitted Proof Content:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(sub.proofContent, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { viewModel.adminApproveSubmission(sub.id, "Verified by Admin") },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.WalletAccent)
                                    ) {
                                        Text("Approve & Credit ₹${sub.rewardRupees.toInt()}", fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.adminRejectSubmission(sub.id, "Proof details unverified.") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reject", color = StatusRejected, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminWithdrawalsScreen(viewModel: EarnMateViewModel) {
    AdminGuard(viewModel = viewModel) {
        val withdrawals by viewModel.withdrawals.collectAsState()
        val pendingWithdrawals = withdrawals.filter { it.status == WithdrawalStatus.REQUESTED || it.status == WithdrawalStatus.PROCESSING }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                Text("Withdrawal Payouts Approver 💳", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            if (pendingWithdrawals.isEmpty()) {
                EmptyStateCard("No Pending Payouts", "All withdrawal requests have been processed.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                    items(pendingWithdrawals) { req ->
                        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = ModuleColors.WalletAccent.copy(alpha = 0.3f)) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("${req.method.label} Request", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text("₹${req.amountRupees.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = ModuleColors.WalletAccent)
                                }

                                Text("User: ${req.userName} (${req.userEmail})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Payout Details: ${req.payoutDetails}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ModuleColors.HomeAccent)

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { viewModel.adminProcessWithdrawal(req.id, WithdrawalStatus.PAID, "UTR-BANK-" + (100000..999999).random(), null) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.WalletAccent)
                                    ) {
                                        Text("Mark Paid & Dispatch UTR", fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.adminProcessWithdrawal(req.id, WithdrawalStatus.REJECTED, null, "Invalid bank details") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reject & Refund", color = StatusRejected, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminTasksScreen(viewModel: EarnMateViewModel) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(TaskCategory.SURVEY) }
    var rewardStr by remember { mutableStateOf("45") }
    var minutesStr by remember { mutableStateOf("5") }
    var instructionInput by remember { mutableStateOf("Complete all questions genuinely.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("Publish New Task ➕", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = ModuleColors.TasksAccent.copy(alpha = 0.3f)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task Title") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(80.dp))
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = rewardStr, onValueChange = { rewardStr = it }, label = { Text("Reward (₹)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = minutesStr, onValueChange = { minutesStr = it }, label = { Text("Duration (Mins)") }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = instructionInput, onValueChange = { instructionInput = it }, label = { Text("Instructions") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val reward = rewardStr.toDoubleOrNull() ?: 0.0
                    val mins = minutesStr.toIntOrNull() ?: 5
                    viewModel.adminCreateTask(title, desc, category, reward, mins, TaskDifficulty.EASY, ProofType.TEXT_INPUT, listOf(instructionInput))
                    title = ""
                    desc = ""
                },
                enabled = title.isNotBlank() && desc.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.TasksAccent)
            ) {
                Text("Publish Task to Marketplace", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdminSettingsScreen(viewModel: EarnMateViewModel) {
    val config by viewModel.appConfig.collectAsState()

    var minWdStr by remember { mutableStateOf(config.minimumWithdrawalRupees.toInt().toString()) }
    var refRewStr by remember { mutableStateOf(config.referralRewardRupees.toInt().toString()) }
    var dailyBonusStr by remember { mutableStateOf(config.dailyBonusBaseRupees.toInt().toString()) }
    var announcement by remember { mutableStateOf(config.appAnnouncement) }
    var maintenance by remember { mutableStateOf(config.maintenanceMode) }
    var adsEnabled by remember { mutableStateOf(config.adsEnabled) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("App System Configuration ⚙️", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = ModuleColors.AdminAccent.copy(alpha = 0.3f)) {
            OutlinedTextField(value = minWdStr, onValueChange = { minWdStr = it }, label = { Text("Minimum Withdrawal Limit (₹)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = refRewStr, onValueChange = { refRewStr = it }, label = { Text("Referral Reward Bonus (₹)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = dailyBonusStr, onValueChange = { dailyBonusStr = it }, label = { Text("Base Daily Check-in Bonus (₹)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = announcement, onValueChange = { announcement = it }, label = { Text("Global App Announcement Banner") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Enable Ad Placements", fontSize = 14.sp)
                Switch(checked = adsEnabled, onCheckedChange = { adsEnabled = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.adminUpdateConfig(
                        minWdStr.toDoubleOrNull() ?: 100.0,
                        refRewStr.toDoubleOrNull() ?: 25.0,
                        dailyBonusStr.toDoubleOrNull() ?: 2.0,
                        maintenance,
                        announcement,
                        adsEnabled
                    )
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.AdminAccent)
            ) {
                Text("Save Configuration", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdminGamesScreen(viewModel: EarnMateViewModel) {
    val configs by viewModel.gameConfigs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("Games & Rewards Controls 🎮", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Configure max daily plays, enable/disable games, and probability reward bounds.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        configs.forEach { config ->
            var isEnabled by remember(config) { mutableStateOf(config.isEnabled) }
            var maxPlaysStr by remember(config) { mutableStateOf(config.maxDailyPlays.toString()) }
            var minRewardStr by remember(config) { mutableStateOf(config.minRewardRupees.toString()) }
            var maxRewardStr by remember(config) { mutableStateOf(config.maxRewardRupees.toString()) }

            GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = ModuleColors.GamesAccent.copy(alpha = 0.3f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(config.gameType.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = maxPlaysStr,
                        onValueChange = { maxPlaysStr = it },
                        label = { Text("Max Daily Plays") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minRewardStr,
                        onValueChange = { minRewardStr = it },
                        label = { Text("Min ₹") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxRewardStr,
                        onValueChange = { maxRewardStr = it },
                        label = { Text("Max ₹") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.adminUpdateGameConfig(
                            type = config.gameType,
                            isEnabled = isEnabled,
                            maxDailyPlays = maxPlaysStr.toIntOrNull() ?: 5,
                            minReward = minRewardStr.toDoubleOrNull() ?: 0.5,
                            maxReward = maxRewardStr.toDoubleOrNull() ?: 10.0
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.GamesAccent)
                ) {
                    Text("Save ${config.gameType.title} Config", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminAdGateScreen(viewModel: EarnMateViewModel) {
    val config by viewModel.appConfig.collectAsState()
    val logs by viewModel.adGateLogs.collectAsState()

    var adGateEnabled by remember(config) { mutableStateOf(config.adGateEnabled) }
    var selectedProvider by remember(config) { mutableStateOf(config.adProvider) }
    var requireAdForTasks by remember(config) { mutableStateOf(config.requireAdForTasks) }
    var requireAdForOffers by remember(config) { mutableStateOf(config.requireAdForOffers) }
    var requireAdForGames by remember(config) { mutableStateOf(config.requireAdForGames) }
    var countdownStr by remember(config) { mutableStateOf(config.adCountdownDurationSeconds.toString()) }

    var selectedFilter by remember { mutableStateOf<AdResultStatus?>(null) }

    val filteredLogs = remember(logs, selectedFilter) {
        if (selectedFilter == null) logs else logs.filter { it.resultStatus == selectedFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Ad Gate Controls & Logs 📺", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Configure global & category Ad Gates, choose ad provider, and inspect real-time unlock audit logs.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = ModuleColors.AdminAccent.copy(alpha = 0.3f)) {
            Text("Global Ad Gate Settings", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ModuleColors.AdminAccent)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Master Ad Gate Switch", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Enables Ad Gate before task/game start", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = adGateEnabled, onCheckedChange = { adGateEnabled = it })
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Text("Ad Provider Engine", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = selectedProvider == "mock",
                    onClick = { selectedProvider = "mock" },
                    label = { Text("Mock Provider (Dev)") }
                )
                FilterChip(
                    selected = selectedProvider == "real",
                    onClick = { selectedProvider = "real" },
                    label = { Text("Real Ad SDK (AdMob/Unity)") }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Text("Category Specific Ad Gates", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Require Ad for Marketplace Tasks", fontSize = 13.sp)
                Switch(checked = requireAdForTasks, onCheckedChange = { requireAdForTasks = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Require Ad for Special Offers", fontSize = 13.sp)
                Switch(checked = requireAdForOffers, onCheckedChange = { requireAdForOffers = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Require Ad for Reward Games", fontSize = 13.sp)
                Switch(checked = requireAdForGames, onCheckedChange = { requireAdForGames = it })
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = countdownStr,
                onValueChange = { countdownStr = it },
                label = { Text("Ad Timer Duration (Seconds)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    viewModel.updateAdGateConfig(
                        enabled = adGateEnabled,
                        provider = selectedProvider,
                        forTasks = requireAdForTasks,
                        forOffers = requireAdForOffers,
                        forGames = requireAdForGames,
                        seconds = countdownStr.toIntOrNull() ?: 5
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.AdminAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Ad Gate Configuration", fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ad Gate Attempt Audit Logs (${filteredLogs.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { selectedFilter = null },
                label = { Text("All Logs") }
            )
            AdResultStatus.values().forEach { status ->
                FilterChip(
                    selected = selectedFilter == status,
                    onClick = { selectedFilter = status },
                    label = { Text(status.label) }
                )
            }
        }

        if (filteredLogs.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "No ad gate event logs found matching selected filter.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            filteredLogs.forEach { log ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                StatusBadge(log.targetType, ModuleColors.AdminAccent)
                                Text(log.targetTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "User: ${log.userId} • ${SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(log.timestamp))}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        StatusBadge(
                            text = log.resultStatus.label,
                            color = when (log.resultStatus) {
                                AdResultStatus.COMPLETED -> ModuleColors.WalletAccent
                                AdResultStatus.SKIPPED -> ModuleColors.TasksAccent
                                AdResultStatus.CLOSED_EARLY -> StatusRejected
                                AdResultStatus.FAILED -> Color.Gray
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Admin Premium System Management Screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPremiumScreen(viewModel: EarnMateViewModel) {
    AdminGuard(viewModel = viewModel) {
        val config by viewModel.premiumConfig.collectAsState()
        val plans by viewModel.premiumPlans.collectAsState()
        val users by viewModel.allUsers.collectAsState()
        val history by viewModel.premiumHistory.collectAsState()

        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf("System Config", "Plans (Prices)", "User Directory & Grants", "Activation History")

        var searchQuery by remember { mutableStateOf("") }

        // Grant Dialog State
        var showGrantDialog by remember { mutableStateOf(false) }
        var selectedUserForGrant by remember { mutableStateOf<UserProfile?>(null) }
        var selectedPlanId by remember { mutableStateOf("plan_monthly") }
        var customDaysInput by remember { mutableStateOf("30") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Premium Management Console 👑", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Manage memberships, grant plans, and configure pricing", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(
                    onClick = { viewModel.navigateTo(Screen.AdminDashboard) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("Admin Home")
                }
            }

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // System Config
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Enable Premium System", fontWeight = FontWeight.Bold)
                                        Text("Master switch for Gold features", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Switch(
                                        checked = config.systemEnabled,
                                        onCheckedChange = { viewModel.adminUpdatePremiumConfig(config.copy(systemEnabled = it)) }
                                    )
                                }

                                HorizontalDivider()

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Strict Free User Ad Gate", fontWeight = FontWeight.Bold)
                                        Text("Show ads to FREE users for unlockable tasks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Switch(
                                        checked = config.strictAdGateForFreeUsers,
                                        onCheckedChange = { viewModel.adminUpdatePremiumConfig(config.copy(strictAdGateForFreeUsers = it)) }
                                    )
                                }

                                HorizontalDivider()

                                Text("Feature Access Flags", fontWeight = FontWeight.Bold)
                                config.featureFlags.forEach { (flagKey, isEnabled) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(flagKey, fontSize = 13.sp)
                                        Switch(
                                            checked = isEnabled,
                                            onCheckedChange = {
                                                val updatedMap = config.featureFlags.toMutableMap()
                                                updatedMap[flagKey] = it
                                                viewModel.adminUpdatePremiumConfig(config.copy(featureFlags = updatedMap))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Plans Settings
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        plans.forEach { plan ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(plan.planName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFFFB300))
                                            Text("ID: ${plan.planId} • ${plan.durationDays} Days", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text("₹${plan.priceRupees.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    HorizontalDivider()
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Plan Status: ${if (plan.active) "ACTIVE" else "DISABLED"}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Switch(
                                            checked = plan.active,
                                            onCheckedChange = { viewModel.adminSavePremiumPlan(plan.copy(active = it)) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // User Directory & Grants
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search users by name, email or ID...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        val filteredUsers = remember(users, searchQuery) {
                            if (searchQuery.isBlank()) users
                            else users.filter { it.fullName.contains(searchQuery, ignoreCase = true) || it.email.contains(searchQuery, ignoreCase = true) || it.uid.contains(searchQuery, ignoreCase = true) }
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredUsers, key = { it.uid }) { u ->
                                val isUserPrem = u.isPremiumActive
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Text(u.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                    if (isUserPrem) {
                                                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFFD700).copy(alpha = 0.2f)) {
                                                            Text("👑 PREMIUM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                        }
                                                    }
                                                }
                                                Text(u.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }

                                            if (isUserPrem) {
                                                Button(
                                                    onClick = { viewModel.adminRevokePremium(u.uid) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Revoke", fontSize = 11.sp)
                                                }
                                            } else {
                                                Button(
                                                    onClick = {
                                                        selectedUserForGrant = u
                                                        showGrantDialog = true
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300), contentColor = Color.Black),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Grant Premium 👑", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        if (isUserPrem) {
                                            HorizontalDivider()
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Expires: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(u.premiumExpiryDate))}", fontSize = 11.sp, color = Color(0xFFFFB300))
                                                TextButton(onClick = { viewModel.adminExtendPremium(u.uid, 30) }) {
                                                    Text("+30 Days", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // History
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(history, key = { it.recordId }) { rec ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(rec.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("₹${rec.pricePaidRupees.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Text("Plan: ${rec.planName} • Txn: ${rec.paymentTxnId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Date: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(rec.startDate))}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showGrantDialog && selectedUserForGrant != null) {
            AlertDialog(
                onDismissRequest = { showGrantDialog = false },
                title = { Text("Grant Premium to ${selectedUserForGrant?.fullName}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Select Premium Plan:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        plans.forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPlanId = p.planId }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    RadioButton(selected = selectedPlanId == p.planId, onClick = { selectedPlanId = p.planId })
                                    Text(p.planName)
                                }
                                Text("₹${p.priceRupees.toInt()}", fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedTextField(
                            value = customDaysInput,
                            onValueChange = { customDaysInput = it },
                            label = { Text("Custom Duration (Days)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val days = customDaysInput.toIntOrNull() ?: 30
                            viewModel.adminGrantPremium(selectedUserForGrant!!.uid, selectedPlanId, days)
                            showGrantDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300), contentColor = Color.Black)
                    ) {
                        Text("Confirm Grant 👑", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showGrantDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// ==========================================
// DEDICATED ADMIN LOGIN & NEW ADMIN MODULES
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(viewModel: EarnMateViewModel) {
    var email by remember { mutableStateOf("admin@earnmate.in") }
    var password by remember { mutableStateOf("admin123") }
    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1931),
                        Color(0xFF0F2027),
                        Color(0xFF2C5364)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = ModuleColors.AdminAccent.copy(alpha = 0.2f),
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Security Portal",
                        tint = ModuleColors.AdminAccent,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Text(
                text = "EarnMate India Admin",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Central Management & Operations Console",
                fontSize = 13.sp,
                color = Color.LightGray
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White.copy(alpha = 0.08f),
                borderColor = ModuleColors.AdminAccent.copy(alpha = 0.4f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(
                        color = Color(0xFF1E3A8A).copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                            Text(
                                "Restricted Access: Authorized Administrators Only.",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Admin Email", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ModuleColors.AdminAccent) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ModuleColors.AdminAccent,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = ModuleColors.AdminAccent,
                            cursorColor = ModuleColors.AdminAccent
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ModuleColors.AdminAccent) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color.LightGray
                                )
                            }
                        },
                        visualTransformation = if (showPassword) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ModuleColors.AdminAccent,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = ModuleColors.AdminAccent,
                            cursorColor = ModuleColors.AdminAccent
                        )
                    )

                    Button(
                        onClick = { viewModel.adminAuthenticate(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.AdminAccent, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null)
                            Text("Authenticate & Enter Admin Console", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    TextButton(
                        onClick = { viewModel.navigateTo(Screen.Dashboard) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("← Switch to User Application Mode", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(viewModel: EarnMateViewModel) {
    AdminGuard(viewModel = viewModel) {
        val users by viewModel.allUsers.collectAsState()
        var searchQuery by remember { mutableStateOf("") }
        var selectedFilter by remember { mutableStateOf("ALL") }

        var selectedUserForDetail by remember { mutableStateOf<UserProfile?>(null) }
        var showSuspendDialog by remember { mutableStateOf(false) }
        var userToSuspend by remember { mutableStateOf<UserProfile?>(null) }
        var suspendReason by remember { mutableStateOf("") }

        val filteredUsers = remember(users, searchQuery, selectedFilter) {
            users.filter { u ->
                val matchesQuery = searchQuery.isBlank() ||
                        u.fullName.contains(searchQuery, ignoreCase = true) ||
                        u.email.contains(searchQuery, ignoreCase = true) ||
                        u.phone.contains(searchQuery, ignoreCase = true) ||
                        u.uid.contains(searchQuery, ignoreCase = true)

                val matchesFilter = when (selectedFilter) {
                    "ACTIVE" -> !u.isSuspended
                    "SUSPENDED" -> u.isSuspended
                    "PREMIUM" -> u.isPremiumActive
                    else -> true
                }
                matchesQuery && matchesFilter
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Column {
                        Text("User Management 👥", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Total registered users: ${users.size}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, email, phone, or UID...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = selectedFilter == "ALL", onClick = { selectedFilter = "ALL" }, label = { Text("All (${users.size})") })
                FilterChip(selected = selectedFilter == "ACTIVE", onClick = { selectedFilter = "ACTIVE" }, label = { Text("Active") })
                FilterChip(selected = selectedFilter == "SUSPENDED", onClick = { selectedFilter = "SUSPENDED" }, label = { Text("Suspended") })
                FilterChip(selected = selectedFilter == "PREMIUM", onClick = { selectedFilter = "PREMIUM" }, label = { Text("Premium 👑") })
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredUsers, key = { it.uid }) { u ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(u.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        if (u.isPremiumActive) {
                                            StatusBadge("👑 PREMIUM", Color(0xFFFFD700))
                                        }
                                        if (u.isAdmin) {
                                            StatusBadge("ADMIN", ModuleColors.AdminAccent)
                                        }
                                    }
                                    Text("${u.email} • ${u.phone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                StatusBadge(
                                    text = if (u.isSuspended) "SUSPENDED" else "ACTIVE",
                                    color = if (u.isSuspended) StatusRejected else StatusApproved
                                )
                            }

                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Wallet: ₹${u.availableBalance.toInt()} | Earned: ₹${u.totalEarned.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("UID: ${u.uid} | Referral Code: ${u.referralCode}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { selectedUserForDetail = u },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Details", fontSize = 11.sp)
                                    }

                                    if (u.isSuspended) {
                                        Button(
                                            onClick = { viewModel.adminUnsuspendUser(u.uid) },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusApproved),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Unsuspend", fontSize = 11.sp, color = Color.White)
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                userToSuspend = u
                                                showSuspendDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusRejected),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("Suspend", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Suspend Reason Dialog
        if (showSuspendDialog && userToSuspend != null) {
            AlertDialog(
                onDismissRequest = { showSuspendDialog = false },
                title = { Text("Suspend Account: ${userToSuspend?.fullName}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Reason for suspension:", fontSize = 12.sp)
                        OutlinedTextField(
                            value = suspendReason,
                            onValueChange = { suspendReason = it },
                            placeholder = { Text("e.g. Fraudulent activity, fake task proofs...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.adminSuspendUser(userToSuspend!!.uid, suspendReason)
                            showSuspendDialog = false
                            suspendReason = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRejected)
                    ) {
                        Text("Confirm Suspend", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSuspendDialog = false }) { Text("Cancel") }
                }
            )
        }

        // User Detail Dialog
        selectedUserForDetail?.let { u ->
            AlertDialog(
                onDismissRequest = { selectedUserForDetail = null },
                title = { Text("User Profile Details: ${u.fullName}") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("User ID: ${u.uid}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Email: ${u.email}", fontSize = 12.sp)
                        Text("Phone: ${u.phone}", fontSize = 12.sp)
                        Text("Account Status: ${if (u.isSuspended) "SUSPENDED 🚫" else "ACTIVE ✅"}", fontSize = 12.sp)
                        Text("Membership: ${u.membershipType} (Premium Active: ${u.isPremiumActive})", fontSize = 12.sp)
                        HorizontalDivider()
                        Text("Available Wallet Balance: ₹${u.availableBalance}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Total Lifetime Earnings: ₹${u.totalEarned}", fontSize = 12.sp)
                        Text("Total Tasks Completed: ${u.completedTasksCount}", fontSize = 12.sp)
                        Text("Referral Code: ${u.referralCode}", fontSize = 12.sp)
                        Text("Referred By: ${u.referredByCode?.ifEmpty { "None" } ?: "None"}", fontSize = 12.sp)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedUserForDetail = null }) { Text("Close") }
                }
            )
        }
    }
}

@Composable
fun AdminRewardsScreen(viewModel: EarnMateViewModel) {
    AdminGuard(viewModel = viewModel) {
        val config by viewModel.appConfig.collectAsState()

        var dailyBonusStr by remember(config) { mutableStateOf(config.dailyBonusBaseRupees.toString()) }
        var referralBonusStr by remember(config) { mutableStateOf(config.referralRewardRupees.toString()) }
        var streakMultiplierStr by remember { mutableStateOf("1.2") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Reward Management 🎁", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Global Reward Rules & Thresholds", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ModuleColors.AdminAccent)

                    OutlinedTextField(
                        value = dailyBonusStr,
                        onValueChange = { dailyBonusStr = it },
                        label = { Text("Base Daily Bonus (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = referralBonusStr,
                        onValueChange = { referralBonusStr = it },
                        label = { Text("Referral Bonus per Active Sign-up (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = streakMultiplierStr,
                        onValueChange = { streakMultiplierStr = it },
                        label = { Text("Daily Streak Multiplier") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            viewModel.adminUpdateConfig(
                                minWd = config.minimumWithdrawalRupees,
                                refReward = referralBonusStr.toDoubleOrNull() ?: 10.0,
                                dailyBonus = dailyBonusStr.toDoubleOrNull() ?: 5.0,
                                maint = config.maintenanceMode,
                                ann = config.appAnnouncement,
                                ads = config.adsEnabled
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.AdminAccent)
                    ) {
                        Text("Save Reward Configurations", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminJobsScreen(viewModel: EarnMateViewModel) {
    AdminGuard(viewModel = viewModel) {
        val jobs by viewModel.freelanceJobs.collectAsState()
        var searchQuery by remember { mutableStateOf("") }

        val filteredJobs = remember(jobs, searchQuery) {
            if (searchQuery.isBlank()) jobs
            else jobs.filter { it.title.contains(searchQuery, ignoreCase = true) || it.clientName.contains(searchQuery, ignoreCase = true) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Job Listing Moderation 📝", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search client jobs by title or client name...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredJobs, key = { it.id }) { job ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("₹${job.budgetRupees.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Text("Client: ${job.clientName} • Category: ${job.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(job.description, fontSize = 12.sp, maxLines = 2)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(job.status.name, ModuleColors.AdminAccent)
                                Button(
                                    onClick = { viewModel.adminDeleteJobListing(job.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusRejected)
                                ) {
                                    Text("Remove Listing", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminReportsScreen(viewModel: EarnMateViewModel) {
    AdminGuard(viewModel = viewModel) {
        val reports by viewModel.centralReports.collectAsState()
        var selectedCategory by remember { mutableStateOf<ReportCategory?>(null) }
        var selectedStatus by remember { mutableStateOf<ReportStatus?>(null) }

        var reportToResolve by remember { mutableStateOf<CentralReport?>(null) }
        var adminNotesInput by remember { mutableStateOf("") }
        var targetStatus by remember { mutableStateOf(ReportStatus.RESOLVED) }

        val filtered = remember(reports, selectedCategory, selectedStatus) {
            reports.filter { r ->
                (selectedCategory == null || r.category == selectedCategory) &&
                        (selectedStatus == null || r.status == selectedStatus)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Central Reports Console 🚩", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = selectedCategory == null, onClick = { selectedCategory = null }, label = { Text("All Categories") })
                ReportCategory.values().forEach { cat ->
                    FilterChip(selected = selectedCategory == cat, onClick = { selectedCategory = cat }, label = { Text(cat.label) })
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filtered, key = { it.id }) { rep ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(rep.category.label, ModuleColors.AdminAccent)
                                StatusBadge(
                                    text = rep.status.label,
                                    color = when (rep.status) {
                                        ReportStatus.OPEN -> ModuleColors.TasksAccent
                                        ReportStatus.UNDER_REVIEW -> ModuleColors.WalletAccent
                                        ReportStatus.RESOLVED -> StatusApproved
                                        ReportStatus.REJECTED -> StatusRejected
                                    }
                                )
                            }

                            Text(rep.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Reporter: ${rep.reporterName} | Target: ${rep.targetName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(rep.description, fontSize = 12.sp)

                            if (rep.adminNotes.isNotEmpty()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Admin Notes: ${rep.adminNotes}", fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                                }
                            }

                            Button(
                                onClick = {
                                    reportToResolve = rep
                                    adminNotesInput = rep.adminNotes
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Action Report", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        reportToResolve?.let { rep ->
            AlertDialog(
                onDismissRequest = { reportToResolve = null },
                title = { Text("Resolve Report: ${rep.title}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Set Report Status:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(selected = targetStatus == ReportStatus.UNDER_REVIEW, onClick = { targetStatus = ReportStatus.UNDER_REVIEW }, label = { Text("Under Review") })
                            FilterChip(selected = targetStatus == ReportStatus.RESOLVED, onClick = { targetStatus = ReportStatus.RESOLVED }, label = { Text("Resolve") })
                            FilterChip(selected = targetStatus == ReportStatus.REJECTED, onClick = { targetStatus = ReportStatus.REJECTED }, label = { Text("Reject") })
                        }

                        OutlinedTextField(
                            value = adminNotesInput,
                            onValueChange = { adminNotesInput = it },
                            label = { Text("Internal Admin Notes") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.adminResolveReport(rep.id, targetStatus, adminNotesInput)
                            reportToResolve = null
                        }
                    ) {
                        Text("Save Status")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { reportToResolve = null }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun AdminAdsScreen(viewModel: EarnMateViewModel) {
    AdminGuard(viewModel = viewModel) {
        val config by viewModel.appConfig.collectAsState()

        var adsEnabled by remember(config) { mutableStateOf(config.adsEnabled) }
        var bannerEnabled by remember(config) { mutableStateOf(config.bannerAdsEnabled) }
        var interstitialEnabled by remember(config) { mutableStateOf(config.interstitialAdsEnabled) }
        var rewardedEnabled by remember(config) { mutableStateOf(config.rewardedAdsEnabled) }
        var adGateEnabled by remember(config) { mutableStateOf(config.adGateEnabled) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("AdMob & Ads Config 📺", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Ad Configuration Switches", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ModuleColors.AdminAccent)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Master Ads Switch", fontWeight = FontWeight.Bold)
                        Switch(checked = adsEnabled, onCheckedChange = { adsEnabled = it })
                    }

                    HorizontalDivider()

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Banner Ads")
                        Switch(checked = bannerEnabled, onCheckedChange = { bannerEnabled = it })
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Interstitial Ads")
                        Switch(checked = interstitialEnabled, onCheckedChange = { interstitialEnabled = it })
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Rewarded Ads")
                        Switch(checked = rewardedEnabled, onCheckedChange = { rewardedEnabled = it })
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Ad Gate Controls")
                        Switch(checked = adGateEnabled, onCheckedChange = { adGateEnabled = it })
                    }

                    Button(
                        onClick = {
                            viewModel.adminUpdateAdsConfig(adsEnabled, bannerEnabled, interstitialEnabled, rewardedEnabled, adGateEnabled)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.AdminAccent)
                    ) {
                        Text("Save AdMob Settings", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminNotificationsScreen(viewModel: EarnMateViewModel) {
    AdminGuard(viewModel = viewModel) {
        val broadcasts by viewModel.broadcastNotifications.collectAsState()

        var titleInput by remember { mutableStateOf("") }
        var messageInput by remember { mutableStateOf("") }
        var selectedAudience by remember { mutableStateOf(NotificationAudience.ALL_USERS) }
        var targetUserIdInput by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Broadcast Push Notifications 🔔", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Send Broadcast Message", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ModuleColors.AdminAccent)

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Notification Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        label = { Text("Message Body") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Target Audience:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NotificationAudience.values().forEach { aud ->
                            FilterChip(
                                selected = selectedAudience == aud,
                                onClick = { selectedAudience = aud },
                                label = { Text(aud.label) }
                            )
                        }
                    }

                    if (selectedAudience == NotificationAudience.SPECIFIC_USER) {
                        OutlinedTextField(
                            value = targetUserIdInput,
                            onValueChange = { targetUserIdInput = it },
                            label = { Text("Target User ID / Email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = {
                            if (titleInput.isBlank() || messageInput.isBlank()) {
                                viewModel.showSnackbar("Please fill title and message.")
                                return@Button
                            }
                            viewModel.adminSendNotification(titleInput, messageInput, selectedAudience, targetUserIdInput)
                            titleInput = ""
                            messageInput = ""
                            targetUserIdInput = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.AdminAccent)
                    ) {
                        Text("Dispatch Broadcast Notification 🚀", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text("Broadcast History (${broadcasts.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(broadcasts, key = { it.id }) { b ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(b.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                StatusBadge(b.targetAudience.label, ModuleColors.AdminAccent)
                            }
                            Text(b.message, fontSize = 12.sp)
                            Text(
                                "Sent: ${SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(b.sentAt))}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminActivityScreen(viewModel: EarnMateViewModel) {
    AdminGuard(viewModel = viewModel) {
        val logs by viewModel.adminActivityLogs.collectAsState()
        var searchQuery by remember { mutableStateOf("") }

        val filteredLogs = remember(logs, searchQuery) {
            if (searchQuery.isBlank()) logs
            else logs.filter { it.action.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true) || it.adminEmail.contains(searchQuery, ignoreCase = true) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Audit Activity Logs 📜", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search logs by action, description, or admin email...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredLogs, key = { it.id }) { log ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatusBadge(log.action, ModuleColors.AdminAccent)
                                Text(
                                    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(log.timestamp)),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(log.description, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Admin: ${log.adminEmail} | Target: ${log.targetId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

