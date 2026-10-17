package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AttendanceEntity
import com.example.data.local.CompanySettingsEntity
import com.example.data.local.CustomerEntity
import com.example.data.local.EmployeeEntity
import com.example.data.local.FeedbackEntity
import com.example.data.local.InvoiceEntity
import com.example.data.local.JobEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.SalaryEntity
import com.example.data.local.ServiceCategoryEntity
import com.example.data.local.SparePartEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.AuthSession
import com.example.data.model.InvoiceItemRow
import com.example.data.model.JobPriority
import com.example.data.model.JobStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.SalaryType
import com.example.data.model.UsedPartItem
import com.example.data.model.UserRole
import com.example.data.repository.SmartRefRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class OwnerTab(val title: String) {
    DASHBOARD("Dashboard"),
    JOBS("Jobs"),
    INVOICES("Invoices"),
    EMPLOYEES("Staff"),
    ATTENDANCE("Attendance"),
    SALARY("Payroll"),
    INVENTORY("Parts"),
    CUSTOMERS("Clients"),
    FEEDBACK("Reviews"),
    REPORTS("Reports"),
    SETTINGS("Settings")
}

enum class TechTab(val title: String) {
    MY_JOBS("My Jobs"),
    ATTENDANCE("Punch In/Out"),
    SALARY("My Salary"),
    NOTIFICATIONS("Alerts")
}

class MainViewModel(private val repository: SmartRefRepository) : ViewModel() {

    val session: StateFlow<AuthSession> = repository.currentSession
    val employees: StateFlow<List<EmployeeEntity>> = repository.allEmployees.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val jobs: StateFlow<List<JobEntity>> = repository.allJobs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val invoices: StateFlow<List<InvoiceEntity>> = repository.allInvoices.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val attendance: StateFlow<List<AttendanceEntity>> = repository.allAttendance.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val salaries: StateFlow<List<SalaryEntity>> = repository.allSalaries.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val customers: StateFlow<List<CustomerEntity>> = repository.allCustomers.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val feedbacks: StateFlow<List<FeedbackEntity>> = repository.allFeedbacks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val parts: StateFlow<List<SparePartEntity>> = repository.allParts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val lowStockParts: StateFlow<List<SparePartEntity>> = repository.lowStockParts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val notifications: StateFlow<List<NotificationEntity>> = repository.allNotifications.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val settings: StateFlow<CompanySettingsEntity?> = repository.companySettings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )
    val serviceCategories: StateFlow<List<ServiceCategoryEntity>> = repository.serviceCategories.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Navigation state
    private val _currentOwnerTab = MutableStateFlow(OwnerTab.DASHBOARD)
    val currentOwnerTab = _currentOwnerTab.asStateFlow()

    private val _currentTechTab = MutableStateFlow(TechTab.MY_JOBS)
    val currentTechTab = _currentTechTab.asStateFlow()

    // Selected job for detail / execution screen
    private val _selectedJob = MutableStateFlow<JobEntity?>(null)
    val selectedJob = _selectedJob.asStateFlow()

    // UI events / messages
    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    fun setOwnerTab(tab: OwnerTab) {
        _currentOwnerTab.value = tab
    }

    fun setTechTab(tab: TechTab) {
        _currentTechTab.value = tab
    }

    fun selectJob(job: JobEntity?) {
        _selectedJob.value = job
    }

    // --- Authentication Actions ---
    fun login(mobile: String, role: UserRole, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.loginWithMobile(mobile, role)
            result.fold(
                onSuccess = {
                    _uiMessage.emit("Welcome back, ${it.employeeName}!")
                    onComplete(true)
                },
                onFailure = {
                    _uiMessage.emit(it.message ?: "Login failed. Check mobile number.")
                    onComplete(false)
                }
            )
        }
    }

    fun quickSwitchSession(session: AuthSession) {
        repository.switchSession(session)
        viewModelScope.launch {
            _uiMessage.emit("Switched to ${session.role.displayName}: ${session.employeeName}")
        }
    }

    fun logout() {
        repository.logout()
        _selectedJob.value = null
    }

    // --- Job Actions ---
    fun createJob(
        customerName: String,
        customerMobile: String,
        customerAddress: String,
        applianceCategory: String,
        applianceBrand: String,
        modelNumber: String,
        problemDescription: String,
        serviceDate: String,
        preferredTime: String,
        priority: JobPriority,
        assignedTechId: Long?,
        assignedTechName: String,
        assignedHelperId: Long?,
        assignedHelperName: String,
        serviceCharge: Double,
        instructions: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.createJob(
                    customerName = customerName,
                    customerMobile = customerMobile,
                    customerAddress = customerAddress,
                    applianceCategory = applianceCategory,
                    applianceBrand = applianceBrand,
                    modelNumber = modelNumber,
                    problemDescription = problemDescription,
                    serviceDate = serviceDate,
                    preferredTime = preferredTime,
                    priority = priority,
                    assignedTechId = assignedTechId,
                    assignedTechName = assignedTechName,
                    assignedHelperId = assignedHelperId,
                    assignedHelperName = assignedHelperName,
                    serviceCharge = serviceCharge,
                    instructions = instructions
                )
                _uiMessage.emit("Job created and assigned successfully!")
                onSuccess()
            } catch (e: Exception) {
                _uiMessage.emit("Error creating job: ${e.message}")
            }
        }
    }

    fun updateJobStatus(
        jobId: Long,
        newStatus: JobStatus,
        statusNote: String = "",
        usedParts: List<UsedPartItem>? = null,
        workNotes: String = ""
    ) {
        viewModelScope.launch {
            try {
                repository.updateJobStatus(jobId, newStatus, statusNote, usedParts, workNotes)
                // update selected job if open
                _selectedJob.value = _selectedJob.value?.copy(status = newStatus)
                _uiMessage.emit("Job status updated to ${newStatus.label}")
            } catch (e: Exception) {
                _uiMessage.emit("Failed to update status: ${e.message}")
            }
        }
    }

    fun deleteJob(job: JobEntity) {
        viewModelScope.launch {
            repository.deleteJob(job)
            _selectedJob.value = null
            _uiMessage.emit("Job ${job.jobCode} removed")
        }
    }

    // --- Invoice Actions ---
    fun createInvoice(
        jobId: Long,
        jobCode: String,
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        technicianName: String,
        serviceDescription: String,
        items: List<InvoiceItemRow>,
        discount: Double,
        taxPercent: Double,
        paymentStatus: PaymentStatus,
        paymentMethod: PaymentMethod,
        paidAmount: Double,
        isDraft: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.createInvoice(
                    jobId = jobId,
                    jobCode = jobCode,
                    customerName = customerName,
                    customerPhone = customerPhone,
                    customerAddress = customerAddress,
                    technicianName = technicianName,
                    serviceDescription = serviceDescription,
                    items = items,
                    discount = discount,
                    taxPercent = taxPercent,
                    paymentStatus = paymentStatus,
                    paymentMethod = paymentMethod,
                    paidAmount = paidAmount,
                    isDraft = isDraft
                )
                val msg = if (isDraft) "Draft invoice submitted for Owner review!" else "Invoice created successfully!"
                _uiMessage.emit(msg)
                onSuccess()
            } catch (e: Exception) {
                _uiMessage.emit("Error creating invoice: ${e.message}")
            }
        }
    }

    fun approveInvoice(invoice: InvoiceEntity) {
        viewModelScope.launch {
            repository.approveInvoice(invoice)
            _uiMessage.emit("Invoice ${invoice.invoiceNumber} approved!")
        }
    }

    fun updateInvoicePayment(invoiceId: Long, status: PaymentStatus, paidAmount: Double, method: PaymentMethod) {
        viewModelScope.launch {
            repository.updateInvoicePayment(invoiceId, status, paidAmount, method)
            _uiMessage.emit("Payment record updated!")
        }
    }

    // --- Employee & Attendance Actions ---
    fun addEmployee(
        fullName: String,
        mobileNumber: String,
        employeeCode: String,
        designation: String,
        role: UserRole,
        joiningDate: String,
        salaryType: SalaryType,
        baseSalary: Double,
        overtimeRate: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.addEmployee(
                    EmployeeEntity(
                        fullName = fullName,
                        mobileNumber = mobileNumber,
                        employeeCode = employeeCode,
                        designation = designation,
                        role = role,
                        joiningDate = joiningDate,
                        salaryType = salaryType,
                        baseSalary = baseSalary,
                        overtimeHourlyRate = overtimeRate
                    )
                )
                _uiMessage.emit("Employee $fullName registered successfully!")
                onSuccess()
            } catch (e: Exception) {
                _uiMessage.emit("Failed to add employee: ${e.message}")
            }
        }
    }

    fun toggleEmployeeStatus(employee: EmployeeEntity) {
        viewModelScope.launch {
            repository.toggleEmployeeStatus(employee)
            val state = if (!employee.isActive) "activated" else "deactivated"
            _uiMessage.emit("${employee.fullName} account has been $state")
        }
    }

    fun recordPunchAttendance(
        employeeId: Long,
        employeeName: String,
        status: AttendanceStatus = AttendanceStatus.PRESENT,
        overtimeHours: Double = 0.0,
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                repository.recordAttendance(employeeId, employeeName, status, overtimeHours, notes)
                _uiMessage.emit("Attendance punched successfully!")
            } catch (e: Exception) {
                _uiMessage.emit("Error punching attendance: ${e.message}")
            }
        }
    }

    fun approveAttendance(attendance: AttendanceEntity, approved: Boolean) {
        viewModelScope.launch {
            repository.approveAttendance(attendance, approved)
            val st = if (approved) "Approved" else "Rejected"
            _uiMessage.emit("Attendance for ${attendance.employeeName} marked as $st")
        }
    }

    // --- Salary Actions ---
    fun runPayrollCalculation(monthYear: String) {
        viewModelScope.launch {
            try {
                repository.calculateAndGenerateSalaries(monthYear)
                _uiMessage.emit("Payroll calculation completed for $monthYear!")
            } catch (e: Exception) {
                _uiMessage.emit("Payroll calculation failed: ${e.message}")
            }
        }
    }

    // --- Inventory Actions ---
    fun addOrUpdatePart(part: SparePartEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.addOrUpdatePart(part)
            _uiMessage.emit("Spare part ${part.name} saved!")
            onSuccess()
        }
    }

    fun deletePart(part: SparePartEntity) {
        viewModelScope.launch {
            repository.deletePart(part)
            _uiMessage.emit("Spare part removed")
        }
    }

    // --- Customer Feedback ---
    fun submitCustomerFeedback(feedback: FeedbackEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.submitFeedback(feedback)
            _uiMessage.emit("Feedback recorded. Thank you!")
            onSuccess()
        }
    }

    // --- Settings & Branding ---
    fun updateCompanySettings(settings: CompanySettingsEntity) {
        viewModelScope.launch {
            repository.updateCompanySettings(settings)
            _uiMessage.emit("Company settings and branding saved!")
        }
    }

    fun addServiceCategory(category: ServiceCategoryEntity) {
        viewModelScope.launch {
            repository.addServiceCategory(category)
            _uiMessage.emit("Category ${category.name} added!")
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
            _uiMessage.emit("All notifications marked as read")
        }
    }
}
