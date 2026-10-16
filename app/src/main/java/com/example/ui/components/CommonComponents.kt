package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusCriticalBg
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusInfoBg
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusPurpleBg
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg

@Composable
fun OrderStatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.OPEN -> Pair(StatusInfoBg, StatusInfo)
        OrderStatus.SCREENING -> Pair(StatusInfoBg, StatusInfo)
        OrderStatus.DIAGNOSIS -> Pair(StatusPurpleBg, StatusPurple)
        OrderStatus.AWAITING_APPROVAL -> Pair(StatusWarningBg, StatusWarning)
        OrderStatus.AWAITING_PARTS -> Pair(StatusWarningBg, StatusWarning)
        OrderStatus.IN_REPAIR -> Pair(Color(0xFFDBEAFE), BluePrimary)
        OrderStatus.TESTING -> Pair(StatusPurpleBg, StatusPurple)
        OrderStatus.COMPLETED -> Pair(StatusSuccessBg, StatusSuccess)
        OrderStatus.READY_FOR_PICKUP -> Pair(StatusSuccessBg, StatusSuccess)
        OrderStatus.DELIVERED -> Pair(Color(0xFFE2E8F0), Color(0xFF475569))
        OrderStatus.CANCELLED -> Pair(StatusCriticalBg, StatusCritical)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(textColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            val currentLang = com.example.domain.util.LocalAppLanguage.current
            Text(
                text = com.example.domain.util.AppStrings.getOrderStatus(status, currentLang),
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconTint.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                AutoFitText(
                    text = title,
                    targetFontSize = 12.sp,
                    minFontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AutoFitText(
                    text = value,
                    targetFontSize = 20.sp,
                    minFontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    AutoFitText(
                        text = subtitle,
                        targetFontSize = 11.sp,
                        minFontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        if (actionText != null && onActionClick != null) {
            Surface(
                onClick = onActionClick,
                color = Color.Transparent
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Auto-scales text font size to fit exactly inside the available button/container space,
 * ensuring words are never clipped, truncated, or broken.
 */
@Composable
fun AutoFitText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    targetFontSize: TextUnit = 14.sp,
    minFontSize: TextUnit = 9.sp,
    fontWeight: FontWeight? = null,
    maxLines: Int = 1,
    textAlign: TextAlign? = null
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val textMeasurer = rememberTextMeasurer()
        val availableWidthPx = with(density) { maxWidth.toPx() }

        val calculatedSize = remember(text, availableWidthPx, targetFontSize, minFontSize, fontWeight) {
            var size = targetFontSize.value
            while (size > minFontSize.value) {
                val measured = textMeasurer.measure(
                    text = AnnotatedString(text),
                    style = TextStyle(
                        fontSize = size.sp,
                        fontWeight = fontWeight ?: FontWeight.Normal
                    ),
                    maxLines = maxLines
                )
                if (measured.size.width <= availableWidthPx) {
                    break
                }
                size -= 0.5f
            }
            size.coerceAtLeast(minFontSize.value).sp
        }

        Text(
            text = text,
            color = color,
            fontSize = calculatedSize,
            fontWeight = fontWeight,
            maxLines = maxLines,
            softWrap = false,
            textAlign = textAlign
        )
    }
}
