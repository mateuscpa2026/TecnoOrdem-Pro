package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.model.OrderStatus
import com.example.domain.util.Formatters
import com.example.domain.util.appString
import com.example.ui.components.AutoFitText
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.MainViewModel
import java.io.File

@Composable
fun ReportsScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val totalOrders by viewModel.totalOrdersCount.collectAsState()
    val openOrders by viewModel.openOrdersCount.collectAsState()
    val inRepairOrders by viewModel.inRepairOrdersCount.collectAsState()
    val readyPickup by viewModel.readyPickupCount.collectAsState()
    val completedOrders by viewModel.completedOrdersCount.collectAsState()
    val totalRevenue by viewModel.totalIncome.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val lowStockCount by viewModel.lowStockCount.collectAsState()
    val activeWarrantiesCount by viewModel.activeWarrantiesCount.collectAsState()
    val orders by viewModel.allOrdersRaw.collectAsState()

    val revenue = totalRevenue ?: 0.0
    val expenses = totalExpenses ?: 0.0
    val netProfit = revenue - expenses

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("reports_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "Relatórios Gerenciais",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Indicadores consolidados da assistência técnica",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        try {
                            val csvContent = buildString {
                                appendLine("RELATÓRIO DE GESTÃO - TECNOORDEM PRO")
                                appendLine("Data de Emissão;${Formatters.formatDateTime(System.currentTimeMillis())}")
                                appendLine("Total de Ordens;$totalOrders")
                                appendLine("Ordens em Reparo;$inRepairOrders")
                                appendLine("Ordens Concluídas;$completedOrders")
                                appendLine("Receita Total (R$);${revenue}")
                                appendLine("Despesas Totais (R$);${expenses}")
                                appendLine("Resultado Líquido (R$);${netProfit}")
                                appendLine("")
                                appendLine("LISTAGEM DE ORDENS")
                                appendLine("OS;Data;Cliente;Equipamento;Status;Total (R$);Saldo (R$)")
                                orders.forEach { o ->
                                    val client = o.customer?.name ?: ""
                                    val equip = "${o.equipment?.brand ?: ""} ${o.equipment?.model ?: ""}".trim()
                                    appendLine("${o.workOrder.orderNumber};${Formatters.formatDate(o.workOrder.openedAt)};$client;$equip;${o.workOrder.status.titlePt};${o.workOrder.totalAmount};${o.workOrder.remainingBalance}")
                                }
                            }

                            val dir = File(context.cacheDir, "reports").apply { mkdirs() }
                            val file = File(dir, "relatorio_tecnoordem_${System.currentTimeMillis()}.csv")
                            file.writeText(csvContent, Charsets.ISO_8859_1)

                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/csv"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                putExtra(Intent.EXTRA_SUBJECT, "Relatório Gerencial TecnoOrdem Pro")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Exportar Relatório CSV"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erro ao exportar CSV: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    AutoFitText(
                        text = appString("export_csv"),
                        targetFontSize = 13.sp,
                        minFontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Financial Performance Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "DESEMPENHO FINANCEIRO CONSOLIDADO",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Faturamento Realizado:", style = MaterialTheme.typography.bodyMedium)
                        Text(Formatters.formatCurrency(revenue), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Despesas Operacionais:", style = MaterialTheme.typography.bodyMedium)
                        Text(Formatters.formatCurrency(expenses), style = MaterialTheme.typography.bodyMedium)
                    }
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Lucro Líquido Estimado:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            Formatters.formatCurrency(netProfit),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = if (netProfit >= 0) StatusSuccess else MaterialTheme.colorScheme.error)
                        )
                    }
                }
            }
        }

        // Operational Funnel
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "FUNIL OPERACIONAL DE ATENDIMENTOS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1. Abertas / Em Triagem:", style = MaterialTheme.typography.bodyMedium)
                        Text("$openOrders ordens", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("2. Na Bancada / Em Reparo:", style = MaterialTheme.typography.bodyMedium)
                        Text("$inRepairOrders ordens", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("3. Prontas p/ Retirada do Cliente:", style = MaterialTheme.typography.bodyMedium)
                        Text("$readyPickup ordens", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("4. Concluídas e Entregues:", style = MaterialTheme.typography.bodyMedium)
                        Text("$completedOrders ordens", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // Warranties and Stock Health
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "CONFORMIDADE & SAÚDE DO ESTOQUE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Garantias Ativas (CDC Art. 26):", style = MaterialTheme.typography.bodyMedium)
                        Text("$activeWarrantiesCount equipamentos", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Peças com Estoque Baixo:", style = MaterialTheme.typography.bodyMedium)
                        Text("$lowStockCount itens", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}
