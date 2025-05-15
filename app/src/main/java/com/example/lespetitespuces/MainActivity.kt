package com.example.lespetitespuces

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lespetitespuces.Model.CategoryModel
import com.example.lespetitespuces.model.ItemsModel
import com.example.lespetitespuces.ui.screens.MainScreen
import com.example.lespetitespuces.ui.screens.ProductDetailScreen // Nous allons créer ce fichier
import com.example.lespetitespuces.ui.theme.LesPetitesPucesTheme
import com.example.lespetitespuces.ui.theme.MainViewModel

// Définir les routes pour la navigation
object AppRoutes {
    const val MAIN_SCREEN = "main"
    const val PRODUCT_DETAIL_SCREEN = "product_detail"
    const val ARG_ITEM_ID = "itemId" // Clé pour l'argument de l'ID de l'item
}

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

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

                    AppNavigation(
                        categories = categories,
                        allItems = allItems
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    categories: List<CategoryModel>,
    allItems: List<ItemsModel>
) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoutes.MAIN_SCREEN) {
        composable(AppRoutes.MAIN_SCREEN) {
            MainScreen(
                navController = navController, // Passer le NavController
                categories = categories,
                allItems = allItems
            )
        }
        composable(
            route = "${AppRoutes.PRODUCT_DETAIL_SCREEN}/{${AppRoutes.ARG_ITEM_ID}}", // Route avec argument
            arguments = listOf(navArgument(AppRoutes.ARG_ITEM_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString(AppRoutes.ARG_ITEM_ID)
            // Trouver l'item correspondant. Pour plus de robustesse, itemId devrait être un ID unique.
            // Ici, nous utilisons le titre, ce qui peut être problématique s'ils ne sont pas uniques.
            val selectedItem = allItems.find { it.title == itemId } // ATTENTION: S'assurer que 'itemId' est unique!

            if (itemId != null && selectedItem != null) {
                ProductDetailScreen(
                    navController = navController,
                    item = selectedItem
                )
            } else {
                // Gérer le cas où l'item n'est pas trouvé ou l'ID est null (ex: afficher un message d'erreur)
                // Pour l'instant, on pourrait juste revenir en arrière ou afficher un écran vide.
                navController.popBackStack() // Solution simple pour l'instant
            }
        }
    }
}