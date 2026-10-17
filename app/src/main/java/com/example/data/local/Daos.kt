package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.JobStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees ORDER BY id ASC")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE isActive = 1 ORDER BY id ASC")
    fun getActiveEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE id = :id LIMIT 1")
    suspend fun getEmployeeById(id: Long): EmployeeEntity?

    @Query("SELECT * FROM employees WHERE mobileNumber = :mobile LIMIT 1")
    suspend fun getEmployeeByMobile(mobile: String): EmployeeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<EmployeeEntity>)

    @Update
    suspend fun updateEmployee(employee: EmployeeEntity)

    @Delete
    suspend fun deleteEmployee(employee: EmployeeEntity)
}

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY id DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE assignedTechnicianId = :techId OR assignedHelperId = :techId ORDER BY id DESC")
    fun getJobsForEmployee(techId: Long): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE status = :status ORDER BY id DESC")
    fun getJobsByStatus(status: JobStatus): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    suspend fun getJobById(id: Long): JobEntity?

    @Query("SELECT * FROM jobs WHERE customerName LIKE '%' || :query || '%' OR customerMobile LIKE '%' || :query || '%' OR jobCode LIKE '%' || :query || '%'")
    fun searchJobs(query: String): Flow<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobEntity>)

    @Update
    suspend fun updateJob(job: JobEntity)

    @Delete
    suspend fun deleteJob(job: JobEntity)
}

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY id DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getInvoiceById(id: Long): InvoiceEntity?

    @Query("SELECT * FROM invoices WHERE jobId = :jobId LIMIT 1")
    suspend fun getInvoiceByJobId(jobId: Long): InvoiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<InvoiceEntity>)

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)

    @Delete
    suspend fun deleteInvoice(invoice: InvoiceEntity)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY date DESC, id DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE employeeId = :empId ORDER BY date DESC")
    fun getAttendanceForEmployee(empId: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE employeeId = :empId AND date = :date LIMIT 1")
    suspend fun getAttendanceByDate(empId: Long, date: String): AttendanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attendances: List<AttendanceEntity>)

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Delete
    suspend fun deleteAttendance(attendance: AttendanceEntity)
}

@Dao
interface SalaryDao {
    @Query("SELECT * FROM salaries ORDER BY id DESC")
    fun getAllSalaries(): Flow<List<SalaryEntity>>

    @Query("SELECT * FROM salaries WHERE employeeId = :empId ORDER BY id DESC")
    fun getSalariesForEmployee(empId: Long): Flow<List<SalaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalary(salary: SalaryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalaries(salaries: List<SalaryEntity>)

    @Update
    suspend fun updateSalary(salary: SalaryEntity)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY id DESC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE mobile = :mobile LIMIT 1")
    suspend fun getCustomerByMobile(mobile: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR mobile LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<CustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<CustomerEntity>)

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)
}

@Dao
interface FeedbackDao {
    @Query("SELECT * FROM feedbacks ORDER BY id DESC")
    fun getAllFeedbacks(): Flow<List<FeedbackEntity>>

    @Query("SELECT * FROM feedbacks WHERE jobId = :jobId LIMIT 1")
    suspend fun getFeedbackByJobId(jobId: Long): FeedbackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedbacks(feedbacks: List<FeedbackEntity>)
}

@Dao
interface SparePartDao {
    @Query("SELECT * FROM spare_parts ORDER BY name ASC")
    fun getAllParts(): Flow<List<SparePartEntity>>

    @Query("SELECT * FROM spare_parts WHERE availableQuantity <= minStockAlert")
    fun getLowStockParts(): Flow<List<SparePartEntity>>

    @Query("SELECT * FROM spare_parts WHERE id = :id LIMIT 1")
    suspend fun getPartById(id: Long): SparePartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPart(part: SparePartEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParts(parts: List<SparePartEntity>)

    @Update
    suspend fun updatePart(part: SparePartEntity)

    @Delete
    suspend fun deletePart(part: SparePartEntity)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE targetRole = 'ALL' OR targetRole = :role OR targetEmployeeId = :empId ORDER BY timestamp DESC")
    fun getNotificationsForRole(role: String, empId: Long?): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM company_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<CompanySettingsEntity?>

    @Query("SELECT * FROM company_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): CompanySettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: CompanySettingsEntity)

    @Update
    suspend fun updateSettings(settings: CompanySettingsEntity)
}

@Dao
interface ServiceCategoryDao {
    @Query("SELECT * FROM service_categories ORDER BY id ASC")
    fun getAllCategories(): Flow<List<ServiceCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: ServiceCategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<ServiceCategoryEntity>)

    @Update
    suspend fun updateCategory(category: ServiceCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: ServiceCategoryEntity)
}
