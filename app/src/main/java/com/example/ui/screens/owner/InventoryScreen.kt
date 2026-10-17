package com.example.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SparePartEntity
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy900
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed

@Composable
fun InventoryScreen(
    parts: List<SparePartEntity>,
    onSavePart: (SparePartEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Cyan500,
                contentColor = Navy900,
                modifier = Modifier.testTag("fab_add_spare_part")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Part")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(parts, key = { it.id }) { part ->
                    val isLowStock = part.availableQuantity <= part.minStockAlert

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(part.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Code: ${part.code} • Category: ${part.category}", fontSize = 12.sp, color = Color.Gray)
                                }

                                Surface(
                                    color = if (isLowStock) Color(0xFFFEE2E2) else Color(0xFFD1FAE5),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        if (isLowStock) {
                                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = StatusRed, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.size(4.dp))
                                        }
                                        Text(
                                            text = "${part.availableQuantity} in stock",
                                            color = if (isLowStock) StatusRed else StatusGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Cost: ₹${part.purchasePrice.toInt()}", fontSize = 12.sp, color = Color.Gray)
                                Text("Selling Price: ₹${part.sellingPrice.toInt()}", fontWeight = FontWeight.Bold, color = Cyan500, fontSize = 14.sp)
                                val margin = ((part.sellingPrice - part.purchasePrice) / part.sellingPrice * 100).toInt()
                                Text("Margin: $margin%", fontSize = 12.sp, color = StatusGreen, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var partName by remember { mutableStateOf("") }
        var partCode by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Refrigeration Gas") }
        var buyPrice by remember { mutableStateOf("") }
        var sellPrice by remember { mutableStateOf("") }
        var quantity by remember { mutableStateOf("10") }
        var minStock by remember { mutableStateOf("3") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Spare Part / Gas") },
            text = {
                Column {
                    OutlinedTextField(value = partName, onValueChange = { partName = it }, label = { Text("Part Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = partCode, onValueChange = { partCode = it }, label = { Text("Part Code / SKU") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = buyPrice, onValueChange = { buyPrice = it }, label = { Text("Cost (₹)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = sellPrice, onValueChange = { sellPrice = it }, label = { Text("Selling (₹)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Stock Qty") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = minStock, onValueChange = { minStock = it }, label = { Text("Min Alert Qty") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (partName.isNotBlank()) {
                            onSavePart(
                                SparePartEntity(
                                    name = partName,
                                    code = partCode.ifBlank { "SP-" + (100 + (Math.random() * 900).toInt()) },
                                    category = category,
                                    purchasePrice = buyPrice.toDoubleOrNull() ?: 100.0,
                                    sellingPrice = sellPrice.toDoubleOrNull() ?: 250.0,
                                    availableQuantity = quantity.toIntOrNull() ?: 10,
                                    minStockAlert = minStock.toIntOrNull() ?: 3
                                )
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900)
                ) {
                    Text("Add Part")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}
