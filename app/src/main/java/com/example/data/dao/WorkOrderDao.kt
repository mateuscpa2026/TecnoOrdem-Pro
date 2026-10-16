package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.OrderStatus
import com.example.data.model.WorkOrder
import com.example.data.model.WorkOrderWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkOrderDao {
    @Transaction
    @Query("SELECT * FROM work_orders ORDER BY openedAt DESC")
    fun getAllOrdersWithDetails(): Flow<List<WorkOrderWithDetails>>

    @Transaction
    @Query("SELECT * FROM work_orders WHERE id = :id LIMIT 1")
    fun getOrderWithDetailsById(id: Long): Flow<WorkOrderWithDetails?>

    @Transaction
    @Query("SELECT * FROM work_orders WHERE id = :id LIMIT 1")
    suspend fun getOrderWithDetailsByIdSync(id: Long): WorkOrderWithDetails?

    @Query("SELECT * FROM work_orders WHERE id = :id LIMIT 1")
    suspend fun getOrderByIdSync(id: Long): WorkOrder?

    @Transaction
    @Query("SELECT * FROM work_orders WHERE status = :status ORDER BY openedAt DESC")
    fun getOrdersByStatus(status: OrderStatus): Flow<List<WorkOrderWithDetails>>

    @Transaction
    @Query("SELECT * FROM work_orders WHERE customerId = :customerId ORDER BY openedAt DESC")
    fun getOrdersByCustomer(customerId: Long): Flow<List<WorkOrderWithDetails>>

    @Query("SELECT MAX(orderNumber) FROM work_orders")
    suspend fun getMaxOrderNumber(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: WorkOrder): Long

    @Update
    suspend fun updateOrder(order: WorkOrder)

    @Delete
    suspend fun deleteOrder(order: WorkOrder)

    @Query("SELECT COUNT(*) FROM work_orders WHERE status = :status")
    fun getCountByStatus(status: OrderStatus): Flow<Int>

    @Query("SELECT COUNT(*) FROM work_orders")
    fun getTotalOrdersCount(): Flow<Int>

    @Query("SELECT SUM(totalAmount) FROM work_orders WHERE status IN ('COMPLETED', 'READY_FOR_PICKUP', 'DELIVERED')")
    fun getTotalRevenue(): Flow<Double?>

    @Query("SELECT SUM(remainingBalance) FROM work_orders WHERE remainingBalance > 0 AND status != 'CANCELLED'")
    fun getTotalPendingBalance(): Flow<Double?>
}
