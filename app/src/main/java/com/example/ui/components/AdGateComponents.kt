package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ad.AdServiceFactory
import com.example.data.ad.RewardedAdManager
import com.example.data.ad.findActivity
import com.example.data.model.AdPlacementType
import com.example.data.model.AdResultStatus
import com.example.ui.EarnMateViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class AdGateState {
    PROMPT,
    LOADING,
    SUCCESS,
    RETRY
}

@Composable
fun AdGateDialog(
    viewModel: EarnMateViewModel,
    targetType: String,
    targetTitle: String,
    targetId: String,
    onDismiss: () -> Unit,
    onUnlocked: () -> Unit
) {
    val context = LocalContext.current
    var currentState by remember { mutableStateOf(AdGateState.PROMPT) }
    var failureReason by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = {
            if (currentState != AdGateState.LOADING) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = currentState != AdGateState.LOADING,
            dismissOnClickOutside = currentState != AdGateState.LOADING
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            when (currentState) {
                AdGateState.PROMPT -> {
                    AdGatePromptCard(
                        targetType = targetType,
                        targetTitle = targetTitle,
                        onWatchAdClick = {
                            val activity = context.findActivity()
                            if (activity != null) {
                                currentState = AdGateState.LOADING
                                if (!RewardedAdManager.isAdReady()) {
                                    RewardedAdManager.loadRewardedAd(context, onLoaded = {
                                        RewardedAdManager.showRewardedAd(
                                            activity = activity,
                                            onRewardEarned = { rewardItem ->
                                                viewModel.logAdGateEvent(
                                                    AdPlacementType.REWARDED,
                                                    targetType,
                                                    targetId,
                                                    targetTitle,
                                                    AdResultStatus.COMPLETED
                                                )
                                                currentState = AdGateState.SUCCESS
                                                scope.launch {
                                                    delay(1000L)
                                                    onUnlocked()
                                                    onDismiss()
                                                }
                                            },
                                            onFailedToShow = { errMsg ->
                                                failureReason = errMsg
                                                currentState = AdGateState.RETRY
                                            }
                                        )
                                    }, onFailed = { errMsg ->
                                        failureReason = errMsg
                                        currentState = AdGateState.RETRY
                                    })
                                } else {
                                    RewardedAdManager.showRewardedAd(
                                        activity = activity,
                                        onRewardEarned = { rewardItem ->
                                            viewModel.logAdGateEvent(
                                                AdPlacementType.REWARDED,
                                                targetType,
                                                targetId,
                                                targetTitle,
                                                AdResultStatus.COMPLETED
                                            )
                                            currentState = AdGateState.SUCCESS
                                            scope.launch {
                                                delay(1000L)
                                                onUnlocked()
                                                onDismiss()
                                            }
                                        },
                                        onFailedToShow = { errMsg ->
                                            failureReason = errMsg
                                            currentState = AdGateState.RETRY
                                        }
                                    )
                                }
                            } else {
                                onUnlocked()
                                onDismiss()
                            }
                        },
                        onCancelClick = onDismiss
                    )
                }

                AdGateState.LOADING -> {
                    AdGateLoadingCard(targetTitle = targetTitle)
                }

                AdGateState.SUCCESS -> {
                    AdGateSuccessCard(targetTitle = targetTitle)
                }

                AdGateState.RETRY -> {
                    AdGateRetryCard(
                        reason = failureReason,
                        onRetryClick = {
                            val activity = context.findActivity()
                            if (activity != null) {
                                currentState = AdGateState.LOADING
                                RewardedAdManager.loadRewardedAd(context, onLoaded = {
                                    RewardedAdManager.showRewardedAd(
                                        activity = activity,
                                        onRewardEarned = {
                                            currentState = AdGateState.SUCCESS
                                            scope.launch {
                                                delay(800L)
                                                onUnlocked()
                                                onDismiss()
                                            }
                                        },
                                        onFailedToShow = {
                                            failureReason = it
                                            currentState = AdGateState.RETRY
                                        }
                                    )
                                }, onFailed = {
                                    failureReason = it
                                    currentState = AdGateState.RETRY
                                })
                            } else {
                                onUnlocked()
                                onDismiss()
                            }
                        },
                        onCancelClick = onDismiss
                    )
                }
            }
        }
    }
}

@Composable
private fun AdGatePromptCard(
    targetType: String,
    targetTitle: String,
    onWatchAdClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151C32)),
        border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                color = BrandPrimary.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Text(
                text = "Ad Gate Unlock",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Watch a short ad to unlock access to:\n\"$targetTitle\" ($targetType)",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )

            Surface(
                color = BrandAccent.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BrandAccent.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = BrandAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "100% Free • Rewards credited after completion",
                        fontSize = 11.sp,
                        color = BrandAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onWatchAdClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("watch_ad_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("WATCH AD TO UNLOCK", fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun AdGateLoadingCard(targetTitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151C32))
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = BrandSecondary, modifier = Modifier.size(48.dp))
            Text(
                text = "Loading Test Ad...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Preparing stream for $targetTitle",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun AdGateSuccessCard(targetTitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151C32)),
        border = BorderStroke(1.dp, BrandAccent)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = BrandAccent.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BrandAccent,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Text(
                text = "Ad Completed!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrandAccent
            )

            Text(
                text = "Unlocking \"$targetTitle\"...",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun AdGateRetryCard(
    reason: String,
    onRetryClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151C32)),
        border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                color = Color(0xFFFF5252).copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Text(
                text = "Ad Incomplete",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Ad wasn't completed. Please watch the full ad to continue to your reward item.\n($reason)",
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", color = Color.White)
                }

                Button(
                    onClick = onRetryClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("TRY AGAIN", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
