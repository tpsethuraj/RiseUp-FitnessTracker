package com.fitnesstracker.ui.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
fun SignUpScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
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
                            "Create Account",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            "Sign up to get started",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !loading
                        )
                        Spacer(modifier = Modifier.height(16.dp))
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
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val image = if (confirmPasswordVisible)
                                    Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff

                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }, enabled = !loading) {
                                    Icon(imageVector = image, contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password")
                                }
                            },
                            enabled = !loading
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                                    scope.launch { snackbarHostState.showSnackbar("Please fill all fields") }
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    scope.launch { snackbarHostState.showSnackbar("Passwords do not match") }
                                    return@Button
                                }
                                loading = true
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val user = hashMapOf(
                                                "name" to name,
                                                "email" to email
                                            )
                                            FirebaseFirestore.getInstance().collection("users").document(task.result?.user?.uid ?: "")
                                                .set(user)
                                                .addOnSuccessListener {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Sign up successful!")
                                                        navController.navigate("login") { popUpTo("signup") { inclusive = true } }
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Error: ${e.message}")
                                                    }
                                                }
                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Sign up failed: ${task.exception?.message}")
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
                                Text("SIGN UP")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already have an account? ", color = Color.Gray)
                    TextButton(
                        onClick = { navController.navigate("login") { popUpTo("signup") { inclusive = true } } },
                        enabled = !loading
                    ) {
                        Text("Login", color = MaterialTheme.colors.primary)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    FitnessTrackerTheme {
        SignUpScreen(navController = rememberNavController())
    }
}
