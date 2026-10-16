package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Language
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.example.domain.util.LanguagePickerDialog
import com.example.domain.util.LocalAppLanguage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.WorkOrderWithDetails
import com.example.domain.util.AppLanguage
import com.example.domain.util.AppStrings
import com.example.ui.components.AppLogoView
import com.example.ui.screens.CompanySettingsScreen
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FinancialScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.NewOrderDialog
import com.example.ui.screens.OrderDetailDialog
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.WarrantiesScreen
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.TecnoOrdemTheme
import com.example.ui.viewmodel.MainViewModel

enum class AppTab(val key: String, val icon: ImageVector) {
    DASHBOARD("nav_dashboard", Icons.Default.Dashboard),
    ORDERS("nav_orders", Icons.Default.Assignment),
    CUSTOMERS("nav_customers", Icons.Default.People),
    INVENTORY("nav_inventory", Icons.Default.Inventory2),
    FINANCIAL("nav_financial", Icons.Default.AttachMoney),
    WARRANTIES("nav_warranties", Icons.Default.Security),
    REPORTS("nav_reports", Icons.Default.Assessment),
    SETTINGS("nav_settings", Icons.Default.Store)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TecnoOrdemTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    viewModel: MainViewModel = viewModel(factory = MainViewModel.provideFactory())
) {
    var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }
    var selectedOrderForDetail by remember { mutableStateOf<WorkOrderWithDetails?>(null) }
    var showNewOrderDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("tecno_ordem_app_prefs", Context.MODE_PRIVATE) }
    var hasSelectedInitialLanguage by remember {
        mutableStateOf(prefs.getBoolean("has_selected_lang_v4", false))
    }
    var showLanguageDialog by remember { mutableStateOf(!hasSelectedInitialLanguage) }

    val companyProfile by viewModel.companyProfile.collectAsState()
    val appLang = remember(companyProfile.appLanguage) { AppLanguage.fromCode(companyProfile.appLanguage) }

    // Handle Android system back button: return to Dashboard tab if on another tab
    BackHandler(enabled = currentTab != AppTab.DASHBOARD) {
        currentTab = AppTab.DASHBOARD
    }

    CompositionLocalProvider(LocalAppLanguage provides appLang) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppLogoView(
                                logoUri = companyProfile.logoUri,
                                size = 36.dp,
                                isCircular = false
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = companyProfile.tradeName.ifBlank { "TecnoOrdem Pro" },
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                                Text(
                                    text = AppStrings.get(currentTab.key, appLang),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            onClick = { showLanguageDialog = true },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .testTag("action_language_select")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(text = appLang.flagEmoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = appLang.code.split("-")[0].uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        IconButton(
                            onClick = { currentTab = AppTab.WARRANTIES },
                            modifier = Modifier.testTag("action_warranties_top")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Garantias CDC",
                                tint = if (currentTab == AppTab.WARRANTIES) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { currentTab = AppTab.REPORTS },
                            modifier = Modifier.testTag("action_reports_top")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = "Relatórios",
                                tint = if (currentTab == AppTab.REPORTS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { currentTab = AppTab.SETTINGS },
                            modifier = Modifier.testTag("action_settings_top")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = "Dados da Empresa",
                                tint = if (currentTab == AppTab.SETTINGS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    val visibleTabs = listOf(
                        AppTab.DASHBOARD,
                        AppTab.ORDERS,
                        AppTab.CUSTOMERS,
                        AppTab.INVENTORY,
                        AppTab.FINANCIAL,
                        AppTab.SETTINGS
                    )

                    visibleTabs.forEach { tab ->
                        val isSelected = currentTab == tab
                        val label = AppStrings.get(tab.key, appLang)
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 9.5.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Visible,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            alwaysShowLabel = true,
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    AppTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToOrders = { currentTab = AppTab.ORDERS },
                        onNavigateToCustomers = { currentTab = AppTab.CUSTOMERS },
                        onNavigateToInventory = { currentTab = AppTab.INVENTORY },
                        onNavigateToFinancial = { currentTab = AppTab.FINANCIAL },
                        onNavigateToWarranties = { currentTab = AppTab.WARRANTIES },
                        onNewOrderClick = { showNewOrderDialog = true },
                        onSelectOrder = { order -> selectedOrderForDetail = order },
                        onOpenLanguagePicker = { showLanguageDialog = true }
                    )

                    AppTab.ORDERS -> OrdersScreen(
                        viewModel = viewModel,
                        onNewOrderClick = { showNewOrderDialog = true },
                        onSelectOrder = { order -> selectedOrderForDetail = order }
                    )

                    AppTab.CUSTOMERS -> CustomersScreen(
                        viewModel = viewModel
                    )

                    AppTab.INVENTORY -> InventoryScreen(
                        viewModel = viewModel
                    )

                    AppTab.FINANCIAL -> FinancialScreen(
                        viewModel = viewModel
                    )

                    AppTab.WARRANTIES -> WarrantiesScreen(
                        viewModel = viewModel
                    )

                    AppTab.REPORTS -> ReportsScreen(
                        viewModel = viewModel
                    )

                    AppTab.SETTINGS -> CompanySettingsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }

        // Detail Modal Dialog
        selectedOrderForDetail?.let { order ->
            OrderDetailDialog(
                item = order,
                viewModel = viewModel,
                onDismiss = { selectedOrderForDetail = null }
            )
        }

        // New Order Modal Dialog
        if (showNewOrderDialog) {
            NewOrderDialog(
                viewModel = viewModel,
                onDismiss = { showNewOrderDialog = false }
            )
        }

        // Language Selection Dialog (First Launch & Header Action)
        if (showLanguageDialog) {
            LanguagePickerDialog(
                currentLanguage = appLang,
                onLanguageSelected = { selectedLang ->
                    viewModel.setAppLanguage(selectedLang)
                    prefs.edit().putBoolean("has_selected_lang_v4", true).apply()
                    hasSelectedInitialLanguage = true
                    showLanguageDialog = false
                },
                onDismiss = {
                    showLanguageDialog = false
                    prefs.edit().putBoolean("has_selected_lang_v4", true).apply()
                }
            )
        }
    }
}
