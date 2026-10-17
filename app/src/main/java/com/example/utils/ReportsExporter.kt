package com.example.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.local.AttendanceEntity
import com.example.data.local.InvoiceEntity
import com.example.data.local.JobEntity
import com.example.data.local.SalaryEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportsExporter {

    fun exportBusinessCsv(
        context: Context,
        jobs: List<JobEntity>,
        invoices: List<InvoiceEntity>,
        salaries: List<SalaryEntity>
    ): File? {
        val outputDir = File(context.cacheDir, "reports")
        if (!outputDir.exists()) outputDir.mkdirs()
        val file = File(outputDir, "SmartRef_Business_Report_${System.currentTimeMillis()}.csv")

        try {
            val fos = FileOutputStream(file)
            val sb = StringBuilder()

            // Header
            sb.append("SMART REFRIGERATION - BUSINESS PERFORMANCE REPORT\n")
            sb.append("Generated on: ${SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())}\n\n")

            // Invoices Summary
            sb.append("--- INVOICE & REVENUE RECORDS ---\n")
            sb.append("Invoice No,Job Ref,Customer,Mobile,Technician,Subtotal,Discount,Tax,Grand Total,Paid Amount,Balance Due,Status,Payment Mode\n")
            for (inv in invoices) {
                sb.append("${inv.invoiceNumber},${inv.jobCode},\"${inv.customerName}\",${inv.customerPhone},\"${inv.technicianName}\",")
                sb.append("${inv.items.sumOf { it.totalPrice }},${inv.discount},${inv.taxAmount},${inv.grandTotal},${inv.paidAmount},${inv.balanceDue},${inv.paymentStatus.label},${inv.paymentMethod.label}\n")
            }

            sb.append("\n--- JOB DISTRIBUTION RECORDS ---\n")
            sb.append("Job ID,Customer,Phone,Address,Appliance,Brand,Status,Technician,Helper,Service Charge\n")
            for (job in jobs) {
                sb.append("${job.jobCode},\"${job.customerName}\",${job.customerMobile},\"${job.customerAddress.replace(",", " ")}\",")
                sb.append("\"${job.applianceCategory}\",\"${job.applianceBrand}\",${job.status.label},\"${job.assignedTechnicianName}\",\"${job.assignedHelperName}\",${job.serviceCharge}\n")
            }

            fos.write(sb.toString().toByteArray())
            fos.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Smart Ref Business Report CSV")
                putExtra(Intent.EXTRA_TEXT, "Exported business data from SMART REF PRO.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "Share Business Report")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
