package com.example.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AttendanceEntity
import com.example.data.local.EmployeeEntity
import com.example.data.local.FeedbackEntity
import com.example.data.local.InvoiceEntity
import com.example.data.local.JobEntity
import com.example.data.local.SalaryEntity
import com.example.data.local.SparePartEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.JobStatus
import com.example.data.model.PaymentStatus
import com.example.data.model.UserRole
import com.example.ui.OwnerTab
import com.example.ui.components.JobStatusBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusRed

@Composable
fun OwnerDashboardScreen(
    employees: List<EmployeeEntity>,
    jobs: List<JobEntity>,
    invoices: List<InvoiceEntity>,
    attendance: List<AttendanceEntity>,
    salaries: List<SalaryEntity>,
    feedbacks: List<FeedbackEntity>,
    lowStockParts: List<SparePartEntity>,
    onCreateJobClick: () -> Unit,
    onNavigateTab: (OwnerTab) -> Unit,
    onSelectJob: (JobEntity) -> Unit
) {
    // Calculated business metrics
    val totalEmployees = employees.size
    val activeTechs = employees.count { it.role == UserRole.TECHNICIAN && it.isActive }
    val activeHelpers = employees.count { it.role == UserRole.HELPER && it.isActive }

    val todayJobs = jobs.size // In demo/live, today's active pipeline
    val pendingJobs = jobs.count { it.status != JobStatus.COMPLETED && it.status != JobStatus.CANCELLED }
    val completedJobs = jobs.count { it.status == JobStatus.COMPLETED }
    val cancelledJobs = jobs.count { it.status == JobStatus.CANCELLED }

    val monthlyRevenue = invoices.sumOf { it.paidAmount }
    val pendingPayments = invoices.sumOf { it.balanceDue }
    val totalSalaryExpense = salaries.sumOf { it.paidAmount }

    val presentToday = attendance.count { it.status == AttendanceStatus.PRESENT }
    val avgRating = if (feedbacks.isNotEmpty()) feedbacks.map { it.rating }.average() else 4.9

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Hero Banner with Large "Create New Job" Button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Business Command Center",
                                color = Cyan500,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Live Service Fleet",
                                color = PureWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Surface(
                            color = Cyan500.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "$activeTechs Techs Active",
                                color = Cyan500,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onCreateJobClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("owner_create_job_hero_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+ CREATE NEW SERVICE JOB",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Low stock warning banner if any
        if (lowStockParts.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { onNavigateTab(OwnerTab.INVENTORY) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Alert", tint = StatusRed)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Inventory Alert: ${lowStockParts.size} Parts Low in Stock", fontWeight = FontWeight.Bold, color = StatusRed, fontSize = 13.sp)
                            Text("Click to view inventory and reorder spare parts", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        // Job Distribution & Service Metrics (Grid of 2 columns)
        item {
            SectionHeader(title = "Jobs & Operations Summary", actionText = "View All Jobs") {
                onNavigateTab(OwnerTab.JOBS)
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        title = "Active / Today Jobs",
                        value = "$todayJobs",
                        icon = Icons.Default.Assignment,
                        containerColor = MaterialTheme.colorScheme.surface,
                        iconColor = StatusBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(OwnerTab.JOBS) }
                    )
                    StatCard(
                        title = "In Progress",
                        value = "$pendingJobs",
                        icon = Icons.Default.HourglassEmpty,
                        containerColor = MaterialTheme.colorScheme.surface,
                        iconColor = StatusAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(OwnerTab.JOBS) }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        title = "Completed Jobs",
                        value = "$completedJobs",
                        icon = Icons.Default.CheckCircle,
                        containerColor = MaterialTheme.colorScheme.surface,
                        iconColor = StatusGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(OwnerTab.JOBS) }
                    )
                    StatCard(
                        title = "Customer Rating",
                        value = "★ ${"%.1f".format(avgRating)}",
                        icon = Icons.Default.Star,
                        containerColor = MaterialTheme.colorScheme.surface,
                        iconColor = Color(0xFFF59E0B),
                        subValue = "Based on ${feedbacks.size} reviews",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(OwnerTab.FEEDBACK) }
                    )
                }
            }
        }

        // Financial & Payroll Metrics
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(title = "Financial Overview", actionText = "Manage Invoices") {
                onNavigateTab(OwnerTab.INVOICES)
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        title = "Revenue Collected",
                        value = "₹${"%.0f".format(monthlyRevenue)}",
                        icon = Icons.Default.AttachMoney,
                        containerColor = MaterialTheme.colorScheme.surface,
                        iconColor = StatusGreen,
                        subValue = "Cleared payments",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(OwnerTab.INVOICES) }
                    )
                    StatCard(
                        title = "Pending Payments",
                        value = "₹${"%.0f".format(pendingPayments)}",
                        icon = Icons.Default.Payments,
                        containerColor = MaterialTheme.colorScheme.surface,
                        iconColor = StatusRed,
                        subValue = "Due from customers",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(OwnerTab.INVOICES) }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        title = "Salary Expenses",
                        value = "₹${"%.0f".format(totalSalaryExpense)}",
                        icon = Icons.Default.CalendarMonth,
                        containerColor = MaterialTheme.colorScheme.surface,
                        iconColor = StatusPurple,
                        subValue = "Monthly payroll disbursed",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(OwnerTab.SALARY) }
                    )
                    StatCard(
                        title = "Today's Attendance",
                        value = "$presentToday / $totalEmployees",
                        icon = Icons.Default.People,
                        containerColor = MaterialTheme.colorScheme.surface,
                        iconColor = Cyan500,
                        subValue = "Employees logged in",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(OwnerTab.ATTENDANCE) }
                    )
                }
            }
        }

        // Staff Fleet Quick Status
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(title = "Technicians & Helpers", actionText = "Manage Staff") {
                onNavigateTab(OwnerTab.EMPLOYEES)
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(employees.take(6)) { emp ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .width(160.dp)
                            .clickable { onNavigateTab(OwnerTab.EMPLOYEES) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(if (emp.role == UserRole.TECHNICIAN) Cyan500.copy(alpha = 0.2f) else StatusPurple.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (emp.role == UserRole.TECHNICIAN) Icons.Default.Engineering else Icons.Default.Handyman,
                                        contentDescription = null,
                                        tint = if (emp.role == UserRole.TECHNICIAN) Navy900 else StatusPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (emp.isActive) "Active" else "Inactive",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (emp.isActive) StatusGreen else StatusRed
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(emp.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                            Text(emp.designation, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                    }
                }
            }
        }

        // Recent Jobs Live List
        item {
            Spacer(modifier = Modifier.height(12.dp))
            SectionHeader(title = "Recent Service Pipeline", actionText = "See All") {
                onNavigateTab(OwnerTab.JOBS)
            }
        }

        items(jobs.take(4)) { job ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .clickable { onSelectJob(job) }
                    .testTag("job_card_${job.jobCode}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(job.jobCode, fontWeight = FontWeight.Bold, color = Cyan500, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PriorityBadge(job.priority)
                            JobStatusBadge(job.status)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(job.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        "${job.applianceCategory} • ${job.applianceBrand}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        job.problemDescription,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Tech: ${job.assignedTechnicianName.ifEmpty { "Unassigned" }}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Navy900
                        )
                        Text(
                            "₹${job.serviceCharge.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Cyan500
                        )
                    }
                }
            }
        }
    }
}
