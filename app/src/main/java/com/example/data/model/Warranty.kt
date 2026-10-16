package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "warranties",
    foreignKeys = [
        ForeignKey(
            entity = WorkOrder::class,
            parentColumns = ["id"],
            childColumns = ["workOrderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Equipment::class,
            parentColumns = ["id"],
            childColumns = ["equipmentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["workOrderId"]),
        Index(value = ["customerId"]),
        Index(value = ["equipmentId"]),
        Index(value = ["endDate"])
    ]
)
data class Warranty(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workOrderId: Long,
    val customerId: Long,
    val equipmentId: Long,
    val type: WarrantyType = WarrantyType.LEGAL_CDC,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000L),
    val daysValid: Int = 90,
    val coveredParts: String = "Peças trocadas na OS",
    val coveredServices: String = "Mão de obra executada na OS",
    val termsText: String = "Garantia legal de 90 dias conforme Art. 26 da Lei nº 8.078/1990 (Código de Defesa do Consumidor).",
    val isActive: Boolean = true,
    val claimCount: Int = 0
)
