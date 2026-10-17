package com.example.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CompanySettingsEntity
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.PureWhite

@Composable
fun SettingsScreen(
    currentSettings: CompanySettingsEntity?,
    onSaveSettings: (CompanySettingsEntity) -> Unit
) {
    var companyName by remember { mutableStateOf(currentSettings?.companyName ?: "SMART REFRIGERATION") }
    var tagline by remember { mutableStateOf(currentSettings?.tagline ?: "One App. Complete Service Control.") }
    var phone by remember { mutableStateOf(currentSettings?.phone ?: "+91 98765 43210") }
    var email by remember { mutableStateOf(currentSettings?.email ?: "service@smartref.in") }
    var address by remember { mutableStateOf(currentSettings?.address ?: "Shop #14, Cooling Tower Complex, Industrial Area") }
    var city by remember { mutableStateOf(currentSettings?.city ?: "Mumbai, MH - 400001") }
    var gstin by remember { mutableStateOf(currentSettings?.gstin ?: "27ABCDE1234F1Z5") }
    var invoicePrefix by remember { mutableStateOf(currentSettings?.invoicePrefix ?: "SR-INV-") }
    var terms by remember {
        mutableStateOf(
            currentSettings?.termsNote ?: "1. 90 days warranty on parts replaced.\n2. 30 days service warranty.\n3. Goods once sold will not be taken back."
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Company Identity & Branding", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Navy900)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Company Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("settings_company_name_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tagline,
                        onValueChange = { tagline = it },
                        label = { Text("Brand Tagline") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Official Phone") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Official Email") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Business Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City, State & PIN") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = gstin,
                            onValueChange = { gstin = it },
                            label = { Text("GSTIN Tax ID") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Invoice Configuration & Terms", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Navy900)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = invoicePrefix,
                        onValueChange = { invoicePrefix = it },
                        label = { Text("Invoice Number Prefix (e.g. SR-INV-)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = terms,
                        onValueChange = { terms = it },
                        label = { Text("Invoice Terms & Warranty Policy") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    onSaveSettings(
                        CompanySettingsEntity(
                            id = 1,
                            companyName = companyName,
                            tagline = tagline,
                            phone = phone,
                            email = email,
                            address = address,
                            city = city,
                            gstin = gstin,
                            invoicePrefix = invoicePrefix,
                            termsNote = terms
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_settings_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Branding & Settings", fontWeight = FontWeight.Bold)
            }
        }

        // Firebase & Cloud Security Architecture section (as required in prompt)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Cloud, contentDescription = null, tint = Cyan500)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Firebase Cloud & Role-Based Security", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "SMART REF PRO uses local Room caching alongside ready-to-deploy Firebase Firestore schemas & zero-trust security rules:",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = Navy900,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                "• Role-based rules isolate Owner admin ops from Field staff.\n" +
                                "• Technicians can only view assigned jobs and draft invoices.\n" +
                                "• Salary and company revenue collections are strictly restricted to Owner.\n" +
                                "• Zero-trust security rules pre-generated in firestore.rules.",
                                color = Cyan500,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
