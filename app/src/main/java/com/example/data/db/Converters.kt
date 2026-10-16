package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.EquipmentType
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.TransactionType
import com.example.data.model.WarrantyType

class Converters {
    @TypeConverter
    fun fromOrderStatus(value: OrderStatus?): String? = value?.name

    @TypeConverter
    fun toOrderStatus(value: String?): OrderStatus? =
        value?.let { runCatching { OrderStatus.valueOf(it) }.getOrDefault(OrderStatus.OPEN) }

    @TypeConverter
    fun fromEquipmentType(value: EquipmentType?): String? = value?.name

    @TypeConverter
    fun toEquipmentType(value: String?): EquipmentType? =
        value?.let { runCatching { EquipmentType.valueOf(it) }.getOrDefault(EquipmentType.SMARTPHONE) }

    @TypeConverter
    fun fromWarrantyType(value: WarrantyType?): String? = value?.name

    @TypeConverter
    fun toWarrantyType(value: String?): WarrantyType? =
        value?.let { runCatching { WarrantyType.valueOf(it) }.getOrDefault(WarrantyType.LEGAL_CDC) }

    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? = value?.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? =
        value?.let { runCatching { TransactionType.valueOf(it) }.getOrDefault(TransactionType.INCOME) }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod?): String? = value?.name

    @TypeConverter
    fun toPaymentMethod(value: String?): PaymentMethod? =
        value?.let { runCatching { PaymentMethod.valueOf(it) }.getOrDefault(PaymentMethod.PIX) }
}
