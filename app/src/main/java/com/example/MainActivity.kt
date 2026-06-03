package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.HealthRepository
import com.example.ui.HealthViewModel
import com.example.ui.HealthViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Database & Repository
    val database = AppDatabase.getDatabase(applicationContext)
    val repository = HealthRepository(database.healthDao)

    setContent {
      MyApplicationTheme {
        // Instantiate ViewModel with Custom Factory containing the repository
        val mainViewModel: HealthViewModel = viewModel(
          factory = HealthViewModelFactory(application, repository)
        )

        // Navigation Controller setup
        val navController = rememberNavController()

        NavHost(
          navController = navController,
          startDestination = "splash"
        ) {
          composable("splash") {
            SplashScreen(onNavigateToDashboard = {
              navController.navigate("dashboard") {
                popUpTo("splash") { inclusive = true }
              }
            })
          }
          composable("dashboard") {
            DashboardScreen(viewModel = mainViewModel, onNavigate = { route ->
              navController.navigate(route)
            })
          }
          composable("log_bp") {
            LogBPScreen(viewModel = mainViewModel, onBack = {
              navController.popBackStack()
            })
          }
          composable("log_sugar") {
            LogSugarScreen(viewModel = mainViewModel, onBack = {
              navController.popBackStack()
            })
          }
          composable("log_symptoms") {
            SymptomsScreen(viewModel = mainViewModel, onBack = {
              navController.popBackStack()
            })
          }
          composable("history") {
            HistoryScreen(
              viewModel = mainViewModel,
              onBack = { navController.popBackStack() },
              onNavigate = { route -> navController.navigate(route) }
            )
          }
          composable("prediction") {
            PredictionScreen(viewModel = mainViewModel, onBack = {
              navController.popBackStack()
            })
          }
          composable("medications") {
            MedicationScreen(
              viewModel = mainViewModel,
              onBack = { navController.popBackStack() },
              onNavigate = { route -> navController.navigate(route) }
            )
          }
          composable("emergency") {
            EmergencyScreen(
              viewModel = mainViewModel,
              onBack = { navController.popBackStack() },
              onNavigate = { route -> navController.navigate(route) }
            )
          }
        }
      }
    }
  }
}
