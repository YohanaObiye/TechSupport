package com.example.techsupport.screens.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.techsupport.R
import com.example.techsupport.Utils
import com.example.techsupport.models.Device

@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val deviceList = remember { mutableStateListOf<Device>() }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val databaseRef = Utils.getDatabaseReference().getReference("devices")
        databaseRef.get().addOnSuccessListener { snapshot ->
            snapshot.children.mapNotNull { it.getValue(Device::class.java) }.let { list ->
                deviceList.clear()
                deviceList.addAll(list)
                isLoading.value = false
            }
        }.addOnFailureListener {
            isLoading.value = false
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(end = 16.dp, start = 16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Welcome to!",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Image(
                    painter = painterResource(id = R.drawable.banner),
                    contentDescription = "Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.FillWidth,
                    alpha = 0.8f
                )
            }
        }

        // Loading or Empty State
        if (isLoading.value) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (deviceList.isEmpty()) {
            item {
                Text(
                    text = "No devices available.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Devices Grid

            items(deviceList.size) { index ->
                DeviceCard(device = deviceList[index])
            }
        }
    }
}

@Composable
fun DeviceCard(device: Device) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(1f) // Ensures each card is square-shaped
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                // Handle device click (e.g., navigate to device detail screen)
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Device Image
            // Image
            AsyncImage(
                model = device.imageUrl, // Placeholder if image URL is null
                contentDescription = device.name,
                modifier = Modifier
                    .size(120.dp),
                contentScale = ContentScale.Crop, // Ensure the image scales properly
                placeholder = painterResource(id = R.drawable.bg), // Optional: add a placeholder resource
                error = painterResource(id = R.drawable.bg) // Optional: add an error image resource
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Device Name
            Text(
                text = device.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}
