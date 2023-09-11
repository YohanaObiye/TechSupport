package com.example.techsupport

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.techsupport.screens.LoginScreen
import com.example.techsupport.screens.MainScreen
import com.example.techsupport.screens.RegisterScreen
import com.example.techsupport.screens.admin.AdminScreen
import com.example.techsupport.screens.admin.CreateKnowledgeBase
import com.example.techsupport.screens.admin.CreateStaffScreen
import com.example.techsupport.screens.admin.ManageDevicesScreen
import com.example.techsupport.screens.admin.ManageKnowledgeBase
import com.example.techsupport.screens.admin.ManageStaffs

@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ScMain.route) {
        // AUTH SCREENS
        composable(route = Screen.ScLogin.route) {
            LoginScreen(modifier = modifier, navController = navController)
        }
        composable(route = Screen.ScRegister.route) {
            RegisterScreen(navController = navController, modifier = modifier)
        }

        // USER SCREENS
        composable(route = Screen.ScMain.route) {
            MainScreen(navController = navController, modifier = modifier)
        }

        // ADMIN SCREENS
        composable(route = Screen.ScAdmin.route) {
            AdminScreen(navController = navController, modifier = modifier)
        }
        composable(route = Screen.ScManageDevices.route) {
            ManageDevicesScreen(navController = navController, modifier = modifier)
        }
        composable(route = Screen.ScManageKnowledgeBase.route) {
            ManageKnowledgeBase(navController = navController, modifier = modifier)
        }
        composable(route = Screen.ScCreateKnowledgeBase.route) {
            CreateKnowledgeBase(navController = navController, modifier = modifier)
        }
        composable(route = Screen.ScManageStaffs.route) {
            ManageStaffs(navController = navController, modifier = modifier)
        }
        composable(route = Screen.ScCreateStaff.route) {
            CreateStaffScreen(navController = navController, modifier = modifier)
        }
    }

    val auth = Utils.getAuth() // Assuming Utils.getAuth() provides FirebaseAuth instance.

    // Check if the user is logged in when the composable is resumed
    LaunchedEffect(key1 = true) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // If user is logged in, navigate to the home screen
            navController.navigate(Screen.ScMain.route) {
                // Pop up to the start destination so the user can't go back to the login screen
//                popUpTo(Screen.ScLogin.route) { inclusive = true }
            }
        }
    }
}
