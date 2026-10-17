package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.JobEntity
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.OwnerTab
import com.example.ui.TechTab
import com.example.ui.components.AddEmployeeDialog
import com.example.ui.components.CreateInvoiceDialog
import com.example.ui.components.CreateJobDialog
import com.example.ui.components.CustomerFeedbackDialog
import com.example.ui.components.QuickSwitchDialog
import com.example.ui.components.SmartRefTopBar
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.employee.TechnicianDashboardScreen
import com.example.ui.screens.owner.AttendanceManagementScreen
import com.example.ui.screens.owner.CustomerScreen
import com.example.ui.screens.owner.EmployeeManagementScreen
import com.example.ui.screens.owner.FeedbackScreen
import com.example.ui.screens.owner.InventoryScreen
import com.example.ui.screens.owner.InvoiceManagementScreen
import com.example.ui.screens.owner.JobManagementScreen
import com.example.ui.screens.owner.OwnerDashboardScreen
import com.example.ui.screens.owner.ReportsScreen
import com.example.ui.screens.owner.SalaryManagementScreen
import com.example.ui.screens.owner.SettingsScreen
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SmartRefProTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as SmartRefApplication
                return MainViewModel(app.repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsState()
            SmartRefProTheme {
                SmartRefMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SmartRefMainApp(viewModel: MainViewModel) {
    val session by viewModel.session.collectAsState()
    val employees by viewModel.employees.collectAsState()
    val jobs by viewModel.jobs.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val attendance by viewModel.attendance.collectAsState()
    val salaries by viewModel.salaries.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val feedbacks by viewModel.feedbacks.collectAsState()
    val parts by viewModel.parts.collectAsState()
    val lowStockParts by viewModel.lowStockParts.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val serviceCategories by viewModel.serviceCategories.collectAsState()

    val currentOwnerTab by viewModel.currentOwnerTab.collectAsState()
    val currentTechTab by viewModel.currentTechTab.collectAsState()
    val selectedJob by viewModel.selectedJob.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog Visibility States
    var showCreateJobDialog by remember { mutableStateOf(false) }
    var showAddEmployeeDialog by remember { mutableStateOf(false) }
    var showQuickSwitchDialog by remember { mutableStateOf(false) }
    var showCreateInvoiceForJob by remember { mutableStateOf<JobEntity?>(null) }
    var showFeedbackForJob by remember { mutableStateOf<JobEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Android Hardware Back Navigation Handler
    BackHandler(enabled = session.isLoggedIn && currentOwnerTab != OwnerTab.DASHBOARD) {
        viewModel.setOwnerTab(OwnerTab.DASHBOARD)
    }

    if (!session.isLoggedIn) {
        LoginScreen(
            settings = settings,
            onLoginSubmit = { mobile, role ->
                viewModel.login(mobile, role) {}
            }
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                SmartRefTopBar(
                    session = session,
                    unreadNotificationCount = notifications.count { !it.isRead },
                    onNotificationsClick = { viewModel.markAllNotificationsRead() },
                    onSwitchRoleClick = { showQuickSwitchDialog = true },
                    onLogoutClick = { viewModel.logout() }
                )
            },
            bottomBar = {
                if (session.role == UserRole.OWNER) {
                    OwnerBottomNav(
                        currentTab = currentOwnerTab,
                        onTabSelect = { viewModel.setOwnerTab(it) }
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (session.role == UserRole.OWNER) {
                    Crossfade(targetState = currentOwnerTab, label = "owner_tabs") { tab ->
                        when (tab) {
                            OwnerTab.DASHBOARD -> OwnerDashboardScreen(
                                employees = employees,
                                jobs = jobs,
                                invoices = invoices,
                                attendance = attendance,
                                salaries = salaries,
                                feedbacks = feedbacks,
                                lowStockParts = lowStockParts,
                                onCreateJobClick = { showCreateJobDialog = true },
                                onNavigateTab = { viewModel.setOwnerTab(it) },
                                onSelectJob = { job ->
                                    viewModel.selectJob(job)
                                    viewModel.setOwnerTab(OwnerTab.JOBS)
                                }
                            )
                            OwnerTab.JOBS -> JobManagementScreen(
                                jobs = jobs,
                                employees = employees,
                                onCreateJobClick = { showCreateJobDialog = true },
                                onStatusChange = { jobId, newStatus ->
                                    viewModel.updateJobStatus(jobId, newStatus)
                                },
                                onGenerateInvoice = { job -> showCreateInvoiceForJob = job },
                                onAddFeedback = { job -> showFeedbackForJob = job },
                                onDeleteJob = { job -> viewModel.deleteJob(job) }
                            )
                            OwnerTab.INVOICES -> InvoiceManagementScreen(
                                invoices = invoices,
                                settings = settings,
                                onApproveDraft = { inv -> viewModel.approveInvoice(inv) },
                                onUpdatePayment = { id, st, paid, meth ->
                                    viewModel.updateInvoicePayment(id, st, paid, meth)
                                }
                            )
                            OwnerTab.EMPLOYEES -> EmployeeManagementScreen(
                                employees = employees,
                                onAddEmployeeClick = { showAddEmployeeDialog = true },
                                onToggleActive = { emp -> viewModel.toggleEmployeeStatus(emp) }
                            )
                            OwnerTab.ATTENDANCE -> AttendanceManagementScreen(
                                attendanceList = attendance,
                                employees = employees,
                                onApproveAttendance = { att, approved ->
                                    viewModel.approveAttendance(att, approved)
                                }
                            )
                            OwnerTab.SALARY -> SalaryManagementScreen(
                                salaries = salaries,
                                employees = employees,
                                settings = settings,
                                onRunPayroll = { month -> viewModel.runPayrollCalculation(month) }
                            )
                            OwnerTab.INVENTORY -> InventoryScreen(
                                parts = parts,
                                onSavePart = { part -> viewModel.addOrUpdatePart(part) {} }
                            )
                            OwnerTab.CUSTOMERS -> CustomerScreen(
                                customers = customers,
                                allJobs = jobs
                            )
                            OwnerTab.FEEDBACK -> FeedbackScreen(feedbacks = feedbacks)
                            OwnerTab.REPORTS -> ReportsScreen(
                                jobs = jobs,
                                invoices = invoices,
                                salaries = salaries
                            )
                            OwnerTab.SETTINGS -> SettingsScreen(
                                currentSettings = settings,
                                onSaveSettings = { updated -> viewModel.updateCompanySettings(updated) }
                            )
                        }
                    }
                } else {
                    // Technician or Helper Workspace
                    val myEmpId = session.employeeId ?: 1L
                    val myJobsList = jobs.filter { it.assignedTechnicianId == myEmpId || it.assignedHelperId == myEmpId }
                    val myAttList = attendance.filter { it.employeeId == myEmpId }
                    val mySalList = salaries.filter { it.employeeId == myEmpId }

                    TechnicianDashboardScreen(
                        session = session,
                        myJobs = myJobsList,
                        myAttendance = myAttList,
                        mySalary = mySalList,
                        allParts = parts,
                        onUpdateStatus = { jId, st, note, partsUsed, wNotes ->
                            viewModel.updateJobStatus(jId, st, note, partsUsed, wNotes)
                        },
                        onPunchAttendance = { st, ot, notes ->
                            viewModel.recordPunchAttendance(myEmpId, session.employeeName, st, ot, notes)
                        },
                        onCreateDraftInvoice = { job -> showCreateInvoiceForJob = job }
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (showCreateJobDialog) {
        CreateJobDialog(
            categories = serviceCategories,
            employees = employees,
            onDismiss = { showCreateJobDialog = false },
            onSubmit = { cName, cMobile, cAddr, cat, brand, model, prob, date, time, prio, tId, tName, hId, hName, chg, inst ->
                viewModel.createJob(
                    customerName = cName,
                    customerMobile = cMobile,
                    customerAddress = cAddr,
                    applianceCategory = cat,
                    applianceBrand = brand,
                    modelNumber = model,
                    problemDescription = prob,
                    serviceDate = date,
                    preferredTime = time,
                    priority = prio,
                    assignedTechId = tId,
                    assignedTechName = tName,
                    assignedHelperId = hId,
                    assignedHelperName = hName,
                    serviceCharge = chg,
                    instructions = inst,
                    onSuccess = { showCreateJobDialog = false }
                )
            }
        )
    }

    if (showAddEmployeeDialog) {
        AddEmployeeDialog(
            onDismiss = { showAddEmployeeDialog = false },
            onSubmit = { name, mobile, code, desig, role, date, salType, sal, ot ->
                viewModel.addEmployee(name, mobile, code, desig, role, date, salType, sal, ot) {
                    showAddEmployeeDialog = false
                }
            }
        )
    }

    if (showQuickSwitchDialog) {
        QuickSwitchDialog(
            currentSession = session,
            employees = employees,
            onDismiss = { showQuickSwitchDialog = false },
            onSelectSession = { newSession ->
                viewModel.quickSwitchSession(newSession)
                showQuickSwitchDialog = false
            }
        )
    }

    showCreateInvoiceForJob?.let { job ->
        CreateInvoiceDialog(
            job = job,
            isOwner = session.role == UserRole.OWNER,
            onDismiss = { showCreateInvoiceForJob = null },
            onSubmit = { items, disc, tax, pStatus, pMethod, paid, isDraft ->
                viewModel.createInvoice(
                    jobId = job.id,
                    jobCode = job.jobCode,
                    customerName = job.customerName,
                    customerPhone = job.customerMobile,
                    customerAddress = job.customerAddress,
                    technicianName = job.assignedTechnicianName.ifEmpty { session.employeeName },
                    serviceDescription = "${job.applianceCategory} service for ${job.applianceBrand}",
                    items = items,
                    discount = disc,
                    taxPercent = tax,
                    paymentStatus = pStatus,
                    paymentMethod = pMethod,
                    paidAmount = paid,
                    isDraft = isDraft,
                    onSuccess = { showCreateInvoiceForJob = null }
                )
            }
        )
    }

    showFeedbackForJob?.let { job ->
        CustomerFeedbackDialog(
            jobCode = job.jobCode,
            customerName = job.customerName,
            onDismiss = { showFeedbackForJob = null },
            onSubmit = { r, q, b, s, comments ->
                viewModel.submitCustomerFeedback(
                    com.example.data.local.FeedbackEntity(
                        jobId = job.id,
                        jobCode = job.jobCode,
                        customerName = job.customerName,
                        customerMobile = job.customerMobile,
                        rating = r,
                        serviceQuality = q,
                        technicianBehaviour = b,
                        workSatisfaction = s,
                        writtenFeedback = comments,
                        date = "Today"
                    )
                ) {
                    showFeedbackForJob = null
                }
            }
        )
    }
}

@Composable
private fun OwnerBottomNav(
    currentTab: OwnerTab,
    onTabSelect: (OwnerTab) -> Unit
) {
    var moreMenuExpanded by remember { mutableStateOf(false) }

    NavigationBar(
        containerColor = Navy900,
        contentColor = PureWhite
    ) {
        NavigationBarItem(
            selected = currentTab == OwnerTab.DASHBOARD,
            onClick = { onTabSelect(OwnerTab.DASHBOARD) },
            icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = Cyan400,
                indicatorColor = Cyan500,
                unselectedIconColor = Color.LightGray,
                unselectedTextColor = Color.LightGray
            ),
            modifier = Modifier.testTag("nav_dashboard")
        )

        NavigationBarItem(
            selected = currentTab == OwnerTab.JOBS,
            onClick = { onTabSelect(OwnerTab.JOBS) },
            icon = { Icon(imageVector = Icons.Default.Assignment, contentDescription = "Jobs") },
            label = { Text("Jobs") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = Cyan400,
                indicatorColor = Cyan500,
                unselectedIconColor = Color.LightGray,
                unselectedTextColor = Color.LightGray
            ),
            modifier = Modifier.testTag("nav_jobs")
        )

        NavigationBarItem(
            selected = currentTab == OwnerTab.INVOICES,
            onClick = { onTabSelect(OwnerTab.INVOICES) },
            icon = { Icon(imageVector = Icons.Default.Receipt, contentDescription = "Invoices") },
            label = { Text("Invoices") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = Cyan400,
                indicatorColor = Cyan500,
                unselectedIconColor = Color.LightGray,
                unselectedTextColor = Color.LightGray
            ),
            modifier = Modifier.testTag("nav_invoices")
        )

        NavigationBarItem(
            selected = currentTab == OwnerTab.EMPLOYEES,
            onClick = { onTabSelect(OwnerTab.EMPLOYEES) },
            icon = { Icon(imageVector = Icons.Default.People, contentDescription = "Staff") },
            label = { Text("Staff") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = Cyan400,
                indicatorColor = Cyan500,
                unselectedIconColor = Color.LightGray,
                unselectedTextColor = Color.LightGray
            ),
            modifier = Modifier.testTag("nav_staff")
        )

        NavigationBarItem(
            selected = currentTab in listOf(
                OwnerTab.ATTENDANCE,
                OwnerTab.SALARY,
                OwnerTab.INVENTORY,
                OwnerTab.CUSTOMERS,
                OwnerTab.FEEDBACK,
                OwnerTab.REPORTS,
                OwnerTab.SETTINGS
            ),
            onClick = { moreMenuExpanded = true },
            icon = {
                Box {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                    DropdownMenu(
                        expanded = moreMenuExpanded,
                        onDismissRequest = { moreMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Attendance Roster") },
                            onClick = {
                                onTabSelect(OwnerTab.ATTENDANCE)
                                moreMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Payroll & Salaries") },
                            onClick = {
                                onTabSelect(OwnerTab.SALARY)
                                moreMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Spare Parts & Inventory") },
                            onClick = {
                                onTabSelect(OwnerTab.INVENTORY)
                                moreMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Customer Database") },
                            onClick = {
                                onTabSelect(OwnerTab.CUSTOMERS)
                                moreMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Customer Reviews (Ratings)") },
                            onClick = {
                                onTabSelect(OwnerTab.FEEDBACK)
                                moreMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Reports & Business Export") },
                            onClick = {
                                onTabSelect(OwnerTab.REPORTS)
                                moreMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Company Settings & Branding") },
                            onClick = {
                                onTabSelect(OwnerTab.SETTINGS)
                                moreMenuExpanded = false
                            }
                        )
                    }
                }
            },
            label = { Text("More") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = Cyan400,
                indicatorColor = Cyan500,
                unselectedIconColor = Color.LightGray,
                unselectedTextColor = Color.LightGray
            ),
            modifier = Modifier.testTag("nav_more")
        )
    }
}
