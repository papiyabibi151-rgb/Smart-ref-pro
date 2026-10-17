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
import com.example.data.local.InvoiceEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfInvoiceGenerator {

    fun generateAndShareInvoice(
        context: Context,
        invoice: InvoiceEntity,
        settings: CompanySettingsEntity?
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 at 72dpi
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        drawInvoice(canvas, invoice, settings)

        pdfDocument.finishPage(page)

        val outputDir = File(context.cacheDir, "invoices")
        if (!outputDir.exists()) outputDir.mkdirs()
        val file = File(outputDir, "${invoice.invoiceNumber}.pdf")

        try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()

            sharePdf(context, file, "Invoice ${invoice.invoiceNumber} - SMART REFRIGERATION")
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            return null
        }
    }

    private fun drawInvoice(
        canvas: Canvas,
        invoice: InvoiceEntity,
        settings: CompanySettingsEntity?
    ) {
        val companyName = settings?.companyName ?: "SMART REFRIGERATION"
        val tagline = settings?.tagline ?: "One App. Complete Service Control."
        val phone = settings?.phone ?: "+91 98765 43210"
        val email = settings?.email ?: "service@smartref.in"
        val address = settings?.address ?: "Shop #14, Cooling Tower Complex, Industrial Area"
        val city = settings?.city ?: "Mumbai, MH - 400001"
        val gstin = settings?.gstin ?: "27ABCDE1234F1Z5"

        val navyPaint = Paint().apply {
            color = Color.rgb(15, 37, 55) // #0F2537
            style = Paint.Style.FILL
        }

        val cyanPaint = Paint().apply {
            color = Color.rgb(0, 180, 216) // #00B4D8
            style = Paint.Style.FILL
        }

        val textPaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 10f
            isAntiAlias = true
        }

        val boldPaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val headerTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val subheaderTextPaint = Paint().apply {
            color = Color.rgb(224, 247, 250)
            textSize = 9f
            isAntiAlias = true
        }

        // 1. Header Banner
        canvas.drawRect(0f, 0f, 595f, 100f, navyPaint)
        canvas.drawRect(0f, 96f, 595f, 100f, cyanPaint)

        canvas.drawText(companyName, 30f, 40f, headerTextPaint)
        canvas.drawText(tagline, 30f, 58f, subheaderTextPaint)
        canvas.drawText("Tax Invoice & Service Report", 410f, 40f, Paint().apply {
            color = Color.WHITE
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        })
        canvas.drawText("GSTIN: $gstin", 410f, 60f, subheaderTextPaint)

        // 2. Company & Customer Meta
        var currentY = 125f

        // Company info left
        textPaint.textSize = 9f
        canvas.drawText(address, 30f, currentY, textPaint)
        canvas.drawText(city, 30f, currentY + 14f, textPaint)
        canvas.drawText("Phone: $phone | Email: $email", 30f, currentY + 28f, textPaint)

        // Invoice info right
        boldPaint.textSize = 10f
        canvas.drawText("INVOICE NO: ${invoice.invoiceNumber}", 380f, currentY, boldPaint)
        val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(invoice.createdAt))
        canvas.drawText("DATE: $dateStr", 380f, currentY + 14f, textPaint)
        canvas.drawText("JOB REF: ${invoice.jobCode}", 380f, currentY + 28f, textPaint)

        currentY += 50f

        // 3. Customer Box
        val boxPaint = Paint().apply {
            color = Color.rgb(241, 245, 249)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(30f, currentY, 565f, currentY + 60f, 6f, 6f, boxPaint)

        boldPaint.textSize = 10f
        canvas.drawText("BILLED TO:", 42f, currentY + 18f, boldPaint)
        canvas.drawText(invoice.customerName, 42f, currentY + 32f, boldPaint)
        textPaint.textSize = 9f
        canvas.drawText("Mobile: ${invoice.customerPhone}", 42f, currentY + 46f, textPaint)
        canvas.drawText("Address: ${invoice.customerAddress}", 220f, currentY + 32f, textPaint)
        canvas.drawText("Attended By: ${invoice.technicianName}", 220f, currentY + 46f, textPaint)

        currentY += 80f

        // 4. Items Table Header
        canvas.drawRect(30f, currentY, 565f, currentY + 24f, navyPaint)
        val thPaint = Paint().apply {
            color = Color.WHITE
            textSize = 9.5f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("#", 40f, currentY + 16f, thPaint)
        canvas.drawText("SERVICE / SPARE PART DESCRIPTION", 70f, currentY + 16f, thPaint)
        canvas.drawText("QTY", 370f, currentY + 16f, thPaint)
        canvas.drawText("RATE (INR)", 430f, currentY + 16f, thPaint)
        canvas.drawText("AMOUNT (INR)", 495f, currentY + 16f, thPaint)

        currentY += 24f

        // 5. Items Rows
        val linePaint = Paint().apply {
            color = Color.rgb(226, 232, 240)
            strokeWidth = 1f
        }

        var idx = 1
        textPaint.textSize = 9f
        for (item in invoice.items) {
            val rowY = currentY + 18f
            canvas.drawText("$idx", 40f, rowY, textPaint)
            val desc = if (item.description.length > 50) item.description.take(50) + "..." else item.description
            canvas.drawText(desc, 70f, rowY, textPaint)
            canvas.drawText("${item.quantity}", 375f, rowY, textPaint)
            canvas.drawText("Rs. ${"%.2f".format(item.unitPrice)}", 430f, rowY, textPaint)
            canvas.drawText("Rs. ${"%.2f".format(item.totalPrice)}", 495f, rowY, textPaint)

            currentY += 24f
            canvas.drawLine(30f, currentY, 565f, currentY, linePaint)
            idx++
        }

        currentY += 15f

        // 6. Summary Totals Box
        val subtotal = invoice.items.sumOf { it.totalPrice }
        val rightColX = 380f
        val rightValX = 495f

        textPaint.textSize = 9.5f
        canvas.drawText("Subtotal:", rightColX, currentY, textPaint)
        canvas.drawText("Rs. ${"%.2f".format(subtotal)}", rightValX, currentY, textPaint)
        currentY += 18f

        if (invoice.discount > 0) {
            canvas.drawText("Discount Applied:", rightColX, currentY, textPaint)
            canvas.drawText("- Rs. ${"%.2f".format(invoice.discount)}", rightValX, currentY, textPaint)
            currentY += 18f
        }

        canvas.drawText("GST / Tax (${invoice.taxPercent}%):", rightColX, currentY, textPaint)
        canvas.drawText("Rs. ${"%.2f".format(invoice.taxAmount)}", rightValX, currentY, textPaint)
        currentY += 22f

        // Grand Total Box
        canvas.drawRect(360f, currentY - 14f, 565f, currentY + 14f, boxPaint)
        boldPaint.textSize = 11f
        canvas.drawText("GRAND TOTAL:", rightColX, currentY + 6f, boldPaint)
        boldPaint.color = Color.rgb(15, 37, 55)
        canvas.drawText("Rs. ${"%.2f".format(invoice.grandTotal)}", rightValX, currentY + 6f, boldPaint)

        currentY += 30f

        // Payment status badge
        val statusBgPaint = Paint().apply {
            color = when (invoice.paymentStatus.name) {
                "PAID" -> Color.rgb(209, 250, 229)
                "PARTIAL" -> Color.rgb(254, 243, 199)
                else -> Color.rgb(254, 226, 226)
            }
            style = Paint.Style.FILL
        }
        val statusTextPaint = Paint().apply {
            color = when (invoice.paymentStatus.name) {
                "PAID" -> Color.rgb(5, 150, 105)
                "PARTIAL" -> Color.rgb(217, 119, 6)
                else -> Color.rgb(220, 38, 38)
            }
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        canvas.drawRoundRect(360f, currentY, 565f, currentY + 28f, 4f, 4f, statusBgPaint)
        canvas.drawText(
            "STATUS: ${invoice.paymentStatus.label.uppercase()} (${invoice.paymentMethod.label})",
            375f,
            currentY + 18f,
            statusTextPaint
        )

        // 7. Footer: Warranty, Terms, Stamp & Signature
        currentY = 660f

        // Terms box
        boldPaint.textSize = 9.5f
        canvas.drawText("TERMS & CONDITIONS / WARRANTY:", 30f, currentY, boldPaint)
        textPaint.textSize = 8.5f
        val termsLines = (settings?.termsNote ?: "1. 90-day warranty on parts replaced.\n2. 30-day service warranty.\n3. Goods once sold are not returnable.").split("\n")
        var termY = currentY + 14f
        for (line in termsLines) {
            canvas.drawText(line, 30f, termY, textPaint)
            termY += 12f
        }

        // Signature & Stamp
        val signY = 750f
        canvas.drawLine(380f, signY, 540f, signY, linePaint)
        canvas.drawText("Authorized Signatory", 410f, signY + 14f, boldPaint)
        canvas.drawText("SMART REFRIGERATION STAMP & SIGN", 370f, signY + 28f, textPaint)

        // Stamp circle
        val stampPaint = Paint().apply {
            color = Color.rgb(0, 180, 216)
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            isAntiAlias = true
        }
        canvas.drawCircle(460f, signY - 35f, 28f, stampPaint)
        val stampText = Paint().apply {
            color = Color.rgb(0, 180, 216)
            textSize = 7f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("SMART REF", 440f, signY - 37f, stampText)
        canvas.drawText("VERIFIED", 443f, signY - 26f, stampText)
    }

    private fun sharePdf(context: Context, file: File, subject: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, "Here is your service invoice from SMART REFRIGERATION.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Share Invoice via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
