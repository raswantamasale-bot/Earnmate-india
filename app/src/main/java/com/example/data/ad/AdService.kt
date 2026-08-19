package com.example.data.ad

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.annotation.Keep
import com.example.data.model.AdPlacementType
import com.example.data.model.AdResult
import com.example.data.model.AdResultStatus
import com.example.data.model.UserProfile
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.delay

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * Official Google AdMob Configuration & Placeholders
 */
@Keep
data class AdMobConfig(
    val ADMOB_APP_ID: String = "ca-app-pub-3940256099942544~3347511713",
    val ADMOB_BANNER_ID: String = "ca-app-pub-3940256099942544/9214589741",
    val ADMOB_INTERSTITIAL_ID: String = "ca-app-pub-9518057750714829/1122197284",
    val ADMOB_REWARDED_ID: String = "ca-app-pub-3940256099942544/5224354917"
)

/**
 * Centralized AdManager controlling ad loads and Premium ad-bypass logic.
 */
object AdManager {
    val config = AdMobConfig()

    /**
     * Determines whether advertisements should be displayed or loaded.
     * PREMIUM users automatically bypass normal app advertisements.
     */
    fun shouldShowAds(user: UserProfile?): Boolean {
        if (user == null) return true
        return !user.isPremiumActive
    }
}

/**
 * Real Google AdMob Rewarded Ad Manager
 * Handles loading, displaying, callback safety, and duplicate reward prevention.
 */
object RewardedAdManager {
    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    fun isAdReady(): Boolean = rewardedAd != null

    fun loadRewardedAd(context: Context, onLoaded: (() -> Unit)? = null, onFailed: ((String) -> Unit)? = null) {
        if (rewardedAd != null) {
            Log.d("AdMobRewarded", "Rewarded ad is already loaded and ready.")
            onLoaded?.invoke()
            return
        }
        if (isLoading) {
            Log.d("AdMobRewarded", "Rewarded ad load request already in progress...")
            return
        }

        isLoading = true
        val adUnitId = AdManager.config.ADMOB_REWARDED_ID
        Log.d("AdMobRewarded", "Loading Rewarded TEST Ad with Unit ID: $adUnitId")

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context.applicationContext,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("AdMobRewarded", "Rewarded ad loaded successfully from Google Mobile Ads SDK.")
                    rewardedAd = ad
                    isLoading = false
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    val errMsg = "Failed to load rewarded ad (Code: ${loadAdError.code}): ${loadAdError.message}"
                    Log.w("AdMobRewarded", errMsg)
                    rewardedAd = null
                    isLoading = false
                    onFailed?.invoke(errMsg)
                }
            }
        )
    }

    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: (rewardItem: RewardItem) -> Unit,
        onAdDismissed: () -> Unit = {},
        onFailedToShow: (String) -> Unit = {}
    ) {
        val currentAd = rewardedAd
        if (currentAd == null) {
            val errMsg = "Rewarded ad is not ready yet. Preloading ad, please try again in a moment."
            Log.e("AdMobRewarded", errMsg)
            loadRewardedAd(activity)
            onFailedToShow(errMsg)
            return
        }

        var rewardGranted = false

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d("AdMobRewarded", "Rewarded ad clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d("AdMobRewarded", "Rewarded ad dismissed. Reward granted: $rewardGranted")
                rewardedAd = null
                // Preload next ad automatically for future voluntary watch
                loadRewardedAd(activity)
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                val errMsg = "Rewarded ad failed to display (Code: ${adError.code}): ${adError.message}"
                Log.e("AdMobRewarded", errMsg)
                rewardedAd = null
                loadRewardedAd(activity)
                onFailedToShow(errMsg)
            }

            override fun onAdImpression() {
                Log.d("AdMobRewarded", "Rewarded ad impression logged.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("AdMobRewarded", "Rewarded ad displayed full screen.")
            }
        }

        currentAd.show(activity, OnUserEarnedRewardListener { rewardItem ->
            Log.d("AdMobRewarded", "Google Mobile Ads reward callback triggered: amount=${rewardItem.amount}, type=${rewardItem.type}")
            if (!rewardGranted) {
                rewardGranted = true
                onRewardEarned(rewardItem)
            }
        })
    }
}

/**
 * Real Google AdMob Interstitial Ad Manager for Game Module
 * Handles loading, preloading, displaying full-screen interstitial ads, and callback management.
 */
object InterstitialAdManager {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"

    fun isAdReady(): Boolean = interstitialAd != null

    fun loadInterstitialAd(context: Context, onLoaded: (() -> Unit)? = null, onFailed: ((String) -> Unit)? = null) {
        if (interstitialAd != null) {
            Log.d("AdMobInterstitial", "Interstitial ad is already loaded and ready.")
            onLoaded?.invoke()
            return
        }
        if (isLoading) {
            Log.d("AdMobInterstitial", "Interstitial ad load request already in progress...")
            return
        }

        isLoading = true
        val adUnitId = AdManager.config.ADMOB_INTERSTITIAL_ID
        Log.d("AdMobInterstitial", "Loading Interstitial Ad with Unit ID: $adUnitId")

        loadInternal(context, adUnitId, isFallback = false, onLoaded, onFailed)
    }

    private fun loadInternal(
        context: Context,
        adUnitId: String,
        isFallback: Boolean,
        onLoaded: (() -> Unit)?,
        onFailed: ((String) -> Unit)?
    ) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context.applicationContext,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("AdMobInterstitial", "Interstitial ad loaded successfully (${if (isFallback) "TEST Fallback" else "Primary Unit ID"}).")
                    interstitialAd = ad
                    isLoading = false
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    val errMsg = "Failed to load interstitial ad (Code: ${loadAdError.code}, Unit: $adUnitId): ${loadAdError.message}"
                    Log.w("AdMobInterstitial", errMsg)

                    // If primary live unit fails to load/fill during development/preview or code 3 (no fill/account review), gracefully fall back to official TEST Ad unit
                    if (!isFallback) {
                        Log.i("AdMobInterstitial", "Primary Interstitial Ad unit $adUnitId failed to serve ad (Code: ${loadAdError.code}). Falling back to Google test ad unit...")
                        loadInternal(context, TEST_INTERSTITIAL_ID, isFallback = true, onLoaded, onFailed)
                    } else {
                        interstitialAd = null
                        isLoading = false
                        onFailed?.invoke(errMsg)
                    }
                }
            }
        )
    }

    fun showInterstitialAd(
        activity: Activity,
        onAdDismissed: () -> Unit,
        onFailedToShow: ((String) -> Unit)? = null
    ) {
        val currentAd = interstitialAd
        if (currentAd == null) {
            val errMsg = "Interstitial ad is not ready yet. Preloading and proceeding directly to game."
            Log.d("AdMobInterstitial", errMsg)
            loadInterstitialAd(activity)
            onFailedToShow?.invoke(errMsg)
            onAdDismissed()
            return
        }

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d("AdMobInterstitial", "Interstitial ad clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d("AdMobInterstitial", "Interstitial ad dismissed by user. Starting game.")
                interstitialAd = null
                // Automatically preload next interstitial ad for future game start
                loadInterstitialAd(activity)
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                val errMsg = "Interstitial ad failed to display (Code: ${adError.code}): ${adError.message}"
                Log.e("AdMobInterstitial", errMsg)
                interstitialAd = null
                loadInterstitialAd(activity)
                onFailedToShow?.invoke(errMsg)
                onAdDismissed()
            }

            override fun onAdImpression() {
                Log.d("AdMobInterstitial", "Interstitial ad impression logged.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("AdMobInterstitial", "Interstitial ad displayed full screen.")
            }
        }

        currentAd.show(activity)
    }
}

/**
 * Provider-agnostic AdService interface.
 * Abstracts any ad network SDK (e.g. AdMob, Unity Ads, AppLovin).
 */
interface AdService {
    fun isAdReady(placementType: AdPlacementType): Boolean
    fun loadAd(placementType: AdPlacementType)
    suspend fun showAd(placementType: AdPlacementType, title: String): AdResult
}

/**
 * MockAdProvider - Simulates a realistic ad provider workflow for testing & development.
 * 
 * Features:
 * - Fake loading delay (1-2 sec)
 * - Full-screen test ad experience with countdown timer
 * - Does NOT award task/game rewards directly (unlocks gate only)
 */
class MockAdProvider : AdService {

    private val readyPlacements = mutableMapOf<AdPlacementType, Boolean>()

    override fun isAdReady(placementType: AdPlacementType): Boolean {
        return readyPlacements[placementType] ?: true
    }

    override fun loadAd(placementType: AdPlacementType) {
        readyPlacements[placementType] = true
    }

    override suspend fun showAd(placementType: AdPlacementType, title: String): AdResult {
        // Simulate network loading delay before displaying ad frame
        delay(1200L)
        
        // Note: The UI layer handles the interactive timer & skip state via the AdGateScreen Composable.
        return AdResult(
            status = AdResultStatus.COMPLETED,
            placementType = placementType,
            message = "Mock ad completed successfully for $title"
        )
    }
}

/**
 * RealAdProvider - Stub implementation for future SDK integration.
 * 
 * ==============================================================================
 * SINGLE ISOLATED INTEGRATION POINT FOR REAL AD SDK (AdMob / Unity Ads / AppLovin)
 * ==============================================================================
 * To plug in a real Ad SDK later:
 * 1. Add your AdMob / Unity Ads SDK dependencies in build.gradle.kts
 * 2. Initialize the SDK in your Application/MainActivity class
 * 3. Implement loadAd / showAd callbacks inside RealAdProvider below
 * 4. Toggle AD_PROVIDER = "real" in AppConfig or Remote Config
 * ==============================================================================
 */
class RealAdProvider : AdService {
    override fun isAdReady(placementType: AdPlacementType): Boolean {
        // TODO [REAL AD SDK]: Return real SDK readiness status (e.g. RewardedAd.isLoaded())
        return false
    }

    override fun loadAd(placementType: AdPlacementType) {
        // TODO [REAL AD SDK]: Call real SDK load API (e.g. RewardedAd.load(...))
    }

    override suspend fun showAd(placementType: AdPlacementType, title: String): AdResult {
        // TODO [REAL AD SDK]: Present real full-screen ad and map listener callbacks:
        // - onUserEarnedReward / onAdDismissed -> AdResultStatus.COMPLETED
        // - onAdDismissed before reward -> AdResultStatus.CLOSED_EARLY
        // - onAdFailedToShow -> AdResultStatus.FAILED
        return AdResult(AdResultStatus.FAILED, placementType, "Real Ad SDK not yet initialized.")
    }
}

/**
 * AdServiceFactory - Central provider resolver.
 * Swapping from Mock to Real Provider is controlled via appConfig (adProvider == "real").
 */
object AdServiceFactory {
    fun getAdProvider(providerType: String = "mock"): AdService {
        return when (providerType.lowercase()) {
            "real" -> RealAdProvider()
            else -> MockAdProvider()
        }
    }
}
