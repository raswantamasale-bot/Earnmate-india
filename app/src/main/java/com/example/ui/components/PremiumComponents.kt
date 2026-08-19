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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.ad.AdManager
import com.example.ui.EarnMateViewModel
import com.example.ui.Screen
import com.example.ui.theme.ModuleColors

val GoldGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFD700),
        Color(0xFFFFB300),
        Color(0xFFFFA000)
    )
)

val GoldDarkGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF2C2200),
        Color(0xFF1A1400)
    )
)

@Composable
fun PremiumGoldBadge(
    modifier: Modifier = Modifier,
    text: String = "PREMIUM"
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFD700).copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = text,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
        }
    }
}

/**
 * Responsive Ad Banner Card.
 * Displays sponsored ad content for FREE users.
 * Automatically hidden for PREMIUM users via AdManager.shouldShowAds.
 */
@Composable
fun AdBannerCard(
    viewModel: EarnMateViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val showAd = remember(user) { AdManager.shouldShowAds(user) }

    AnimatedVisibility(visible = showAd) {
        GlassCard(
            modifier = modifier
                .fillMaxWidth()
                .testTag("ad_banner_card"),
            borderColor = Color.Gray.copy(alpha = 0.3f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Official Google AdMob Banner Ad View
                var adLoaded by remember { mutableStateOf(false) }
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
                                    android.util.Log.d("AdMobBanner", "Banner Ad loaded successfully. Unit ID: ${AdManager.config.ADMOB_BANNER_ID}")
                                    adLoaded = true
                                }
                                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                                    android.util.Log.e("AdMobBanner", "Banner Ad failed to load: Code=${error.code}, Message='${error.message}', Domain='${error.domain}'")
                                    adLoaded = false
                                }
                                override fun onAdOpened() {
                                    android.util.Log.d("AdMobBanner", "Banner Ad opened")
                                }
                                override fun onAdClicked() {
                                    android.util.Log.d("AdMobBanner", "Banner Ad clicked")
                                }
                            }
                            try {
                                android.util.Log.d("AdMobBanner", "Requesting Banner Ad with ID: ${AdManager.config.ADMOB_BANNER_ID}")
                                loadAd(com.google.android.gms.ads.AdRequest.Builder().build())
                            } catch (e: Exception) {
                                android.util.Log.e("AdMobBanner", "Exception calling loadAd: ${e.message}", e)
                            }
                        }
                    }
                )

                // Additional Info & Remove Ads Option
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "AD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Sponsored Ad • AdMob Network",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { viewModel.navigateTo(Screen.Premium) }) {
                        Text(
                            text = "Remove Ads 👑",
                            fontSize = 11.sp,
                            color = Color(0xFFFFB300),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable Premium Upgrade Dialog shown when FREE users tap a gated feature.
 */
@Composable
fun PremiumUpgradeDialog(
    featureTitle: String,
    featureDescription: String,
    onDismiss: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                shape = CircleShape,
                color = Color(0xFFFFD700).copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "Premium Feature 👑",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = featureTitle,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
                Text(
                    text = featureDescription,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFD700).copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✨ Upgrade to EarnMate Premium to unlock this feature and all exclusive perks!",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFFB300),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onUpgradeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFB300),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("upgrade_now_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Upgrade to Premium 👑", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Maybe Later")
            }
        }
    )
}
