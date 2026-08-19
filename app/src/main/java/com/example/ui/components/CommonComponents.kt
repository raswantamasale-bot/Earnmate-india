package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.data.ad.AdManager
import com.example.data.ad.RewardedAdManager
import com.example.data.ad.findActivity
import com.example.ui.EarnMateViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Card(
        modifier = modifier
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

data class BottomNavItemData(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val accentColor: Color
)

@Composable
fun EarnMateBottomNavigation(
    currentScreen: Screen,
    isAdminMode: Boolean,
    unreadNotificationCount: Int,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_nav_bar")
    ) {
        if (isAdminMode) {
            val adminItems = listOf(
                Screen.AdminDashboard to (Icons.Default.AdminPanelSettings to "Overview"),
                Screen.AdminTasks to (Icons.Default.AddTask to "Tasks"),
                Screen.AdminSubmissions to (Icons.Default.FactCheck to "Verify"),
                Screen.AdminWithdrawals to (Icons.Default.VerifiedUser to "Payouts"),
                Screen.AdminSettings to (Icons.Default.Tune to "Config")
            )
            adminItems.forEach { (screen, iconTitle) ->
                val (icon, label) = iconTitle
                val isSelected = currentScreen.route == screen.route
                val accent = ModuleColors.AdminAccent
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(screen) },
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label, fontSize = 11.sp, maxLines = 1, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = accent,
                        selectedTextColor = accent,
                        indicatorColor = accent.copy(alpha = 0.2f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        } else {
            val userItems = listOf(
                BottomNavItemData(Screen.Dashboard, Icons.Default.Home, Icons.Outlined.Home, "Home", ModuleColors.HomeAccent),
                BottomNavItemData(Screen.Tasks, Icons.Default.TaskAlt, Icons.Outlined.Task, "Tasks", ModuleColors.TasksAccent),
                BottomNavItemData(Screen.FreelanceHub, Icons.Default.Work, Icons.Outlined.WorkOutline, "Freelance", Color(0xFF1976D2)),
                BottomNavItemData(Screen.GamesHub, Icons.Default.Casino, Icons.Outlined.Casino, "Games", ModuleColors.GamesAccent),
                BottomNavItemData(Screen.Reels, Icons.Default.Movie, Icons.Outlined.Movie, "Reels", ModuleColors.ReelsAccent),
                BottomNavItemData(Screen.Wallet, Icons.Default.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "Wallet", ModuleColors.WalletAccent),
                BottomNavItemData(Screen.Profile, Icons.Default.Person, Icons.Outlined.Person, "Profile", ModuleColors.ProfileAccent)
            )
            userItems.forEach { item ->
                val isSelected = currentScreen.route == item.screen.route
                val itemAccent = item.accentColor
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(item.screen) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (item.screen == Screen.Dashboard && unreadNotificationCount > 0) {
                                    Badge(containerColor = ModuleColors.ReelsAccent) { Text("$unreadNotificationCount") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            maxLines = 1,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = itemAccent,
                        selectedTextColor = itemAccent,
                        indicatorColor = itemAccent.copy(alpha = 0.18f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AntiFraudNoticeCard() {
    GlassCard(
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        borderColor = ModuleColors.WalletAccent.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = ModuleColors.WalletAccent.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield",
                        tint = ModuleColors.WalletAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column {
                Text(
                    text = "100% Free & Legitimate Rewards",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "EarnMate is NOT a deposit, investment or gambling app. All rewards depend on real task verification.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun ConfigurableAdBannerCard(enabled: Boolean) {
    if (!enabled) return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    )
                )
            )
            .border(1.dp, BrandSecondary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    com.google.android.gms.ads.AdView(context).apply {
                        setAdSize(com.google.android.gms.ads.AdSize.BANNER)
                        this.adUnitId = AdManager.config.ADMOB_BANNER_ID
                        layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        adListener = object : com.google.android.gms.ads.AdListener() {
                            override fun onAdLoaded() {
                                android.util.Log.d("AdMobBanner", "Configurable Banner Ad loaded successfully. Unit ID: ${AdManager.config.ADMOB_BANNER_ID}")
                            }
                            override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                                android.util.Log.e("AdMobBanner", "Configurable Banner Ad failed to load: Code=${error.code}, Message='${error.message}', Domain='${error.domain}'")
                            }
                        }
                        try {
                            android.util.Log.d("AdMobBanner", "Requesting Configurable Banner Ad with ID: ${AdManager.config.ADMOB_BANNER_ID}")
                            loadAd(com.google.android.gms.ads.AdRequest.Builder().build())
                        } catch (e: Exception) {
                            android.util.Log.e("AdMobBanner", "Exception calling loadAd: ${e.message}", e)
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = BrandSecondary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "SPONSORED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandSecondary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "Explore verified skill courses & earning opportunities",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun WatchRewardedAdCard(viewModel: EarnMateViewModel) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val showAds = remember(user) { AdManager.shouldShowAds(user) }

    if (!showAds) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("watch_rewarded_ad_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = BrandPrimary.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = "Watch Ad for Bonus Reward 🎁",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Watch a video ad to earn +₹5.00 instantly",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = {
                    val activity = context.findActivity()
                    if (activity == null) {
                        viewModel.showSnackbar("Unable to display ad. Invalid activity context.")
                        return@Button
                    }
                    if (RewardedAdManager.isAdReady()) {
                        RewardedAdManager.showRewardedAd(
                            activity = activity,
                            onRewardEarned = { rewardItem ->
                                val amount = if (rewardItem.amount > 0) rewardItem.amount.toDouble() else 5.0
                                viewModel.claimRewardedAdBonus(amount, rewardItem.type.ifEmpty { "Video Ad" })
                            },
                            onFailedToShow = { errMsg ->
                                viewModel.showSnackbar(errMsg)
                            }
                        )
                    } else {
                        viewModel.showSnackbar("Loading Rewarded Ad... Please wait a moment.")
                        RewardedAdManager.loadRewardedAd(
                            context = context,
                            onLoaded = {
                                val act = context.findActivity()
                                if (act != null) {
                                    RewardedAdManager.showRewardedAd(
                                        activity = act,
                                        onRewardEarned = { rewardItem ->
                                            val amount = if (rewardItem.amount > 0) rewardItem.amount.toDouble() else 5.0
                                            viewModel.claimRewardedAdBonus(amount, rewardItem.type.ifEmpty { "Video Ad" })
                                        },
                                        onFailedToShow = { errMsg ->
                                            viewModel.showSnackbar(errMsg)
                                        }
                                    )
                                }
                            },
                            onFailed = { errMsg ->
                                viewModel.showSnackbar(errMsg)
                            }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("WATCH AD", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SkeletonLoaderCard() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            )
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    description: String,
    icon: ImageVector = Icons.Default.Inbox,
    accentColor: Color = ModuleColors.HomeAccent,
    actionButtonText: String? = null,
    onAction: (() -> Unit)? = null
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = accentColor.copy(alpha = 0.12f),
                shape = CircleShape,
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            if (actionButtonText != null && onAction != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(actionButtonText, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
