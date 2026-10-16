package com.example.domain.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EquipmentType
import com.example.data.model.OrderStatus

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val flagEmoji: String
) {
    PT_BR("pt-BR", "Português (Brasil)", "🇧🇷"),
    EN("en", "English", "🇺🇸"),
    ES("es", "Español", "🇪🇸"),
    FR("fr", "Français", "🇫🇷"),
    DA("da", "Dansk (Dinamarquês)", "🇩🇰"),
    IT("it", "Italiano", "🇮🇹"),
    JA("ja", "日本語 (Japonês)", "🇯🇵"),
    ZH("zh-CN", "中文 (Chinês)", "🇨🇳"),
    KO("ko", "한국어 (Coreano)", "🇰🇷"),
    RU("ru", "Русский (Russo)", "🇷🇺"),
    PT_PT("pt-PT", "Português (Portugal)", "🇵🇹");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: PT_BR
        }
    }
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.PT_BR }

@Composable
fun appString(key: String): String {
    val lang = LocalAppLanguage.current
    return AppStrings.get(key, lang)
}

object AppStrings {
    fun get(key: String, language: AppLanguage): String {
        return when (language) {
            AppLanguage.PT_BR -> ptBrStrings[key] ?: key
            AppLanguage.EN -> enStrings[key] ?: ptBrStrings[key] ?: key
            AppLanguage.ES -> esStrings[key] ?: ptBrStrings[key] ?: key
            AppLanguage.FR -> frStrings[key] ?: ptBrStrings[key] ?: key
            AppLanguage.DA -> daStrings[key] ?: ptBrStrings[key] ?: key
            AppLanguage.IT -> itStrings[key] ?: ptBrStrings[key] ?: key
            AppLanguage.JA -> jaStrings[key] ?: ptBrStrings[key] ?: key
            AppLanguage.ZH -> zhStrings[key] ?: ptBrStrings[key] ?: key
            AppLanguage.KO -> koStrings[key] ?: ptBrStrings[key] ?: key
            AppLanguage.RU -> ruStrings[key] ?: ptBrStrings[key] ?: key
            AppLanguage.PT_PT -> ptPtStrings[key] ?: ptBrStrings[key] ?: key
        }
    }

    fun getOrderStatus(status: OrderStatus, language: AppLanguage): String {
        return when (language) {
            AppLanguage.PT_BR -> when (status) {
                OrderStatus.OPEN -> "Aberta"
                OrderStatus.SCREENING -> "Em triagem"
                OrderStatus.DIAGNOSIS -> "Em diagnóstico"
                OrderStatus.AWAITING_APPROVAL -> "Aguardando aprovação"
                OrderStatus.AWAITING_PARTS -> "Aguardando peças"
                OrderStatus.IN_REPAIR -> "Em reparo"
                OrderStatus.TESTING -> "Em testes"
                OrderStatus.COMPLETED -> "Concluída"
                OrderStatus.READY_FOR_PICKUP -> "Pronta para retirada"
                OrderStatus.DELIVERED -> "Entregue"
                OrderStatus.CANCELLED -> "Cancelada"
            }
            AppLanguage.EN -> when (status) {
                OrderStatus.OPEN -> "Open"
                OrderStatus.SCREENING -> "Screening"
                OrderStatus.DIAGNOSIS -> "Diagnosis"
                OrderStatus.AWAITING_APPROVAL -> "Awaiting Approval"
                OrderStatus.AWAITING_PARTS -> "Awaiting Parts"
                OrderStatus.IN_REPAIR -> "In Repair"
                OrderStatus.TESTING -> "Testing"
                OrderStatus.COMPLETED -> "Completed"
                OrderStatus.READY_FOR_PICKUP -> "Ready for Pickup"
                OrderStatus.DELIVERED -> "Delivered"
                OrderStatus.CANCELLED -> "Cancelled"
            }
            AppLanguage.ES -> when (status) {
                OrderStatus.OPEN -> "Abierta"
                OrderStatus.SCREENING -> "En triaje"
                OrderStatus.DIAGNOSIS -> "En diagnóstico"
                OrderStatus.AWAITING_APPROVAL -> "Esperando aprobación"
                OrderStatus.AWAITING_PARTS -> "Esperando repuestos"
                OrderStatus.IN_REPAIR -> "En reparación"
                OrderStatus.TESTING -> "En pruebas"
                OrderStatus.COMPLETED -> "Completada"
                OrderStatus.READY_FOR_PICKUP -> "Lista para retirar"
                OrderStatus.DELIVERED -> "Entregada"
                OrderStatus.CANCELLED -> "Cancelada"
            }
            AppLanguage.FR -> when (status) {
                OrderStatus.OPEN -> "Ouvert"
                OrderStatus.SCREENING -> "En triage"
                OrderStatus.DIAGNOSIS -> "En diagnostic"
                OrderStatus.AWAITING_APPROVAL -> "En attente d'accord"
                OrderStatus.AWAITING_PARTS -> "En attente de pièces"
                OrderStatus.IN_REPAIR -> "En réparation"
                OrderStatus.TESTING -> "En test"
                OrderStatus.COMPLETED -> "Terminé"
                OrderStatus.READY_FOR_PICKUP -> "Prêt au retrait"
                OrderStatus.DELIVERED -> "Livré"
                OrderStatus.CANCELLED -> "Annulé"
            }
            AppLanguage.DA -> when (status) {
                OrderStatus.OPEN -> "Åben"
                OrderStatus.SCREENING -> "I visitation"
                OrderStatus.DIAGNOSIS -> "Under diagnose"
                OrderStatus.AWAITING_APPROVAL -> "Afventer godkendelse"
                OrderStatus.AWAITING_PARTS -> "Afventer reservedele"
                OrderStatus.IN_REPAIR -> "Under reparation"
                OrderStatus.TESTING -> "Under test"
                OrderStatus.COMPLETED -> "Fuldført"
                OrderStatus.READY_FOR_PICKUP -> "Klar til afhentning"
                OrderStatus.DELIVERED -> "Udleveret"
                OrderStatus.CANCELLED -> "Annulleret"
            }
            AppLanguage.IT -> when (status) {
                OrderStatus.OPEN -> "Aperta"
                OrderStatus.SCREENING -> "In accettazione"
                OrderStatus.DIAGNOSIS -> "In diagnosi"
                OrderStatus.AWAITING_APPROVAL -> "In attesa di approvazione"
                OrderStatus.AWAITING_PARTS -> "In attesa di ricambi"
                OrderStatus.IN_REPAIR -> "In riparazione"
                OrderStatus.TESTING -> "In collaudo"
                OrderStatus.COMPLETED -> "Completata"
                OrderStatus.READY_FOR_PICKUP -> "Pronta al ritiro"
                OrderStatus.DELIVERED -> "Consegnata"
                OrderStatus.CANCELLED -> "Annullata"
            }
            AppLanguage.JA -> when (status) {
                OrderStatus.OPEN -> "受付"
                OrderStatus.SCREENING -> "初期診断中"
                OrderStatus.DIAGNOSIS -> "診断中"
                OrderStatus.AWAITING_APPROVAL -> "承認待ち"
                OrderStatus.AWAITING_PARTS -> "部品手配中"
                OrderStatus.IN_REPAIR -> "修理中"
                OrderStatus.TESTING -> "テスト中"
                OrderStatus.COMPLETED -> "完了"
                OrderStatus.READY_FOR_PICKUP -> "引取可能"
                OrderStatus.DELIVERED -> "返却済"
                OrderStatus.CANCELLED -> "キャンセル"
            }
            AppLanguage.ZH -> when (status) {
                OrderStatus.OPEN -> "待处理"
                OrderStatus.SCREENING -> "初步检验"
                OrderStatus.DIAGNOSIS -> "故障诊断"
                OrderStatus.AWAITING_APPROVAL -> "等待确认"
                OrderStatus.AWAITING_PARTS -> "配件等待"
                OrderStatus.IN_REPAIR -> "正在维修"
                OrderStatus.TESTING -> "质检测试"
                OrderStatus.COMPLETED -> "已完成"
                OrderStatus.READY_FOR_PICKUP -> "待取件"
                OrderStatus.DELIVERED -> "已交付"
                OrderStatus.CANCELLED -> "已取消"
            }
            AppLanguage.KO -> when (status) {
                OrderStatus.OPEN -> "접수"
                OrderStatus.SCREENING -> "초기 점검"
                OrderStatus.DIAGNOSIS -> "진단 중"
                OrderStatus.AWAITING_APPROVAL -> "승인 대기"
                OrderStatus.AWAITING_PARTS -> "부품 대기"
                OrderStatus.IN_REPAIR -> "수리 중"
                OrderStatus.TESTING -> "테스트 중"
                OrderStatus.COMPLETED -> "완료"
                OrderStatus.READY_FOR_PICKUP -> "수령 준비 완료"
                OrderStatus.DELIVERED -> "출고 완료"
                OrderStatus.CANCELLED -> "취소됨"
            }
            AppLanguage.RU -> when (status) {
                OrderStatus.OPEN -> "Открыт"
                OrderStatus.SCREENING -> "Первичный осмотр"
                OrderStatus.DIAGNOSIS -> "Диагностика"
                OrderStatus.AWAITING_APPROVAL -> "Ожидает согласования"
                OrderStatus.AWAITING_PARTS -> "Ожидает запчасти"
                OrderStatus.IN_REPAIR -> "В ремонте"
                OrderStatus.TESTING -> "Тестирование"
                OrderStatus.COMPLETED -> "Завершен"
                OrderStatus.READY_FOR_PICKUP -> "Готов к выдаче"
                OrderStatus.DELIVERED -> "Выдан"
                OrderStatus.CANCELLED -> "Отменен"
            }
            AppLanguage.PT_PT -> when (status) {
                OrderStatus.OPEN -> "Aberta"
                OrderStatus.SCREENING -> "Em triagem"
                OrderStatus.DIAGNOSIS -> "Em diagnóstico"
                OrderStatus.AWAITING_APPROVAL -> "A aguardar aprovação"
                OrderStatus.AWAITING_PARTS -> "A aguardar peças"
                OrderStatus.IN_REPAIR -> "Em reparação"
                OrderStatus.TESTING -> "Em testes"
                OrderStatus.COMPLETED -> "Concluída"
                OrderStatus.READY_FOR_PICKUP -> "Pronta para levantamento"
                OrderStatus.DELIVERED -> "Entregue"
                OrderStatus.CANCELLED -> "Cancelada"
            }
        }
    }

    private val ptBrStrings = mapOf(
        "slogan" to "Sua assistência técnica, sob controle.",
        "nav_dashboard" to "Início",
        "nav_orders" to "Ordens",
        "nav_customers" to "Clientes",
        "nav_inventory" to "Estoque",
        "nav_financial" to "Finanças",
        "nav_warranties" to "Garantias",
        "nav_reports" to "Relatórios",
        "nav_settings" to "Empresa",
        "quick_access" to "Acesso Rápido",
        "new_order" to "Nova Ordem",
        "new_order_sub" to "Abrir ordem de serviço",
        "customers_sub" to "Cadastrar e consultar",
        "inventory_sub" to "Peças e componentes",
        "financial_sub" to "Receitas e fluxo de caixa",
        "operational_view" to "Visão Operacional",
        "in_repair" to "Em Reparo",
        "ready_pickup" to "Prontas",
        "open_orders" to "Abertas / Triagem",
        "total_orders" to "Total de Ordens",
        "financial_alerts" to "Financeiro & Alertas",
        "revenue_received" to "Receita Recebida",
        "pending_balance" to "Saldos a Receber",
        "active_warranties" to "Garantias CDC",
        "low_stock" to "Estoque Crítico",
        "recent_orders" to "Ordens Recentes",
        "no_orders_yet" to "Nenhuma ordem cadastrada ainda",
        "save_company" to "Salvar Alterações",
        "language_setting" to "Idioma do Aplicativo",
        "custom_logo" to "Logotipo da Empresa",
        "change_logo" to "Alterar Logotipo",
        "restore_default_logo" to "Restaurar Padrão",
        "save" to "Salvar",
        "cancel" to "Cancelar",
        "delete" to "Excluir",
        "edit" to "Editar",
        "confirm" to "Confirmar",
        "close" to "Fechar",
        "search" to "Buscar...",
        "all" to "Todas",
        "filter" to "Filtrar",
        "print" to "Imprimir",
        "share" to "Compartilhar",
        "export_csv" to "Exportar CSV",
        "whatsapp" to "WhatsApp",
        "call" to "Ligar",
        "status" to "Status",
        "duplicate" to "Duplicar OS",
        "orders_title" to "Ordens de Serviço",
        "search_orders" to "Buscar por cliente, OS, aparelho...",
        "new_order_btn" to "Nova OS",
        "customers_title" to "Clientes",
        "search_customers" to "Buscar por nome, CPF ou WhatsApp...",
        "new_customer_btn" to "Novo Cliente",
        "delete_customer_title" to "Excluir Cliente",
        "delete_customer_msg" to "Deseja realmente excluir este cliente? Todas as ordens, equipamentos e garantias associados serão excluídos com segurança.",
        "customer_saved" to "Cliente salvo com sucesso!",
        "customer_deleted" to "Cliente excluído com sucesso!",
        "inventory_title" to "Controle de Estoque",
        "search_inventory" to "Buscar peça por nome ou código...",
        "new_part_btn" to "Nova Peça",
        "financial_title" to "Fluxo Financeiro",
        "new_trans_btn" to "Nova Transação",
        "warranties_title" to "Garantias CDC",
        "reports_title" to "Relatórios Gerenciais",
        "select_language_title" to "Escolha o seu idioma",
        "select_language_desc" to "Selecione o idioma de sua preferência para inicializar o aplicativo:",
        "select_language_confirm" to "Confirmar e Continuar",
        "app_language_label" to "Idioma:"
    )

    private val enStrings = mapOf(
        "slogan" to "Your technical service, under control.",
        "nav_dashboard" to "Home",
        "nav_orders" to "Orders",
        "nav_customers" to "Customers",
        "nav_inventory" to "Inventory",
        "nav_financial" to "Finances",
        "nav_warranties" to "Warranties",
        "nav_reports" to "Reports",
        "nav_settings" to "Company",
        "quick_access" to "Quick Access",
        "new_order" to "New Order",
        "new_order_sub" to "Open technical work order",
        "customers_sub" to "Register and manage",
        "inventory_sub" to "Parts and components",
        "financial_sub" to "Income and cash flow",
        "operational_view" to "Operational Overview",
        "in_repair" to "In Repair",
        "ready_pickup" to "Ready",
        "open_orders" to "Open / Screening",
        "total_orders" to "Total Orders",
        "financial_alerts" to "Finances & Alerts",
        "revenue_received" to "Revenue Received",
        "pending_balance" to "Pending Balance",
        "active_warranties" to "Active Warranties",
        "low_stock" to "Low Stock",
        "recent_orders" to "Recent Orders",
        "no_orders_yet" to "No work orders registered yet",
        "save_company" to "Save Changes",
        "language_setting" to "App Language",
        "custom_logo" to "Company Logo",
        "change_logo" to "Change Logo",
        "restore_default_logo" to "Restore Default",
        "save" to "Save",
        "cancel" to "Cancel",
        "delete" to "Delete",
        "edit" to "Edit",
        "confirm" to "Confirm",
        "close" to "Close",
        "search" to "Search...",
        "all" to "All",
        "filter" to "Filter",
        "print" to "Print",
        "share" to "Share",
        "export_csv" to "Export CSV",
        "whatsapp" to "WhatsApp",
        "call" to "Call",
        "status" to "Status",
        "duplicate" to "Duplicate Order",
        "orders_title" to "Work Orders",
        "search_orders" to "Search by customer, order #, device...",
        "new_order_btn" to "New Order",
        "customers_title" to "Customers",
        "search_customers" to "Search by name, tax ID or phone...",
        "new_customer_btn" to "New Customer",
        "delete_customer_title" to "Delete Customer",
        "delete_customer_msg" to "Are you sure you want to delete this customer? All associated orders, devices, and warranties will be safely removed.",
        "customer_saved" to "Customer saved successfully!",
        "customer_deleted" to "Customer deleted successfully!",
        "inventory_title" to "Inventory Control",
        "search_inventory" to "Search part by name or code...",
        "new_part_btn" to "New Part",
        "financial_title" to "Cash Flow",
        "new_trans_btn" to "New Transaction",
        "warranties_title" to "Warranties",
        "reports_title" to "Management Reports",
        "select_language_title" to "Choose your language",
        "select_language_desc" to "Select your preferred language to initialize the app:",
        "select_language_confirm" to "Confirm and Continue",
        "app_language_label" to "Language:"
    )

    private val esStrings = mapOf(
        "slogan" to "Su servicio técnico, bajo control.",
        "nav_dashboard" to "Inicio",
        "nav_orders" to "Órdenes",
        "nav_customers" to "Clientes",
        "nav_inventory" to "Inventario",
        "nav_financial" to "Finanzas",
        "nav_warranties" to "Garantías",
        "nav_reports" to "Informes",
        "nav_settings" to "Empresa",
        "quick_access" to "Acceso Rápido",
        "new_order" to "Nueva Orden",
        "new_order_sub" to "Abrir orden de servicio",
        "customers_sub" to "Registrar y consultar",
        "inventory_sub" to "Piezas y repuestos",
        "financial_sub" to "Ingresos y flujo de caja",
        "operational_view" to "Visión Operativa",
        "in_repair" to "En Reparación",
        "ready_pickup" to "Listas",
        "open_orders" to "Abiertas / Triaje",
        "total_orders" to "Total de Órdenes",
        "financial_alerts" to "Finanzas y Alertas",
        "revenue_received" to "Ingresos Cobrados",
        "pending_balance" to "Saldos Pendientes",
        "active_warranties" to "Garantías Activas",
        "low_stock" to "Stock Crítico",
        "recent_orders" to "Órdenes Recientes",
        "no_orders_yet" to "No hay órdenes registradas aún",
        "save_company" to "Guardar Cambios",
        "language_setting" to "Idioma de la Aplicación",
        "custom_logo" to "Logotipo de la Empresa",
        "change_logo" to "Cambiar Logotipo",
        "restore_default_logo" to "Restaurar Original",
        "save" to "Guardar",
        "cancel" to "Cancelar",
        "delete" to "Eliminar",
        "edit" to "Editar",
        "confirm" to "Confirmar",
        "close" to "Cerrar",
        "search" to "Buscar...",
        "all" to "Todas",
        "filter" to "Filtrar",
        "print" to "Imprimir",
        "share" to "Compartir",
        "export_csv" to "Exportar CSV",
        "whatsapp" to "WhatsApp",
        "call" to "Llamar",
        "status" to "Estado",
        "duplicate" to "Duplicar OS",
        "orders_title" to "Órdenes de Servicio",
        "search_orders" to "Buscar por cliente, OS, equipo...",
        "new_order_btn" to "Nueva OS",
        "customers_title" to "Clientes",
        "search_customers" to "Buscar por nombre, documento o WhatsApp...",
        "new_customer_btn" to "Nuevo Cliente",
        "delete_customer_title" to "Eliminar Cliente",
        "delete_customer_msg" to "¿Realmente desea eliminar este cliente? Se borrarán sus órdenes y equipos asociados.",
        "customer_saved" to "¡Cliente guardado con éxito!",
        "customer_deleted" to "¡Cliente eliminado con éxito!",
        "inventory_title" to "Control de Inventario",
        "search_inventory" to "Buscar repuesto por nombre o código...",
        "new_part_btn" to "Nuevo Repuesto",
        "financial_title" to "Flujo Financiero",
        "new_trans_btn" to "Nueva Transacción",
        "warranties_title" to "Garantías Activas",
        "reports_title" to "Informes de Gestión",
        "select_language_title" to "Seleccione su idioma",
        "select_language_desc" to "Elija el idioma preferido para iniciar la aplicación:",
        "select_language_confirm" to "Confirmar y Continuar",
        "app_language_label" to "Idioma:"
    )

    private val frStrings = mapOf(
        "slogan" to "Votre atelier de réparation, sous contrôle.",
        "nav_dashboard" to "Accueil",
        "nav_orders" to "Ordres",
        "nav_customers" to "Clients",
        "nav_inventory" to "Stock",
        "nav_financial" to "Finances",
        "nav_warranties" to "Garanties",
        "nav_reports" to "Rapports",
        "nav_settings" to "Entreprise",
        "quick_access" to "Accès Rapide",
        "new_order" to "Nouvel Ordre",
        "new_order_sub" to "Créer un ordre de réparation",
        "customers_sub" to "Enregistrer et consulter",
        "inventory_sub" to "Pièces et composants",
        "financial_sub" to "Revenus et trésorerie",
        "operational_view" to "Vue Opérationnelle",
        "in_repair" to "En Réparation",
        "ready_pickup" to "Prêts",
        "open_orders" to "Ouverts / Triage",
        "total_orders" to "Total des Ordres",
        "financial_alerts" to "Finances et Alertes",
        "revenue_received" to "Revenus Encaissés",
        "pending_balance" to "Soldes Dus",
        "active_warranties" to "Garanties Actives",
        "low_stock" to "Stock Critique",
        "recent_orders" to "Ordres Récents",
        "no_orders_yet" to "Aucun ordre pour le moment",
        "save_company" to "Enregistrer",
        "language_setting" to "Langue de l'application",
        "custom_logo" to "Logo de l'entreprise",
        "change_logo" to "Changer de Logo",
        "restore_default_logo" to "Restaurer Défaut",
        "save" to "Enregistrer",
        "cancel" to "Annuler",
        "delete" to "Supprimer",
        "edit" to "Modifier",
        "confirm" to "Confirmer",
        "close" to "Fermer",
        "search" to "Rechercher...",
        "all" to "Tous",
        "filter" to "Filtrer",
        "print" to "Imprimer",
        "share" to "Partager",
        "export_csv" to "Exporter CSV",
        "whatsapp" to "WhatsApp",
        "call" to "Appeler",
        "status" to "Statut",
        "duplicate" to "Dupliquer",
        "orders_title" to "Ordres de Réparation",
        "search_orders" to "Rechercher par client, ordre, appareil...",
        "new_order_btn" to "Nouvel Ordre",
        "customers_title" to "Clients",
        "search_customers" to "Rechercher client...",
        "new_customer_btn" to "Nouveau Client",
        "delete_customer_title" to "Supprimer le Client",
        "delete_customer_msg" to "Voulez-vous vraiment supprimer ce client et tous ses dossiers associés ?",
        "customer_saved" to "Client enregistré avec succès !",
        "customer_deleted" to "Client supprimé avec succès !",
        "inventory_title" to "Gestion des Stocks",
        "search_inventory" to "Rechercher une pièce...",
        "new_part_btn" to "Nouvelle Pièce",
        "financial_title" to "Trésorerie",
        "new_trans_btn" to "Nouvelle Transaction",
        "warranties_title" to "Garanties Actives",
        "reports_title" to "Rapports d'Activité",
        "select_language_title" to "Choisissez votre langue",
        "select_language_desc" to "Sélectionnez votre langue pour initialiser l'application :",
        "select_language_confirm" to "Confirmer et Continuer",
        "app_language_label" to "Langue :"
    )

    private val daStrings = mapOf(
        "slogan" to "Dit værksted, under fuld kontrol.",
        "nav_dashboard" to "Start",
        "nav_orders" to "Ordrer",
        "nav_customers" to "Kunder",
        "nav_inventory" to "Lager",
        "nav_financial" to "Økonomi",
        "nav_warranties" to "Garanti",
        "nav_reports" to "Rapporter",
        "nav_settings" to "Virksomhed",
        "quick_access" to "Hurtig Adgang",
        "new_order" to "Ny Ordre",
        "new_order_sub" to "Opret arbejdsordre",
        "customers_sub" to "Opret og søg",
        "inventory_sub" to "Dele og komponenter",
        "financial_sub" to "Indtægter og likviditet",
        "operational_view" to "Driftsoversigt",
        "in_repair" to "Under Rep.",
        "ready_pickup" to "Klar",
        "open_orders" to "Åbne / Visitation",
        "total_orders" to "Samlede Ordrer",
        "financial_alerts" to "Økonomi & Advarsler",
        "revenue_received" to "Modtaget Indtægt",
        "pending_balance" to "Udestående Saldo",
        "active_warranties" to "Aktive Garantier",
        "low_stock" to "Lav Beholdning",
        "recent_orders" to "Seneste Ordrer",
        "no_orders_yet" to "Ingen ordrer endnu",
        "save_company" to "Gem Ændringer",
        "language_setting" to "App Sprog",
        "custom_logo" to "Virksomhedslogo",
        "change_logo" to "Skift Logo",
        "restore_default_logo" to "Gendan Standard",
        "save" to "Gem",
        "cancel" to "Annuller",
        "delete" to "Slet",
        "edit" to "Rediger",
        "confirm" to "Bekræft",
        "close" to "Luk",
        "search" to "Søg...",
        "all" to "Alle",
        "filter" to "Filtrer",
        "print" to "Udskriv",
        "share" to "Del",
        "export_csv" to "Eksporter CSV",
        "whatsapp" to "WhatsApp",
        "call" to "Ring",
        "status" to "Status",
        "duplicate" to "Kopier Ordre",
        "orders_title" to "Arbejdsordrer",
        "search_orders" to "Søg efter kunde, ordre, enhed...",
        "new_order_btn" to "Ny Ordre",
        "customers_title" to "Kunder",
        "search_customers" to "Søg kunde...",
        "new_customer_btn" to "Ny Kunde",
        "delete_customer_title" to "Slet Kunde",
        "delete_customer_msg" to "Er du sikker på, at du vil slette denne kunde og tilhørende data?",
        "customer_saved" to "Kunde gemt!",
        "customer_deleted" to "Kunde slettet!",
        "inventory_title" to "Lagerstyring",
        "search_inventory" to "Søg reservedel...",
        "new_part_btn" to "Ny Reservedel",
        "financial_title" to "Økonomisk Oversigt",
        "new_trans_btn" to "Ny Postering",
        "warranties_title" to "Garantioversigt",
        "reports_title" to "Rapporter",
        "select_language_title" to "Vælg dit sprog",
        "select_language_desc" to "Vælg sprog for at starte applikationen:",
        "select_language_confirm" to "Bekræft og Fortsæt",
        "app_language_label" to "Sprog:"
    )

    private val itStrings = mapOf(
        "slogan" to "La tua assistenza tecnica, sotto controllo.",
        "nav_dashboard" to "Home",
        "nav_orders" to "Ordini",
        "nav_customers" to "Clienti",
        "nav_inventory" to "Magazzino",
        "nav_financial" to "Finanze",
        "nav_warranties" to "Garanzie",
        "nav_reports" to "Rapporti",
        "nav_settings" to "Azienda",
        "quick_access" to "Accesso Rapido",
        "new_order" to "Nuovo Ordine",
        "new_order_sub" to "Apri ordine di servizio",
        "customers_sub" to "Registra e consulta",
        "inventory_sub" to "Ricambi e componenti",
        "financial_sub" to "Entrate e cassa",
        "operational_view" to "Vista Operativa",
        "in_repair" to "In Riparazione",
        "ready_pickup" to "Pronte",
        "open_orders" to "Aperte / Accettazione",
        "total_orders" to "Totale Ordini",
        "financial_alerts" to "Finanze e Avvisi",
        "revenue_received" to "Entrate Incassate",
        "pending_balance" to "Saldi da Incassare",
        "active_warranties" to "Garanzie Attive",
        "low_stock" to "Scorte Critiche",
        "recent_orders" to "Ordini Recenti",
        "no_orders_yet" to "Nessun ordine presente",
        "save_company" to "Salva Modifiche",
        "language_setting" to "Lingua dell'App",
        "custom_logo" to "Logo Aziendale",
        "change_logo" to "Cambia Logo",
        "restore_default_logo" to "Ripristina Predefinito",
        "save" to "Salva",
        "cancel" to "Annulla",
        "delete" to "Elimina",
        "edit" to "Modifica",
        "confirm" to "Conferma",
        "close" to "Chiudi",
        "search" to "Cerca...",
        "all" to "Tutti",
        "filter" to "Filtra",
        "print" to "Stampa",
        "share" to "Condividi",
        "export_csv" to "Esporta CSV",
        "whatsapp" to "WhatsApp",
        "call" to "Chiama",
        "status" to "Stato",
        "duplicate" to "Duplica Ordine",
        "orders_title" to "Ordini di Servizio",
        "search_orders" to "Cerca per cliente, ordine, dispositivo...",
        "new_order_btn" to "Nuovo Ordine",
        "customers_title" to "Clienti",
        "search_customers" to "Cerca clienti...",
        "new_customer_btn" to "Nuovo Cliente",
        "delete_customer_title" to "Elimina Cliente",
        "delete_customer_msg" to "Sei sicuro di voler eliminare questo cliente e tutti i suoi dati?",
        "customer_saved" to "Cliente salvato con successo!",
        "customer_deleted" to "Cliente eliminato con successo!",
        "inventory_title" to "Gestione Magazzino",
        "search_inventory" to "Cerca ricambio...",
        "new_part_btn" to "Nuovo Ricambio",
        "financial_title" to "Gestione Finanziaria",
        "new_trans_btn" to "Nuova Transazione",
        "warranties_title" to "Garanzie Attive",
        "reports_title" to "Rapporti di Gestione",
        "select_language_title" to "Scegli la tua lingua",
        "select_language_desc" to "Seleziona la lingua per avviare l'applicazione:",
        "select_language_confirm" to "Conferma e Continua",
        "app_language_label" to "Lingua:"
    )

    private val jaStrings = mapOf(
        "slogan" to "修理工房を、確実に管理。",
        "nav_dashboard" to "ホーム",
        "nav_orders" to "注文",
        "nav_customers" to "顧客",
        "nav_inventory" to "在庫",
        "nav_financial" to "会計",
        "nav_warranties" to "保証",
        "nav_reports" to "レポート",
        "nav_settings" to "会社情報",
        "quick_access" to "クイック操作",
        "new_order" to "新規注文",
        "new_order_sub" to "修理伝票の作成",
        "customers_sub" to "顧客の登録・検索",
        "inventory_sub" to "部品とパーツ管理",
        "financial_sub" to "売上と収支管理",
        "operational_view" to "業務概況",
        "in_repair" to "修理中",
        "ready_pickup" to "引取可能",
        "open_orders" to "受付 / 診断",
        "total_orders" to "総注文数",
        "financial_alerts" to "会計・アラート",
        "revenue_received" to "売上受取額",
        "pending_balance" to "未回収残高",
        "active_warranties" to "有効な保証",
        "low_stock" to "在庫不足警告",
        "recent_orders" to "最近の注文",
        "no_orders_yet" to "登録された注文はありません",
        "save_company" to "保存する",
        "language_setting" to "アプリの言語",
        "custom_logo" to "会社のロゴ",
        "change_logo" to "ロゴを変更",
        "restore_default_logo" to "初期ロゴに戻す",
        "save" to "保存",
        "cancel" to "キャンセル",
        "delete" to "削除",
        "edit" to "編集",
        "confirm" to "確認",
        "close" to "閉じる",
        "search" to "検索...",
        "all" to "すべて",
        "filter" to "絞り込み",
        "print" to "印刷",
        "share" to "共有",
        "export_csv" to "CSV出力",
        "whatsapp" to "WhatsApp",
        "call" to "発信",
        "status" to "状態",
        "duplicate" to "複製",
        "orders_title" to "修理受付一覧",
        "search_orders" to "顧客名・端末・伝票番号で検索...",
        "new_order_btn" to "新規受付",
        "customers_title" to "顧客一覧",
        "search_customers" to "顧客を検索...",
        "new_customer_btn" to "顧客登録",
        "delete_customer_title" to "顧客の削除",
        "delete_customer_msg" to "この顧客および関連データを削除しますか？",
        "customer_saved" to "顧客情報を保存しました",
        "customer_deleted" to "顧客を削除しました",
        "inventory_title" to "在庫管理",
        "search_inventory" to "パーツを検索...",
        "new_part_btn" to "部品登録",
        "financial_title" to "収支管理",
        "new_trans_btn" to "取引登録",
        "warranties_title" to "保証一覧",
        "reports_title" to "経営レポート",
        "select_language_title" to "言語を選択してください",
        "select_language_desc" to "アプリで使用する言語を選択してください：",
        "select_language_confirm" to "確認して開始",
        "app_language_label" to "言語:"
    )

    private val zhStrings = mapOf(
        "slogan" to "维修工单，尽在掌控。",
        "nav_dashboard" to "首页",
        "nav_orders" to "工单",
        "nav_customers" to "客户",
        "nav_inventory" to "库存",
        "nav_financial" to "财务",
        "nav_warranties" to "保修",
        "nav_reports" to "报表",
        "nav_settings" to "企业",
        "quick_access" to "快捷操作",
        "new_order" to "新建工单",
        "new_order_sub" to "创建服务维修单",
        "customers_sub" to "登记与查询客户",
        "inventory_sub" to "配件与元件管理",
        "financial_sub" to "收入与资金流水",
        "operational_view" to "运营概览",
        "in_repair" to "维修中",
        "ready_pickup" to "待取件",
        "open_orders" to "新单 / 待检",
        "total_orders" to "工单总数",
        "financial_alerts" to "财务与提醒",
        "revenue_received" to "已收营业额",
        "pending_balance" to "待收余款",
        "active_warranties" to "生效保修",
        "low_stock" to "库存预警",
        "recent_orders" to "最新工单",
        "no_orders_yet" to "暂无工单记录",
        "save_company" to "保存更改",
        "language_setting" to "应用语言",
        "custom_logo" to "企业标志",
        "change_logo" to "更换标志",
        "restore_default_logo" to "恢复默认",
        "save" to "保存",
        "cancel" to "取消",
        "delete" to "删除",
        "edit" to "编辑",
        "confirm" to "确认",
        "close" to "关闭",
        "search" to "搜索...",
        "all" to "全部",
        "filter" to "筛选",
        "print" to "打印",
        "share" to "分享",
        "export_csv" to "导出CSV",
        "whatsapp" to "WhatsApp",
        "call" to "拨打电话",
        "status" to "状态",
        "duplicate" to "复制工单",
        "orders_title" to "维修工单列表",
        "search_orders" to "按客户、工单、设备搜索...",
        "new_order_btn" to "新建工单",
        "customers_title" to "客户管理",
        "search_customers" to "搜索客户...",
        "new_customer_btn" to "新增客户",
        "delete_customer_title" to "删除客户",
        "delete_customer_msg" to "确定要删除该客户及其关联的数据吗？",
        "customer_saved" to "客户已成功保存！",
        "customer_deleted" to "客户已成功删除！",
        "inventory_title" to "库存管理",
        "search_inventory" to "搜索配件...",
        "new_part_btn" to "新增配件",
        "financial_title" to "财务收支",
        "new_trans_btn" to "记一笔",
        "warranties_title" to "保修管理",
        "reports_title" to "统计报表",
        "select_language_title" to "选择您的语言",
        "select_language_desc" to "请选择启动应用时使用的首选语言：",
        "select_language_confirm" to "确认并继续",
        "app_language_label" to "语言:"
    )

    private val koStrings = mapOf(
        "slogan" to "철저하고 스마트한 수리 관리.",
        "nav_dashboard" to "홈",
        "nav_orders" to "주문",
        "nav_customers" to "고객",
        "nav_inventory" to "재고",
        "nav_financial" to "재무",
        "nav_warranties" to "보증",
        "nav_reports" to "보고서",
        "nav_settings" to "업체정보",
        "quick_access" to "빠른 메뉴",
        "new_order" to "새 주문",
        "new_order_sub" to "수리 접수 등록",
        "customers_sub" to "고객 등록 및 조회",
        "inventory_sub" to "부품 및 재고 관리",
        "financial_sub" to "매출 및 입출금",
        "operational_view" to "운영 현황",
        "in_repair" to "수리 중",
        "ready_pickup" to "출고 대기",
        "open_orders" to "접수 / 점검",
        "total_orders" to "전체 주문",
        "financial_alerts" to "재무 및 알림",
        "revenue_received" to "수납된 매출",
        "pending_balance" to "미수 잔액",
        "active_warranties" to "유효 보증",
        "low_stock" to "재고 부족",
        "recent_orders" to "최근 주문",
        "no_orders_yet" to "등록된 주문이 없습니다",
        "save_company" to "변경사항 저장",
        "language_setting" to "앱 언어",
        "custom_logo" to "업체 로고",
        "change_logo" to "로고 변경",
        "restore_default_logo" to "기본 로고 복원",
        "save" to "저장",
        "cancel" to "취소",
        "delete" to "삭제",
        "edit" to "수정",
        "confirm" to "확인",
        "close" to "닫기",
        "search" to "검색...",
        "all" to "전체",
        "filter" to "필터",
        "print" to "인쇄",
        "share" to "공유",
        "export_csv" to "CSV 내보내기",
        "whatsapp" to "WhatsApp",
        "call" to "전화 걸기",
        "status" to "상태",
        "duplicate" to "주문 복사",
        "orders_title" to "수리 주문 목록",
        "search_orders" to "고객명, 주문번호, 기기 검색...",
        "new_order_btn" to "새 주문",
        "customers_title" to "고객 관리",
        "search_customers" to "고객 검색...",
        "new_customer_btn" to "새 고객 등록",
        "delete_customer_title" to "고객 삭제",
        "delete_customer_msg" to "해당 고객과 관련된 모든 데이터를 삭제하시겠습니까?",
        "customer_saved" to "고객 정보가 저장되었습니다!",
        "customer_deleted" to "고객이 삭제되었습니다!",
        "inventory_title" to "재고 관리",
        "search_inventory" to "부품 검색...",
        "new_part_btn" to "새 부품 등록",
        "financial_title" to "재무 관리",
        "new_trans_btn" to "새 거래 기록",
        "warranties_title" to "보증 관리",
        "reports_title" to "경영 보고서",
        "select_language_title" to "언어를 선택하세요",
        "select_language_desc" to "앱을 시작할 기본 언어를 선택하세요:",
        "select_language_confirm" to "확인 후 시작",
        "app_language_label" to "언어:"
    )

    private val ruStrings = mapOf(
        "slogan" to "Ваш сервисный центр под полным контролем.",
        "nav_dashboard" to "Главная",
        "nav_orders" to "Заказы",
        "nav_customers" to "Клиенты",
        "nav_inventory" to "Склад",
        "nav_financial" to "Финансы",
        "nav_warranties" to "Гарантии",
        "nav_reports" to "Отчеты",
        "nav_settings" to "Компания",
        "quick_access" to "Быстрый доступ",
        "new_order" to "Новый заказ",
        "new_order_sub" to "Открыть заказ-наряд",
        "customers_sub" to "База клиентов",
        "inventory_sub" to "Запчасти и детали",
        "financial_sub" to "Доходы и касса",
        "operational_view" to "Операционная сводка",
        "in_repair" to "В ремонте",
        "ready_pickup" to "Готовы",
        "open_orders" to "Новые / Осмотр",
        "total_orders" to "Всего заказов",
        "financial_alerts" to "Финансы и статус",
        "revenue_received" to "Выручка получена",
        "pending_balance" to "Остаток к оплате",
        "active_warranties" to "Активные гарантии",
        "low_stock" to "Мало на складе",
        "recent_orders" to "Недавние заказы",
        "no_orders_yet" to "Заказов пока нет",
        "save_company" to "Сохранить изменения",
        "language_setting" to "Язык приложения",
        "custom_logo" to "Логотип компании",
        "change_logo" to "Изменить логотип",
        "restore_default_logo" to "Сбросить по умолчанию",
        "save" to "Сохранить",
        "cancel" to "Отмена",
        "delete" to "Удалить",
        "edit" to "Редактировать",
        "confirm" to "Подтвердить",
        "close" to "Закрыть",
        "search" to "Поиск...",
        "all" to "Все",
        "filter" to "Фильтр",
        "print" to "Печать",
        "share" to "Поделиться",
        "export_csv" to "Экспорт CSV",
        "whatsapp" to "WhatsApp",
        "call" to "Позвонить",
        "status" to "Статус",
        "duplicate" to "Дублировать заказ",
        "orders_title" to "Заказы на ремонт",
        "search_orders" to "Поиск по клиенту, заказу, устройству...",
        "new_order_btn" to "Новый заказ",
        "customers_title" to "Клиенты",
        "search_customers" to "Поиск клиентов...",
        "new_customer_btn" to "Новый клиент",
        "delete_customer_title" to "Удалить клиента",
        "delete_customer_msg" to "Вы уверены, что хотите удалить этого клиента и все связанные данные?",
        "customer_saved" to "Клиент успешно сохранен!",
        "customer_deleted" to "Клиент успешно удален!",
        "inventory_title" to "Складской учет",
        "search_inventory" to "Поиск детали...",
        "new_part_btn" to "Новая деталь",
        "financial_title" to "Финансовый поток",
        "new_trans_btn" to "Новая транзакция",
        "warranties_title" to "Гарантийный учет",
        "reports_title" to "Отчеты сервиса",
        "select_language_title" to "Выберите язык",
        "select_language_desc" to "Выберите предпочтительный язык для запуска приложения:",
        "select_language_confirm" to "Подтвердить и продолжить",
        "app_language_label" to "Язык:"
    )

    private val ptPtStrings = mapOf(
        "slogan" to "A sua assistência técnica, sob controlo.",
        "nav_dashboard" to "Início",
        "nav_orders" to "Ordens",
        "nav_customers" to "Clientes",
        "nav_inventory" to "Stock",
        "nav_financial" to "Finanças",
        "nav_warranties" to "Garantias",
        "nav_reports" to "Relatórios",
        "nav_settings" to "Empresa",
        "quick_access" to "Acesso Rápido",
        "new_order" to "Nova Ordem",
        "new_order_sub" to "Abrir ordem de serviço",
        "customers_sub" to "Registar e consultar",
        "inventory_sub" to "Peças e componentes",
        "financial_sub" to "Receitas e fluxo de caixa",
        "operational_view" to "Visão Operacional",
        "in_repair" to "Em Reparação",
        "ready_pickup" to "Prontas",
        "open_orders" to "Abertas / Triagem",
        "total_orders" to "Total de Ordens",
        "financial_alerts" to "Financeiro & Alertas",
        "revenue_received" to "Receita Cobrada",
        "pending_balance" to "Saldos a Receber",
        "active_warranties" to "Garantias Legais",
        "low_stock" to "Stock Crítico",
        "recent_orders" to "Ordens Recentes",
        "no_orders_yet" to "Nenhuma ordem registada ainda",
        "save_company" to "Guardar Alterações",
        "language_setting" to "Idioma da Aplicação",
        "custom_logo" to "Logótipo da Empresa",
        "change_logo" to "Alterar Logótipo",
        "restore_default_logo" to "Restaurar Padrão",
        "save" to "Guardar",
        "cancel" to "Cancelar",
        "delete" to "Eliminar",
        "edit" to "Editar",
        "confirm" to "Confirmar",
        "close" to "Fechar",
        "search" to "Pesquisar...",
        "all" to "Todas",
        "filter" to "Filtrar",
        "print" to "Imprimir",
        "share" to "Partilhar",
        "export_csv" to "Exportar CSV",
        "whatsapp" to "WhatsApp",
        "call" to "Ligar",
        "status" to "Estado",
        "duplicate" to "Duplicar OS",
        "orders_title" to "Ordens de Serviço",
        "search_orders" to "Pesquisar por cliente, OS, equipamento...",
        "new_order_btn" to "Nova OS",
        "customers_title" to "Clientes",
        "search_customers" to "Pesquisar por nome, NIF ou WhatsApp...",
        "new_customer_btn" to "Novo Cliente",
        "delete_customer_title" to "Eliminar Cliente",
        "delete_customer_msg" to "Deseja realmente eliminar este cliente? Todas as ordens e equipamentos associados serão removidos com segurança.",
        "customer_saved" to "Cliente guardado com sucesso!",
        "customer_deleted" to "Cliente eliminado com sucesso!",
        "inventory_title" to "Controlo de Stock",
        "search_inventory" to "Pesquisar peça...",
        "new_part_btn" to "Nova Peça",
        "financial_title" to "Fluxo Financeiro",
        "new_trans_btn" to "Nova Transação",
        "warranties_title" to "Garantias",
        "reports_title" to "Relatórios de Gestão",
        "select_language_title" to "Escolha o seu idioma",
        "select_language_desc" to "Selecione o idioma de sua preferência para inicializar a aplicação:",
        "select_language_confirm" to "Confirmar e Continuar",
        "app_language_label" to "Idioma:"
    )
}

@Composable
fun LanguagePickerDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    var tempSelection by remember { mutableStateOf(currentLanguage) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = AppStrings.get("select_language_title", tempSelection),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = AppStrings.get("select_language_desc", tempSelection),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(AppLanguage.values()) { lang ->
                        val isSelected = tempSelection == lang
                        Card(
                            onClick = {
                                tempSelection = lang
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("lang_option_${lang.code}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = lang.flagEmoji,
                                    fontSize = 22.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lang.displayName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = lang.code,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { tempSelection = lang }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onLanguageSelected(tempSelection)
                    onDismiss()
                },
                modifier = Modifier.testTag("btn_confirm_language")
            ) {
                Text(
                    text = AppStrings.get("select_language_confirm", tempSelection),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_language")
            ) {
                Text(AppStrings.get("cancel", tempSelection))
            }
        }
    )
}
