package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.ui.EarnMateViewModel
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandWarning

@Composable
fun ReferralScreen(viewModel: EarnMateViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val config by viewModel.appConfig.collectAsState()
    val referrals by viewModel.referrals.collectAsState()
    val context = LocalContext.current

    val referralCode = user?.referralCode ?: "EARN9823"
    val shareText = "Hey! Join EarnMate India using my code '$referralCode' to get ₹${config.referralRewardRupees.toInt()} instant welcome reward on completing tasks: https://earnmate.in/invite/$referralCode"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Refer & Earn Bonus 🤝",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Referral Code Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("YOUR UNIQUE REFERRAL CODE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Surface(
                    color = BrandPrimary.copy(0.15f),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandPrimary)
                ) {
                    Text(
                        text = referralCode,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandPrimary,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp).testTag("referral_code_display")
                    )
                }

                Text(
                    text = "Earn ₹${config.referralRewardRupees.toInt()} for every friend who signs up & completes their first task!",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share EarnMate Code"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Link")
                    }

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Referral Code", referralCode)
                            clipboard.setPrimaryClip(clip)
                            viewModel.showSnackbar("Referral code '$referralCode' copied!")
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Code")
                    }
                }
            }
        }

        // Stats Summary Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GlassCard(modifier = Modifier.weight(1f)) {
                Column {
                    Text("Total Friends", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${referrals.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            GlassCard(modifier = Modifier.weight(1f)) {
                Column {
                    Text("Referral Earned", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${referrals.sumOf { it.earnedForReferrer }.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandAccent)
                }
            }
        }

        // Referral History
        Text("Referred Users History", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        if (referrals.isEmpty()) {
            EmptyStateCard("No Referrals Yet", "Share your code above with friends to start earning referral rewards!")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                referrals.forEach { ref ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Surface(shape = CircleShape, color = BrandPrimary.copy(0.2f), modifier = Modifier.size(36.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(ref.username.take(1).uppercase(), fontWeight = FontWeight.Bold, color = BrandPrimary)
                                    }
                                }
                                Column {
                                    Text("@${ref.username}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(ref.status, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Text("+₹${ref.earnedForReferrer.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrandAccent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyCheckInScreen(viewModel: EarnMateViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val config by viewModel.appConfig.collectAsState()

    val streak = user?.currentStreak ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Daily Streak Rewards 🔥",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(color = BrandWarning.copy(0.2f), shape = CircleShape, modifier = Modifier.size(64.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = BrandWarning, modifier = Modifier.size(36.dp))
                    }
                }

                Text("$streak Day Streak!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)

                Text(
                    text = "Check in every single day to maximize your streak bonus. Reaching Day 7 gives you a massive ₹20 bonus reward!",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = { viewModel.claimDailyCheckIn() },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("claim_daily_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Claim Today's Bonus", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 7-Day Grid Breakdown
        Text("7-Day Streak Rewards Calendar", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..7).forEach { day ->
                val dayReward = config.dailyBonusBaseRupees + (day - 1) * 2.0
                val isCurrent = (streak % 7) == (day - 1)
                val isClaimed = (streak % 7) >= day

                Surface(
                    color = if (isCurrent) BrandPrimary.copy(0.2f) else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isCurrent) 2.dp else 1.dp,
                        color = if (isCurrent) BrandPrimary else MaterialTheme.colorScheme.outline.copy(0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(
                                imageVector = if (isClaimed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isClaimed) BrandAccent else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text("Day $day Streak", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Text("₹${dayReward.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = BrandAccent)
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardScreen(viewModel: EarnMateViewModel) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    var selectedTab by remember { mutableStateOf("Weekly") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Leaderboard 🏆",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Hide me", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Switch(
                    checked = user?.hideFromLeaderboard == true,
                    onCheckedChange = { viewModel.toggleLeaderboardVisibility(it) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val tabs = listOf("Weekly", "Monthly", "All-Time")
            tabs.forEach { t ->
                FilterChip(
                    selected = selectedTab == t,
                    onClick = { selectedTab = t },
                    label = { Text(t) }
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(leaderboard) { entry ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (entry.isCurrentUser) BrandPrimary.copy(0.15f) else MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                color = when (entry.rank) {
                                    1 -> Color(0xFFFFD700)
                                    2 -> Color(0xFFC0C0C0)
                                    3 -> Color(0xFFCD7F32)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "#${entry.rank}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (entry.rank <= 3) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = entry.username,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (entry.isCurrentUser) BrandPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${entry.completedTasks} tasks completed",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "₹${String.format("%.2f", entry.totalEarned)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(viewModel: EarnMateViewModel) {
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Notifications Center 🔔",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (notifications.isEmpty()) {
            EmptyStateCard("No Notifications", "You are all caught up!")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(notifications) { notif ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.markNotificationRead(notif.id) },
                        backgroundColor = if (!notif.isRead) BrandPrimary.copy(0.1f) else MaterialTheme.colorScheme.surface
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = notif.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (!notif.isRead) {
                                    StatusBadge("NEW", BrandSecondary)
                                }
                            }

                            Text(
                                text = notif.message,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
