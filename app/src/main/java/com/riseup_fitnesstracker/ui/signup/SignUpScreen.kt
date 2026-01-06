package com.riseup_fitnesstracker.ui.signup

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.riseup_fitnesstracker.R
import com.riseup_fitnesstracker.ui.theme.FitnessTrackerTheme
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
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (!isPreview) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            val user = task.result?.user
                            val userData = hashMapOf(
                                "name" to (user?.displayName ?: "User"),
                                "email" to user?.email
                            )
                            FirebaseFirestore.getInstance().collection("users").document(user!!.uid).set(userData)
                                .addOnSuccessListener {
                                    navController.navigate("home/${user.displayName}")
                                }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Google Sign-In failed.")
                            }
                        }
                    }

                } catch (e: ApiException) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Google Sign-In failed: ${e.localizedMessage}")
                    }
                }
            }
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = 8.dp,
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Create Account",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !loading
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !loading
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, null) },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !loading
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, null) },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !loading
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !loading,
                            onClick = {

                                if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Please fill all fields")
                                    }
                                    return@Button
                                }

                                if (password != confirmPassword) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Passwords do not match")
                                    }
                                    return@Button
                                }

                                if (!isPreview) {
                                    loading = true
                                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                                                val userData = hashMapOf(
                                                    "name" to name,
                                                    "email" to email
                                                )

                                                FirebaseFirestore.getInstance().collection("users").document(userId)
                                                    .set(userData)
                                                    .addOnSuccessListener {
                                                        loading = false
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar("Sign up successful!")
                                                            navController.navigate("login") {
                                                                popUpTo("signup") { inclusive = true }
                                                            }
                                                        }
                                                    }
                                                    .addOnFailureListener {
                                                        loading = false
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar("Failed to save user data")
                                                        }
                                                    }

                                            } else {
                                                loading = false
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        task.exception?.message ?: "Signup failed"
                                                    )
                                                }
                                            }
                                        }
                                }
                            }
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = MaterialTheme.colors.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("SIGN UP")
                            }
                        }

                        Row(modifier = Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Divider(modifier = Modifier.weight(1f))
                            Text("OR", modifier = Modifier.padding(horizontal = 8.dp))
                            Divider(modifier = Modifier.weight(1f))
                        }

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface, contentColor = MaterialTheme.colors.onSurface),
                            onClick = { 
                                if (!isPreview) {
                                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken(context.getString(R.string.default_web_client_id))
                                        .requestEmail()
                                        .build()

                                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                                }
                             }
                        ) {
                            Icon(painter = painterResource(id = R.drawable.ic_google_logo), contentDescription = "Google sign-in", modifier = Modifier.size(24.dp))
                            Text("Sign up with Google", modifier = Modifier.padding(start = 16.dp))
                        }

                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already have an account? ", color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
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
