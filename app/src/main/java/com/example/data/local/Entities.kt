package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AttendanceStatus
import com.example.data.model.InvoiceItemRow
import com.example.data.model.JobPriority
import com.example.data.model.JobStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.SalaryType
import com.example.data.model.UsedPartItem
import com.example.data.model.UserRole

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val mobileNumber: String,
    val employeeCode: String,
    val designation: String,
    val role: UserRole,
    val joiningDate: String,
    val salaryType: SalaryType = SalaryType.MONTHLY,
    val baseSalary: Double,
    val overtimeHourlyRate: Double = 150.0,
    val isActive: Boolean = true,
    val canCreateInvoice: Boolean = true,
    val canCollectPayment: Boolean = true
)

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jobCode: String,
    val customerName: String,
    val customerMobile: String,
    val customerAddress: String,
    val applianceCategory: String,
    val applianceBrand: String,
    val modelNumber: String = "",
    val problemDescription: String,
    val serviceDate: String,
    val preferredTime: String = "10:00 AM - 01:00 PM",
    val priority: JobPriority = JobPriority.NORMAL,
    val assignedTechnicianId: Long? = null,
    val assignedTechnicianName: String = "",
    val assignedHelperId: Long? = null,
    val assignedHelperName: String = "",
    val serviceCharge: Double = 500.0,
    val additionalInstructions: String = "",
    val status: JobStatus = JobStatus.NEW,
    val statusNote: String = "",
    val partsUsed: List<UsedPartItem> = emptyList(),
    val workReportNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val jobId: Long,
    val jobCode: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val technicianName: String,
    val serviceDescription: String,
    val items: List<InvoiceItemRow>,
    val discount: Double = 0.0,
    val taxPercent: Double = 18.0,
    val taxAmount: Double = 0.0,
    val grandTotal: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val paidAmount: Double = 0.0,
    val balanceDue: Double = 0.0,
    val warrantyDetails: String = "90 Days warranty on parts, 30 days service warranty",
    val technicianSignature: String = "Digitally Signed",
    val companyStamp: String = "SMART REFRIGERATION VERIFIED",
    val status: String = "APPROVED", // DRAFT, APPROVED, CANCELLED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: Long,
    val employeeName: String,
    val date: String, // yyyy-MM-dd
    val checkInTime: String = "",
    val checkOutTime: String = "",
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val overtimeHours: Double = 0.0,
    val isApprovedByOwner: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "salaries")
data class SalaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: Long,
    val employeeName: String,
    val monthYear: String, // "October 2026"
    val baseSalary: Double,
    val presentDays: Int = 26,
    val halfDays: Int = 0,
    val absentDays: Int = 0,
    val leaveDays: Int = 0,
    val overtimeHours: Double = 0.0,
    val overtimePay: Double = 0.0,
    val incentives: Double = 0.0,
    val bonus: Double = 0.0,
    val advanceDeductions: Double = 0.0,
    val otherDeductions: Double = 0.0,
    val netSalary: Double = 0.0,
    val paidAmount: Double = 0.0,
    val pendingAmount: Double = 0.0,
    val paymentDate: String = "",
    val paymentMethod: String = "Bank Transfer",
    val status: String = "PAID" // PAID, PARTIALLY_PAID, UNPAID
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mobile: String,
    val address: String,
    val applianceCategory: String = "",
    val applianceBrand: String = "",
    val modelNumber: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "feedbacks")
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jobId: Long,
    val jobCode: String,
    val customerName: String,
    val customerMobile: String,
    val rating: Int = 5,
    val serviceQuality: Int = 5,
    val technicianBehaviour: Int = 5,
    val workSatisfaction: Int = 5,
    val writtenFeedback: String = "",
    val date: String = ""
)

@Entity(tableName = "spare_parts")
data class SparePartEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String,
    val category: String,
    val purchasePrice: Double,
    val sellingPrice: Double,
    val availableQuantity: Int,
    val minStockAlert: Int = 3
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // JOB_ASSIGNED, STATUS_UPDATE, INVOICE, SALARY, ATTENDANCE, ANNOUNCEMENT
    val targetRole: String = "ALL", // ALL, OWNER, TECHNICIAN, HELPER
    val targetEmployeeId: Long? = null,
    val isRead: Boolean = false
)

@Entity(tableName = "company_settings")
data class CompanySettingsEntity(
    @PrimaryKey val id: Int = 1,
    val companyName: String = "SMART REFRIGERATION",
    val tagline: String = "One App. Complete Service Control.",
    val phone: String = "+91 98765 43210",
    val email: String = "service@smartref.in",
    val address: String = "Shop #14, Cooling Tower Complex, Industrial Area, Sector 5",
    val city: String = "Mumbai, MH - 400001",
    val gstin: String = "27ABCDE1234F1Z5",
    val primaryColorHex: String = "#0F2537",
    val secondaryColorHex: String = "#00B4D8",
    val invoicePrefix: String = "SR-INV-",
    val termsNote: String = "1. 90-day warranty on replaced parts.\n2. 30-day warranty on technician workmanship.\n3. Electrical fluctuation damages not covered under warranty."
)

@Entity(tableName = "service_categories")
data class ServiceCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val defaultCharge: Double,
    val iconKey: String = "ac"
)
