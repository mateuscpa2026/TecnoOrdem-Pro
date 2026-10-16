package com.example.data.model

enum class OrderStatus(val titlePt: String, val titleEn: String) {
    OPEN("Aberta", "Open"),
    SCREENING("Em triagem", "In Triage"),
    DIAGNOSIS("Em diagnóstico", "In Diagnosis"),
    AWAITING_APPROVAL("Aguardando aprovação", "Awaiting Approval"),
    AWAITING_PARTS("Aguardando peças", "Awaiting Parts"),
    IN_REPAIR("Em reparo", "In Repair"),
    TESTING("Em testes", "Testing"),
    COMPLETED("Concluída", "Completed"),
    READY_FOR_PICKUP("Pronta para retirada", "Ready for Pickup"),
    DELIVERED("Entregue", "Delivered"),
    CANCELLED("Cancelada", "Cancelled")
}

enum class EquipmentType(val titlePt: String) {
    SMARTPHONE("Smartphone / Celular"),
    NOTEBOOK("Notebook / Laptop"),
    COMPUTER("Computador / Desktop"),
    TABLET("Tablet"),
    PRINTER("Impressora"),
    TELEVISION("Televisor / Smart TV"),
    CONSOLE("Videogame / Console"),
    APPLIANCE("Eletrodoméstico"),
    OTHER("Outro Equipamento")
}

enum class WarrantyType(val titlePt: String, val defaultDays: Int) {
    LEGAL_CDC("Garantia Legal (Art. 26 CDC)", 90),
    CONTRACTUAL("Garantia Contratual da Assistência", 90),
    PARTS_ONLY("Garantia de Peças Substituídas", 90),
    LABOR_ONLY("Garantia de Mão de Obra / Serviço", 90)
}

enum class TransactionType {
    INCOME, EXPENSE
}

enum class PaymentMethod(val titlePt: String) {
    PIX("Pix"),
    CASH("Dinheiro"),
    DEBIT_CARD("Cartão de Débito"),
    CREDIT_CARD("Cartão de Crédito"),
    BANK_TRANSFER("Transferência"),
    OTHER("Outro")
}
