package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Equipment
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM equipments ORDER BY createdAt DESC")
    fun getAllEquipments(): Flow<List<Equipment>>

    @Query("SELECT * FROM equipments WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getEquipmentsByCustomer(customerId: Long): Flow<List<Equipment>>

    @Query("SELECT * FROM equipments WHERE id = :id LIMIT 1")
    fun getEquipmentById(id: Long): Flow<Equipment?>

    @Query("SELECT * FROM equipments WHERE id = :id LIMIT 1")
    suspend fun getEquipmentByIdSync(id: Long): Equipment?

    @Query("""
        SELECT * FROM equipments 
        WHERE model LIKE '%' || :query || '%' 
           OR brand LIKE '%' || :query || '%' 
           OR serialNumberOrImei LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchEquipments(query: String): Flow<List<Equipment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: Equipment): Long

    @Update
    suspend fun updateEquipment(equipment: Equipment)

    @Delete
    suspend fun deleteEquipment(equipment: Equipment)

    @Query("SELECT COUNT(*) FROM equipments")
    fun getEquipmentCount(): Flow<Int>
}
