package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OfferItem
import com.example.data.model.TransactionType
import com.example.data.model.WithdrawalMethod
import com.example.ui.EarnMateViewModel
import com.example.ui.Screen
import com.example.ui.components.AntiFraudNoticeCard
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun OffersScreen(viewModel: EarnMateViewModel) {
    val offers by viewModel.offers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = ModuleColors.TasksAccent.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocalOffer, contentDescription = null, tint = ModuleColors.TasksAccent)
                }
            }
            Text(
                text = "Special Offers Wall 🏷️",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "Complete partner promotional offers & app installs for higher reward payouts.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(offers) { offer ->
                OfferCardItem(offer = offer, onClaim = {
                    viewModel.checkAndRunAdGate(
                        targetType = "Offer",
                        targetId = offer.id,
                        targetTitle = offer.title
                    ) {
                        viewModel.showSnackbar("Offer started for '${offer.title}'. Instructions dispatched.")
                    }
                })
            }
        }
    }
}

@Composable
fun OfferCardItem(offer: OfferItem, onClaim: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = ModuleColors.TasksAccent.copy(alpha = 0.3f)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(offer.badgeText, ModuleColors.TasksAccent)
                Text(
                    text = "₹${offer.rewardRupees.toInt()}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ModuleColors.WalletAccent
                )
            }

            Text(
                text = offer.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = offer.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Provider: ${offer.providerName} • Approx ${offer.estimatedMinutes} mins",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = ModuleColors.TasksAccent
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            Text(
                text = "Terms: ${offer.termsAndConditions}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onClaim,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.TasksAccent)
            ) {
                Text("Complete Offer & Claim ₹${offer.rewardRupees.toInt()}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WalletScreen(viewModel: EarnMateViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredTransactions = transactions.filter { tx ->
        when (selectedFilter) {
            "EARNINGS" -> tx.amount > 0
            "WITHDRAWALS" -> tx.type == TransactionType.WITHDRAWAL
            "BONUSES" -> tx.type == TransactionType.DAILY_BONUS || tx.type == TransactionType.REFERRAL_REWARD
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = ModuleColors.WalletAccent.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = ModuleColors.WalletAccent)
                }
            }
            Text(
                text = "My Rewards Wallet 👛",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Emerald Financial Balance Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, ModuleColors.WalletAccent.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(ModuleColors.WalletGradient))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "AVAILABLE FOR WITHDRAWAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${String.format("%.2f", user?.availableBalance ?: 0.0)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Button(
                            onClick = { viewModel.navigateTo(Screen.Withdraw) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = ModuleColors.WalletSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Withdraw", color = ModuleColors.WalletSecondary, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Pending Review", fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f))
                            Text("₹${String.format("%.2f", user?.pendingRewards ?: 0.0)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Lifetime Earned", fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f))
                            Text("₹${String.format("%.2f", user?.totalEarned ?: 0.0)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Ledger Section Header & Filters
        Text(
            text = "Transaction History Ledger",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val filters = listOf("ALL", "EARNINGS", "WITHDRAWALS", "BONUSES")
            filters.forEach { f ->
                FilterChip(
                    selected = selectedFilter == f,
                    onClick = { selectedFilter = f },
                    label = { Text(f) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ModuleColors.WalletAccent,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredTransactions) { tx ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ModuleColors.WalletAccent.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = tx.description,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${tx.type.label} • ID: ${tx.id}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = (if (tx.amount >= 0) "+₹" else "-₹") + String.format("%.2f", Math.abs(tx.amount)),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (tx.amount >= 0) ModuleColors.WalletAccent else StatusRejected
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WithdrawScreen(viewModel: EarnMateViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val config by viewModel.appConfig.collectAsState()

    var selectedMethod by remember { mutableStateOf(WithdrawalMethod.UPI) }
    var payoutDetailsInput by remember { mutableStateOf("rahul.sharma@upi") }
    var amountInput by remember { mutableStateOf("100") }

    val amountDouble = amountInput.toDoubleOrNull() ?: 0.0
    val fee = amountDouble * (config.withdrawalFeePercentage / 100.0)
    val netAmount = (amountDouble - fee).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.Wallet) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Request Payout",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = ModuleColors.WalletAccent.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Available Balance", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${String.format("%.2f", user?.availableBalance ?: 0.0)}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ModuleColors.WalletAccent)
                }

                StatusBadge("Min ₹${config.minimumWithdrawalRupees.toInt()}", ModuleColors.WalletAccent)
            }
        }

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = ModuleColors.WalletAccent.copy(alpha = 0.25f)
        ) {
            Text("Select Payout Method", fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WithdrawalMethod.values().forEach { method ->
                    val isSelected = selectedMethod == method
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedMethod = method }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) ModuleColors.WalletAccent else MaterialTheme.colorScheme.outline.copy(0.3f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        color = if (isSelected) ModuleColors.WalletAccent.copy(0.15f) else MaterialTheme.colorScheme.surface
                    ) {
                        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = method.label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) ModuleColors.WalletAccent else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = payoutDetailsInput,
                onValueChange = { payoutDetailsInput = it },
                label = {
                    Text(if (selectedMethod == WithdrawalMethod.UPI) "Enter UPI VPA ID (e.g. mobile@upi)" else "Enter Bank Account No, IFSC Code & Holder Name")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("payout_details_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it },
                label = { Text("Withdrawal Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("withdrawal_amount_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Requested Amount:", fontSize = 12.sp)
                        Text("₹${String.format("%.2f", amountDouble)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Processing Fee (${config.withdrawalFeePercentage}%):", fontSize = 12.sp)
                        Text("₹${String.format("%.2f", fee)}", fontSize = 12.sp)
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Net Transferred Payout:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("₹${String.format("%.2f", netAmount)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ModuleColors.WalletAccent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.requestWithdrawal(selectedMethod, amountDouble, payoutDetailsInput)
                },
                enabled = amountDouble >= config.minimumWithdrawalRupees && amountDouble <= (user?.availableBalance ?: 0.0) && payoutDetailsInput.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("submit_withdrawal_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.WalletAccent)
            ) {
                Text("Confirm Withdrawal Request", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        AntiFraudNoticeCard()
    }
}
