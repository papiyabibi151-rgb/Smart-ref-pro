package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.InvoiceItemRow
import com.example.data.model.UsedPartItem
import org.json.JSONArray
import org.json.JSONObject

class SmartRefConverters {

    @TypeConverter
    fun fromUsedPartsList(parts: List<UsedPartItem>?): String {
        if (parts.isNullOrEmpty()) return "[]"
        val array = JSONArray()
        for (part in parts) {
            val obj = JSONObject()
            obj.put("partId", part.partId)
            obj.put("partName", part.partName)
            obj.put("partCode", part.partCode)
            obj.put("quantity", part.quantity)
            obj.put("unitPrice", part.unitPrice)
            obj.put("totalPrice", part.totalPrice)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toUsedPartsList(data: String?): List<UsedPartItem> {
        if (data.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<UsedPartItem>()
        try {
            val array = JSONArray(data)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    UsedPartItem(
                        partId = obj.optLong("partId", 0L),
                        partName = obj.optString("partName", ""),
                        partCode = obj.optString("partCode", ""),
                        quantity = obj.optInt("quantity", 1),
                        unitPrice = obj.optDouble("unitPrice", 0.0),
                        totalPrice = obj.optDouble("totalPrice", 0.0)
                    )
                )
            }
        } catch (_: Exception) {
        }
        return list
    }

    @TypeConverter
    fun fromInvoiceItemsList(items: List<InvoiceItemRow>?): String {
        if (items.isNullOrEmpty()) return "[]"
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            obj.put("description", item.description)
            obj.put("quantity", item.quantity)
            obj.put("unitPrice", item.unitPrice)
            obj.put("totalPrice", item.totalPrice)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toInvoiceItemsList(data: String?): List<InvoiceItemRow> {
        if (data.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<InvoiceItemRow>()
        try {
            val array = JSONArray(data)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    InvoiceItemRow(
                        description = obj.optString("description", ""),
                        quantity = obj.optInt("quantity", 1),
                        unitPrice = obj.optDouble("unitPrice", 0.0),
                        totalPrice = obj.optDouble("totalPrice", 0.0)
                    )
                )
            }
        } catch (_: Exception) {
        }
        return list
    }
}
