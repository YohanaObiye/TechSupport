package com.example.techsupport.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.techsupport.Constants
import com.example.techsupport.Screen
import com.example.techsupport.Utils
import com.example.techsupport.models.KB
import java.text.DateFormat
import java.util.Date

@Composable
fun ManageKnowledgeBase(modifier: Modifier = Modifier, navController: NavHostController) {
    // State to hold knowledge base data
    val knowledgeBaseList = remember { mutableStateListOf<KB>() }
    val isLoading = remember { mutableStateOf(true) }

    // Fetch data from Firebase
    LaunchedEffect(Unit) {
        val databaseRef = Utils.getDatabaseReference().getReference(Constants.KNOWLEDGE_BASE_DB)
        databaseRef.get().addOnSuccessListener { snapshot ->
            snapshot.children.mapNotNull { it.getValue(KB::class.java) }.let { list ->
                knowledgeBaseList.clear()
                knowledgeBaseList.addAll(list)
                isLoading.value = false
            }
        }.addOnFailureListener {
            isLoading.value = false // Stop loading if fetching fails
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            Text(
                text = "Manage Knowledge Base",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.ScCreateKnowledgeBase.route)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Content"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            if (isLoading.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (knowledgeBaseList.isEmpty()) {
                Text(
                    text = "No Knowledge Base content found!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                knowledgeBaseList.forEach { kb ->
                    KnowledgeBaseCard(kb = kb)
                }
            }
        }
    }
}

@Composable
fun KnowledgeBaseCard(kb: KB) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { /* Handle click if needed */ },
        elevation = CardDefaults.cardElevation(4.dp) // Set elevation
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            AsyncImage(
                model = kb.imageUrl,
                contentDescription = kb.title,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content
            Column {
                Text(
                    text = kb.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = kb.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Created: ${
                        DateFormat.getDateTimeInstance().format(Date(kb.createdAt))
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

