package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.ad.AdManager
import com.example.data.ad.InterstitialAdManager
import com.example.data.ad.findActivity
import com.example.data.model.*
import com.example.ui.EarnMateViewModel
import com.example.ui.Screen
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReelsScreen(viewModel: EarnMateViewModel) {
    val reels by viewModel.reels.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) } // 0 = Public Feed, 1 = My Reels
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedLanguage by remember { mutableStateOf<String?>(null) }

    var reportingReel by remember { mutableStateOf<Reel?>(null) }

    // Interstitial Ad Frequency & Lifecycle State for Reels Section
    var reelsCompletedCount by remember { mutableIntStateOf(0) }
    var isAdShowing by remember { mutableStateOf(false) }

    // Preload Interstitial Ad when opening Reels feed if not already loaded
    LaunchedEffect(Unit) {
        if (AdManager.shouldShowAds(currentUser) && !InterstitialAdManager.isAdReady()) {
            InterstitialAdManager.loadInterstitialAd(context)
        }
    }

    val approvedReels = remember(reels, selectedCategory, selectedLanguage) {
        reels.filter { it.status == ReelStatus.APPROVED }
            .filter { selectedCategory == null || it.category == selectedCategory }
            .filter { selectedLanguage == null || it.language == selectedLanguage }
    }

    val myReels = remember(reels, currentUser) {
        reels.filter { currentUser != null && it.userId == currentUser?.uid }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(Screen.CreateReel) },
                containerColor = BrandPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("upload_reel_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.VideoCall, contentDescription = "Upload Reel")
                    Text("Upload Reel", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header & Disclaimer Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            color = BrandPrimary.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Movie, contentDescription = null, tint = BrandPrimary)
                            }
                        }
                        Column {
                            Text("EarnMate Reels 🎬", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Watch short clips & earn +₹0.20 per view", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Surface(
                        color = BrandAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, BrandAccent.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Earn up to ₹${appConfig.maxDailyReelRewardRupees}/day",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Primary Tabs (Feed vs My Uploads)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = BrandPrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Public Feed (${approvedReels.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("My Uploads (${myReels.size})", fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (selectedTab == 0) {
                // Category & Language Filters
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("All Categories") }
                        )
                        ReelCategory.values().forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category.title,
                                onClick = { selectedCategory = category.title },
                                label = { Text(category.title) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedLanguage == null,
                            onClick = { selectedLanguage = null },
                            label = { Text("All Languages") }
                        )
                        ReelLanguage.values().forEach { lang ->
                            FilterChip(
                                selected = selectedLanguage == lang.label,
                                onClick = { selectedLanguage = lang.label },
                                label = { Text(lang.label) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (approvedReels.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateCard(
                            title = "No Approved Reels Found",
                            description = "No videos match your selected category filter or no reels are published yet.",
                            icon = Icons.Default.MovieFilter
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(approvedReels, key = { it.id }) { reel ->
                            ReelFeedItemCard(
                                reel = reel,
                                minWatchSeconds = appConfig.minWatchTimeSecondsForReward,
                                onLikeClick = { viewModel.toggleLikeReel(reel.id) },
                                onReportClick = { reportingReel = reel },
                                onWatchRewardClaim = { durationMs ->
                                    viewModel.logReelView(reel.id, durationMs)

                                    // Check natural transition threshold (every 4 reels watched)
                                    reelsCompletedCount++
                                    val activity = context.findActivity()
                                    val showAds = AdManager.shouldShowAds(currentUser)

                                    if (reelsCompletedCount >= 4 && showAds && activity != null && !isAdShowing) {
                                        reelsCompletedCount = 0
                                        if (InterstitialAdManager.isAdReady()) {
                                            isAdShowing = true
                                            InterstitialAdManager.showInterstitialAd(
                                                activity = activity,
                                                onAdDismissed = {
                                                    isAdShowing = false
                                                },
                                                onFailedToShow = {
                                                    isAdShowing = false
                                                }
                                            )
                                        } else {
                                            // Ad not ready yet; preload with backoff for the next transition, don't block user
                                            InterstitialAdManager.loadInterstitialAd(context)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                // My Reels Tab
                if (myReels.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateCard(
                            title = "No Reels Uploaded Yet",
                            description = "Tap the 'Upload Reel' button to submit your short clip. Once approved by admins, it will appear in the public feed!",
                            icon = Icons.Default.VideoCall,
                            actionButtonText = "Create First Reel",
                            onAction = { viewModel.navigateTo(Screen.CreateReel) }
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(myReels, key = { it.id }) { reel ->
                            MyReelStatusCard(reel = reel)
                        }
                    }
                }
            }
        }
    }

    // Safety Report Modal Dialog
    reportingReel?.let { reel ->
        ReportReelDialog(
            reel = reel,
            onDismiss = { reportingReel = null },
            onSubmitReport = { reason, notes ->
                viewModel.reportReel(reel.id, reason, notes)
                reportingReel = null
            }
        )
    }
}

@Composable
private fun ReelFeedItemCard(
    reel: Reel,
    minWatchSeconds: Int,
    onLikeClick: () -> Unit,
    onReportClick: () -> Unit,
    onWatchRewardClaim: (Long) -> Unit
) {
    var watchSecondsElapsed by remember { mutableStateOf(0) }
    var rewardClaimed by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }

    // Simulated Watch Timer
    LaunchedEffect(isPlaying, rewardClaimed) {
        if (isPlaying && !rewardClaimed) {
            while (watchSecondsElapsed < minWatchSeconds) {
                delay(1000L)
                watchSecondsElapsed++
            }
            if (watchSecondsElapsed >= minWatchSeconds && !rewardClaimed) {
                rewardClaimed = true
                onWatchRewardClaim(watchSecondsElapsed * 1000L)
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Video Player Simulation Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                // Background visual
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.PlayCircleFilled else Icons.Default.PauseCircle,
                        contentDescription = null,
                        tint = BrandSecondary,
                        modifier = Modifier
                            .size(56.dp)
                            .clickable { isPlaying = !isPlaying }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = reel.category,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                // Watch Reward Progress Overlay (Top Right)
                Surface(
                    color = if (rewardClaimed) BrandAccent.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (rewardClaimed) BrandAccent else Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (rewardClaimed) Icons.Default.CheckCircle else Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (rewardClaimed) BrandAccent else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (rewardClaimed) "Rewarded +₹0.20!" else "Watch ${minWatchSeconds - watchSecondsElapsed}s",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (rewardClaimed) BrandAccent else Color.White
                        )
                    }
                }

                // Category Tag (Top Left)
                Surface(
                    color = BrandPrimary.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = reel.language,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Watch Progress Bar
            LinearProgressIndicator(
                progress = { (watchSecondsElapsed.toFloat() / minWatchSeconds.toFloat()).coerceAtMost(1f) },
                modifier = Modifier.fillMaxWidth(),
                color = BrandAccent,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )

            // Content Details & Creator Header
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            color = BrandSecondary.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = reel.userName.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = BrandSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Column {
                            Text(reel.userName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(reel.createdAt)),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Report Button
                    IconButton(onClick = onReportClick) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = "Report Reel",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = reel.caption,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // Bottom Engagement Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable { onLikeClick() }
                        ) {
                            Icon(
                                imageVector = if (reel.isLikedByCurrentUser) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (reel.isLikedByCurrentUser) Color(0xFFFF5252) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${reel.likesCount}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Views",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${reel.viewsCount}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    StatusBadge(text = reel.category, color = BrandSecondary)
                }
            }
        }
    }
}

@Composable
private fun MyReelStatusCard(reel: Reel) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reel.caption,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${reel.category} • ${reel.language} • ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(reel.createdAt))}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            StatusBadge(
                text = reel.status.label,
                color = when (reel.status) {
                    ReelStatus.APPROVED -> BrandAccent
                    ReelStatus.PENDING -> BrandWarning
                    ReelStatus.REJECTED -> Color(0xFFFF5252)
                    ReelStatus.REMOVED -> Color.Gray
                }
            )
        }

        if (reel.status == ReelStatus.REJECTED && !reel.rejectionReason.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = Color(0xFFFF5252).copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                    Text(
                        text = "Rejection Reason: ${reel.rejectionReason}",
                        fontSize = 11.sp,
                        color = Color(0xFFFF5252)
                    )
                }
            }
        }
    }
}

@Composable
fun CreateReelScreen(viewModel: EarnMateViewModel) {
    var caption by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ReelCategory.TECH.title) }
    var selectedLanguage by remember { mutableStateOf(ReelLanguage.HINDI.label) }
    var selectedVideoUrl by remember { mutableStateOf("") }

    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

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
            IconButton(onClick = { viewModel.navigateTo(Screen.Reels) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Creator Upload 🎥", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("1. Select Video Clip", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable {
                        selectedVideoUrl = "https://assets.mixkit.co/videos/preview/mixkit-tree-with-yellow-flowers-1173-large.mp4"
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (selectedVideoUrl.isBlank()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Tap to select video from storage (Max 60s)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandAccent, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Video Attached (30s MP4)", fontSize = 12.sp, color = BrandAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("2. Reel Information", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Caption / Title") },
                placeholder = { Text("e.g. Best side income apps 2026!") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Category", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReelCategory.values().forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat.title,
                        onClick = { selectedCategory = cat.title },
                        label = { Text(cat.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Language", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReelLanguage.values().forEach { lang ->
                    FilterChip(
                        selected = selectedLanguage == lang.label,
                        onClick = { selectedLanguage = lang.label },
                        label = { Text(lang.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isUploading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { uploadProgress },
                        modifier = Modifier.fillMaxWidth(),
                        color = BrandPrimary
                    )
                    Text("Uploading to Firebase Storage... ${(uploadProgress * 100).toInt()}%", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Button(
                    onClick = {
                        if (caption.isBlank()) {
                            viewModel.showSnackbar("Please enter a reel caption.")
                            return@Button
                        }
                        isUploading = true
                        scope.launch {
                            // Resumable upload progress simulation
                            while (uploadProgress < 1f) {
                                delay(300L)
                                uploadProgress += 0.25f
                            }
                            viewModel.uploadReel(
                                caption = caption,
                                category = selectedCategory,
                                language = selectedLanguage,
                                durationSeconds = 30,
                                videoUrl = selectedVideoUrl
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_reel_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SUBMIT FOR ADMIN APPROVAL", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminReelsScreen(viewModel: EarnMateViewModel) {
    AdminGuard(viewModel = viewModel) {
        val reels by viewModel.reels.collectAsState()
        val reports by viewModel.reelReports.collectAsState()

        var activeTab by remember { mutableStateOf(0) } // 0 = Pending Approval, 1 = Reported Reels
        var rejectingReel by remember { mutableStateOf<Reel?>(null) }
        var rejectionReasonInput by remember { mutableStateOf("") }

        val pendingReels = remember(reels) { reels.filter { it.status == ReelStatus.PENDING } }
        val unresolvedReports = remember(reports) { reports.filter { !it.isResolved } }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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
            Text("Reel Moderation Queue 🎬", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = BrandPrimary
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Pending Review (${pendingReels.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Reported Reels (${unresolvedReports.size})", fontWeight = FontWeight.Bold) }
            )
        }

        if (activeTab == 0) {
            if (pendingReels.isEmpty()) {
                EmptyStateCard(
                    title = "Pending Queue Empty",
                    description = "All creator uploaded reels have been reviewed and moderated!",
                    icon = Icons.Default.CheckCircle
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Oldest First Review", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = {
                        pendingReels.forEach { viewModel.approveReel(it.id) }
                    }) {
                        Text("Approve All", color = BrandAccent, fontWeight = FontWeight.Bold)
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pendingReels, key = { it.id }) { reel ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(reel.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    StatusBadge(reel.category, BrandSecondary)
                                }

                                Text(reel.caption, fontSize = 13.sp)

                                Surface(
                                    color = Color.Black,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { rejectingReel = reel },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252))
                                    ) {
                                        Text("Reject")
                                    }

                                    Button(
                                        onClick = { viewModel.approveReel(reel.id) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandAccent)
                                    ) {
                                        Text("APPROVE", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Reported Reels Tab
            if (unresolvedReports.isEmpty()) {
                EmptyStateCard(
                    title = "No Active Safety Reports",
                    description = "There are no flagged or reported reels requiring action.",
                    icon = Icons.Default.VerifiedUser
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(unresolvedReports, key = { it.id }) { report ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Reported Reel: ${report.reelCaption}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    StatusBadge(report.reason.label, Color(0xFFFF5252))
                                }

                                Text("Notes: ${report.notes.ifBlank { "No additional details provided." }}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Uploader: ${report.uploaderName} • Reporter ID: ${report.reporterUserId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.resolveReelReport(report.id, "Dismissed") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Dismiss Report")
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.removeReel(report.reelId)
                                            viewModel.resolveReelReport(report.id, "Removed")
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                                    ) {
                                        Text("REMOVE REEL", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Rejection Modal
    rejectingReel?.let { reel ->
        Dialog(onDismissRequest = { rejectingReel = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Reject Reel Moderation", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Specify reason for rejecting \"${reel.caption}\":", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedTextField(
                        value = rejectionReasonInput,
                        onValueChange = { rejectionReasonInput = it },
                        label = { Text("Rejection Reason") },
                        placeholder = { Text("e.g. Low video quality / Violation of TOS") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(onClick = { rejectingReel = null }, modifier = Modifier.weight(1f)) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                viewModel.rejectReel(reel.id, rejectionReasonInput)
                                rejectingReel = null
                                rejectionReasonInput = ""
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                        ) {
                            Text("CONFIRM REJECT")
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun ReportReelDialog(
    reel: Reel,
    onDismiss: () -> Unit,
    onSubmitReport: (ReelReportReason, String) -> Unit
) {
    var selectedReason by remember { mutableStateOf(ReelReportReason.SPAM) }
    var notesInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Report Reel Content", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFFF5252))
                Text("Help keep EarnMate safe. Select a reason for reporting \"${reel.caption}\":", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                ReelReportReason.values().forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(reason.label, fontSize = 13.sp)
                    }
                }

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Additional Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onSubmitReport(selectedReason, notesInput) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                    ) {
                        Text("SUBMIT REPORT")
                    }
                }
            }
        }
    }
}
