package com.example.data.repository

import com.example.data.local.AttendanceDao
import com.example.data.local.AttendanceEntity
import com.example.data.local.CompanySettingsEntity
import com.example.data.local.CustomerDao
import com.example.data.local.CustomerEntity
import com.example.data.local.EmployeeDao
import com.example.data.local.EmployeeEntity
import com.example.data.local.FeedbackDao
import com.example.data.local.FeedbackEntity
import com.example.data.local.InvoiceDao
import com.example.data.local.InvoiceEntity
import com.example.data.local.JobDao
import com.example.data.local.JobEntity
import com.example.data.local.NotificationDao
import com.example.data.local.NotificationEntity
import com.example.data.local.SalaryDao
import com.example.data.local.SalaryEntity
import com.example.data.local.ServiceCategoryDao
import com.example.data.local.ServiceCategoryEntity
import com.example.data.local.SettingsDao
import com.example.data.local.SmartRefDatabase
import com.example.data.local.SparePartDao
import com.example.data.local.SparePartEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.AuthSession
import com.example.data.model.CompanyProfile
import com.example.data.model.InvoiceItemRow
import com.example.data.model.JobPriority
import com.example.data.model.JobStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.SalaryType
import com.example.data.model.UsedPartItem
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmartRefRepository(
    private val database: SmartRefDatabase,
    private val appScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val employeeDao: EmployeeDao = database.employeeDao()
    private val jobDao: JobDao = database.jobDao()
    private val invoiceDao: InvoiceDao = database.invoiceDao()
    private val attendanceDao: AttendanceDao = database.attendanceDao()
    private val salaryDao: SalaryDao = database.salaryDao()
    private val customerDao: CustomerDao = database.customerDao()
    private val feedbackDao: FeedbackDao = database.feedbackDao()
    private val sparePartDao: SparePartDao = database.sparePartDao()
    private val notificationDao: NotificationDao = database.notificationDao()
    private val settingsDao: SettingsDao = database.settingsDao()
    private val categoryDao: ServiceCategoryDao = database.serviceCategoryDao()

    private val _currentSession = MutableStateFlow(
        AuthSession(
            isLoggedIn = true,
            role = UserRole.OWNER,
            employeeName = "SMART REFRIGERATION Owner",
            mobileNumber = "9876543210"
        )
    )
    val currentSession = _currentSession.asStateFlow()

    init {
        appScope.launch {
            seedInitialDataIfEmpty()
        }
    }

    // --- Flows ---
    val allEmployees: Flow<List<EmployeeEntity>> = employeeDao.getAllEmployees()
    val allJobs: Flow<List<JobEntity>> = jobDao.getAllJobs()
    val allInvoices: Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoices()
    val allAttendance: Flow<List<AttendanceEntity>> = attendanceDao.getAllAttendance()
    val allSalaries: Flow<List<SalaryEntity>> = salaryDao.getAllSalaries()
    val allCustomers: Flow<List<CustomerEntity>> = customerDao.getAllCustomers()
    val allFeedbacks: Flow<List<FeedbackEntity>> = feedbackDao.getAllFeedbacks()
    val allParts: Flow<List<SparePartEntity>> = sparePartDao.getAllParts()
    val lowStockParts: Flow<List<SparePartEntity>> = sparePartDao.getLowStockParts()
    val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    val companySettings: Flow<CompanySettingsEntity?> = settingsDao.getSettings()
    val serviceCategories: Flow<List<ServiceCategoryEntity>> = categoryDao.getAllCategories()

    fun getJobsForCurrentEmployee(empId: Long): Flow<List<JobEntity>> {
        return jobDao.getJobsForEmployee(empId)
    }

    fun getAttendanceForCurrentEmployee(empId: Long): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceForEmployee(empId)
    }

    fun getSalariesForCurrentEmployee(empId: Long): Flow<List<SalaryEntity>> {
        return salaryDao.getSalariesForEmployee(empId)
    }

    // --- Auth Management ---
    suspend fun loginWithMobile(mobile: String, role: UserRole): Result<AuthSession> {
        val cleanMobile = mobile.trim().replace(" ", "").replace("+91", "")
        if (cleanMobile.length < 10) {
            return Result.failure(Exception("Please enter a valid 10-digit mobile number"))
        }

        if (role == UserRole.OWNER) {
            val settings = settingsDao.getSettingsSync()
            val ownerMobile = settings?.phone?.replace(" ", "")?.replace("+91", "") ?: "9876543210"
            // Allow owner login with registered number
            val session = AuthSession(
                isLoggedIn = true,
                role = UserRole.OWNER,
                employeeName = "Company Owner",
                mobileNumber = cleanMobile
            )
            _currentSession.value = session
            return Result.success(session)
        } else {
            // Find registered employee
            val employee = employeeDao.getEmployeeByMobile(cleanMobile)
            if (employee == null) {
                return Result.failure(Exception("Mobile number $cleanMobile is not registered with SMART REFRIGERATION. Contact Owner."))
            }
            if (!employee.isActive) {
                return Result.failure(Exception("Your employee account has been deactivated. Please contact the Owner."))
            }
            if (employee.role != role) {
                return Result.failure(Exception("Account is registered as ${employee.role.displayName}, not as ${role.displayName}."))
            }

            val session = AuthSession(
                isLoggedIn = true,
                role = employee.role,
                employeeId = employee.id,
                employeeName = employee.fullName,
                mobileNumber = cleanMobile
            )
            _currentSession.value = session
            return Result.success(session)
        }
    }

    fun switchSession(session: AuthSession) {
        _currentSession.value = session
    }

    fun logout() {
        _currentSession.value = AuthSession(isLoggedIn = false)
    }

    // --- Job Actions ---
    suspend fun createJob(
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
        instructions: String
    ): Long {
        val jobCode = "SR-JOB-" + (1000 + (Math.random() * 9000).toInt())
        val job = JobEntity(
            jobCode = jobCode,
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
            assignedTechnicianId = assignedTechId,
            assignedTechnicianName = assignedTechName,
            assignedHelperId = assignedHelperId,
            assignedHelperName = assignedHelperName,
            serviceCharge = serviceCharge,
            additionalInstructions = instructions,
            status = if (assignedTechId != null) JobStatus.ASSIGNED else JobStatus.NEW
        )
        val jobId = jobDao.insertJob(job)

        // Save customer if not exists
        val existingCustomer = customerDao.getCustomerByMobile(customerMobile)
        if (existingCustomer == null) {
            customerDao.insertCustomer(
                CustomerEntity(
                    name = customerName,
                    mobile = customerMobile,
                    address = customerAddress,
                    applianceCategory = applianceCategory,
                    applianceBrand = applianceBrand,
                    modelNumber = modelNumber
                )
            )
        }

        // Send notifications
        if (assignedTechId != null) {
            notificationDao.insertNotification(
                NotificationEntity(
                    title = "New Job Assigned: $jobCode",
                    message = "Customer: $customerName for $applianceCategory ($applianceBrand). Scheduled: $serviceDate",
                    type = "JOB_ASSIGNED",
                    targetRole = "TECHNICIAN",
                    targetEmployeeId = assignedTechId
                )
            )
        }

        return jobId
    }

    suspend fun updateJobStatus(
        jobId: Long,
        newStatus: JobStatus,
        statusNote: String = "",
        usedParts: List<UsedPartItem>? = null,
        workNotes: String = ""
    ) {
        val job = jobDao.getJobById(jobId) ?: return
        val updatedParts = usedParts ?: job.partsUsed
        val completedTimestamp = if (newStatus == JobStatus.COMPLETED) System.currentTimeMillis() else job.completedAt

        val updated = job.copy(
            status = newStatus,
            statusNote = statusNote.ifEmpty { "Status updated to ${newStatus.label}" },
            partsUsed = updatedParts,
            workReportNotes = if (workNotes.isNotEmpty()) workNotes else job.workReportNotes,
            completedAt = completedTimestamp
        )
        jobDao.updateJob(updated)

        // Deduct inventory if parts were used and job is completed
        if (newStatus == JobStatus.COMPLETED && updatedParts.isNotEmpty()) {
            for (part in updatedParts) {
                val stockPart = sparePartDao.getPartById(part.partId)
                if (stockPart != null) {
                    val newQty = (stockPart.availableQuantity - part.quantity).coerceAtLeast(0)
                    sparePartDao.updatePart(stockPart.copy(availableQuantity = newQty))
                }
            }
        }

        // Notify Owner
        notificationDao.insertNotification(
            NotificationEntity(
                title = "Job ${job.jobCode} Update",
                message = "${job.assignedTechnicianName} updated status to ${newStatus.label}: ${customerAddressShort(job.customerAddress)}",
                type = "STATUS_UPDATE",
                targetRole = "OWNER"
            )
        )
    }

    suspend fun updateJob(job: JobEntity) {
        jobDao.updateJob(job)
    }

    suspend fun deleteJob(job: JobEntity) {
        jobDao.deleteJob(job)
    }

    // --- Invoice Actions ---
    suspend fun createInvoice(
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
        isDraft: Boolean
    ): Long {
        val subtotal = items.sumOf { it.totalPrice }
        val discountedSubtotal = (subtotal - discount).coerceAtLeast(0.0)
        val taxAmount = (discountedSubtotal * taxPercent) / 100.0
        val grandTotal = discountedSubtotal + taxAmount
        val balance = (grandTotal - paidAmount).coerceAtLeast(0.0)

        val settings = settingsDao.getSettingsSync()
        val prefix = settings?.invoicePrefix ?: "SR-INV-"
        val invNumber = prefix + (2000 + (Math.random() * 8000).toInt())

        val invoice = InvoiceEntity(
            invoiceNumber = invNumber,
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
            taxAmount = taxAmount,
            grandTotal = grandTotal,
            paymentStatus = paymentStatus,
            paymentMethod = paymentMethod,
            paidAmount = paidAmount,
            balanceDue = balance,
            status = if (isDraft) "DRAFT" else "APPROVED"
        )

        val id = invoiceDao.insertInvoice(invoice)

        if (isDraft) {
            notificationDao.insertNotification(
                NotificationEntity(
                    title = "New Draft Invoice: $invNumber",
                    message = "$technicianName created draft invoice for Job $jobCode (₹$grandTotal)",
                    type = "INVOICE",
                    targetRole = "OWNER"
                )
            )
        }

        return id
    }

    suspend fun approveInvoice(invoice: InvoiceEntity) {
        invoiceDao.updateInvoice(invoice.copy(status = "APPROVED"))
    }

    suspend fun updateInvoicePayment(invoiceId: Long, status: PaymentStatus, paid: Double, method: PaymentMethod) {
        val invoice = invoiceDao.getInvoiceById(invoiceId) ?: return
        val balance = (invoice.grandTotal - paid).coerceAtLeast(0.0)
        invoiceDao.updateInvoice(
            invoice.copy(
                paymentStatus = status,
                paidAmount = paid,
                balanceDue = balance,
                paymentMethod = method
            )
        )
    }

    // --- Employee & Attendance Actions ---
    suspend fun addEmployee(employee: EmployeeEntity): Long {
        return employeeDao.insertEmployee(employee)
    }

    suspend fun updateEmployee(employee: EmployeeEntity) {
        employeeDao.updateEmployee(employee)
    }

    suspend fun toggleEmployeeStatus(employee: EmployeeEntity) {
        employeeDao.updateEmployee(employee.copy(isActive = !employee.isActive))
    }

    suspend fun recordAttendance(
        employeeId: Long,
        employeeName: String,
        status: AttendanceStatus,
        overtimeHours: Double = 0.0,
        notes: String = ""
    ) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        val existing = attendanceDao.getAttendanceByDate(employeeId, today)
        if (existing == null) {
            attendanceDao.insertAttendance(
                AttendanceEntity(
                    employeeId = employeeId,
                    employeeName = employeeName,
                    date = today,
                    checkInTime = timeNow,
                    status = status,
                    overtimeHours = overtimeHours,
                    isApprovedByOwner = false,
                    notes = notes
                )
            )
        } else {
            // Punch out update
            attendanceDao.updateAttendance(
                existing.copy(
                    checkOutTime = timeNow,
                    status = status,
                    overtimeHours = overtimeHours,
                    notes = notes.ifEmpty { existing.notes }
                )
            )
        }
    }

    suspend fun approveAttendance(attendance: AttendanceEntity, approved: Boolean) {
        attendanceDao.updateAttendance(attendance.copy(isApprovedByOwner = approved))
    }

    // --- Salary Actions ---
    suspend fun calculateAndGenerateSalaries(monthYear: String) {
        val employees = employeeDao.getAllEmployees().first()
        val allAtt = attendanceDao.getAllAttendance().first()

        for (emp in employees) {
            val empAttendance = allAtt.filter { it.employeeId == emp.id }
            val presentCount = empAttendance.count { it.status == AttendanceStatus.PRESENT }
            val halfDays = empAttendance.count { it.status == AttendanceStatus.HALF_DAY }
            val absentCount = empAttendance.count { it.status == AttendanceStatus.ABSENT }
            val leaveCount = empAttendance.count { it.status == AttendanceStatus.LEAVE }
            val overtimeTotalHours = empAttendance.sumOf { it.overtimeHours }
            val overtimePay = overtimeTotalHours * emp.overtimeHourlyRate

            val effectiveDays = presentCount + (halfDays * 0.5)
            val baseSalaryCalculated = if (emp.salaryType == SalaryType.DAILY) {
                effectiveDays * emp.baseSalary
            } else {
                // Monthly with prorated attendance
                (emp.baseSalary / 26.0) * effectiveDays.coerceAtLeast(1.0)
            }

            val incentives = if (presentCount >= 24) 1000.0 else 0.0
            val net = baseSalaryCalculated + overtimePay + incentives

            val salaryRecord = SalaryEntity(
                employeeId = emp.id,
                employeeName = emp.fullName,
                monthYear = monthYear,
                baseSalary = emp.baseSalary,
                presentDays = presentCount,
                halfDays = halfDays,
                absentDays = absentCount,
                leaveDays = leaveCount,
                overtimeHours = overtimeTotalHours,
                overtimePay = overtimePay,
                incentives = incentives,
                netSalary = net,
                paidAmount = net,
                pendingAmount = 0.0,
                paymentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                status = "PAID"
            )
            salaryDao.insertSalary(salaryRecord)
        }
    }

    suspend fun updateSalary(salary: SalaryEntity) {
        salaryDao.updateSalary(salary)
    }

    // --- Inventory Actions ---
    suspend fun addOrUpdatePart(part: SparePartEntity) {
        if (part.id == 0L) {
            sparePartDao.insertPart(part)
        } else {
            sparePartDao.updatePart(part)
        }
    }

    suspend fun deletePart(part: SparePartEntity) {
        sparePartDao.deletePart(part)
    }

    // --- Feedback ---
    suspend fun submitFeedback(feedback: FeedbackEntity) {
        feedbackDao.insertFeedback(feedback)
        notificationDao.insertNotification(
            NotificationEntity(
                title = "New Customer Review (${feedback.rating}★)",
                message = "${feedback.customerName} reviewed Job ${feedback.jobCode}: \"${feedback.writtenFeedback}\"",
                type = "FEEDBACK",
                targetRole = "OWNER"
            )
        )
    }

    // --- Settings & Branding ---
    suspend fun updateCompanySettings(settings: CompanySettingsEntity) {
        settingsDao.updateSettings(settings)
    }

    suspend fun addServiceCategory(category: ServiceCategoryEntity) {
        categoryDao.insertCategory(category)
    }

    suspend fun markNotificationRead(id: Long) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsRead() {
        notificationDao.markAllAsRead()
    }

    // --- Initial Demo / Seeder Data ---
    private suspend fun seedInitialDataIfEmpty() {
        val settings = settingsDao.getSettingsSync()
        if (settings == null) {
            settingsDao.insertSettings(CompanySettingsEntity())
        }

        val employees = employeeDao.getAllEmployees().first()
        if (employees.isEmpty()) {
            val seededEmployees = listOf(
                EmployeeEntity(
                    fullName = "Ramesh Kumar",
                    mobileNumber = "9812345671",
                    employeeCode = "SR-TECH-01",
                    designation = "Senior HVAC Technician",
                    role = UserRole.TECHNICIAN,
                    joiningDate = "15 Jan 2024",
                    salaryType = SalaryType.MONTHLY,
                    baseSalary = 28000.0,
                    overtimeHourlyRate = 200.0,
                    isActive = true
                ),
                EmployeeEntity(
                    fullName = "Amit Sharma",
                    mobileNumber = "9812345672",
                    employeeCode = "SR-TECH-02",
                    designation = "Refrigeration Specialist",
                    role = UserRole.TECHNICIAN,
                    joiningDate = "10 Mar 2024",
                    salaryType = SalaryType.MONTHLY,
                    baseSalary = 26000.0,
                    overtimeHourlyRate = 180.0,
                    isActive = true
                ),
                EmployeeEntity(
                    fullName = "Rajesh Singh",
                    mobileNumber = "9812345673",
                    employeeCode = "SR-TECH-03",
                    designation = "Inverter AC Technician",
                    role = UserRole.TECHNICIAN,
                    joiningDate = "01 Jun 2024",
                    salaryType = SalaryType.MONTHLY,
                    baseSalary = 24000.0,
                    overtimeHourlyRate = 160.0,
                    isActive = true
                ),
                EmployeeEntity(
                    fullName = "Rahul Verma",
                    mobileNumber = "9812345674",
                    employeeCode = "SR-HELP-01",
                    designation = "Service Helper",
                    role = UserRole.HELPER,
                    joiningDate = "01 Aug 2024",
                    salaryType = SalaryType.MONTHLY,
                    baseSalary = 16000.0,
                    overtimeHourlyRate = 120.0,
                    isActive = true
                ),
                EmployeeEntity(
                    fullName = "Sunita Das",
                    mobileNumber = "9812345675",
                    employeeCode = "SR-HELP-02",
                    designation = "Electrical Helper",
                    role = UserRole.HELPER,
                    joiningDate = "15 Sep 2024",
                    salaryType = SalaryType.MONTHLY,
                    baseSalary = 15000.0,
                    overtimeHourlyRate = 120.0,
                    isActive = true
                )
            )
            employeeDao.insertEmployees(seededEmployees)

            // Seed Categories
            val categories = listOf(
                ServiceCategoryEntity(name = "Split AC", defaultCharge = 650.0, iconKey = "ac"),
                ServiceCategoryEntity(name = "Window AC", defaultCharge = 500.0, iconKey = "ac"),
                ServiceCategoryEntity(name = "Inverter AC", defaultCharge = 750.0, iconKey = "ac"),
                ServiceCategoryEntity(name = "Refrigerator", defaultCharge = 550.0, iconKey = "fridge"),
                ServiceCategoryEntity(name = "Deep Freezer", defaultCharge = 800.0, iconKey = "fridge"),
                ServiceCategoryEntity(name = "Washing Machine", defaultCharge = 500.0, iconKey = "wash"),
                ServiceCategoryEntity(name = "Water Purifier (RO)", defaultCharge = 450.0, iconKey = "water"),
                ServiceCategoryEntity(name = "Microwave Oven", defaultCharge = 400.0, iconKey = "oven")
            )
            categoryDao.insertCategories(categories)

            // Seed Spare Parts
            val parts = listOf(
                SparePartEntity(name = "R32 Refrigerant Gas Cylinder (1kg)", code = "GAS-R32", category = "Gas", purchasePrice = 450.0, sellingPrice = 850.0, availableQuantity = 12, minStockAlert = 4),
                SparePartEntity(name = "R410A Refrigerant Gas (1kg)", code = "GAS-R410", category = "Gas", purchasePrice = 500.0, sellingPrice = 950.0, availableQuantity = 8, minStockAlert = 3),
                SparePartEntity(name = "AC Dual Run Capacitor 45+5 uF", code = "CAP-45UF", category = "Electrical", purchasePrice = 180.0, sellingPrice = 450.0, availableQuantity = 15, minStockAlert = 5),
                SparePartEntity(name = "Refrigerator PTC Relay & OLP Kit", code = "RELAY-PTC", category = "Electrical", purchasePrice = 120.0, sellingPrice = 350.0, availableQuantity = 20, minStockAlert = 6),
                SparePartEntity(name = "Copper Pipe 1/2 inch (per meter)", code = "COP-050", category = "Piping", purchasePrice = 280.0, sellingPrice = 450.0, availableQuantity = 35, minStockAlert = 10),
                SparePartEntity(name = "Thermostat Switch Danfoss Type", code = "THERM-01", category = "Controls", purchasePrice = 160.0, sellingPrice = 420.0, availableQuantity = 2, minStockAlert = 5), // Low stock
                SparePartEntity(name = "AC Indoor Blower Fan Motor", code = "MOT-BLOW", category = "Motors", purchasePrice = 1100.0, sellingPrice = 1950.0, availableQuantity = 4, minStockAlert = 2)
            )
            sparePartDao.insertParts(parts)

            // Seed Initial Jobs
            val jobs = listOf(
                JobEntity(
                    jobCode = "SR-JOB-1021",
                    customerName = "Vikram Malhotra",
                    customerMobile = "9820011223",
                    customerAddress = "Flat 402, Green Valley Heights, Powai, Mumbai",
                    applianceCategory = "Split AC",
                    applianceBrand = "Daikin 1.5 Ton",
                    modelNumber = "FTKF50TV16U",
                    problemDescription = "Cooling not effective, ice forming on copper pipe",
                    serviceDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                    preferredTime = "10:30 AM",
                    priority = JobPriority.HIGH,
                    assignedTechnicianId = 1L,
                    assignedTechnicianName = "Ramesh Kumar",
                    assignedHelperId = 4L,
                    assignedHelperName = "Rahul Verma",
                    serviceCharge = 650.0,
                    status = JobStatus.ON_THE_WAY,
                    statusNote = "Technician and helper traveling to customer site"
                ),
                JobEntity(
                    jobCode = "SR-JOB-1022",
                    customerName = "Pooja Patel",
                    customerMobile = "9820044556",
                    customerAddress = "Villa 12, Palm Meadows, Andheri East, Mumbai",
                    applianceCategory = "Refrigerator",
                    applianceBrand = "Samsung Double Door 324L",
                    modelNumber = "RT34T4513S8",
                    problemDescription = "Lower compartment not cooling, clicking noise from back compressor",
                    serviceDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                    preferredTime = "02:00 PM",
                    priority = JobPriority.URGENT,
                    assignedTechnicianId = 2L,
                    assignedTechnicianName = "Amit Sharma",
                    assignedHelperId = 5L,
                    assignedHelperName = "Sunita Das",
                    serviceCharge = 550.0,
                    status = JobStatus.WORK_STARTED,
                    statusNote = "Diagnosis completed. Testing PTC Relay & Thermostat"
                ),
                JobEntity(
                    jobCode = "SR-JOB-1020",
                    customerName = "Anand Mehra",
                    customerMobile = "9820077889",
                    customerAddress = "B-301, Silver Arch Apartments, Malad West, Mumbai",
                    applianceCategory = "Inverter AC",
                    applianceBrand = "Voltas 2 Ton",
                    modelNumber = "24V DY",
                    problemDescription = "Full gas charging and jet pump chemical wash completed",
                    serviceDate = "15 Oct 2026",
                    preferredTime = "11:00 AM",
                    priority = JobPriority.NORMAL,
                    assignedTechnicianId = 1L,
                    assignedTechnicianName = "Ramesh Kumar",
                    serviceCharge = 750.0,
                    status = JobStatus.COMPLETED,
                    statusNote = "Work verified and invoice generated",
                    partsUsed = listOf(
                        UsedPartItem(partId = 1L, partName = "R32 Refrigerant Gas Cylinder (1kg)", partCode = "GAS-R32", quantity = 1, unitPrice = 850.0, totalPrice = 850.0),
                        UsedPartItem(partId = 3L, partName = "AC Dual Run Capacitor 45+5 uF", partCode = "CAP-45UF", quantity = 1, unitPrice = 450.0, totalPrice = 450.0)
                    ),
                    workReportNotes = "Pressure tested at 250 psi. Nitrogen leak test passed. Sub-cooling normal at 6.2 deg C.",
                    completedAt = System.currentTimeMillis() - 86400000L
                )
            )
            jobDao.insertJobs(jobs)

            // Seed Completed Job Invoice
            val invoiceItems = listOf(
                InvoiceItemRow(description = "Inverter AC Comprehensive Jet Service & Pressure Test", quantity = 1, unitPrice = 750.0, totalPrice = 750.0),
                InvoiceItemRow(description = "R32 Pure Refrigerant Gas Top-Up (1kg)", quantity = 1, unitPrice = 850.0, totalPrice = 850.0),
                InvoiceItemRow(description = "Heavy Duty Motor Run Capacitor 45uF Replacement", quantity = 1, unitPrice = 450.0, totalPrice = 450.0)
            )
            val completedInvoice = InvoiceEntity(
                invoiceNumber = "SR-INV-2045",
                jobId = 3L,
                jobCode = "SR-JOB-1020",
                customerName = "Anand Mehra",
                customerPhone = "9820077889",
                customerAddress = "B-301, Silver Arch Apartments, Malad West, Mumbai",
                technicianName = "Ramesh Kumar",
                serviceDescription = "Inverter AC Jet Cleaning and R32 Gas Top-up",
                items = invoiceItems,
                discount = 100.0,
                taxPercent = 18.0,
                taxAmount = 351.0,
                grandTotal = 2301.0,
                paymentStatus = PaymentStatus.PAID,
                paymentMethod = PaymentMethod.UPI,
                paidAmount = 2301.0,
                balanceDue = 0.0,
                status = "APPROVED"
            )
            invoiceDao.insertInvoice(completedInvoice)

            // Seed Customers
            customerDao.insertCustomers(
                listOf(
                    CustomerEntity(name = "Vikram Malhotra", mobile = "9820011223", address = "Flat 402, Green Valley Heights, Powai, Mumbai", applianceCategory = "Split AC", applianceBrand = "Daikin"),
                    CustomerEntity(name = "Pooja Patel", mobile = "9820044556", address = "Villa 12, Palm Meadows, Andheri East, Mumbai", applianceCategory = "Refrigerator", applianceBrand = "Samsung"),
                    CustomerEntity(name = "Anand Mehra", mobile = "9820077889", address = "B-301, Silver Arch Apartments, Malad West, Mumbai", applianceCategory = "Inverter AC", applianceBrand = "Voltas")
                )
            )

            // Seed Feedback
            feedbackDao.insertFeedback(
                FeedbackEntity(
                    jobId = 3L,
                    jobCode = "SR-JOB-1020",
                    customerName = "Anand Mehra",
                    customerMobile = "9820077889",
                    rating = 5,
                    serviceQuality = 5,
                    technicianBehaviour = 5,
                    workSatisfaction = 5,
                    writtenFeedback = "Ramesh arrived on time and did a thorough jet chemical wash. AC cooling is now super fast like brand new!",
                    date = "15 Oct 2026"
                )
            )

            // Seed Attendance
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val seededAttendance = listOf(
                AttendanceEntity(employeeId = 1L, employeeName = "Ramesh Kumar", date = todayStr, checkInTime = "09:15 AM", status = AttendanceStatus.PRESENT, isApprovedByOwner = true),
                AttendanceEntity(employeeId = 2L, employeeName = "Amit Sharma", date = todayStr, checkInTime = "09:30 AM", status = AttendanceStatus.PRESENT, isApprovedByOwner = true),
                AttendanceEntity(employeeId = 3L, employeeName = "Rajesh Singh", date = todayStr, checkInTime = "09:45 AM", status = AttendanceStatus.PRESENT, isApprovedByOwner = true),
                AttendanceEntity(employeeId = 4L, employeeName = "Rahul Verma", date = todayStr, checkInTime = "09:20 AM", status = AttendanceStatus.PRESENT, isApprovedByOwner = true),
                AttendanceEntity(employeeId = 5L, employeeName = "Sunita Das", date = todayStr, checkInTime = "09:35 AM", status = AttendanceStatus.PRESENT, isApprovedByOwner = true)
            )
            attendanceDao.insertAll(seededAttendance)

            // Seed Notifications
            notificationDao.insertNotifications(
                listOf(
                    NotificationEntity(
                        title = "Welcome to SMART REF PRO",
                        message = "System initialized for SMART REFRIGERATION. All employee and job tracking systems ready.",
                        type = "ANNOUNCEMENT",
                        targetRole = "ALL"
                    ),
                    NotificationEntity(
                        title = "Low Stock Alert: Thermostat Switch",
                        message = "Only 2 units remaining in stock. Please reorder from distributor.",
                        type = "STOCK",
                        targetRole = "OWNER"
                    )
                )
            )
        }
    }

    private fun customerAddressShort(address: String): String {
        return if (address.length > 25) address.take(25) + "..." else address
    }
}
