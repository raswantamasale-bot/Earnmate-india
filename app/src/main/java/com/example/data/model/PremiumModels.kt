package com.example.data.model

import androidx.annotation.Keep

@Keep
enum class MembershipType {
    FREE,
    PREMIUM
}

@Keep
enum class PremiumStatus {
    NONE,
    ACTIVE,
    EXPIRED
}

@Keep
data class PremiumPlan(
    val planId: String = "",
    val planName: String = "",
    val priceRupees: Double = 0.0,
    val durationDays: Int = 30,
    val currency: String = "INR",
    val active: Boolean = true,
    val features: List<String> = emptyList()
)

@Keep
data class PremiumConfig(
    val systemEnabled: Boolean = true,
    val strictAdGateForFreeUsers: Boolean = true,
    val freeGigLimit: Int = 2,
    val premiumGigLimit: Int = 10,
    val freePortfolioLimit: Int = 3,
    val premiumPortfolioLimit: Int = 15,
    val featureFlags: Map<String, Boolean> = mapOf(
        "ad_free" to true,
        "badge" to true,
        "featured_profile" to true,
        "more_gigs" to true,
        "advanced_filters" to true,
        "saved_jobs" to true,
        "proposal_templates" to true,
        "analytics" to true
    )
)

@Keep
data class PremiumMembershipHistory(
    val recordId: String = "",
    val userId: String = "",
    val userName: String = "",
    val planId: String = "",
    val planName: String = "",
    val pricePaidRupees: Double = 0.0,
    val startDate: Long = System.currentTimeMillis(),
    val expiryDate: Long = System.currentTimeMillis() + (30L * 24 * 3600 * 1000L),
    val activatedByAdminId: String? = null,
    val paymentTxnId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Keep
data class ProposalTemplate(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val templateText: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Keep
data class FreelancerAnalytics(
    val profileViews: Int = 0,
    val gigViews: Int = 0,
    val gigClicks: Int = 0,
    val applicationsSubmitted: Int = 0,
    val ordersReceived: Int = 0,
    val completedOrders: Int = 0,
    val totalEarningsRupees: Double = 0.0
)
