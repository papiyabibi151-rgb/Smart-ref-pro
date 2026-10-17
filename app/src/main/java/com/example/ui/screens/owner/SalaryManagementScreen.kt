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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CompanySettingsEntity
import com.example.data.local.EmployeeEntity
import com.example.data.local.SalaryEntity
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusGreen
import com.example.utils.PdfSalarySlipGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SalaryManagementScreen(
    salaries: List<SalaryEntity>,
    employees: List<EmployeeEntity>,
    settings: CompanySettingsEntity?,
    onRunPayroll: (String) -> Unit
) {
    val context = LocalContext.current
    val currentMonthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    val totalDisbursed = salaries.sumOf { it.paidAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Payroll Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "PAYROLL CONTROL • $currentMonthYear",
                    color = Cyan500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total Disbursed: ₹${"%.0f".format(totalDisbursed)}",
                    color = PureWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onRunPayroll(currentMonthYear) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("run_payroll_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Auto-Calculate Monthly Payroll", fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (salaries.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No salary slips generated for $currentMonthYear yet.")
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Click 'Auto-Calculate Monthly Payroll' above to compute wages from attendance records.", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }

            items(salaries, key = { it.id }) { sal ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(sal.employeeName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Period: ${sal.monthYear} • Base: ₹${sal.baseSalary.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(
                                color = Color(0xFFD1FAE5),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    sal.status,
                                    color = StatusGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

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
                                    Text("Present Days", fontSize = 11.sp, color = Color.Gray)
                                    Text("${sal.presentDays} Days", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }
                                Column {
                                    Text("Overtime Pay", fontSize = 11.sp, color = Color.Gray)
                                    Text("₹${sal.overtimePay.toInt()}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }
                                Column {
                                    Text("Incentive/Bonus", fontSize = 11.sp, color = Color.Gray)
                                    Text("₹${(sal.incentives + sal.bonus).toInt()}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Net Pay: ₹${"%.0f".format(sal.netSalary)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Cyan500)
                                Text("Paid on: ${sal.paymentDate}", fontSize = 11.sp, color = Color.Gray)
                            }

                            Button(
                                onClick = {
                                    PdfSalarySlipGenerator.generateAndShareSalarySlip(context, sal, settings)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Navy800, contentColor = PureWhite),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Cyan500)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Slip PDF", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
