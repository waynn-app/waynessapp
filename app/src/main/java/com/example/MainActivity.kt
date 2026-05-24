package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.db.AppDatabase
import com.example.repository.WaynessRepository
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.WaynessViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Supports full edge-to-edge drawing
        enableEdgeToEdge()

        // Initialize Local persistence layer
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = WaynessRepository(database.waynessDao())
        
        // Instantiate the master viewModel
        val viewModel: WaynessViewModel by viewModels {
            WaynessViewModelFactory(application, repository)
        }

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Unused scaffold parameter is suppressed as screens handle innerPadding or safe drawing
                    MainAppScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

// Simple Factory to inject Dependencies cleanly without bulky DI frameworks.
class WaynessViewModelFactory(
    private val application: Application,
    private val repository: WaynessRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaynessViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WaynessViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
