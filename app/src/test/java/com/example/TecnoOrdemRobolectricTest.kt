package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.TecnoOrdemDatabase
import com.example.data.model.CompanyProfile
import com.example.data.model.Customer
import com.example.data.model.Equipment
import com.example.data.model.EquipmentType
import com.example.data.model.FinancialTransaction
import com.example.data.model.InventoryPart
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionType
import com.example.data.model.WarrantyType
import com.example.data.model.WorkOrder
import com.example.data.repository.TecnoOrdemRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TecnoOrdemRobolectricTest {

    private lateinit var db: TecnoOrdemDatabase
    private lateinit var repository: TecnoOrdemRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TecnoOrdemDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        repository = TecnoOrdemRepository(
            companyProfileDao = db.companyProfileDao(),
            customerDao = db.customerDao(),
            equipmentDao = db.equipmentDao(),
            workOrderDao = db.workOrderDao(),
            statusHistoryDao = db.statusHistoryDao(),
            inventoryDao = db.inventoryDao(),
            financialDao = db.financialDao(),
            warrantyDao = db.warrantyDao()
        )
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testCustomerAndEquipmentCreation() = runBlocking {
        val customerId = repository.saveCustomer(
            Customer(
                name = "Juliana Ferreira",
                phone = "(11) 99887-6655",
                whatsapp = "(11) 99887-6655",
                documentNumber = "123.456.789-00"
            )
        )
        assertTrue(customerId > 0)

        val retrievedCust = repository.getCustomerByIdSync(customerId)
        assertNotNull(retrievedCust)
        assertEquals("Juliana Ferreira", retrievedCust?.name)

        val equipId = repository.saveEquipment(
            Equipment(
                customerId = customerId,
                type = EquipmentType.SMARTPHONE,
                brand = "Apple",
                model = "iPhone 14 Pro",
                serialNumberOrImei = "35987110992341",
                entryCondition = "Tela sem trincos, não liga"
            )
        )
        assertTrue(equipId > 0)

        val retrievedEquip = repository.getEquipmentByIdSync(equipId)
        assertNotNull(retrievedEquip)
        assertEquals("iPhone 14 Pro", retrievedEquip?.model)
    }

    @Test
    fun testWorkOrderFinancialCalculationsAndStatusTransitions() = runBlocking {
        val custId = repository.saveCustomer(
            Customer(name = "Roberto Carlos", phone = "(21) 98765-4321")
        )
        val equipId = repository.saveEquipment(
            Equipment(customerId = custId, brand = "Dell", model = "G15 5530")
        )

        // Create order with Labor = 200, Parts = 350, Discount = 50, DownPayment = 100
        // Expected total = (200 + 350) - 50 = 500
        // Expected remaining balance = 500 - 100 = 400
        val order = WorkOrder(
            orderNumber = 0,
            customerId = custId,
            equipmentId = equipId,
            laborPrice = 200.0,
            partsPrice = 350.0,
            discountAmount = 50.0,
            downPaymentAmount = 100.0,
            reportedDefect = "Troca de pasta térmica e teclado",
            status = OrderStatus.OPEN
        )

        val orderId = repository.saveWorkOrder(order)
        val saved = repository.getOrderByIdSync(orderId)

        assertNotNull(saved)
        assertEquals(500.0, saved!!.totalAmount, 0.01)
        assertEquals(400.0, saved.remainingBalance, 0.01)
        assertTrue(saved.orderNumber >= 1001)

        // Status transition to IN_REPAIR
        repository.updateOrderStatus(orderId, OrderStatus.IN_REPAIR, "Iniciado serviço na bancada")
        val inRepair = repository.getOrderByIdSync(orderId)
        assertEquals(OrderStatus.IN_REPAIR, inRepair?.status)

        // Status transition to DELIVERED -> triggers CDC 90-day warranty
        repository.updateOrderStatus(orderId, OrderStatus.DELIVERED, "Entregue e testado com o cliente")
        val delivered = repository.getOrderByIdSync(orderId)
        assertEquals(OrderStatus.DELIVERED, delivered?.status)

        // Verify warranty was automatically issued
        val warranties = repository.allWarranties.first()
        assertTrue(warranties.isNotEmpty())
        val w = warranties.first()
        assertEquals(WarrantyType.LEGAL_CDC, w.type)
        assertEquals(90, w.daysValid)
    }

    @Test
    fun testInventoryStockManagement() = runBlocking {
        val partId = repository.saveInventoryPart(
            InventoryPart(
                skuCode = "BAT-IP13-PRM",
                name = "Bateria iPhone 13 Premium",
                quantity = 5,
                minQuantityAlert = 2,
                costPrice = 80.0,
                salePrice = 180.0
            )
        )
        assertTrue(partId > 0)

        // Decrement 2 pieces
        val success = repository.decrementStock(partId, 2)
        assertTrue(success)

        val updated = db.inventoryDao().getPartByIdSync(partId)
        assertEquals(3, updated?.quantity)
    }

    @Test
    fun testCompanyProfilePersistence() = runBlocking {
        val customProfile = CompanyProfile(
            tradeName = "TecnoMaster Reparos",
            documentNumber = "99.888.777/0001-11",
            phone = "(11) 3333-4444",
            pixKey = "financeiro@tecnomaster.com"
        )
        repository.updateCompanyProfile(customProfile)

        val current = repository.getCompanyProfileSync()
        assertEquals("TecnoMaster Reparos", current.tradeName)
        assertEquals("financeiro@tecnomaster.com", current.pixKey)
    }

    @Test
    fun testDataStorePreferencesPersistence() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataStore = com.example.data.preferences.AppPreferencesDataStore(context)

        dataStore.setAppLanguage("es")
        dataStore.setCompanyLogoUri("file:///data/user/0/logo_test.png")
        dataStore.setHasSelectedInitialLanguage(true)

        val prefs = dataStore.userPreferencesFlow.first()
        assertEquals("es", prefs.appLanguage)
        assertEquals("file:///data/user/0/logo_test.png", prefs.companyLogoUri)
        assertTrue(prefs.hasSelectedInitialLanguage)

        // Clear logo
        dataStore.clearCompanyLogo()
        val updatedPrefs = dataStore.userPreferencesFlow.first()
        assertEquals("", updatedPrefs.companyLogoUri)
    }

    @Test
    fun testWorkOrdersRoomRelationsAndCascade() = runBlocking {
        // 1. Create Customer
        val customerId = repository.saveCustomer(
            Customer(
                name = "Fernando Mendes",
                documentNumber = "789.456.123-00",
                phone = "(11) 91234-5678",
                city = "Campinas",
                state = "SP"
            )
        )
        assertTrue(customerId > 0)

        // 2. Create Equipment linked to Customer via foreignKey
        val equipId = repository.saveEquipment(
            Equipment(
                customerId = customerId,
                type = EquipmentType.NOTEBOOK,
                brand = "Lenovo",
                model = "ThinkPad T14",
                serialNumberOrImei = "PF-998822"
            )
        )
        assertTrue(equipId > 0)

        // 3. Create WorkOrder linked to Customer and Equipment
        val order = WorkOrder(
            orderNumber = 2001,
            customerId = customerId,
            equipmentId = equipId,
            status = OrderStatus.OPEN,
            reportedDefect = "Tela piscando ao mover tampa",
            laborPrice = 150.0,
            partsPrice = 300.0,
            totalAmount = 450.0,
            remainingBalance = 450.0
        )
        val orderId = repository.saveWorkOrder(order)
        assertTrue(orderId > 0)

        // 4. Assert WorkOrderWithDetails relation mapping
        val details = repository.getOrderWithDetailsByIdSync(orderId)
        assertNotNull(details)
        assertEquals("Fernando Mendes", details?.customer?.name)
        assertEquals("ThinkPad T14", details?.equipment?.model)
        assertEquals(2001L, details?.workOrder?.orderNumber)

        // 5. Assert CustomerWithDetails relation mapping
        val customerDetails = repository.getCustomerWithDetailsSync(customerId)
        assertNotNull(customerDetails)
        assertEquals(1, customerDetails?.equipments?.size)
        assertEquals("Lenovo", customerDetails?.equipments?.first()?.brand)
        assertEquals(1, customerDetails?.workOrders?.size)
        assertEquals(2001L, customerDetails?.workOrders?.first()?.orderNumber)

        // 6. Test Cascading Deletion: deleting customer cleans up linked equipment and work orders
        val customer = repository.getCustomerByIdSync(customerId)
        assertNotNull(customer)
        repository.deleteCustomer(customer!!)

        // Verify entities were cascaded
        val orderAfterDelete = repository.getOrderByIdSync(orderId)
        assertEquals(null, orderAfterDelete)
        val equipAfterDelete = repository.getEquipmentByIdSync(equipId)
        assertEquals(null, equipAfterDelete)
        val customerAfterDelete = repository.getCustomerByIdSync(customerId)
        assertEquals(null, customerAfterDelete)
    }
}
