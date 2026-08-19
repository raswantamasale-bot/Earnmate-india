package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.ui.EarnMateViewModel
import com.example.ui.Screen
import com.example.ui.components.AntiFraudNoticeCard
import com.example.ui.components.ConfigurableAdBannerCard
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun DashboardScreen(viewModel: EarnMateViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val config by viewModel.appConfig.collectAsState()

    val unreadNotifsCount = notifications.count { !it.isRead }
    val featuredTasks = tasks.filter { it.isFeatured }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Top Greeting Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { viewModel.navigateTo(Screen.Profile) },
                    color = ModuleColors.HomeAccent.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ModuleColors.HomeAccent)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = (user?.fullName?.take(1) ?: "U").uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ModuleColors.HomeAccent
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Namaste, ${user?.fullName?.split(" ")?.firstOrNull() ?: "User"}! 🙏",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (user?.isPremiumActive == true) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PremiumGold.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PremiumGold),
                                modifier = Modifier.clickable { viewModel.navigateTo(Screen.Premium) }
                            ) {
                                Text(
                                    text = "👑 PREMIUM",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PremiumGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "@${user?.username ?: "user"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BadgedBox(
                    badge = {
                        if (unreadNotifsCount > 0) {
                            Badge(containerColor = ModuleColors.ReelsAccent) { Text("$unreadNotifsCount") }
                        }
                    },
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.Notifications) }
                ) {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Notifications) }) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        // --- Announcement Banner ---
        if (config.appAnnouncement.isNotBlank()) {
            Surface(
                color = ModuleColors.HomeAccent.copy(alpha = 0.12f),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ModuleColors.HomeAccent.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = "Announcement", tint = ModuleColors.HomeAccent)
                    Text(
                        text = config.appAnnouncement,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // --- Ad Banner Card (bypassed for PREMIUM users) ---
        com.example.ui.components.AdBannerCard(viewModel = viewModel)

        // --- Premium Upgrade Banner for FREE Users ---
        if (user?.isPremiumActive != true) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(Screen.Premium) },
                borderColor = Color(0xFFFFD700).copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFB300))
                            }
                        }
                        Column {
                            Text("Upgrade to EarnMate Premium 👑", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFFB300))
                            Text("Ad-Free • Gold Badge • Featured Priority", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFFFB300))
                }
            }
        }

        // --- Main Balance Card (Emerald/Teal Financial Theme) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, ModuleColors.WalletAccent.copy(0.4f), RoundedCornerShape(24.dp))
                .testTag("main_balance_card"),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(ModuleColors.WalletGradient))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AVAILABLE BALANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f),
                            letterSpacing = 1.sp
                        )

                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { viewModel.navigateTo(Screen.DailyCheckIn) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                                Text(
                                    text = "${user?.currentStreak ?: 0} Day Streak",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "₹${String.format("%.2f", user?.availableBalance ?: 0.0)}",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Pending Review",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                            Text(
                                text = "₹${String.format("%.2f", user?.pendingRewards ?: 0.0)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Column {
                            Text(
                                text = "Lifetime Earned",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                            Text(
                                text = "₹${String.format("%.2f", user?.totalEarned ?: 0.0)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = { viewModel.navigateTo(Screen.Withdraw) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("withdraw_cta_button")
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = ModuleColors.WalletSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Withdraw", color = ModuleColors.WalletSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // --- Quick Actions Grid with Module-Specific Accent Colors ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val actions = listOf(
                Triple(Screen.Tasks, Icons.Default.TaskAlt to "Tasks", ModuleColors.TasksAccent),
                Triple(Screen.FreelanceHub, Icons.Default.Work to "Freelance", Color(0xFF1976D2)),
                Triple(Screen.GamesHub, Icons.Default.Casino to "Games", ModuleColors.GamesAccent),
                Triple(Screen.DailyCheckIn, Icons.Default.CalendarToday to "Streak", ModuleColors.DailyBonusAccent),
                Triple(Screen.Referrals, Icons.Default.Share to "Refer", ModuleColors.ReferralsAccent),
                Triple(Screen.Wallet, Icons.Default.AccountBalanceWallet to "Wallet", ModuleColors.WalletAccent)
            )
            actions.forEach { (screen, iconLabel, accentColor) ->
                val (icon, label) = iconLabel
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { viewModel.navigateTo(screen) }
                ) {
                    Surface(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(54.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = icon, contentDescription = label, tint = accentColor)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                }
            }
        }

        // --- Games & Rewards Highlight Banner (Pink/Magenta Accent) ---
        GlassCard(
            backgroundColor = MaterialTheme.colorScheme.surface,
            borderColor = ModuleColors.GamesAccent.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo(Screen.GamesHub) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = ModuleColors.GamesAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Casino, contentDescription = null, tint = ModuleColors.GamesAccent, modifier = Modifier.size(28.dp))
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Games & Rewards Hub", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Surface(color = ModuleColors.GamesAccent.copy(alpha = 0.18f), shape = RoundedCornerShape(6.dp)) {
                                Text("FUN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ModuleColors.GamesAccent, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Text("Spin Wheel, Scratch Cards & Trivia Quiz", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ModuleColors.GamesAccent)
            }
        }

        // --- Freelancer Marketplace Feature Card (Indigo Accent) ---
        GlassCard(
            backgroundColor = MaterialTheme.colorScheme.surface,
            borderColor = ModuleColors.FreelanceAccent.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo(Screen.FreelanceHub) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = ModuleColors.FreelanceAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Work, contentDescription = null, tint = ModuleColors.FreelanceAccent, modifier = Modifier.size(28.dp))
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Freelancer Marketplace", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Surface(color = ModuleColors.FreelanceAccent.copy(alpha = 0.18f), shape = RoundedCornerShape(6.dp)) {
                                Text("NEW", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ModuleColors.FreelanceAccent, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Text("Hire talent or offer gigs (Thumbnails, Scripts, Edit)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ModuleColors.FreelanceAccent)
            }
        }

        // --- Daily Progress Section ---
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = ModuleColors.TasksAccent.copy(alpha = 0.3f)
        ) {
            val completed = user?.completedTasksCount ?: 0
            val target = 5
            val progress = (completed.toFloat() / target.toFloat()).coerceAtMost(1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Goal Progress",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$completed of $target daily tasks completed",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = ModuleColors.TasksAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "₹10 Extra Bonus",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ModuleColors.TasksAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = ModuleColors.TasksAccent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // --- Featured Opportunities Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Featured Tasks & Offers",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "View All",
                fontSize = 12.sp,
                color = ModuleColors.TasksAccent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { viewModel.navigateTo(Screen.Tasks) }
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(featuredTasks) { task ->
                FeaturedTaskCard(task = task, onSelect = {
                    viewModel.selectTask(task)
                    viewModel.navigateTo(Screen.Tasks)
                })
            }
        }

        ConfigurableAdBannerCard(enabled = config.adsEnabled)

        // --- Recent Activity Ledger ---
        Text(
            text = "Recent Activity",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (transactions.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "No recent transactions. Complete your first task above to earn!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                transactions.take(4).forEach { tx ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = if (tx.amount >= 0) ModuleColors.WalletAccent.copy(0.15f) else ModuleColors.TasksAccent.copy(0.15f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        val icon = if (tx.amount >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
                                        val iconTint = if (tx.amount >= 0) ModuleColors.WalletAccent else ModuleColors.TasksAccent
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = iconTint,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = tx.description,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = tx.type.label,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = (if (tx.amount >= 0) "+₹" else "-₹") + String.format("%.2f", Math.abs(tx.amount)),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (tx.amount >= 0) ModuleColors.WalletAccent else ModuleColors.TasksAccent
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        AntiFraudNoticeCard()
        Spacer(modifier = Modifier.height(12.dp))
        com.example.ui.components.WatchRewardedAdCard(viewModel = viewModel)
        Spacer(modifier = Modifier.height(12.dp))
        com.example.ui.components.AdBannerCard(viewModel = viewModel)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FeaturedTaskCard(task: TaskItem, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onSelect() }
            .border(1.dp, ModuleColors.TasksAccent.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(task.category.label, ModuleColors.TasksAccent)
                Text(
                    text = "₹${task.rewardRupees.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ModuleColors.WalletAccent
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = task.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${task.estimatedMinutes} mins", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Text(
                    text = "Start >",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ModuleColors.TasksAccent
                )
            }
        }
    }
}
