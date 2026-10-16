package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CompanyProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyProfileDao {
    @Query("SELECT * FROM company_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<CompanyProfile?>

    @Query("SELECT * FROM company_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileSync(): CompanyProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: CompanyProfile)

    @Update
    suspend fun update(profile: CompanyProfile)
}
