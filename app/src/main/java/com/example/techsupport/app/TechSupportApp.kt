package com.example.techsupport.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.techsupport.Navigation
import com.example.techsupport.screens.RegisterScreen

@Composable
fun TechSupportApp(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Navigation(modifier = Modifier.padding(innerPadding))
    }
}
