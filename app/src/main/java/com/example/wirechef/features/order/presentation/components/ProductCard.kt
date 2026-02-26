package com.example.wirechef.features.order.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wirechef.features.product.domain.entities.Product

@Composable
fun ProductCard(product: Product, onAddClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = product.description, color = Color.Gray, fontSize = 14.sp)
            }

            Text(
                text = "$${product.price}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .size(34.dp)
                    .background(Color.Transparent, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .padding(2.dp)
                )
            }
        }
    }
}