package com.example.wirechef.features.order.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
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
import com.example.wirechef.features.order.presentation.components.CartItemRow
import com.example.wirechef.features.order.presentation.components.ProductCard
import com.example.wirechef.features.order.presentation.components.WaiterOrderCard
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
            Toast.makeText(context, "Pedido enviado a cocina", Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }
    }
    LaunchedEffect(uiState.orderSendError) {
        uiState.orderSendError?.let {
            Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD54F))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = "Perfil", modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(uiState.waiterName, color = Color.White, fontSize = 14.sp)
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

        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.secondary
        ) {
            Tab(
                selected = uiState.selectedTab == 0,
                onClick = { viewModel.selectTab(0) },
                text = { Text("Nuevo Pedido", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = uiState.selectedTab == 1,
                onClick = { viewModel.selectTab(1) },
                text = { Text("Mis Pedidos", fontWeight = FontWeight.Bold) }
            )
        }

        if (uiState.selectedTab == 0) {
            MenuAndCartSection(uiState, viewModel)
        } else {
            MyOrdersSection(uiState)
        }
    }
}

@Composable
fun MenuAndCartSection(
    uiState: com.example.wirechef.features.order.presentation.viewmodels.WaiterState,
    viewModel: WaiterViewModel
) {
    val categorias = listOf("comidas", "bebidas", "postres")

    Column(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.weight(1f)) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categorias) { cat ->
                    val isSelected = uiState.selectedCategory == cat
                    Button(
                        onClick = { viewModel.selectCategory(cat) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(cat.replaceFirstChar { it.uppercase() })
                    }
                }
            }

            // Lista de Productos
            if (uiState.isLoadingProducts) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.productsError != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${uiState.productsError}", color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.products) { product ->
                        ProductCard(
                            product = product,
                            onAddClick = { viewModel.addToCart(product, "") }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Column {
                Text("Pedido Actual", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 8.dp))

                if (uiState.cart.isEmpty()) {
                    Text("No hay productos", color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 180.dp)) {
                        items(uiState.cart, key = { it.product.id }) { cartItem ->
                            CartItemRow(
                                item = cartItem,
                                onNoteChange = { newNote ->
                                    viewModel.updateCartItemNote(cartItem.product, newNote)
                                },
                                onRemove = { viewModel.removeFromCart(cartItem) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.tableNumberInput,
                        onValueChange = { viewModel.updateTableNumber(it) },
                        label = { Text("Mesa") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(100.dp),
                        singleLine = true
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total:", color = Color.Gray, fontSize = 14.sp)
                        Text(
                            text = "$${uiState.cartTotal}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.sendOrder() },
                    enabled = !uiState.isSendingOrder && uiState.cart.isNotEmpty(),
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

@Composable
fun MyOrdersSection(uiState: com.example.wirechef.features.order.presentation.viewmodels.WaiterState) {
    if (uiState.isLoadingOrders) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (uiState.myOrders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aún no tienes pedidos activos.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.myOrders.sortedByDescending { it.id }) { order ->
                WaiterOrderCard(order = order)
            }
        }
    }
}