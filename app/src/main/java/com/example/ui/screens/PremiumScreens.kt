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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProposalTemplate
import com.example.ui.EarnMateViewModel
import com.example.ui.Screen
import com.example.ui.components.GlassCard
import com.example.ui.components.PremiumGoldBadge
import com.example.ui.components.PremiumUpgradeDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatDate(ms: Long): String {
    if (ms <= 0) return "N/A"
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(ms))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(viewModel: EarnMateViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val plans by viewModel.premiumPlans.collectAsState()

    var showPaymentComingSoonDialog by remember { mutableStateOf(false) }
    var selectedPlanName by remember { mutableStateOf("") }

    val isPremium = user?.isPremiumActive == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EarnMate Premium 👑", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Card Header
            if (isPremium) {
                // PREMIUM Active Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = Color(0xFFFFD700)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.WorkspacePremium,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "EarnMate Premium",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFFFFD700)
                                    )
                                    Text(
                                        text = "Status: ACTIVE 👑",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            PremiumGoldBadge(text = "ACTIVE")
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Active Plan", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(user?.premiumPlan?.ifBlank { "VIP Premium" } ?: "VIP Premium", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Column {
                                Text("Start Date", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(formatDate(user?.premiumStartDate ?: 0L), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Column {
                                Text("Expiry Date", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(formatDate(user?.premiumExpiryDate ?: 0L), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFFB300))
                            }
                        }

                        val remainingDays = remember(user?.premiumExpiryDate) {
                            val diff = (user?.premiumExpiryDate ?: 0L) - System.currentTimeMillis()
                            if (diff > 0) (diff / 86400000L).toInt() else 0
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFD700).copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                                    Text("Days Remaining", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                                Text("$remainingDays Days Left", fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                            }
                        }

                        // Premium Shortcuts
                        Text("Your Exclusive Premium Utilities:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.navigateTo(Screen.SavedJobs) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("Saved Jobs", fontSize = 12.sp)
                                }
                            }
                            Button(
                                onClick = { viewModel.navigateTo(Screen.ProposalTemplates) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("Templates", fontSize = 12.sp)
                                }
                            }
                        }
                        Button(
                            onClick = { viewModel.navigateTo(Screen.FreelancerAnalytics) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300), contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Insights, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text("View Freelancer Analytics & Insights 📊", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // FREE Membership Banner
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                            Column {
                                Text("Current Membership: FREE", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Basic features active", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text("FREE PLAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }

            // Benefits Grid / List
            Text("EarnMate Premium Benefits ⭐", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            val benefits = listOf(
                Triple("🚫 Ad-Free Experience", "Automatically bypass all normal advertisements & ad gates", Icons.Default.Block),
                Triple("⭐ Gold PREMIUM Badge", "Display an official Gold Badge on profile and listed gigs", Icons.Default.Star),
                Triple("🔥 Featured Priority", "Rank higher on Browse Services and Freelancer Directory", Icons.Default.TrendingUp),
                Triple("📦 10 Gigs Capacity", "List up to 10 active service offerings (5x FREE limit)", Icons.Default.AddBusiness),
                Triple("🔍 Advanced Job Filters", "Filter jobs by budget range, skills, category & deadline", Icons.Default.FilterList),
                Triple("🔖 Saved Jobs Locker", "Save interesting jobs and manage applications seamlessly", Icons.Default.Bookmark),
                Triple("📄 Proposal Templates", "Store & insert high-converting proposal templates", Icons.Default.Description),
                Triple("📊 Freelancer Analytics", "Access profile views, gig clicks, order conversion trends", Icons.Default.Insights)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                benefits.forEach { (title, desc, icon) ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFD700).copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(icon, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                                }
                            }
                            Column {
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Available Plans Section
            if (!isPremium) {
                Text("Select a Premium Plan 💎", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                plans.filter { it.active }.forEach { plan ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPlanName = plan.planName
                                showPaymentComingSoonDialog = true
                            },
                        borderColor = Color(0xFFFFD700).copy(alpha = 0.5f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(plan.planName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFFFB300))
                                    Text("${plan.durationDays} Days Duration", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    "₹${plan.priceRupees.toInt()}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            HorizontalDivider()
                            plan.features.take(4).forEach { feat ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                    Text(feat, fontSize = 12.sp)
                                }
                            }
                            Button(
                                onClick = {
                                    selectedPlanName = plan.planName
                                    showPaymentComingSoonDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300), contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp).testTag("plan_upgrade_${plan.planId}")
                            ) {
                                Text("Upgrade - ₹${plan.priceRupees.toInt()}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPaymentComingSoonDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentComingSoonDialog = false },
            icon = {
                Icon(Icons.Default.Payments, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(40.dp))
            },
            title = { Text("Payment Provider Integration", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Payment integration coming soon! 💳")
                    Text(
                        "The payment gateway for $selectedPlanName is prepared in test mode. Admin can grant instant testing memberships from the Admin Console.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showPaymentComingSoonDialog = false }) {
                    Text("Got It")
                }
            }
        )
    }
}

/**
 * Saved Jobs Screen for Premium Users.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedJobsScreen(viewModel: EarnMateViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val allJobs by viewModel.freelanceJobs.collectAsState()

    val savedJobIds = user?.savedJobIds ?: emptyList()
    val savedJobs = remember(savedJobIds, allJobs) {
        allJobs.filter { savedJobIds.contains(it.id) }
    }

    var searchQuery by remember { mutableStateOf("") }

    val filteredJobs = remember(savedJobs, searchQuery) {
        if (searchQuery.isBlank()) savedJobs
        else savedJobs.filter { it.title.contains(searchQuery, ignoreCase = true) || it.category.label.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Jobs Locker 🔖", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.FreelanceHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search saved jobs...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (filteredJobs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("No saved jobs found", fontWeight = FontWeight.Bold)
                        Text("Tap the bookmark icon on Browse Jobs to save jobs here.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredJobs, key = { it.id }) { job ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                                        Text(job.category.label, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    IconButton(onClick = { viewModel.toggleSaveJob(job.id) }) {
                                        Icon(Icons.Default.Bookmark, contentDescription = "Unsave", tint = Color(0xFFFFB300))
                                    }
                                }
                                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(job.description, fontSize = 12.sp, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Budget: ₹${job.budgetRupees.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Button(
                                        onClick = {
                                            viewModel.selectJob(job.id)
                                            viewModel.navigateTo(Screen.JobDetails)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Apply Now")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Proposal Templates Manager for Premium Users.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalTemplatesScreen(viewModel: EarnMateViewModel) {
    val templates by viewModel.proposalTemplates.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingTemplate by remember { mutableStateOf<ProposalTemplate?>(null) }

    var inputTitle by remember { mutableStateOf("") }
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proposal Templates 📄", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.FreelanceHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    inputTitle = ""
                    inputText = ""
                    editingTemplate = null
                    showAddDialog = true
                },
                containerColor = Color(0xFFFFB300),
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Template")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Saved Proposal Templates (${templates.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            if (templates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No proposal templates created yet. Tap + to add one!")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(templates, key = { it.id }) { tpl ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(tpl.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Row {
                                        IconButton(onClick = { viewModel.duplicateProposalTemplate(tpl) }) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = {
                                            editingTemplate = tpl
                                            inputTitle = tpl.title
                                            inputText = tpl.templateText
                                            showAddDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }
                                        IconButton(onClick = { viewModel.deleteProposalTemplate(tpl.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                                HorizontalDivider()
                                Text(tpl.templateText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (editingTemplate == null) "Create Proposal Template" else "Edit Template") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = { Text("Template Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Proposal Content") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingTemplate == null) {
                            viewModel.addProposalTemplate(inputTitle, inputText)
                        } else {
                            viewModel.updateProposalTemplate(editingTemplate!!.id, inputTitle, inputText)
                        }
                        showAddDialog = false
                    }
                ) {
                    Text("Save Template")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Freelancer Analytics & Performance Insights Screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreelancerAnalyticsScreen(viewModel: EarnMateViewModel) {
    val analytics = remember { viewModel.getFreelancerAnalytics() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Freelancer Analytics 📊", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.FreelanceHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Color(0xFFFFD700)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Total Freelance Revenue", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "₹${"%.2f".format(analytics.totalEarningsRupees)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Completed Orders: ${analytics.completedOrders}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text("Key Metric Cards", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            val metrics = listOf(
                Pair("Profile Views", analytics.profileViews.toString()),
                Pair("Gig Views", analytics.gigViews.toString()),
                Pair("Gig Clicks", analytics.gigClicks.toString()),
                Pair("Proposals Submitted", analytics.applicationsSubmitted.toString()),
                Pair("Orders Received", analytics.ordersReceived.toString()),
                Pair("Orders Completed", analytics.completedOrders.toString())
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                metrics.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { (label, value) ->
                            GlassCard(modifier = Modifier.weight(1f)) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                                }
                            }
                        }
                    }
                }
            }

            Text("Performance Insights", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFFFFB300))
                        Text("Conversion Rate", fontWeight = FontWeight.Bold)
                    }
                    val conv = if (analytics.gigViews > 0) (analytics.ordersReceived.toDouble() / analytics.gigViews * 100) else 0.0
                    Text("Gig View to Order Conversion: ${"%.1f".format(conv)}%", fontSize = 13.sp)
                }
            }
        }
    }
}
