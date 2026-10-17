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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.EmployeeEntity
import com.example.data.model.UserRole
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusRed

@Composable
fun EmployeeManagementScreen(
    employees: List<EmployeeEntity>,
    onAddEmployeeClick: () -> Unit,
    onToggleActive: (EmployeeEntity) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Technicians, 2: Helpers

    val filteredList = when (selectedTab) {
        1 -> employees.filter { it.role == UserRole.TECHNICIAN }
        2 -> employees.filter { it.role == UserRole.HELPER }
        else -> employees
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEmployeeClick,
                containerColor = Cyan500,
                contentColor = Navy900,
                modifier = Modifier.testTag("fab_add_employee")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Employee")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Navy800,
                contentColor = PureWhite
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("All Staff (${employees.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Technicians (${employees.count { it.role == UserRole.TECHNICIAN }})") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Helpers (${employees.count { it.role == UserRole.HELPER }})") }
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.id }) { emp ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth().testTag("employee_card_${emp.employeeCode}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (emp.role == UserRole.TECHNICIAN) Cyan500.copy(alpha = 0.2f) else StatusPurple.copy(alpha = 0.2f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (emp.role == UserRole.TECHNICIAN) Icons.Default.Engineering else Icons.Default.Handyman,
                                            contentDescription = null,
                                            tint = if (emp.role == UserRole.TECHNICIAN) Navy900 else StatusPurple
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(emp.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("${emp.employeeCode} • ${emp.designation}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Surface(
                                    color = if (emp.isActive) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (emp.isActive) "ACTIVE" else "DEACTIVATED",
                                        color = if (emp.isActive) StatusGreen else StatusRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Registered Mobile", fontSize = 11.sp, color = Color.Gray)
                                        Text("+91 ${emp.mobileNumber}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    }
                                    Column {
                                        Text("Base Salary", fontSize = 11.sp, color = Color.Gray)
                                        Text("₹${emp.baseSalary.toInt()} / mo", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    }
                                    Column {
                                        Text("Overtime Rate", fontSize = 11.sp, color = Color.Gray)
                                        Text("₹${emp.overtimeHourlyRate.toInt()} / hr", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (emp.isActive) "Account Active" else "Access Revoked",
                                        fontSize = 12.sp,
                                        color = if (emp.isActive) StatusGreen else StatusRed,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = emp.isActive,
                                        onCheckedChange = { onToggleActive(emp) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Cyan500,
                                            checkedTrackColor = Navy800
                                        )
                                    )
                                }

                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${emp.mobileNumber}"))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call Staff", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
