package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isCorporate: Boolean = false,
    val documentNumber: String = "", // CPF or CNPJ
    val phone: String = "",
    val whatsapp: String = "",
    val email: String = "",
    val address: String = "",
    val neighborhood: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val totalOrdersCount: Int = 0,
    val totalSpent: Double = 0.0
)
