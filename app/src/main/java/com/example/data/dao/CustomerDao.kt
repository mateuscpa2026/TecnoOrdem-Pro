package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Customer
import com.example.data.model.CustomerWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    fun getCustomerById(id: Long): Flow<Customer?>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerByIdSync(id: Long): Customer?

    @androidx.room.Transaction
    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    fun getCustomerWithDetails(id: Long): Flow<CustomerWithDetails?>

    @androidx.room.Transaction
    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerWithDetailsSync(id: Long): CustomerWithDetails?

    @Query("""
        SELECT * FROM customers 
        WHERE name LIKE '%' || :query || '%' 
           OR documentNumber LIKE '%' || :query || '%' 
           OR phone LIKE '%' || :query || '%' 
           OR whatsapp LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("DELETE FROM status_history WHERE workOrderId IN (SELECT id FROM work_orders WHERE customerId = :customerId)")
    suspend fun deleteStatusHistoryForCustomer(customerId: Long)

    @Query("DELETE FROM warranties WHERE customerId = :customerId")
    suspend fun deleteWarrantiesForCustomer(customerId: Long)

    @Query("DELETE FROM financial_transactions WHERE workOrderId IN (SELECT id FROM work_orders WHERE customerId = :customerId)")
    suspend fun deleteTransactionsForCustomer(customerId: Long)

    @Query("DELETE FROM work_orders WHERE customerId = :customerId")
    suspend fun deleteWorkOrdersForCustomer(customerId: Long)

    @Query("DELETE FROM equipments WHERE customerId = :customerId")
    suspend fun deleteEquipmentsForCustomer(customerId: Long)

    @Query("DELETE FROM customers WHERE id = :customerId")
    suspend fun deleteCustomerById(customerId: Long)

    @androidx.room.Transaction
    suspend fun deleteCustomerCascade(customerId: Long) {
        deleteStatusHistoryForCustomer(customerId)
        deleteWarrantiesForCustomer(customerId)
        deleteTransactionsForCustomer(customerId)
        deleteWorkOrdersForCustomer(customerId)
        deleteEquipmentsForCustomer(customerId)
        deleteCustomerById(customerId)
    }

    @Query("SELECT COUNT(*) FROM customers")
    fun getCustomerCount(): Flow<Int>
}
