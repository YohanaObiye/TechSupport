package com.example.techsupport.screens.tabs

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.techsupport.R
import com.example.techsupport.Screen


@Composable
fun ProfileScreen(
    navController: NavHostController,
    mainNavController: NavHostController,
    modifier: Modifier,
) {
//    CREATE CONTEXT
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Image
                Image(
                    painter = painterResource(id = R.drawable.profile), // Replace with your image resource
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Name and Role
                Column {
                    Text(
                        text = "William John Malik", // Replace with dynamic name if required
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Aggressive Investor", // Replace with dynamic role if required
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }


        // Clickable Cards
        ClickableCard(
            icon = Icons.Default.Person,
            title = "Personal Data",
            onClick = {
                Toast.makeText(context, "Personal Data clicked", Toast.LENGTH_SHORT).show()
            }
        )
        ClickableCard(
            icon = Icons.Default.Settings,
            title = "Settings",
            onClick = {
                Toast.makeText(context, "Settings clicked", Toast.LENGTH_SHORT).show()
            }
        )

        ClickableCard(
            icon = Icons.Default.Description,
            title = "E-Statement",
            onClick = {
                Toast.makeText(context, "E-Statement clicked", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(Modifier.height(16.dp))

        ClickableCard(
            icon = Icons.Default.QuestionAnswer,
            title = "FAQs",
            onClick = {
                Toast.makeText(context, "FAQs clicked", Toast.LENGTH_SHORT).show()
            }
        )
        ClickableCard(
            icon = Icons.Default.Book,
            title = "Our Handbook",
            onClick = {
                Toast.makeText(context, "Our Handbook clicked", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(Modifier.height(16.dp))

        ClickableCard(
            icon = Icons.Default.HeadsetMic, // Replace with a relevant icon
            title = "Technician",
            onClick = {
                Toast.makeText(context, "Tech Support clicked", Toast.LENGTH_SHORT).show()
            }
        )

        ClickableCard(
            icon = Icons.Default.Security, // Replace with a relevant icon
            title = "Admin",
            onClick = {
                mainNavController.navigate(Screen.ScAdmin.route)
            }
        )

    }
}


@Composable
fun ClickableCard(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = "Arrow Icon",
                tint = Color.Gray
            )
        }
    }
}