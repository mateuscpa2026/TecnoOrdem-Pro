package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Warranty
import kotlinx.coroutines.flow.Flow

@Dao
interface WarrantyDao {
    @Query("SELECT * FROM warranties ORDER BY endDate ASC")
    fun getAllWarranties(): Flow<List<Warranty>>

    @Query("SELECT * FROM warranties WHERE workOrderId = :workOrderId LIMIT 1")
    fun getWarrantyByWorkOrder(workOrderId: Long): Flow<Warranty?>

    @Query("SELECT * FROM warranties WHERE isActive = 1 AND endDate >= :currentTime ORDER BY endDate ASC")
    fun getActiveWarranties(currentTime: Long = System.currentTimeMillis()): Flow<List<Warranty>>

    @Query("SELECT * FROM warranties WHERE isActive = 1 AND endDate BETWEEN :now AND :threshold")
    fun getExpiringWarranties(now: Long, threshold: Long): Flow<List<Warranty>>

    @Query("SELECT COUNT(*) FROM warranties WHERE isActive = 1 AND endDate >= :now")
    fun getActiveWarrantiesCount(now: Long = System.currentTimeMillis()): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarranty(warranty: Warranty): Long

    @Update
    suspend fun updateWarranty(warranty: Warranty)

    @Delete
    suspend fun deleteWarranty(warranty: Warranty)
}
