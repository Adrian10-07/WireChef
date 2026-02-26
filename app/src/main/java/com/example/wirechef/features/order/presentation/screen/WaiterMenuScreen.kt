package com.example.wirechef.features.order.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.wirechef.features.order.presentation.components.ProductCard
import com.example.wirechef.features.order.presentation.components.CartItemRow
import com.example.wirechef.features.order.presentation.viewmodels.WaiterViewModel

@Composable
fun WaiterMenuScreen(
    viewModel: WaiterViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogout()
        }
    }

    LaunchedEffect(uiState.orderSendSuccess) {
        if (uiState.orderSendSuccess) {
            Toast.makeText(context, "¡Pedido enviado a cocina!", Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }
    }

    // Mostrar errores de envío como Toast
    LaunchedEffect(uiState.orderSendError) {
        uiState.orderSendError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text("Mesero: ${uiState.waiterName}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            OutlinedButton(
                onClick = { viewModel.logout() },
                border = BorderStroke(1.dp, Color(0xFF555555)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Salir", fontSize = 14.sp)
            }
        }

        val categories = listOf("comidas", "bebidas", "postres")
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isActive = uiState.selectedCategory == category
                Button(
                    onClick = { viewModel.selectCategory(category) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) MaterialTheme.colorScheme.secondary else Color.Transparent,
                        contentColor = if (isActive) Color.White else MaterialTheme.colorScheme.onBackground
                    ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(category.uppercase(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            if (uiState.isLoadingProducts) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.productsError != null) {
                Text(
                    text = uiState.productsError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.products) { product ->
                        ProductCard(
                            product = product,
                            onAddClick = { viewModel.addToCart(product) }
                        )
                    }
                }
            }
        }

        if (uiState.cart.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tu Pedido",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = uiState.tableNumberInput,
                        onValueChange = { viewModel.updateTableNumber(it) },
                        placeholder = { Text("Mesa #", color = Color.Gray, fontSize = 14.sp) },
                        modifier = Modifier.width(100.dp).height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.cart) { cartItem ->
                        CartItemRow(
                            item = cartItem,
                            onNoteChange = { newNote -> viewModel.updateCartItemNote(cartItem.product, newNote) },
                            onRemove = { viewModel.removeFromCart(cartItem.product) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total:", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = "$${uiState.cartTotal}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.sendOrder() },
                    enabled = !uiState.isSendingOrder,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (uiState.isSendingOrder) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Enviar a Cocina", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}