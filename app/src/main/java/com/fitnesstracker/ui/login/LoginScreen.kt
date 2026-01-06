package com.fitnesstracker.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fitnesstracker.ui.theme.FitnessTrackerTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = 8.dp,
                    backgroundColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Welcome Back",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            "Sign in to access your account",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            enabled = !loading
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val image = if (passwordVisible)
                                    Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff

                                IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !loading) {
                                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                                }
                            },
                            enabled = !loading
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                if (email.isBlank() || password.isBlank()) {
                                    scope.launch { snackbarHostState.showSnackbar("Enter email & password") }
                                    return@Button
                                }
                                loading = true
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val userId = task.result?.user?.uid ?: ""
                                            FirebaseFirestore.getInstance().collection("users").document(userId).get()
                                                .addOnSuccessListener { document ->
                                                    val username = document.getString("name") ?: ""
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Successfully signed in!")
                                                        navController.navigate("home/$username") {
                                                            popUpTo("login") { inclusive = true }
                                                            launchSingleTop = true
                                                        }
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Error: ${e.message}")
                                                    }
                                                }
                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Login failed: ${task.exception?.message}")
                                            }
                                        }
                                        loading = false
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !loading
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colors.onPrimary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text("LOGIN")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Don't have an account? ", color = Color.Gray)
                    TextButton(
                        onClick = { navController.navigate("signup") },
                        enabled = !loading
                    ) {
                        Text("SignUp", color = MaterialTheme.colors.primary)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FitnessTrackerTheme {
        LoginScreen(navController = rememberNavController())
    }
}
