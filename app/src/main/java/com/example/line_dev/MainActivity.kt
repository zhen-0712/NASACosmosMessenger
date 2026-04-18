package com.example.line_dev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.line_dev.ui.chat.ChatScreen
import com.example.line_dev.ui.favorites.FavoritesScreen
import com.example.line_dev.ui.theme.LinedevTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinedevTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "chat",
                    onClick = { navController.navigate("chat") },
                    icon = { Icon(painterResource(android.R.drawable.ic_menu_send), contentDescription = "Nova") },
                    label = { Text("Nova") }
                )
                NavigationBarItem(
                    selected = currentRoute == "favorites",
                    onClick = { navController.navigate("favorites") },
                    icon = { Icon(painterResource(android.R.drawable.btn_star_big_on), contentDescription = "收藏") },
                    label = { Text("收藏") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "chat",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("chat") { ChatScreen() }
            composable("favorites") { FavoritesScreen() }
        }
    }
}