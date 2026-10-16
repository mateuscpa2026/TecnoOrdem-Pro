package com.example.domain.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import com.example.data.model.CompanyProfile
import com.example.data.model.Customer
import com.example.data.model.Equipment
import com.example.data.model.Warranty
import com.example.data.model.WorkOrder
import java.io.File
import java.io.FileOutputStream

enum class DocumentKind(val docTitle: String) {
    WORK_ORDER("ORDEM DE SERVIÇO"),
    CHECK_IN_RECEIPT("COMPROVANTE DE ENTRADA"),
    QUOTE("ORÇAMENTO COMERCIAL"),
    WARRANTY_CERTIFICATE("CERTIFICADO DE GARANTIA (CDC)"),
    DELIVERY_RECEIPT("TERMO DE ENTREGA E QUITAÇÃO")
}

object PdfDocumentGenerator {

    private const val PAGE_WIDTH = 595 // A4 width in pt
    private const val PAGE_HEIGHT = 842 // A4 height in pt

    fun generateDocument(
        context: Context,
        kind: DocumentKind,
        company: CompanyProfile,
        workOrder: WorkOrder,
        customer: Customer?,
        equipment: Equipment?,
        warranty: Warranty? = null
    ): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        drawDocument(canvas, kind, company, workOrder, customer, equipment, warranty)

        document.finishPage(page)

        val outputDir = File(context.cacheDir, "documents").apply { mkdirs() }
        val filename = "${kind.name.lowercase()}_os_${workOrder.orderNumber}_${System.currentTimeMillis()}.pdf"
        val outputFile = File(outputDir, filename)

        FileOutputStream(outputFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        return outputFile
    }

    private fun drawDocument(
        canvas: Canvas,
        kind: DocumentKind,
        company: CompanyProfile,
        workOrder: WorkOrder,
        customer: Customer?,
        equipment: Equipment?,
        warranty: Warranty?
    ) {
        val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
        val paintBg = Paint(Paint.ANTI_ALIAS_FLAG)

        // Margin
        val left = 36f
        val right = PAGE_WIDTH - 36f
        var y = 36f

        // 1. Top Header Banner (Navy #0B1220)
        paintBg.color = Color.parseColor("#0B1220")
        canvas.drawRoundRect(RectF(left, y, right, y + 68f), 8f, 8f, paintBg)

        // Header Title
        paintText.color = Color.WHITE
        paintText.textSize = 16f
        paintText.isFakeBoldText = true
        canvas.drawText(company.tradeName.ifBlank { "TecnoOrdem Pro" }, left + 14f, y + 26f, paintText)

        paintText.textSize = 9.5f
        paintText.isFakeBoldText = false
        paintText.color = Color.parseColor("#94A3B8")
        canvas.drawText("${company.documentNumber} | Tel: ${company.phone} | Whats: ${company.whatsapp}", left + 14f, y + 42f, paintText)
        canvas.drawText("${company.address}, ${company.city} - ${company.state}", left + 14f, y + 56f, paintText)

        // Document Kind Badge & OS Number (Right side)
        paintText.color = Color.parseColor("#38BDF8")
        paintText.textSize = 12f
        paintText.isFakeBoldText = true
        val kindText = kind.docTitle
        val kindWidth = paintText.measureText(kindText)
        canvas.drawText(kindText, right - 14f - kindWidth, y + 26f, paintText)

        paintText.color = Color.WHITE
        paintText.textSize = 14f
        val osText = "Nº ${workOrder.orderNumber}"
        val osWidth = paintText.measureText(osText)
        canvas.drawText(osText, right - 14f - osWidth, y + 46f, paintText)

        paintText.color = Color.parseColor("#94A3B8")
        paintText.textSize = 8.5f
        val dateText = "Data: ${Formatters.formatDateTime(workOrder.openedAt)}"
        val dateWidth = paintText.measureText(dateText)
        canvas.drawText(dateText, right - 14f - dateWidth, y + 58f, paintText)

        y += 78f

        // 2. Customer Section Box
        y = drawSectionBox(canvas, left, right, y, "DADOS DO CLIENTE") { startY ->
            paintText.color = Color.parseColor("#0B1220")
            paintText.textSize = 9.5f
            paintText.isFakeBoldText = true
            canvas.drawText("Nome:", left + 12f, startY + 16f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(customer?.name ?: "Cliente não informado", left + 55f, startY + 16f, paintText)

            paintText.isFakeBoldText = true
            canvas.drawText("CPF/CNPJ:", left + 320f, startY + 16f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(customer?.documentNumber?.ifBlank { "Não informado" } ?: "-", left + 380f, startY + 16f, paintText)

            paintText.isFakeBoldText = true
            canvas.drawText("Telefone:", left + 12f, startY + 30f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(customer?.phone?.ifBlank { customer.whatsapp } ?: "-", left + 65f, startY + 30f, paintText)

            paintText.isFakeBoldText = true
            canvas.drawText("Endereço:", left + 200f, startY + 30f, paintText)
            paintText.isFakeBoldText = false
            val addr = "${customer?.address ?: ""}, ${customer?.city ?: ""}"
            canvas.drawText(addr.trim().ifEmpty { "-" }, left + 255f, startY + 30f, paintText)

            40f
        }

        y += 8f

        // 3. Equipment Section Box
        y = drawSectionBox(canvas, left, right, y, "EQUIPAMENTO & CONDIÇÃO DE ENTRADA") { startY ->
            paintText.color = Color.parseColor("#0B1220")
            paintText.textSize = 9.5f
            paintText.isFakeBoldText = true
            canvas.drawText("Aparelho:", left + 12f, startY + 16f, paintText)
            paintText.isFakeBoldText = false
            val eqName = "${equipment?.brand ?: ""} ${equipment?.model ?: ""}".trim().ifEmpty { "Equipamento geral" }
            canvas.drawText(eqName, left + 65f, startY + 16f, paintText)

            paintText.isFakeBoldText = true
            canvas.drawText("Série/IMEI:", left + 300f, startY + 16f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(equipment?.serialNumberOrImei?.ifBlank { "Não informado" } ?: "-", left + 360f, startY + 16f, paintText)

            paintText.isFakeBoldText = true
            canvas.drawText("Acessórios:", left + 12f, startY + 30f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(equipment?.accessoriesReceived?.ifBlank { "Nenhum acessório" } ?: "-", left + 75f, startY + 30f, paintText)

            paintText.isFakeBoldText = true
            canvas.drawText("Condição:", left + 12f, startY + 44f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(equipment?.entryCondition ?: "Sinais normais de uso", left + 65f, startY + 44f, paintText)

            54f
        }

        y += 8f

        // 4. Defect & Diagnostic Box
        y = drawSectionBox(canvas, left, right, y, "DIAGNÓSTICO E SERVIÇOS TÉCNICOS") { startY ->
            paintText.color = Color.parseColor("#0B1220")
            paintText.textSize = 9.5f
            paintText.isFakeBoldText = true
            canvas.drawText("Defeito Relatado:", left + 12f, startY + 16f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(workOrder.reportedDefect.ifBlank { "Conforme análise em bancada" }, left + 110f, startY + 16f, paintText)

            paintText.isFakeBoldText = true
            canvas.drawText("Parecer Técnico:", left + 12f, startY + 32f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(workOrder.technicalDiagnosis.ifBlank { "Em diagnóstico técnico" }, left + 110f, startY + 32f, paintText)

            paintText.isFakeBoldText = true
            canvas.drawText("Serviços:", left + 12f, startY + 48f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(workOrder.servicesDescription.ifBlank { "Mão de obra técnica especializada" }, left + 110f, startY + 48f, paintText)

            paintText.isFakeBoldText = true
            canvas.drawText("Peças Trocadas:", left + 12f, startY + 64f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(workOrder.partsDescription.ifBlank { "Nenhuma peça adicional" }, left + 110f, startY + 64f, paintText)

            74f
        }

        y += 8f

        // 5. Financial Summary Box
        y = drawSectionBox(canvas, left, right, y, "VALORES & FORMA DE PAGAMENTO") { startY ->
            paintText.textSize = 9.5f
            paintText.color = Color.parseColor("#0B1220")

            paintText.isFakeBoldText = true
            canvas.drawText("Mão de Obra:", left + 12f, startY + 16f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(Formatters.formatCurrency(workOrder.laborPrice), left + 90f, startY + 16f, paintText)

            paintText.isFakeBoldText = true
            canvas.drawText("Peças:", left + 180f, startY + 16f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(Formatters.formatCurrency(workOrder.partsPrice), left + 225f, startY + 16f, paintText)

            paintText.isFakeBoldText = true
            canvas.drawText("Desconto:", left + 300f, startY + 16f, paintText)
            paintText.isFakeBoldText = false
            canvas.drawText(Formatters.formatCurrency(workOrder.discountAmount), left + 360f, startY + 16f, paintText)

            // Total highlight
            paintText.isFakeBoldText = true
            paintText.textSize = 11f
            paintText.color = Color.parseColor("#1769E0")
            canvas.drawText("VALOR TOTAL: ${Formatters.formatCurrency(workOrder.totalAmount)}", left + 12f, startY + 36f, paintText)

            paintText.textSize = 9.5f
            paintText.color = Color.parseColor("#059669")
            canvas.drawText("Sinal Pago: ${Formatters.formatCurrency(workOrder.downPaymentAmount)}", left + 220f, startY + 36f, paintText)

            paintText.color = if (workOrder.remainingBalance > 0) Color.parseColor("#DC2626") else Color.parseColor("#059669")
            canvas.drawText("Saldo Pendente: ${Formatters.formatCurrency(workOrder.remainingBalance)}", left + 350f, startY + 36f, paintText)

            paintText.color = Color.parseColor("#475569")
            paintText.textSize = 8.5f
            paintText.isFakeBoldText = false
            canvas.drawText("Dados para Pagamento: ${company.paymentInstructions}", left + 12f, startY + 52f, paintText)

            60f
        }

        y += 8f

        // 6. Brazilian Consumer Law (CDC) and Terms Box
        y = drawSectionBox(canvas, left, right, y, "TERMOS DE GARANTIA & LEGISLAÇÃO (CDC)") { startY ->
            paintText.color = Color.parseColor("#334155")
            paintText.textSize = 8f
            paintText.isFakeBoldText = false

            val warrantyText = warranty?.termsText ?: company.warrantyTerms
            canvas.drawText("1. ${warrantyText.take(110)}", left + 12f, startY + 14f, paintText)
            if (warrantyText.length > 110) {
                canvas.drawText("   ${warrantyText.substring(110).take(110)}", left + 12f, startY + 25f, paintText)
            }

            canvas.drawText("2. Equipamentos retirados devem ser conferidos pelo cliente no ato da entrega.", left + 12f, startY + 37f, paintText)
            canvas.drawText("3. Prazo de validade do orçamento: 10 dias corridos. Técnico Responsável: ${company.technicalManager}.", left + 12f, startY + 49f, paintText)

            58f
        }

        y += 14f

        // 7. Signatures Area
        paintBg.color = Color.parseColor("#94A3B8")
        paintBg.strokeWidth = 1f
        paintBg.style = Paint.Style.STROKE

        val colWidth = (right - left - 40f) / 2f

        // Tech Signature line
        canvas.drawLine(left + 10f, y + 36f, left + 10f + colWidth, y + 36f, paintBg)
        paintText.color = Color.parseColor("#0B1220")
        paintText.textSize = 8.5f
        paintText.isFakeBoldText = true
        val sig1 = company.technicalManager.ifBlank { "Técnico Responsável" }
        canvas.drawText(sig1, left + 10f + (colWidth - paintText.measureText(sig1)) / 2f, y + 48f, paintText)
        paintText.isFakeBoldText = false
        paintText.textSize = 7.5f
        paintText.color = Color.parseColor("#64748B")
        val sub1 = company.tradeName
        canvas.drawText(sub1, left + 10f + (colWidth - paintText.measureText(sub1)) / 2f, y + 58f, paintText)

        // Customer Signature line
        val custLeft = left + 30f + colWidth
        canvas.drawLine(custLeft, y + 36f, custLeft + colWidth, y + 36f, paintBg)
        paintText.color = Color.parseColor("#0B1220")
        paintText.textSize = 8.5f
        paintText.isFakeBoldText = true
        val sig2 = customer?.name ?: "Assinatura do Cliente"
        canvas.drawText(sig2, custLeft + (colWidth - paintText.measureText(sig2)) / 2f, y + 48f, paintText)
        paintText.isFakeBoldText = false
        paintText.textSize = 7.5f
        paintText.color = Color.parseColor("#64748B")
        val sub2 = "De acordo com os termos e serviços executados"
        canvas.drawText(sub2, custLeft + (colWidth - paintText.measureText(sub2)) / 2f, y + 58f, paintText)

        // Footer note
        paintText.textSize = 7.5f
        paintText.color = Color.parseColor("#94A3B8")
        val footerText = "Documento emitido eletronicamente via TecnoOrdem Pro • ${Formatters.formatDateTime(System.currentTimeMillis())}"
        val footWidth = paintText.measureText(footerText)
        canvas.drawText(footerText, (PAGE_WIDTH - footWidth) / 2f, PAGE_HEIGHT - 20f, paintText)
    }

    private inline fun drawSectionBox(
        canvas: Canvas,
        left: Float,
        right: Float,
        top: Float,
        title: String,
        contentDrawer: (startY: Float) -> Float
    ): Float {
        val paintHeaderBg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F1F5F9") }
        val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#CBD5E1")
            style = Paint.Style.STROKE
            strokeWidth = 0.8f
        }
        val paintTitle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0F172A")
            textSize = 9f
            isFakeBoldText = true
        }

        val headerHeight = 16f
        val startContentY = top + headerHeight
        val contentHeight = contentDrawer(startContentY)
        val totalBoxHeight = headerHeight + contentHeight + 4f

        // Draw Box background & border
        canvas.drawRoundRect(RectF(left, top, right, top + totalBoxHeight), 4f, 4f, paintBorder)

        // Header strip
        canvas.drawRoundRect(RectF(left, top, right, top + headerHeight), 4f, 4f, paintHeaderBg)
        canvas.drawText(title, left + 8f, top + 12f, paintTitle)

        return top + totalBoxHeight
    }
}
