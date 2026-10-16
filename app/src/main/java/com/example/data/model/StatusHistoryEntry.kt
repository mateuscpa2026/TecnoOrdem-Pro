package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "status_history",
    foreignKeys = [
        ForeignKey(
            entity = WorkOrder::class,
            parentColumns = ["id"],
            childColumns = ["workOrderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["workOrderId"])]
)
data class StatusHistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workOrderId: Long,
    val previousStatus: OrderStatus?,
    val newStatus: OrderStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val updatedBy: String = "Técnico"
)
