package com.example.wirechef.features.order.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wirechef.features.order.presentation.viewmodels.CartItem

@Composable
fun CartItemRow(item: CartItem, onNoteChange: (String) -> Unit, onRemove: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${item.quantity}x ${item.product.name}",
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = Color(0xFFDD4444))
            }
        }

        OutlinedTextField(
            value = item.note,
            onValueChange = onNoteChange,
            placeholder = { Text("Nota (ej. sin cebolla)", color = Color.Gray, fontSize = 12.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = Color(0xFF2A2A2A),
                unfocusedContainerColor = Color(0xFF2A2A2A),
                unfocusedBorderColor = Color(0xFF444444),
                focusedBorderColor = MaterialTheme.colorScheme.secondary
            )
        )
    }
}