package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.EarnMateViewModel
import com.example.ui.Screen
import com.example.ui.components.AdGateDialog
import com.example.ui.components.EarnMateBottomNavigation
import com.example.ui.screens.*
import com.example.ui.theme.EarnMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val requestConfiguration = com.google.android.gms.ads.RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR))
                .build()
            com.google.android.gms.ads.MobileAds.setRequestConfiguration(requestConfiguration)

            com.google.android.gms.ads.MobileAds.initialize(this) { status ->
                android.util.Log.d("AdMob", "Google Mobile Ads SDK Initialized: ${status.adapterStatusMap}")
                // Preload Rewarded Ad & Interstitial Ad in background
                com.example.data.ad.RewardedAdManager.loadRewardedAd(this@MainActivity)
                com.example.data.ad.InterstitialAdManager.loadInterstitialAd(this@MainActivity)
            }
        } catch (e: Exception) {
            android.util.Log.w("AdMob", "Warning initializing Google Mobile Ads SDK: ${e.message}", e)
        }
        enableEdgeToEdge()
        setContent {
            val viewModel: EarnMateViewModel = viewModel()
            val user by viewModel.currentUser.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val isAdminMode by viewModel.isAdminMode.collectAsState()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val snackbarMessage by viewModel.snackbarMessage.collectAsState()
            val notifications by viewModel.notifications.collectAsState()
            val pendingAdGateAction by viewModel.pendingAdGateAction.collectAsState()

            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(snackbarMessage) {
                snackbarMessage?.let {
                    snackbarHostState.showSnackbar(it)
                    viewModel.clearSnackbar()
                }
            }

            EarnMateTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (user != null && currentScreen != Screen.Login && currentScreen != Screen.Signup && currentScreen != Screen.ForgotPassword) {
                            EarnMateBottomNavigation(
                                currentScreen = currentScreen,
                                isAdminMode = isAdminMode && (user?.isAdmin == true),
                                unreadNotificationCount = notifications.count { !it.isRead },
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (user == null) {
                            when (currentScreen) {
                                is Screen.Signup -> SignupScreen(viewModel)
                                is Screen.ForgotPassword -> ForgotPasswordScreen(viewModel)
                                is Screen.AdminLogin -> AdminLoginScreen(viewModel)
                                else -> LoginScreen(viewModel)
                            }
                        } else {
                            Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                                if (screen.route.startsWith("admin_") && user?.isAdmin != true) {
                                    DashboardScreen(viewModel)
                                } else {
                                    when (screen) {
                                        is Screen.Dashboard -> DashboardScreen(viewModel)
                                        is Screen.Tasks -> TasksScreen(viewModel)
                                        is Screen.Offers -> OffersScreen(viewModel)
                                        is Screen.GamesHub -> GamesHubScreen(viewModel)
                                        is Screen.SpinWheel -> SpinWheelScreen(viewModel)
                                        is Screen.ScratchCard -> ScratchCardScreen(viewModel)
                                        is Screen.DailyQuiz -> DailyQuizScreen(viewModel)
                                        is Screen.MemoryMatch -> MemoryMatchScreen(viewModel)
                                        is Screen.LuckyDraw -> LuckyDrawScreen(viewModel)
                                        is Screen.Wallet -> WalletScreen(viewModel)
                                        is Screen.Withdraw -> WithdrawScreen(viewModel)
                                        is Screen.Referrals -> ReferralScreen(viewModel)
                                        is Screen.DailyCheckIn -> DailyCheckInScreen(viewModel)
                                        is Screen.Leaderboard -> LeaderboardScreen(viewModel)
                                        is Screen.Notifications -> NotificationsScreen(viewModel)
                                        is Screen.Profile -> ProfileScreen(viewModel)
                                        is Screen.Settings -> SettingsScreen(viewModel)
                                        is Screen.Support -> SupportScreen(viewModel)
                                        is Screen.Reels -> ReelsScreen(viewModel)
                                        is Screen.CreateReel -> CreateReelScreen(viewModel)
                                        is Screen.FreelanceHub -> FreelanceHubScreen(viewModel)
                                        is Screen.BrowseJobs -> BrowseJobsScreen(viewModel)
                                        is Screen.JobDetails -> JobDetailsScreen(viewModel)
                                        is Screen.PostJob -> PostJobScreen(viewModel)
                                        is Screen.BrowseServices -> BrowseServicesScreen(viewModel)
                                        is Screen.ServiceDetails -> ServiceDetailsScreen(viewModel)
                                        is Screen.CreateService -> CreateServiceScreen(viewModel)
                                        is Screen.FreelancerProfileView -> FreelancerProfileScreen(viewModel)
                                        is Screen.FreelanceOrders -> FreelanceOrdersScreen(viewModel)
                                        is Screen.OrderWorkspace -> OrderWorkspaceScreen(viewModel)
                                        is Screen.AdminLogin -> AdminLoginScreen(viewModel)
                                        is Screen.AdminDashboard -> AdminDashboardScreen(viewModel)
                                        is Screen.AdminUsers -> AdminUsersScreen(viewModel)
                                        is Screen.AdminTasks -> AdminTasksScreen(viewModel)
                                        is Screen.AdminSubmissions -> AdminSubmissionsScreen(viewModel)
                                        is Screen.AdminRewards -> AdminRewardsScreen(viewModel)
                                        is Screen.AdminGames -> AdminGamesScreen(viewModel)
                                        is Screen.AdminWithdrawals -> AdminWithdrawalsScreen(viewModel)
                                        is Screen.AdminFreelancer -> AdminFreelanceScreen(viewModel)
                                        is Screen.AdminJobs -> AdminJobsScreen(viewModel)
                                        is Screen.AdminReels -> AdminReelsScreen(viewModel)
                                        is Screen.AdminReports -> AdminReportsScreen(viewModel)
                                        is Screen.AdminPremium -> AdminPremiumScreen(viewModel)
                                        is Screen.AdminAds -> AdminAdsScreen(viewModel)
                                        is Screen.AdminNotifications -> AdminNotificationsScreen(viewModel)
                                        is Screen.AdminSettings -> AdminSettingsScreen(viewModel)
                                        is Screen.AdminAdGate -> AdminAdGateScreen(viewModel)
                                        is Screen.AdminActivity -> AdminActivityScreen(viewModel)
                                        is Screen.Premium -> PremiumScreen(viewModel)
                                        is Screen.SavedJobs -> SavedJobsScreen(viewModel)
                                        is Screen.ProposalTemplates -> ProposalTemplatesScreen(viewModel)
                                        is Screen.FreelancerAnalytics -> FreelancerAnalyticsScreen(viewModel)
                                        else -> DashboardScreen(viewModel)
                                    }
                                }
                            }
                        }
                    }

                    // Render AdGateDialog when an action is gated
                    pendingAdGateAction?.let { pending ->
                        AdGateDialog(
                            viewModel = viewModel,
                            targetType = pending.targetType,
                            targetTitle = pending.targetTitle,
                            targetId = pending.targetId,
                            onDismiss = { viewModel.dismissAdGate() },
                            onUnlocked = { pending.onUnlocked() }
                        )
                    }
                }
            }
        }
    }
}
