package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.InventoryPart
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_parts ORDER BY name ASC")
    fun getAllParts(): Flow<List<InventoryPart>>

    @Query("SELECT * FROM inventory_parts WHERE id = :id LIMIT 1")
    fun getPartById(id: Long): Flow<InventoryPart?>

    @Query("SELECT * FROM inventory_parts WHERE id = :id LIMIT 1")
    suspend fun getPartByIdSync(id: Long): InventoryPart?

    @Query("SELECT * FROM inventory_parts WHERE quantity <= minQuantityAlert ORDER BY quantity ASC")
    fun getLowStockParts(): Flow<List<InventoryPart>>

    @Query("SELECT COUNT(*) FROM inventory_parts WHERE quantity <= minQuantityAlert")
    fun getLowStockCount(): Flow<Int>

    @Query("""
        SELECT * FROM inventory_parts 
        WHERE name LIKE '%' || :query || '%' 
           OR skuCode LIKE '%' || :query || '%' 
           OR barcode LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchParts(query: String): Flow<List<InventoryPart>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPart(part: InventoryPart): Long

    @Update
    suspend fun updatePart(part: InventoryPart)

    @Delete
    suspend fun deletePart(part: InventoryPart)

    @Query("UPDATE inventory_parts SET quantity = quantity - :amount WHERE id = :id AND quantity >= :amount")
    suspend fun decrementStock(id: Long, amount: Int): Int

    @Query("UPDATE inventory_parts SET quantity = quantity + :amount WHERE id = :id")
    suspend fun incrementStock(id: Long, amount: Int)
}
