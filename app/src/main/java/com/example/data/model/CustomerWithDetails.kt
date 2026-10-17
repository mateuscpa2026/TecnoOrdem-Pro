package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class CustomerWithDetails(
    @Embedded val customer: Customer,
    @Relation(
        parentColumn = "id",
        entityColumn = "customerId"
    )
    val equipments: List<Equipment> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "customerId"
    )
    val workOrders: List<WorkOrder> = emptyList()
)
