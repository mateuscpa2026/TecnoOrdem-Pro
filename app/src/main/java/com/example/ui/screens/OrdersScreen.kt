package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.OrderStatus
import com.example.data.model.WorkOrderWithDetails
import com.example.domain.util.AppStrings
import com.example.domain.util.LocalAppLanguage
import com.example.domain.util.appString
import com.example.ui.components.EmptyStateView
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: MainViewModel,
    onNewOrderClick: () -> Unit,
    onSelectOrder: (WorkOrderWithDetails) -> Unit
) {
    val orders by viewModel.filteredOrders.collectAsState()
    val searchQuery by viewModel.orderSearchQuery.collectAsState()
    val selectedFilter by viewModel.selectedStatusFilter.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewOrderClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_new_order")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Ordem de Serviço")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("orders_screen")
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setOrderSearchQuery(it) },
                placeholder = { Text(appString("search_orders")) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setOrderSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpar busca")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_order_input")
            )

            // Status Filter Chips Horizontal Row
            val currentLang = LocalAppLanguage.current
            val statusChips = listOf(
                null to appString("all"),
                OrderStatus.OPEN to AppStrings.getOrderStatus(OrderStatus.OPEN, currentLang),
                OrderStatus.DIAGNOSIS to AppStrings.getOrderStatus(OrderStatus.DIAGNOSIS, currentLang),
                OrderStatus.AWAITING_APPROVAL to AppStrings.getOrderStatus(OrderStatus.AWAITING_APPROVAL, currentLang),
                OrderStatus.IN_REPAIR to AppStrings.getOrderStatus(OrderStatus.IN_REPAIR, currentLang),
                OrderStatus.READY_FOR_PICKUP to AppStrings.getOrderStatus(OrderStatus.READY_FOR_PICKUP, currentLang),
                OrderStatus.COMPLETED to AppStrings.getOrderStatus(OrderStatus.COMPLETED, currentLang),
                OrderStatus.DELIVERED to AppStrings.getOrderStatus(OrderStatus.DELIVERED, currentLang),
                OrderStatus.CANCELLED to AppStrings.getOrderStatus(OrderStatus.CANCELLED, currentLang)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusChips.forEach { (status, label) ->
                    val isSelected = selectedFilter == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedStatusFilter(status) },
                        label = { Text(label, maxLines = 1, softWrap = false) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Order List
            if (orders.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Description,
                    title = appString("no_orders_yet"),
                    message = appString("search_orders")
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(orders, key = { it.workOrder.id }) { item ->
                        OrderListItemCard(
                            item = item,
                            onClick = { onSelectOrder(item) }
                        )
                    }
                }
            }
        }
    }
}
