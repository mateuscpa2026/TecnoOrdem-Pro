package com.example.domain.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    private val ptBrLocale = Locale("pt", "BR")

    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(ptBrLocale)
        return format.format(amount)
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", ptBrLocale)
        return sdf.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", ptBrLocale)
        return sdf.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM", ptBrLocale)
        return sdf.format(Date(timestamp))
    }
}
