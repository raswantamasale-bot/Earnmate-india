package com.example.data.model

import androidx.annotation.Keep

@Keep
enum class FreelancerCategory(val label: String, val iconName: String) {
    THUMBNAIL_DESIGN("Thumbnail Design", "image"),
    VIDEO_EDITING("Video Editing", "movie"),
    LOGO_DESIGN("Logo Design", "brush"),
    GRAPHIC_DESIGN("Graphic Design", "palette"),
    SCRIPT_WRITING("Script Writing", "description"),
    CONTENT_WRITING("Content Writing", "edit_note"),
    DATA_ENTRY("Data Entry", "table_chart"),
    WEBSITE_DEV("Website Dev", "code"),
    SOCIAL_MEDIA("Social Media", "share"),
    AI_SERVICES("AI Services", "psychology"),
    VOICE_OVER("Voice-over", "mic"),
    TRANSLATION("Translation", "translate"),
    OTHER("Other Services", "more_horiz")
}

val FreelancerCategory.displayName: String get() = label
val FreelancerCategory.iconEmoji: String
    get() = when (this) {
        FreelancerCategory.THUMBNAIL_DESIGN -> "🖼️"
        FreelancerCategory.VIDEO_EDITING -> "🎬"
        FreelancerCategory.LOGO_DESIGN -> "🎨"
        FreelancerCategory.GRAPHIC_DESIGN -> "🖌️"
        FreelancerCategory.SCRIPT_WRITING -> "📝"
        FreelancerCategory.CONTENT_WRITING -> "✍️"
        FreelancerCategory.DATA_ENTRY -> "📊"
        FreelancerCategory.WEBSITE_DEV -> "💻"
        FreelancerCategory.SOCIAL_MEDIA -> "📱"
        FreelancerCategory.AI_SERVICES -> "🤖"
        FreelancerCategory.VOICE_OVER -> "🎙️"
        FreelancerCategory.TRANSLATION -> "🌐"
        FreelancerCategory.OTHER -> "💼"
    }

@Keep
enum class ServiceStatus(val label: String) {
    DRAFT("Draft"),
    PENDING_REVIEW("Under Review"),
    PUBLISHED("Live"),
    PAUSED("Paused"),
    REJECTED("Rejected")
}

@Keep
enum class JobStatus(val label: String) {
    OPEN("Open for Bids"),
    IN_REVIEW("In Review"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    DISPUTED("Disputed")
}

@Keep
enum class ProposalStatus(val label: String) {
    PENDING("Pending Review"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    WITHDRAWN("Withdrawn")
}

@Keep
enum class OrderStatus(val label: String) {
    ACTIVE("In Progress"),
    SUBMITTED("Work Delivered"),
    REVISION_REQUESTED("Revision Requested"),
    APPROVED("Approved & Completed"),
    CANCELLED("Cancelled"),
    DISPUTED("Disputed")
}

@Keep
enum class OrderPaymentStatus(val label: String) {
    PAYMENT_PENDING("Payment Pending"),
    PAYMENT_CONFIRMED("Funds in Escrow"),
    PAYMENT_FAILED("Payment Failed"),
    REFUND_PENDING("Refund Pending"),
    REFUNDED("Refunded")
}

@Keep
enum class DisputeStatus(val label: String) {
    OPEN("Dispute Opened"),
    UNDER_REVIEW("Under Admin Review"),
    RESOLVED_REFUND("Resolved (Refunded to Client)"),
    RESOLVED_PAY("Resolved (Paid to Freelancer)"),
    REJECTED("Dispute Dismissed")
}

@Keep
data class FreelancerProfile(
    val userId: String = "",
    val username: String = "",
    val displayName: String = "",
    val profilePhotoUrl: String = "",
    val bio: String = "",
    val skills: List<String> = emptyList(),
    val experienceLevel: String = "INTERMEDIATE",
    val languages: List<String> = listOf("Hindi", "English"),
    val portfolioLinks: List<String> = emptyList(),
    val completedJobsCount: Int = 0,
    val rating: Double = 5.0,
    val totalReviewsCount: Int = 0,
    val responseRatePercentage: Int = 100,
    val availabilityStatus: String = "AVAILABLE",
    val joinedDate: Long = System.currentTimeMillis(),
    val isFeatured: Boolean = false,
    val isPremium: Boolean = false
)

@Keep
data class FreelancerService(
    val id: String = "",
    val freelancerId: String = "",
    val freelancerName: String = "",
    val freelancerRating: Double = 5.0,
    val title: String = "",
    val description: String = "",
    val category: FreelancerCategory = FreelancerCategory.THUMBNAIL_DESIGN,
    val startingPriceRupees: Double = 199.0,
    val deliveryTimeDays: Int = 2,
    val revisionsAllowed: Int = 3,
    val skills: List<String> = emptyList(),
    val portfolioImages: List<String> = emptyList(),
    val status: ServiceStatus = ServiceStatus.PUBLISHED,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFeatured: Boolean = false,
    val isPremium: Boolean = false
)

@Keep
data class ClientJob(
    val id: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val title: String = "",
    val description: String = "",
    val category: FreelancerCategory = FreelancerCategory.THUMBNAIL_DESIGN,
    val requiredSkills: List<String> = emptyList(),
    val budgetRupees: Double = 500.0,
    val deadlineDays: Int = 3,
    val attachments: List<String> = emptyList(),
    val freelancersCount: Int = 1,
    val status: JobStatus = JobStatus.OPEN,
    val proposalsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Keep
data class JobProposal(
    val id: String = "",
    val jobId: String = "",
    val freelancerId: String = "",
    val freelancerName: String = "",
    val freelancerRating: Double = 5.0,
    val proposalMessage: String = "",
    val proposedPriceRupees: Double = 500.0,
    val estimatedDeliveryDays: Int = 3,
    val attachments: List<String> = emptyList(),
    val status: ProposalStatus = ProposalStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

@Keep
data class FreelanceOrder(
    val id: String = "",
    val jobId: String = "",
    val serviceId: String = "",
    val jobTitle: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val freelancerId: String = "",
    val freelancerName: String = "",
    val agreedPriceRupees: Double = 500.0,
    val platformCommissionPercentage: Double = 10.0,
    val freelancerEarningRupees: Double = 450.0,
    val deadlineTimestamp: Long = System.currentTimeMillis() + 3 * 86400000L,
    val revisionsAllowed: Int = 3,
    val revisionsUsed: Int = 0,
    val status: OrderStatus = OrderStatus.ACTIVE,
    val paymentStatus: OrderPaymentStatus = OrderPaymentStatus.PAYMENT_CONFIRMED,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Keep
data class OrderMessage(
    val id: String = "",
    val orderId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val messageText: String = "",
    val attachments: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Keep
data class OrderDelivery(
    val id: String = "",
    val orderId: String = "",
    val freelancerId: String = "",
    val deliveryNotes: String = "",
    val attachments: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val revisionNumber: Int = 1
)

@Keep
data class FreelancerReview(
    val id: String = "",
    val orderId: String = "",
    val serviceId: String = "",
    val freelancerId: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val rating: Int = 5,
    val reviewText: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Keep
data class FreelanceDispute(
    val id: String = "",
    val orderId: String = "",
    val reporterUserId: String = "",
    val reporterName: String = "",
    val reportedUserId: String = "",
    val reason: String = "",
    val description: String = "",
    val evidence: List<String> = emptyList(),
    val status: DisputeStatus = DisputeStatus.OPEN,
    val adminNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Keep
data class FreelanceConfig(
    val commissionPercentage: Double = 10.0,
    val minOrderValueRupees: Double = 50.0,
    val autoApproveDays: Int = 3
)
