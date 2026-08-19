package com.example.data.repository

import com.example.data.model.*
import com.example.data.payment.PaymentProvider
import com.example.data.payment.PaymentResult
import com.example.data.payment.StandardIndianPayoutProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EarnMateRepository {

    private val paymentProvider: PaymentProvider = StandardIndianPayoutProvider()

    // Configuration State
    private val _appConfig = MutableStateFlow(AppConfig())
    val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()

    // Current User Profile State
    private val _currentUser = MutableStateFlow<UserProfile?>(
        UserProfile(
            uid = "user_demo_001",
            username = "rahul_rewards",
            fullName = "Rahul Sharma",
            email = "rahul.sharma@example.in",
            phone = "+91 98765 43210",
            profilePhotoUrl = "",
            createdAt = System.currentTimeMillis() - 14 * 24 * 3600 * 1000L,
            referralCode = "EARN9823",
            totalEarned = 485.0,
            availableBalance = 240.0,
            pendingRewards = 45.0,
            completedTasksCount = 12,
            currentStreak = 4,
            lastCheckInDate = getYesterdayDateString(),
            isAdmin = false, // Default to false for normal user mode
            language = "English"
        )
    )
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    // Tasks Marketplace State
    private val _tasks = MutableStateFlow<List<TaskItem>>(getInitialTasks())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    // Task Submissions State
    private val _submissions = MutableStateFlow<List<TaskSubmission>>(getInitialSubmissions())
    val submissions: StateFlow<List<TaskSubmission>> = _submissions.asStateFlow()

    // Offers State
    private val _offers = MutableStateFlow<List<OfferItem>>(getInitialOffers())
    val offers: StateFlow<List<OfferItem>> = _offers.asStateFlow()

    // Wallet Transactions Ledger
    private val _transactions = MutableStateFlow<List<WalletTransaction>>(getInitialTransactions())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    // Withdrawal Requests State
    private val _withdrawals = MutableStateFlow<List<WithdrawalRequest>>(getInitialWithdrawals())
    val withdrawals: StateFlow<List<WithdrawalRequest>> = _withdrawals.asStateFlow()

    // Notifications State
    private val _notifications = MutableStateFlow<List<NotificationItem>>(getInitialNotifications())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Support Tickets State
    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(getInitialTickets())
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    // Referral List State
    private val _referrals = MutableStateFlow<List<ReferralUserSummary>>(getInitialReferrals())
    val referrals: StateFlow<List<ReferralUserSummary>> = _referrals.asStateFlow()

    // All Users List (for Admin management)
    private val _allUsers = MutableStateFlow<List<UserProfile>>(getInitialAllUsers())
    val allUsers: StateFlow<List<UserProfile>> = _allUsers.asStateFlow()

    // --- Games & Rewards Module State ---
    private val _gameConfigs = MutableStateFlow<List<GameConfigItem>>(getInitialGameConfigs())
    val gameConfigs: StateFlow<List<GameConfigItem>> = _gameConfigs.asStateFlow()

    private val _gamePlays = MutableStateFlow<List<GamePlayRecord>>(getInitialGamePlays())
    val gamePlays: StateFlow<List<GamePlayRecord>> = _gamePlays.asStateFlow()

    private val _luckyDrawPools = MutableStateFlow<List<LuckyDrawPool>>(getInitialLuckyDrawPools())
    val luckyDrawPools: StateFlow<List<LuckyDrawPool>> = _luckyDrawPools.asStateFlow()

    private val _userLuckyDrawTickets = MutableStateFlow<List<LuckyDrawTicket>>(getInitialLuckyDrawTickets())
    val userLuckyDrawTickets: StateFlow<List<LuckyDrawTicket>> = _userLuckyDrawTickets.asStateFlow()

    // --- Ad Gate Event Logs State ---
    private val _adGateLogs = MutableStateFlow<List<AdGateLog>>(getInitialAdGateLogs())
    val adGateLogs: StateFlow<List<AdGateLog>> = _adGateLogs.asStateFlow()

    // --- Reels State ---
    private val _reels = MutableStateFlow<List<Reel>>(getInitialReels())
    val reels: StateFlow<List<Reel>> = _reels.asStateFlow()

    private val _reelReports = MutableStateFlow<List<ReelReport>>(getInitialReelReports())
    val reelReports: StateFlow<List<ReelReport>> = _reelReports.asStateFlow()

    private val _reelViewRecords = MutableStateFlow<List<ReelViewRecord>>(emptyList())
    val reelViewRecords: StateFlow<List<ReelViewRecord>> = _reelViewRecords.asStateFlow()

    // --- Freelancer Marketplace State ---
    private val _freelancerProfiles = MutableStateFlow<List<FreelancerProfile>>(getInitialFreelancerProfiles())
    val freelancerProfiles: StateFlow<List<FreelancerProfile>> = _freelancerProfiles.asStateFlow()

    private val _freelancerServices = MutableStateFlow<List<FreelancerService>>(getInitialFreelancerServices())
    val freelancerServices: StateFlow<List<FreelancerService>> = _freelancerServices.asStateFlow()

    private val _freelanceJobs = MutableStateFlow<List<ClientJob>>(getInitialFreelanceJobs())
    val freelanceJobs: StateFlow<List<ClientJob>> = _freelanceJobs.asStateFlow()

    private val _jobProposals = MutableStateFlow<List<JobProposal>>(getInitialJobProposals())
    val jobProposals: StateFlow<List<JobProposal>> = _jobProposals.asStateFlow()

    private val _freelanceOrders = MutableStateFlow<List<FreelanceOrder>>(getInitialFreelanceOrders())
    val freelanceOrders: StateFlow<List<FreelanceOrder>> = _freelanceOrders.asStateFlow()

    private val _orderMessages = MutableStateFlow<List<OrderMessage>>(getInitialOrderMessages())
    val orderMessages: StateFlow<List<OrderMessage>> = _orderMessages.asStateFlow()

    private val _orderDeliveries = MutableStateFlow<List<OrderDelivery>>(getInitialOrderDeliveries())
    val orderDeliveries: StateFlow<List<OrderDelivery>> = _orderDeliveries.asStateFlow()

    private val _freelancerReviews = MutableStateFlow<List<FreelancerReview>>(getInitialFreelancerReviews())
    val freelancerReviews: StateFlow<List<FreelancerReview>> = _freelancerReviews.asStateFlow()

    private val _freelanceDisputes = MutableStateFlow<List<FreelanceDispute>>(emptyList())
    val freelanceDisputes: StateFlow<List<FreelanceDispute>> = _freelanceDisputes.asStateFlow()

    private val _freelanceConfig = MutableStateFlow(FreelanceConfig())
    val freelanceConfig: StateFlow<FreelanceConfig> = _freelanceConfig.asStateFlow()

    // --- Premium Membership System State ---
    private val _premiumConfig = MutableStateFlow(PremiumConfig())
    val premiumConfig: StateFlow<PremiumConfig> = _premiumConfig.asStateFlow()

    private val _premiumPlans = MutableStateFlow<List<PremiumPlan>>(getInitialPremiumPlans())
    val premiumPlans: StateFlow<List<PremiumPlan>> = _premiumPlans.asStateFlow()

    private val _premiumHistory = MutableStateFlow<List<PremiumMembershipHistory>>(getInitialPremiumHistory())
    val premiumHistory: StateFlow<List<PremiumMembershipHistory>> = _premiumHistory.asStateFlow()

    private val _proposalTemplates = MutableStateFlow<List<ProposalTemplate>>(getInitialProposalTemplates())
    val proposalTemplates: StateFlow<List<ProposalTemplate>> = _proposalTemplates.asStateFlow()

    // --- Central Admin System State ---
    private val _centralReports = MutableStateFlow<List<CentralReport>>(getInitialCentralReports())
    val centralReports: StateFlow<List<CentralReport>> = _centralReports.asStateFlow()

    private val _adminActivityLogs = MutableStateFlow<List<AdminActivityLog>>(getInitialAdminLogs())
    val adminActivityLogs: StateFlow<List<AdminActivityLog>> = _adminActivityLogs.asStateFlow()

    private val _broadcastNotifications = MutableStateFlow<List<BroadcastNotification>>(getInitialBroadcastNotifications())
    val broadcastNotifications: StateFlow<List<BroadcastNotification>> = _broadcastNotifications.asStateFlow()

    // --- Authentication Actions ---
    fun login(email: String, pass: String): Result<UserProfile> {
        if (email.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Please enter both email and password."))
        }
        val user = _allUsers.value.find { it.email.equals(email.trim(), ignoreCase = true) }
            ?: UserProfile(
                uid = "user_" + System.currentTimeMillis() % 100000,
                username = email.substringBefore("@").lowercase(),
                fullName = email.substringBefore("@").replace(".", " ").capitalizeWords(),
                email = email.trim(),
                phone = "+91 91234 56789",
                referralCode = "EARN" + (1000..9999).random(),
                availableBalance = 100.0,
                totalEarned = 100.0,
                isAdmin = email.contains("admin")
            )
        _currentUser.value = user
        return Result.success(user)
    }

    fun signUp(fullName: String, email: String, pass: String, phone: String, referralCodeInput: String?): Result<UserProfile> {
        if (fullName.isBlank() || email.isBlank() || pass.isBlank() || phone.isBlank()) {
            return Result.failure(Exception("All fields are required."))
        }
        val newCode = "EARN" + (1000..9999).random()
        var bonus = 0.0
        var refAppliedCode: String? = null

        // Validate Referral Code if provided
        if (!referralCodeInput.isNullOrBlank()) {
            val refCodeClean = referralCodeInput.trim().uppercase()
            if (refCodeClean == newCode) {
                return Result.failure(Exception("You cannot use your own referral code."))
            }
            bonus = _appConfig.value.referralRewardRupees
            refAppliedCode = refCodeClean
        }

        val newUser = UserProfile(
            uid = "user_" + System.currentTimeMillis() % 100000,
            username = fullName.lowercase().replace(" ", "_"),
            fullName = fullName.trim(),
            email = email.trim(),
            phone = phone.trim(),
            referralCode = newCode,
            referredByCode = refAppliedCode,
            availableBalance = bonus,
            totalEarned = bonus,
            createdAt = System.currentTimeMillis()
        )

        _currentUser.value = newUser
        _allUsers.value = listOf(newUser) + _allUsers.value

        if (bonus > 0.0) {
            addTransaction(
                userId = newUser.uid,
                type = TransactionType.REFERRAL_REWARD,
                amount = bonus,
                description = "Welcome Referral Bonus (Applied $refAppliedCode)"
            )
            addNotification("Welcome Bonus Received!", "₹$bonus was credited to your wallet for signing up with a referral code.")
        }

        return Result.success(newUser)
    }

    fun logout() {
        _currentUser.value = null
    }

    fun deleteAccount(): Result<Boolean> {
        val user = _currentUser.value ?: return Result.failure(Exception("No user logged in."))
        _allUsers.value = _allUsers.value.filter { it.uid != user.uid }
        _currentUser.value = null
        return Result.success(true)
    }

    fun updateProfile(fullName: String, phone: String, username: String, language: String): Result<Boolean> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in."))
        val updated = user.copy(
            fullName = fullName.ifBlank { user.fullName },
            phone = phone.ifBlank { user.phone },
            username = username.ifBlank { user.username },
            language = language
        )
        _currentUser.value = updated
        _allUsers.value = _allUsers.value.map { if (it.uid == user.uid) updated else it }
        return Result.success(true)
    }

    // --- Task Actions ---
    fun submitTaskProof(taskId: String, proofContent: String): Result<Boolean> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please log in to submit tasks."))
        val task = _tasks.value.find { it.id == taskId } ?: return Result.failure(Exception("Task not found."))

        if (proofContent.isBlank()) {
            return Result.failure(Exception("Please provide proof details before submitting."))
        }

        // Check if user already submitted
        val existing = _submissions.value.find { it.taskId == taskId && it.userId == user.uid && it.status == SubmissionStatus.PENDING }
        if (existing != null) {
            return Result.failure(Exception("You have an active submission under review for this task."))
        }

        val submission = TaskSubmission(
            id = "sub_" + System.currentTimeMillis() % 100000,
            taskId = taskId,
            taskTitle = task.title,
            userId = user.uid,
            userName = user.fullName,
            userEmail = user.email,
            submittedAt = System.currentTimeMillis(),
            proofContent = proofContent.trim(),
            rewardRupees = task.rewardRupees,
            status = SubmissionStatus.PENDING
        )

        _submissions.value = listOf(submission) + _submissions.value

        // Update task state locally for this user
        _tasks.value = _tasks.value.map { if (it.id == taskId) it.copy(status = TaskStatus.UNDER_REVIEW) else it }

        // Update user pending balance
        val updatedUser = user.copy(pendingRewards = user.pendingRewards + task.rewardRupees)
        _currentUser.value = updatedUser

        addNotification("Task Submitted!", "Your submission for '${task.title}' is now under admin review.")

        return Result.success(true)
    }

    // --- Daily Reward Check-in ---
    fun claimDailyCheckIn(): Result<Double> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not logged in."))
        val todayStr = getTodayDateString()

        if (user.lastCheckInDate == todayStr) {
            return Result.failure(Exception("You have already claimed your daily check-in reward today. Come back tomorrow!"))
        }

        val yesterdayStr = getYesterdayDateString()
        val newStreak = if (user.lastCheckInDate == yesterdayStr) user.currentStreak + 1 else 1

        // Base reward increases with streak: Day 1: ₹2, Day 2: ₹3, Day 3: ₹4... Day 7: ₹15
        val streakMultiplier = (newStreak % 7).let { if (it == 0) 7 else it }
        val rewardAmount = _appConfig.value.dailyBonusBaseRupees + (streakMultiplier - 1) * 2.0

        val updatedUser = user.copy(
            availableBalance = user.availableBalance + rewardAmount,
            totalEarned = user.totalEarned + rewardAmount,
            currentStreak = newStreak,
            lastCheckInDate = todayStr
        )
        _currentUser.value = updatedUser

        addTransaction(
            userId = user.uid,
            type = TransactionType.DAILY_BONUS,
            amount = rewardAmount,
            description = "Day $streakMultiplier Streak Daily Reward"
        )

        addNotification("Daily Bonus Claimed!", "Streak Day $streakMultiplier: ₹$rewardAmount credited to your wallet.")

        return Result.success(rewardAmount)
    }

    // --- Rewarded Ad Bonus ---
    fun claimRewardedAdBonus(rewardAmount: Double = 5.0, rewardType: String = "Ad Reward"): Result<Double> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not logged in."))
        if (rewardAmount <= 0) return Result.failure(Exception("Invalid reward amount."))

        val updatedUser = user.copy(
            availableBalance = user.availableBalance + rewardAmount,
            totalEarned = user.totalEarned + rewardAmount
        )
        _currentUser.value = updatedUser

        addTransaction(
            userId = user.uid,
            type = TransactionType.DAILY_BONUS,
            amount = rewardAmount,
            description = "AdMob Rewarded Video ($rewardType)"
        )

        addNotification("Rewarded Ad Completed! 🎁", "+₹${"%.2f".format(rewardAmount)} credited to your wallet.")
        return Result.success(rewardAmount)
    }

    // --- Games & Rewards Logic (Server-side validation simulation) ---

    fun getTodayGamePlayCount(type: GameType): Int {
        val userId = _currentUser.value?.uid ?: return 0
        val todayStart = getTodayStartTimestamp()
        return _gamePlays.value.count { it.userId == userId && it.gameType == type && it.playedAt >= todayStart }
    }

    fun playSpinWheel(): Result<Double> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not logged in."))
        val config = _gameConfigs.value.find { it.gameType == GameType.SPIN_WHEEL }
            ?: GameConfigItem(GameType.SPIN_WHEEL)

        if (!config.isEnabled) {
            return Result.failure(Exception("Spin & Win is currently under maintenance."))
        }

        val playsToday = getTodayGamePlayCount(GameType.SPIN_WHEEL)
        if (playsToday >= config.maxDailyPlays) {
            return Result.failure(Exception("You have reached your daily limit of ${config.maxDailyPlays} spins! Come back tomorrow."))
        }

        // Server-side weighted outcome calculation
        val possibleRewards = listOf(0.5, 1.0, 1.5, 2.0, 3.0, 5.0, 0.0, 10.0)
        val rawReward = possibleRewards.random()
        val rewardAmount = rawReward.coerceIn(config.minRewardRupees, config.maxRewardRupees)

        // Credit User
        if (rewardAmount > 0) {
            _currentUser.value = user.copy(
                availableBalance = user.availableBalance + rewardAmount,
                totalEarned = user.totalEarned + rewardAmount
            )
            addTransaction(
                userId = user.uid,
                type = TransactionType.GAME_REWARD,
                amount = rewardAmount,
                description = "Spin & Win Daily Game Reward"
            )
        }

        // Record play history
        val playRecord = GamePlayRecord(
            id = "game_" + System.currentTimeMillis(),
            userId = user.uid,
            gameType = GameType.SPIN_WHEEL,
            playedAt = System.currentTimeMillis(),
            rewardAmount = rewardAmount,
            details = "Wheel landed on ₹$rewardAmount"
        )
        _gamePlays.value = listOf(playRecord) + _gamePlays.value

        return Result.success(rewardAmount)
    }

    fun playScratchCard(): Result<Double> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not logged in."))
        val config = _gameConfigs.value.find { it.gameType == GameType.SCRATCH_CARD }
            ?: GameConfigItem(GameType.SCRATCH_CARD)

        if (!config.isEnabled) {
            return Result.failure(Exception("Scratch & Earn is currently disabled."))
        }

        val playsToday = getTodayGamePlayCount(GameType.SCRATCH_CARD)
        if (playsToday >= config.maxDailyPlays) {
            return Result.failure(Exception("Daily limit reached (${config.maxDailyPlays}/${config.maxDailyPlays}). Try again tomorrow!"))
        }

        // Calculate random reward within bounds
        val rewardAmount = (config.minRewardRupees + Math.random() * (config.maxRewardRupees - config.minRewardRupees))
            .let { (it * 2.0).toInt() / 2.0 } // round to nearest 0.50

        if (rewardAmount > 0) {
            _currentUser.value = user.copy(
                availableBalance = user.availableBalance + rewardAmount,
                totalEarned = user.totalEarned + rewardAmount
            )
            addTransaction(
                userId = user.uid,
                type = TransactionType.GAME_REWARD,
                amount = rewardAmount,
                description = "Scratch Card Reward"
            )
        }

        val playRecord = GamePlayRecord(
            id = "game_" + System.currentTimeMillis(),
            userId = user.uid,
            gameType = GameType.SCRATCH_CARD,
            playedAt = System.currentTimeMillis(),
            rewardAmount = rewardAmount,
            details = "Scratched card revealed ₹$rewardAmount"
        )
        _gamePlays.value = listOf(playRecord) + _gamePlays.value

        return Result.success(rewardAmount)
    }

    fun submitQuizResult(correctCount: Int, totalQuestions: Int, timeTakenSeconds: Int): Result<QuizResult> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not logged in."))
        val config = _gameConfigs.value.find { it.gameType == GameType.DAILY_QUIZ }
            ?: GameConfigItem(GameType.DAILY_QUIZ)

        if (!config.isEnabled) {
            return Result.failure(Exception("Daily Quiz is currently unavailable."))
        }

        val playsToday = getTodayGamePlayCount(GameType.DAILY_QUIZ)
        if (playsToday >= config.maxDailyPlays) {
            return Result.failure(Exception("You have already completed your daily trivia quiz limit!"))
        }

        val scorePercentage = (correctCount * 100) / totalQuestions
        val baseReward = correctCount * 1.0 // ₹1 per correct answer
        val rewardAmount = baseReward.coerceIn(config.minRewardRupees, config.maxRewardRupees)

        if (rewardAmount > 0) {
            _currentUser.value = user.copy(
                availableBalance = user.availableBalance + rewardAmount,
                totalEarned = user.totalEarned + rewardAmount
            )
            addTransaction(
                userId = user.uid,
                type = TransactionType.GAME_REWARD,
                amount = rewardAmount,
                description = "Daily Quiz ($correctCount/$totalQuestions correct) Reward"
            )
        }

        val playRecord = GamePlayRecord(
            id = "game_" + System.currentTimeMillis(),
            userId = user.uid,
            gameType = GameType.DAILY_QUIZ,
            playedAt = System.currentTimeMillis(),
            rewardAmount = rewardAmount,
            details = "Trivia Score: $correctCount/$totalQuestions in ${timeTakenSeconds}s"
        )
        _gamePlays.value = listOf(playRecord) + _gamePlays.value

        val result = QuizResult(
            totalQuestions = totalQuestions,
            correctAnswers = correctCount,
            scorePercentage = scorePercentage,
            rewardEarned = rewardAmount,
            timeTakenSeconds = timeTakenSeconds
        )

        return Result.success(result)
    }

    fun playMemoryMatch(timeTakenSeconds: Int, movesCount: Int): Result<Double> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not logged in."))
        val config = _gameConfigs.value.find { it.gameType == GameType.MEMORY_MATCH }
            ?: GameConfigItem(GameType.MEMORY_MATCH)

        if (!config.isEnabled) {
            return Result.failure(Exception("Memory Flip is currently disabled."))
        }

        val playsToday = getTodayGamePlayCount(GameType.MEMORY_MATCH)
        if (playsToday >= config.maxDailyPlays) {
            return Result.failure(Exception("Daily Memory Flip limit reached!"))
        }

        val rewardAmount = if (timeTakenSeconds < 45) 3.0 else 2.0
        val finalReward = rewardAmount.coerceIn(config.minRewardRupees, config.maxRewardRupees)

        _currentUser.value = user.copy(
            availableBalance = user.availableBalance + finalReward,
            totalEarned = user.totalEarned + finalReward
        )
        addTransaction(
            userId = user.uid,
            type = TransactionType.GAME_REWARD,
            amount = finalReward,
            description = "Memory Match Completion Reward"
        )

        val playRecord = GamePlayRecord(
            id = "game_" + System.currentTimeMillis(),
            userId = user.uid,
            gameType = GameType.MEMORY_MATCH,
            playedAt = System.currentTimeMillis(),
            rewardAmount = finalReward,
            details = "Completed memory grid in ${timeTakenSeconds}s ($movesCount moves)"
        )
        _gamePlays.value = listOf(playRecord) + _gamePlays.value

        return Result.success(finalReward)
    }

    fun claimLuckyDrawTicket(poolId: String): Result<LuckyDrawTicket> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not logged in."))
        val pool = _luckyDrawPools.value.find { it.id == poolId }
            ?: return Result.failure(Exception("Lucky Draw pool not found."))

        val existing = _userLuckyDrawTickets.value.find { it.userId == user.uid }
        if (existing != null) {
            return Result.failure(Exception("You have already claimed your ticket (#${existing.ticketNumber}) for today's draw!"))
        }

        val ticketNumber = (10000..99999).random()
        val ticket = LuckyDrawTicket(
            ticketId = "tkt_" + System.currentTimeMillis(),
            userId = user.uid,
            userName = user.fullName,
            ticketNumber = ticketNumber,
            drawnAt = System.currentTimeMillis()
        )

        _userLuckyDrawTickets.value = _userLuckyDrawTickets.value + ticket
        _luckyDrawPools.value = _luckyDrawPools.value.map {
            if (it.id == poolId) it.copy(totalEntriesCount = it.totalEntriesCount + 1) else it
        }

        val playRecord = GamePlayRecord(
            id = "game_" + System.currentTimeMillis(),
            userId = user.uid,
            gameType = GameType.LUCKY_DRAW,
            playedAt = System.currentTimeMillis(),
            rewardAmount = 0.0,
            details = "Claimed Lucky Draw Entry Ticket #$ticketNumber"
        )
        _gamePlays.value = listOf(playRecord) + _gamePlays.value

        return Result.success(ticket)
    }

    fun adminUpdateGameConfig(type: GameType, isEnabled: Boolean, maxDailyPlays: Int, minReward: Double, maxReward: Double): Result<Unit> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Access Denied: Admin privileges required."))
        _gameConfigs.value = _gameConfigs.value.map {
            if (it.gameType == type) {
                it.copy(
                    isEnabled = isEnabled,
                    maxDailyPlays = maxDailyPlays,
                    minRewardRupees = minReward,
                    maxRewardRupees = maxReward
                )
            } else it
        }
        return Result.success(Unit)
    }

    fun getSampleQuizQuestions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "q1",
            question = "Which Indian digital payment system is operated by NPCI?",
            options = listOf("UPI", "SWIFT", "SEPA", "FedNow"),
            correctIndex = 0,
            explanation = "Unified Payments Interface (UPI) is developed by NPCI in India.",
            category = "Financial Literacy"
        ),
        QuizQuestion(
            id = "q2",
            question = "What does 'FSSAI' certification signify on packaged food items in India?",
            options = listOf("Import Duty Paid", "Food Safety Standards", "Tax Exemption", "Export Quality"),
            correctIndex = 1,
            explanation = "FSSAI ensures food safety and quality standards across India.",
            category = "General Knowledge"
        ),
        QuizQuestion(
            id = "q3",
            question = "Which river is widely known as the 'Sorrow of Bengal' before flood mitigation dams were built?",
            options = listOf("Ganga", "Damodar River", "Brahmaputra", "Yamuna"),
            correctIndex = 1,
            explanation = "Damodar River was formerly known as the Sorrow of Bengal due to frequent floods.",
            category = "Indian Geography"
        ),
        QuizQuestion(
            id = "q4",
            question = "In mobile computing, what does 5G Standalone (SA) architecture mean?",
            options = listOf("Uses 4G LTE Core", "Independent 5G Next-Gen Core", "Satellite only", "Wi-Fi dependent"),
            correctIndex = 1,
            explanation = "5G SA operates on a dedicated 5G core network without requiring 4G LTE signaling.",
            category = "Technology"
        ),
        QuizQuestion(
            id = "q5",
            question = "Which scheme by Government of India promotes digital financial inclusion for zero-balance savings accounts?",
            options = listOf("PM Jan Dhan Yojana", "Make in India", "PM Awas Yojana", "Startup India"),
            correctIndex = 0,
            explanation = "PM Jan Dhan Yojana provides basic banking accounts with debit cards and insurance.",
            category = "Government Schemes"
        )
    )

    private fun getTodayStartTimestamp(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // --- Wallet Withdrawal ---
    suspend fun requestWithdrawal(method: WithdrawalMethod, amount: Double, payoutDetails: String): Result<WithdrawalRequest> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not logged in."))
        val minLimit = _appConfig.value.minimumWithdrawalRupees

        if (amount < minLimit) {
            return Result.failure(Exception("Minimum withdrawal amount is ₹${minLimit.toInt()}."))
        }
        if (amount > user.availableBalance) {
            return Result.failure(Exception("Insufficient available balance. You have ₹${user.availableBalance}."))
        }
        if (payoutDetails.isBlank()) {
            return Result.failure(Exception("Please enter valid payout details (UPI ID or Bank Account info)."))
        }

        val fee = amount * (_appConfig.value.withdrawalFeePercentage / 100.0)
        val netPayout = amount - fee

        val request = WithdrawalRequest(
            id = "WD-" + System.currentTimeMillis() % 1000000,
            userId = user.uid,
            userName = user.fullName,
            userEmail = user.email,
            method = method,
            amountRupees = netPayout,
            feeRupees = fee,
            payoutDetails = payoutDetails.trim(),
            status = WithdrawalStatus.REQUESTED,
            requestedAt = System.currentTimeMillis()
        )

        // Initiate Payout Provider Validation
        val payoutResult = paymentProvider.initiatePayout(request)
        val finalStatus = when (payoutResult) {
            is PaymentResult.Success -> WithdrawalStatus.APPROVED
            is PaymentResult.Pending -> WithdrawalStatus.PROCESSING
            is PaymentResult.Failure -> WithdrawalStatus.REQUESTED
        }

        val updatedRequest = request.copy(
            status = finalStatus,
            transactionReference = if (payoutResult is PaymentResult.Pending) payoutResult.referenceId else null
        )

        // Deduct available balance
        val updatedUser = user.copy(availableBalance = user.availableBalance - amount)
        _currentUser.value = updatedUser

        _withdrawals.value = listOf(updatedRequest) + _withdrawals.value

        addTransaction(
            userId = user.uid,
            type = TransactionType.WITHDRAWAL,
            amount = -amount,
            status = TransactionStatus.PENDING,
            description = "Withdrawal request via ${method.label} to $payoutDetails"
        )

        addNotification("Withdrawal Requested", "₹$amount requested via ${method.label}. Status: ${finalStatus.label}.")

        return Result.success(updatedRequest)
    }

    // --- Support Ticket ---
    fun submitSupportTicket(subject: String, category: String, message: String): Result<SupportTicket> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not logged in."))
        if (subject.isBlank() || message.isBlank()) {
            return Result.failure(Exception("Please fill out subject and message."))
        }
        val ticket = SupportTicket(
            id = "TICK-" + System.currentTimeMillis() % 10000,
            userId = user.uid,
            userName = user.fullName,
            userEmail = user.email,
            subject = subject.trim(),
            category = category,
            message = message.trim(),
            status = TicketStatus.OPEN,
            createdAt = System.currentTimeMillis()
        )
        _supportTickets.value = listOf(ticket) + _supportTickets.value
        addNotification("Ticket Created", "Support ticket '${ticket.id}' submitted. Our team will respond shortly.")
        return Result.success(ticket)
    }

    // --- Admin Actions ---
    fun adminApproveSubmission(submissionId: String, adminNote: String?): Result<Boolean> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Access Denied: Admin privileges required."))
        val sub = _submissions.value.find { it.id == submissionId } ?: return Result.failure(Exception("Submission not found."))
        if (sub.status != SubmissionStatus.PENDING) {
            return Result.failure(Exception("Submission is already processed."))
        }

        val updatedSub = sub.copy(status = SubmissionStatus.APPROVED, adminNote = adminNote)
        _submissions.value = _submissions.value.map { if (it.id == submissionId) updatedSub else it }

        // Credit User Rewards
        val targetUser = _allUsers.value.find { it.uid == sub.userId }
        if (targetUser != null) {
            val updatedTarget = targetUser.copy(
                availableBalance = targetUser.availableBalance + sub.rewardRupees,
                totalEarned = targetUser.totalEarned + sub.rewardRupees,
                pendingRewards = (targetUser.pendingRewards - sub.rewardRupees).coerceAtLeast(0.0),
                completedTasksCount = targetUser.completedTasksCount + 1
            )
            _allUsers.value = _allUsers.value.map { if (it.uid == sub.userId) updatedTarget else it }

            if (_currentUser.value?.uid == sub.userId) {
                _currentUser.value = updatedTarget
            }

            addTransaction(
                userId = sub.userId,
                type = TransactionType.TASK_REWARD,
                amount = sub.rewardRupees,
                description = "Task Reward Approved: ${sub.taskTitle}"
            )
        }

        addNotification("Task Approved! 🎉", "Your submission for '${sub.taskTitle}' was approved. ₹${sub.rewardRupees} added to wallet.")

        return Result.success(true)
    }

    fun adminRejectSubmission(submissionId: String, adminNote: String): Result<Boolean> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Access Denied: Admin privileges required."))
        val sub = _submissions.value.find { it.id == submissionId } ?: return Result.failure(Exception("Submission not found."))
        val updatedSub = sub.copy(status = SubmissionStatus.REJECTED, adminNote = adminNote.ifBlank { "Proof verification failed." })
        _submissions.value = _submissions.value.map { if (it.id == submissionId) updatedSub else it }

        val targetUser = _allUsers.value.find { it.uid == sub.userId }
        if (targetUser != null) {
            val updatedTarget = targetUser.copy(
                pendingRewards = (targetUser.pendingRewards - sub.rewardRupees).coerceAtLeast(0.0)
            )
            _allUsers.value = _allUsers.value.map { if (it.uid == sub.userId) updatedTarget else it }
            if (_currentUser.value?.uid == sub.userId) {
                _currentUser.value = updatedTarget
            }
        }

        addNotification("Task Submission Rejected", "Submission for '${sub.taskTitle}' rejected. Reason: ${adminNote.ifBlank { "Proof verification failed" }}.")

        return Result.success(true)
    }

    fun adminProcessWithdrawal(withdrawalId: String, newStatus: WithdrawalStatus, txRef: String?, rejectionReason: String?): Result<Boolean> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Access Denied: Admin privileges required."))
        val req = _withdrawals.value.find { it.id == withdrawalId } ?: return Result.failure(Exception("Withdrawal request not found."))
        val updatedReq = req.copy(
            status = newStatus,
            processedAt = System.currentTimeMillis(),
            transactionReference = txRef ?: req.transactionReference,
            rejectionReason = rejectionReason
        )
        _withdrawals.value = _withdrawals.value.map { if (it.id == withdrawalId) updatedReq else it }

        // If rejected/cancelled, refund balance back to user
        if (newStatus == WithdrawalStatus.REJECTED || newStatus == WithdrawalStatus.CANCELLED) {
            val targetUser = _allUsers.value.find { it.uid == req.userId }
            if (targetUser != null) {
                val refundedAmount = req.amountRupees + req.feeRupees
                val updatedTarget = targetUser.copy(availableBalance = targetUser.availableBalance + refundedAmount)
                _allUsers.value = _allUsers.value.map { if (it.uid == req.userId) updatedTarget else it }
                if (_currentUser.value?.uid == req.userId) {
                    _currentUser.value = updatedTarget
                }
                addTransaction(
                    userId = req.userId,
                    type = TransactionType.ADJUSTMENT,
                    amount = refundedAmount,
                    description = "Refunded rejected withdrawal ${req.id}"
                )
            }
        }

        addNotification("Withdrawal Status Update", "Your withdrawal request ${req.id} is now ${newStatus.label}.")

        return Result.success(true)
    }

    fun adminCreateTask(title: String, desc: String, category: TaskCategory, reward: Double, mins: Int, difficulty: TaskDifficulty, proofType: ProofType, instructions: List<String>): Result<TaskItem> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Access Denied: Admin privileges required."))
        if (title.isBlank() || desc.isBlank() || reward <= 0) {
            return Result.failure(Exception("Invalid task parameters."))
        }
        val newTask = TaskItem(
            id = "task_" + System.currentTimeMillis() % 100000,
            title = title.trim(),
            description = desc.trim(),
            category = category,
            rewardRupees = reward,
            estimatedMinutes = mins,
            difficulty = difficulty,
            requiredProofType = proofType,
            instructions = instructions.filter { it.isNotBlank() },
            isFeatured = true
        )
        _tasks.value = listOf(newTask) + _tasks.value
        return Result.success(newTask)
    }

    fun adminUpdateConfig(minWithdrawal: Double, referralReward: Double, dailyBonus: Double, maintenanceMode: Boolean, announcement: String, adsEnabled: Boolean): Result<Boolean> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Access Denied: Admin privileges required."))
        _appConfig.value = AppConfig(
            minimumWithdrawalRupees = minWithdrawal,
            referralRewardRupees = referralReward,
            dailyBonusBaseRupees = dailyBonus,
            maintenanceMode = maintenanceMode,
            appAnnouncement = announcement,
            adsEnabled = adsEnabled
        )
        return Result.success(true)
    }

    fun toggleLeaderboardVisibility(hide: Boolean) {
        val user = _currentUser.value ?: return
        _currentUser.value = user.copy(hideFromLeaderboard = hide)
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map { if (it.id == id) it.copy(isRead = true) else it }
    }

    // --- Helpers ---
    private fun addTransaction(userId: String, type: TransactionType, amount: Double, status: TransactionStatus = TransactionStatus.COMPLETED, description: String) {
        val tx = WalletTransaction(
            id = "TX-" + System.currentTimeMillis() % 10000000,
            userId = userId,
            type = type,
            amount = amount,
            status = status,
            timestamp = System.currentTimeMillis(),
            description = description
        )
        _transactions.value = listOf(tx) + _transactions.value
    }

    private fun addNotification(title: String, message: String) {
        val notif = NotificationItem(
            id = "NOTIF-" + System.currentTimeMillis() % 100000,
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    private fun String?.isNull_Or_Blank(): Boolean = this == null || this.trim().isEmpty()

    private fun getTodayDateString(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun getYesterdayDateString(): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DATE, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

    // --- Seed Data Suppliers ---
    private fun getInitialTasks(): List<TaskItem> = listOf(
        TaskItem(
            id = "task_101",
            title = "India Digital Banking Survey 2026",
            description = "Share your opinion on digital UPI payments, Neobanking & savings apps in India.",
            category = TaskCategory.SURVEY,
            rewardRupees = 35.0,
            estimatedMinutes = 5,
            difficulty = TaskDifficulty.EASY,
            eligibility = "All Indian residents above 18 years",
            instructions = listOf(
                "Click Start Survey and answer all 8 questions genuinely.",
                "Copy the completion code shown on the final page.",
                "Paste the code into the submission box below."
            ),
            requiredProofType = ProofType.CODE_VERIFICATION,
            isFeatured = true
        ),
        TaskItem(
            id = "task_102",
            title = "Test AI Financial Coach Mobile App",
            description = "Download and test the new beta budget planner app. Create 1 expense entry.",
            category = TaskCategory.APP_TESTING,
            rewardRupees = 50.0,
            estimatedMinutes = 8,
            difficulty = TaskDifficulty.MEDIUM,
            eligibility = "Android users with active Google Play",
            instructions = listOf(
                "Install app from provided link.",
                "Open app and add 1 dummy daily expense item.",
                "Take a screenshot of the dashboard and upload or paste image link."
            ),
            requiredProofType = ProofType.SCREENSHOT_URL,
            isFeatured = true
        ),
        TaskItem(
            id = "task_103",
            title = "Skill Quiz: UPI Safety & Anti-Fraud Awareness",
            description = "Learn how to detect fake QR codes and phishing SMS to protect your money.",
            category = TaskCategory.LEARNING,
            rewardRupees = 25.0,
            estimatedMinutes = 4,
            difficulty = TaskDifficulty.EASY,
            instructions = listOf(
                "Read the 3-minute UPI Fraud Prevention guide.",
                "Answer 3 quick quiz questions correctly.",
                "Submit your quiz confirmation summary."
            ),
            requiredProofType = ProofType.TEXT_INPUT,
            isFeatured = false
        ),
        TaskItem(
            id = "task_104",
            title = "Categorize Indian E-Commerce Product Names",
            description = "Annotate 10 product items as Electronics, Clothing, or Grocery for AI training.",
            category = TaskCategory.DATA_ENTRY,
            rewardRupees = 40.0,
            estimatedMinutes = 6,
            difficulty = TaskDifficulty.EASY,
            instructions = listOf(
                "Open data task sheet.",
                "Select correct category dropdown for 10 product titles.",
                "Submit completion ID."
            ),
            requiredProofType = ProofType.TEXT_INPUT,
            isFeatured = false
        ),
        TaskItem(
            id = "task_105",
            title = "Evaluate Gemini AI Vernacular Translations",
            description = "Review 5 English-to-Hindi AI sentence translations and rate accuracy.",
            category = TaskCategory.AI_TASK,
            rewardRupees = 60.0,
            estimatedMinutes = 10,
            difficulty = TaskDifficulty.HARD,
            instructions = listOf(
                "Read English prompt and Hindi AI output.",
                "Mark any grammar or context errors.",
                "Submit completed feedback form link."
            ),
            requiredProofType = ProofType.LINK_SUBMISSION,
            isFeatured = true
        )
    )

    private fun getInitialOffers(): List<OfferItem> = listOf(
        OfferItem(
            id = "off_201",
            title = "Sign up for Free Demat & Stock Learning",
            description = "Register a free zero-annual-fee account with SEBI registered partner.",
            providerName = "FinPartner India",
            rewardRupees = 120.0,
            estimatedMinutes = 10,
            termsAndConditions = "Must complete KYC verification with valid PAN card. No deposit required.",
            requirements = listOf("Valid PAN Card", "Aadhaar linked Mobile number"),
            badgeText = "Highest Reward"
        ),
        OfferItem(
            id = "off_202",
            title = "Try Vernacular News App for 3 Days",
            description = "Install app, read 2 news articles daily for 3 consecutive days.",
            providerName = "KhabarNow",
            rewardRupees = 45.0,
            estimatedMinutes = 5,
            termsAndConditions = "New users only. Must enable push notifications.",
            requirements = listOf("New install", "3-day retention check"),
            badgeText = "Popular"
        )
    )

    private fun getInitialTransactions(): List<WalletTransaction> = listOf(
        WalletTransaction(
            id = "TX-892120",
            userId = "user_demo_001",
            type = TransactionType.DAILY_BONUS,
            amount = 10.0,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis() - 24 * 3600 * 1000L,
            description = "Day 4 Streak Reward"
        ),
        WalletTransaction(
            id = "TX-892119",
            userId = "user_demo_001",
            type = TransactionType.TASK_REWARD,
            amount = 35.0,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis() - 48 * 3600 * 1000L,
            description = "Completed: UPI Security Survey"
        ),
        WalletTransaction(
            id = "TX-892100",
            userId = "user_demo_001",
            type = TransactionType.REFERRAL_REWARD,
            amount = 25.0,
            status = TransactionStatus.COMPLETED,
            timestamp = System.currentTimeMillis() - 72 * 3600 * 1000L,
            description = "Referral Bonus for @priya_m"
        )
    )

    private fun getInitialSubmissions(): List<TaskSubmission> = listOf(
        TaskSubmission(
            id = "sub_901",
            taskId = "task_101",
            taskTitle = "India Digital Banking Survey 2026",
            userId = "user_demo_001",
            userName = "Rahul Sharma",
            userEmail = "rahul.sharma@example.in",
            submittedAt = System.currentTimeMillis() - 3600 * 1000L,
            proofContent = "COMPLETION-CODE-INDIA-88219",
            rewardRupees = 35.0,
            status = SubmissionStatus.PENDING
        )
    )

    private fun getInitialWithdrawals(): List<WithdrawalRequest> = listOf(
        WithdrawalRequest(
            id = "WD-100482",
            userId = "user_demo_001",
            userName = "Rahul Sharma",
            userEmail = "rahul.sharma@example.in",
            method = WithdrawalMethod.UPI,
            amountRupees = 200.0,
            feeRupees = 0.0,
            payoutDetails = "rahul.sharma@upi",
            status = WithdrawalStatus.APPROVED,
            requestedAt = System.currentTimeMillis() - 5 * 24 * 3600 * 1000L,
            processedAt = System.currentTimeMillis() - 4 * 24 * 3600 * 1000L,
            transactionReference = "BANK-UTR-99821800"
        )
    )

    private fun getInitialNotifications(): List<NotificationItem> = listOf(
        NotificationItem(
            id = "notif_1",
            title = "Welcome to EarnMate India! 🇮🇳",
            message = "Complete verified tasks, surveys & learn skills to earn real rewards via UPI. Always 100% free with zero deposits.",
            timestamp = System.currentTimeMillis() - 3600 * 1000L,
            isRead = false
        ),
        NotificationItem(
            id = "notif_2",
            title = "Daily Check-in Available",
            message = "Your Day 5 daily streak bonus is waiting! Tap to claim your free reward.",
            timestamp = System.currentTimeMillis() - 2 * 3600 * 1000L,
            isRead = false
        )
    )

    private fun getInitialTickets(): List<SupportTicket> = listOf(
        SupportTicket(
            id = "TICK-4021",
            userId = "user_demo_001",
            userName = "Rahul Sharma",
            userEmail = "rahul.sharma@example.in",
            subject = "Withdrawal UTR receipt clarification",
            category = "Withdrawals",
            message = "Received UPI notification for WD-100482. Thank you for prompt 24hr credit!",
            status = TicketStatus.RESOLVED,
            createdAt = System.currentTimeMillis() - 3 * 24 * 3600 * 1000L,
            adminReply = "Happy to assist! Bank UTR reference is updated in your ledger."
        )
    )

    private fun getInitialReferrals(): List<ReferralUserSummary> = listOf(
        ReferralUserSummary("u_201", "priya_m", System.currentTimeMillis() - 3 * 24 * 3600 * 1000L, "Active", 25.0),
        ReferralUserSummary("u_202", "amit_v", System.currentTimeMillis() - 5 * 24 * 3600 * 1000L, "Active", 25.0),
        ReferralUserSummary("u_203", "sunita_k", System.currentTimeMillis() - 10 * 24 * 3600 * 1000L, "Active", 25.0)
    )

    private fun getInitialAllUsers(): List<UserProfile> = listOf(
        UserProfile("user_demo_001", "rahul_rewards", "Rahul Sharma", "rahul.sharma@example.in", "+91 98765 43210", "", System.currentTimeMillis(), "EARN9823", null, 485.0, 240.0, 45.0, 12, 4, getYesterdayDateString(), false),
        UserProfile("admin_user_001", "admin_earnmate", "Admin Manager", "admin@earnmate.in", "+91 99999 88888", "", System.currentTimeMillis(), "EARN9999", null, 0.0, 0.0, 0.0, 0, 0, getTodayDateString(), true),
        UserProfile("u_201", "priya_m", "Priya Mehta", "priya.m@example.in", "+91 98111 22233", "", System.currentTimeMillis() - 3 * 24 * 3600 * 1000L, "EARN1122", "EARN9823", 210.0, 150.0, 20.0, 8, 2, getYesterdayDateString(), false),
        UserProfile("u_202", "amit_v", "Amit Verma", "amit.v@example.in", "+91 98222 33344", "", System.currentTimeMillis() - 5 * 24 * 3600 * 1000L, "EARN3344", "EARN9823", 320.0, 280.0, 0.0, 15, 6, getTodayDateString(), false),
        UserProfile("u_203", "sunita_k", "Sunita Kumar", "sunita.k@example.in", "+91 98333 44455", "", System.currentTimeMillis() - 10 * 24 * 3600 * 1000L, "EARN5566", "EARN9823", 180.0, 80.0, 0.0, 6, 1, getYesterdayDateString(), false)
    )

    private fun getInitialGameConfigs(): List<GameConfigItem> = listOf(
        GameConfigItem(GameType.SPIN_WHEEL, isEnabled = true, maxDailyPlays = 5, minRewardRupees = 0.5, maxRewardRupees = 10.0, subtitle = "Spin the wheel for random daily rewards"),
        GameConfigItem(GameType.SCRATCH_CARD, isEnabled = true, maxDailyPlays = 5, minRewardRupees = 0.5, maxRewardRupees = 10.0, subtitle = "Scratch to reveal surprise wallet rewards"),
        GameConfigItem(GameType.DAILY_QUIZ, isEnabled = true, maxDailyPlays = 3, minRewardRupees = 1.0, maxRewardRupees = 5.0, subtitle = "Test your knowledge & earn up to ₹5/quiz"),
        GameConfigItem(GameType.MEMORY_MATCH, isEnabled = true, maxDailyPlays = 3, minRewardRupees = 1.0, maxRewardRupees = 3.0, subtitle = "Match card pairs to test memory & win"),
        GameConfigItem(GameType.LUCKY_DRAW, isEnabled = true, maxDailyPlays = 1, minRewardRupees = 0.0, maxRewardRupees = 500.0, subtitle = "Claim free ticket for daily ₹500 pool")
    )

    private fun getInitialGamePlays(): List<GamePlayRecord> = listOf(
        GamePlayRecord("gp_1", "user_demo_001", GameType.SPIN_WHEEL, System.currentTimeMillis() - 86400000L, 2.0, "Wheel landed on ₹2.0"),
        GamePlayRecord("gp_2", "user_demo_001", GameType.SCRATCH_CARD, System.currentTimeMillis() - 86400000L, 3.5, "Scratched card revealed ₹3.5"),
        GamePlayRecord("gp_3", "user_demo_001", GameType.DAILY_QUIZ, System.currentTimeMillis() - 172800000L, 4.0, "Trivia score: 4/5")
    )

    private fun getInitialLuckyDrawPools(): List<LuckyDrawPool> = listOf(
        LuckyDrawPool(
            id = "pool_today",
            title = "Daily ₹500 Bumper Lucky Draw",
            prizePoolRupees = 500.0,
            totalWinners = 5,
            drawTimestamp = System.currentTimeMillis() + 43200000L,
            isCompleted = false,
            totalEntriesCount = 342
        ),
        LuckyDrawPool(
            id = "pool_yesterday",
            title = "Yesterday's Lucky Draw Pool",
            prizePoolRupees = 500.0,
            totalWinners = 5,
            drawTimestamp = System.currentTimeMillis() - 43200000L,
            isCompleted = true,
            totalEntriesCount = 512,
            winnerTicketNumbers = listOf(14829, 39201, 88294, 52019, 71029),
            winnerNames = listOf("Priya M.", "Rohan K.", "Amit V.", "Deepak S.", "Ananya R.")
        )
    )

    private fun getInitialLuckyDrawTickets(): List<LuckyDrawTicket> = emptyList()

    // --- Ad Gate Logging & Config Actions ---

    fun logAdGateAttempt(
        placementType: AdPlacementType,
        targetType: String,
        targetId: String,
        targetTitle: String,
        resultStatus: AdResultStatus
    ) {
        val user = _currentUser.value
        val newLog = AdGateLog(
            id = "adlog_" + System.currentTimeMillis(),
            userId = user?.uid ?: "guest_user",
            placementType = placementType,
            targetType = targetType,
            targetId = targetId,
            targetTitle = targetTitle,
            resultStatus = resultStatus,
            timestamp = System.currentTimeMillis()
        )
        _adGateLogs.value = listOf(newLog) + _adGateLogs.value
    }

    fun updateAdGateConfig(
        adGateEnabled: Boolean,
        adProvider: String,
        requireAdForTasks: Boolean,
        requireAdForOffers: Boolean,
        requireAdForGames: Boolean,
        countdownSeconds: Int
    ) {
        if (_currentUser.value?.isAdmin != true) return
        _appConfig.value = _appConfig.value.copy(
            adGateEnabled = adGateEnabled,
            adProvider = adProvider,
            requireAdForTasks = requireAdForTasks,
            requireAdForOffers = requireAdForOffers,
            requireAdForGames = requireAdForGames,
            adCountdownDurationSeconds = countdownSeconds
        )
    }

    private fun getInitialAdGateLogs(): List<AdGateLog> = listOf(
        AdGateLog(
            id = "adlog_101",
            userId = "user_demo_001",
            placementType = AdPlacementType.REWARDED,
            targetType = "Task",
            targetId = "task_001",
            targetTitle = "Install PhonePe",
            resultStatus = AdResultStatus.COMPLETED,
            timestamp = System.currentTimeMillis() - 3600000L
        ),
        AdGateLog(
            id = "adlog_102",
            userId = "user_demo_001",
            placementType = AdPlacementType.REWARDED,
            targetType = "Game",
            targetId = "spin_wheel",
            targetTitle = "Spin & Win",
            resultStatus = AdResultStatus.SKIPPED,
            timestamp = System.currentTimeMillis() - 7200000L
        ),
        AdGateLog(
            id = "adlog_103",
            userId = "u_201",
            placementType = AdPlacementType.REWARDED,
            targetType = "Offer",
            targetId = "off_01",
            targetTitle = "CryptoX App SignUp",
            resultStatus = AdResultStatus.COMPLETED,
            timestamp = System.currentTimeMillis() - 14400000L
        )
    )

    // --- Reels Actions ---

    fun uploadReel(
        caption: String,
        category: String,
        language: String,
        durationSeconds: Int,
        videoUrl: String
    ): Result<Reel> {
        val user = _currentUser.value ?: return Result.failure(Exception("Must be logged in to upload a reel."))
        val newReel = Reel(
            id = "reel_" + System.currentTimeMillis(),
            userId = user.uid,
            userName = user.fullName.ifBlank { user.username },
            userAvatarUrl = user.profilePhotoUrl,
            videoUrl = videoUrl.ifBlank { "https://assets.mixkit.co/videos/preview/mixkit-tree-with-yellow-flowers-1173-large.mp4" },
            caption = caption,
            category = category,
            language = language,
            durationSeconds = durationSeconds,
            status = ReelStatus.PENDING,
            createdAt = System.currentTimeMillis()
        )
        _reels.value = listOf(newReel) + _reels.value

        // Send confirmation notification to user
        addNotification(
            "Reel Submitted 🎬",
            "Your reel \"$caption\" is pending admin review. You will be notified once approved!"
        )

        return Result.success(newReel)
    }

    fun approveReel(reelId: String): Result<Unit> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Access Denied: Admin privileges required."))
        val reelList = _reels.value.toMutableList()
        val index = reelList.indexOfFirst { it.id == reelId }
        if (index == -1) return Result.failure(Exception("Reel not found."))

        val updatedReel = reelList[index].copy(status = ReelStatus.APPROVED)
        reelList[index] = updatedReel
        _reels.value = reelList

        // Notify uploader
        addNotification(
            "Reel Approved! 🎉",
            "Your reel \"${updatedReel.caption}\" has been approved and is now live in the public feed!"
        )

        return Result.success(Unit)
    }

    fun rejectReel(reelId: String, reason: String): Result<Unit> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Access Denied: Admin privileges required."))
        val reelList = _reels.value.toMutableList()
        val index = reelList.indexOfFirst { it.id == reelId }
        if (index == -1) return Result.failure(Exception("Reel not found."))

        val updatedReel = reelList[index].copy(
            status = ReelStatus.REJECTED,
            rejectionReason = reason.ifBlank { "Does not follow content community standards." }
        )
        reelList[index] = updatedReel
        _reels.value = reelList

        // Notify uploader
        addNotification(
            "Reel Moderation Update ⚠️",
            "Your reel \"${updatedReel.caption}\" was not approved. Reason: ${updatedReel.rejectionReason}"
        )

        return Result.success(Unit)
    }

    fun removeReel(reelId: String): Result<Unit> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Access Denied: Admin privileges required."))
        val reelList = _reels.value.toMutableList()
        val index = reelList.indexOfFirst { it.id == reelId }
        if (index == -1) return Result.failure(Exception("Reel not found."))

        reelList[index] = reelList[index].copy(status = ReelStatus.REMOVED)
        _reels.value = reelList
        return Result.success(Unit)
    }

    fun toggleLikeReel(reelId: String) {
        val reelList = _reels.value.toMutableList()
        val index = reelList.indexOfFirst { it.id == reelId }
        if (index != -1) {
            val item = reelList[index]
            val currentlyLiked = item.isLikedByCurrentUser
            val newCount = if (currentlyLiked) (item.likesCount - 1).coerceAtLeast(0) else item.likesCount + 1
            reelList[index] = item.copy(isLikedByCurrentUser = !currentlyLiked, likesCount = newCount)
            _reels.value = reelList
        }
    }

    fun reportReel(reelId: String, reason: ReelReportReason, notes: String): Result<Unit> {
        val user = _currentUser.value ?: return Result.failure(Exception("Must be logged in to report."))
        val reel = _reels.value.find { it.id == reelId } ?: return Result.failure(Exception("Reel not found."))

        val newReport = ReelReport(
            id = "rep_" + System.currentTimeMillis(),
            reelId = reelId,
            reelCaption = reel.caption,
            uploaderId = reel.userId,
            uploaderName = reel.userName,
            reporterUserId = user.uid,
            reason = reason,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        _reelReports.value = listOf(newReport) + _reelReports.value
        return Result.success(Unit)
    }

    fun resolveReelReport(reportId: String, action: String) {
        if (_currentUser.value?.isAdmin != true) return
        val reports = _reelReports.value.toMutableList()
        val index = reports.indexOfFirst { it.id == reportId }
        if (index != -1) {
            reports[index] = reports[index].copy(isResolved = true)
            _reelReports.value = reports
        }
    }

    fun logReelView(reelId: String, watchDurationMs: Long): Result<Double> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in."))
        val config = _appConfig.value

        // Minimum watch duration requirement (e.g. 5 seconds)
        if (watchDurationMs < (config.minWatchTimeSecondsForReward * 1000L)) {
            return Result.failure(Exception("Watch at least ${config.minWatchTimeSecondsForReward}s to earn view reward."))
        }

        // Prevent double reward for same reel per user per day
        val alreadyRewarded = _reelViewRecords.value.any {
            it.userId == user.uid && it.reelId == reelId && (System.currentTimeMillis() - it.timestamp) < 86400000L
        }
        if (alreadyRewarded) {
            return Result.failure(Exception("Reel view reward already claimed for today."))
        }

        val rewardAmount = config.rewardPerReelViewRupees
        val newRecord = ReelViewRecord(
            id = "rview_" + System.currentTimeMillis(),
            userId = user.uid,
            reelId = reelId,
            watchDurationMs = watchDurationMs,
            rewardAmount = rewardAmount,
            timestamp = System.currentTimeMillis()
        )
        _reelViewRecords.value = listOf(newRecord) + _reelViewRecords.value

        // Increment views count on reel
        val reelList = _reels.value.toMutableList()
        val idx = reelList.indexOfFirst { it.id == reelId }
        if (idx != -1) {
            reelList[idx] = reelList[idx].copy(viewsCount = reelList[idx].viewsCount + 1)
            _reels.value = reelList
        }

        // Credit wallet
        val updatedUser = user.copy(
            availableBalance = user.availableBalance + rewardAmount,
            totalEarned = user.totalEarned + rewardAmount
        )
        _currentUser.value = updatedUser

        addTransaction(
            userId = user.uid,
            type = TransactionType.TASK_REWARD,
            amount = rewardAmount,
            description = "Reel Watch Reward (${reelList.getOrNull(idx)?.caption ?: "Short Reel"})"
        )

        addNotification("Reel Watch Reward!", "+₹$rewardAmount credited to your wallet.")

        return Result.success(rewardAmount)
    }

    private fun getInitialReels(): List<Reel> = listOf(
        Reel(
            id = "reel_001",
            userId = "creator_raj",
            userName = "Rajesh Tech",
            caption = "Top 3 Student Earning Apps in India 📱💰",
            category = ReelCategory.FINANCE.title,
            language = ReelLanguage.HINDI.label,
            durationSeconds = 25,
            status = ReelStatus.APPROVED,
            likesCount = 342,
            viewsCount = 1820,
            createdAt = System.currentTimeMillis() - 86400000L
        ),
        Reel(
            id = "reel_002",
            userId = "creator_priya",
            userName = "Priya Code",
            caption = "How to finish daily tasks fast & withdraw to UPI! ⚡",
            category = ReelCategory.TUTORIALS.title,
            language = ReelLanguage.ENGLISH.label,
            durationSeconds = 40,
            status = ReelStatus.APPROVED,
            likesCount = 512,
            viewsCount = 2940,
            createdAt = System.currentTimeMillis() - 172800000L
        ),
        Reel(
            id = "reel_003",
            userId = "user_demo_001",
            userName = "Demo User",
            caption = "My first withdrawal proof from EarnMate India! 🚀",
            category = ReelCategory.MOTIVATION.title,
            language = ReelLanguage.HINDI.label,
            durationSeconds = 18,
            status = ReelStatus.PENDING,
            likesCount = 0,
            viewsCount = 12,
            createdAt = System.currentTimeMillis() - 3600000L
        )
    )

    private fun getInitialReelReports(): List<ReelReport> = listOf(
        ReelReport(
            id = "rep_101",
            reelId = "reel_001",
            reelCaption = "Top 3 Student Earning Apps in India 📱💰",
            uploaderId = "creator_raj",
            uploaderName = "Rajesh Tech",
            reporterUserId = "user_test_99",
            reason = ReelReportReason.SPAM,
            notes = "Contains external promotional link in overlay.",
            timestamp = System.currentTimeMillis() - 7200000L,
            isResolved = false
        )
    )

    // --- Freelancer Marketplace Repository Handlers ---

    fun becomeFreelancer(
        bio: String,
        skills: List<String>,
        experienceLevel: String,
        languages: List<String>,
        portfolioLinks: List<String>
    ): Result<FreelancerProfile> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        val profile = FreelancerProfile(
            userId = user.uid,
            username = user.username,
            displayName = user.fullName,
            profilePhotoUrl = user.profilePhotoUrl,
            bio = bio,
            skills = skills,
            experienceLevel = experienceLevel,
            languages = languages,
            portfolioLinks = portfolioLinks,
            completedJobsCount = 0,
            rating = 5.0,
            totalReviewsCount = 0,
            responseRatePercentage = 100,
            availabilityStatus = "AVAILABLE",
            joinedDate = System.currentTimeMillis()
        )
        val list = _freelancerProfiles.value.toMutableList()
        list.removeAll { it.userId == user.uid }
        list.add(0, profile)
        _freelancerProfiles.value = list
        return Result.success(profile)
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
    ): Result<FreelancerService> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        if (title.isBlank() || description.isBlank()) {
            return Result.failure(Exception("Title and description are required."))
        }
        val service = FreelancerService(
            id = "srv_" + System.currentTimeMillis() % 100000,
            freelancerId = user.uid,
            freelancerName = user.fullName,
            freelancerRating = _freelancerProfiles.value.find { it.userId == user.uid }?.rating ?: 5.0,
            title = title.trim(),
            description = description.trim(),
            category = category,
            startingPriceRupees = startingPriceRupees,
            deliveryTimeDays = deliveryTimeDays,
            revisionsAllowed = revisionsAllowed,
            skills = skills,
            portfolioImages = portfolioImages,
            status = ServiceStatus.PUBLISHED,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val list = _freelancerServices.value.toMutableList()
        list.add(0, service)
        _freelancerServices.value = list
        return Result.success(service)
    }

    fun postJob(
        title: String,
        description: String,
        category: FreelancerCategory,
        requiredSkills: List<String>,
        budgetRupees: Double,
        deadlineDays: Int,
        attachments: List<String>
    ): Result<ClientJob> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        if (title.isBlank() || description.isBlank()) {
            return Result.failure(Exception("Job title and description are required."))
        }
        val job = ClientJob(
            id = "job_" + System.currentTimeMillis() % 100000,
            clientId = user.uid,
            clientName = user.fullName,
            title = title.trim(),
            description = description.trim(),
            category = category,
            requiredSkills = requiredSkills,
            budgetRupees = budgetRupees,
            deadlineDays = deadlineDays,
            attachments = attachments,
            freelancersCount = 1,
            status = JobStatus.OPEN,
            proposalsCount = 0,
            createdAt = System.currentTimeMillis()
        )
        val list = _freelanceJobs.value.toMutableList()
        list.add(0, job)
        _freelanceJobs.value = list
        return Result.success(job)
    }

    fun submitProposal(
        jobId: String,
        proposalMessage: String,
        proposedPriceRupees: Double,
        estimatedDeliveryDays: Int,
        attachments: List<String>
    ): Result<JobProposal> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        val existing = _jobProposals.value.find { it.jobId == jobId && it.freelancerId == user.uid }
        if (existing != null) {
            return Result.failure(Exception("You have already submitted a proposal for this job."))
        }
        val job = _freelanceJobs.value.find { it.id == jobId }
            ?: return Result.failure(Exception("Job not found."))
        if (job.clientId == user.uid) {
            return Result.failure(Exception("You cannot submit a proposal for your own job posting."))
        }

        val proposal = JobProposal(
            id = "prop_" + System.currentTimeMillis() % 100000,
            jobId = jobId,
            freelancerId = user.uid,
            freelancerName = user.fullName,
            freelancerRating = _freelancerProfiles.value.find { it.userId == user.uid }?.rating ?: 5.0,
            proposalMessage = proposalMessage.trim(),
            proposedPriceRupees = proposedPriceRupees,
            estimatedDeliveryDays = estimatedDeliveryDays,
            attachments = attachments,
            status = ProposalStatus.PENDING,
            createdAt = System.currentTimeMillis()
        )
        val propList = _jobProposals.value.toMutableList()
        propList.add(0, proposal)
        _jobProposals.value = propList

        // Increment proposal count on job
        val updatedJobs = _freelanceJobs.value.map {
            if (it.id == jobId) it.copy(proposalsCount = it.proposalsCount + 1) else it
        }
        _freelanceJobs.value = updatedJobs

        return Result.success(proposal)
    }

    fun acceptProposal(proposalId: String): Result<FreelanceOrder> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        val proposal = _jobProposals.value.find { it.id == proposalId }
            ?: return Result.failure(Exception("Proposal not found."))
        val job = _freelanceJobs.value.find { it.id == proposal.jobId }
            ?: return Result.failure(Exception("Job not found."))

        if (job.clientId != user.uid && !user.isAdmin) {
            return Result.failure(Exception("Only job client can accept proposals."))
        }

        val config = _freelanceConfig.value
        val commissionPct = config.commissionPercentage
        val freelancerEarning = proposal.proposedPriceRupees * (1.0 - commissionPct / 100.0)

        val order = FreelanceOrder(
            id = "ord_" + System.currentTimeMillis() % 100000,
            jobId = job.id,
            serviceId = "",
            jobTitle = job.title,
            clientId = job.clientId,
            clientName = job.clientName,
            freelancerId = proposal.freelancerId,
            freelancerName = proposal.freelancerName,
            agreedPriceRupees = proposal.proposedPriceRupees,
            platformCommissionPercentage = commissionPct,
            freelancerEarningRupees = freelancerEarning,
            deadlineTimestamp = System.currentTimeMillis() + proposal.estimatedDeliveryDays * 86400000L,
            revisionsAllowed = 3,
            revisionsUsed = 0,
            status = OrderStatus.ACTIVE,
            paymentStatus = OrderPaymentStatus.PAYMENT_CONFIRMED,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        // Mark proposal accepted & other proposals rejected
        _jobProposals.value = _jobProposals.value.map {
            when {
                it.id == proposalId -> it.copy(status = ProposalStatus.ACCEPTED)
                it.jobId == job.id -> it.copy(status = ProposalStatus.REJECTED)
                else -> it
            }
        }

        // Mark job assigned
        _freelanceJobs.value = _freelanceJobs.value.map {
            if (it.id == job.id) it.copy(status = JobStatus.ASSIGNED) else it
        }

        val orderList = _freelanceOrders.value.toMutableList()
        orderList.add(0, order)
        _freelanceOrders.value = orderList

        // Initial system message in order workspace
        val initMsg = OrderMessage(
            id = "msg_" + System.currentTimeMillis() % 100000,
            orderId = order.id,
            senderId = "system",
            senderName = "EarnMate Escrow Bot",
            messageText = "Order workspace initiated! Agreed amount ₹${proposal.proposedPriceRupees.toInt()} is held safely in EarnMate Escrow.",
            timestamp = System.currentTimeMillis()
        )
        val msgList = _orderMessages.value.toMutableList()
        msgList.add(initMsg)
        _orderMessages.value = msgList

        return Result.success(order)
    }

    fun submitOrderDelivery(orderId: String, deliveryNotes: String, attachments: List<String>): Result<OrderDelivery> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        val order = _freelanceOrders.value.find { it.id == orderId }
            ?: return Result.failure(Exception("Order not found."))

        if (order.freelancerId != user.uid) {
            return Result.failure(Exception("Only assigned freelancer can submit work."))
        }

        val delivery = OrderDelivery(
            id = "del_" + System.currentTimeMillis() % 100000,
            orderId = orderId,
            freelancerId = user.uid,
            deliveryNotes = deliveryNotes,
            attachments = attachments,
            timestamp = System.currentTimeMillis(),
            revisionNumber = order.revisionsUsed + 1
        )

        val delList = _orderDeliveries.value.toMutableList()
        delList.add(0, delivery)
        _orderDeliveries.value = delList

        _freelanceOrders.value = _freelanceOrders.value.map {
            if (it.id == orderId) it.copy(status = OrderStatus.SUBMITTED, updatedAt = System.currentTimeMillis()) else it
        }

        // Add message
        val deliveryMsg = OrderMessage(
            id = "msg_" + System.currentTimeMillis() % 100000,
            orderId = orderId,
            senderId = user.uid,
            senderName = user.fullName,
            messageText = "📦 WORK DELIVERED:\n$deliveryNotes",
            attachments = attachments,
            timestamp = System.currentTimeMillis()
        )
        val msgList = _orderMessages.value.toMutableList()
        msgList.add(deliveryMsg)
        _orderMessages.value = msgList

        return Result.success(delivery)
    }

    fun requestOrderRevision(orderId: String, revisionNote: String): Result<FreelanceOrder> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        val order = _freelanceOrders.value.find { it.id == orderId }
            ?: return Result.failure(Exception("Order not found."))

        if (order.clientId != user.uid && !user.isAdmin) {
            return Result.failure(Exception("Only client can request revisions."))
        }

        if (order.revisionsUsed >= order.revisionsAllowed) {
            return Result.failure(Exception("All ${order.revisionsAllowed} included revisions have been used."))
        }

        val updatedOrder = order.copy(
            revisionsUsed = order.revisionsUsed + 1,
            status = OrderStatus.REVISION_REQUESTED,
            updatedAt = System.currentTimeMillis()
        )

        _freelanceOrders.value = _freelanceOrders.value.map { if (it.id == orderId) updatedOrder else it }

        val revMsg = OrderMessage(
            id = "msg_" + System.currentTimeMillis() % 100000,
            orderId = orderId,
            senderId = user.uid,
            senderName = user.fullName,
            messageText = "🔄 REVISION REQUESTED (${updatedOrder.revisionsUsed}/${updatedOrder.revisionsAllowed}):\n$revisionNote",
            timestamp = System.currentTimeMillis()
        )
        val msgList = _orderMessages.value.toMutableList()
        msgList.add(revMsg)
        _orderMessages.value = msgList

        return Result.success(updatedOrder)
    }

    fun approveOrderDelivery(orderId: String): Result<FreelanceOrder> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        val order = _freelanceOrders.value.find { it.id == orderId }
            ?: return Result.failure(Exception("Order not found."))

        if (order.clientId != user.uid && !user.isAdmin) {
            return Result.failure(Exception("Only client can approve delivery."))
        }

        val updatedOrder = order.copy(
            status = OrderStatus.APPROVED,
            updatedAt = System.currentTimeMillis()
        )
        _freelanceOrders.value = _freelanceOrders.value.map { if (it.id == orderId) updatedOrder else it }

        // Release earnings from escrow to freelancer's available balance!
        if (order.freelancerId == user.uid) {
            val u = _currentUser.value
            if (u != null) {
                val newBal = u.availableBalance + order.freelancerEarningRupees
                val newTotal = u.totalEarned + order.freelancerEarningRupees
                _currentUser.value = u.copy(availableBalance = newBal, totalEarned = newTotal)
            }
        }

        // Record transaction
        val tx = WalletTransaction(
            id = "tx_" + System.currentTimeMillis() % 100000,
            userId = order.freelancerId,
            type = TransactionType.TASK_REWARD,
            amount = order.freelancerEarningRupees,
            description = "Freelance Job Earnings: Escrow release for order #${order.id} (${order.jobTitle})",
            timestamp = System.currentTimeMillis(),
            status = TransactionStatus.COMPLETED
        )
        val txList = _transactions.value.toMutableList()
        txList.add(0, tx)
        _transactions.value = txList

        // Increment freelancer completed jobs count
        _freelancerProfiles.value = _freelancerProfiles.value.map {
            if (it.userId == order.freelancerId) it.copy(completedJobsCount = it.completedJobsCount + 1) else it
        }

        // Set job status completed
        _freelanceJobs.value = _freelanceJobs.value.map {
            if (it.id == order.jobId) it.copy(status = JobStatus.COMPLETED) else it
        }

        val approveMsg = OrderMessage(
            id = "msg_" + System.currentTimeMillis() % 100000,
            orderId = orderId,
            senderId = "system",
            senderName = "EarnMate Escrow Bot",
            messageText = "🎉 Delivery approved! Escrow payment of ₹${order.freelancerEarningRupees.toInt()} released to freelancer wallet.",
            timestamp = System.currentTimeMillis()
        )
        val msgList = _orderMessages.value.toMutableList()
        msgList.add(approveMsg)
        _orderMessages.value = msgList

        return Result.success(updatedOrder)
    }

    fun sendOrderMessage(orderId: String, messageText: String, attachments: List<String> = emptyList()): Result<OrderMessage> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        if (messageText.isBlank() && attachments.isEmpty()) {
            return Result.failure(Exception("Message cannot be empty."))
        }
        val order = _freelanceOrders.value.find { it.id == orderId }
            ?: return Result.failure(Exception("Order not found."))

        if (order.clientId != user.uid && order.freelancerId != user.uid && !user.isAdmin) {
            return Result.failure(Exception("Access denied: You are not part of this order workspace."))
        }

        val msg = OrderMessage(
            id = "msg_" + System.currentTimeMillis() % 100000,
            orderId = orderId,
            senderId = user.uid,
            senderName = user.fullName,
            messageText = messageText.trim(),
            attachments = attachments,
            timestamp = System.currentTimeMillis()
        )
        val msgList = _orderMessages.value.toMutableList()
        msgList.add(msg)
        _orderMessages.value = msgList
        return Result.success(msg)
    }

    fun submitFreelancerReview(orderId: String, rating: Int, reviewText: String): Result<FreelancerReview> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        val order = _freelanceOrders.value.find { it.id == orderId }
            ?: return Result.failure(Exception("Order not found."))

        if (order.clientId != user.uid && !user.isAdmin) {
            return Result.failure(Exception("Only client can submit reviews."))
        }

        val review = FreelancerReview(
            id = "rev_" + System.currentTimeMillis() % 100000,
            orderId = orderId,
            serviceId = order.serviceId,
            freelancerId = order.freelancerId,
            clientId = user.uid,
            clientName = user.fullName,
            rating = rating.coerceIn(1, 5),
            reviewText = reviewText.trim(),
            createdAt = System.currentTimeMillis()
        )
        val revList = _freelancerReviews.value.toMutableList()
        revList.add(0, review)
        _freelancerReviews.value = revList

        val freelancerRevs = revList.filter { it.freelancerId == order.freelancerId }
        val avgRating = freelancerRevs.map { it.rating }.average()
        _freelancerProfiles.value = _freelancerProfiles.value.map {
            if (it.userId == order.freelancerId) {
                it.copy(
                    rating = (avgRating * 10).toInt() / 10.0,
                    totalReviewsCount = freelancerRevs.size
                )
            } else it
        }

        return Result.success(review)
    }

    fun openDispute(orderId: String, reason: String, description: String, evidence: List<String>): Result<FreelanceDispute> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        val order = _freelanceOrders.value.find { it.id == orderId }
            ?: return Result.failure(Exception("Order not found."))

        val reported = if (order.clientId == user.uid) order.freelancerId else order.clientId

        val dispute = FreelanceDispute(
            id = "disp_" + System.currentTimeMillis() % 100000,
            orderId = orderId,
            reporterUserId = user.uid,
            reporterName = user.fullName,
            reportedUserId = reported,
            reason = reason,
            description = description,
            evidence = evidence,
            status = DisputeStatus.OPEN,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val dispList = _freelanceDisputes.value.toMutableList()
        dispList.add(0, dispute)
        _freelanceDisputes.value = dispList

        _freelanceOrders.value = _freelanceOrders.value.map {
            if (it.id == orderId) it.copy(status = OrderStatus.DISPUTED) else it
        }

        return Result.success(dispute)
    }

    fun adminUpdateFreelanceConfig(commissionPercentage: Double, minOrderValueRupees: Double, autoApproveDays: Int): Result<Unit> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        if (!user.isAdmin) return Result.failure(Exception("Admin privileges required."))
        _freelanceConfig.value = FreelanceConfig(
            commissionPercentage = commissionPercentage,
            minOrderValueRupees = minOrderValueRupees,
            autoApproveDays = autoApproveDays
        )
        return Result.success(Unit)
    }

    fun adminResolveDispute(disputeId: String, resolutionStatus: String, adminNotes: String): Result<Unit> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not authenticated"))
        if (!user.isAdmin) return Result.failure(Exception("Admin privileges required."))

        val dispute = _freelanceDisputes.value.find { it.id == disputeId }
            ?: return Result.failure(Exception("Dispute case not found."))

        val newStatus = when (resolutionStatus.uppercase()) {
            "REFUND" -> DisputeStatus.RESOLVED_REFUND
            "PAY_FREELANCER" -> DisputeStatus.RESOLVED_PAY
            else -> DisputeStatus.REJECTED
        }

        _freelanceDisputes.value = _freelanceDisputes.value.map {
            if (it.id == disputeId) it.copy(status = newStatus, adminNotes = adminNotes, updatedAt = System.currentTimeMillis()) else it
        }

        val orderStatus = if (newStatus == DisputeStatus.RESOLVED_REFUND) OrderStatus.CANCELLED else OrderStatus.APPROVED
        _freelanceOrders.value = _freelanceOrders.value.map {
            if (it.id == dispute.orderId) it.copy(status = orderStatus, updatedAt = System.currentTimeMillis()) else it
        }

        return Result.success(Unit)
    }

    private fun getInitialFreelancerProfiles(): List<FreelancerProfile> = listOf(
        FreelancerProfile(
            userId = "freelance_amit",
            username = "amit_thumbnails",
            displayName = "Amit Kumar",
            profilePhotoUrl = "",
            bio = "Professional YouTube Thumbnail Designer & Photoshop Expert with 4+ years of experience crafting high-CTR thumbnails.",
            skills = listOf("Photoshop", "YouTube Thumbnails", "Graphic Design", "Canva"),
            experienceLevel = "EXPERT",
            languages = listOf("Hindi", "English"),
            portfolioLinks = listOf("https://behance.net/demo_amit"),
            completedJobsCount = 42,
            rating = 4.9,
            totalReviewsCount = 38,
            responseRatePercentage = 98,
            availabilityStatus = "AVAILABLE",
            joinedDate = System.currentTimeMillis() - 180 * 86400000L
        ),
        FreelancerProfile(
            userId = "freelance_sneha",
            username = "sneha_edits",
            displayName = "Sneha Sharma",
            profilePhotoUrl = "",
            bio = "Short-form video editor specializing in Instagram Reels, Shorts, Premiere Pro & CapCut animations.",
            skills = listOf("Premiere Pro", "Video Editing", "Reels", "Subtitles", "CapCut"),
            experienceLevel = "INTERMEDIATE",
            languages = listOf("Hindi", "English"),
            portfolioLinks = listOf("https://youtube.com/demo_sneha"),
            completedJobsCount = 19,
            rating = 4.8,
            totalReviewsCount = 15,
            responseRatePercentage = 100,
            availabilityStatus = "AVAILABLE",
            joinedDate = System.currentTimeMillis() - 90 * 86400000L
        )
    )

    private fun getInitialFreelancerServices(): List<FreelancerService> = listOf(
        FreelancerService(
            id = "srv_101",
            freelancerId = "freelance_amit",
            freelancerName = "Amit Kumar",
            freelancerRating = 4.9,
            title = "High CTR YouTube Thumbnail Design (2 Hours Delivery)",
            description = "Get viral, high-converting YouTube thumbnails designed using Adobe Photoshop. Guaranteed high click-through rate!",
            category = FreelancerCategory.THUMBNAIL_DESIGN,
            startingPriceRupees = 199.0,
            deliveryTimeDays = 1,
            revisionsAllowed = 3,
            skills = listOf("Photoshop", "Thumbnail Design"),
            portfolioImages = emptyList(),
            status = ServiceStatus.PUBLISHED,
            createdAt = System.currentTimeMillis() - 10 * 86400000L
        ),
        FreelancerService(
            id = "srv_102",
            freelancerId = "freelance_sneha",
            freelancerName = "Sneha Sharma",
            freelancerRating = 4.8,
            title = "Viral Instagram Reel / Short Video Editing with Sound FX",
            description = "I will convert your raw video into engaging 60s Instagram Reels/YouTube Shorts with trending subtitles, motion graphics, and audio mixing.",
            category = FreelancerCategory.VIDEO_EDITING,
            startingPriceRupees = 349.0,
            deliveryTimeDays = 2,
            revisionsAllowed = 2,
            skills = listOf("Premiere Pro", "CapCut", "Reels"),
            portfolioImages = emptyList(),
            status = ServiceStatus.PUBLISHED,
            createdAt = System.currentTimeMillis() - 5 * 86400000L
        )
    )

    private fun getInitialFreelanceJobs(): List<ClientJob> = listOf(
        ClientJob(
            id = "job_201",
            clientId = "client_rohit",
            clientName = "Rohit Verma Tech",
            title = "Need 5 Tech YouTube Thumbnails for Smartphone Reviews",
            description = "Looking for a skilled graphic designer to create 5 eye-catching thumbnails for upcoming smartphone comparison videos.",
            category = FreelancerCategory.THUMBNAIL_DESIGN,
            requiredSkills = listOf("Photoshop", "YouTube"),
            budgetRupees = 800.0,
            deadlineDays = 2,
            proposalsCount = 3,
            status = JobStatus.OPEN,
            createdAt = System.currentTimeMillis() - 86400000L
        ),
        ClientJob(
            id = "job_202",
            clientId = "client_pooja",
            clientName = "Pooja Finance Channel",
            title = "Script Writer for 10-Minute Finance & Money Reels",
            description = "Require a knowledgeable scriptwriter in Hindi to write 3 engaging video scripts about mutual funds and stock market basics.",
            category = FreelancerCategory.SCRIPT_WRITING,
            requiredSkills = listOf("Script Writing", "Finance", "Hindi"),
            budgetRupees = 1200.0,
            deadlineDays = 4,
            proposalsCount = 2,
            status = JobStatus.OPEN,
            createdAt = System.currentTimeMillis() - 43200000L
        )
    )

    private fun getInitialJobProposals(): List<JobProposal> = listOf(
        JobProposal(
            id = "prop_301",
            jobId = "job_201",
            freelancerId = "freelance_amit",
            freelancerName = "Amit Kumar",
            freelancerRating = 4.9,
            proposalMessage = "Hi Rohit! I specialize in Tech YouTube Thumbnails. Check out my 4.9-star rating. Can deliver all 5 thumbnails within 24 hours with unlimited tweaks.",
            proposedPriceRupees = 750.0,
            estimatedDeliveryDays = 1,
            status = ProposalStatus.PENDING,
            createdAt = System.currentTimeMillis() - 36000000L
        )
    )

    private fun getInitialFreelanceOrders(): List<FreelanceOrder> = listOf(
        FreelanceOrder(
            id = "ord_401",
            jobId = "job_200_demo",
            serviceId = "srv_101",
            jobTitle = "Custom YouTube Gaming Banner & Logo",
            clientId = "client_rohit",
            clientName = "Rohit Verma Tech",
            freelancerId = "freelance_amit",
            freelancerName = "Amit Kumar",
            agreedPriceRupees = 500.0,
            platformCommissionPercentage = 10.0,
            freelancerEarningRupees = 450.0,
            deadlineTimestamp = System.currentTimeMillis() + 86400000L,
            revisionsAllowed = 3,
            revisionsUsed = 0,
            status = OrderStatus.ACTIVE,
            paymentStatus = OrderPaymentStatus.PAYMENT_CONFIRMED,
            createdAt = System.currentTimeMillis() - 172800000L
        )
    )

    private fun getInitialOrderMessages(): List<OrderMessage> = listOf(
        OrderMessage(
            id = "msg_501",
            orderId = "ord_401",
            senderId = "system",
            senderName = "EarnMate Escrow Bot",
            messageText = "Order workspace initiated! Agreed amount ₹500 is held safely in EarnMate Escrow.",
            timestamp = System.currentTimeMillis() - 172800000L
        ),
        OrderMessage(
            id = "msg_502",
            orderId = "ord_401",
            senderId = "client_rohit",
            senderName = "Rohit Verma Tech",
            messageText = "Hi Amit! Please use red and dark purple color scheme for the gaming banner.",
            timestamp = System.currentTimeMillis() - 160000000L
        )
    )

    private fun getInitialOrderDeliveries(): List<OrderDelivery> = emptyList()

    private fun getInitialFreelancerReviews(): List<FreelancerReview> = listOf(
        FreelancerReview(
            id = "rev_601",
            orderId = "ord_prev_01",
            serviceId = "srv_101",
            freelancerId = "freelance_amit",
            clientId = "user_demo_001",
            clientName = "Rahul Sharma",
            rating = 5,
            reviewText = "Awesome thumbnail! CTR increased by 14% on my latest video. Highly recommended!",
            createdAt = System.currentTimeMillis() - 500000000L
        )
    )

    // --- Premium Membership Methods ---
    fun checkAndRefreshPremiumStatus() {
        val user = _currentUser.value ?: return
        if (user.membershipType.equals("PREMIUM", ignoreCase = true) && user.premiumStatus.equals("active", ignoreCase = true)) {
            if (user.premiumExpiryDate > 0 && user.premiumExpiryDate <= System.currentTimeMillis()) {
                val expiredUser = user.copy(
                    membershipType = "FREE",
                    premiumStatus = "expired"
                )
                _currentUser.value = expiredUser
                updateUserInAllList(expiredUser)
                addNotification("Premium Subscription Expired", "Your EarnMate Premium membership has expired. Renew anytime to regain exclusive perks!")
            }
        }
    }

    fun isPremiumUser(): Boolean {
        checkAndRefreshPremiumStatus()
        return _currentUser.value?.isPremiumActive == true
    }

    fun hasPremiumFeature(featureKey: String): Boolean {
        val config = _premiumConfig.value
        if (!config.systemEnabled) return false
        val featureEnabled = config.featureFlags[featureKey] ?: true
        return featureEnabled && isPremiumUser()
    }

    fun toggleSaveJob(jobId: String): Result<Boolean> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        val currentSaved = user.savedJobIds.toMutableList()
        val isSavedNow = if (currentSaved.contains(jobId)) {
            currentSaved.remove(jobId)
            false
        } else {
            currentSaved.add(jobId)
            true
        }
        val updatedUser = user.copy(savedJobIds = currentSaved)
        _currentUser.value = updatedUser
        updateUserInAllList(updatedUser)
        return Result.success(isSavedNow)
    }

    fun addProposalTemplate(title: String, text: String): Result<ProposalTemplate> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        if (title.isBlank() || text.isBlank()) return Result.failure(Exception("Title and content cannot be blank"))
        val tpl = ProposalTemplate(
            id = "tpl_" + System.currentTimeMillis(),
            userId = user.uid,
            title = title,
            templateText = text,
            createdAt = System.currentTimeMillis()
        )
        _proposalTemplates.value = listOf(tpl) + _proposalTemplates.value
        return Result.success(tpl)
    }

    fun updateProposalTemplate(id: String, title: String, text: String): Result<Unit> {
        val list = _proposalTemplates.value.toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx == -1) return Result.failure(Exception("Template not found"))
        list[idx] = list[idx].copy(title = title, templateText = text)
        _proposalTemplates.value = list
        return Result.success(Unit)
    }

    fun deleteProposalTemplate(id: String): Result<Unit> {
        _proposalTemplates.value = _proposalTemplates.value.filter { it.id != id }
        return Result.success(Unit)
    }

    fun duplicateProposalTemplate(template: ProposalTemplate): Result<ProposalTemplate> {
        return addProposalTemplate(template.title + " (Copy)", template.templateText)
    }

    fun getFreelancerAnalytics(): FreelancerAnalytics {
        val user = _currentUser.value ?: return FreelancerAnalytics()
        val userId = user.uid
        val myProposals = _jobProposals.value.filter { it.freelancerId == userId }
        val myOrders = _freelanceOrders.value.filter { it.freelancerId == userId }
        val completed = myOrders.filter { it.status == OrderStatus.APPROVED }
        val totalEarned = completed.sumOf { it.freelancerEarningRupees }

        return FreelancerAnalytics(
            profileViews = 48 + myOrders.size * 3,
            gigViews = 124 + myProposals.size * 5,
            gigClicks = 38 + myProposals.size * 2,
            applicationsSubmitted = myProposals.size,
            ordersReceived = myOrders.size,
            completedOrders = completed.size,
            totalEarningsRupees = totalEarned
        )
    }

    // --- Admin Premium Controls ---
    fun adminUpdatePremiumConfig(config: PremiumConfig): Result<Unit> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Admin authorization required"))
        _premiumConfig.value = config
        return Result.success(Unit)
    }

    fun adminSavePremiumPlan(plan: PremiumPlan): Result<Unit> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Admin authorization required"))
        val list = _premiumPlans.value.toMutableList()
        val idx = list.indexOfFirst { it.planId == plan.planId }
        if (idx != -1) {
            list[idx] = plan
        } else {
            list.add(plan)
        }
        _premiumPlans.value = list
        return Result.success(Unit)
    }

    fun adminGrantPremium(targetUserId: String, planId: String, customDays: Int? = null): Result<Unit> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Admin authorization required"))
        val plan = _premiumPlans.value.find { it.planId == planId } ?: _premiumPlans.value.firstOrNull()
        val durationDays = customDays ?: plan?.durationDays ?: 30
        val planName = plan?.planName ?: "Admin Premium Grant"
        val price = plan?.priceRupees ?: 0.0

        val now = System.currentTimeMillis()
        val expiry = now + (durationDays.toLong() * 24 * 3600 * 1000L)

        val targetUser = _allUsers.value.find { it.uid == targetUserId }
        val updatedUser = (targetUser ?: _currentUser.value)?.copy(
            membershipType = "PREMIUM",
            premiumStatus = "active",
            premiumStartDate = now,
            premiumExpiryDate = expiry,
            premiumPlan = planName
        ) ?: return Result.failure(Exception("Target user not found"))

        if (updatedUser.uid == _currentUser.value?.uid) {
            _currentUser.value = updatedUser
        }
        updateUserInAllList(updatedUser)

        val historyRecord = PremiumMembershipHistory(
            recordId = "pm_hist_" + System.currentTimeMillis(),
            userId = updatedUser.uid,
            userName = updatedUser.fullName,
            planId = planId,
            planName = planName,
            pricePaidRupees = price,
            startDate = now,
            expiryDate = expiry,
            activatedByAdminId = _currentUser.value?.uid,
            paymentTxnId = "ADMIN_GRANT_" + System.currentTimeMillis()
        )
        _premiumHistory.value = listOf(historyRecord) + _premiumHistory.value

        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(expiry))
        addNotification("EarnMate Premium Activated! 👑", "Admin granted $planName membership to your account until $dateStr.")
        return Result.success(Unit)
    }

    fun adminRevokePremium(targetUserId: String): Result<Unit> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Admin authorization required"))
        val targetUser = _allUsers.value.find { it.uid == targetUserId } ?: return Result.failure(Exception("User not found"))
        val updatedUser = targetUser.copy(
            membershipType = "FREE",
            premiumStatus = "expired"
        )
        if (updatedUser.uid == _currentUser.value?.uid) {
            _currentUser.value = updatedUser
        }
        updateUserInAllList(updatedUser)
        addNotification("Membership Update", "Your Premium membership has been revoked by admin.")
        return Result.success(Unit)
    }

    fun adminExtendPremium(targetUserId: String, extraDays: Int): Result<Unit> {
        if (_currentUser.value?.isAdmin != true) return Result.failure(Exception("Admin authorization required"))
        val targetUser = _allUsers.value.find { it.uid == targetUserId } ?: return Result.failure(Exception("User not found"))
        val baseExpiry = if (targetUser.premiumExpiryDate > System.currentTimeMillis()) targetUser.premiumExpiryDate else System.currentTimeMillis()
        val newExpiry = baseExpiry + (extraDays.toLong() * 24 * 3600 * 1000L)
        val updatedUser = targetUser.copy(
            membershipType = "PREMIUM",
            premiumStatus = "active",
            premiumExpiryDate = newExpiry
        )
        if (updatedUser.uid == _currentUser.value?.uid) {
            _currentUser.value = updatedUser
        }
        updateUserInAllList(updatedUser)
        addNotification("Premium Extended!", "Your Premium subscription has been extended by $extraDays days.")
        return Result.success(Unit)
    }

    private fun updateUserInAllList(user: UserProfile) {
        val list = _allUsers.value.toMutableList()
        val idx = list.indexOfFirst { it.uid == user.uid }
        if (idx != -1) {
            list[idx] = user
            _allUsers.value = list
        }
    }

    private fun getInitialPremiumPlans(): List<PremiumPlan> = listOf(
        PremiumPlan(
            planId = "plan_monthly",
            planName = "Monthly Premium",
            priceRupees = 99.0,
            durationDays = 30,
            currency = "INR",
            active = true,
            features = listOf(
                "Ad-Free Experience across EarnMate",
                "⭐ Gold PREMIUM Badge on Gigs & Profile",
                "Featured Gig & Profile Priority",
                "10 Active Gigs Capacity (5x FREE limit)",
                "Advanced Job Filters & Sorting",
                "Saved Jobs Locker",
                "Proposal Templates Helper",
                "Freelancer Analytics & Insights"
            )
        ),
        PremiumPlan(
            planId = "plan_quarterly",
            planName = "Quarterly VIP Premium",
            priceRupees = 249.0,
            durationDays = 90,
            currency = "INR",
            active = true,
            features = listOf(
                "Ad-Free Experience across EarnMate",
                "⭐ Gold PREMIUM Badge on Gigs & Profile",
                "Featured Gig & Profile Priority",
                "10 Active Gigs Capacity (5x FREE limit)",
                "Advanced Job Filters & Sorting",
                "Saved Jobs Locker",
                "Proposal Templates Helper",
                "Freelancer Analytics & Insights",
                "Save 16% Off Monthly Cost"
            )
        )
    )

    private fun getInitialPremiumHistory(): List<PremiumMembershipHistory> = listOf(
        PremiumMembershipHistory(
            recordId = "pm_hist_101",
            userId = "user_demo_001",
            userName = "Rahul Sharma",
            planId = "plan_monthly",
            planName = "Monthly Premium",
            pricePaidRupees = 99.0,
            startDate = System.currentTimeMillis() - 10 * 86400000L,
            expiryDate = System.currentTimeMillis() + 20 * 86400000L,
            activatedByAdminId = "admin_master",
            paymentTxnId = "TXN_UPI_998234"
        )
    )

    private fun getInitialProposalTemplates(): List<ProposalTemplate> = listOf(
        ProposalTemplate(
            id = "tpl_101",
            userId = "user_demo_001",
            title = "YouTube Thumbnail Proposal Standard",
            templateText = "Hi! I specialize in high CTR YouTube thumbnail design with bold typography and glow effects. I can deliver 2 custom thumbnail options within 24 hours. Check my portfolio in profile!",
            createdAt = System.currentTimeMillis() - 5 * 86400000L
        ),
        ProposalTemplate(
            id = "tpl_102",
            userId = "user_demo_001",
            title = "Reels & Video Editing Proposal",
            templateText = "Hello! I edit viral Instagram Reels and Shorts with fast pacing, sound effects, and subtitles. Let's make your content stand out!",
            createdAt = System.currentTimeMillis() - 3 * 86400000L
        )
    )

    // --- Admin Operations ---

    fun logAdminActivity(action: String, targetId: String, description: String) {
        val admin = _currentUser.value
        val log = AdminActivityLog(
            id = "log_" + System.currentTimeMillis(),
            adminId = admin?.uid ?: "admin_master",
            adminEmail = admin?.email ?: "admin@earnmate.in",
            action = action,
            targetId = targetId,
            description = description,
            timestamp = System.currentTimeMillis()
        )
        _adminActivityLogs.value = listOf(log) + _adminActivityLogs.value
    }

    fun adminAuthenticate(email: String, pass: String): Result<UserProfile> {
        if (email.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Please enter both email and password."))
        }
        val cleanEmail = email.trim().lowercase()
        // Check if existing user or admin
        var user = _allUsers.value.find { it.email.lowercase() == cleanEmail }
        if (user == null) {
            if (cleanEmail.contains("admin") || cleanEmail == "admin@earnmate.in") {
                user = UserProfile(
                    uid = "admin_super_01",
                    username = "super_admin",
                    fullName = "Master Administrator",
                    email = cleanEmail,
                    phone = "+91 99999 00000",
                    isAdmin = true,
                    availableBalance = 5000.0,
                    totalEarned = 5000.0
                )
                _allUsers.value = listOf(user) + _allUsers.value
            } else {
                return Result.failure(Exception("Invalid admin credentials or account does not exist."))
            }
        }

        if (!user.isAdmin) {
            return Result.failure(Exception("Access Denied: Account '${user.email}' is not authorized as an Admin."))
        }

        _currentUser.value = user
        logAdminActivity("ADMIN_LOGIN", user.uid, "Admin authenticated successfully: ${user.email}")
        return Result.success(user)
    }

    fun adminSuspendUser(userId: String, reason: String): Result<Boolean> {
        val user = _allUsers.value.find { it.uid == userId } ?: return Result.failure(Exception("User not found."))
        val updated = user.copy(isSuspended = true)
        updateUserInAllList(updated)
        logAdminActivity("USER_SUSPEND", userId, "Suspended user ${user.fullName} (${user.email}). Reason: $reason")
        return Result.success(true)
    }

    fun adminUnsuspendUser(userId: String): Result<Boolean> {
        val user = _allUsers.value.find { it.uid == userId } ?: return Result.failure(Exception("User not found."))
        val updated = user.copy(isSuspended = false)
        updateUserInAllList(updated)
        logAdminActivity("USER_UNSUSPEND", userId, "Unsuspended user ${user.fullName} (${user.email})")
        return Result.success(true)
    }

    fun adminUpdateTask(updatedTask: TaskItem): Result<Boolean> {
        val list = _tasks.value.toMutableList()
        val idx = list.indexOfFirst { it.id == updatedTask.id }
        if (idx != -1) {
            list[idx] = updatedTask
            _tasks.value = list
            logAdminActivity("TASK_UPDATE", updatedTask.id, "Updated task '${updatedTask.title}'")
            return Result.success(true)
        }
        return Result.failure(Exception("Task not found."))
    }

    fun adminDeleteTask(taskId: String): Result<Boolean> {
        val task = _tasks.value.find { it.id == taskId } ?: return Result.failure(Exception("Task not found."))
        _tasks.value = _tasks.value.filter { it.id != taskId }
        logAdminActivity("TASK_DELETE", taskId, "Deleted task '${task.title}'")
        return Result.success(true)
    }

    fun adminToggleTaskActive(taskId: String): Result<Boolean> {
        val task = _tasks.value.find { it.id == taskId } ?: return Result.failure(Exception("Task not found."))
        val newStatus = if (task.status == TaskStatus.AVAILABLE) TaskStatus.EXPIRED else TaskStatus.AVAILABLE
        val updated = task.copy(status = newStatus)
        adminUpdateTask(updated)
        logAdminActivity("TASK_TOGGLE", taskId, "Toggled task status to $newStatus")
        return Result.success(true)
    }

    fun adminResolveReport(reportId: String, status: ReportStatus, adminNotes: String): Result<Boolean> {
        val report = _centralReports.value.find { it.id == reportId } ?: return Result.failure(Exception("Report not found."))
        val updated = report.copy(status = status, adminNotes = adminNotes)
        _centralReports.value = _centralReports.value.map { if (it.id == reportId) updated else it }
        logAdminActivity("REPORT_RESOLVE", reportId, "Resolved report '${report.title}' with status: $status")
        return Result.success(true)
    }

    fun adminCreateReport(
        category: ReportCategory,
        title: String,
        description: String,
        reporterId: String,
        reporterName: String,
        targetId: String,
        targetName: String
    ): Result<CentralReport> {
        val r = CentralReport(
            id = "rep_" + System.currentTimeMillis(),
            category = category,
            title = title,
            description = description,
            reporterId = reporterId,
            reporterName = reporterName,
            targetId = targetId,
            targetName = targetName,
            status = ReportStatus.OPEN,
            createdAt = System.currentTimeMillis()
        )
        _centralReports.value = listOf(r) + _centralReports.value
        return Result.success(r)
    }

    fun adminSendNotification(
        title: String,
        message: String,
        audience: NotificationAudience,
        targetUserId: String?
    ): Result<BroadcastNotification> {
        val admin = _currentUser.value
        val b = BroadcastNotification(
            id = "bcast_" + System.currentTimeMillis(),
            title = title.trim(),
            message = message.trim(),
            targetAudience = audience,
            targetUserId = targetUserId,
            sentAt = System.currentTimeMillis(),
            sentByAdminId = admin?.uid ?: "admin_master"
        )
        _broadcastNotifications.value = listOf(b) + _broadcastNotifications.value

        // Dispatch user-visible notification
        val notif = NotificationItem(
            id = "notif_" + System.currentTimeMillis(),
            title = title,
            message = message,
            type = NotificationType.ANNOUNCEMENT,
            timestamp = System.currentTimeMillis()
        )
        _notifications.value = listOf(notif) + _notifications.value

        logAdminActivity("NOTIFICATION_BROADCAST", b.id, "Sent notification '${b.title}' to audience: ${audience.label}")
        return Result.success(b)
    }

    fun adminUpdateAdsConfig(
        adsEnabled: Boolean,
        bannerEnabled: Boolean,
        interstitialEnabled: Boolean,
        rewardedEnabled: Boolean,
        adGateEnabled: Boolean
    ): Result<Boolean> {
        val config = _appConfig.value.copy(
            adsEnabled = adsEnabled,
            bannerAdsEnabled = bannerEnabled,
            interstitialAdsEnabled = interstitialEnabled,
            rewardedAdsEnabled = rewardedEnabled,
            adGateEnabled = adGateEnabled
        )
        _appConfig.value = config
        logAdminActivity("ADS_CONFIG_UPDATE", "app_config", "Updated AdMob configuration. Ads Enabled: $adsEnabled")
        return Result.success(true)
    }

    fun adminDeleteJobListing(jobId: String): Result<Boolean> {
        _freelanceJobs.value = _freelanceJobs.value.filterNot { it.id == jobId }
        logAdminActivity("JOB_DELETE", jobId, "Admin removed job listing ID $jobId")
        return Result.success(true)
    }

    private fun getInitialCentralReports(): List<CentralReport> = listOf(
        CentralReport(
            id = "rep_201",
            category = ReportCategory.JOB,
            title = "Suspicious Job Posting - Unrealistic Earnings",
            description = "Job posting claims ₹50,000 per hour for data entry. Requires upfront fee.",
            reporterId = "user_demo_001",
            reporterName = "Rahul Sharma",
            targetId = "job_102",
            targetName = "High Speed Data Entry Specialist",
            status = ReportStatus.OPEN,
            createdAt = System.currentTimeMillis() - 2 * 3600000L
        ),
        CentralReport(
            id = "rep_202",
            category = ReportCategory.REEL,
            title = "Copyright Infringement in Reel Video",
            description = "Video uses copyrighted music track without attribution.",
            reporterId = "user_demo_002",
            reporterName = "Priya Patel",
            targetId = "reel_301",
            targetName = "Amazing Tech Gadgets Unboxing",
            status = ReportStatus.UNDER_REVIEW,
            createdAt = System.currentTimeMillis() - 8 * 3600000L
        )
    )

    private fun getInitialAdminLogs(): List<AdminActivityLog> = listOf(
        AdminActivityLog(
            id = "log_101",
            adminId = "admin_super_01",
            adminEmail = "admin@earnmate.in",
            action = "SYSTEM_INIT",
            targetId = "app_config",
            description = "EarnMate India Admin Console initialized successfully.",
            timestamp = System.currentTimeMillis() - 24 * 3600000L
        ),
        AdminActivityLog(
            id = "log_102",
            adminId = "admin_super_01",
            adminEmail = "admin@earnmate.in",
            action = "TASK_CREATED",
            targetId = "task_001",
            description = "Published task 'E-Commerce App UX Feedback'",
            timestamp = System.currentTimeMillis() - 12 * 3600000L
        )
    )

    private fun getInitialBroadcastNotifications(): List<BroadcastNotification> = listOf(
        BroadcastNotification(
            id = "bcast_101",
            title = "EarnMate Weekend Surge Bonus! 🚀",
            message = "Complete 3 tasks today to earn a ₹50 bonus directly in your wallet!",
            targetAudience = NotificationAudience.ALL_USERS,
            sentAt = System.currentTimeMillis() - 36 * 3600000L,
            sentByAdminId = "admin_super_01"
        )
    )

}

