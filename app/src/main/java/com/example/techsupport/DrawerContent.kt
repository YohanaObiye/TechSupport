package com.example.techsupport.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.techsupport.R
import android.content.Context

@Composable
fun DrawerContent(context: Context) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            Image(
                painter = painterResource(id = R.drawable.banner),
                contentDescription = "Banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.FillWidth,
                alpha = DefaultAlpha,
            )
            Text(
                "Tech Support",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
            HorizontalDivider()

            Text(
                "Menu",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            NavigationDrawerItem(
                label = { Text("Home") },
                selected = false,
                onClick = {
                    Toast.makeText(context, "Home clicked", Toast.LENGTH_SHORT).show()
                }
            )
            NavigationDrawerItem(
                label = { Text("Settings") },
                selected = false,
                onClick = {
                    Toast.makeText(context, "Settings clicked", Toast.LENGTH_SHORT).show()
                }
            )
            NavigationDrawerItem(
                label = { Text("Help and Feedback") },
                selected = false,
                onClick = {
                    Toast.makeText(context, "Help and Feedback clicked", Toast.LENGTH_SHORT)
                        .show()
                }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}
