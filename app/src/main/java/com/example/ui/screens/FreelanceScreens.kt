package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.EarnMateViewModel
import com.example.ui.Screen
import com.example.ui.components.ConfigurableAdBannerCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreelanceHubScreen(viewModel: EarnMateViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val profiles by viewModel.freelancerProfiles.collectAsState()
    val myProfile = profiles.find { it.userId == currentUser?.uid }
    val jobs by viewModel.freelanceJobs.collectAsState()
    val services by viewModel.freelancerServices.collectAsState()
    val orders by viewModel.freelanceOrders.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Overview, 1: Find Work, 2: Find Freelancers, 3: My Orders

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💼 EarnMate Freelance", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.PostJob) }) {
                        Icon(Icons.Default.AddBusiness, contentDescription = "Post Job", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = activeTab) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Overview") },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Jobs") },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("Services") },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = null) }
                )
                Tab(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    text = { Text("Orders (${orders.size})") },
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = null) }
                )
            }

            when (activeTab) {
                0 -> FreelanceOverviewContent(
                    viewModel = viewModel,
                    myProfile = myProfile,
                    jobsCount = jobs.size,
                    servicesCount = services.size,
                    activeOrdersCount = orders.count { it.status == OrderStatus.ACTIVE || it.status == OrderStatus.SUBMITTED },
                    onNavigateToTab = { activeTab = it }
                )
                1 -> BrowseJobsContent(viewModel = viewModel)
                2 -> BrowseServicesContent(viewModel = viewModel)
                3 -> FreelanceOrdersContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FreelanceOverviewContent(
    viewModel: EarnMateViewModel,
    myProfile: FreelancerProfile?,
    jobsCount: Int,
    servicesCount: Int,
    activeOrdersCount: Int,
    onNavigateToTab: (Int) -> Unit
) {
    var showBecomeFreelancerDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "🚀 Hire Experts or Offer Skills",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Get work done safely with EarnMate Escrow protection or earn money completing client gigs in India.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.navigateTo(Screen.PostJob) },
                            modifier = Modifier.testTag("btn_post_job_overview")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Post a Job")
                        }
                        if (myProfile != null) {
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(Screen.CreateService) }
                            ) {
                                Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Offer Service")
                            }
                        } else {
                            OutlinedButton(
                                onClick = { showBecomeFreelancerDialog = true }
                            ) {
                                Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Become Freelancer")
                            }
                        }
                    }
                }
            }
        }

        // Profile Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (myProfile != null) "⭐" else "👤",
                                        fontSize = 20.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (myProfile != null) myProfile.displayName else "Client Account",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.colorScheme.onSurface.let { MaterialTheme.typography.titleMedium }
                                )
                                Text(
                                    text = if (myProfile != null) "Verified Freelancer • Rating ${myProfile.rating}★ (${myProfile.completedJobsCount} Jobs)" else "Switch mode or create freelancer profile",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (myProfile == null) {
                            TextButton(onClick = { showBecomeFreelancerDialog = true }) {
                                Text("Register")
                            }
                        } else {
                            TextButton(onClick = {
                                viewModel.selectFreelancerUser(myProfile.userId)
                                viewModel.navigateTo(Screen.FreelancerProfileView)
                            }) {
                                Text("View Profile")
                            }
                        }
                    }

                    if (myProfile != null && myProfile.skills.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Skills:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(myProfile.skills) { skill ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(skill, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Premium Freelancer Utilities Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("👑 Premium Freelancer Toolkit", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        TextButton(onClick = { viewModel.navigateTo(Screen.Premium) }) {
                            Text("Perks Info", fontSize = 12.sp, color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.SavedJobs) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Saved Jobs", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.ProposalTemplates) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Templates", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.FreelancerAnalytics) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Insights, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Analytics", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Quick Stats Metrics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(1) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📋 $jobsCount", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Open Jobs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(2) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎨 $servicesCount", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Gigs Live", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(3) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔄 $activeOrdersCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Active Orders", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Ad Gate Banner integration
        item {
            ConfigurableAdBannerCard(enabled = true)
        }

        // Category Grid
        item {
            Text(
                text = "Browse Categories",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FreelancerCategory.values().toList().chunked(2).forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowCategories.forEach { cat ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToTab(2) },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(cat.iconEmoji, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = cat.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        if (rowCategories.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    if (showBecomeFreelancerDialog) {
        BecomeFreelancerDialog(
            onDismiss = { showBecomeFreelancerDialog = false },
            onSubmit = { bio, skills, exp, langs, portfolio ->
                viewModel.becomeFreelancer(bio, skills, exp, langs, portfolio)
                showBecomeFreelancerDialog = false
            }
        )
    }
}

@Composable
fun BrowseJobsContent(viewModel: EarnMateViewModel) {
    val jobs by viewModel.freelanceJobs.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val savedJobIds = user?.savedJobIds ?: emptyList()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<FreelancerCategory?>(null) }

    val filteredJobs = jobs.filter { job ->
        val matchesSearch = job.title.contains(searchQuery, ignoreCase = true) ||
                job.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || job.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & Filter Header
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search jobs by title or skill...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("txt_search_jobs"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All Categories") }
                    )
                }
                items(FreelancerCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                        label = { Text("${cat.iconEmoji} ${cat.displayName}") }
                    )
                }
            }
        }

        if (filteredJobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No jobs found matching your filter.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredJobs) { job ->
                    JobCardItem(
                        job = job,
                        isSaved = savedJobIds.contains(job.id),
                        onToggleSave = { viewModel.toggleSaveJob(job.id) },
                        onClick = {
                            viewModel.selectJob(job.id)
                            viewModel.navigateTo(Screen.JobDetails)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun JobCardItem(
    job: ClientJob,
    isSaved: Boolean = false,
    onToggleSave: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = job.category.iconEmoji + " " + job.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                    Text(
                        text = "Posted by ${job.clientName} • ${job.category.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (onToggleSave != null) {
                        IconButton(onClick = { onToggleSave() }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save Job",
                                tint = if (isSaved) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "₹${job.budgetRupees.toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = job.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${job.deadlineDays} Days", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${job.proposalsCount} Proposals", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    color = when (job.status) {
                        JobStatus.OPEN -> MaterialTheme.colorScheme.secondaryContainer
                        JobStatus.ASSIGNED -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = job.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BrowseServicesContent(viewModel: EarnMateViewModel) {
    val services by viewModel.freelancerServices.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<FreelancerCategory?>(null) }

    val filteredServices = services.filter { service ->
        val matchesSearch = service.title.contains(searchQuery, ignoreCase = true) ||
                service.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || service.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search services (e.g. Thumbnail, Editing)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("txt_search_services"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All Services") }
                    )
                }
                items(FreelancerCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                        label = { Text("${cat.iconEmoji} ${cat.displayName}") }
                    )
                }
            }
        }

        if (filteredServices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎨", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No services listed in this category yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredServices) { srv ->
                    ServiceCardItem(
                        service = srv,
                        onClick = {
                            viewModel.selectService(srv.id)
                            viewModel.navigateTo(Screen.ServiceDetails)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceCardItem(service: FreelancerService, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(service.category.iconEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = service.freelancerName,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("${service.freelancerRating}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Starting ₹${service.startingPriceRupees.toInt()}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = service.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = service.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⚡ Delivery: ${service.deliveryTimeDays} Day(s) • ${service.revisionsAllowed} Revisions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FreelanceOrdersContent(viewModel: EarnMateViewModel) {
    val orders by viewModel.freelanceOrders.collectAsState()

    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🛍️", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("No freelance orders yet.", fontWeight = FontWeight.Bold)
                Text("Accept a proposal or order a service to start an escrow workspace.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                OrderCardItem(
                    order = order,
                    onClick = {
                        viewModel.selectOrder(order.id)
                        viewModel.navigateTo(Screen.OrderWorkspace)
                    }
                )
            }
        }
    }
}

@Composable
fun OrderCardItem(order: FreelanceOrder, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order #${order.id}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = order.jobTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = when (order.status) {
                        OrderStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
                        OrderStatus.SUBMITTED -> MaterialTheme.colorScheme.tertiaryContainer
                        OrderStatus.APPROVED -> Color(0xFFE8F5E9)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Client: ${order.clientName} | Freelancer: ${order.freelancerName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Agreed Amount: ₹${order.agreedPriceRupees.toInt()}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Workspace Chat ➔",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun BecomeFreelancerDialog(
    onDismiss: () -> Unit,
    onSubmit: (bio: String, skills: List<String>, exp: String, langs: List<String>, portfolio: List<String>) -> Unit
) {
    var bio by remember { mutableStateOf("") }
    var skillsInput by remember { mutableStateOf("Photoshop, Thumbnails, Video Editing") }
    var portfolioLink by remember { mutableStateOf("https://") }
    var experienceLevel by remember { mutableStateOf("INTERMEDIATE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Become EarnMate Freelancer", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Fill out your freelance profile to start offering gigs and pitching for jobs.", style = MaterialTheme.typography.bodySmall)

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Short Bio / About You") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = skillsInput,
                    onValueChange = { skillsInput = it },
                    label = { Text("Skills (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = portfolioLink,
                    onValueChange = { portfolioLink = it },
                    label = { Text("Portfolio Link / Behance / Channel") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val skillList = skillsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    val portList = if (portfolioLink.isNotBlank()) listOf(portfolioLink.trim()) else emptyList()
                    onSubmit(bio, skillList, experienceLevel, listOf("Hindi", "English"), portList)
                },
                enabled = bio.isNotBlank() && skillsInput.isNotBlank()
            ) {
                Text("Activate Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(viewModel: EarnMateViewModel) {
    val selectedJobId by viewModel.selectedJobId.collectAsState()
    val jobs by viewModel.freelanceJobs.collectAsState()
    val job = jobs.find { it.id == selectedJobId }
    val proposals by viewModel.jobProposals.collectAsState()
    val jobProposals = proposals.filter { it.jobId == selectedJobId }
    val currentUser by viewModel.currentUser.collectAsState()

    var proposalMsg by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf(job?.budgetRupees?.toInt()?.toString() ?: "500") }
    var deliveryDaysInput by remember { mutableStateOf(job?.deadlineDays?.toString() ?: "2") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Details") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.FreelanceHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (job == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Job listing not found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = job.category.iconEmoji + " " + job.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "₹${job.budgetRupees.toInt()}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Posted by ${job.clientName} • Category: ${job.category.displayName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(job.description, style = MaterialTheme.typography.bodyLarge)

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Required Skills:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(job.requiredSkills) { skill ->
                                    SuggestionChip(onClick = {}, label = { Text(skill) })
                                }
                            }
                        }
                    }
                }

                // If Current user is Client -> Show proposals submitted
                if (job.clientId == currentUser?.uid) {
                    item {
                        Text(
                            text = "Submitted Proposals (${jobProposals.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (jobProposals.isEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("No proposals received yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        items(jobProposals) { prop ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(prop.freelancerName, fontWeight = FontWeight.Bold)
                                        Text("Bid: ₹${prop.proposedPriceRupees.toInt()} (${prop.estimatedDeliveryDays} Days)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(prop.proposalMessage, style = MaterialTheme.typography.bodyMedium)

                                    Spacer(modifier = Modifier.height(12.dp))
                                    if (prop.status == ProposalStatus.PENDING) {
                                        Button(
                                            onClick = { viewModel.acceptProposal(prop.id) },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Accept Proposal & Start Escrow Workspace")
                                        }
                                    } else {
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(prop.status.label, modifier = Modifier.padding(6.dp), style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Freelancer Mode -> Submit proposal
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("Submit Proposal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                                OutlinedTextField(
                                    value = proposalMsg,
                                    onValueChange = { proposalMsg = it },
                                    label = { Text("Pitch / Proposal Message") },
                                    placeholder = { Text("Explain why you are the best fit for this job...") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("txt_proposal_msg"),
                                    maxLines = 4
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = priceInput,
                                        onValueChange = { priceInput = it },
                                        label = { Text("Bid Amount (₹)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = deliveryDaysInput,
                                        onValueChange = { deliveryDaysInput = it },
                                        label = { Text("Delivery (Days)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Button(
                                    onClick = {
                                        val price = priceInput.toDoubleOrNull() ?: job.budgetRupees
                                        val days = deliveryDaysInput.toIntOrNull() ?: job.deadlineDays
                                        viewModel.submitProposal(job.id, proposalMsg, price, days, emptyList())
                                    },
                                    enabled = proposalMsg.isNotBlank(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_submit_proposal")
                                ) {
                                    Text("Send Proposal")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(viewModel: EarnMateViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(FreelancerCategory.THUMBNAIL_DESIGN) }
    var skills by remember { mutableStateOf("Photoshop, Graphic Design") }
    var budget by remember { mutableStateOf("500") }
    var deadline by remember { mutableStateOf("3") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post a Job Requirement") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.FreelanceHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Job Title") },
                    placeholder = { Text("e.g. Need 5 Tech YouTube Thumbnails") },
                    modifier = Modifier.fillMaxWidth().testTag("txt_job_title")
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Detailed Job Requirements") },
                    modifier = Modifier.fillMaxWidth().testTag("txt_job_desc"),
                    minLines = 3
                )
            }

            item {
                Text("Category:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(FreelancerCategory.values()) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.displayName) }
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = skills,
                    onValueChange = { skills = it },
                    label = { Text("Required Skills (Comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = budget,
                        onValueChange = { budget = it },
                        label = { Text("Total Budget (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = { Text("Deadline (Days)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val skillList = skills.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        viewModel.postJob(
                            title = title,
                            description = description,
                            category = category,
                            requiredSkills = skillList,
                            budgetRupees = budget.toDoubleOrNull() ?: 500.0,
                            deadlineDays = deadline.toIntOrNull() ?: 3,
                            attachments = emptyList()
                        )
                    },
                    enabled = title.isNotBlank() && description.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("btn_post_job_submit")
                ) {
                    Text("Publish Job Listing")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailsScreen(viewModel: EarnMateViewModel) {
    val selectedServiceId by viewModel.selectedServiceId.collectAsState()
    val services by viewModel.freelancerServices.collectAsState()
    val service = services.find { it.id == selectedServiceId }
    val reviews by viewModel.freelancerReviews.collectAsState()
    val serviceReviews = reviews.filter { it.serviceId == selectedServiceId || it.freelancerId == service?.freelancerId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Gig Details") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.FreelanceHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (service == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Service gig not found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(service.category.iconEmoji + " " + service.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("By ${service.freelancerName} • Rating ${service.freelancerRating}★", color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(service.description, style = MaterialTheme.typography.bodyLarge)

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Package Price", style = MaterialTheme.typography.labelMedium)
                                    Text("₹${service.startingPriceRupees.toInt()}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Button(
                                    onClick = {
                                        // Create job order from service
                                        viewModel.postJob(
                                            title = service.title,
                                            description = "Direct order for gig: ${service.title}",
                                            category = service.category,
                                            requiredSkills = service.skills,
                                            budgetRupees = service.startingPriceRupees,
                                            deadlineDays = service.deliveryTimeDays,
                                            attachments = emptyList()
                                        )
                                    }
                                ) {
                                    Text("Order Gig Now")
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Reviews & Ratings (${serviceReviews.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }

                if (serviceReviews.isEmpty()) {
                    item {
                        Text("No reviews yet for this service.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    items(serviceReviews) { rev ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(rev.clientName, fontWeight = FontWeight.Bold)
                                    Text("★".repeat(rev.rating), color = Color(0xFFFFB300))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(rev.reviewText, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServiceScreen(viewModel: EarnMateViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(FreelancerCategory.THUMBNAIL_DESIGN) }
    var price by remember { mutableStateOf("199") }
    var deliveryDays by remember { mutableStateOf("1") }
    var revisions by remember { mutableStateOf("3") }
    var skills by remember { mutableStateOf("Photoshop, Thumbnails") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publish Service Gig") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.FreelanceHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Service Title (e.g., I will edit viral reels)") },
                    modifier = Modifier.fillMaxWidth().testTag("txt_service_title")
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("What is included in this gig?") },
                    modifier = Modifier.fillMaxWidth().testTag("txt_service_desc"),
                    minLines = 3
                )
            }

            item {
                Text("Category:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(FreelancerCategory.values()) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.displayName) }
                        )
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Starting Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = deliveryDays,
                        onValueChange = { deliveryDays = it },
                        label = { Text("Delivery (Days)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = revisions,
                        onValueChange = { revisions = it },
                        label = { Text("Revisions") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = skills,
                    onValueChange = { skills = it },
                    label = { Text("Tags / Skills") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val skillList = skills.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        viewModel.createService(
                            title = title,
                            description = description,
                            category = category,
                            startingPriceRupees = price.toDoubleOrNull() ?: 199.0,
                            deliveryTimeDays = deliveryDays.toIntOrNull() ?: 1,
                            revisionsAllowed = revisions.toIntOrNull() ?: 3,
                            skills = skillList,
                            portfolioImages = emptyList()
                        )
                    },
                    enabled = title.isNotBlank() && description.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("btn_create_service_submit")
                ) {
                    Text("Publish Service")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderWorkspaceScreen(viewModel: EarnMateViewModel) {
    val selectedOrderId by viewModel.selectedOrderId.collectAsState()
    val orders by viewModel.freelanceOrders.collectAsState()
    val order = orders.find { it.id == selectedOrderId }
    val allMessages by viewModel.orderMessages.collectAsState()
    val orderMsgs = allMessages.filter { it.orderId == selectedOrderId }.sortedBy { it.timestamp }
    val currentUser by viewModel.currentUser.collectAsState()

    var chatInput by remember { mutableStateOf("") }
    var deliveryNotesInput by remember { mutableStateOf("") }
    var revisionNotesInput by remember { mutableStateOf("") }
    var reviewText by remember { mutableStateOf("") }
    var ratingStars by remember { mutableStateOf(5) }

    var showDeliveryDialog by remember { mutableStateOf(false) }
    var showRevisionDialog by remember { mutableStateOf(false) }
    var showDisputeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(order?.jobTitle ?: "Order Workspace", fontWeight = FontWeight.Bold)
                        Text("Escrow Protection Active", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.FreelanceHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDisputeDialog = true }) {
                        Icon(Icons.Default.Gavel, contentDescription = "Open Dispute", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Order not found.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Workspace Header Banner
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Agreed Escrow Amount: ₹${order.agreedPriceRupees.toInt()}", fontWeight = FontWeight.Bold)
                            Text("Revisions Used: ${order.revisionsUsed}/${order.revisionsAllowed}", style = MaterialTheme.typography.bodySmall)
                        }
                        Surface(
                            color = when (order.status) {
                                OrderStatus.APPROVED -> Color(0xFFE8F5E9)
                                OrderStatus.SUBMITTED -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.primaryContainer
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = order.status.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Chat Messages Feed
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(orderMsgs) { msg ->
                        val isMe = msg.senderId == currentUser?.uid
                        val isSystem = msg.senderId == "system"

                        if (isSystem) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = msg.messageText,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    color = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(msg.senderName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(msg.messageText, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }

                // Workspace Actions Bar based on user role and order state
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (order.status == OrderStatus.ACTIVE || order.status == OrderStatus.REVISION_REQUESTED) {
                            if (order.freelancerId == currentUser?.uid) {
                                Button(
                                    onClick = { showDeliveryDialog = true },
                                    modifier = Modifier.fillMaxWidth().testTag("btn_deliver_work")
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Deliver Final Work")
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        if (order.status == OrderStatus.SUBMITTED) {
                            if (order.clientId == currentUser?.uid) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { showRevisionDialog = true },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Request Revision")
                                    }
                                    Button(
                                        onClick = { viewModel.approveOrderDelivery(order.id) },
                                        modifier = Modifier.weight(1f).testTag("btn_approve_delivery")
                                    ) {
                                        Text("Approve & Release ₹")
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        if (order.status == OrderStatus.APPROVED && order.clientId == currentUser?.uid) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Leave Freelancer Review:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                    Row {
                                        (1..5).forEach { star ->
                                            Text(
                                                text = if (star <= ratingStars) "★" else "☆",
                                                fontSize = 20.sp,
                                                color = Color(0xFFFFB300),
                                                modifier = Modifier.clickable { ratingStars = star }
                                            )
                                        }
                                    }
                                    OutlinedTextField(
                                        value = reviewText,
                                        onValueChange = { reviewText = it },
                                        placeholder = { Text("Write feedback...") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(
                                        onClick = { viewModel.submitFreelancerReview(order.id, ratingStars, reviewText) },
                                        enabled = reviewText.isNotBlank()
                                    ) {
                                        Text("Submit Review")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Chat Input Bar
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text("Type workspace message...") },
                                modifier = Modifier.weight(1f).testTag("txt_chat_input"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = {
                                    if (chatInput.isNotBlank()) {
                                        viewModel.sendOrderMessage(order.id, chatInput)
                                        chatInput = ""
                                    }
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeliveryDialog && order != null) {
        AlertDialog(
            onDismissRequest = { showDeliveryDialog = false },
            title = { Text("Submit Work Delivery") },
            text = {
                OutlinedTextField(
                    value = deliveryNotesInput,
                    onValueChange = { deliveryNotesInput = it },
                    label = { Text("Delivery Notes / Drive Link") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.submitOrderDelivery(order.id, deliveryNotesInput, emptyList())
                    showDeliveryDialog = false
                }) {
                    Text("Deliver Work")
                }
            },
            dismissButton = { TextButton(onClick = { showDeliveryDialog = false }) { Text("Cancel") } }
        )
    }

    if (showRevisionDialog && order != null) {
        AlertDialog(
            onDismissRequest = { showRevisionDialog = false },
            title = { Text("Request Work Revision") },
            text = {
                OutlinedTextField(
                    value = revisionNotesInput,
                    onValueChange = { revisionNotesInput = it },
                    label = { Text("Specify Changes Needed") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.requestOrderRevision(order.id, revisionNotesInput)
                    showRevisionDialog = false
                }) {
                    Text("Send Request")
                }
            },
            dismissButton = { TextButton(onClick = { showRevisionDialog = false }) { Text("Cancel") } }
        )
    }

    if (showDisputeDialog && order != null) {
        var disputeReason by remember { mutableStateOf("Work Quality Issue") }
        var disputeDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDisputeDialog = false },
            title = { Text("Open Order Dispute") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("EarnMate moderators will review workspace logs to resolve payment.", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = disputeReason,
                        onValueChange = { disputeReason = it },
                        label = { Text("Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = disputeDesc,
                        onValueChange = { disputeDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.openDispute(order.id, disputeReason, disputeDesc, emptyList())
                    showDisputeDialog = false
                }) {
                    Text("Submit Case")
                }
            },
            dismissButton = { TextButton(onClick = { showDisputeDialog = false }) { Text("Cancel") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreelancerProfileScreen(viewModel: EarnMateViewModel) {
    val selectedUserId by viewModel.selectedFreelancerUserId.collectAsState()
    val profiles by viewModel.freelancerProfiles.collectAsState()
    val profile = profiles.find { it.userId == selectedUserId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Freelancer Profile") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.FreelanceHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (profile == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Profile not found.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(profile.displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("@${profile.username} • Joined ${SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(Date(profile.joinedDate))}", color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(profile.bio, style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column {
                                Text("Rating", style = MaterialTheme.typography.labelSmall)
                                Text("★ ${profile.rating}", fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                            }
                            Column {
                                Text("Completed", style = MaterialTheme.typography.labelSmall)
                                Text("${profile.completedJobsCount} Jobs", fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Response", style = MaterialTheme.typography.labelSmall)
                                Text("${profile.responseRatePercentage}%", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Skills:", fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(profile.skills) { skill ->
                                SuggestionChip(onClick = {}, label = { Text(skill) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFreelancerScreen(viewModel: EarnMateViewModel) {
    val disputes by viewModel.freelanceDisputes.collectAsState()
    val config by viewModel.freelanceConfig.collectAsState()

    var commInput by remember { mutableStateOf(config.commissionPercentage.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Freelance Moderation & Disputes") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.AdminDashboard) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Platform Commission Configuration", fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = commInput,
                                onValueChange = { commInput = it },
                                label = { Text("Commission %") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                viewModel.adminUpdateFreelanceConfig(
                                    commissionPercentage = commInput.toDoubleOrNull() ?: 10.0,
                                    minOrderValueRupees = 50.0,
                                    autoApproveDays = 3
                                )
                            }) {
                                Text("Save")
                            }
                        }
                    }
                }
            }

            item {
                Text("Open Dispute Cases (${disputes.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            if (disputes.isEmpty()) {
                item {
                    Text("No disputes logged.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(disputes) { disp ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Dispute #${disp.id} (Order #${disp.orderId})", fontWeight = FontWeight.Bold)
                            Text("Reporter: ${disp.reporterName} | Reason: ${disp.reason}")
                            Text("Details: ${disp.description}", style = MaterialTheme.typography.bodySmall)

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.adminResolveDispute(disp.id, "REFUND", "Refunded to client by admin.") },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Refund Client")
                                }
                                Button(
                                    onClick = { viewModel.adminResolveDispute(disp.id, "PAY_FREELANCER", "Pay freelancer approved by admin.") }
                                ) {
                                    Text("Pay Freelancer")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseJobsScreen(viewModel: EarnMateViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse Client Jobs 💼") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.FreelanceHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.PostJob) }) {
                        Icon(Icons.Default.Add, contentDescription = "Post Job")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            BrowseJobsContent(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseServicesScreen(viewModel: EarnMateViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore Freelancer Services 🎨") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.FreelanceHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.CreateService) }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Service")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            BrowseServicesContent(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreelanceOrdersScreen(viewModel: EarnMateViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders & Workspaces 📦") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.FreelanceHub) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            FreelanceOrdersContent(viewModel = viewModel)
        }
    }
}

@Composable
fun AdminFreelanceScreen(viewModel: EarnMateViewModel) {
    AdminFreelancerScreen(viewModel = viewModel)
}

