package com.example.ui.screens.owner

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.EmployeeEntity
import com.example.data.local.JobEntity
import com.example.data.model.JobPriority
import com.example.data.model.JobStatus
import com.example.ui.components.JobStatusBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.PureWhite

@Composable
fun JobManagementScreen(
    jobs: List<JobEntity>,
    employees: List<EmployeeEntity>,
    onCreateJobClick: () -> Unit,
    onStatusChange: (jobId: Long, newStatus: JobStatus) -> Unit,
    onGenerateInvoice: (JobEntity) -> Unit,
    onAddFeedback: (JobEntity) -> Unit,
    onDeleteJob: (JobEntity) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<JobStatus?>(null) }

    val filteredJobs = jobs.filter { job ->
        val matchesSearch = searchQuery.isBlank() ||
                job.customerName.contains(searchQuery, ignoreCase = true) ||
                job.customerMobile.contains(searchQuery) ||
                job.jobCode.contains(searchQuery, ignoreCase = true) ||
                job.applianceCategory.contains(searchQuery, ignoreCase = true) ||
                job.assignedTechnicianName.contains(searchQuery, ignoreCase = true)

        val matchesStatus = selectedStatusFilter == null || job.status == selectedStatusFilter
        matchesSearch && matchesStatus
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateJobClick,
                containerColor = Cyan500,
                contentColor = Navy900,
                modifier = Modifier.testTag("fab_create_job")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Job")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by customer, phone, job ID, or technician") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("job_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Status Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    StatusChip(
                        title = "All (${jobs.size})",
                        selected = selectedStatusFilter == null,
                        onClick = { selectedStatusFilter = null }
                    )
                }
                items(JobStatus.values()) { status ->
                    val count = jobs.count { it.status == status }
                    StatusChip(
                        title = "${status.label} ($count)",
                        selected = selectedStatusFilter == status,
                        onClick = { selectedStatusFilter = status }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Job List
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredJobs.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No service jobs found matching criteria", fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Create a new job using the + button below", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }

                items(filteredJobs, key = { it.id }) { job ->
                    JobItemCard(
                        job = job,
                        onCallCustomer = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${job.customerMobile}"))
                            context.startActivity(intent)
                        },
                        onStatusChange = { newStatus -> onStatusChange(job.id, newStatus) },
                        onGenerateInvoice = { onGenerateInvoice(job) },
                        onAddFeedback = { onAddFeedback(job) },
                        onDelete = { onDeleteJob(job) }
                    )
                }
            }
        }
    }
}

@Composable
private fun JobItemCard(
    job: JobEntity,
    onCallCustomer: () -> Unit,
    onStatusChange: (JobStatus) -> Unit,
    onGenerateInvoice: () -> Unit,
    onAddFeedback: () -> Unit,
    onDelete: () -> Unit
) {
    var statusMenuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("job_item_${job.jobCode}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = job.jobCode,
                        fontWeight = FontWeight.ExtraBold,
                        color = Cyan500,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PriorityBadge(job.priority)
                }

                Box {
                    Surface(
                        modifier = Modifier.clickable { statusMenuExpanded = true },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        JobStatusBadge(job.status)
                    }

                    DropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        for (st in JobStatus.values()) {
                            DropdownMenuItem(
                                text = { Text(st.label) },
                                onClick = {
                                    onStatusChange(st)
                                    statusMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Customer & Appliance Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.customerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${job.applianceCategory} • ${job.applianceBrand} ${if (job.modelNumber.isNotEmpty()) "(${job.modelNumber})" else ""}",
                        color = Cyan500,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Address: ${job.customerAddress}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onCallCustomer,
                    modifier = Modifier.testTag("call_customer_${job.jobCode}")
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = "Call Customer", tint = Color(0xFF10B981))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Problem: ${job.problemDescription}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (job.statusNote.isNotEmpty()) {
                        Text(
                            text = "Status Note: ${job.statusNote}",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                    if (job.workReportNotes.isNotEmpty()) {
                        Text(
                            text = "Technician Report: ${job.workReportNotes}",
                            fontSize = 11.sp,
                            color = Color(0xFF0369A1)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Assigned Crew & Fee
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Lead Tech: ${job.assignedTechnicianName.ifEmpty { "Unassigned" }}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Navy900
                    )
                    if (job.assignedHelperName.isNotEmpty()) {
                        Text(
                            text = "Helper: ${job.assignedHelperName}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Fee: ₹${job.serviceCharge.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Cyan500
                    )
                    Text(
                        text = "Date: ${job.serviceDate}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onGenerateInvoice,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Invoice", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onAddFeedback,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFF59E0B))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Review", fontSize = 12.sp)
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) Cyan500 else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            color = if (selected) Navy900 else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
