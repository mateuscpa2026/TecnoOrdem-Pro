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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.OrderStatus
import com.example.data.model.WorkOrderWithDetails
import com.example.domain.util.DocumentKind
import com.example.domain.util.Formatters
import com.example.ui.components.AutoFitText
import com.example.ui.components.OrderPhotoCard
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailDialog(
    item: WorkOrderWithDetails,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentStatus by remember { mutableStateOf(item.workOrder.status) }
    var statusDropdownExpanded by remember { mutableStateOf(false) }
    var statusChangeNote by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    var photoBefore by remember(item.workOrder.photoBeforeUri) { mutableStateOf(item.workOrder.photoBeforeUri) }
    var photoBeforeNotes by remember(item.workOrder.photoBeforeNotes) { mutableStateOf(item.workOrder.photoBeforeNotes) }
    var photoAfter by remember(item.workOrder.photoAfterUri) { mutableStateOf(item.workOrder.photoAfterUri) }
    var photoAfterNotes by remember(item.workOrder.photoAfterNotes) { mutableStateOf(item.workOrder.photoAfterNotes) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("dialog_order_detail"),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Ordem de Serviço #${item.workOrder.orderNumber}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Abertura: ${Formatters.formatDateTime(item.workOrder.openedAt)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Divider()

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Status Transition Section
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Estado Atual da Ordem",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OrderStatusBadge(status = currentStatus)

                                ExposedDropdownMenuBox(
                                    expanded = statusDropdownExpanded,
                                    onExpandedChange = { statusDropdownExpanded = it }
                                ) {
                                    Button(
                                        onClick = { statusDropdownExpanded = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                        modifier = Modifier.menuAnchor()
                                    ) {
                                        AutoFitText(
                                            text = "Alterar Status",
                                            targetFontSize = 13.sp,
                                            minFontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    ExposedDropdownMenu(
                                        expanded = statusDropdownExpanded,
                                        onDismissRequest = { statusDropdownExpanded = false }
                                    ) {
                                        OrderStatus.values().forEach { st ->
                                            DropdownMenuItem(
                                                text = { Text(st.titlePt) },
                                                onClick = {
                                                    currentStatus = st
                                                    statusDropdownExpanded = false
                                                    viewModel.updateOrderStatus(
                                                        orderId = item.workOrder.id,
                                                        newStatus = st,
                                                        notes = "Status alterado para ${st.titlePt}"
                                                    )
                                                    Toast.makeText(context, "Status atualizado: ${st.titlePt}", Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Client & Device Summary
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "CLIENTE & EQUIPAMENTO",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Cliente: ${item.customer?.name ?: "Não informado"}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Documento: ${item.customer?.documentNumber?.ifBlank { "Não informado" } ?: "-"} • Tel: ${item.customer?.phone?.ifBlank { item.customer.whatsapp } ?: "-"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            val equipTitle = "${item.equipment?.brand ?: ""} ${item.equipment?.model ?: ""}".trim().ifEmpty { "Equipamento geral" }
                            Text(
                                text = "Aparelho: $equipTitle (${item.equipment?.type?.titlePt ?: "Geral"})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Série / IMEI: ${item.equipment?.serialNumberOrImei?.ifBlank { "Não informado" } ?: "-"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Acessórios de Entrada: ${item.equipment?.accessoriesReceived ?: "-"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Condição Inicial: ${item.equipment?.entryCondition ?: "-"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Technical Details & Diagnosis
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "DIAGNÓSTICO TÉCNICO & SERVIÇOS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Defeito Relatado:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = item.workOrder.reportedDefect.ifBlank { "Nenhum defeito detalhado" },
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = "Parecer Técnico:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = item.workOrder.technicalDiagnosis.ifBlank { "Em diagnóstico na bancada" },
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = "Serviços e Peças Executados:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${item.workOrder.servicesDescription}\n${item.workOrder.partsDescription}".trim().ifEmpty { "Manutenção geral" },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Photographic Inspection (Before & After Native Camera / Gallery)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("card_photo_inspection")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LAUDO FOTOGRÁFICO DO APARELHO",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = "Antes & Depois",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // 1. Photo Before (Check-in / Reported Defect)
                            OrderPhotoCard(
                                title = "1. Foto de Entrada (Antes / Defeito)",
                                subtitle = "Estado físico do aparelho recebido na assistência",
                                photoUriString = photoBefore,
                                notes = photoBeforeNotes,
                                onPhotoChanged = { newUri ->
                                    photoBefore = newUri
                                    val updated = item.workOrder.copy(photoBeforeUri = newUri, photoBeforeNotes = photoBeforeNotes)
                                    viewModel.updateWorkOrder(updated)
                                },
                                onNotesChanged = { newNotes ->
                                    photoBeforeNotes = newNotes
                                    val updated = item.workOrder.copy(photoBeforeNotes = newNotes)
                                    viewModel.updateWorkOrder(updated)
                                },
                                testTagPrefix = "detail_photo_before"
                            )

                            // 2. Photo After (Delivered / Ready)
                            OrderPhotoCard(
                                title = "2. Foto de Saída (Depois / Reparado)",
                                subtitle = "Aparelho reparado, testado e pronto para entrega",
                                photoUriString = photoAfter,
                                notes = photoAfterNotes,
                                onPhotoChanged = { newUri ->
                                    photoAfter = newUri
                                    val updated = item.workOrder.copy(photoAfterUri = newUri, photoAfterNotes = photoAfterNotes)
                                    viewModel.updateWorkOrder(updated)
                                },
                                onNotesChanged = { newNotes ->
                                    photoAfterNotes = newNotes
                                    val updated = item.workOrder.copy(photoAfterNotes = newNotes)
                                    viewModel.updateWorkOrder(updated)
                                },
                                testTagPrefix = "detail_photo_after"
                            )
                        }
                    }

                    // Financial Summary
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "RESUMO FINANCEIRO",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Mão de Obra:", style = MaterialTheme.typography.bodySmall)
                                Text(Formatters.formatCurrency(item.workOrder.laborPrice), style = MaterialTheme.typography.bodySmall)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Peças e Materiais:", style = MaterialTheme.typography.bodySmall)
                                Text(Formatters.formatCurrency(item.workOrder.partsPrice), style = MaterialTheme.typography.bodySmall)
                            }
                            if (item.workOrder.discountAmount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Desconto Concedido:", style = MaterialTheme.typography.bodySmall, color = StatusSuccess)
                                    Text("- ${Formatters.formatCurrency(item.workOrder.discountAmount)}", style = MaterialTheme.typography.bodySmall, color = StatusSuccess)
                                }
                            }
                            Divider()
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("VALOR TOTAL:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Text(
                                    Formatters.formatCurrency(item.workOrder.totalAmount),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BluePrimary)
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Sinal Recebido:", style = MaterialTheme.typography.bodySmall, color = StatusSuccess)
                                Text(Formatters.formatCurrency(item.workOrder.downPaymentAmount), style = MaterialTheme.typography.bodySmall, color = StatusSuccess)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Saldo a Receber:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                Text(
                                    Formatters.formatCurrency(item.workOrder.remainingBalance),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (item.workOrder.remainingBalance > 0) StatusCritical else StatusSuccess
                                )
                            }
                        }
                    }

                    // Document Actions & Sharing
                    Text(
                        text = "DOCUMENTOS & COMPARTILHAMENTO",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.shareDocument(context, DocumentKind.WORK_ORDER, item)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), // WhatsApp Green
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            AutoFitText(
                                text = "WhatsApp",
                                color = Color.White,
                                targetFontSize = 13.sp,
                                minFontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.printDocument(context, DocumentKind.WORK_ORDER, item)
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            AutoFitText(
                                text = "Imprimir",
                                targetFontSize = 13.sp,
                                minFontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Document Generation Options
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Modelos Disponíveis para Exportação:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            TextButton(
                                onClick = { viewModel.shareDocument(context, DocumentKind.CHECK_IN_RECEIPT, item) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Comprovante de Entrada do Equipamento", modifier = Modifier.weight(1f), fontSize = 13.sp)
                            }

                            TextButton(
                                onClick = { viewModel.shareDocument(context, DocumentKind.QUOTE, item) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Orçamento Comercial em PDF", modifier = Modifier.weight(1f), fontSize = 13.sp)
                            }

                            TextButton(
                                onClick = { viewModel.shareDocument(context, DocumentKind.WARRANTY_CERTIFICATE, item) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Certificado de Garantia Legal (Art. 26 CDC)", modifier = Modifier.weight(1f), fontSize = 13.sp)
                            }

                            TextButton(
                                onClick = { viewModel.shareDocument(context, DocumentKind.DELIVERY_RECEIPT, item) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Termo de Retirada e Quitação", modifier = Modifier.weight(1f), fontSize = 13.sp)
                            }
                        }
                    }

                    // Order Duplication & Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.duplicateOrder(item.workOrder.id) {
                                    Toast.makeText(context, "Ordem duplicada com novo número!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            AutoFitText(
                                text = "Duplicar OS",
                                targetFontSize = 13.sp,
                                minFontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        OutlinedButton(
                            onClick = { showDeleteConfirm = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCritical),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            AutoFitText(
                                text = "Excluir",
                                targetFontSize = 13.sp,
                                minFontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Excluir Ordem de Serviço") },
            text = { Text("Tem certeza que deseja excluir a OS #${item.workOrder.orderNumber}? Esta ação não poderá ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteWorkOrder(item.workOrder)
                        showDeleteConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCritical)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
