package com.example

import com.example.data.model.InvoiceItemRow
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun invoiceCalculations_areAccurate() {
        val items = listOf(
            InvoiceItemRow(description = "Split AC Jet Service", quantity = 1, unitPrice = 650.0, totalPrice = 650.0),
            InvoiceItemRow(description = "R32 Refrigerant Gas", quantity = 1, unitPrice = 850.0, totalPrice = 850.0),
            InvoiceItemRow(description = "Capacitor 45uF", quantity = 1, unitPrice = 450.0, totalPrice = 450.0)
        )
        val subtotal = items.sumOf { it.totalPrice }
        assertEquals(1950.0, subtotal, 0.001)

        val discount = 150.0
        val taxable = subtotal - discount
        assertEquals(1800.0, taxable, 0.001)

        val taxPercent = 18.0
        val taxAmount = (taxable * taxPercent) / 100.0
        assertEquals(324.0, taxAmount, 0.001)

        val grandTotal = taxable + taxAmount
        assertEquals(2124.0, grandTotal, 0.001)
    }

    @Test
    fun salaryCalculations_withOvertime_isAccurate() {
        val baseSalary = 26000.0
        val effectiveDays = 26.0
        val baseCalculated = (baseSalary / 26.0) * effectiveDays
        val overtimeHours = 10.0
        val overtimeRate = 180.0
        val overtimePay = overtimeHours * overtimeRate
        val incentives = 1000.0

        val netPay = baseCalculated + overtimePay + incentives
        assertEquals(28800.0, netPay, 0.001)
    }
}
