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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
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
import com.example.data.local.CompanySettingsEntity
import com.example.data.local.InvoiceEntity
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.utils.PdfInvoiceGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoiceManagementScreen(
    invoices: List<InvoiceEntity>,
    settings: CompanySettingsEntity?,
    onApproveDraft: (InvoiceEntity) -> Unit,
    onUpdatePayment: (invoiceId: Long, status: PaymentStatus, paid: Double, method: PaymentMethod) -> Unit
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, DRAFT, UNPAID, PAID
    var selectedInvoiceForPayment by remember { mutableStateOf<InvoiceEntity?>(null) }

    val filtered = when (selectedFilter) {
        "DRAFT" -> invoices.filter { it.status == "DRAFT" }
        "UNPAID" -> invoices.filter { it.paymentStatus == PaymentStatus.UNPAID }
        "PAID" -> invoices.filter { it.paymentStatus == PaymentStatus.PAID }
        else -> invoices
    }

    val totalBilled = invoices.sumOf { it.grandTotal }
    val totalPaid = invoices.sumOf { it.paidAmount }
    val totalOutstanding = invoices.sumOf { it.balanceDue }
    val draftCount = invoices.count { it.status == "DRAFT" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Quick Stats Summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Total Billed", fontSize = 11.sp, color = Color.Gray)
                    Text("₹${"%.0f".format(totalBilled)}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Collected", fontSize = 11.sp, color = StatusGreen)
                    Text("₹${"%.0f".format(totalPaid)}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = StatusGreen)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Outstanding", fontSize = 11.sp, color = StatusRed)
                    Text("₹${"%.0f".format(totalOutstanding)}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = StatusRed)
                }
            }
        }

        // Filters
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                InvoiceFilterChip(title = "All (${invoices.size})", selected = selectedFilter == "ALL") { selectedFilter = "ALL" }
            }
            item {
                InvoiceFilterChip(title = "Drafts for Review ($draftCount)", selected = selectedFilter == "DRAFT") { selectedFilter = "DRAFT" }
            }
            item {
                InvoiceFilterChip(title = "Pending Payment", selected = selectedFilter == "UNPAID") { selectedFilter = "UNPAID" }
            }
            item {
                InvoiceFilterChip(title = "Paid Invoices", selected = selectedFilter == "PAID") { selectedFilter = "PAID" }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Invoices List
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered, key = { it.id }) { inv ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("invoice_card_${inv.invoiceNumber}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(inv.invoiceNumber, fontWeight = FontWeight.Bold, color = Cyan500, fontSize = 15.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (inv.status == "DRAFT") {
                                    Surface(
                                        color = StatusAmber.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            "DRAFT REVIEW",
                                            color = StatusAmber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Surface(
                                    color = when (inv.paymentStatus) {
                                        PaymentStatus.PAID -> Color(0xFFD1FAE5)
                                        PaymentStatus.PARTIAL -> Color(0xFFFEF3C7)
                                        PaymentStatus.UNPAID -> Color(0xFFFEE2E2)
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        inv.paymentStatus.label.uppercase(),
                                        color = when (inv.paymentStatus) {
                                            PaymentStatus.PAID -> StatusGreen
                                            PaymentStatus.PARTIAL -> StatusAmber
                                            PaymentStatus.UNPAID -> StatusRed
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(inv.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Job: ${inv.jobCode} • Phone: ${inv.customerPhone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(inv.serviceDescription, fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total: ₹${"%.2f".format(inv.grandTotal)}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Navy900)
                                if (inv.balanceDue > 0) {
                                    Text("Balance: ₹${"%.2f".format(inv.balanceDue)}", color = StatusRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (inv.status == "DRAFT") {
                                    Button(
                                        onClick = { onApproveDraft(inv) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Approve", fontSize = 12.sp)
                                    }
                                }

                                OutlinedButton(
                                    onClick = { selectedInvoiceForPayment = inv },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pay", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        PdfInvoiceGenerator.generateAndShareInvoice(context, inv, settings)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Navy800, contentColor = PureWhite),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("share_invoice_${inv.invoiceNumber}")
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Cyan500)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PDF", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Payment Update Dialog
    selectedInvoiceForPayment?.let { inv ->
        var paymentMethod by remember { mutableStateOf(inv.paymentMethod) }
        var paidAmountInput by remember { mutableStateOf(inv.grandTotal.toInt().toString()) }

        AlertDialog(
            onDismissRequest = { selectedInvoiceForPayment = null },
            title = { Text("Record Customer Payment") },
            text = {
                Column {
                    Text("Invoice ${inv.invoiceNumber} • Grand Total: ₹${inv.grandTotal}")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = paidAmountInput,
                        onValueChange = { paidAmountInput = it },
                        label = { Text("Amount Paid (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Payment Method", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Row {
                        PaymentMethod.values().forEach { m ->
                            TextButton(
                                onClick = { paymentMethod = m },
                                colors = ButtonDefaults.textButtonColors(contentColor = if (paymentMethod == m) Cyan500 else Color.Gray)
                            ) {
                                Text(m.label, fontWeight = if (paymentMethod == m) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val paid = paidAmountInput.toDoubleOrNull() ?: inv.grandTotal
                        val newStatus = when {
                            paid >= inv.grandTotal -> PaymentStatus.PAID
                            paid > 0 -> PaymentStatus.PARTIAL
                            else -> PaymentStatus.UNPAID
                        }
                        onUpdatePayment(inv.id, newStatus, paid, paymentMethod)
                        selectedInvoiceForPayment = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900)
                ) {
                    Text("Save Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedInvoiceForPayment = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun InvoiceFilterChip(title: String, selected: Boolean, onClick: () -> Unit) {
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
