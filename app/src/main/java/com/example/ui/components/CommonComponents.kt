package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthSession
import com.example.data.model.JobPriority
import com.example.data.model.JobStatus
import com.example.data.model.UserRole
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberBg
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusBlueBg
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusPurpleBg
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartRefTopBar(
    session: AuthSession,
    unreadNotificationCount: Int,
    onNotificationsClick: () -> Unit,
    onSwitchRoleClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Navy900,
            titleContentColor = PureWhite,
            actionIconContentColor = PureWhite
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Cyan500),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = "Logo",
                        tint = Navy900,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "SMART REF PRO",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = "${session.role.displayName}: ${session.employeeName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Cyan400
                    )
                }
            }
        },
        actions = {
            BadgedBox(
                badge = {
                    if (unreadNotificationCount > 0) {
                        Badge(containerColor = Cyan500, contentColor = Navy900) {
                            Text("$unreadNotificationCount")
                        }
                    }
                }
            ) {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("notifications_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = PureWhite
                    )
                }
            }

            IconButton(
                onClick = onSwitchRoleClick,
                modifier = Modifier.testTag("switch_role_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Switch Role",
                    tint = Cyan400
                )
            }
        }
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    subValue: String? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .testTag("stat_card_${title.lowercase().replace(" ", "_")}")
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subValue != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subValue,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun JobStatusBadge(status: JobStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        JobStatus.NEW -> StatusBlueBg to StatusBlue
        JobStatus.ASSIGNED -> StatusPurpleBg to StatusPurple
        JobStatus.ACCEPTED -> Color(0xFFE0F2FE) to Color(0xFF0284C7)
        JobStatus.ON_THE_WAY -> StatusAmberBg to StatusAmber
        JobStatus.WORK_STARTED -> Color(0xFFFEF08A) to Color(0xFF854D0E)
        JobStatus.WAITING_FOR_PARTS -> StatusRedBg to StatusRed
        JobStatus.COMPLETED -> StatusGreenBg to StatusGreen
        JobStatus.CANCELLED -> Color(0xFFF3F4F6) to Color(0xFF6B7280)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Text(
            text = status.label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PriorityBadge(priority: JobPriority, modifier: Modifier = Modifier) {
    val (bg, fg) = when (priority) {
        JobPriority.LOW -> Color(0xFFE2E8F0) to Color(0xFF475569)
        JobPriority.NORMAL -> StatusBlueBg to StatusBlue
        JobPriority.HIGH -> StatusAmberBg to StatusAmber
        JobPriority.URGENT -> StatusRedBg to StatusRed
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Text(
            text = priority.label.uppercase(),
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = Cyan500,
                modifier = Modifier
                    .clickable { onActionClick() }
                    .padding(4.dp)
            )
        }
    }
}
