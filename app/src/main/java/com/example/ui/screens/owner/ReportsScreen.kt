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
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.InvoiceEntity
import com.example.data.local.JobEntity
import com.example.data.local.SalaryEntity
import com.example.data.model.JobStatus
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.utils.ReportsExporter

@Composable
fun ReportsScreen(
    jobs: List<JobEntity>,
    invoices: List<InvoiceEntity>,
    salaries: List<SalaryEntity>
) {
    val context = LocalContext.current
    val totalRevenue = invoices.sumOf { it.paidAmount }
    val totalExpenses = salaries.sumOf { it.paidAmount }
    val netProfit = totalRevenue - totalExpenses

    val completedJobs = jobs.count { it.status == JobStatus.COMPLETED }
    val totalJobs = jobs.size
    val completionRate = if (totalJobs > 0) (completedJobs.toDouble() / totalJobs * 100).toInt() else 0

    val categoriesList: List<String> = jobs.map { it.applianceCategory }.filter { it.isNotBlank() }.distinct()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("FINANCIAL P&L ANALYSIS", color = Cyan500, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Net Business Profit", color = PureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            "₹${"%.0f".format(netProfit)}",
                            color = if (netProfit >= 0) StatusGreen else StatusRed,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Gross Service Revenue", fontSize = 11.sp, color = Color.LightGray)
                            Text("₹${"%.0f".format(totalRevenue)}", color = PureWhite, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Staff Salary Expense", fontSize = 11.sp, color = Color.LightGray)
                            Text("₹${"%.0f".format(totalExpenses)}", color = PureWhite, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Completion Rate", fontSize = 11.sp, color = Color.LightGray)
                            Text("$completionRate%", color = Cyan500, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            ReportsExporter.exportBusinessCsv(context, jobs, invoices, salaries)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Complete Business CSV Report", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Service Category Distribution", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        // Category Breakdown
        items(categoriesList) { catName: String ->
            val catJobs = jobs.filter { it.applianceCategory == catName }
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(catName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${catJobs.size} total jobs recorded", fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(
                        "${catJobs.count { it.status == JobStatus.COMPLETED }} Completed",
                        fontWeight = FontWeight.SemiBold,
                        color = StatusGreen,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
