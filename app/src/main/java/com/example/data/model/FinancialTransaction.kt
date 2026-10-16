package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "financial_transactions",
    foreignKeys = [
        ForeignKey(
            entity = WorkOrder::class,
            parentColumns = ["id"],
            childColumns = ["workOrderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["workOrderId"]),
        Index(value = ["date"]),
        Index(value = ["type"])
    ]
)
data class FinancialTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType = TransactionType.INCOME,
    val category: String = "Serviço Técnico",
    val description: String,
    val amount: Double,
    val paymentMethod: PaymentMethod = PaymentMethod.PIX,
    val date: Long = System.currentTimeMillis(),
    val workOrderId: Long? = null,
    val isPaid: Boolean = true,
    val notes: String = ""
)
