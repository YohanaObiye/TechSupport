package com.example.techsupport.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.techsupport.R
import com.example.techsupport.screens.tabs.ChatScreen
import com.example.techsupport.screens.tabs.HomeScreen
import com.example.techsupport.screens.tabs.KnowledgeBaseScreen
import com.example.techsupport.screens.tabs.ProfileScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, modifier: Modifier) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navTabController = rememberNavController()


    // Bottom navigation bar items
    val tabBarItems = listOf(
        TabBarItem(
            "Home",
            painterResource(id = R.drawable.home),
            painterResource(id = R.drawable.home)
        ),
        TabBarItem(
            "Chat",
            painterResource(id = R.drawable.chat),
            painterResource(id = R.drawable.chat)
        ),
        TabBarItem(
            "Knowledge Base",
            painterResource(id = R.drawable.stack),
            painterResource(id = R.drawable.stack)
        ),
        TabBarItem(
            "Profile",
            painterResource(id = R.drawable.user),
            painterResource(id = R.drawable.user)
        )
    )

    // Modal Drawer and Scaffold layout
    ModalNavigationDrawer(
        drawerContent = { DrawerContent(context = context) },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Tech Support") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                TabView(tabBarItems, navTabController)
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavHost(navController = navTabController, startDestination = "Home") {
                    composable("Home") { HomeScreen(navTabController, modifier) }
                    composable("Chat") { ChatScreen(navTabController, modifier) }
                    composable("Knowledge Base") { KnowledgeBaseScreen(navTabController, modifier) }
                    composable("Profile") {
                        ProfileScreen(
                            navTabController,
                            mainNavController = navController,
                            modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    NavigationBar {
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tabBarItem.title) {
                        popUpTo = navController.graph.startDestinationId
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = tabBarItem.selectedIcon,
                        contentDescription = tabBarItem.title
                    )
                },
                label = { Text(tabBarItem.title) }
            )
        }
    }
}

// Data class for TabBar items with drawable icons
data class TabBarItem(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter
)