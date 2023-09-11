package com.example.techsupport.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.techsupport.Constants
import com.example.techsupport.R
import com.example.techsupport.Screen
import com.example.techsupport.Utils
import com.example.techsupport.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current

    // State variables for the input fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isTermsAccepted by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(stringResource(id = R.string.create_account))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Icon"
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .width(125.dp)
                        .border(2.dp, Color.Black, CircleShape),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Fit,
                    alpha = DefaultAlpha,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 30.sp,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.slogan),
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(20.dp))
                // Input Fields
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(text = stringResource(id = R.string.first_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(painter = painterResource(R.drawable.user), contentDescription = null)
                    }
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(text = stringResource(id = R.string.last_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(painter = painterResource(R.drawable.user), contentDescription = null)
                    }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = stringResource(id = R.string.email)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.envelope),
                            contentDescription = null
                        )
                    }
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = stringResource(id = R.string.password)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.lock_closed),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            val visibilityIcon = if (passwordVisible) {
                                Icons.Filled.Visibility
                            } else {
                                Icons.Filled.VisibilityOff
                            }
                            Icon(imageVector = visibilityIcon, contentDescription = null)
                        }
                    },
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    }
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(text = stringResource(id = R.string.confirm_password)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.lock_closed),
                            contentDescription = null
                        )
                    },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isTermsAccepted,
                        onCheckedChange = { isTermsAccepted = it }
                    )
                    Text(
                        text = stringResource(id = R.string.terms_and_conditions),
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.ScTermsAndConditions.route)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Register Button
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        when {
                            firstName.isEmpty() -> Toast.makeText(
                                context,
                                "Please enter your first name.",
                                Toast.LENGTH_SHORT
                            ).show()

                            lastName.isEmpty() -> Toast.makeText(
                                context,
                                "Please enter your last name.",
                                Toast.LENGTH_SHORT
                            ).show()

                            email.isEmpty() -> Toast.makeText(
                                context,
                                "Please enter your email.",
                                Toast.LENGTH_SHORT
                            ).show()

                            password.isEmpty() -> Toast.makeText(
                                context,
                                "Please enter a password.",
                                Toast.LENGTH_SHORT
                            ).show()

                            password != confirmPassword -> Toast.makeText(
                                context,
                                "Passwords do not match.",
                                Toast.LENGTH_SHORT
                            ).show()

                            !isTermsAccepted -> Toast.makeText(
                                context,
                                "Please accept the terms and conditions.",
                                Toast.LENGTH_SHORT
                            ).show()

                            else -> {
                                isLoading = true
                                registerUser(
                                    firstName = firstName,
                                    lastName = lastName,
                                    email = email,
                                    password = password,
                                    onSuccess = {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Welcome, $firstName!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate(Screen.ScMain.route) {
                                            popUpTo(Screen.ScRegister.route) { inclusive = true }
                                        }
                                    },
                                    onError = { errorMessage ->
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Register")
                    }
                }
            }
        }
    }
}

fun registerUser(
    firstName: String,
    lastName: String,
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    Utils.getAuth().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                val createdAt = Utils.getDate()
                // Create a User instance
                val user = User(
                    id = userId,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    createdAt = createdAt,
                )

                Utils.getDatabaseReference().reference.child(Constants.USERS_DB).child(userId)
                    .setValue(user)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            onSuccess()
                        } else {
                            onError("Failed to save user to database.")
                        }
                    }
            } else {
                onError(task.exception?.message ?: "Registration failed.")
            }
        }
}
