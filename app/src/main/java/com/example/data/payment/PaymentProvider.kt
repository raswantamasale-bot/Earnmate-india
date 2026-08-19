package com.example.data.payment

import com.example.data.model.WithdrawalMethod
import com.example.data.model.WithdrawalRequest

sealed class PaymentResult {
    data class Success(val transactionRef: String, val message: String) : PaymentResult()
    data class Pending(val referenceId: String, val message: String) : PaymentResult()
    data class Failure(val errorReason: String) : PaymentResult()
}

/**
 * PaymentProvider abstraction to decouple payouts (UPI / Bank transfer)
 * from specific payment gateway vendors (Cashfree, Razorpay, Decentro, etc.)
 */
interface PaymentProvider {
    val providerName: String
    fun isMethodSupported(method: WithdrawalMethod): Boolean
    suspend fun initiatePayout(request: WithdrawalRequest): PaymentResult
    suspend fun checkStatus(referenceId: String): PaymentResult
}

class StandardIndianPayoutProvider : PaymentProvider {
    override val providerName: String = "EarnMate Direct Payout Abstraction v1.0"

    override fun isMethodSupported(method: WithdrawalMethod): Boolean = true

    override suspend fun initiatePayout(request: WithdrawalRequest): PaymentResult {
        // Backend validation: Verify request bounds
        if (request.amountRupees <= 0) {
            return PaymentResult.Failure("Invalid withdrawal amount.")
        }
        if (request.payoutDetails.isBlank()) {
            return PaymentResult.Failure("Missing payout details (UPI ID or Bank Account info).")
        }

        // Simulate gateway reference assignment and processing
        val txRef = "EM-PAY-" + System.currentTimeMillis() % 10000000 + "-" + (1000..9999).random()
        return PaymentResult.Pending(
            referenceId = txRef,
            message = "Payout initiated under reference $txRef. Transfer will complete via bank batch processing within 24 hours."
        )
    }

    override suspend fun checkStatus(referenceId: String): PaymentResult {
        return PaymentResult.Success(
            transactionRef = referenceId,
            message = "Payout completed successfully."
        )
    }
}
