package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Warranty
import com.example.domain.util.Formatters
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusCriticalBg
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.viewmodel.MainViewModel

@Composable
fun WarrantiesScreen(
    viewModel: MainViewModel
) {
    val warranties by viewModel.allWarranties.collectAsState()
    val activeCount by viewModel.activeWarrantiesCount.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("warranties_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // CDC Legal Information Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = BluePrimary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Garantia Legal & Lei nº 8.078/1990 (CDC)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Proteção ao consumidor e conformidade técnica",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• Art. 26 do CDC: Estabelece prazo decadencial de 90 (noventa) dias para reclamação de vícios em produtos e serviços duráveis (celulares, notebooks, computadores, etc.).\n" +
                                "• A contagem inicia-se na entrega efetiva do equipamento reparado.\n" +
                                "• A garantia abrange a mão de obra técnica executada e as peças substituídas pela assistência.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Garantias Registradas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    color = StatusSuccessBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$activeCount Ativa(s)",
                        color = StatusSuccess,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (warranties.isEmpty()) {
            item {
                EmptyStateView(
                    icon = Icons.Default.Security,
                    title = "Nenhuma garantia ativa",
                    message = "As garantias legais de 90 dias são emitidas automaticamente quando uma ordem de serviço é marcada como 'Entregue'."
                )
            }
        } else {
            items(warranties, key = { it.id }) { warranty ->
                WarrantyCard(warranty = warranty)
            }
        }
    }
}

@Composable
fun WarrantyCard(
    warranty: Warranty
) {
    val now = System.currentTimeMillis()
    val isExpired = now > warranty.endDate
    val daysRemaining = if (isExpired) 0 else ((warranty.endDate - now) / (24 * 60 * 60 * 1000L)).toInt()

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = warranty.type.titlePt,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    color = if (isExpired) StatusCriticalBg else StatusSuccessBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isExpired) "Vencida" else "$daysRemaining dias restantes",
                        color = if (isExpired) StatusCritical else StatusSuccess,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = "Vigência: ${Formatters.formatDate(warranty.startDate)} até ${Formatters.formatDate(warranty.endDate)} (${warranty.daysValid} dias)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Peças Cobertas: ${warranty.coveredParts}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Serviços Cobertos: ${warranty.coveredServices}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = warranty.termsText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}
