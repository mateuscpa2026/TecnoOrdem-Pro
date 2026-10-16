package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.model.Customer
import com.example.domain.util.appString
import com.example.ui.components.AutoFitText
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.StatusCritical
import com.example.ui.viewmodel.MainViewModel

@Composable
fun CustomersScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val customers by viewModel.filteredCustomers.collectAsState()
    val searchQuery by viewModel.customerSearchQuery.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_new_customer")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Cliente")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("customers_screen")
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setCustomerSearchQuery(it) },
                placeholder = { Text(appString("search_customers")) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setCustomerSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpar")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (customers.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Person,
                    title = appString("customers_title"),
                    message = appString("search_customers")
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(customers, key = { it.id }) { customer ->
                        CustomerCard(
                            customer = customer,
                            onWhatsAppClick = {
                                val cleanNum = customer.whatsapp.replace(Regex("[^0-9]"), "")
                                if (cleanNum.isNotBlank()) {
                                    val uri = Uri.parse("https://api.whatsapp.com/send?phone=55$cleanNum")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                } else {
                                    Toast.makeText(context, "WhatsApp não informado", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCallClick = {
                                val cleanNum = customer.phone.replace(Regex("[^0-9]"), "")
                                if (cleanNum.isNotBlank()) {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNum")))
                                } else {
                                    Toast.makeText(context, "Telefone não informado", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDeleteClick = { customerToDelete = customer }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        CustomerFormDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newCust ->
                viewModel.saveCustomer(newCust) {
                    Toast.makeText(context, "Cliente cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                    showAddDialog = false
                }
            }
        )
    }

    customerToDelete?.let { cust ->
        val customerDeletedMsg = appString("customer_deleted")
        val deleteErrorFallback = appString("error_occurred")
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            title = { Text(appString("delete_customer_title")) },
            text = { Text("${appString("delete_customer_msg")} (${cust.name})") },
            confirmButton = {
                Button(
                    onClick = {
                        val toDelete = cust
                        customerToDelete = null
                        viewModel.deleteCustomer(toDelete) { success, errorMsg ->
                            if (success) {
                                Toast.makeText(context, customerDeletedMsg, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Erro: ${errorMsg ?: deleteErrorFallback}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCritical),
                    modifier = Modifier.testTag("btn_confirm_delete_customer")
                ) {
                    Text(appString("delete"))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { customerToDelete = null },
                    modifier = Modifier.testTag("btn_cancel_delete_customer")
                ) {
                    Text(appString("cancel"))
                }
            }
        )
    }
}

@Composable
fun CustomerCard(
    customer: Customer,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = StatusCritical, modifier = Modifier.size(18.dp))
                }
            }

            if (customer.documentNumber.isNotBlank()) {
                Text(
                    text = "CPF/CNPJ: ${customer.documentNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (customer.address.isNotBlank()) {
                val fullAddr = "${customer.address}, ${customer.neighborhood} - ${customer.city}/${customer.state}".trim()
                Text(
                    text = fullAddr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (customer.whatsapp.isNotBlank()) {
                    OutlinedButton(
                        onClick = onWhatsAppClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Message, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        AutoFitText(text = "WhatsApp", targetFontSize = 12.sp, minFontSize = 9.sp)
                    }
                }

                if (customer.phone.isNotBlank()) {
                    OutlinedButton(
                        onClick = onCallClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        AutoFitText(text = appString("call"), targetFontSize = 12.sp, minFontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerFormDialog(
    onDismiss: () -> Unit,
    onSave: (Customer) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var docNumber by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("SP") }
    var zipCode by remember { mutableStateOf("") }

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
                    Text("Cadastrar Cliente", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Divider()

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Completo / Razão Social *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = docNumber,
                    onValueChange = { docNumber = it },
                    label = { Text("CPF ou CNPJ") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefone") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("WhatsApp *") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Endereço (Rua, Número, Apto)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = neighborhood,
                        onValueChange = { neighborhood = it },
                        label = { Text("Bairro") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Cidade") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = {
                        if (name.isBlank()) return@Button
                        onSave(
                            Customer(
                                name = name.trim(),
                                documentNumber = docNumber.trim(),
                                phone = phone.trim(),
                                whatsapp = whatsapp.ifBlank { phone }.trim(),
                                email = email.trim(),
                                address = address.trim(),
                                neighborhood = neighborhood.trim(),
                                city = city.trim(),
                                state = state.trim(),
                                zipCode = zipCode.trim()
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
