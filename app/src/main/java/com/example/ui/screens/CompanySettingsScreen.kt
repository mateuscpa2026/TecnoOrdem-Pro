package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanyProfile
import com.example.domain.util.AppLanguage
import com.example.domain.util.AppStrings
import com.example.ui.components.AppLogoView
import com.example.ui.components.AutoFitText
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanySettingsScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val profile by viewModel.companyProfile.collectAsState()
    val currentLang = remember(profile.appLanguage) { AppLanguage.fromCode(profile.appLanguage) }

    var tradeName by remember(profile) { mutableStateOf(profile.tradeName) }
    var corporateName by remember(profile) { mutableStateOf(profile.corporateName) }
    var documentNumber by remember(profile) { mutableStateOf(profile.documentNumber) }
    var phone by remember(profile) { mutableStateOf(profile.phone) }
    var whatsapp by remember(profile) { mutableStateOf(profile.whatsapp) }
    var email by remember(profile) { mutableStateOf(profile.email) }
    var website by remember(profile) { mutableStateOf(profile.website) }
    var address by remember(profile) { mutableStateOf(profile.address) }
    var neighborhood by remember(profile) { mutableStateOf(profile.neighborhood) }
    var city by remember(profile) { mutableStateOf(profile.city) }
    var state by remember(profile) { mutableStateOf(profile.state) }
    var zipCode by remember(profile) { mutableStateOf(profile.zipCode) }
    var technicalManager by remember(profile) { mutableStateOf(profile.technicalManager) }
    var businessHours by remember(profile) { mutableStateOf(profile.businessHours) }
    var pixKey by remember(profile) { mutableStateOf(profile.pixKey) }
    var paymentInstructions by remember(profile) { mutableStateOf(profile.paymentInstructions) }
    var standardTerms by remember(profile) { mutableStateOf(profile.standardTerms) }
    var warrantyTerms by remember(profile) { mutableStateOf(profile.warrantyTerms) }
    var selectedLanguage by remember(profile) { mutableStateOf(currentLang) }
    var languageDropdownExpanded by remember { mutableStateOf(false) }

    var customLogoUri by remember(profile) { mutableStateOf(profile.logoUri) }

    // Zero-permission Android Photo Picker for picking custom shop logo
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                // Delete previous logo files to keep storage clean
                context.filesDir.listFiles { _, name -> name.startsWith("company_logo_") || name == "company_logo.png" }?.forEach { it.delete() }

                // Create a permanent file in app internal storage (never loses permissions across restarts)
                val logoFile = File(context.filesDir, "company_logo_${System.currentTimeMillis()}.png")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(logoFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val permanentUri = Uri.fromFile(logoFile).toString()
                customLogoUri = permanentUri

                // Persist immediately in the database so all screens update in real time and survive app close
                val current = viewModel.companyProfile.value
                val updated = current.copy(logoUri = permanentUri)
                viewModel.updateCompanyProfile(updated)

                Toast.makeText(context, "Logotipo da empresa atualizado e salvo com sucesso!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao salvar logotipo: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("company_settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Identidade da Assistência Técnica",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Personalize o nome, logotipo, idioma, dados fiscais, termos e formas de pagamento que aparecem no aplicativo e nos documentos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Custom Logo Personalization Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "LOGOTIPO DA EMPRESA",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppLogoView(
                            logoUri = customLogoUri,
                            size = 56.dp,
                            isCircular = false
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (customLogoUri.isNotBlank()) "Logotipo personalizado ativo" else "Logotipo padrão da assistência",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Exibido no aplicativo e em todos os documentos e PDFs gerados",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_pick_custom_logo")
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            AutoFitText(
                                text = AppStrings.get("change_logo", selectedLanguage),
                                targetFontSize = 13.sp,
                                minFontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (customLogoUri.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    customLogoUri = ""
                                    try {
                                        context.filesDir.listFiles { _, name -> name.startsWith("company_logo_") || name == "company_logo.png" }?.forEach { it.delete() }
                                    } catch (_: Exception) {}
                                    val current = viewModel.companyProfile.value
                                    val updated = current.copy(logoUri = "")
                                    viewModel.updateCompanyProfile(updated)
                                    Toast.makeText(context, "Logotipo padrão restaurado e salvo!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                AutoFitText(
                                    text = AppStrings.get("restore_default_logo", selectedLanguage),
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

        // Language Picker Card (10 Languages)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get("language_setting", selectedLanguage),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = languageDropdownExpanded,
                        onExpandedChange = { languageDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${selectedLanguage.flagEmoji} ${selectedLanguage.displayName}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Idioma do Sistema") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("dropdown_language_selector")
                        )

                        ExposedDropdownMenu(
                            expanded = languageDropdownExpanded,
                            onDismissRequest = { languageDropdownExpanded = false }
                        ) {
                            AppLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text("${lang.flagEmoji}  ${lang.displayName}") },
                                    onClick = {
                                        selectedLanguage = lang
                                        languageDropdownExpanded = false
                                        viewModel.setAppLanguage(lang)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Company Details Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "DADOS DA EMPRESA",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = tradeName,
                        onValueChange = { tradeName = it },
                        label = { Text("Nome Comercial / Nome Fantasia *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = corporateName,
                        onValueChange = { corporateName = it },
                        label = { Text("Razão Social") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = documentNumber,
                            onValueChange = { documentNumber = it },
                            label = { Text("CNPJ ou CPF *") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = technicalManager,
                            onValueChange = { technicalManager = it },
                            label = { Text("Responsável Técnico") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Telefone Fixo") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = whatsapp,
                            onValueChange = { whatsapp = it },
                            label = { Text("WhatsApp Comercial *") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("E-mail Comercial") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = website,
                            onValueChange = { website = it },
                            label = { Text("Site / Instagram") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Address & Hours
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "ENDEREÇO & ATENDIMENTO",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Endereço Completo (Rua, Número, Sala)") },
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

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text("Estado (UF)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = zipCode,
                            onValueChange = { zipCode = it },
                            label = { Text("CEP") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = businessHours,
                        onValueChange = { businessHours = it },
                        label = { Text("Horário de Atendimento") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Payment & Pix
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "DADOS DE PAGAMENTO & PIX",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = pixKey,
                        onValueChange = { pixKey = it },
                        label = { Text("Chave Pix Oficial") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = paymentInstructions,
                        onValueChange = { paymentInstructions = it },
                        label = { Text("Instruções de Pagamento impressas no rodapé dos orçamentos") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Legal & Warranty Terms
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "TERMOS COMERCIAIS & GARANTIA CDC",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = warrantyTerms,
                        onValueChange = { warrantyTerms = it },
                        label = { Text("Termos da Garantia Legal (Lei 8.078/90 CDC) *") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = standardTerms,
                        onValueChange = { standardTerms = it },
                        label = { Text("Condições Gerais de Recebimento de Equipamentos") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val updated = profile.copy(
                        tradeName = tradeName.trim().ifBlank { "TecnoOrdem Pro" },
                        corporateName = corporateName.trim(),
                        documentNumber = documentNumber.trim(),
                        phone = phone.trim(),
                        whatsapp = whatsapp.trim(),
                        email = email.trim(),
                        website = website.trim(),
                        address = address.trim(),
                        neighborhood = neighborhood.trim(),
                        city = city.trim(),
                        state = state.trim(),
                        zipCode = zipCode.trim(),
                        technicalManager = technicalManager.trim(),
                        businessHours = businessHours.trim(),
                        pixKey = pixKey.trim(),
                        paymentInstructions = paymentInstructions.trim(),
                        standardTerms = standardTerms.trim(),
                        warrantyTerms = warrantyTerms.trim(),
                        appLanguage = selectedLanguage.code,
                        logoUri = customLogoUri
                    )
                    viewModel.updateCompanyProfile(updated)
                    viewModel.setAppLanguage(selectedLanguage)
                    val prefs = context.getSharedPreferences("tecno_ordem_app_prefs", android.content.Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("has_selected_lang_v4", true).putString("app_language", selectedLanguage.code).apply()
                    Toast.makeText(context, AppStrings.get("company_saved", selectedLanguage), Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_company_settings")
            ) {
                AutoFitText(
                    text = AppStrings.get("save_company", selectedLanguage),
                    fontWeight = FontWeight.Bold,
                    targetFontSize = 15.sp,
                    minFontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
