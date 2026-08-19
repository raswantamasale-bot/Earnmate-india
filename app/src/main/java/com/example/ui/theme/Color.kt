package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// EarnMate India Core Brand Palette (Saffron, Green, Navy Blue, Gold, White)
val SaffronPrimary = Color(0xFFFF9933)      // India Saffron
val SaffronVariant = Color(0xFFE67E22)      // Warm Saffron Accent
val IndiaGreen = Color(0xFF138808)          // India Green
val IndiaGreenVariant = Color(0xFF0E6606)   // Rich Green Accent
val NavyBlue = Color(0xFF0A1931)            // Core Deep Navy Blue
val NavyBlueSurface = Color(0xFF132238)     // Card Surface Navy
val NavyBlueVariant = Color(0xFF1C2D4B)     // Surface Variant Navy
val PremiumGold = Color(0xFFFFD700)         // Gold for Premium
val GoldVariant = Color(0xFFE5A900)         // Gold Gradient Accent

val BrandPrimary = SaffronPrimary
val BrandPrimaryVariant = SaffronVariant
val BrandSecondary = IndiaGreen
val BrandAccent = PremiumGold
val BrandWarning = Color(0xFFF59E0B)
val BrandError = Color(0xFFEF4444)

// Module Visual Identity Color Tokens
object ModuleColors {
    // 1. HOME / DASHBOARD (Saffron & Navy)
    val HomeAccent = SaffronPrimary
    val HomeSecondary = Color(0xFFFF8000)
    val HomeGradient = listOf(SaffronPrimary, Color(0xFFFF6F00))

    // 2. EARNINGS / WALLET (India Green)
    val WalletAccent = IndiaGreen
    val WalletSecondary = Color(0xFF0E6606)
    val WalletGradient = listOf(IndiaGreen, Color(0xFF0A4F05))

    // 3. TASKS (Saffron / Warm Orange)
    val TasksAccent = Color(0xFFFF8C00)
    val TasksSecondary = Color(0xFFE67E22)
    val TasksGradient = listOf(Color(0xFFFF8C00), Color(0xFFD35400))

    // 4. DAILY BONUS / STREAK (Gold & Warm Saffron)
    val DailyBonusAccent = PremiumGold
    val DailyBonusSecondary = Color(0xFFDAA520)
    val DailyBonusGradient = listOf(PremiumGold, Color(0xFFDAA520))

    // 5. GAMES (Purple / Magenta / Indigo)
    val GamesAccent = Color(0xFF8E44AD)
    val GamesSecondary = Color(0xFF2980B9)
    val GamesGradient = listOf(Color(0xFF8E44AD), Color(0xFF2980B9))

    // 6. REFERRALS (Teal & India Green)
    val ReferralsAccent = Color(0xFF16A085)
    val ReferralsSecondary = IndiaGreen
    val ReferralsGradient = listOf(Color(0xFF16A085), IndiaGreen)

    // 7. REELS (Coral / Saffron Crimson)
    val ReelsAccent = Color(0xFFE74C3C)
    val ReelsSecondary = Color(0xFFC0392B)
    val ReelsGradient = listOf(Color(0xFFE74C3C), Color(0xFF8E44AD))

    // 8. CREATOR SECTION (Violet / Gold)
    val CreatorAccent = Color(0xFF9B59B6)
    val CreatorSecondary = PremiumGold

    // 9. SURVEYS / OPPORTUNITIES (Gold)
    val OpportunitiesAccent = PremiumGold
    val OpportunitiesSecondary = Color(0xFFD4AC0D)

    // 10. ADMIN PANEL (Navy / Blue)
    val AdminAccent = Color(0xFF2980B9)
    val AdminSurface = NavyBlue
    val AdminCard = NavyBlueSurface

    // 11. PROFILE / SETTINGS (Navy / Slate)
    val ProfileAccent = Color(0xFF34495E)
    val ProfileSecondary = Color(0xFF2C3E50)

    // 12. FREELANCER MARKETPLACE (Navy / Gold)
    val FreelanceAccent = Color(0xFF2980B9)
    val FreelanceSecondary = PremiumGold
    val FreelanceGradient = listOf(NavyBlueVariant, Color(0xFF1F3A60))

    // 13. PREMIUM SYSTEM (Gold)
    val PremiumAccent = PremiumGold
    val PremiumSecondary = GoldVariant
    val PremiumGradient = listOf(Color(0xFFFFD700), Color(0xFFFFA500), Color(0xFFDAA520))
}

// Dark Palette (Navy Blue background depth with Gold/Saffron/Green accents)
val DarkBackground = NavyBlue
val DarkSurface = NavyBlueSurface
val DarkSurfaceVariant = NavyBlueVariant
val DarkBorder = Color(0xFF2A3F60)
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFFB0C4DE)

// Light Palette (Warm light background with Navy/Saffron card accents)
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEDF2F7)
val LightBorder = Color(0xFFCBD5E1)
val LightTextPrimary = Color(0xFF0A1931)
val LightTextSecondary = Color(0xFF4A5568)

// Status colors
val StatusPending = Color(0xFFFF9933)
val StatusApproved = IndiaGreen
val StatusRejected = Color(0xFFE74C3C)
val StatusProcessing = Color(0xFF3498DB)
