package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "work_orders",
    foreignKeys = [
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
        Index(value = ["customerId"]),
        Index(value = ["equipmentId"]),
        Index(value = ["orderNumber"], unique = true),
        Index(value = ["status"])
    ]
)
data class WorkOrder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: Long,
    val customerId: Long,
    val equipmentId: Long,
    val openedAt: Long = System.currentTimeMillis(),
    val estimatedDeliveryAt: Long = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000L),
    val completedAt: Long? = null,
    val deliveredAt: Long? = null,
    val status: OrderStatus = OrderStatus.OPEN,
    val reportedDefect: String = "",
    val technicalDiagnosis: String = "",
    val servicesDescription: String = "",
    val partsDescription: String = "",
    val laborPrice: Double = 0.0,
    val partsPrice: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val downPaymentAmount: Double = 0.0,
    val remainingBalance: Double = 0.0,
    val assignedTechnician: String = "Técnico Responsável",
    val internalNotes: String = "",
    val termsAccepted: Boolean = true,
    val approvalMethod: String = "Pendente",
    val approvalTimestamp: Long? = null
)
