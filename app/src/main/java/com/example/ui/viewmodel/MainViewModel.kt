package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.TecnoOrdemApp
import com.example.data.model.CompanyProfile
import com.example.data.model.Customer
import com.example.data.model.Equipment
import com.example.data.model.FinancialTransaction
import com.example.data.model.InventoryPart
import com.example.data.model.OrderStatus
import com.example.data.model.Warranty
import com.example.data.model.WorkOrder
import com.example.data.model.WorkOrderWithDetails
import com.example.data.preferences.AppPreferencesDataStore
import com.example.data.preferences.UserPreferences
import com.example.data.repository.TecnoOrdemRepository
import com.example.domain.util.DocumentKind
import com.example.domain.util.PdfDocumentGenerator
import com.example.domain.util.PrintHelper
import com.example.domain.util.ShareHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    private val repository: TecnoOrdemRepository,
    private val appPreferences: AppPreferencesDataStore = TecnoOrdemApp.instance.preferencesDataStore
) : ViewModel() {

    // User Preferences from DataStore (Language, Logo URI, Onboarding state)
    val userPreferences: StateFlow<UserPreferences> = appPreferences.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    // Company Profile merged with DataStore preferences (DataStore is immediate source of truth for language and logo)
    val companyProfile: StateFlow<CompanyProfile> = combine(
        repository.companyProfile,
        userPreferences
    ) { dbProfile, prefs ->
        val base = dbProfile ?: CompanyProfile()
        base.copy(
            appLanguage = prefs.appLanguage.ifBlank { base.appLanguage },
            logoUri = if (prefs.companyLogoUri.isNotBlank()) prefs.companyLogoUri else base.logoUri
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CompanyProfile())

    // Search & Filter States
    private val _orderSearchQuery = MutableStateFlow("")
    val orderSearchQuery = _orderSearchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<OrderStatus?>(null)
    val selectedStatusFilter = _selectedStatusFilter.asStateFlow()

    private val _customerSearchQuery = MutableStateFlow("")
    val customerSearchQuery = _customerSearchQuery.asStateFlow()

    private val _inventorySearchQuery = MutableStateFlow("")
    val inventorySearchQuery = _inventorySearchQuery.asStateFlow()

    // Base flows from repository
    val allOrdersRaw = repository.allOrdersWithDetails
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredOrders: StateFlow<List<WorkOrderWithDetails>> = combine(
        allOrdersRaw,
        _orderSearchQuery,
        _selectedStatusFilter
    ) { orders, query, statusFilter ->
        orders.filter { item ->
            val matchesStatus = statusFilter == null || item.workOrder.status == statusFilter
            val matchesQuery = query.isBlank() ||
                item.workOrder.orderNumber.toString().contains(query, ignoreCase = true) ||
                (item.customer?.name?.contains(query, ignoreCase = true) == true) ||
                (item.customer?.documentNumber?.contains(query, ignoreCase = true) == true) ||
                (item.customer?.phone?.contains(query, ignoreCase = true) == true) ||
                (item.equipment?.brand?.contains(query, ignoreCase = true) == true) ||
                (item.equipment?.model?.contains(query, ignoreCase = true) == true) ||
                (item.workOrder.reportedDefect.contains(query, ignoreCase = true))

            matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCustomers = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredCustomers: StateFlow<List<Customer>> = combine(
        allCustomers,
        _customerSearchQuery
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.documentNumber.contains(query, ignoreCase = true) ||
            it.phone.contains(query, ignoreCase = true) ||
            it.whatsapp.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEquipments = repository.allEquipments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInventoryParts = repository.allInventoryParts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredInventoryParts: StateFlow<List<InventoryPart>> = combine(
        allInventoryParts,
        _inventorySearchQuery
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.skuCode.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true) ||
            it.barcode.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockParts = repository.lowStockParts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome = repository.totalIncome
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpenses = repository.totalExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val allWarranties = repository.allWarranties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeWarranties = repository.activeWarranties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // KPI Counters
    val totalOrdersCount = repository.getTotalOrdersCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val openOrdersCount = repository.getOrderCountByStatus(OrderStatus.OPEN)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val inRepairOrdersCount = repository.getOrderCountByStatus(OrderStatus.IN_REPAIR)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val readyPickupCount = repository.getOrderCountByStatus(OrderStatus.READY_FOR_PICKUP)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedOrdersCount = repository.getOrderCountByStatus(OrderStatus.COMPLETED)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalPendingBalance = repository.getTotalPendingBalance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val lowStockCount = repository.getLowStockCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeWarrantiesCount = repository.getActiveWarrantiesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // User Operations
    fun setOrderSearchQuery(query: String) {
        _orderSearchQuery.value = query
    }

    fun setSelectedStatusFilter(status: OrderStatus?) {
        _selectedStatusFilter.value = status
    }

    fun setCustomerSearchQuery(query: String) {
        _customerSearchQuery.value = query
    }

    fun setInventorySearchQuery(query: String) {
        _inventorySearchQuery.value = query
    }

    fun saveCustomer(customer: Customer, onDone: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.saveCustomer(customer)
            onDone(id)
        }
    }

    fun deleteCustomer(customer: Customer, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                repository.deleteCustomer(customer)
                onResult(true, null)
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Erro ao excluir cliente", e)
                onResult(false, e.localizedMessage ?: "Erro ao excluir cliente")
            }
        }
    }

    fun setAppLanguage(language: com.example.domain.util.AppLanguage) {
        viewModelScope.launch {
            val current = companyProfile.value
            repository.updateCompanyProfile(current.copy(appLanguage = language.code))
        }
    }

    fun saveEquipment(equipment: Equipment, onDone: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.saveEquipment(equipment)
            onDone(id)
        }
    }

    fun deleteEquipment(equipment: Equipment) {
        viewModelScope.launch {
            repository.deleteEquipment(equipment)
        }
    }

    fun saveWorkOrder(order: WorkOrder, notes: String = "", onDone: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.saveWorkOrder(order, notes)
            onDone(id)
        }
    }

    fun updateOrderStatus(
        orderId: Long,
        newStatus: OrderStatus,
        notes: String = "",
        technician: String = "Técnico"
    ) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus, notes, technician)
        }
    }

    fun duplicateOrder(orderId: Long, onDone: (Long?) -> Unit = {}) {
        viewModelScope.launch {
            val newId = repository.duplicateOrder(orderId)
            onDone(newId)
        }
    }

    fun deleteWorkOrder(order: WorkOrder) {
        viewModelScope.launch {
            repository.deleteWorkOrder(order)
        }
    }

    fun saveInventoryPart(part: InventoryPart) {
        viewModelScope.launch {
            repository.saveInventoryPart(part)
        }
    }

    fun deleteInventoryPart(part: InventoryPart) {
        viewModelScope.launch {
            repository.deleteInventoryPart(part)
        }
    }

    fun adjustStock(partId: Long, increment: Boolean, amount: Int = 1) {
        viewModelScope.launch {
            if (increment) {
                val part = repository.allInventoryParts
                // Use increment or save
            } else {
                repository.decrementStock(partId, amount)
            }
        }
    }

    fun saveTransaction(transaction: FinancialTransaction) {
        viewModelScope.launch {
            repository.saveTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: FinancialTransaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun updateCompanyProfile(profile: CompanyProfile) {
        viewModelScope.launch {
            repository.updateCompanyProfile(profile)
        }
    }

    // Document Generation & Actions
    fun generateDocument(
        context: Context,
        kind: DocumentKind,
        item: WorkOrderWithDetails,
        warranty: Warranty? = null
    ): File {
        return PdfDocumentGenerator.generateDocument(
            context = context,
            kind = kind,
            company = companyProfile.value,
            workOrder = item.workOrder,
            customer = item.customer,
            equipment = item.equipment,
            warranty = warranty
        )
    }

    fun shareDocument(
        context: Context,
        kind: DocumentKind,
        item: WorkOrderWithDetails,
        warranty: Warranty? = null
    ) {
        val pdf = generateDocument(context, kind, item, warranty)
        ShareHelper.shareDocument(
            context = context,
            pdfFile = pdf,
            kind = kind,
            workOrder = item.workOrder,
            customer = item.customer,
            equipment = item.equipment
        )
    }

    fun printDocument(
        context: Context,
        kind: DocumentKind,
        item: WorkOrderWithDetails,
        warranty: Warranty? = null
    ) {
        val pdf = generateDocument(context, kind, item, warranty)
        PrintHelper.printPdf(
            context = context,
            pdfFile = pdf,
            jobTitle = "${kind.docTitle} OS #${item.workOrder.orderNumber}"
        )
    }

    fun getStatusHistory(orderId: Long) = repository.getStatusHistory(orderId)

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(TecnoOrdemApp.instance.repository) as T
            }
        }
    }
}
