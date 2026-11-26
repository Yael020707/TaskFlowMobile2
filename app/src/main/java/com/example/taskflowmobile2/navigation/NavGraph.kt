package com.example.taskflowmobile2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskflowmobile2.ui.screens.DashboardScreen
import com.example.taskflowmobile2.ui.screens.LoginScreen
import com.example.taskflowmobile2.ui.viewmodel.TaskViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
}

@Composable
fun TaskFlowNavGraph(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()

    val startDestination = if (uiState.isLoggedIn) Screen.Dashboard.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { email, pass ->
                    viewModel.login(email, pass) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                errorMessage = uiState.loginError
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                uiState = uiState,
                onFilterSelect = { filter ->
                    viewModel.setFilter(filter)
                },
                onToggleTask = { tarea ->
                    viewModel.toggleTaskCompletion(tarea)
                },
                onDeleteTask = { tarea ->
                    viewModel.deleteTask(tarea)
                },
                onCreateTask = { titulo, desc, prio, fecha, catId ->
                    viewModel.createTask(titulo, desc, prio, fecha, catId) { }
                },
                onUpdateTask = { tarea ->
                    viewModel.updateTask(tarea) { }
                },
                onSelectTask = { taskId ->
                    viewModel.selectTask(taskId)
                },
                onAddHito = { taskId, desc ->
                    viewModel.addHito(taskId, desc)
                },
                onToggleHito = { hito ->
                    viewModel.toggleHitoCompletion(hito)
                },
                onDeleteHito = { hito ->
                    viewModel.deleteHito(hito)
                },
                onToggleDarkMode = { enabled ->
                    viewModel.toggleDarkMode(enabled)
                },
                onLogoutClick = {
                    viewModel.logout {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onClearMessage = {
                    viewModel.clearUserMessage()
                }
            )
        }
    }
}
