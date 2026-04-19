package com.example.line_dev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.line_dev.ui.chat.ChatScreen
import com.example.line_dev.ui.favorites.FavoritesScreen
import com.example.line_dev.ui.theme.LinedevTheme

class MainActivity : ComponentActivity() {

    var isKeyboardOpen by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 監聽鍵盤狀態
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            isKeyboardOpen = insets.isVisible(WindowInsetsCompat.Type.ime())
            ViewCompat.onApplyWindowInsets(view, insets)
        }

        setContent {
            LinedevTheme {
                MainScreen(isKeyboardOpen = isKeyboardOpen)
            }
        }
    }
}

@Composable
fun MainScreen(isKeyboardOpen: Boolean = false) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            if (!isKeyboardOpen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = androidx.compose.ui.unit.Dp(0f)
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "chat",
                        onClick = { navController.navigate("chat") },
                        icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Nova") },
                        label = { Text("Nova") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == "favorites",
                        onClick = { navController.navigate("favorites") },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = "收藏") },
                        label = { Text("收藏") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
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