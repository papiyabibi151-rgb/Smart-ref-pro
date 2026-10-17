package com.example.data.model

enum class UserRole(val displayName: String) {
    OWNER("Owner / Super Admin"),
    TECHNICIAN("Technician"),
    HELPER("Helper")
}

enum class JobStatus(val label: String) {
    NEW("New"),
    ASSIGNED("Assigned"),
    ACCEPTED("Accepted"),
    ON_THE_WAY("On The Way"),
    WORK_STARTED("Work Started"),
    WAITING_FOR_PARTS("Waiting for Parts"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class JobPriority(val label: String) {
    LOW("Low"),
    NORMAL("Normal"),
    HIGH("High"),
    URGENT("Urgent")
}

enum class PaymentStatus(val label: String) {
    PAID("Paid"),
    PARTIAL("Partially Paid"),
    UNPAID("Unpaid")
}

enum class PaymentMethod(val label: String) {
    CASH("Cash"),
    UPI("UPI"),
    BANK_TRANSFER("Bank Transfer"),
    OTHER("Other")
}

enum class AttendanceStatus(val label: String) {
    PRESENT("Present"),
    ABSENT("Absent"),
    HALF_DAY("Half Day"),
    LEAVE("Leave"),
    WEEKLY_OFF("Weekly Off"),
    OVERTIME("Overtime")
}

enum class SalaryType(val label: String) {
    MONTHLY("Monthly"),
    DAILY("Daily")
}

data class UsedPartItem(
    val partId: Long = 0,
    val partName: String = "",
    val partCode: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0
)

data class InvoiceItemRow(
    val description: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0
)

data class CompanyProfile(
    val name: String = "SMART REFRIGERATION",
    val tagline: String = "One App. Complete Service Control.",
    val phone: String = "+91 98765 43210",
    val email: String = "service@smartref.in",
    val address: String = "Shop #14, Cooling Tower Complex, Industrial Area, Sector 5",
    val city: String = "Mumbai, MH - 400001",
    val gstin: String = "27ABCDE1234F1Z5",
    val primaryColorHex: String = "#0F2537",
    val secondaryColorHex: String = "#00B4D8",
    val invoicePrefix: String = "SR-INV-",
    val termsNote: String = "1. Warranty covers parts replaced for 90 days.\n2. Service warranty valid for 30 days.\n3. Goods once sold will not be taken back."
)

data class AuthSession(
    val isLoggedIn: Boolean = false,
    val role: UserRole = UserRole.OWNER,
    val employeeId: Long? = null,
    val employeeName: String = "Owner",
    val mobileNumber: String = "9876543210"
)
