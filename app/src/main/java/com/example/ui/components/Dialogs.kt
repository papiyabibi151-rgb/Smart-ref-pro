package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.EmployeeEntity
import com.example.data.local.JobEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.ServiceCategoryEntity
import com.example.data.model.AuthSession
import com.example.data.model.InvoiceItemRow
import com.example.data.model.JobPriority
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.SalaryType
import com.example.data.model.UserRole
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.PureWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobDialog(
    categories: List<ServiceCategoryEntity>,
    employees: List<EmployeeEntity>,
    onDismiss: () -> Unit,
    onSubmit: (
        customerName: String,
        customerMobile: String,
        customerAddress: String,
        category: String,
        brand: String,
        model: String,
        problem: String,
        date: String,
        time: String,
        priority: JobPriority,
        techId: Long?,
        techName: String,
        helperId: Long?,
        helperName: String,
        charge: Double,
        instructions: String
    ) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerMobile by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(if (categories.isNotEmpty()) categories.first().name else "Split AC") }
    var brand by remember { mutableStateOf("LG") }
    var model by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    var serviceDate by remember { mutableStateOf(SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())) }
    var preferredTime by remember { mutableStateOf("11:00 AM - 01:00 PM") }
    var priority by remember { mutableStateOf(JobPriority.NORMAL) }
    var serviceCharge by remember { mutableStateOf("650") }
    var instructions by remember { mutableStateOf("") }

    val technicians = employees.filter { it.role == UserRole.TECHNICIAN && it.isActive }
    val helpers = employees.filter { it.role == UserRole.HELPER && it.isActive }

    var selectedTech by remember { mutableStateOf<EmployeeEntity?>(technicians.firstOrNull()) }
    var selectedHelper by remember { mutableStateOf<EmployeeEntity?>(helpers.firstOrNull()) }

    var catExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }
    var techExpanded by remember { mutableStateOf(false) }
    var helperExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(650.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create New Service Job",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Customer Info
                Text("Customer Details", fontWeight = FontWeight.SemiBold, color = Cyan500, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Name *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("job_customer_name_input"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customerMobile,
                    onValueChange = { if (it.length <= 10) customerMobile = it },
                    label = { Text("Customer Mobile Number (10 digits) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("job_customer_mobile_input"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customerAddress,
                    onValueChange = { customerAddress = it },
                    label = { Text("Complete Service Address *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("job_customer_address_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Appliance Info
                Text("Appliance & Problem Details", fontWeight = FontWeight.SemiBold, color = Cyan500, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Appliance Category *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        for (cat in categories) {
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    selectedCategory = cat.name
                                    serviceCharge = cat.defaultCharge.toInt().toString()
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand / Make") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Model No.") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = problem,
                    onValueChange = { problem = it },
                    label = { Text("Problem Description *") },
                    placeholder = { Text("e.g. Not cooling, loud compressor noise") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("job_problem_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Schedule & Assignment
                Text("Schedule & Staff Assignment", fontWeight = FontWeight.SemiBold, color = Cyan500, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = serviceDate,
                        onValueChange = { serviceDate = it },
                        label = { Text("Service Date") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = preferredTime,
                        onValueChange = { preferredTime = it },
                        label = { Text("Preferred Time") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Technician Dropdown
                ExposedDropdownMenuBox(
                    expanded = techExpanded,
                    onExpandedChange = { techExpanded = !techExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedTech?.fullName ?: "Unassigned",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assign Lead Technician") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = techExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = techExpanded,
                        onDismissRequest = { techExpanded = false }
                    ) {
                        for (tech in technicians) {
                            DropdownMenuItem(
                                text = { Text("${tech.fullName} (${tech.designation})") },
                                onClick = {
                                    selectedTech = tech
                                    techExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Helper Dropdown
                ExposedDropdownMenuBox(
                    expanded = helperExpanded,
                    onExpandedChange = { helperExpanded = !helperExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedHelper?.fullName ?: "None",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assign Service Helper (Optional)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = helperExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = helperExpanded,
                        onDismissRequest = { helperExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("None") },
                            onClick = {
                                selectedHelper = null
                                helperExpanded = false
                            }
                        )
                        for (helper in helpers) {
                            DropdownMenuItem(
                                text = { Text("${helper.fullName} (${helper.employeeCode})") },
                                onClick = {
                                    selectedHelper = helper
                                    helperExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = serviceCharge,
                        onValueChange = { serviceCharge = it },
                        label = { Text("Inspection / Service Fee (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    // Priority selector
                    ExposedDropdownMenuBox(
                        expanded = priorityExpanded,
                        onExpandedChange = { priorityExpanded = !priorityExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = priority.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Priority") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = priorityExpanded,
                            onDismissRequest = { priorityExpanded = false }
                        ) {
                            for (p in JobPriority.values()) {
                                DropdownMenuItem(
                                    text = { Text(p.label) },
                                    onClick = {
                                        priority = p
                                        priorityExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Additional Technician Instructions") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (customerName.isNotBlank() && customerMobile.isNotBlank() && problem.isNotBlank()) {
                            onSubmit(
                                customerName,
                                customerMobile,
                                customerAddress,
                                selectedCategory,
                                brand,
                                model,
                                problem,
                                serviceDate,
                                preferredTime,
                                priority,
                                selectedTech?.id,
                                selectedTech?.fullName ?: "",
                                selectedHelper?.id,
                                selectedHelper?.fullName ?: "",
                                serviceCharge.toDoubleOrNull() ?: 500.0,
                                instructions
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_create_job_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Distribute & Assign Job", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun AddEmployeeDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        name: String,
        mobile: String,
        code: String,
        designation: String,
        role: UserRole,
        joiningDate: String,
        salaryType: SalaryType,
        salary: Double,
        overtimeRate: Double
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("HVAC Technician") }
    var role by remember { mutableStateOf(UserRole.TECHNICIAN) }
    var salaryType by remember { mutableStateOf(SalaryType.MONTHLY) }
    var salary by remember { mutableStateOf("25000") }
    var overtimeRate by remember { mutableStateOf("180") }
    var code by remember { mutableStateOf("SR-EMP-" + (100 + (Math.random() * 900).toInt())) }
    val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(600.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Register Employee",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("employee_name_input"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mobile,
                    onValueChange = { if (it.length <= 10) mobile = it },
                    label = { Text("Registered Mobile Number (10 digits) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("employee_mobile_input"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Employee ID") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = designation,
                        onValueChange = { designation = it },
                        label = { Text("Designation") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Role Assignment", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Cyan500)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == UserRole.TECHNICIAN,
                        onClick = {
                            role = UserRole.TECHNICIAN
                            designation = "HVAC Technician"
                        }
                    )
                    Text("Technician", modifier = Modifier.clickable { role = UserRole.TECHNICIAN })
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = role == UserRole.HELPER,
                        onClick = {
                            role = UserRole.HELPER
                            designation = "Service Helper"
                        }
                    )
                    Text("Helper", modifier = Modifier.clickable { role = UserRole.HELPER })
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Salary Configuration", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Cyan500)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = salaryType == SalaryType.MONTHLY,
                        onClick = { salaryType = SalaryType.MONTHLY }
                    )
                    Text("Monthly Salary", modifier = Modifier.clickable { salaryType = SalaryType.MONTHLY })
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = salaryType == SalaryType.DAILY,
                        onClick = { salaryType = SalaryType.DAILY }
                    )
                    Text("Daily Wage", modifier = Modifier.clickable { salaryType = SalaryType.DAILY })
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = salary,
                        onValueChange = { salary = it },
                        label = { Text(if (salaryType == SalaryType.MONTHLY) "Monthly Base (₹)" else "Daily Rate (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = overtimeRate,
                        onValueChange = { overtimeRate = it },
                        label = { Text("Overtime Rate (₹/hr)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && mobile.isNotBlank()) {
                            onSubmit(
                                name,
                                mobile,
                                code,
                                designation,
                                role,
                                today,
                                salaryType,
                                salary.toDoubleOrNull() ?: 20000.0,
                                overtimeRate.toDoubleOrNull() ?: 150.0
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_add_employee_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Authorize & Add Employee", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun QuickSwitchDialog(
    currentSession: AuthSession,
    employees: List<EmployeeEntity>,
    onDismiss: () -> Unit,
    onSelectSession: (AuthSession) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Switch Role / Account", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Test the application across different permission roles:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Owner Option
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (currentSession.role == UserRole.OWNER) Cyan500.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectSession(
                                AuthSession(
                                    isLoggedIn = true,
                                    role = UserRole.OWNER,
                                    employeeName = "SMART REFRIGERATION Owner",
                                    mobileNumber = "9876543210"
                                )
                            )
                        }
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Company Owner (Super Admin)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Full business control & permissions", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Technicians & Helpers", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Cyan500)
                Spacer(modifier = Modifier.height(6.dp))

                for (emp in employees.filter { it.isActive }) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (currentSession.employeeId == emp.id) Cyan500.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                onSelectSession(
                                    AuthSession(
                                        isLoggedIn = true,
                                        role = emp.role,
                                        employeeId = emp.id,
                                        employeeName = emp.fullName,
                                        mobileNumber = emp.mobileNumber
                                    )
                                )
                            }
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(emp.fullName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("${emp.role.displayName} • ${emp.designation}", style = MaterialTheme.typography.labelSmall)
                            }
                            Text(
                                "Mob: ${emp.mobileNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CreateInvoiceDialog(
    job: JobEntity,
    isOwner: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (
        items: List<InvoiceItemRow>,
        discount: Double,
        taxPercent: Double,
        status: PaymentStatus,
        method: PaymentMethod,
        paid: Double,
        isDraft: Boolean
    ) -> Unit
) {
    val items = remember {
        mutableStateListOf(
            InvoiceItemRow(
                description = "${job.applianceCategory} Inspection & Service Charge",
                quantity = 1,
                unitPrice = job.serviceCharge,
                totalPrice = job.serviceCharge
            )
        )
    }

    // Add used parts if available
    remember {
        for (part in job.partsUsed) {
            items.add(
                InvoiceItemRow(
                    description = "${part.partName} (${part.partCode})",
                    quantity = part.quantity,
                    unitPrice = part.unitPrice,
                    totalPrice = part.totalPrice
                )
            )
        }
        true
    }

    var newItemDesc by remember { mutableStateOf("") }
    var newItemQty by remember { mutableStateOf("1") }
    var newItemPrice by remember { mutableStateOf("") }

    var discountStr by remember { mutableStateOf("0") }
    var taxPercentStr by remember { mutableStateOf("18") }
    var paymentStatus by remember { mutableStateOf(PaymentStatus.PAID) }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.UPI) }

    val subtotal = items.sumOf { it.totalPrice }
    val discount = discountStr.toDoubleOrNull() ?: 0.0
    val taxPercent = taxPercentStr.toDoubleOrNull() ?: 0.0
    val taxable = (subtotal - discount).coerceAtLeast(0.0)
    val taxAmount = (taxable * taxPercent) / 100.0
    val grandTotal = taxable + taxAmount

    var paidAmountStr by remember { mutableStateOf(grandTotal.toInt().toString()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(650.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isOwner) "Generate Tax Invoice" else "Draft Service Invoice",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Job: ${job.jobCode} • ${job.customerName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Cyan500
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text("Line Items & Spare Parts", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                // Items list
                for (item in items) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.description, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text("${item.quantity} x ₹${item.unitPrice}", style = MaterialTheme.typography.labelSmall)
                        }
                        Text("₹${item.totalPrice}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        IconButton(onClick = { items.remove(item) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Item", tint = Color.Red, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Add item row
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newItemDesc,
                        onValueChange = { newItemDesc = it },
                        label = { Text("Item / Part") },
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newItemQty,
                        onValueChange = { newItemQty = it },
                        label = { Text("Qty") },
                        modifier = Modifier.weight(0.8f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newItemPrice,
                        onValueChange = { newItemPrice = it },
                        label = { Text("Rate (₹)") },
                        modifier = Modifier.weight(1.2f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            val q = newItemQty.toIntOrNull() ?: 1
                            val p = newItemPrice.toDoubleOrNull() ?: 0.0
                            if (newItemDesc.isNotBlank() && p > 0) {
                                items.add(InvoiceItemRow(description = newItemDesc, quantity = q, unitPrice = p, totalPrice = q * p))
                                newItemDesc = ""
                                newItemPrice = ""
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Cyan500)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = discountStr,
                        onValueChange = { discountStr = it },
                        label = { Text("Discount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = taxPercentStr,
                        onValueChange = { taxPercentStr = it },
                        label = { Text("GST / Tax (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                // Payment summary
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal:")
                            Text("₹${"%.2f".format(subtotal)}", fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tax (${taxPercent}%):")
                            Text("₹${"%.2f".format(taxAmount)}", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grand Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("₹${"%.2f".format(grandTotal)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Cyan500)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Payment Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = paidAmountStr,
                        onValueChange = { paidAmountStr = it },
                        label = { Text("Paid Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!isOwner) {
                        Button(
                            onClick = {
                                onSubmit(
                                    items.toList(),
                                    discount,
                                    taxPercent,
                                    paymentStatus,
                                    paymentMethod,
                                    paidAmountStr.toDoubleOrNull() ?: grandTotal,
                                    true // Draft
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                        ) {
                            Text("Submit as Draft")
                        }
                    }

                    Button(
                        onClick = {
                            onSubmit(
                                items.toList(),
                                discount,
                                taxPercent,
                                paymentStatus,
                                paymentMethod,
                                paidAmountStr.toDoubleOrNull() ?: grandTotal,
                                false // Approved
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900)
                    ) {
                        Text(if (isOwner) "Approve & Generate PDF" else "Submit Final Invoice", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerFeedbackDialog(
    jobCode: String,
    customerName: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, quality: Int, behaviour: Int, satisfaction: Int, comments: String) -> Unit
) {
    var overallRating by remember { mutableIntStateOf(5) }
    var qualityRating by remember { mutableIntStateOf(5) }
    var behaviourRating by remember { mutableIntStateOf(5) }
    var satisfactionRating by remember { mutableIntStateOf(5) }
    var comments by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Customer Service Feedback", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Job: $jobCode • Customer: $customerName", style = MaterialTheme.typography.bodySmall, color = Cyan500)
                Spacer(modifier = Modifier.height(14.dp))

                Text("Overall Rating", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                RatingStars(rating = overallRating, onRatingSelected = { overallRating = it })

                Spacer(modifier = Modifier.height(8.dp))
                Text("Service & Technical Quality", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                RatingStars(rating = qualityRating, onRatingSelected = { qualityRating = it })

                Spacer(modifier = Modifier.height(8.dp))
                Text("Technician Behaviour & Punctuality", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                RatingStars(rating = behaviourRating, onRatingSelected = { behaviourRating = it })

                Spacer(modifier = Modifier.height(8.dp))
                Text("Work Satisfaction", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                RatingStars(rating = satisfactionRating, onRatingSelected = { satisfactionRating = it })

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = { Text("Customer Written Review") },
                    placeholder = { Text("Excellent cooling, polite technician...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(overallRating, qualityRating, behaviourRating, satisfactionRating, comments)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900)
            ) {
                Text("Submit Review", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RatingStars(rating: Int, onRatingSelected: (Int) -> Unit) {
    Row {
        for (i in 1..5) {
            IconButton(
                onClick = { onRatingSelected(i) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "$i stars",
                    tint = if (i <= rating) Color(0xFFF59E0B) else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
