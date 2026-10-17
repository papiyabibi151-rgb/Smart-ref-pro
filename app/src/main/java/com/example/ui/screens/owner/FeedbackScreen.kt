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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FeedbackEntity
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.PureWhite

@Composable
fun FeedbackScreen(feedbacks: List<FeedbackEntity>) {
    val avgOverall = if (feedbacks.isNotEmpty()) feedbacks.map { it.rating }.average() else 5.0
    val avgQuality = if (feedbacks.isNotEmpty()) feedbacks.map { it.serviceQuality }.average() else 5.0
    val avgBehaviour = if (feedbacks.isNotEmpty()) feedbacks.map { it.technicianBehaviour }.average() else 5.0
    val avgSatisfaction = if (feedbacks.isNotEmpty()) feedbacks.map { it.workSatisfaction }.average() else 5.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Rating Overview Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Customer Satisfaction Score", color = Cyan500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${"%.1f".format(avgOverall)}", color = PureWhite, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.size(6.dp))
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                        }
                    }
                    Text("Total Reviews: ${feedbacks.size}", color = Color.LightGray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Service Quality", fontSize = 11.sp, color = Color.LightGray)
                        Text("★ ${"%.1f".format(avgQuality)}", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Technician Behaviour", fontSize = 11.sp, color = Color.LightGray)
                        Text("★ ${"%.1f".format(avgBehaviour)}", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Work Satisfaction", fontSize = 11.sp, color = Color.LightGray)
                        Text("★ ${"%.1f".format(avgSatisfaction)}", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Reviews List
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(feedbacks, key = { it.id }) { fb ->
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
                            Text(fb.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(fb.rating) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        Text("Job: ${fb.jobCode} • Date: ${fb.date}", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"${fb.writtenFeedback}\"",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
