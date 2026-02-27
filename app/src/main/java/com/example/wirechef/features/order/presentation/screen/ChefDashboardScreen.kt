package com.example.wirechef.features.order.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wirechef.features.order.presentation.viewmodels.ChefViewModel

@Composable
fun ChefDashboardScreen(
    viewModel: ChefViewModel = hiltViewModel(),
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutClick()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD54F))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { /* TODO: Lógica de perfil si aplica */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = "Perfil", modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adrian", color = Color.White, fontSize = 14.sp)
            }

            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir", modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salir", color = Color.White, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay órdenes pendientes.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.orders) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mesa ${order.tableNumber}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                Surface(
                                    color = Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    // Traducción visual del estado original
                                    val statusText = when(order.status) {
                                        "pending" -> "En espera"
                                        "preparing" -> "Preparando"
                                        "ready" -> "Listo"
                                        else -> order.status
                                    }
                                    Text(
                                        text = statusText,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            order.items.forEachIndexed { index, item ->
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Producto ${item.productId} x${item.quantity}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black
                                        )
                                    }

                                    if (item.notes.isNotBlank()) {
                                        Text(
                                            text = "Nota: ${item.notes}",
                                            color = MaterialTheme.colorScheme.error,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }

                                    HorizontalDivider(
                                        color = if (index == 0) Color(0xFF2196F3) else Color.Black,
                                        thickness = 1.dp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (order.status == "pending") {
                                    OutlinedButton(
                                        onClick = { viewModel.updateOrderStatus(order.id, "preparing") },
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text("Preparar")
                                    }
                                }
                                Button(
                                    onClick = { viewModel.updateOrderStatus(order.id, "ready") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                                ) {
                                    Text("Listo para entregar", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}