package com.example.lespetitespuces // Le package de votre MainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // <<--- IMPORT IMPORTANT
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue // <<--- IMPORT IMPORTANT
import androidx.compose.runtime.livedata.observeAsState // <<--- IMPORT IMPORTANT
import androidx.compose.ui.Modifier
import com.example.lespetitespuces.Model.CategoryModel // Votre modèle de catégorie
import com.example.lespetitespuces.ui.screens.MainScreen
import com.example.lespetitespuces.ui.theme.LesPetitesPucesTheme
import com.example.lespetitespuces.ui.theme.MainViewModel


class MainActivity : ComponentActivity() {

    // Instancier le ViewModel en utilisant la délégation KTX
    // Cela garantit que le ViewModel est correctement géré par le cycle de vie de l'activité.
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LesPetitesPucesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Observer les catégories depuis le ViewModel.
                    // mainViewModel.loadCategory() retourne un LiveData<MutableList<CategoryModel>>.
                    // observeAsState le convertit en un State<List<CategoryModel>> que Compose peut utiliser.
                    // L'utilisation de `initial = emptyList()` garantit que `categories` n'est jamais null.
                    val categories: List<CategoryModel> by mainViewModel.loadCategory().observeAsState(initial = emptyList())

                    // Appeler MainScreen et lui passer la liste des catégories observées.
                    MainScreen(categories = categories)
                }
            }
        }
    }
}