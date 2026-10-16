package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "equipments",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class Equipment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val type: EquipmentType = EquipmentType.SMARTPHONE,
    val brand: String,
    val model: String,
    val serialNumberOrImei: String = "",
    val color: String = "",
    val entryCondition: String = "Sinais de uso normais",
    val preExistingDamages: String = "Nenhuma avaria além do defeito relatado",
    val accessoriesReceived: String = "Somente o aparelho",
    val securityUnlockInfo: String = "", // Protected / optional note
    val createdAt: Long = System.currentTimeMillis()
)
