package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company_profile")
data class CompanyProfile(
    @PrimaryKey val id: Int = 1,
    val tradeName: String = "TecnoOrdem Pro",
    val corporateName: String = "",
    val documentNumber: String = "", // CNPJ ou CPF
    val phone: String = "",
    val whatsapp: String = "",
    val email: String = "",
    val website: String = "",
    val address: String = "",
    val neighborhood: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val technicalManager: String = "",
    val businessHours: String = "",
    val pixKey: String = "",
    val paymentInstructions: String = "",
    val standardTerms: String = "O cliente declara estar ciente de que o prazo para diagnóstico inicial é de 24 a 48 horas úteis. Equipamentos prontos devem ser retirados dentro do prazo acordado.",
    val warrantyTerms: String = "Nos termos do Art. 26 da Lei nº 8.078/1990 (Código de Defesa do Consumidor), é assegurada garantia legal de 90 (noventa) dias sobre o serviço executado e peças substituídas pela assistência técnica.",
    val appThemeMode: String = "SYSTEM", // SYSTEM, LIGHT, DARK
    val appLanguage: String = "pt-BR", // pt-BR, en, es, fr, da, it, ja, zh-CN, ko, ru, pt-PT
    val currencySymbol: String = "R$",
    val logoUri: String = "" // Custom shop logo URI or empty for default TecnoOrdem Pro logo
)
