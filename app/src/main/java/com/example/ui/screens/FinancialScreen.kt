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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.FinancialTransaction
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionType
import com.example.domain.util.appString
import com.example.domain.util.Formatters
import com.example.ui.components.AutoFitText
import com.example.ui.components.EmptyStateView
import com.example.ui.components.KpiCard
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusCriticalBg
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val transactions by viewModel.allTransactions.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()

    val income = totalIncome ?: 0.0
    val expense = totalExpenses ?: 0.0
    val netProfit = income - expense

    var filterType by remember { mutableStateOf<TransactionType?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredList = remember(transactions, filterType) {
        if (filterType == null) transactions
        else transactions.filter { it.type == filterType }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_new_transaction")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Transação")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("financial_screen")
        ) {
            // KPI Summary Cards
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Entradas (Receitas)",
                        value = Formatters.formatCurrency(income),
                        icon = Icons.Default.ArrowUpward,
                        iconTint = StatusSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Saídas (Despesas)",
                        value = Formatters.formatCurrency(expense),
                        icon = Icons.Default.ArrowDownward,
                        iconTint = StatusCritical,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (netProfit >= 0) StatusSuccessBg else StatusCriticalBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Resultado Líquido do Período",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (netProfit >= 0) StatusSuccess else StatusCritical
                            )
                            Text(
                                text = Formatters.formatCurrency(netProfit),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (netProfit >= 0) StatusSuccess else StatusCritical
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = if (netProfit >= 0) StatusSuccess else StatusCritical,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterType == null,
                    onClick = { filterType = null },
                    label = { Text("${appString("all")} (${transactions.size})", maxLines = 1, softWrap = false) }
                )
                FilterChip(
                    selected = filterType == TransactionType.INCOME,
                    onClick = { filterType = TransactionType.INCOME },
                    label = { Text(appString("revenue_received"), maxLines = 1, softWrap = false) }
                )
                FilterChip(
                    selected = filterType == TransactionType.EXPENSE,
                    onClick = { filterType = TransactionType.EXPENSE },
                    label = { Text(appString("nav_financial"), maxLines = 1, softWrap = false) }
                )
            }

            // Transactions list
            if (filteredList.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.AttachMoney,
                    title = appString("financial_title"),
                    message = appString("financial_sub")
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        TransactionCard(
                            item = item,
                            onDelete = {
                                viewModel.deleteTransaction(item)
                                Toast.makeText(context, "Transação removida", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        NewTransactionDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newTx ->
                viewModel.saveTransaction(newTx)
                Toast.makeText(context, "Transação registrada!", Toast.LENGTH_SHORT).show()
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TransactionCard(
    item: FinancialTransaction,
    onDelete: () -> Unit
) {
    val isIncome = item.type == TransactionType.INCOME

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (isIncome) StatusSuccessBg else StatusCriticalBg,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isIncome) StatusSuccess else StatusCritical,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${item.category} • ${item.paymentMethod.titlePt} • ${Formatters.formatDateTime(item.date)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (isIncome) "+ " else "- "}${Formatters.formatCurrency(item.amount)}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isIncome) StatusSuccess else StatusCritical
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTransactionDialog(
    onDismiss: () -> Unit,
    onSave: (FinancialTransaction) -> Unit
) {
    var type by remember { mutableStateOf(TransactionType.INCOME) }
    var description by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Serviço Técnico") }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.PIX) }
    var paymentDropdownExpanded by remember { mutableStateOf(false) }

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
                    Text("Registrar Transação", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Divider()

                // Income / Expense selector
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = type == TransactionType.INCOME,
                        onClick = {
                            type = TransactionType.INCOME
                            category = "Serviço Técnico"
                        },
                        label = { Text("Entrada (Receita)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == TransactionType.EXPENSE,
                        onClick = {
                            type = TransactionType.EXPENSE
                            category = "Compra de Peças"
                        },
                        label = { Text("Saída (Despesa)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição * (ex: Pagamento de Reparo, Aluguel)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Valor (R$) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoria") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = paymentDropdownExpanded,
                    onExpandedChange = { paymentDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = paymentMethod.titlePt,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Forma de Pagamento") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = paymentDropdownExpanded,
                        onDismissRequest = { paymentDropdownExpanded = false }
                    ) {
                        PaymentMethod.values().forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method.titlePt) },
                                onClick = {
                                    paymentMethod = method
                                    paymentDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        val amount = amountStr.toDoubleOrNull() ?: 0.0
                        if (description.isBlank() || amount <= 0) return@Button

                        onSave(
                            FinancialTransaction(
                                type = type,
                                description = description.trim(),
                                amount = amount,
                                category = category.trim(),
                                paymentMethod = paymentMethod
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
