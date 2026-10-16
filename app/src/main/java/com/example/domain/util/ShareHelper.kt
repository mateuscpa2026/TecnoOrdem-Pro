package com.example.domain.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.Customer
import com.example.data.model.Equipment
import com.example.data.model.WorkOrder
import java.io.File

object ShareHelper {

    fun shareDocument(
        context: Context,
        pdfFile: File,
        kind: DocumentKind,
        workOrder: WorkOrder,
        customer: Customer?,
        equipment: Equipment?
    ) {
        try {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val customerName = customer?.name?.split(" ")?.firstOrNull() ?: "Cliente"
            val equipModel = "${equipment?.brand ?: ""} ${equipment?.model ?: ""}".trim().ifEmpty { "equipamento" }

            val message = when (kind) {
                DocumentKind.WORK_ORDER -> {
                    "Olá, $customerName! Sua ordem de serviço nº ${workOrder.orderNumber} para o $equipModel foi registrada na nossa assistência técnica. Você pode consultar os detalhes no documento anexo em PDF."
                }
                DocumentKind.CHECK_IN_RECEIPT -> {
                    "Olá, $customerName! Segue o comprovante de entrada do seu $equipModel (OS nº ${workOrder.orderNumber}) com o checklist inicial de recepção."
                }
                DocumentKind.QUOTE -> {
                    "Olá, $customerName! O orçamento do seu equipamento $equipModel (OS nº ${workOrder.orderNumber}) está disponível para sua avaliação. O documento anexo contém os serviços propostos e os respectivos valores."
                }
                DocumentKind.DELIVERY_RECEIPT -> {
                    "Olá, $customerName! Seu equipamento $equipModel está pronto para retirada na nossa assistência técnica. Segue o termo de entrega e recibo da OS nº ${workOrder.orderNumber}."
                }
                DocumentKind.WARRANTY_CERTIFICATE -> {
                    "Olá, $customerName! Segue o Certificado de Garantia da OS nº ${workOrder.orderNumber} referente aos serviços e peças do $equipModel, nos termos do Art. 26 do CDC."
                }
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "${kind.docTitle} - OS #${workOrder.orderNumber}")
                putExtra(Intent.EXTRA_TEXT, message)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Compartilhar documento...")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao compartilhar documento: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
