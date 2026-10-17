package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.local.CompanySettingsEntity
import com.example.data.local.SalaryEntity
import java.io.File
import java.io.FileOutputStream

object PdfSalarySlipGenerator {

    fun generateAndShareSalarySlip(
        context: Context,
        salary: SalaryEntity,
        settings: CompanySettingsEntity?
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        drawSalarySlip(canvas, salary, settings)

        pdfDocument.finishPage(page)

        val outputDir = File(context.cacheDir, "salaries")
        if (!outputDir.exists()) outputDir.mkdirs()
        val file = File(outputDir, "Salary_${salary.employeeName.replace(" ", "_")}_${salary.monthYear.replace(" ", "_")}.pdf")

        try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Salary Slip ${salary.monthYear} - ${salary.employeeName}")
                putExtra(Intent.EXTRA_TEXT, "Attached is the monthly salary slip for ${salary.employeeName}.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "Share Salary Slip")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            return null
        }
    }

    private fun drawSalarySlip(
        canvas: Canvas,
        salary: SalaryEntity,
        settings: CompanySettingsEntity?
    ) {
        val navyPaint = Paint().apply {
            color = Color.rgb(15, 37, 55)
            style = Paint.Style.FILL
        }
        val cyanPaint = Paint().apply {
            color = Color.rgb(0, 180, 216)
            style = Paint.Style.FILL
        }
        val boldPaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        val textPaint = Paint().apply {
            color = Color.rgb(51, 65, 85)
            textSize = 9.5f
            isAntiAlias = true
        }

        // Header
        canvas.drawRect(0f, 0f, 595f, 90f, navyPaint)
        canvas.drawRect(0f, 86f, 595f, 90f, cyanPaint)

        val headerText = Paint().apply {
            color = Color.WHITE
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText(settings?.companyName ?: "SMART REFRIGERATION", 30f, 40f, headerText)
        canvas.drawText("CONFIDENTIAL SALARY PAYSLIP", 30f, 60f, Paint().apply {
            color = Color.rgb(224, 247, 250)
            textSize = 10f
            isAntiAlias = true
        })
        canvas.drawText("PAY PERIOD: ${salary.monthYear.uppercase()}", 380f, 45f, Paint().apply {
            color = Color.WHITE
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        })

        var y = 120f
        // Employee Box
        val boxPaint = Paint().apply {
            color = Color.rgb(241, 245, 249)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(30f, y, 565f, y + 65f, 6f, 6f, boxPaint)
        boldPaint.textSize = 11f
        canvas.drawText("Employee Name: ${salary.employeeName}", 45f, y + 25f, boldPaint)
        textPaint.textSize = 9.5f
        canvas.drawText("Employee ID: SR-EMP-${salary.employeeId}", 45f, y + 45f, textPaint)
        canvas.drawText("Payment Date: ${salary.paymentDate}", 350f, y + 25f, textPaint)
        canvas.drawText("Payment Mode: ${salary.paymentMethod} (Status: ${salary.status})", 350f, y + 45f, textPaint)

        y += 85f

        // Attendance summary
        boldPaint.textSize = 10f
        canvas.drawText("ATTENDANCE SUMMARY FOR THE MONTH", 30f, y, boldPaint)
        y += 15f
        canvas.drawRect(30f, y, 565f, y + 22f, navyPaint)
        val thPaint = Paint().apply {
            color = Color.WHITE
            textSize = 9f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("PRESENT DAYS", 40f, y + 15f, thPaint)
        canvas.drawText("HALF DAYS", 160f, y + 15f, thPaint)
        canvas.drawText("LEAVE / ABSENT", 280f, y + 15f, thPaint)
        canvas.drawText("OVERTIME HOURS", 430f, y + 15f, thPaint)

        y += 22f
        canvas.drawRoundRect(30f, y, 565f, y + 26f, 0f, 0f, boxPaint)
        canvas.drawText("${salary.presentDays} Days", 40f, y + 17f, boldPaint)
        canvas.drawText("${salary.halfDays} Days", 160f, y + 17f, boldPaint)
        canvas.drawText("${salary.leaveDays + salary.absentDays} Days", 280f, y + 17f, boldPaint)
        canvas.drawText("${"%.1f".format(salary.overtimeHours)} Hrs", 430f, y + 17f, boldPaint)

        y += 45f

        // Earnings & Deductions Table
        canvas.drawRect(30f, y, 290f, y + 22f, navyPaint)
        canvas.drawRect(305f, y, 565f, y + 22f, navyPaint)
        canvas.drawText("EARNINGS", 40f, y + 15f, thPaint)
        canvas.drawText("AMOUNT (INR)", 210f, y + 15f, thPaint)
        canvas.drawText("DEDUCTIONS", 315f, y + 15f, thPaint)
        canvas.drawText("AMOUNT (INR)", 480f, y + 15f, thPaint)

        y += 22f
        val linePaint = Paint().apply {
            color = Color.rgb(226, 232, 240)
            strokeWidth = 1f
        }

        // Row 1
        canvas.drawText("Base Salary", 40f, y + 16f, textPaint)
        canvas.drawText("Rs. ${"%.2f".format(salary.baseSalary)}", 210f, y + 16f, textPaint)
        canvas.drawText("Advance Deductions", 315f, y + 16f, textPaint)
        canvas.drawText("Rs. ${"%.2f".format(salary.advanceDeductions)}", 480f, y + 16f, textPaint)
        y += 22f
        canvas.drawLine(30f, y, 565f, y, linePaint)

        // Row 2
        canvas.drawText("Overtime Earnings", 40f, y + 16f, textPaint)
        canvas.drawText("Rs. ${"%.2f".format(salary.overtimePay)}", 210f, y + 16f, textPaint)
        canvas.drawText("Other Deductions", 315f, y + 16f, textPaint)
        canvas.drawText("Rs. ${"%.2f".format(salary.otherDeductions)}", 480f, y + 16f, textPaint)
        y += 22f
        canvas.drawLine(30f, y, 565f, y, linePaint)

        // Row 3
        canvas.drawText("Incentives & Bonus", 40f, y + 16f, textPaint)
        canvas.drawText("Rs. ${"%.2f".format(salary.incentives + salary.bonus)}", 210f, y + 16f, textPaint)
        canvas.drawText("-", 315f, y + 16f, textPaint)
        canvas.drawText("Rs. 0.00", 480f, y + 16f, textPaint)
        y += 22f
        canvas.drawLine(30f, y, 565f, y, linePaint)

        // Net Pay Banner
        y += 20f
        canvas.drawRoundRect(30f, y, 565f, y + 40f, 6f, 6f, boxPaint)
        boldPaint.textSize = 12f
        canvas.drawText("NET PAYABLE SALARY:", 45f, y + 26f, boldPaint)
        val netPaint = Paint().apply {
            color = Color.rgb(15, 37, 55)
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("Rs. ${"%.2f".format(salary.netSalary)}", 430f, y + 26f, netPaint)

        // Signatures
        y += 120f
        canvas.drawLine(50f, y, 180f, y, linePaint)
        canvas.drawText("Employee Signature", 65f, y + 15f, textPaint)

        canvas.drawLine(380f, y, 520f, y, linePaint)
        canvas.drawText("Company Seal & Authorized Signatory", 360f, y + 15f, textPaint)
    }
}
