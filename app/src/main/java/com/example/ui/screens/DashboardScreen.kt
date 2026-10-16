package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.data.model.WorkOrderWithDetails
import androidx.compose.material.icons.filled.ArrowDropDown
import com.example.domain.util.AppLanguage
import com.example.domain.util.AppStrings
import com.example.domain.util.appString
import com.example.domain.util.Formatters
import com.example.ui.components.AppLogoView
import com.example.ui.components.EmptyStateView
import com.example.ui.components.KpiCard
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusCriticalBg
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToOrders: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToFinancial: () -> Unit,
    onNavigateToWarranties: () -> Unit,
    onNewOrderClick: () -> Unit,
    onSelectOrder: (WorkOrderWithDetails) -> Unit,
    onOpenLanguagePicker: () -> Unit = {}
) {
    val company by viewModel.companyProfile.collectAsState()
    val orders by viewModel.allOrdersRaw.collectAsState()
    val customers by viewModel.allCustomers.collectAsState()

    val totalOrders by viewModel.totalOrdersCount.collectAsState()
    val openOrders by viewModel.openOrdersCount.collectAsState()
    val inRepairOrders by viewModel.inRepairOrdersCount.collectAsState()
    val readyPickup by viewModel.readyPickupCount.collectAsState()
    val completedOrders by viewModel.completedOrdersCount.collectAsState()
    val totalRevenue by viewModel.totalIncome.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val pendingBalance by viewModel.totalPendingBalance.collectAsState()
    val lowStockCount by viewModel.lowStockCount.collectAsState()
    val activeWarrantiesCount by viewModel.activeWarrantiesCount.collectAsState()

    val currentLang = remember(company.appLanguage) { AppLanguage.fromCode(company.appLanguage) }
    val netProfit = (totalRevenue ?: 0.0) - (totalExpenses ?: 0.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Hero Header Banner with Language Pill
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(NavyDeep, Color(0xFF14243F))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = company.tradeName.ifBlank { "TecnoOrdem Pro" },
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = AppStrings.get("slogan", currentLang),
                                style = MaterialTheme.typography.bodySmall,
                                color = BlueAccent
                            )
                        }
                        com.example.ui.components.AppLogoView(
                            logoUri = company.logoUri,
                            size = 48.dp,
                            isCircular = false
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Language Selector Pill directly visible on main screen
                    Surface(
                        onClick = onOpenLanguagePicker,
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.16f),
                        modifier = Modifier.testTag("btn_dashboard_language")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "🌐", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = currentLang.flagEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentLang.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // CARDS DE ACESSO RÁPIDO (Material3 Quick Access Cards)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            ) {
                SectionHeader(title = appString("quick_access"))
                Spacer(modifier = Modifier.height(12.dp))

                // Linha 1: Nova Ordem & Clientes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: Nova Ordem
                    QuickAccessCard(
                        title = appString("new_order"),
                        description = appString("new_order_sub"),
                        badgeText = AppStrings.get("new_order_btn", currentLang),
                        icon = Icons.Default.AddCircle,
                        iconColor = BluePrimary,
                        iconBgColor = BluePrimary.copy(alpha = 0.12f),
                        testTag = "card_quick_new_order",
                        modifier = Modifier.weight(1f),
                        onClick = onNewOrderClick
                    )

                    // Card 2: Clientes
                    QuickAccessCard(
                        title = appString("nav_customers"),
                        description = appString("customers_sub"),
                        badgeText = "${customers.size} ${appString("all").lowercase()}",
                        icon = Icons.Default.People,
                        iconColor = StatusPurple,
                        iconBgColor = StatusPurple.copy(alpha = 0.12f),
                        testTag = "card_quick_customers",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCustomers
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Linha 2: Estoque & Financeiro
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 3: Estoque
                    QuickAccessCard(
                        title = appString("nav_inventory"),
                        description = appString("inventory_sub"),
                        badgeText = if (lowStockCount > 0) "$lowStockCount ${appString("low_stock")}" else "OK",
                        badgeColor = if (lowStockCount > 0) StatusCritical else StatusInfo,
                        badgeBgColor = if (lowStockCount > 0) StatusCriticalBg else StatusInfo.copy(alpha = 0.12f),
                        icon = Icons.Default.Inventory2,
                        iconColor = if (lowStockCount > 0) StatusWarning else StatusInfo,
                        iconBgColor = (if (lowStockCount > 0) StatusWarning else StatusInfo).copy(alpha = 0.12f),
                        testTag = "card_quick_inventory",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToInventory
                    )

                    // Card 4: Financeiro
                    QuickAccessCard(
                        title = appString("nav_financial"),
                        description = appString("financial_sub"),
                        badgeText = Formatters.formatCurrency(totalRevenue ?: 0.0),
                        badgeColor = StatusSuccess,
                        badgeBgColor = StatusSuccessBg,
                        icon = Icons.Default.AttachMoney,
                        iconColor = StatusSuccess,
                        iconBgColor = StatusSuccess.copy(alpha = 0.12f),
                        testTag = "card_quick_financial",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToFinancial
                    )
                }
            }
        }

        // Operational Status Metrics (Cards)
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)) {
                SectionHeader(title = appString("operational_view"), actionText = appString("all"), onActionClick = onNavigateToOrders)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = appString("in_repair"),
                        value = inRepairOrders.toString(),
                        icon = Icons.Default.Build,
                        iconTint = BluePrimary,
                        subtitle = "${inRepairOrders} ${appString("nav_orders").lowercase()}",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToOrders
                    )
                    KpiCard(
                        title = appString("ready_pickup"),
                        value = readyPickup.toString(),
                        icon = Icons.Default.CheckCircle,
                        iconTint = StatusSuccess,
                        subtitle = "${readyPickup} ${appString("nav_orders").lowercase()}",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToOrders
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = appString("open_orders"),
                        value = openOrders.toString(),
                        icon = Icons.Default.PendingActions,
                        iconTint = StatusWarning,
                        subtitle = "${openOrders} ${appString("nav_orders").lowercase()}",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToOrders
                    )
                    KpiCard(
                        title = appString("total_orders"),
                        value = totalOrders.toString(),
                        icon = Icons.Default.Description,
                        iconTint = StatusInfo,
                        subtitle = "$completedOrders ${appString("confirm").lowercase()}",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToOrders
                    )
                }
            }
        }

        // Financial & Alert Metrics
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)) {
                SectionHeader(title = appString("financial_alerts"))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = appString("revenue_received"),
                        value = Formatters.formatCurrency(totalRevenue ?: 0.0),
                        icon = Icons.Default.AttachMoney,
                        iconTint = StatusSuccess,
                        subtitle = "${appString("nav_financial")}: ${Formatters.formatCurrency(netProfit)}",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToFinancial
                    )
                    KpiCard(
                        title = appString("pending_balance"),
                        value = Formatters.formatCurrency(pendingBalance ?: 0.0),
                        icon = Icons.Default.PendingActions,
                        iconTint = if ((pendingBalance ?: 0.0) > 0) StatusCritical else StatusSuccess,
                        subtitle = Formatters.formatCurrency(pendingBalance ?: 0.0),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToFinancial
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = appString("active_warranties"),
                        value = "$activeWarrantiesCount",
                        icon = Icons.Default.Security,
                        iconTint = BluePrimary,
                        subtitle = appString("nav_warranties"),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToWarranties
                    )
                    KpiCard(
                        title = appString("low_stock"),
                        value = "$lowStockCount",
                        icon = Icons.Default.Inventory2,
                        iconTint = if (lowStockCount > 0) StatusWarning else StatusSuccess,
                        subtitle = appString("nav_inventory"),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToInventory
                    )
                }
            }
        }

        // Recent Work Orders Section
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)) {
                SectionHeader(
                    title = appString("recent_orders"),
                    actionText = "${appString("all")} (${orders.size})",
                    onActionClick = onNavigateToOrders
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (orders.isEmpty()) {
            item {
                EmptyStateView(
                    icon = Icons.Default.Description,
                    title = appString("no_orders_yet"),
                    message = appString("new_order_sub")
                )
            }
        } else {
            items(orders.take(5)) { item ->
                OrderListItemCard(
                    item = item,
                    onClick = { onSelectOrder(item) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                )
            }
        }
    }
}

/**
 * Material3 Quick Access Card for primary actions:
 * - Nova Ordem
 * - Clientes
 * - Estoque
 * - Financeiro
 */
@Composable
fun QuickAccessCard(
    title: String,
    description: String,
    badgeText: String? = null,
    badgeColor: Color = BluePrimary,
    badgeBgColor: Color = BluePrimary.copy(alpha = 0.12f),
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(iconBgColor, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (badgeText != null) {
                    Surface(
                        color = badgeBgColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeColor,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    softWrap = false
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun OrderListItemCard(
    item: WorkOrderWithDetails,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "OS #${item.workOrder.orderNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Formatters.formatDate(item.workOrder.openedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OrderStatusBadge(status = item.workOrder.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.customer?.name ?: "Cliente não identificado",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Smartphone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                val equipDesc = "${item.equipment?.brand ?: ""} ${item.equipment?.model ?: ""}".trim().ifEmpty { "Equipamento geral" }
                Text(
                    text = equipDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (item.workOrder.reportedDefect.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Defeito: ${item.workOrder.reportedDefect}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ${Formatters.formatCurrency(item.workOrder.totalAmount)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (item.workOrder.remainingBalance > 0) {
                    Text(
                        text = "Saldo: ${Formatters.formatCurrency(item.workOrder.remainingBalance)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = StatusCritical
                    )
                } else if (item.workOrder.totalAmount > 0) {
                    Text(
                        text = "Quitado",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = StatusSuccess
                    )
                }
            }
        }
    }
}
