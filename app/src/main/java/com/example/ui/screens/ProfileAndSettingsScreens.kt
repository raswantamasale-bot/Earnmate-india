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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.EarnMateViewModel
import com.example.ui.Screen
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ProfileScreen(viewModel: EarnMateViewModel) {
    val user by viewModel.currentUser.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = ModuleColors.HomeAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = ModuleColors.HomeAccent)
                    }
                }
                Text(
                    text = "My Profile 👤",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Surface(
                color = StatusRejected.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusRejected.copy(alpha = 0.4f)),
                modifier = Modifier.clickable { viewModel.logout() }.testTag("preview_reset_session_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = "Log Out", tint = StatusRejected, modifier = Modifier.size(14.dp))
                    Text("Log Out", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusRejected)
                }
            }
        }

        // Profile Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = ModuleColors.HomeAccent.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(
                        modifier = Modifier.size(60.dp).clip(CircleShape),
                        color = ModuleColors.HomeAccent.copy(0.2f),
                        border = androidx.compose.foundation.BorderStroke(2.dp, ModuleColors.HomeAccent)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = (user?.fullName?.take(1) ?: "U").uppercase(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = ModuleColors.HomeAccent
                            )
                        }
                    }

                    Column {
                        Text(user?.fullName ?: "Rahul Sharma", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("@${user?.username ?: "user"}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(user?.email ?: "email@domain.com", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = ModuleColors.HomeAccent)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Phone", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(user?.phone ?: "+91 98765 43210", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Referral Code", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(user?.referralCode ?: "EARN9823", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ModuleColors.ReferralsAccent)
                }
                Column {
                    Text("Language", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(user?.language ?: "English", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Stats Summary
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GlassCard(modifier = Modifier.weight(1f), borderColor = ModuleColors.WalletAccent.copy(alpha = 0.3f)) {
                Text("Lifetime Earned", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("₹${String.format("%.2f", user?.totalEarned ?: 0.0)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ModuleColors.WalletAccent)
            }
            GlassCard(modifier = Modifier.weight(1f), borderColor = ModuleColors.TasksAccent.copy(alpha = 0.3f)) {
                Text("Completed Tasks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${user?.completedTasksCount ?: 0}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ModuleColors.TasksAccent)
            }
        }

        // Premium Membership Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo(Screen.Premium) },
            borderColor = Color(0xFFFFD700)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFD700).copy(alpha = 0.2f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFB300))
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("EarnMate Premium", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            if (user?.isPremiumActive == true) {
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFFD700).copy(alpha = 0.2f)) {
                                    Text("ACTIVE 👑", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text(
                            if (user?.isPremiumActive == true) "Plan: ${user?.premiumPlan} • Tap to view perks" else "Upgrade for Ad-Free & Gold Badges 👑",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFFFB300))
            }
        }

        // Settings Navigation Menu
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileMenuItem(Icons.Default.WorkspacePremium, "EarnMate Premium & Perks 👑") { viewModel.navigateTo(Screen.Premium) }
                ProfileMenuItem(Icons.Default.Settings, "Settings & Preferences") { viewModel.navigateTo(Screen.Settings) }
                ProfileMenuItem(Icons.Default.Help, "Help Center & Support Tickets") { viewModel.navigateTo(Screen.Support) }
                ProfileMenuItem(Icons.Default.Share, "Invite Friends & Earn Bonus") { viewModel.navigateTo(Screen.Referrals) }
                HorizontalDivider()
                ProfileMenuItem(Icons.Default.Logout, "Log Out & Return to Sign-In Screen", isDestructive = true) { viewModel.logout() }
            }
        }
    }

    if (showEditDialog && user != null) {
        EditProfileDialog(
            user = user!!,
            onDismiss = { showEditDialog = false },
            onSave = { name, phone, uname, lang ->
                viewModel.updateProfile(name, phone, uname, lang)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ProfileMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, isDestructive: Boolean = false, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = if (isDestructive) StatusRejected else MaterialTheme.colorScheme.onSurface)
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (isDestructive) StatusRejected else MaterialTheme.colorScheme.onSurface)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EditProfileDialog(
    user: com.example.data.model.UserProfile,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var fullName by remember { mutableStateOf(user.fullName) }
    var phone by remember { mutableStateOf(user.phone) }
    var username by remember { mutableStateOf(user.username) }
    var language by remember { mutableStateOf(user.language) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile Information") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, singleLine = true)
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, singleLine = true)
                OutlinedTextField(value = language, onValueChange = { language = it }, label = { Text("Language (English, Hindi, etc.)") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { onSave(fullName, phone, username, language) }, colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.HomeAccent)) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SettingsScreen(viewModel: EarnMateViewModel) {
    val isDark by viewModel.isDarkTheme.collectAsState()

    var showPrivacyModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { viewModel.navigateTo(Screen.Profile) }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("Settings & Preferences ⚙️", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Dark Theme", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Switch(checked = isDark, onCheckedChange = { viewModel.toggleDarkTheme() })
                }
                HorizontalDivider()
                Row(modifier = Modifier.fillMaxWidth().clickable { showPrivacyModal = true }, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Privacy Policy & Data Security", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
                HorizontalDivider()
                Row(modifier = Modifier.fillMaxWidth().clickable { viewModel.deleteAccount() }, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Delete Account", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusRejected)
                    Icon(Icons.Default.Delete, contentDescription = null, tint = StatusRejected)
                }
            }
        }
    }

    if (showPrivacyModal) {
        AlertDialog(
            onDismissRequest = { showPrivacyModal = false },
            title = { Text("Privacy & Anti-Fraud Policy") },
            text = {
                Text(
                    "EarnMate India complies with RBI & SEBI financial advertising guidelines. No user deposits are required. All rewards are disbursed upon verified completion of tasks, surveys, and promotional offers.",
                    fontSize = 12.sp
                )
            },
            confirmButton = { TextButton(onClick = { showPrivacyModal = false }) { Text("Close") } }
        )
    }
}

@Composable
fun SupportScreen(viewModel: EarnMateViewModel) {
    val tickets by viewModel.supportTickets.collectAsState()

    var subject by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Task Verification") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Help & Support Center 💬", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("Submit Support Ticket", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Describe your issue or query") }, modifier = Modifier.fillMaxWidth().height(100.dp))

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.submitSupportTicket(subject, category, message)
                    subject = ""
                    message = ""
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ModuleColors.HomeAccent)
            ) {
                Text("Submit Ticket", fontWeight = FontWeight.Bold)
            }
        }

        Text("Your Tickets", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        tickets.forEach { ticket ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(ticket.subject, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        StatusBadge(ticket.status.label, ModuleColors.HomeAccent)
                    }
                    Text(ticket.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (ticket.adminReply != null) {
                        Surface(color = ModuleColors.WalletAccent.copy(0.15f), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("Admin Reply: ${ticket.adminReply}", fontSize = 11.sp, modifier = Modifier.padding(8.dp), color = ModuleColors.WalletAccent)
                        }
                    }
                }
            }
        }
    }
}
