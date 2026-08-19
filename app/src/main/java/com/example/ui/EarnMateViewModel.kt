package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.EarnMateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val iconName: String) {
    object Login : Screen("login", "Login", "lock")
    object Signup : Screen("signup", "Signup", "person_add")
    object ForgotPassword : Screen("forgot_pass", "Forgot Password", "key")
    object Dashboard : Screen("dashboard", "Home", "home")
    object Tasks : Screen("tasks", "Tasks", "task_alt")
    object Offers : Screen("offers", "Offers", "local_offer")
    object GamesHub : Screen("games_hub", "Games", "casino")
    object SpinWheel : Screen("spin_wheel", "Spin & Win", "casino")
    object ScratchCard : Screen("scratch_card", "Scratch & Earn", "gesture")
    object DailyQuiz : Screen("daily_quiz", "Daily Quiz", "quiz")
    object MemoryMatch : Screen("memory_match", "Memory Flip", "extension")
    object LuckyDraw : Screen("lucky_draw", "Lucky Draw", "confirmation_number")
    object Wallet : Screen("wallet", "Wallet", "account_balance_wallet")
    object Withdraw : Screen("withdraw", "Withdraw", "payments")
    object Referrals : Screen("referrals", "Refer", "share")
    object DailyCheckIn : Screen("daily", "Daily Bonus", "calendar_today")
    object Leaderboard : Screen("leaderboard", "Leaderboard", "leaderboard")
    object Notifications : Screen("notifications", "Notifications", "notifications")
    object Profile : Screen("profile", "Profile", "person")
    object Settings : Screen("settings", "Settings", "settings")
    object Support : Screen("support", "Support", "help")

    // Reels Screens
    object Reels : Screen("reels", "Short Reels", "movie")
    object CreateReel : Screen("create_reel", "Upload Reel", "video_call")

    // Freelancer Marketplace Screens
    object FreelanceHub : Screen("freelance_hub", "Freelance Hub", "work")
    object BrowseJobs : Screen("browse_jobs", "Find Jobs", "search")
    object JobDetails : Screen("job_details", "Job Details", "assignment")
    object PostJob : Screen("post_job", "Post a Job", "post_add")
    object BrowseServices : Screen("browse_services", "Find Services", "grid_view")
    object ServiceDetails : Screen("service_details", "Service Details", "design_services")
    object CreateService : Screen("create_service", "List Service", "add_business")
    object FreelancerProfileView : Screen("freelance_profile", "Freelancer Profile", "account_box")
    object FreelanceOrders : Screen("freelance_orders", "My Orders", "shopping_bag")
    object OrderWorkspace : Screen("order_workspace", "Workspace", "chat")
    
    // Premium Membership Screens
    object Premium : Screen("premium", "EarnMate Premium", "workspace_premium")
    object SavedJobs : Screen("saved_jobs", "Saved Jobs", "bookmark")
    object ProposalTemplates : Screen("proposal_templates", "Proposal Templates", "description")
    object FreelancerAnalytics : Screen("freelance_analytics", "Analytics & Insights", "insights")
    
    // Admin Console Screens
    object AdminLogin : Screen("admin_login", "Admin Login", "lock")
    object AdminDashboard : Screen("admin_dash", "Admin Dashboard", "admin_panel_settings")
    object AdminUsers : Screen("admin_users", "User Management", "group")
    object AdminTasks : Screen("admin_tasks", "Manage Tasks", "add_task")
    object AdminSubmissions : Screen("admin_submissions", "Verify Proofs", "fact_check")
    object AdminRewards : Screen("admin_rewards", "Reward Rules", "card_giftcard")
    object AdminGames : Screen("admin_games", "Games & Rules", "sports_esports")
    object AdminWithdrawals : Screen("admin_withdrawals", "Withdrawal Requests", "verified_user")
    object AdminFreelancer : Screen("admin_freelance", "Freelancers", "business_center")
    object AdminJobs : Screen("admin_jobs", "Job Moderation", "work")
    object AdminReels : Screen("admin_reels", "Reels Moderation", "rate_review")
    object AdminReports : Screen("admin_reports", "Central Reports", "report")
    object AdminPremium : Screen("admin_premium", "Premium System", "workspace_premium")
    object AdminAds : Screen("admin_ads", "Ads Configuration", "campaign")
    object AdminNotifications : Screen("admin_notifications", "Push Notifications", "notifications_active")
    object AdminSettings : Screen("admin_config", "System Settings", "tune")
    object AdminAdGate : Screen("admin_adgate", "Ad Gate & Logs", "play_circle")
    object AdminActivity : Screen("admin_activity", "Audit Logs", "history")
}

class EarnMateViewModel(
    private val repository: EarnMateRepository = EarnMateRepository()
) : ViewModel() {

    val currentUser = repository.currentUser
    val appConfig = repository.appConfig
    val tasks = repository.tasks
    val submissions = repository.submissions
    val offers = repository.offers
    val transactions = repository.transactions
    val withdrawals = repository.withdrawals
    val notifications = repository.notifications
    val supportTickets = repository.supportTickets
    val referrals = repository.referrals
    val allUsers = repository.allUsers
    val centralReports = repository.centralReports
    val adminActivityLogs = repository.adminActivityLogs
    val broadcastNotifications = repository.broadcastNotifications

    // Games State
    val gameConfigs = repository.gameConfigs
    val gamePlays = repository.gamePlays
    val luckyDrawPools = repository.luckyDrawPools
    val userLuckyDrawTickets = repository.userLuckyDrawTickets
    val sampleQuizQuestions = repository.getSampleQuizQuestions()

    // Ad Gate State & Actions
    val adGateLogs = repository.adGateLogs
    private val _pendingAdGateAction = MutableStateFlow<AdGatePendingAction?>(null)
    val pendingAdGateAction: StateFlow<AdGatePendingAction?> = _pendingAdGateAction.asStateFlow()

    // Reels State
    val reels = repository.reels
    val reelReports = repository.reelReports

    // Freelancer Marketplace State
    val freelanceConfig = repository.freelanceConfig
    val freelancerProfiles = repository.freelancerProfiles
    val freelancerServices = repository.freelancerServices
    val freelanceJobs = repository.freelanceJobs
    val jobProposals = repository.jobProposals
    val freelanceOrders = repository.freelanceOrders
    val orderDeliveries = repository.orderDeliveries
    val orderMessages = repository.orderMessages
    val freelancerReviews = repository.freelancerReviews
    val freelanceDisputes = repository.freelanceDisputes

    // Selection States for Freelancer Module
    private val _selectedJobId = MutableStateFlow<String?>(null)
    val selectedJobId: StateFlow<String?> = _selectedJobId.asStateFlow()

    private val _selectedServiceId = MutableStateFlow<String?>(null)
    val selectedServiceId: StateFlow<String?> = _selectedServiceId.asStateFlow()

    private val _selectedOrderId = MutableStateFlow<String?>(null)
    val selectedOrderId: StateFlow<String?> = _selectedOrderId.asStateFlow()

    private val _selectedFreelancerUserId = MutableStateFlow<String?>(null)
    val selectedFreelancerUserId: StateFlow<String?> = _selectedFreelancerUserId.asStateFlow()

    // Active Screen Route
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Admin Mode Toggle
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    // Dark Theme Toggle State
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Snackbar Feedback Message
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Selected Task Detail
    private val _selectedTask = MutableStateFlow<TaskItem?>(null)
    val selectedTask: StateFlow<TaskItem?> = _selectedTask.asStateFlow()

    // Leaderboard Computation
    val leaderboard: StateFlow<List<LeaderboardEntry>> = combine(allUsers, currentUser) { users, current ->
        users.filter { !it.hideFromLeaderboard }
            .sortedByDescending { it.totalEarned }
            .mapIndexed { index, user ->
                LeaderboardEntry(
                    rank = index + 1,
                    username = if (user.uid == current?.uid) "${user.username} (You)" else user.username,
                    totalEarned = user.totalEarned,
                    completedTasks = user.completedTasksCount,
                    isCurrentUser = user.uid == current?.uid
                )
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: Screen) {
        if (screen.route.startsWith("admin_")) {
            val user = currentUser.value
            if (user != null && !user.isAdmin) {
                showSnackbar("Access Denied: Admin privileges required.")
                _currentScreen.value = Screen.Dashboard
                return
            }
            if (user == null && screen != Screen.AdminLogin) {
                showSnackbar("Access Denied: Admin privileges required.")
                _currentScreen.value = Screen.Login
                return
            }
        }
        _currentScreen.value = screen
    }

    fun toggleAdminMode(enabled: Boolean) {
        val user = currentUser.value
        if (enabled) {
            if (user?.isAdmin == true) {
                _isAdminMode.value = true
                _currentScreen.value = Screen.AdminDashboard
                showSnackbar("Switched to Admin Console Mode")
            } else {
                _isAdminMode.value = false
                _currentScreen.value = Screen.Dashboard
                showSnackbar("Access Denied: Admin privileges required.")
            }
        } else {
            _isAdminMode.value = false
            _currentScreen.value = Screen.Dashboard
            showSnackbar("Switched to User Dashboard")
        }
    }

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun showSnackbar(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun selectTask(task: TaskItem) {
        _selectedTask.value = task
    }

    fun clearSelectedTask() {
        _selectedTask.value = null
    }

    // --- Authentication Actions ---
    fun login(email: String, pass: String) {
        val res = repository.login(email, pass)
        res.onSuccess {
            showSnackbar("Welcome back, ${it.fullName}!")
            navigateTo(Screen.Dashboard)
        }.onFailure {
            showSnackbar(it.message ?: "Login failed")
        }
    }

    fun signUp(fullName: String, email: String, pass: String, phone: String, refCode: String?) {
        val res = repository.signUp(fullName, email, pass, phone, refCode)
        res.onSuccess {
            showSnackbar("Account created successfully! Welcome to EarnMate India.")
            navigateTo(Screen.Dashboard)
        }.onFailure {
            showSnackbar(it.message ?: "Registration failed")
        }
    }

    fun logout() {
        repository.logout()
        _isAdminMode.value = false
        showSnackbar("Logged out successfully.")
        navigateTo(Screen.Login)
    }

    fun deleteAccount() {
        val res = repository.deleteAccount()
        res.onSuccess {
            showSnackbar("Account deleted.")
            navigateTo(Screen.Login)
        }.onFailure {
            showSnackbar(it.message ?: "Failed to delete account.")
        }
    }

    fun updateProfile(fullName: String, phone: String, username: String, language: String) {
        val res = repository.updateProfile(fullName, phone, username, language)
        res.onSuccess {
            showSnackbar("Profile updated successfully.")
        }.onFailure {
            showSnackbar(it.message ?: "Update failed.")
        }
    }

    // --- Task Actions ---
    fun submitTaskProof(taskId: String, proofContent: String) {
        val res = repository.submitTaskProof(taskId, proofContent)
        res.onSuccess {
            showSnackbar("Task submitted successfully for review!")
            clearSelectedTask()
        }.onFailure {
            showSnackbar(it.message ?: "Submission failed.")
        }
    }

    // --- Daily Bonus ---
    fun claimDailyCheckIn() {
        val res = repository.claimDailyCheckIn()
        res.onSuccess { reward ->
            showSnackbar("🎉 Claimed ₹${reward.toInt()} Daily Streak Reward!")
        }.onFailure {
            showSnackbar(it.message ?: "Check-in failed.")
        }
    }

    // --- Rewarded Ad Bonus ---
    fun claimRewardedAdBonus(amount: Double = 5.0, rewardType: String = "Bonus") {
        viewModelScope.launch {
            val res = repository.claimRewardedAdBonus(amount, rewardType)
            res.onSuccess { credited ->
                showSnackbar("🎁 Ad completed! +₹${"%.2f".format(credited)} credited to your wallet!")
            }.onFailure { err ->
                showSnackbar(err.message ?: "Failed to credit ad reward.")
            }
        }
    }

    // --- Withdrawal ---
    fun requestWithdrawal(method: WithdrawalMethod, amount: Double, payoutDetails: String) {
        viewModelScope.launch {
            val res = repository.requestWithdrawal(method, amount, payoutDetails)
            res.onSuccess {
                showSnackbar("Withdrawal request for ₹${amount.toInt()} submitted successfully.")
                navigateTo(Screen.Wallet)
            }.onFailure {
                showSnackbar(it.message ?: "Withdrawal request failed.")
            }
        }
    }

    // --- Support Ticket ---
    fun submitSupportTicket(subject: String, category: String, message: String) {
        val res = repository.submitSupportTicket(subject, category, message)
        res.onSuccess {
            showSnackbar("Support ticket ${it.id} submitted.")
        }.onFailure {
            showSnackbar(it.message ?: "Failed to create support ticket.")
        }
    }

    // --- Admin Actions ---
    fun adminApproveSubmission(subId: String, note: String?) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminApproveSubmission(subId, note)
        res.onSuccess { showSnackbar("Submission approved and user credited.") }
            .onFailure { showSnackbar(it.message ?: "Approval failed.") }
    }

    fun adminRejectSubmission(subId: String, note: String) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminRejectSubmission(subId, note)
        res.onSuccess { showSnackbar("Submission rejected.") }
            .onFailure { showSnackbar(it.message ?: "Rejection failed.") }
    }

    fun adminProcessWithdrawal(reqId: String, newStatus: WithdrawalStatus, txRef: String?, reason: String?) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminProcessWithdrawal(reqId, newStatus, txRef, reason)
        res.onSuccess { showSnackbar("Withdrawal $reqId updated to ${newStatus.label}.") }
            .onFailure { showSnackbar(it.message ?: "Update failed.") }
    }

    fun adminCreateTask(title: String, desc: String, category: TaskCategory, reward: Double, mins: Int, difficulty: TaskDifficulty, proofType: ProofType, instructions: List<String>) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminCreateTask(title, desc, category, reward, mins, difficulty, proofType, instructions)
        res.onSuccess { showSnackbar("New task '${it.title}' published!") }
            .onFailure { showSnackbar(it.message ?: "Task creation failed.") }
    }

    fun adminAuthenticate(email: String, pass: String) {
        val res = repository.adminAuthenticate(email, pass)
        res.onSuccess {
            _isAdminMode.value = true
            showSnackbar("Admin login successful. Welcome, ${it.fullName}!")
            navigateTo(Screen.AdminDashboard)
        }.onFailure {
            showSnackbar(it.message ?: "Admin authentication failed.")
        }
    }

    fun adminSuspendUser(userId: String, reason: String) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminSuspendUser(userId, reason)
        res.onSuccess { showSnackbar("User suspended.") }
            .onFailure { showSnackbar(it.message ?: "Failed to suspend user.") }
    }

    fun adminUnsuspendUser(userId: String) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminUnsuspendUser(userId)
        res.onSuccess { showSnackbar("User unsuspended.") }
            .onFailure { showSnackbar(it.message ?: "Failed to unsuspend user.") }
    }

    fun adminUpdateTask(task: TaskItem) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminUpdateTask(task)
        res.onSuccess { showSnackbar("Task '${task.title}' updated.") }
            .onFailure { showSnackbar(it.message ?: "Failed to update task.") }
    }

    fun adminDeleteTask(taskId: String) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminDeleteTask(taskId)
        res.onSuccess { showSnackbar("Task deleted.") }
            .onFailure { showSnackbar(it.message ?: "Failed to delete task.") }
    }

    fun adminToggleTaskActive(taskId: String) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminToggleTaskActive(taskId)
        res.onSuccess { showSnackbar("Task active status toggled.") }
            .onFailure { showSnackbar(it.message ?: "Failed to toggle task.") }
    }

    fun adminResolveReport(reportId: String, status: ReportStatus, adminNotes: String) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminResolveReport(reportId, status, adminNotes)
        res.onSuccess { showSnackbar("Report marked as ${status.label}.") }
            .onFailure { showSnackbar(it.message ?: "Failed to update report.") }
    }

    fun adminSendNotification(title: String, message: String, audience: NotificationAudience, targetUserId: String? = null) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminSendNotification(title, message, audience, targetUserId)
        res.onSuccess { showSnackbar("Broadcast notification sent to ${audience.label}!") }
            .onFailure { showSnackbar(it.message ?: "Failed to broadcast notification.") }
    }

    fun adminUpdateAdsConfig(adsEnabled: Boolean, bannerEnabled: Boolean, interstitialEnabled: Boolean, rewardedEnabled: Boolean, adGateEnabled: Boolean) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminUpdateAdsConfig(adsEnabled, bannerEnabled, interstitialEnabled, rewardedEnabled, adGateEnabled)
        res.onSuccess { showSnackbar("AdMob settings updated successfully.") }
            .onFailure { showSnackbar(it.message ?: "Failed to update AdMob settings.") }
    }

    fun adminDeleteJobListing(jobId: String) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminDeleteJobListing(jobId)
        res.onSuccess { showSnackbar("Job listing removed by admin.") }
            .onFailure { showSnackbar(it.message ?: "Failed to remove job listing.") }
    }

    fun adminUpdateConfig(minWd: Double, refReward: Double, dailyBonus: Double, maint: Boolean, ann: String, ads: Boolean) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.adminUpdateConfig(minWd, refReward, dailyBonus, maint, ann, ads)
        res.onSuccess { showSnackbar("App configuration updated!") }
            .onFailure { showSnackbar(it.message ?: "Config update failed.") }
    }

    fun markNotificationRead(id: String) {
        repository.markNotificationRead(id)
    }

    fun toggleLeaderboardVisibility(hide: Boolean) {
        repository.toggleLeaderboardVisibility(hide)
        showSnackbar(if (hide) "Profile hidden from public leaderboard." else "Profile visible on leaderboard.")
    }

    // --- Games & Rewards Actions ---
    fun getTodayGamePlayCount(type: GameType): Int = repository.getTodayGamePlayCount(type)

    fun playSpinWheel(onResult: (Double) -> Unit) {
        val res = repository.playSpinWheel()
        res.onSuccess { reward ->
            if (reward > 0) {
                showSnackbar("🎉 Congratulations! Won ₹${"%.1f".format(reward)} from Spin & Win!")
            } else {
                showSnackbar("Better luck next time! Try another spin.")
            }
            onResult(reward)
        }.onFailure {
            showSnackbar(it.message ?: "Spin failed.")
        }
    }

    fun playScratchCard(onResult: (Double) -> Unit) {
        val res = repository.playScratchCard()
        res.onSuccess { reward ->
            if (reward > 0) {
                showSnackbar("✨ Scratched & Earned ₹${"%.1f".format(reward)}!")
            } else {
                showSnackbar("No reward this time! Try another card.")
            }
            onResult(reward)
        }.onFailure {
            showSnackbar(it.message ?: "Scratch failed.")
        }
    }

    fun submitQuizResult(correctCount: Int, totalQuestions: Int, timeTakenSeconds: Int, onResult: (QuizResult) -> Unit) {
        val res = repository.submitQuizResult(correctCount, totalQuestions, timeTakenSeconds)
        res.onSuccess { quizRes ->
            showSnackbar("🧠 Quiz completed! Score: ${quizRes.correctAnswers}/${quizRes.totalQuestions}. Earned ₹${"%.1f".format(quizRes.rewardEarned)}!")
            onResult(quizRes)
        }.onFailure {
            showSnackbar(it.message ?: "Quiz submission failed.")
        }
    }

    fun playMemoryMatch(timeTakenSeconds: Int, movesCount: Int, onResult: (Double) -> Unit) {
        val res = repository.playMemoryMatch(timeTakenSeconds, movesCount)
        res.onSuccess { reward ->
            showSnackbar("🧩 Memory match cleared in ${timeTakenSeconds}s! Earned ₹${"%.1f".format(reward)}.")
            onResult(reward)
        }.onFailure {
            showSnackbar(it.message ?: "Memory match error.")
        }
    }

    fun claimLuckyDrawTicket(poolId: String, onResult: (LuckyDrawTicket) -> Unit) {
        val res = repository.claimLuckyDrawTicket(poolId)
        res.onSuccess { tkt ->
            showSnackbar("🎟️ Lucky Draw Ticket #${tkt.ticketNumber} claimed for today's pool!")
            onResult(tkt)
        }.onFailure {
            showSnackbar(it.message ?: "Failed to claim ticket.")
        }
    }

    fun adminUpdateGameConfig(type: GameType, isEnabled: Boolean, maxDailyPlays: Int, minReward: Double, maxReward: Double) {
        val res = repository.adminUpdateGameConfig(type, isEnabled, maxDailyPlays, minReward, maxReward)
        res.onSuccess { showSnackbar("Updated config for ${type.title}.") }
            .onFailure { showSnackbar(it.message ?: "Failed to update game config.") }
    }

    // --- Ad Gate Navigation & Verification Logic ---

    fun checkAndRunAdGate(
        targetType: String, // "Task", "Offer", "Game"
        targetId: String,
        targetTitle: String,
        onUnlocked: () -> Unit
    ) {
        if (isPremiumUser()) {
            onUnlocked()
            return
        }
        val config = appConfig.value
        val requiresAd = config.adGateEnabled && when (targetType.lowercase()) {
            "task" -> config.requireAdForTasks
            "offer" -> config.requireAdForOffers
            "game" -> config.requireAdForGames
            else -> false
        }

        if (!requiresAd) {
            onUnlocked()
        } else {
            _pendingAdGateAction.value = AdGatePendingAction(
                targetType = targetType,
                targetId = targetId,
                targetTitle = targetTitle,
                placementType = AdPlacementType.REWARDED,
                onUnlocked = onUnlocked
            )
        }
    }

    fun dismissAdGate() {
        _pendingAdGateAction.value = null
    }

    fun logAdGateEvent(
        placementType: AdPlacementType,
        targetType: String,
        targetId: String,
        targetTitle: String,
        status: AdResultStatus
    ) {
        repository.logAdGateAttempt(placementType, targetType, targetId, targetTitle, status)
    }

    fun updateAdGateConfig(
        enabled: Boolean,
        provider: String,
        forTasks: Boolean,
        forOffers: Boolean,
        forGames: Boolean,
        seconds: Int
    ) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        repository.updateAdGateConfig(enabled, provider, forTasks, forOffers, forGames, seconds)
        showSnackbar("Ad Gate configuration saved successfully!")
    }

    // --- Reels Actions ---

    fun uploadReel(
        caption: String,
        category: String,
        language: String,
        durationSeconds: Int,
        videoUrl: String
    ) {
        val res = repository.uploadReel(caption, category, language, durationSeconds, videoUrl)
        res.onSuccess {
            showSnackbar("Reel submitted for admin moderation! 🎬")
            navigateTo(Screen.Reels)
        }.onFailure {
            showSnackbar(it.message ?: "Failed to upload reel.")
        }
    }

    fun approveReel(reelId: String) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.approveReel(reelId)
        res.onSuccess { showSnackbar("Reel approved and published!") }
            .onFailure { showSnackbar(it.message ?: "Failed to approve reel.") }
    }

    fun rejectReel(reelId: String, reason: String) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.rejectReel(reelId, reason)
        res.onSuccess { showSnackbar("Reel rejected.") }
            .onFailure { showSnackbar(it.message ?: "Failed to reject reel.") }
    }

    fun removeReel(reelId: String) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        val res = repository.removeReel(reelId)
        res.onSuccess { showSnackbar("Reel removed.") }
            .onFailure { showSnackbar(it.message ?: "Failed to remove reel.") }
    }

    fun toggleLikeReel(reelId: String) {
        repository.toggleLikeReel(reelId)
    }

    fun reportReel(reelId: String, reason: ReelReportReason, notes: String) {
        val res = repository.reportReel(reelId, reason, notes)
        res.onSuccess { showSnackbar("Reel reported to moderators for safety review.") }
            .onFailure { showSnackbar(it.message ?: "Failed to submit report.") }
    }

    fun resolveReelReport(reportId: String, action: String) {
        if (currentUser.value?.isAdmin != true) {
            showSnackbar("Access Denied: Admin privileges required.")
            return
        }
        repository.resolveReelReport(reportId, action)
        showSnackbar("Report marked as resolved ($action).")
    }

    fun logReelView(reelId: String, watchDurationMs: Long) {
        val res = repository.logReelView(reelId, watchDurationMs)
        res.onSuccess { reward ->
            showSnackbar("Reel reward earned: +₹${String.format("%.2f", reward)}! 💰")
        }.onFailure { err ->
            // Silent or minor toast if already claimed
        }
    }

    // --- Freelancer Marketplace ViewModel Handlers ---

    fun selectJob(jobId: String?) {
        _selectedJobId.value = jobId
        if (jobId != null) navigateTo(Screen.JobDetails)
    }

    fun selectService(serviceId: String?) {
        _selectedServiceId.value = serviceId
        if (serviceId != null) navigateTo(Screen.ServiceDetails)
    }

    fun selectOrder(orderId: String?) {
        _selectedOrderId.value = orderId
        if (orderId != null) navigateTo(Screen.OrderWorkspace)
    }

    fun selectFreelancerUser(userId: String?) {
        _selectedFreelancerUserId.value = userId
        if (userId != null) navigateTo(Screen.FreelancerProfileView)
    }

    fun becomeFreelancer(
        bio: String,
        skills: List<String>,
        experienceLevel: String,
        languages: List<String>,
        portfolioLinks: List<String>
    ) {
        val res = repository.becomeFreelancer(bio, skills, experienceLevel, languages, portfolioLinks)
        res.onSuccess {
            showSnackbar("Welcome to Freelancer Hub! Your profile is live. 🚀")
            navigateTo(Screen.FreelanceHub)
        }.onFailure { showSnackbar(it.message ?: "Failed to activate freelancer profile.") }
    }

    fun createService(
        title: String,
        description: String,
        category: FreelancerCategory,
        startingPriceRupees: Double,
        deliveryTimeDays: Int,
        revisionsAllowed: Int,
        skills: List<String>,
        portfolioImages: List<String>
    ) {
        val res = repository.createService(title, description, category, startingPriceRupees, deliveryTimeDays, revisionsAllowed, skills, portfolioImages)
        res.onSuccess { srv ->
            showSnackbar("Service '${srv.title}' published successfully! 🎨")
            navigateTo(Screen.BrowseServices)
        }.onFailure { showSnackbar(it.message ?: "Failed to publish service.") }
    }

    fun postJob(
        title: String,
        description: String,
        category: FreelancerCategory,
        requiredSkills: List<String>,
        budgetRupees: Double,
        deadlineDays: Int,
        attachments: List<String>
    ) {
        val res = repository.postJob(title, description, category, requiredSkills, budgetRupees, deadlineDays, attachments)
        res.onSuccess { job ->
            showSnackbar("Job '${job.title}' posted successfully! 💼")
            navigateTo(Screen.BrowseJobs)
        }.onFailure { showSnackbar(it.message ?: "Failed to post job.") }
    }

    fun submitProposal(
        jobId: String,
        proposalMessage: String,
        proposedPriceRupees: Double,
        estimatedDeliveryDays: Int,
        attachments: List<String>
    ) {
        val res = repository.submitProposal(jobId, proposalMessage, proposedPriceRupees, estimatedDeliveryDays, attachments)
        res.onSuccess {
            showSnackbar("Proposal submitted to client successfully! 📝")
            navigateTo(Screen.BrowseJobs)
        }.onFailure { showSnackbar(it.message ?: "Failed to submit proposal.") }
    }

    fun acceptProposal(proposalId: String) {
        val res = repository.acceptProposal(proposalId)
        res.onSuccess { order ->
            showSnackbar("Proposal accepted! Order workspace initiated. 🤝")
            selectOrder(order.id)
        }.onFailure { showSnackbar(it.message ?: "Failed to accept proposal.") }
    }

    fun submitOrderDelivery(orderId: String, message: String, files: List<String>) {
        val res = repository.submitOrderDelivery(orderId, message, files)
        res.onSuccess {
            showSnackbar("Work delivered to client successfully! 📦")
        }.onFailure { showSnackbar(it.message ?: "Failed to deliver work.") }
    }

    fun approveOrderDelivery(orderId: String) {
        val res = repository.approveOrderDelivery(orderId)
        res.onSuccess {
            showSnackbar("Order approved! Earning dispatched to freelancer wallet. 🎉")
        }.onFailure { showSnackbar(it.message ?: "Failed to approve delivery.") }
    }

    fun requestOrderRevision(orderId: String, revisionNote: String) {
        val res = repository.requestOrderRevision(orderId, revisionNote)
        res.onSuccess {
            showSnackbar("Revision request sent to freelancer. 🔄")
        }.onFailure { showSnackbar(it.message ?: "Failed to request revision.") }
    }

    fun sendOrderMessage(orderId: String, messageText: String, attachments: List<String> = emptyList()) {
        val res = repository.sendOrderMessage(orderId, messageText, attachments)
        res.onFailure { showSnackbar(it.message ?: "Failed to send message.") }
    }

    fun submitFreelancerReview(orderId: String, rating: Int, reviewText: String) {
        val res = repository.submitFreelancerReview(orderId, rating, reviewText)
        res.onSuccess {
            showSnackbar("Thank you! Review submitted. ⭐")
        }.onFailure { showSnackbar(it.message ?: "Failed to submit review.") }
    }

    fun openDispute(orderId: String, reason: String, description: String, evidence: List<String>) {
        val res = repository.openDispute(orderId, reason, description, evidence)
        res.onSuccess {
            showSnackbar("Dispute case submitted. Admin team will review. ⚖️")
        }.onFailure { showSnackbar(it.message ?: "Failed to open dispute.") }
    }

    fun adminUpdateFreelanceConfig(commissionPercentage: Double, minOrderValueRupees: Double, autoApproveDays: Int) {
        val res = repository.adminUpdateFreelanceConfig(commissionPercentage, minOrderValueRupees, autoApproveDays)
        res.onSuccess { showSnackbar("Freelance Marketplace settings updated.") }
            .onFailure { showSnackbar(it.message ?: "Failed to update config.") }
    }

    fun adminResolveDispute(disputeId: String, resolutionStatus: String, adminNotes: String) {
        val res = repository.adminResolveDispute(disputeId, resolutionStatus, adminNotes)
        res.onSuccess { showSnackbar("Dispute updated: $resolutionStatus") }
            .onFailure { showSnackbar(it.message ?: "Failed to resolve dispute.") }
    }

    // --- Premium Membership State & Actions ---
    val premiumConfig = repository.premiumConfig
    val premiumPlans = repository.premiumPlans
    val premiumHistory = repository.premiumHistory
    val proposalTemplates = repository.proposalTemplates

    fun isPremiumUser(): Boolean = repository.isPremiumUser()
    fun hasPremiumFeature(featureKey: String): Boolean = repository.hasPremiumFeature(featureKey)

    fun toggleSaveJob(jobId: String) {
        val res = repository.toggleSaveJob(jobId)
        res.onSuccess { saved ->
            showSnackbar(if (saved) "Job saved to your Saved Jobs locker! 🔖" else "Job removed from Saved Jobs.")
        }.onFailure { showSnackbar(it.message ?: "Failed to save job.") }
    }

    fun addProposalTemplate(title: String, text: String) {
        val res = repository.addProposalTemplate(title, text)
        res.onSuccess { showSnackbar("Proposal template saved! 📄") }
            .onFailure { showSnackbar(it.message ?: "Failed to save template.") }
    }

    fun updateProposalTemplate(id: String, title: String, text: String) {
        val res = repository.updateProposalTemplate(id, title, text)
        res.onSuccess { showSnackbar("Proposal template updated.") }
            .onFailure { showSnackbar(it.message ?: "Failed to update template.") }
    }

    fun deleteProposalTemplate(id: String) {
        val res = repository.deleteProposalTemplate(id)
        res.onSuccess { showSnackbar("Template deleted.") }
    }

    fun duplicateProposalTemplate(template: ProposalTemplate) {
        val res = repository.duplicateProposalTemplate(template)
        res.onSuccess { showSnackbar("Template duplicated!") }
    }

    fun getFreelancerAnalytics(): FreelancerAnalytics = repository.getFreelancerAnalytics()

    fun adminUpdatePremiumConfig(config: PremiumConfig) {
        val res = repository.adminUpdatePremiumConfig(config)
        res.onSuccess { showSnackbar("Premium system settings updated!") }
            .onFailure { showSnackbar(it.message ?: "Failed to update settings.") }
    }

    fun adminSavePremiumPlan(plan: PremiumPlan) {
        val res = repository.adminSavePremiumPlan(plan)
        res.onSuccess { showSnackbar("Premium plan ${plan.planName} saved!") }
            .onFailure { showSnackbar(it.message ?: "Failed to save plan.") }
    }

    fun adminGrantPremium(userId: String, planId: String, customDays: Int? = null) {
        val res = repository.adminGrantPremium(userId, planId, customDays)
        res.onSuccess { showSnackbar("Premium granted successfully! 👑") }
            .onFailure { showSnackbar(it.message ?: "Failed to grant premium.") }
    }

    fun adminRevokePremium(userId: String) {
        val res = repository.adminRevokePremium(userId)
        res.onSuccess { showSnackbar("User premium membership revoked.") }
            .onFailure { showSnackbar(it.message ?: "Failed to revoke premium.") }
    }

    fun adminExtendPremium(userId: String, extraDays: Int) {
        val res = repository.adminExtendPremium(userId, extraDays)
        res.onSuccess { showSnackbar("User premium extended by $extraDays days!") }
            .onFailure { showSnackbar(it.message ?: "Failed to extend premium.") }
    }
}

data class AdGatePendingAction(
    val targetType: String,
    val targetId: String,
    val targetTitle: String,
    val placementType: AdPlacementType = AdPlacementType.REWARDED,
    val onUnlocked: () -> Unit
)

