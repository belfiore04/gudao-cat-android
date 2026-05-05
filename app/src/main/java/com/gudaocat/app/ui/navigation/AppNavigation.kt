package com.gudaocat.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gudaocat.app.ui.screens.auth.LoginScreen
import com.gudaocat.app.ui.screens.cat.CatDetailScreen
import com.gudaocat.app.ui.screens.cat.CreateCatScreen
import com.gudaocat.app.ui.screens.auth.RegisterScreen
import com.gudaocat.app.ui.screens.community.CommunityScreen
import com.gudaocat.app.ui.screens.community.CreatePostScreen
import com.gudaocat.app.ui.screens.community.PostDetailScreen
import com.gudaocat.app.ui.screens.home.HomeScreen
import com.gudaocat.app.ui.screens.profile.ProfileScreen
import com.gudaocat.app.ui.screens.profile.UserProfileScreen
import com.gudaocat.app.ui.screens.recognize.RecognizeScreen
import com.gudaocat.app.ui.theme.DarkBg
import com.gudaocat.app.ui.theme.DarkSurface
import com.gudaocat.app.ui.theme.Orange
import com.gudaocat.app.ui.theme.TextDim
import com.gudaocat.app.viewmodel.AuthViewModel
import com.gudaocat.app.viewmodel.CatViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Recognize : Screen("recognize")
    object Community : Screen("community")
    object Profile : Screen("profile")
    object CreateCat : Screen("cat/create")
    object CreatePost : Screen("post/create")
    object CatDetail : Screen("cat/{catId}") {
        fun createRoute(catId: Int) = "cat/$catId"
    }
    object PostDetail : Screen("post/{postId}") {
        fun createRoute(postId: Int) = "post/$postId"
    }
    object UserProfile : Screen("user/{userId}") {
        fun createRoute(userId: Int) = "user/$userId"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "首页", Icons.Rounded.Home),
    BottomNavItem(Screen.Recognize, "识猫", Icons.Rounded.CameraAlt),
    BottomNavItem(Screen.Community, "社区", Icons.Rounded.Forum),
    BottomNavItem(Screen.Profile, "我的", Icons.Rounded.Person),
)

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    catViewModel: CatViewModel = hiltViewModel(),
) {
    val authState by authViewModel.state.collectAsState()
    val navController = rememberNavController()

    val startDestination = if (authState.isLoggedIn) Screen.Home.route else Screen.Login.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavItems.map { it.screen.route }

    LaunchedEffect(authState.isLoggedIn, currentRoute) {
        when {
            authState.isLoggedIn && currentRoute in listOf(Screen.Login.route, Screen.Register.route) -> {
                catViewModel.loadCats()
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            !authState.isLoggedIn && currentRoute in bottomNavItems.map { it.screen.route } -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    Scaffold(
        containerColor = DarkBg,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 0.dp,
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = navBackStackEntry?.destination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                Icon(item.icon, contentDescription = item.label)
                            },
                            label = {
                                Text(item.label, style = MaterialTheme.typography.labelMedium)
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Orange,
                                selectedTextColor = Orange,
                                unselectedIconColor = TextDim,
                                unselectedTextColor = TextDim,
                                indicatorColor = Orange.copy(alpha = 0.12f),
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onCatClick = { catId ->
                        navController.navigate(Screen.CatDetail.createRoute(catId))
                    },
                    viewModel = catViewModel,
                )
            }
            composable(Screen.Recognize.route) {
                RecognizeScreen(
                    onCatClick = { catId ->
                        navController.navigate(Screen.CatDetail.createRoute(catId))
                    },
                    onCreateCatClick = {
                        navController.navigate(Screen.CreateCat.route)
                    },
                )
            }
            composable(Screen.Community.route) {
                CommunityScreen(
                    onPostClick = { postId ->
                        navController.navigate(Screen.PostDetail.createRoute(postId))
                    },
                    onAuthorClick = { userId ->
                        navController.navigate(Screen.UserProfile.createRoute(userId))
                    },
                    onCreatePostClick = {
                        navController.navigate(Screen.CreatePost.route)
                    },
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }
            composable(Screen.CreateCat.route) {
                CreateCatScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(Screen.CreatePost.route) {
                CreatePostScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = {
                        navController.navigate(Screen.Community.route) {
                            popUpTo(Screen.Community.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(
                route = Screen.CatDetail.route,
                arguments = listOf(navArgument("catId") { type = NavType.IntType }),
            ) { entry ->
                CatDetailScreen(
                    catId = entry.arguments?.getInt("catId") ?: -1,
                    onBack = { navController.popBackStack() },
                    onCreatorClick = { userId ->
                        navController.navigate(Screen.UserProfile.createRoute(userId))
                    },
                )
            }
            composable(
                route = Screen.PostDetail.route,
                arguments = listOf(navArgument("postId") { type = NavType.IntType }),
            ) { entry ->
                PostDetailScreen(
                    postId = entry.arguments?.getInt("postId") ?: -1,
                    onBack = { navController.popBackStack() },
                    onAuthorClick = { userId ->
                        navController.navigate(Screen.UserProfile.createRoute(userId))
                    },
                    onCatClick = { catId ->
                        navController.navigate(Screen.CatDetail.createRoute(catId))
                    },
                )
            }
            composable(
                route = Screen.UserProfile.route,
                arguments = listOf(navArgument("userId") { type = NavType.IntType }),
            ) { entry ->
                UserProfileScreen(
                    userId = entry.arguments?.getInt("userId") ?: -1,
                    onBack = { navController.popBackStack() },
                    onCatClick = { catId ->
                        navController.navigate(Screen.CatDetail.createRoute(catId))
                    },
                    onPostClick = { postId ->
                        navController.navigate(Screen.PostDetail.createRoute(postId))
                    },
                )
            }
        }
    }
}
