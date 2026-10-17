package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CompanySettingsEntity
import com.example.data.model.UserRole
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.PureWhite

@Composable
fun LoginScreen(
    settings: CompanySettingsEntity?,
    onLoginSubmit: (mobile: String, role: UserRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf(UserRole.OWNER) }
    var mobileNumber by remember { mutableStateOf("9876543210") }
    var otpCode by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }

    val companyName = settings?.companyName ?: "SMART REFRIGERATION"
    val tagline = settings?.tagline ?: "One App. Complete Service Control."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy900)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // App Brand Logo
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Cyan500),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AcUnit,
                    contentDescription = "Logo",
                    tint = Navy900,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "SMART REF PRO",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PureWhite
            )
            Text(
                text = companyName,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Cyan400
            )
            Text(
                text = tagline,
                fontSize = 12.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Role Selector Tabs
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RoleTabItem(
                        title = "Owner",
                        role = UserRole.OWNER,
                        selected = selectedRole == UserRole.OWNER,
                        onSelect = {
                            selectedRole = UserRole.OWNER
                            mobileNumber = "9876543210"
                            otpSent = false
                        }
                    )
                    RoleTabItem(
                        title = "Technician",
                        role = UserRole.TECHNICIAN,
                        selected = selectedRole == UserRole.TECHNICIAN,
                        onSelect = {
                            selectedRole = UserRole.TECHNICIAN
                            mobileNumber = "9812345671" // Ramesh Kumar
                            otpSent = false
                        }
                    )
                    RoleTabItem(
                        title = "Helper",
                        role = UserRole.HELPER,
                        selected = selectedRole == UserRole.HELPER,
                        onSelect = {
                            selectedRole = UserRole.HELPER
                            mobileNumber = "9812345674" // Rahul Verma
                            otpSent = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Login Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${selectedRole.displayName} Secure Login",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Navy900
                    )
                    Text(
                        text = if (selectedRole == UserRole.OWNER)
                            "Enter registered Owner mobile to access control center"
                        else
                            "Registered mobile number required. Unregistered employees cannot access data.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { if (it.length <= 10) mobileNumber = it },
                        label = { Text("Registered Mobile Number") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone", tint = Navy900)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_mobile_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Navy900,
                            unfocusedTextColor = Navy900
                        )
                    )

                    AnimatedVisibility(visible = otpSent) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = { if (it.length <= 6) otpCode = it },
                                label = { Text("Enter OTP (Use 1234)") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = "OTP", tint = Navy900)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_otp_input"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Navy900,
                                    unfocusedTextColor = Navy900
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (!otpSent) {
                                if (mobileNumber.length >= 10) {
                                    otpSent = true
                                    otpCode = "1234" // Auto-fill standard OTP for quick test
                                }
                            } else {
                                onLoginSubmit(mobileNumber, selectedRole)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_action_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (!otpSent) "Send Secure OTP" else "Verify OTP & Enter",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    if (otpSent) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "OTP sent to +91 $mobileNumber (Auto-verified: 1234)",
                            fontSize = 11.sp,
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Protected by Role-Based Access Control • Data Isolated per Company",
                fontSize = 11.sp,
                color = Color.LightGray.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RoleTabItem(
    title: String,
    role: UserRole,
    selected: Boolean,
    onSelect: () -> Unit
) {
    val bg = if (selected) Cyan500 else Color.Transparent
    val fg = if (selected) Navy900 else PureWhite

    Surface(
        color = bg,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .clickable { onSelect() }
            .testTag("role_tab_${role.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (role) {
                    UserRole.OWNER -> Icons.Default.Business
                    UserRole.TECHNICIAN -> Icons.Default.Engineering
                    UserRole.HELPER -> Icons.Default.Handyman
                },
                contentDescription = title,
                tint = fg,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = fg,
                fontSize = 13.sp
            )
        }
    }
}
