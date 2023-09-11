package com.example.techsupport.screens.tabs

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage

// Data class for blog content
data class BlogContent(
    val title: String,
    val caption: String,
    val imageUrl: String,
    val datePosted: String
)

// Sample blog content
val blogList = listOf(
    BlogContent(
        title = "How to Fix Common Laptop Issues",
        caption = "Learn quick tips to resolve your laptop problems.",
        imageUrl = "https://images.unsplash.com/photo-1541807084-5c52ec5ff8af",
        datePosted = "Jan 10, 2025"
    ),
    BlogContent(
        title = "Troubleshooting Phone Connectivity",
        caption = "Resolve issues with network and Wi-Fi on your phone.",
        imageUrl = "https://images.unsplash.com/photo-1512499617640-c2f999018cb0",
        datePosted = "Jan 15, 2025"
    ),
    BlogContent(
        title = "Desktop Overheating Solutions",
        caption = "Simple steps to keep your desktop cool.",
        imageUrl = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8",
        datePosted = "Jan 18, 2025"
    ),
    BlogContent(
        title = "Fixing TV Display Problems",
        caption = "Identify and solve common TV display issues.",
        imageUrl = "https://images.unsplash.com/photo-1602288637787-f1964a5fe0c6",
        datePosted = "Jan 20, 2025"
    ),
    BlogContent(
        title = "How to Troubleshoot Smartwatch Issues",
        caption = "Quick fixes for your smartwatch troubles.",
        imageUrl = "https://images.unsplash.com/photo-1603791440384-56cd371ee9c5",
        datePosted = "Jan 22, 2025"
    )
)

@Composable
fun KnowledgeBaseScreen(navController: NavHostController, modifier: Modifier) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(blogList.size) { index ->
            BlogCard(blogContent = blogList[index], onClick = {
                Toast.makeText(
                    context,
                    "Clicked on: ${blogList[index].title}",
                    Toast.LENGTH_SHORT
                ).show()
            })
        }
    }
}

@Composable
fun BlogCard(blogContent: BlogContent, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = blogContent.imageUrl,
                contentDescription = blogContent.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                alpha = DefaultAlpha
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = blogContent.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = blogContent.caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = blogContent.datePosted,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
