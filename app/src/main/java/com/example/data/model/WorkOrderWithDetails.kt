package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class WorkOrderWithDetails(
    @Embedded val workOrder: WorkOrder,
    @Relation(
        parentColumn = "customerId",
        entityColumn = "id"
    )
    val customer: Customer?,
    @Relation(
        parentColumn = "equipmentId",
        entityColumn = "id"
    )
    val equipment: Equipment?
)
