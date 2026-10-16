package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory_parts",
    indices = [
        Index(value = ["skuCode"], unique = true),
        Index(value = ["barcode"])
    ]
)
data class InventoryPart(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val skuCode: String,
    val barcode: String = "",
    val name: String,
    val category: String = "Peças Gerais",
    val supplier: String = "",
    val quantity: Int = 0,
    val minQuantityAlert: Int = 2,
    val costPrice: Double = 0.0,
    val salePrice: Double = 0.0,
    val shelfLocation: String = "Prateleira A1",
    val lastUpdated: Long = System.currentTimeMillis()
)
