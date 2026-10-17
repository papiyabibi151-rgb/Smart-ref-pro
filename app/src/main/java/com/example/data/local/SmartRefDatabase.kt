package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        EmployeeEntity::class,
        JobEntity::class,
        InvoiceEntity::class,
        AttendanceEntity::class,
        SalaryEntity::class,
        CustomerEntity::class,
        FeedbackEntity::class,
        SparePartEntity::class,
        NotificationEntity::class,
        CompanySettingsEntity::class,
        ServiceCategoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(SmartRefConverters::class)
abstract class SmartRefDatabase : RoomDatabase() {

    abstract fun employeeDao(): EmployeeDao
    abstract fun jobDao(): JobDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun salaryDao(): SalaryDao
    abstract fun customerDao(): CustomerDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun sparePartDao(): SparePartDao
    abstract fun notificationDao(): NotificationDao
    abstract fun settingsDao(): SettingsDao
    abstract fun serviceCategoryDao(): ServiceCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: SmartRefDatabase? = null

        fun getDatabase(context: Context): SmartRefDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartRefDatabase::class.java,
                    "smart_ref_pro.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
