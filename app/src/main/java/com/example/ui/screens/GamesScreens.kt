package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ad.AdManager
import com.example.data.ad.InterstitialAdManager
import com.example.data.ad.findActivity
import com.example.data.model.*
import com.example.ui.EarnMateViewModel
import com.example.ui.Screen
import com.example.ui.components.AntiFraudNoticeCard
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesHubScreen(viewModel: EarnMateViewModel) {
    val context = LocalContext.current
    val configs by viewModel.gameConfigs.collectAsState()
    val plays by viewModel.gamePlays.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    val totalGamesEarned = remember(plays) {
        plays.sumOf { it.rewardAmount }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Games & Rewards Hub 🎮", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.dp, ModuleColors.GamesAccent.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.horizontalGradient(ModuleColors.GamesGradient))
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TOTAL GAME REWARDS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.85f),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${"%.2f".format(totalGamesEarned)}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Free play rewards • 0 Deposits required",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape,
                                modifier = Modifier.size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SportsEsports,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                AntiFraudNoticeCard()
            }

            item {
                Text(
                    text = "Featured Reward Games",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(configs) { config ->
                val playsToday = viewModel.getTodayGamePlayCount(config.gameType)
                GameCardItem(
                    config = config,
                    playsToday = playsToday,
                    onPlayClick = {
                        val launchGame = {
                            when (config.gameType) {
                                GameType.SPIN_WHEEL -> viewModel.navigateTo(Screen.SpinWheel)
                                GameType.SCRATCH_CARD -> viewModel.navigateTo(Screen.ScratchCard)
                                GameType.DAILY_QUIZ -> viewModel.navigateTo(Screen.DailyQuiz)
                                GameType.MEMORY_MATCH -> viewModel.navigateTo(Screen.MemoryMatch)
                                GameType.LUCKY_DRAW -> viewModel.navigateTo(Screen.LuckyDraw)
                            }
                        }

                        val activity = context.findActivity()
                        val currentUser = viewModel.currentUser.value
                        val showAds = AdManager.shouldShowAds(currentUser)

                        if (activity != null && showAds) {
                            if (InterstitialAdManager.isAdReady()) {
                                InterstitialAdManager.showInterstitialAd(
                                    activity = activity,
                                    onAdDismissed = {
                                        launchGame()
                                    },
                                    onFailedToShow = { errMsg ->
                                        android.util.Log.d("AdMobInterstitial", "Interstitial ad unavailable ($errMsg), launching game directly.")
                                    }
                                )
                            } else {
                                // Ad not ready yet; preload for next time and start game directly
                                InterstitialAdManager.loadInterstitialAd(context)
                                launchGame()
                            }
                        } else {
                            launchGame()
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recent Game Rewards Ledger",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (plays.isEmpty()) {
                item {
                    Text(
                        text = "No games played yet. Try Spin & Win or Scratch & Earn above!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(plays.take(5)) { play ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
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
                                    color = ModuleColors.GamesAccent.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = when (play.gameType) {
                                                GameType.SPIN_WHEEL -> Icons.Default.Casino
                                                GameType.SCRATCH_CARD -> Icons.Default.Gesture
                                                GameType.DAILY_QUIZ -> Icons.Default.Quiz
                                                GameType.MEMORY_MATCH -> Icons.Default.Extension
                                                GameType.LUCKY_DRAW -> Icons.Default.ConfirmationNumber
                                            },
                                            contentDescription = null,
                                            tint = ModuleColors.GamesAccent
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = play.gameType.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = play.details,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = "+₹${"%.1f".format(play.rewardAmount)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (play.rewardAmount > 0) ModuleColors.WalletAccent else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun GameCardItem(
    config: GameConfigItem,
    playsToday: Int,
    onPlayClick: () -> Unit
) {
    val isLimitReached = playsToday >= config.maxDailyPlays
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = ModuleColors.GamesAccent.copy(alpha = 0.3f),
        onClick = if (config.isEnabled) onPlayClick else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = ModuleColors.GamesAccent.copy(alpha = 0.15f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (config.gameType) {
                            GameType.SPIN_WHEEL -> Icons.Default.Casino
                            GameType.SCRATCH_CARD -> Icons.Default.Gesture
                            GameType.DAILY_QUIZ -> Icons.Default.Quiz
                            GameType.MEMORY_MATCH -> Icons.Default.Extension
                            GameType.LUCKY_DRAW -> Icons.Default.ConfirmationNumber
                        },
                        contentDescription = config.gameType.title,
                        tint = ModuleColors.GamesAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = config.gameType.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = if (isLimitReached) Color.Gray.copy(alpha = 0.2f) else ModuleColors.GamesAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "$playsToday/${config.maxDailyPlays} Today",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLimitReached) Color.Gray else ModuleColors.GamesAccent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = config.subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Win ₹${config.minRewardRupees} - ₹${config.maxRewardRupees} per attempt",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ModuleColors.WalletAccent
                )
            }

            Button(
                onClick = onPlayClick,
                enabled = config.isEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ModuleColors.GamesAccent,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isLimitReached) "Done" else "Play",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// SPIN & WIN WHEEL SCREEN
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinWheelScreen(viewModel: EarnMateViewModel) {
    val scope = rememberCoroutineScope()
    var isSpinning by remember { mutableStateOf(false) }
    var rewardWon by remember { mutableStateOf<Double?>(null) }

    val spinAnimation = remember { Animatable(0f) }

    val rewards = listOf("₹0.5", "₹1.0", "₹1.5", "₹2.0", "₹3.0", "₹5.0", "Try Again", "₹10.0")
    val rewardValues = listOf(0.5, 1.0, 1.5, 2.0, 3.0, 5.0, 0.0, 10.0)
    val colors = listOf(
        ModuleColors.GamesAccent, Color(0xFF00C2FF), ModuleColors.WalletAccent, Color(0xFFFF9F43),
        Color(0xFFA55EEA), Color(0xFF20BF6B), Color(0xFFFD9644), Color(0xFF4B7BEC)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spin & Win", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.GamesHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Surface(
                color = ModuleColors.GamesAccent.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Casino, contentDescription = null, tint = ModuleColors.GamesAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Spin the wheel for verified daily rewards! 100% Free",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(320.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(300.dp)
                        .rotate(spinAnimation.value)
                ) {
                    val sweepAngle = 360f / rewards.size
                    val radius = size.minDimension / 2f

                    rewards.forEachIndexed { index, _ ->
                        val startAngle = index * sweepAngle - 90f
                        drawArc(
                            color = colors[index % colors.size],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true
                        )
                        drawArc(
                            color = Color.White.copy(alpha = 0.3f),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    drawCircle(
                        color = Color.White,
                        radius = radius,
                        style = Stroke(width = 6.dp.toPx())
                    )

                    // Draw slice reward labels
                    drawIntoCanvas { canvas ->
                        val nativeCanvas = canvas.nativeCanvas
                        val textPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 13.sp.toPx()
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                            setShadowLayer(4f, 0f, 2f, android.graphics.Color.DKGRAY)
                        }

                        rewards.forEachIndexed { index, label ->
                            val midAngle = index * sweepAngle - 90f + (sweepAngle / 2f)
                            val rad = Math.toRadians(midAngle.toDouble())
                            val textRadius = radius * 0.65f
                            val x = (center.x + textRadius * kotlin.math.cos(rad)).toFloat()
                            val y = (center.y + textRadius * kotlin.math.sin(rad)).toFloat()

                            nativeCanvas.save()
                            nativeCanvas.rotate(midAngle + 90f, x, y)
                            nativeCanvas.drawText(label, x, y + 4.dp.toPx(), textPaint)
                            nativeCanvas.restore()
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Pointer",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-12).dp)
                )

                Surface(
                    color = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, CircleShape),
                    border = BorderStroke(3.dp, ModuleColors.GamesAccent)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = ModuleColors.GamesAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            rewardWon?.let { amount ->
                GlassCard(
                    backgroundColor = ModuleColors.WalletAccent.copy(alpha = 0.15f),
                    borderColor = ModuleColors.WalletAccent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (amount > 0) "🎉 REWARD WON!" else "Better Luck Next Time!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (amount > 0) ModuleColors.WalletAccent else MaterialTheme.colorScheme.onSurface
                        )
                        if (amount > 0) {
                            Text(
                                text = "₹${"%.1f".format(amount)} Credited to Wallet",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ModuleColors.WalletAccent
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (isSpinning) return@Button
                    isSpinning = true
                    rewardWon = null

                    viewModel.playSpinWheel { reward ->
                        scope.launch {
                            val sweepAngle = 360f / rewards.size
                            val winningIndex = rewardValues.indexOfFirst { kotlin.math.abs(it - reward) < 0.01 }
                                .takeIf { it >= 0 } ?: (0 until rewards.size).random()

                            // Pointer is at Top (-90 deg). Slice starts at index*sweepAngle - 90.
                            // To land slice center at top pointer:
                            val targetSliceAngle = 360f - (winningIndex * sweepAngle + sweepAngle / 2f)
                            val currentAngle = spinAnimation.value
                            val baseTurns = 5 * 360f
                            val targetRotation = (currentAngle - (currentAngle % 360f)) + baseTurns + targetSliceAngle

                            spinAnimation.animateTo(
                                targetValue = targetRotation,
                                animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing)
                            )
                            isSpinning = false
                            rewardWon = reward
                        }
                    }
                },
                enabled = !isSpinning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("spin_now_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.GamesAccent),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isSpinning) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Spinning Wheel...")
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SPIN WHEEL NOW", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// SCRATCH & EARN SCREEN
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScratchCardScreen(viewModel: EarnMateViewModel) {
    var isScratched by remember { mutableStateOf(false) }
    var rewardAmount by remember { mutableStateOf<Double?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scratch & Earn", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.GamesHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Tap or Scratch the card surface to reveal your guaranteed reward!",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable {
                        if (!isScratched && !isLoading) {
                            isLoading = true
                            viewModel.playScratchCard { reward ->
                                isLoading = false
                                isScratched = true
                                rewardAmount = reward
                            }
                        }
                    },
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isScratched) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "YOU WON!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ModuleColors.GamesAccent
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "₹${"%.1f".format(rewardAmount ?: 0.0)}",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ModuleColors.WalletAccent
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Reward added directly to available wallet balance",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(ModuleColors.GamesGradient)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Gesture,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isLoading) "Revealing..." else "SCRATCH HERE",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            if (isScratched) {
                Button(
                    onClick = {
                        isScratched = false
                        rewardAmount = null
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.GamesAccent),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Scratch Another Card", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// DAILY QUIZ SCREEN
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyQuizScreen(viewModel: EarnMateViewModel) {
    val questions = remember { viewModel.sampleQuizQuestions }
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var correctCount by remember { mutableStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(false) }
    var quizResult by remember { mutableStateOf<QuizResult?>(null) }
    var secondsElapsed by remember { mutableStateOf(0) }

    LaunchedEffect(isQuizFinished) {
        if (!isQuizFinished) {
            while (true) {
                delay(1000L)
                secondsElapsed++
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Trivia Quiz", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.GamesHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isQuizFinished && quizResult != null) {
                val res = quizResult!!
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = ModuleColors.WalletAccent,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Quiz Completed!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Score: ${res.correctAnswers} / ${res.totalQuestions} (${res.scorePercentage}%)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ModuleColors.GamesAccent
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Reward Earned: ₹${"%.1f".format(res.rewardEarned)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ModuleColors.WalletAccent
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.navigateTo(Screen.GamesHub) },
                            colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.GamesAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Return to Games Hub", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (currentIndex < questions.size) {
                val currentQ = questions[currentIndex]

                LinearProgressIndicator(
                    progress = { (currentIndex + 1) / questions.size.toFloat() },
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                    color = ModuleColors.GamesAccent
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Question ${currentIndex + 1} of ${questions.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Time: ${secondsElapsed}s",
                        fontSize = 13.sp,
                        color = ModuleColors.GamesAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = currentQ.question,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }

                currentQ.options.forEachIndexed { optIdx, optionText ->
                    val isSelected = selectedOptionIndex == optIdx
                    val isCorrect = optIdx == currentQ.correctIndex
                    val cardBg = when {
                        selectedOptionIndex == null -> MaterialTheme.colorScheme.surface
                        isSelected && isCorrect -> Color(0xFF22C55E).copy(alpha = 0.2f)
                        isSelected && !isCorrect -> Color(0xFFFF5252).copy(alpha = 0.2f)
                        isCorrect -> Color(0xFF22C55E).copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    GlassCard(
                        backgroundColor = cardBg,
                        borderColor = if (isSelected) ModuleColors.GamesAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        onClick = {
                            if (selectedOptionIndex == null) {
                                selectedOptionIndex = optIdx
                                if (isCorrect) correctCount++
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = ModuleColors.GamesAccent.copy(alpha = 0.12f),
                                shape = CircleShape,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = ('A' + optIdx).toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = ModuleColors.GamesAccent
                                    )
                                }
                            }
                            Text(
                                text = optionText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (selectedOptionIndex != null) {
                    GlassCard(
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Explanation: ${currentQ.explanation}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            if (currentIndex + 1 < questions.size) {
                                currentIndex++
                                selectedOptionIndex = null
                            } else {
                                isQuizFinished = true
                                viewModel.submitQuizResult(correctCount, questions.size, secondsElapsed) { res ->
                                    quizResult = res
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.GamesAccent),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (currentIndex + 1 < questions.size) "Next Question" else "Finish & Claim Reward",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// MEMORY MATCH SCREEN
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryMatchScreen(viewModel: EarnMateViewModel) {
    val scope = rememberCoroutineScope()
    val icons = listOf(
        Icons.Default.CurrencyRupee, Icons.Default.Star, Icons.Default.EmojiEvents,
        Icons.Default.CardGiftcard, Icons.Default.LocalOffer, Icons.Default.FlashOn
    )

    var cards by remember {
        mutableStateOf(
            (icons + icons).shuffled().mapIndexed { idx, icon ->
                MemoryCard(id = idx, symbol = icon.name, pairId = icons.indexOf(icon), isFlipped = false, isMatched = false)
            }
        )
    }

    var flippedIndices by remember { mutableStateOf<List<Int>>(emptyList()) }
    var movesCount by remember { mutableStateOf(0) }
    var secondsElapsed by remember { mutableStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (true) {
                delay(1000L)
                secondsElapsed++
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Memory Flip", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.GamesHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Moves: $movesCount", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Time: ${secondsElapsed}s", fontWeight = FontWeight.Bold, color = ModuleColors.GamesAccent)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cards.size) { index ->
                    val card = cards[index]
                    val isRevealed = card.isFlipped || card.isMatched

                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                if (flippedIndices.size < 2 && !card.isFlipped && !card.isMatched) {
                                    val updated = cards.toMutableList()
                                    updated[index] = card.copy(isFlipped = true)
                                    cards = updated
                                    flippedIndices = flippedIndices + index

                                    if (flippedIndices.size == 2) {
                                        movesCount++
                                        val idx1 = flippedIndices[0]
                                        val idx2 = flippedIndices[1]
                                        if (cards[idx1].pairId == cards[idx2].pairId) {
                                            // Match!
                                            val matchedList = cards.toMutableList()
                                            matchedList[idx1] = matchedList[idx1].copy(isMatched = true)
                                            matchedList[idx2] = matchedList[idx2].copy(isMatched = true)
                                            cards = matchedList
                                            flippedIndices = emptyList()

                                            if (cards.all { it.isMatched }) {
                                                isGameOver = true
                                                viewModel.playMemoryMatch(secondsElapsed, movesCount) { _ -> }
                                            }
                                        } else {
                                            // Reset after delay
                                            scope.launch {
                                                delay(800L)
                                                val resetList = cards.toMutableList()
                                                resetList[idx1] = resetList[idx1].copy(isFlipped = false)
                                                resetList[idx2] = resetList[idx2].copy(isFlipped = false)
                                                cards = resetList
                                                flippedIndices = emptyList()
                                            }
                                        }
                                    }
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isRevealed) ModuleColors.GamesAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isRevealed) {
                                Icon(
                                    imageVector = icons[card.pairId],
                                    contentDescription = null,
                                    tint = ModuleColors.GamesAccent,
                                    modifier = Modifier.size(36.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Extension,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (isGameOver) {
                GlassCard(
                    backgroundColor = ModuleColors.WalletAccent.copy(alpha = 0.15f),
                    borderColor = ModuleColors.WalletAccent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🎮 Game Solved in $movesCount moves!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ModuleColors.WalletAccent)
                        Text("+₹1.5 Reward Credited to Wallet!", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ModuleColors.WalletAccent)
                    }
                }
            }
        }
    }
}

// ==========================================
// LUCKY DRAW SCREEN
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuckyDrawScreen(viewModel: EarnMateViewModel) {
    var ticketNumber by remember { mutableStateOf<String?>(null) }
    var isParticipating by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Lucky Jackpot Draw", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.GamesHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(ModuleColors.GamesGradient))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "BUMPER JACKPOT",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "₹500.00",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Winner announced daily at 9:00 PM IST",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            if (ticketNumber != null) {
                GlassCard(
                    backgroundColor = ModuleColors.WalletAccent.copy(alpha = 0.15f),
                    borderColor = ModuleColors.WalletAccent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("🎟️ Your Free Entry Ticket", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(ticketNumber!!, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ModuleColors.WalletAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Good Luck! Check back at 9 PM for live winner results.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            } else {
                Button(
                    onClick = {
                        isParticipating = true
                        viewModel.claimLuckyDrawTicket("DAILY_POOL_01") { ticket ->
                            isParticipating = false
                            ticketNumber = "#${ticket.ticketNumber}"
                        }
                    },
                    enabled = !isParticipating,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.GamesAccent),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isParticipating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CLAIM FREE LUCKY TICKET", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
