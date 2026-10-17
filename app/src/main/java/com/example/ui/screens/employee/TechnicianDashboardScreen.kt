package com.example.ui.screens.employee

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.AttendanceEntity
import com.example.data.local.JobEntity
import com.example.data.local.SalaryEntity
import com.example.data.local.SparePartEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.AuthSession
import com.example.data.model.JobStatus
import com.example.data.model.UsedPartItem
import com.example.data.model.UserRole
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
import com.example.ui.theme.StatusRed

@Composable
fun TechnicianDashboardScreen(
    session: AuthSession,
    myJobs: List<JobEntity>,
    myAttendance: List<AttendanceEntity>,
    mySalary: List<SalaryEntity>,
    allParts: List<SparePartEntity>,
    onUpdateStatus: (jobId: Long, newStatus: JobStatus, statusNote: String, parts: List<UsedPartItem>?, notes: String) -> Unit,
    onPunchAttendance: (status: AttendanceStatus, overtimeHours: Double, notes: String) -> Unit,
    onCreateDraftInvoice: (JobEntity) -> Unit
) {
    val context = LocalContext.current
    var selectedJobForAction by remember { mutableStateOf<JobEntity?>(null) }
    var showWorkReportDialog by remember { mutableStateOf(false) }

    val isHelper = session.role == UserRole.HELPER
    val assignedCount = myJobs.size
    val pendingCount = myJobs.count { it.status != JobStatus.COMPLETED && it.status != JobStatus.CANCELLED }
    val completedCount = myJobs.count { it.status == JobStatus.COMPLETED }

    val todayPunched = myAttendance.firstOrNull() // today or recent
    val currentMonthSalary = mySalary.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Welcome & Punch Attendance Quick Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${session.role.displayName.uppercase()} WORKSPACE",
                                color = Cyan500,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Namaste, ${session.employeeName}",
                                color = PureWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = Cyan500.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (todayPunched != null) "Checked In (${todayPunched.checkInTime})" else "Not Punched",
                                color = if (todayPunched != null) StatusGreen else StatusAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onPunchAttendance(AttendanceStatus.PRESENT, 0.0, "Morning Check-In") },
                            modifier = Modifier.weight(1f).height(46.dp).testTag("tech_punch_in_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Punch In", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onPunchAttendance(AttendanceStatus.PRESENT, 1.5, "Evening Check-Out & Overtime") },
                            modifier = Modifier.weight(1f).height(46.dp).testTag("tech_punch_out_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp), tint = PureWhite)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Punch Out", color = PureWhite, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Field Metrics Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Assigned Jobs",
                    value = "$assignedCount",
                    icon = Icons.Default.Assignment,
                    containerColor = MaterialTheme.colorScheme.surface,
                    iconColor = StatusBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Pending Work",
                    value = "$pendingCount",
                    icon = Icons.Default.Schedule,
                    containerColor = MaterialTheme.colorScheme.surface,
                    iconColor = StatusAmber,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Done",
                    value = "$completedCount",
                    icon = Icons.Default.CheckCircle,
                    containerColor = MaterialTheme.colorScheme.surface,
                    iconColor = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Confidential Personal Salary Peek (Strictly isolated to logged in staff only)
        if (currentMonthSalary != null) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("My Confidential Monthly Wages (${currentMonthSalary.monthYear})", fontSize = 11.sp, color = Color.Gray)
                            Text("Net Pay: ₹${"%.0f".format(currentMonthSalary.netSalary)}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Cyan500)
                        }
                        Surface(
                            color = Color(0xFFD1FAE5),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Status: ${currentMonthSalary.status}",
                                color = StatusGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Assigned Jobs List
        item {
            Spacer(modifier = Modifier.height(14.dp))
            SectionHeader(title = "My Assigned Service Jobs")
        }

        if (myJobs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No jobs currently assigned to you.", fontWeight = FontWeight.SemiBold)
                        Text("New jobs assigned by company owner will appear here in real-time.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        items(myJobs, key = { it.id }) { job ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("tech_job_card_${job.jobCode}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(job.jobCode, fontWeight = FontWeight.ExtraBold, color = Cyan500, fontSize = 15.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PriorityBadge(job.priority)
                            JobStatusBadge(job.status)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(job.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${job.applianceCategory} • ${job.applianceBrand}", color = Cyan500, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Address: ${job.customerAddress}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Preferred Time: ${job.preferredTime}", fontSize = 11.sp, color = Color.Gray)
                        }

                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${job.customerMobile}"))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Call Customer", tint = Color(0xFF10B981))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Problem: ${job.problemDescription}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            if (job.additionalInstructions.isNotEmpty()) {
                                Text("Owner Note: ${job.additionalInstructions}", fontSize = 11.sp, color = Color.DarkGray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dynamic Workflow Action Buttons based on Job State
                    when (job.status) {
                        JobStatus.NEW, JobStatus.ASSIGNED -> {
                            Button(
                                onClick = {
                                    onUpdateStatus(job.id, JobStatus.ACCEPTED, "Job accepted by ${session.employeeName}", null, "")
                                },
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Accept Job Assignment", fontWeight = FontWeight.Bold)
                            }
                        }
                        JobStatus.ACCEPTED -> {
                            Button(
                                onClick = {
                                    onUpdateStatus(job.id, JobStatus.ON_THE_WAY, "Technician departed for customer location", null, "")
                                },
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusAmber, contentColor = Navy900),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.DirectionsWalk, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mark 'On The Way'", fontWeight = FontWeight.Bold)
                            }
                        }
                        JobStatus.ON_THE_WAY -> {
                            Button(
                                onClick = {
                                    onUpdateStatus(job.id, JobStatus.WORK_STARTED, "Arrived at customer premises. Inspection started.", null, "")
                                },
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Arrived & Start Work", fontWeight = FontWeight.Bold)
                            }
                        }
                        JobStatus.WORK_STARTED, JobStatus.WAITING_FOR_PARTS -> {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        selectedJobForAction = job
                                        showWorkReportDialog = true
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Parts / Notes", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        onUpdateStatus(job.id, JobStatus.COMPLETED, "Work completed successfully and tested", null, "Service finished.")
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen, contentColor = PureWhite),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Mark Complete", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        JobStatus.COMPLETED -> {
                            if (!isHelper) {
                                Button(
                                    onClick = { onCreateDraftInvoice(job) },
                                    modifier = Modifier.fillMaxWidth().height(46.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Create Draft Invoice for Owner", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(
                                    "✓ Job Completed",
                                    color = StatusGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        JobStatus.CANCELLED -> {
                            Text("Job Cancelled", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Work Report & Parts Usage Dialog for Technician
    if (showWorkReportDialog && selectedJobForAction != null) {
        val job = selectedJobForAction!!
        var reportNotes by remember { mutableStateOf(job.workReportNotes) }
        var selectedPart by remember { mutableStateOf(allParts.firstOrNull()) }
        var partQty by remember { mutableStateOf("1") }

        AlertDialog(
            onDismissRequest = { showWorkReportDialog = false },
            title = { Text("Update Work & Used Parts") },
            text = {
                Column {
                    Text("Job: ${job.jobCode} • ${job.customerName}", fontSize = 12.sp, color = Cyan500)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = reportNotes,
                        onValueChange = { reportNotes = it },
                        label = { Text("Service Work Notes") },
                        placeholder = { Text("e.g. Gas pressure tested, capacitor replaced") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Spare Part Used from Inventory:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    if (allParts.isNotEmpty()) {
                        Text("Selected: ${selectedPart?.name ?: "None"}", fontSize = 12.sp, color = Navy800, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = partQty,
                                onValueChange = { partQty = it },
                                label = { Text("Qty") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val used = mutableListOf<UsedPartItem>()
                        used.addAll(job.partsUsed)
                        if (selectedPart != null) {
                            val q = partQty.toIntOrNull() ?: 1
                            used.add(
                                UsedPartItem(
                                    partId = selectedPart!!.id,
                                    partName = selectedPart!!.name,
                                    partCode = selectedPart!!.code,
                                    quantity = q,
                                    unitPrice = selectedPart!!.sellingPrice,
                                    totalPrice = q * selectedPart!!.sellingPrice
                                )
                            )
                        }
                        onUpdateStatus(job.id, job.status, "Parts added: ${selectedPart?.name ?: ""}", used, reportNotes)
                        showWorkReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900)
                ) {
                    Text("Save Report & Parts")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWorkReportDialog = false }) { Text("Cancel") }
            }
        )
    }
}
