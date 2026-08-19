package com.example.data.firebase

import androidx.annotation.Keep

/**
 * Centralized Firebase Configuration for "EarnMate India".
 * 
 * Target Firebase Project:
 * - Project ID: earnmate-india
 * - Storage Bucket: earnmate-india.firebasestorage.app
 * - Auth Domain: earnmate-india.firebaseapp.com
 */
@Keep
data class FirebaseOptionsConfig(
    val apiKey: String = "AIzaSyDNKIZqYF1obZnbfVKOyWWjmGrzV_mFyXQ",
    val authDomain: String = "earnmate-india.firebaseapp.com",
    val projectId: String = "earnmate-india",
    val storageBucket: String = "earnmate-india.firebasestorage.app",
    val messagingSenderId: String = "910094850745",
    val appId: String = "1:910094850745:web:29d17e85efd52d3a929968",
    val measurementId: String = "G-GBVSZM7VR3"
)

object EarnMateFirebase {
    val config = FirebaseOptionsConfig()

    /**
     * Expected Firestore Collections in 'earnmate-india':
     * - 'users' -> UserProfile docs
     * - 'tasks' -> Marketplace Task docs
     * - 'task_submissions' -> User submission verifications
     * - 'offers' -> Special sponsored offers
     * - 'withdrawals' -> UPI/Bank payout requests
     * - 'notifications' -> Broadcast & user notifications
     * - 'support_tickets' -> Customer support tickets
     * - 'app_config' -> Global app settings & limits
     * - 'game_configs' -> Games & rewards config
     * - 'game_plays' -> Audit ledger for game spins/scratches
     * - 'lucky_draw_tickets' -> User entries for weekly draws
     * - 'lucky_draw_pools' -> Winner draws history
     * - 'ad_gate_logs' -> Ad gate completion/skip logs
     * - 'reels' -> Short creator reels (pending/approved/rejected)
     * - 'reel_reports' -> User reported reels for safety moderation
     * - 'reel_views' -> Watch view reward audit log
     */
    val collections = listOf(
        "users", "tasks", "task_submissions", "offers", "withdrawals",
        "notifications", "support_tickets", "app_config", "game_configs",
        "game_plays", "lucky_draw_tickets", "lucky_draw_pools",
        "ad_gate_logs", "reels", "reel_reports", "reel_views"
    )
}
