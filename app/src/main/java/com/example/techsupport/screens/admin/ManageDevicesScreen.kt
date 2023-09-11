package com.example.techsupport.screens.admin

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.techsupport.Constants
import com.example.techsupport.Utils
import com.example.techsupport.components.LoadingAnimation
import com.example.techsupport.models.Device
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageDevicesScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var isLoading1 by remember { mutableStateOf(false) }
    var isLoading2 by remember { mutableStateOf(false) }
    var devices by remember { mutableStateOf(listOf<Device>()) } // Mutable state list for devices

    // Fetch devices from Firebase
    LaunchedEffect(Unit) {
        isLoading2 = true
        Utils.getDatabaseReference().reference.child(Constants.DEVICES_DB)
            .get()
            .addOnSuccessListener { snapshot ->
                isLoading2 = false
                devices = snapshot.children.mapNotNull { it.getValue(Device::class.java) }
            }
            .addOnFailureListener {
                isLoading2 = false
                Toast.makeText(context, "Failed to fetch devices", Toast.LENGTH_SHORT).show()
            }
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Manage Devices")
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = {
                        showDialog = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add a device"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Devices List

            if (isLoading2)
                LoadingAnimation(modifier)
            else
                if (devices.isEmpty()) {
                    Text(text = "No devices found.", modifier = Modifier.padding(16.dp))
                } else {
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        items(devices.size) { index ->
                            val device = devices[devices.size - 1 - index]

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Name: ${device.name}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Created At: ${Date(device.createdAt)}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }

            if (showDialog) {
                AddDeviceDialog(
                    isLoading = isLoading1,
                    onDismiss = {
                        if (!isLoading1) showDialog = false
                    },
                    onAddDevice = { deviceName ->
                        isLoading1 = true
                        val device = Device(
                            id = Utils.getUniqueId(),
                            name = deviceName,
                            createdAt = Date().time
                        )

                        Utils.getDatabaseReference().reference.child(Constants.DEVICES_DB)
                            .child(device.id).setValue(device)
                            .addOnSuccessListener {
                                isLoading1 = false
                                Toast.makeText(
                                    context,
                                    "Device added successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showDialog = false
                            }
                            .addOnFailureListener {
                                isLoading1 = false
                                Toast.makeText(
                                    context,
                                    "Failed to add device. Kindly try again later",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                )
            }
        }
    }
}

@Composable
fun AddDeviceDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onAddDevice: (String) -> Unit,
) {
    var deviceName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Add a Device", style = MaterialTheme.typography.bodyLarge)
        },
        text = {
            Column {
                Text(text = "Enter the name of the device to add:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = deviceName,
                    onValueChange = {
                        deviceName = it
                        errorMessage = ""
                    },
                    label = { Text(text = "Device Name") },
                    isError = errorMessage.isNotEmpty(),
                    enabled = !isLoading // Disable input when loading
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (deviceName.isBlank()) {
                        errorMessage = "Device name cannot be empty."
                    } else {
                        onAddDevice(deviceName)
                    }
                },
                enabled = !isLoading // Disable button when loading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp), // Smaller size to fit inside the button
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary // Matches button text color
                    )
                } else {
                    Text(text = "Add")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        }
    )
}
