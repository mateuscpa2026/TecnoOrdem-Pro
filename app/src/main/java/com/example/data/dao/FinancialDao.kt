package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FinancialTransaction
import com.example.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialDao {
    @Query("SELECT * FROM financial_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<FinancialTransaction>>

    @Query("SELECT * FROM financial_transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<FinancialTransaction>>

    @Query("SELECT * FROM financial_transactions WHERE workOrderId = :workOrderId ORDER BY date DESC")
    fun getTransactionsByOrder(workOrderId: Long): Flow<List<FinancialTransaction>>

    @Query("SELECT SUM(amount) FROM financial_transactions WHERE type = 'INCOME' AND isPaid = 1")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM financial_transactions WHERE type = 'EXPENSE' AND isPaid = 1")
    fun getTotalExpenses(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinancialTransaction): Long

    @Update
    suspend fun updateTransaction(transaction: FinancialTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: FinancialTransaction)
}
