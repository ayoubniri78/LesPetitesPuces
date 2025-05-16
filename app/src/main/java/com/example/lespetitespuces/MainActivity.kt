// fichier: com/example/lespetitespuces/MainActivity.kt
package com.example.lespetitespuces

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.lespetitespuces.Model.CategoryModel
import com.example.lespetitespuces.model.ItemsModel
import com.example.lespetitespuces.navigation.AppNavigationGraph
import com.example.lespetitespuces.ui.theme.LesPetitesPucesTheme
import com.example.lespetitespuces.ui.theme.MainViewModel // Votre ViewModel pour les données
import com.example.lespetitespuces.viewmodel.AuthViewModel // Votre ViewModel pour l'auth

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels() // Instance de AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LesPetitesPucesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val categories: List<CategoryModel> by mainViewModel.loadCategory().observeAsState(initial = emptyList())
                    val allItems: List<ItemsModel> by mainViewModel.loadItems().observeAsState(initial = emptyList())

                    AppNavigationGraph(
                        authViewModel = authViewModel, // Passer AuthViewModel
                        categories = categories,
                        allItems = allItems
                    )
                }
            }
        }
    }
}