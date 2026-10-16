package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.InventoryPart
import com.example.domain.util.Formatters
import com.example.domain.util.appString
import com.example.ui.components.AutoFitText
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusCriticalBg
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.MainViewModel

@Composable
fun InventoryScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val parts by viewModel.filteredInventoryParts.collectAsState()
    val searchQuery by viewModel.inventorySearchQuery.collectAsState()
    val lowStockCount by viewModel.lowStockCount.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_new_part")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Peça")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("inventory_screen")
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setInventorySearchQuery(it) },
                placeholder = { Text(appString("search_inventory")) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setInventorySearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpar")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Low Stock Warning Banner
            if (lowStockCount > 0) {
                Surface(
                    color = StatusCriticalBg,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = StatusCritical, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${appString("low_stock")}: $lowStockCount",
                            color = StatusCritical,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (parts.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Inventory2,
                    title = appString("inventory_title"),
                    message = appString("search_inventory")
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(parts, key = { it.id }) { part ->
                        InventoryPartCard(
                            part = part,
                            onIncrement = {
                                viewModel.saveInventoryPart(part.copy(quantity = part.quantity + 1))
                            },
                            onDecrement = {
                                if (part.quantity > 0) {
                                    viewModel.saveInventoryPart(part.copy(quantity = part.quantity - 1))
                                }
                            },
                            onDelete = {
                                viewModel.deleteInventoryPart(part)
                                Toast.makeText(context, "Peça excluída", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        NewPartDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newPart ->
                viewModel.saveInventoryPart(newPart)
                Toast.makeText(context, "Peça salva no estoque!", Toast.LENGTH_SHORT).show()
                showAddDialog = false
            }
        )
    }
}

@Composable
fun InventoryPartCard(
    part: InventoryPart,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit
) {
    val isLowStock = part.quantity <= part.minQuantityAlert

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = part.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "SKU: ${part.skuCode} • Local: ${part.shelfLocation}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = StatusCritical, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Preço Venda: ${Formatters.formatCurrency(part.salePrice)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Custo: ${Formatters.formatCurrency(part.costPrice)} (Mín: ${part.minQuantityAlert} un)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Quantity Counter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedIconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Diminuir", modifier = Modifier.size(16.dp))
                    }

                    Surface(
                        color = if (isLowStock) StatusCriticalBg else MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${part.quantity} un",
                            color = if (isLowStock) StatusCritical else MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    OutlinedIconButton(
                        onClick = onIncrement,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Aumentar", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NewPartDialog(
    onDismiss: () -> Unit,
    onSave: (InventoryPart) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var skuCode by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Telas & Displays") }
    var shelfLocation by remember { mutableStateOf("Gaveta A1") }
    var quantityStr by remember { mutableStateOf("1") }
    var minQtyStr by remember { mutableStateOf("2") }
    var costPriceStr by remember { mutableStateOf("") }
    var salePriceStr by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Cadastrar Peça de Estoque", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Divider()

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Peça *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = skuCode,
                        onValueChange = { skuCode = it },
                        label = { Text("Código SKU *") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoria") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("Qtd. Inicial *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minQtyStr,
                        onValueChange = { minQtyStr = it },
                        label = { Text("Alerta Mínimo") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = costPriceStr,
                        onValueChange = { costPriceStr = it },
                        label = { Text("Custo (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = salePriceStr,
                        onValueChange = { salePriceStr = it },
                        label = { Text("Preço Venda (R$) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = shelfLocation,
                    onValueChange = { shelfLocation = it },
                    label = { Text("Localização na Bancada / Gaveta") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (name.isBlank()) return@Button
                        val finalSku = skuCode.ifBlank { "SKU-${System.currentTimeMillis().toString().takeLast(6)}" }
                        val qty = quantityStr.toIntOrNull() ?: 1
                        val minQty = minQtyStr.toIntOrNull() ?: 2
                        val cost = costPriceStr.toDoubleOrNull() ?: 0.0
                        val sale = salePriceStr.toDoubleOrNull() ?: 0.0

                        onSave(
                            InventoryPart(
                                name = name.trim(),
                                skuCode = finalSku.trim(),
                                barcode = barcode.trim(),
                                category = category.trim(),
                                shelfLocation = shelfLocation.trim(),
                                quantity = qty,
                                minQuantityAlert = minQty,
                                costPrice = cost,
                                salePrice = sale
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    AutoFitText(
                        text = appString("save"),
                        fontWeight = FontWeight.Bold,
                        targetFontSize = 15.sp,
                        minFontSize = 11.sp
                    )
                }
            }
        }
    }
}
