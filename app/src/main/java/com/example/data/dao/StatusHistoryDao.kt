package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.model.StatusHistoryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusHistoryDao {
    @Query("SELECT * FROM status_history WHERE workOrderId = :workOrderId ORDER BY timestamp DESC")
    fun getHistoryForOrder(workOrderId: Long): Flow<List<StatusHistoryEntry>>

    @Insert
    suspend fun insertHistory(entry: StatusHistoryEntry): Long
}
