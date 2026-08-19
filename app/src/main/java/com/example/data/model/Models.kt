package com.example.data.model

import androidx.annotation.Keep

@Keep
enum class TaskCategory(val label: String, val iconName: String) {
    SURVEY("Surveys", "poll"),
    APP_TESTING("App Testing", "phone_android"),
    CONTENT("Content Tasks", "article"),
    AI_TASK("AI Tasks", "psychology"),
    DATA_ENTRY("Data Tasks", "dataset"),
    MICRO_TASK("Micro Tasks", "bolt"),
    LEARNING("Learning Tasks", "school"),
    PROMO("Promotions", "campaign")
}

@Keep
enum class TaskDifficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

@Keep
enum class ProofType(val label: String) {
    TEXT_INPUT("Text Proof / Answer"),
    SCREENSHOT_URL("Screenshot Upload"),
    LINK_SUBMISSION("Shared Link"),
    CODE_VERIFICATION("Verification Code")
}

@Keep
enum class TaskStatus {
    AVAILABLE, STARTED, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, EXPIRED
}

@Keep
enum class SubmissionStatus {
    PENDING, APPROVED, REJECTED
}

@Keep
enum class OfferStatus {
    AVAILABLE, IN_PROGRESS, COMPLETED
}

@Keep
enum class TransactionType(val label: String) {
    TASK_REWARD("Task Reward"),
    OFFER_REWARD("Offer Reward"),
    REFERRAL_REWARD("Referral Bonus"),
    DAILY_BONUS("Daily Check-in"),
    GAME_REWARD("Game Reward"),
    WITHDRAWAL("Withdrawal"),
    ADJUSTMENT("Bonus/Adjustment")
}

@Keep
enum class TransactionStatus {
    COMPLETED, PENDING, FAILED, CANCELLED
}

@Keep
enum class WithdrawalMethod(val label: String) {
    UPI("UPI Transfer"),
    BANK_TRANSFER("Bank Account Transfer")
}

@Keep
enum class WithdrawalStatus(val label: String) {
    REQUESTED("Requested"),
    PROCESSING("Processing"),
    APPROVED("Approved"),
    PAID("Paid"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled")
}

@Keep
enum class NotificationType {
    TASK_APPROVED, TASK_REJECTED, REWARD_RECEIVED, WITHDRAWAL_UPDATE, REFERRAL_REWARD, ANNOUNCEMENT
}

@Keep
enum class TicketStatus(val label: String) {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    CLOSED("Closed")
}

@Keep
data class UserProfile(
    val uid: String = "",
    val username: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val profilePhotoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val referralCode: String = "",
    val referredByCode: String? = null,
    val totalEarned: Double = 0.0,
    val availableBalance: Double = 0.0,
    val pendingRewards: Double = 0.0,
    val completedTasksCount: Int = 0,
    val currentStreak: Int = 0,
    val lastCheckInDate: String = "",
    val isAdmin: Boolean = false,
    val isSuspended: Boolean = false,
    val hideFromLeaderboard: Boolean = false,
    val language: String = "English",
    val membershipType: String = "FREE",
    val premiumStatus: String = "none",
    val premiumStartDate: Long = 0L,
    val premiumExpiryDate: Long = 0L,
    val premiumPlan: String = "",
    val premiumAutoRenew: Boolean = false,
    val savedJobIds: List<String> = emptyList()
) {
    val isPremiumActive: Boolean
        get() = membershipType.equals("PREMIUM", ignoreCase = true) &&
                premiumStatus.equals("active", ignoreCase = true) &&
                premiumExpiryDate > System.currentTimeMillis()
}

@Keep
data class TaskItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: TaskCategory = TaskCategory.SURVEY,
    val rewardRupees: Double = 0.0,
    val estimatedMinutes: Int = 5,
    val difficulty: TaskDifficulty = TaskDifficulty.EASY,
    val eligibility: String = "All verified Indian users",
    val instructions: List<String> = emptyList(),
    val requiredProofType: ProofType = ProofType.TEXT_INPUT,
    val status: TaskStatus = TaskStatus.AVAILABLE,
    val expiryTimestamp: Long = System.currentTimeMillis() + (7 * 24 * 3600 * 1000L),
    val isFeatured: Boolean = false,
    val iconName: String = "task"
)

@Keep
data class TaskSubmission(
    val id: String = "",
    val taskId: String = "",
    val taskTitle: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val proofContent: String = "",
    val rewardRupees: Double = 0.0,
    val status: SubmissionStatus = SubmissionStatus.PENDING,
    val adminNote: String? = null
)

@Keep
data class OfferItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val providerName: String = "",
    val rewardRupees: Double = 0.0,
    val estimatedMinutes: Int = 10,
    val termsAndConditions: String = "",
    val requirements: List<String> = emptyList(),
    val status: OfferStatus = OfferStatus.AVAILABLE,
    val badgeText: String = "Top Offer"
)

@Keep
data class WalletTransaction(
    val id: String = "",
    val userId: String = "",
    val type: TransactionType = TransactionType.TASK_REWARD,
    val amount: Double = 0.0,
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String = "",
    val referenceId: String? = null
)

@Keep
data class WithdrawalRequest(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val method: WithdrawalMethod = WithdrawalMethod.UPI,
    val amountRupees: Double = 0.0,
    val feeRupees: Double = 0.0,
    val payoutDetails: String = "",
    val status: WithdrawalStatus = WithdrawalStatus.REQUESTED,
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null,
    val transactionReference: String? = null,
    val rejectionReason: String? = null
)

@Keep
data class ReferralUserSummary(
    val uid: String = "",
    val username: String = "",
    val joinedAt: Long = System.currentTimeMillis(),
    val status: String = "Active", // Active, Pending
    val earnedForReferrer: Double = 25.0
)

@Keep
data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.ANNOUNCEMENT,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Keep
data class SupportTicket(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val subject: String = "",
    val category: String = "",
    val message: String = "",
    val status: TicketStatus = TicketStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis(),
    val adminReply: String? = null
)

@Keep
data class LeaderboardEntry(
    val rank: Int = 0,
    val username: String = "",
    val totalEarned: Double = 0.0,
    val completedTasks: Int = 0,
    val isCurrentUser: Boolean = false
)

@Keep
enum class AdPlacementType(val label: String) {
    REWARDED("Rewarded Video"),
    INTERSTITIAL("Interstitial"),
    BANNER("Banner")
}

@Keep
enum class AdResultStatus(val label: String) {
    COMPLETED("Completed"),
    SKIPPED("Skipped"),
    CLOSED_EARLY("Closed Early"),
    FAILED("Failed")
}

@Keep
data class AdResult(
    val status: AdResultStatus,
    val placementType: AdPlacementType = AdPlacementType.REWARDED,
    val message: String = ""
)

@Keep
data class AdGateLog(
    val id: String = "",
    val userId: String = "",
    val placementType: AdPlacementType = AdPlacementType.REWARDED,
    val targetType: String = "", // "Task", "Offer", "Game"
    val targetId: String = "",
    val targetTitle: String = "",
    val resultStatus: AdResultStatus = AdResultStatus.COMPLETED,
    val timestamp: Long = System.currentTimeMillis()
)

@Keep
data class AppConfig(
    val minimumWithdrawalRupees: Double = 100.0,
    val referralRewardRupees: Double = 25.0,
    val dailyBonusBaseRupees: Double = 2.0,
    val maintenanceMode: Boolean = false,
    val appAnnouncement: String = "Welcome to EarnMate India! Complete daily tasks, test apps, and earn verified rewards safely via UPI/Bank transfer.",
    val withdrawalFeePercentage: Double = 0.0,
    val adsEnabled: Boolean = true,
    val bannerAdsEnabled: Boolean = true,
    val rewardedAdsEnabled: Boolean = true,
    val interstitialAdsEnabled: Boolean = true,
    // Ad Gate Configuration
    val adGateEnabled: Boolean = true,
    val adProvider: String = "mock", // "mock" | "real"
    val requireAdForTasks: Boolean = true,
    val requireAdForOffers: Boolean = true,
    val requireAdForGames: Boolean = true,
    val adCountdownDurationSeconds: Int = 5,
    // Reels Configuration
    val reelsEnabled: Boolean = true,
    val maxReelDurationSeconds: Int = 60,
    val minWatchTimeSecondsForReward: Int = 5,
    val rewardPerReelViewRupees: Double = 0.20,
    val maxDailyReelRewardRupees: Double = 5.0
)

// --- Games & Rewards Data Models ---

@Keep
enum class GameType(val title: String, val iconName: String) {
    SPIN_WHEEL("Spin & Win", "casino"),
    SCRATCH_CARD("Scratch & Earn", "gesture"),
    DAILY_QUIZ("Daily Trivia Quiz", "quiz"),
    MEMORY_MATCH("Memory Flip", "extension"),
    LUCKY_DRAW("Daily Lucky Draw", "confirmation_number")
}

@Keep
data class GameConfigItem(
    val gameType: GameType = GameType.SPIN_WHEEL,
    val isEnabled: Boolean = true,
    val maxDailyPlays: Int = 5,
    val minRewardRupees: Double = 0.5,
    val maxRewardRupees: Double = 10.0,
    val cooldownMinutes: Int = 0,
    val subtitle: String = "Free reward mechanic"
)

@Keep
data class GamePlayRecord(
    val id: String = "",
    val userId: String = "",
    val gameType: GameType = GameType.SPIN_WHEEL,
    val playedAt: Long = System.currentTimeMillis(),
    val rewardAmount: Double = 0.0,
    val details: String = ""
)

@Keep
data class QuizQuestion(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val explanation: String = "",
    val category: String = "General Knowledge"
)

@Keep
data class QuizResult(
    val totalQuestions: Int = 5,
    val correctAnswers: Int = 0,
    val scorePercentage: Int = 0,
    val rewardEarned: Double = 0.0,
    val timeTakenSeconds: Int = 0
)

@Keep
data class MemoryCard(
    val id: Int = 0,
    val symbol: String = "",
    val pairId: Int = 0,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

@Keep
data class LuckyDrawTicket(
    val ticketId: String = "",
    val userId: String = "",
    val userName: String = "",
    val ticketNumber: Int = 0,
    val drawnAt: Long = System.currentTimeMillis()
)

@Keep
data class LuckyDrawPool(
    val id: String = "",
    val title: String = "Daily Mega Lucky Draw",
    val prizePoolRupees: Double = 500.0,
    val totalWinners: Int = 5,
    val drawTimestamp: Long = System.currentTimeMillis() + 86400000L,
    val isCompleted: Boolean = false,
    val totalEntriesCount: Int = 120,
    val winnerTicketNumbers: List<Int> = emptyList(),
    val winnerNames: List<String> = emptyList()
)

// --- Reels Data Models ---

@Keep
enum class ReelStatus(val label: String) {
    PENDING("Pending Moderation"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    REMOVED("Removed")
}

@Keep
enum class ReelCategory(val title: String) {
    TECH("Tech & Gadgets"),
    TUTORIALS("Tutorials & How-To"),
    FINANCE("Earn & Money"),
    MOTIVATION("Motivation"),
    ENTERTAINMENT("Fun & Clips"),
    OTHER("General")
}

@Keep
enum class ReelLanguage(val label: String) {
    HINDI("Hindi"),
    ENGLISH("English"),
    TAMIL("Tamil"),
    TELUGU("Telugu"),
    BENGALI("Bengali")
}

@Keep
data class Reel(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String? = null,
    val videoUrl: String = "",
    val thumbnailUrl: String? = null,
    val caption: String = "",
    val category: String = ReelCategory.TECH.title,
    val language: String = ReelLanguage.HINDI.label,
    val durationSeconds: Int = 30,
    val status: ReelStatus = ReelStatus.PENDING,
    val rejectionReason: String? = null,
    val likesCount: Int = 0,
    val viewsCount: Int = 0,
    val isLikedByCurrentUser: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Keep
enum class ReelReportReason(val label: String) {
    NUDITY("Nudity or Explicit Content"),
    VIOLENCE("Violence or Harmful Acts"),
    COPYRIGHT("Copyright Infringement"),
    HATE_SPEECH("Hate Speech or Bullying"),
    SPAM("Spam or Misleading"),
    OTHER("Other Policy Violation")
}

@Keep
data class ReelReport(
    val id: String = "",
    val reelId: String = "",
    val reelCaption: String = "",
    val uploaderId: String = "",
    val uploaderName: String = "",
    val reporterUserId: String = "",
    val reason: ReelReportReason = ReelReportReason.SPAM,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)

@Keep
data class ReelViewRecord(
    val id: String = "",
    val userId: String = "",
    val reelId: String = "",
    val watchDurationMs: Long = 0L,
    val rewardAmount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

// --- Admin Application Data Models ---

@Keep
data class AdminActivityLog(
    val id: String = "",
    val adminId: String = "",
    val adminEmail: String = "",
    val action: String = "",
    val targetId: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Keep
enum class ReportCategory(val label: String) {
    USER("User Account"),
    REEL("Reel Content"),
    FREELANCER("Freelancer Profile/Gig"),
    JOB("Job Listing"),
    OTHER("Other Issue")
}

@Keep
enum class ReportStatus(val label: String) {
    OPEN("Open"),
    UNDER_REVIEW("Under Review"),
    RESOLVED("Resolved"),
    REJECTED("Rejected")
}

@Keep
data class CentralReport(
    val id: String = "",
    val category: ReportCategory = ReportCategory.USER,
    val title: String = "",
    val description: String = "",
    val reporterId: String = "",
    val reporterName: String = "",
    val targetId: String = "",
    val targetName: String = "",
    val status: ReportStatus = ReportStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis(),
    val adminNotes: String = ""
)

@Keep
enum class NotificationAudience(val label: String) {
    ALL_USERS("All Users"),
    PREMIUM_ONLY("Premium Users"),
    FREELANCERS_ONLY("Freelancers"),
    SPECIFIC_USER("Specific User")
}

@Keep
data class BroadcastNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val targetAudience: NotificationAudience = NotificationAudience.ALL_USERS,
    val targetUserId: String? = null,
    val sentAt: Long = System.currentTimeMillis(),
    val sentByAdminId: String = ""
)



