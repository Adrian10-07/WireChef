package com.example.wirechef.features.order.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wirechef.features.order.domain.entities.Order

@Composable
fun WaiterOrderCard(order: Order) {
    // Colores basados en test.html
    val (statusText, statusBgColor, statusTextColor) = when (order.status) {
        "pending" -> Triple("En espera", Color(0xFFFFF3CD), Color(0xFF856404))
        "preparing" -> Triple("Preparando", Color(0xFFD1ECF1), Color(0xFF0C5460))
        "ready" -> Triple("Listo", Color(0xFFD4EDDA), Color(0xFF155724))
        else -> Triple(order.status, Color.LightGray, Color.DarkGray)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Mesa ${order.tableNumber}", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Box(
                    modifier = Modifier
                        .background(statusBgColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = statusText, color = statusTextColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            order.items.forEach { item ->
                Text(text = "Producto ID: ${item.productId} x${item.quantity}", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}