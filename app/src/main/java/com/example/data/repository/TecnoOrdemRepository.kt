package com.example.data.repository

import com.example.data.dao.CompanyProfileDao
import com.example.data.dao.CustomerDao
import com.example.data.dao.EquipmentDao
import com.example.data.dao.FinancialDao
import com.example.data.dao.InventoryDao
import com.example.data.dao.StatusHistoryDao
import com.example.data.dao.WarrantyDao
import com.example.data.dao.WorkOrderDao
import com.example.data.model.CompanyProfile
import com.example.data.model.Customer
import com.example.data.model.CustomerWithDetails
import com.example.data.model.Equipment
import com.example.data.model.FinancialTransaction
import com.example.data.model.InventoryPart
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.StatusHistoryEntry
import com.example.data.model.TransactionType
import com.example.data.model.Warranty
import com.example.data.model.WarrantyType
import com.example.data.model.WorkOrder
import com.example.data.model.WorkOrderWithDetails
import kotlinx.coroutines.flow.Flow

class TecnoOrdemRepository(
    private val companyProfileDao: CompanyProfileDao,
    private val customerDao: CustomerDao,
    private val equipmentDao: EquipmentDao,
    private val workOrderDao: WorkOrderDao,
    private val statusHistoryDao: StatusHistoryDao,
    private val inventoryDao: InventoryDao,
    private val financialDao: FinancialDao,
    private val warrantyDao: WarrantyDao
) {
    // Company Profile
    val companyProfile: Flow<CompanyProfile?> = companyProfileDao.getProfile()
    suspend fun getCompanyProfileSync(): CompanyProfile =
        companyProfileDao.getProfileSync() ?: CompanyProfile()
    suspend fun updateCompanyProfile(profile: CompanyProfile) =
        companyProfileDao.insertOrUpdate(profile)

    // Customers
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()
    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)
    fun getCustomerById(id: Long): Flow<Customer?> = customerDao.getCustomerById(id)
    suspend fun getCustomerByIdSync(id: Long): Customer? = customerDao.getCustomerByIdSync(id)
    fun getCustomerWithDetails(id: Long): Flow<CustomerWithDetails?> = customerDao.getCustomerWithDetails(id)
    suspend fun getCustomerWithDetailsSync(id: Long): CustomerWithDetails? = customerDao.getCustomerWithDetailsSync(id)
    suspend fun saveCustomer(customer: Customer): Long = customerDao.insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = customerDao.deleteCustomerCascade(customer.id)

    // Equipments
    val allEquipments: Flow<List<Equipment>> = equipmentDao.getAllEquipments()
    fun getEquipmentsByCustomer(customerId: Long): Flow<List<Equipment>> =
        equipmentDao.getEquipmentsByCustomer(customerId)
    fun getEquipmentById(id: Long): Flow<Equipment?> = equipmentDao.getEquipmentById(id)
    suspend fun getEquipmentByIdSync(id: Long): Equipment? = equipmentDao.getEquipmentByIdSync(id)
    suspend fun saveEquipment(equipment: Equipment): Long = equipmentDao.insertEquipment(equipment)
    suspend fun updateEquipment(equipment: Equipment) = equipmentDao.updateEquipment(equipment)
    suspend fun deleteEquipment(equipment: Equipment) = equipmentDao.deleteEquipment(equipment)

    // Work Orders
    val allOrdersWithDetails: Flow<List<WorkOrderWithDetails>> = workOrderDao.getAllOrdersWithDetails()
    fun getOrdersByStatus(status: OrderStatus): Flow<List<WorkOrderWithDetails>> =
        workOrderDao.getOrdersByStatus(status)
    fun getOrderWithDetailsById(id: Long): Flow<WorkOrderWithDetails?> =
        workOrderDao.getOrderWithDetailsById(id)
    suspend fun getOrderWithDetailsByIdSync(id: Long): WorkOrderWithDetails? =
        workOrderDao.getOrderWithDetailsByIdSync(id)
    suspend fun getOrderByIdSync(id: Long): WorkOrder? = workOrderDao.getOrderByIdSync(id)

    // Dashboard metrics
    fun getOrderCountByStatus(status: OrderStatus): Flow<Int> = workOrderDao.getCountByStatus(status)
    fun getTotalOrdersCount(): Flow<Int> = workOrderDao.getTotalOrdersCount()
    fun getTotalRevenue(): Flow<Double?> = workOrderDao.getTotalRevenue()
    fun getTotalPendingBalance(): Flow<Double?> = workOrderDao.getTotalPendingBalance()
    fun getActiveWarrantiesCount(): Flow<Int> = warrantyDao.getActiveWarrantiesCount()
    fun getLowStockCount(): Flow<Int> = inventoryDao.getLowStockCount()

    suspend fun saveWorkOrder(order: WorkOrder, notes: String = ""): Long {
        val total = (order.laborPrice + order.partsPrice) - order.discountAmount
        val balance = (total - order.downPaymentAmount).coerceAtLeast(0.0)

        val finalOrderNumber = if (order.orderNumber <= 0) {
            val maxNum = workOrderDao.getMaxOrderNumber() ?: 1000L
            maxNum + 1
        } else {
            order.orderNumber
        }

        val updatedOrder = order.copy(
            orderNumber = finalOrderNumber,
            totalAmount = total,
            remainingBalance = balance
        )

        val id = workOrderDao.insertOrder(updatedOrder)
        val assignedId = if (order.id == 0L) id else order.id

        // Record initial status history
        statusHistoryDao.insertHistory(
            StatusHistoryEntry(
                workOrderId = assignedId,
                previousStatus = null,
                newStatus = order.status,
                notes = if (notes.isNotEmpty()) notes else "Ordem criada/atualizada"
            )
        )

        // If down payment provided on a new order, record financial entry
        if (order.id == 0L && order.downPaymentAmount > 0) {
            financialDao.insertTransaction(
                FinancialTransaction(
                    type = TransactionType.INCOME,
                    category = "Sinal de OS",
                    description = "Sinal da OS #${finalOrderNumber}",
                    amount = order.downPaymentAmount,
                    workOrderId = assignedId
                )
            )
        }

        return assignedId
    }

    suspend fun updateOrderStatus(
        workOrderId: Long,
        newStatus: OrderStatus,
        notes: String = "",
        technician: String = "Técnico"
    ) {
        val existing = workOrderDao.getOrderByIdSync(workOrderId) ?: return
        if (existing.status == newStatus) return

        val previous = existing.status
        val isNowCompleted = newStatus == OrderStatus.COMPLETED
        val isNowDelivered = newStatus == OrderStatus.DELIVERED

        val completedTime = if (isNowCompleted && existing.completedAt == null) {
            System.currentTimeMillis()
        } else existing.completedAt

        val deliveredTime = if (isNowDelivered && existing.deliveredAt == null) {
            System.currentTimeMillis()
        } else existing.deliveredAt

        val updated = existing.copy(
            status = newStatus,
            completedAt = completedTime,
            deliveredAt = deliveredTime
        )

        workOrderDao.updateOrder(updated)

        // Log history entry
        statusHistoryDao.insertHistory(
            StatusHistoryEntry(
                workOrderId = workOrderId,
                previousStatus = previous,
                newStatus = newStatus,
                notes = notes,
                updatedBy = technician
            )
        )

        // If delivered, automatically issue standard statutory CDC 90-day warranty
        if (isNowDelivered) {
            val now = System.currentTimeMillis()
            val cdcNinetyDays = now + (90L * 24 * 60 * 60 * 1000L)
            warrantyDao.insertWarranty(
                Warranty(
                    workOrderId = workOrderId,
                    customerId = existing.customerId,
                    equipmentId = existing.equipmentId,
                    type = WarrantyType.LEGAL_CDC,
                    startDate = now,
                    endDate = cdcNinetyDays,
                    daysValid = 90,
                    coveredParts = if (existing.partsDescription.isNotBlank()) existing.partsDescription else "Peças substituídas",
                    coveredServices = if (existing.servicesDescription.isNotBlank()) existing.servicesDescription else "Serviço executado",
                    termsText = "Garantia legal de 90 dias conforme Art. 26 do Código de Defesa do Consumidor (Lei 8.078/90) sobre mão de obra e peças substituídas."
                )
            )

            // If remaining balance exists, record receipt upon delivery
            if (existing.remainingBalance > 0) {
                financialDao.insertTransaction(
                    FinancialTransaction(
                        type = TransactionType.INCOME,
                        category = "Quitação de OS",
                        description = "Pagamento final OS #${existing.orderNumber}",
                        amount = existing.remainingBalance,
                        workOrderId = workOrderId
                    )
                )
                workOrderDao.updateOrder(updated.copy(remainingBalance = 0.0))
            }
        }
    }

    suspend fun duplicateOrder(sourceOrderId: Long): Long? {
        val original = workOrderDao.getOrderByIdSync(sourceOrderId) ?: return null
        val nextNumber = (workOrderDao.getMaxOrderNumber() ?: 1000L) + 1
        val duplicated = original.copy(
            id = 0,
            orderNumber = nextNumber,
            openedAt = System.currentTimeMillis(),
            completedAt = null,
            deliveredAt = null,
            status = OrderStatus.OPEN,
            downPaymentAmount = 0.0,
            remainingBalance = original.totalAmount,
            internalNotes = "Duplicada a partir da OS #${original.orderNumber}"
        )
        return saveWorkOrder(duplicated, "Duplicada a partir da OS #${original.orderNumber}")
    }

    suspend fun deleteWorkOrder(order: WorkOrder) = workOrderDao.deleteOrder(order)

    // Status History
    fun getStatusHistory(workOrderId: Long): Flow<List<StatusHistoryEntry>> =
        statusHistoryDao.getHistoryForOrder(workOrderId)

    // Inventory
    val allInventoryParts: Flow<List<InventoryPart>> = inventoryDao.getAllParts()
    val lowStockParts: Flow<List<InventoryPart>> = inventoryDao.getLowStockParts()
    fun searchParts(query: String): Flow<List<InventoryPart>> = inventoryDao.searchParts(query)
    suspend fun saveInventoryPart(part: InventoryPart): Long = inventoryDao.insertPart(part)
    suspend fun updateInventoryPart(part: InventoryPart) = inventoryDao.updatePart(part)
    suspend fun deleteInventoryPart(part: InventoryPart) = inventoryDao.deletePart(part)
    suspend fun decrementStock(partId: Long, amount: Int): Boolean =
        inventoryDao.decrementStock(partId, amount) > 0

    // Financial
    val allTransactions: Flow<List<FinancialTransaction>> = financialDao.getAllTransactions()
    val totalIncome: Flow<Double?> = financialDao.getTotalIncome()
    val totalExpenses: Flow<Double?> = financialDao.getTotalExpenses()
    fun getTransactionsByOrder(workOrderId: Long): Flow<List<FinancialTransaction>> =
        financialDao.getTransactionsByOrder(workOrderId)
    suspend fun saveTransaction(transaction: FinancialTransaction): Long =
        financialDao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: FinancialTransaction) =
        financialDao.deleteTransaction(transaction)

    // Warranties
    val allWarranties: Flow<List<Warranty>> = warrantyDao.getAllWarranties()
    val activeWarranties: Flow<List<Warranty>> = warrantyDao.getActiveWarranties()
    fun getWarrantyByWorkOrder(workOrderId: Long): Flow<Warranty?> =
        warrantyDao.getWarrantyByWorkOrder(workOrderId)
    suspend fun saveWarranty(warranty: Warranty): Long = warrantyDao.insertWarranty(warranty)
    suspend fun updateWarranty(warranty: Warranty) = warrantyDao.updateWarranty(warranty)
    suspend fun deleteWarranty(warranty: Warranty) = warrantyDao.deleteWarranty(warranty)
}
