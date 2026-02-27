package com.example.wirechef.features.user.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wirechef.features.user.presentation.viewmodels.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (role: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Variable para evitar doble navegación
    var hasNavigated by remember { mutableStateOf(false) }

    // Usamos el uiState entero como llave, o mejor aún, observamos específicamente loggedUser
    LaunchedEffect(uiState.loggedUser) {
        if (uiState.loggedUser != null && !hasNavigated) {
            hasNavigated = true // Marcamos como navegado

            uiState.welcomeMessage?.let { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }

            // Le damos un pequeñísimo delay opcional para que el Toast se alcance a mostrar
            delay(100)

            // Llamamos al callback de navegación usando el role exacto del objeto user
            onLoginSuccess(uiState.loggedUser!!.role)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WaiterChef",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Selecciona tu rol para continuar",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = uiState.nameInput,
            onValueChange = { viewModel.onNameChanged(it) },
            placeholder = { Text("Ingresa tu nombre") },
            modifier = Modifier
                .width(260.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2A2A2A),
                unfocusedContainerColor = Color(0xFF2A2A2A),
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            )
        )

        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Button(
                    // Aquí envías "waiter" explícitamente al ViewModel
                    onClick = { viewModel.login("waiter") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Mesero", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    // Aquí envías "chef" explícitamente al ViewModel
                    onClick = { viewModel.login("chef") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Cocinero", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}