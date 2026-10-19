package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.domain.util.appString
import com.example.ui.components.AutoFitText
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Customer
import com.example.data.model.Equipment
import com.example.data.model.EquipmentType
import com.example.data.model.OrderStatus
import com.example.data.model.WorkOrder
import com.example.ui.components.OrderPhotoCard
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val customers by viewModel.allCustomers.collectAsState()
    val equipments by viewModel.allEquipments.collectAsState()

    var selectedCustomer by remember { mutableStateOf<Customer?>(customers.firstOrNull()) }
    var customerDropdownExpanded by remember { mutableStateOf(false) }

    // Quick fields if creating inline
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerCpfCnpj by remember { mutableStateOf("") }

    var selectedEquipmentType by remember { mutableStateOf(EquipmentType.SMARTPHONE) }
    var equipTypeDropdownExpanded by remember { mutableStateOf(false) }
    var equipBrand by remember { mutableStateOf("") }
    var equipModel by remember { mutableStateOf("") }
    var equipSerialOrImei by remember { mutableStateOf("") }
    var equipCondition by remember { mutableStateOf("Sinais normais de uso") }
    var equipAccessories by remember { mutableStateOf("Aparelho sem acessórios") }

    var photoBeforeUri by remember { mutableStateOf("") }
    var photoBeforeNotes by remember { mutableStateOf("") }

    var reportedDefect by remember { mutableStateOf("") }
    var servicesDescription by remember { mutableStateOf("") }
    var partsDescription by remember { mutableStateOf("") }
    var laborPriceStr by remember { mutableStateOf("") }
    var partsPriceStr by remember { mutableStateOf("") }
    var discountStr by remember { mutableStateOf("") }
    var downPaymentStr by remember { mutableStateOf("") }
    var technician by remember { mutableStateOf("Carlos Eduardo") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("dialog_new_order"),
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
                    Text(
                        text = "Nova Ordem de Serviço",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Divider()

                // Form Scrollable
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Customer Selection
                    Text(
                        text = "1. IDENTIFICAÇÃO DO CLIENTE",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (customers.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = customerDropdownExpanded,
                            onExpandedChange = { customerDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCustomer?.name ?: "Selecione um cliente cadastrado",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerDropdownExpanded) },
                                label = { Text("Cliente Cadastrado") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = customerDropdownExpanded,
                                onDismissRequest = { customerDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("+ Cadastrar Novo Cliente Agora") },
                                    onClick = {
                                        selectedCustomer = null
                                        customerDropdownExpanded = false
                                    }
                                )
                                Divider()
                                customers.forEach { cust ->
                                    DropdownMenuItem(
                                        text = { Text("${cust.name} (${cust.phone.ifBlank { cust.whatsapp }})") },
                                        onClick = {
                                            selectedCustomer = cust
                                            customerDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (selectedCustomer == null) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Nome Completo / Razão Social *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_new_customer_name")
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = customerPhone,
                                onValueChange = { customerPhone = it },
                                label = { Text("Telefone / WhatsApp *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = customerCpfCnpj,
                                onValueChange = { customerCpfCnpj = it },
                                label = { Text("CPF / CNPJ") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // Equipment Details
                    Text(
                        text = "2. EQUIPAMENTO E CHECKLIST DE ENTRADA",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    ExposedDropdownMenuBox(
                        expanded = equipTypeDropdownExpanded,
                        onExpandedChange = { equipTypeDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedEquipmentType.titlePt,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Equipamento") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = equipTypeDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = equipTypeDropdownExpanded,
                            onDismissRequest = { equipTypeDropdownExpanded = false }
                        ) {
                            EquipmentType.values().forEach { eq ->
                                DropdownMenuItem(
                                    text = { Text(eq.titlePt) },
                                    onClick = {
                                        selectedEquipmentType = eq
                                        equipTypeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = equipBrand,
                            onValueChange = { equipBrand = it },
                            label = { Text("Marca (ex: Samsung, Dell) *") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = equipModel,
                            onValueChange = { equipModel = it },
                            label = { Text("Modelo (ex: Galaxy S22) *") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = equipSerialOrImei,
                        onValueChange = { equipSerialOrImei = it },
                        label = { Text("Número de Série ou IMEI") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = equipAccessories,
                        onValueChange = { equipAccessories = it },
                        label = { Text("Acessórios Recebidos (Cabo, Fonte, Capa, etc.)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = equipCondition,
                        onValueChange = { equipCondition = it },
                        label = { Text("Estado de Conservação / Avarias Visíveis") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OrderPhotoCard(
                        title = "Foto do Aparelho na Entrada (Antes)",
                        subtitle = "Tire uma foto do estado físico do aparelho com a câmera nativa ou anexe da galeria",
                        photoUriString = photoBeforeUri,
                        notes = photoBeforeNotes,
                        onPhotoChanged = { photoBeforeUri = it },
                        onNotesChanged = { photoBeforeNotes = it },
                        testTagPrefix = "new_order_photo_before"
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // Defect & Technical Info
                    Text(
                        text = "3. DEFEITO RELATADO & SERVIÇOS",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = reportedDefect,
                        onValueChange = { reportedDefect = it },
                        label = { Text("Defeito Relatado pelo Cliente *") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_reported_defect")
                    )

                    OutlinedTextField(
                        value = servicesDescription,
                        onValueChange = { servicesDescription = it },
                        label = { Text("Serviços Previstos / Solicitados") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = partsDescription,
                        onValueChange = { partsDescription = it },
                        label = { Text("Peças Previstas / Utilizadas") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // Financial Values
                    Text(
                        text = "4. VALORES & PAGAMENTO",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = laborPriceStr,
                            onValueChange = { laborPriceStr = it },
                            label = { Text("Mão de Obra (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = partsPriceStr,
                            onValueChange = { partsPriceStr = it },
                            label = { Text("Peças (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = discountStr,
                            onValueChange = { discountStr = it },
                            label = { Text("Desconto (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = downPaymentStr,
                            onValueChange = { downPaymentStr = it },
                            label = { Text("Sinal / Entrada (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = technician,
                        onValueChange = { technician = it },
                        label = { Text("Técnico Responsável") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Divider()

                // Save Footer Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val finalCustName = selectedCustomer?.name ?: customerName.trim()
                            if (finalCustName.isBlank()) {
                                Toast.makeText(context, "Por favor informe o nome do cliente", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (equipBrand.isBlank() && equipModel.isBlank()) {
                                Toast.makeText(context, "Informe a marca e o modelo do equipamento", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val labor = laborPriceStr.toDoubleOrNull() ?: 0.0
                            val parts = partsPriceStr.toDoubleOrNull() ?: 0.0
                            val discount = discountStr.toDoubleOrNull() ?: 0.0
                            val downPayment = downPaymentStr.toDoubleOrNull() ?: 0.0

                            if (selectedCustomer != null) {
                                val custId = selectedCustomer!!.id
                                val equip = Equipment(
                                    customerId = custId,
                                    type = selectedEquipmentType,
                                    brand = equipBrand.ifBlank { "Geral" },
                                    model = equipModel.ifBlank { "Equipamento" },
                                    serialNumberOrImei = equipSerialOrImei,
                                    entryCondition = equipCondition,
                                    accessoriesReceived = equipAccessories
                                )
                                viewModel.saveEquipment(equip) { equipId ->
                                    val order = WorkOrder(
                                        orderNumber = 0,
                                        customerId = custId,
                                        equipmentId = equipId,
                                        status = OrderStatus.OPEN,
                                        reportedDefect = reportedDefect,
                                        servicesDescription = servicesDescription,
                                        partsDescription = partsDescription,
                                        laborPrice = labor,
                                        partsPrice = parts,
                                        discountAmount = discount,
                                        downPaymentAmount = downPayment,
                                        assignedTechnician = technician,
                                        photoBeforeUri = photoBeforeUri,
                                        photoBeforeNotes = photoBeforeNotes
                                    )
                                    viewModel.saveWorkOrder(order) {
                                        Toast.makeText(context, "Ordem de serviço aberta com sucesso!", Toast.LENGTH_LONG).show()
                                        onDismiss()
                                    }
                                }
                            } else {
                                val newCust = Customer(
                                    name = customerName.trim(),
                                    phone = customerPhone.trim(),
                                    whatsapp = customerPhone.trim(),
                                    documentNumber = customerCpfCnpj.trim()
                                )
                                viewModel.saveCustomer(newCust) { newCustId ->
                                    val equip = Equipment(
                                        customerId = newCustId,
                                        type = selectedEquipmentType,
                                        brand = equipBrand.ifBlank { "Geral" },
                                        model = equipModel.ifBlank { "Equipamento" },
                                        serialNumberOrImei = equipSerialOrImei,
                                        entryCondition = equipCondition,
                                        accessoriesReceived = equipAccessories
                                    )
                                    viewModel.saveEquipment(equip) { equipId ->
                                        val order = WorkOrder(
                                            orderNumber = 0,
                                            customerId = newCustId,
                                            equipmentId = equipId,
                                            status = OrderStatus.OPEN,
                                            reportedDefect = reportedDefect,
                                            servicesDescription = servicesDescription,
                                            partsDescription = partsDescription,
                                            laborPrice = labor,
                                            partsPrice = parts,
                                            discountAmount = discount,
                                            downPaymentAmount = downPayment,
                                            assignedTechnician = technician,
                                            photoBeforeUri = photoBeforeUri,
                                            photoBeforeNotes = photoBeforeNotes
                                        )
                                        viewModel.saveWorkOrder(order) {
                                            Toast.makeText(context, "Ordem de serviço aberta com sucesso!", Toast.LENGTH_LONG).show()
                                            onDismiss()
                                        }
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_save_new_order")
                    ) {
                        AutoFitText(
                            text = appString("new_order"),
                            fontWeight = FontWeight.Bold,
                            targetFontSize = 15.sp,
                            minFontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
