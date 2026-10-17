package com.example.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AttendanceEntity
import com.example.data.local.EmployeeEntity
import com.example.data.model.AttendanceStatus
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceManagementScreen(
    attendanceList: List<AttendanceEntity>,
    employees: List<EmployeeEntity>,
    onApproveAttendance: (AttendanceEntity, Boolean) -> Unit
) {
    val todayStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
    val todayPunches = attendanceList.size
    val approvedCount = attendanceList.count { it.isApprovedByOwner }
    val pendingApproval = attendanceList.count { !it.isApprovedByOwner }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Summary Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(todayStr, color = Cyan500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Staff Attendance Roster", color = PureWhite, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Logged Today", color = Color.LightGray, fontSize = 11.sp)
                        Text("$todayPunches Employees", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Column {
                        Text("Approved", color = Color.LightGray, fontSize = 11.sp)
                        Text("$approvedCount", color = StatusGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Column {
                        Text("Pending Approval", color = Color.LightGray, fontSize = 11.sp)
                        Text("$pendingApproval", color = StatusAmber, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        // Attendance records list
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(attendanceList, key = { it.id }) { att ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(att.employeeName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Surface(
                                color = when (att.status) {
                                    AttendanceStatus.PRESENT -> Color(0xFFD1FAE5)
                                    AttendanceStatus.HALF_DAY -> Color(0xFFFEF3C7)
                                    AttendanceStatus.ABSENT -> Color(0xFFFEE2E2)
                                    AttendanceStatus.OVERTIME -> Color(0xFFDBEAFE)
                                    else -> Color(0xFFE2E8F0)
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = att.status.label.uppercase(),
                                    color = when (att.status) {
                                        AttendanceStatus.PRESENT -> StatusGreen
                                        AttendanceStatus.HALF_DAY -> StatusAmber
                                        AttendanceStatus.ABSENT -> StatusRed
                                        else -> Color.DarkGray
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Punch In: ${att.checkInTime.ifEmpty { "--" }}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Punch Out: ${att.checkOutTime.ifEmpty { "Active on field" }}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        if (att.overtimeHours > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Overtime Logged: ${att.overtimeHours} hours", fontSize = 12.sp, color = Cyan500, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (att.isApprovedByOwner) "✓ Approved by Owner" else "⏳ Pending Approval",
                                fontSize = 12.sp,
                                color = if (att.isApprovedByOwner) StatusGreen else StatusAmber,
                                fontWeight = FontWeight.Medium
                            )

                            if (!att.isApprovedByOwner) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { onApproveAttendance(att, false) },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp), tint = StatusRed)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Reject", fontSize = 11.sp, color = StatusRed)
                                    }

                                    Button(
                                        onClick = { onApproveAttendance(att, true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
