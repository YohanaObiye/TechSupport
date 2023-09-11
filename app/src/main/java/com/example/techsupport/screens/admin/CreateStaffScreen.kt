package com.example.techsupport.screens.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.techsupport.Constants
import com.example.techsupport.Utils
import com.example.techsupport.models.User
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStaffScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current

    // State variables for the input fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var categories by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }

    // Fetch device categories from Firebase
    LaunchedEffect(Unit) {
        fetchDeviceCategoriesFromFirebase { result ->
            categories = result
        }
        Toast.makeText(context, "Size: ${categories.size}", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(

                title = { Text("Create Staff") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back Icon")
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
                // Input fields for staff creation
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown for device categories
                Text(text = "Category")
                var expanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select a Category") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { expanded = !expanded }
                            )
                        }
                    )
                    if (categories.isEmpty())
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .padding(24.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(size = 24.dp))
                        }
                    else
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Register button
                Button(
                    onClick = {
                        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || selectedCategory.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Please fill in all fields.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            isLoading = true
                            createStaff(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                category = selectedCategory,
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        "Staff created successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigateUp()
                                },
                                onError = { errorMessage ->
                                    isLoading = false
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Create Staff")
                    }
                }
            }
        }
    }
}

// Firebase function to fetch device categories
fun fetchDeviceCategoriesFromFirebase(onResult: (List<String>) -> Unit) {
    Utils.getDatabaseReference().getReference(Constants.DEVICES_DB).get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Categories", task.result.toString())
                val categories =
                    task.result.children.mapNotNull { it.child("name").value as? String }
                Log.d("Categories", categories.distinct().toString())

                onResult(categories.distinct())
            } else {
                onResult(emptyList())
            }
        }
}

fun createStaff(
    firstName: String,
    lastName: String,
    email: String,
    category: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val defaultPassword = "pass!@#$"

    // Create a new user in Firebase Authentication
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, defaultPassword)
        .addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                // Get the generated user ID
                val userId = authTask.result?.user?.uid ?: return@addOnCompleteListener

                // Create a new User object
                val staff = User(
                    id = userId,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    createdAt = Date().time,
                    category = category,
                    role = "staff"
                )

                // Save the staff details to the database
                Utils.getDatabaseReference().getReference(Constants.USERS_DB).child(userId)
                    .setValue(staff)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            onSuccess()
                        } else {
                            onError(dbTask.exception?.message ?: "Failed to save staff details.")
                        }
                    }
            } else {
                onError(authTask.exception?.message ?: "Failed to create staff authentication.")
            }
        }
}
