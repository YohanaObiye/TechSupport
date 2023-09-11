package com.example.techsupport.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.techsupport.Constants
import com.example.techsupport.Utils
import com.example.techsupport.components.ImageSelectionDialog
import com.example.techsupport.models.Device
import com.example.techsupport.models.KB
import kotlinx.coroutines.tasks.await
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateKnowledgeBase(modifier: Modifier = Modifier, navController: NavHostController) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var content by remember { mutableStateOf(TextFieldValue("")) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<Device?>(null) }
    var isDialogOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Knowledge Base") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selected Image Preview (if available)
            if (selectedImageUrl != null) {
                AsyncImage(
                    model = selectedImageUrl,
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            }

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Category Dropdown
            DeviceDropdown(
                fetchDevices = { fetchDevices() },
                onDeviceSelected = { selectedDevice ->
                    selectedCategory = selectedDevice
                }
            )

            // Content Input
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )

            // Select Image Button
            Button(
                onClick = {
                    if (title.text.isBlank()) {
                        // Toast if the title is empty
                        Toast.makeText(
                            context,
                            "Please add the title first",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Show dialog to select image
                        isDialogOpen = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Image")
            }

            // Define the loading state
            var isLoading by remember { mutableStateOf(false) }

// Save Knowledge Base Button
            Button(
                onClick = {
                    if (isLoading) return@Button // Prevent clicks when already loading

                    // Validate the inputs
                    if (title.text.isBlank()) {
                        Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedImageUrl.isNullOrEmpty()) {
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedCategory == null) {
                        Toast.makeText(context, "Please select a category", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    if (content.text.isBlank()) {
                        Toast.makeText(context, "Content cannot be empty", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    // Set loading state to true
                    isLoading = true

                    // Create a Knowledge Base object using the KB data class
                    val knowledgeBase = KB(
                        id = "", // Firebase will generate the ID, so leave it empty for now
                        title = title.text,
                        category = selectedCategory!!.name,
                        content = content.text,
                        imageUrl = selectedImageUrl!!,
                        createdAt = System.currentTimeMillis()
                    )

                    // Save the object to Firebase
                    val databaseRef =
                        Utils.getDatabaseReference().getReference(Constants.KNOWLEDGE_BASE_DB)
                    databaseRef.push().setValue(knowledgeBase)
                        .addOnSuccessListener {
                            // Success message
                            Toast.makeText(
                                context,
                                "Knowledge Base saved successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener { exception ->
                            // Handle error
                            Toast.makeText(
                                context,
                                "Error: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnCompleteListener {
                            // Reset loading state
                            isLoading = false
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // Disable the button when loading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Knowledge Base")
                }
            }

        }

        // Dialog to select image
        if (isDialogOpen) {
            ImageSelectionDialog(
                title = title.text,
                onImageSelected = { url ->
                    selectedImageUrl = url
                    isDialogOpen = false
                },
                onDismiss = { isDialogOpen = false }
            )
        }
    }
}


@Composable
fun DeviceDropdown(
    fetchDevices: suspend () -> List<Device>, // Method to fetch devices
    onDeviceSelected: (Device) -> Unit // Callback when a device is selected
) {
    var devices by remember { mutableStateOf<List<Device>>(emptyList()) }
    var selectedDevice by remember { mutableStateOf<Device?>(null) }
    var expanded by remember { mutableStateOf(false) }

    // Fetch devices and select the first one automatically
    LaunchedEffect(Unit) {
        devices = fetchDevices()
        if (devices.isNotEmpty()) {
            selectedDevice = devices.first()
            onDeviceSelected(selectedDevice!!)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Display the selected device in a button-like dropdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    RoundedCornerShape(4.dp)
                )
                .clickable { expanded = true }
                .padding(16.dp)
        ) {
            Text(
                text = selectedDevice?.name ?: "Select a Device",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Dropdown menu to list all devices
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            devices.forEach { device ->
                DropdownMenuItem(
                    onClick = {
                        selectedDevice = device
                        expanded = false
                        onDeviceSelected(device) // Notify the parent of the selection
                    },
                    text = { Text(text = device.name) },
                )
            }
        }
    }
}

suspend fun fetchDevices(): List<Device> {
    val devices = mutableListOf<Device>()

    try {
        // Fetch the reference to the devices database
        val devicesRef = Utils.getDatabaseReference().getReference(Constants.DEVICES_DB)

        // Use `get()` to fetch the data once
        val snapshot = devicesRef.get().await()

        // Iterate through the children and map them to the `Device` data class
        snapshot.children.forEach { child ->
            val device = child.getValue(Device::class.java)
            device?.let { devices.add(it) }
        }
    } catch (e: Exception) {
        e.printStackTrace() // Log or handle exceptions appropriately
    }

    return devices
}
