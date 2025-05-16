// fichier: com/example/lespetitespuces/navigation/AppNavigation.kt
package com.example.lespetitespuces.navigation

import android.util.Log // Utile pour le débogage
import androidx.compose.runtime.Composable
// import androidx.compose.runtime.LaunchedEffect // Pas utilisé directement ici, mais peut être utile dans les écrans
// import androidx.compose.runtime.getValue // Pas utilisé directement ici
// import androidx.compose.runtime.livedata.observeAsState // Pas utilisé directement ici
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lespetitespuces.Model.CategoryModel
import com.example.lespetitespuces.model.ItemsModel
import com.example.lespetitespuces.ui.screens.CartScreen
import com.example.lespetitespuces.ui.screens.LoginScreen
import com.example.lespetitespuces.ui.screens.MainScreen
import com.example.lespetitespuces.ui.screens.ProductDetailScreen
import com.example.lespetitespuces.ui.screens.RegisterScreen
import com.example.lespetitespuces.viewmodel.AuthViewModel
import com.example.lespetitespuces.ui.screens.CheckoutScreen // Importer
import com.example.lespetitespuces.ui.screens.ValidatePaymentScreen
import com.example.lespetitespuces.ui.screens.ProfileScreen

@Composable
fun AppNavigationGraph(
    authViewModel: AuthViewModel = viewModel(),
    // CartViewModel sera instancié dans CartScreen et ProductDetailScreen directement via viewModel()
    categories: List<CategoryModel>,
    allItems: List<ItemsModel>
) {
    val navController: NavHostController = rememberNavController()


    NavHost(navController = navController, startDestination = AppRoutes.MAIN_SCREEN) {
        composable(AppRoutes.MAIN_SCREEN) {
            MainScreen(
                navController = navController,
                authViewModel = authViewModel,
                categories = categories,
                allItems = allItems
            )
        }

        composable(
            route = "${AppRoutes.PRODUCT_DETAIL_SCREEN}/{${AppRoutes.ARG_ITEM_ID}}", // Route avec argument
            arguments = listOf(navArgument(AppRoutes.ARG_ITEM_ID) {
                type = NavType.StringType
                // nullable = true // Si l'ID peut être optionnel, mais généralement non pour un détail
            })
        ) { backStackEntry ->
            // Récupérer l'argument 'itemId' de la route
            val itemIdFromArgs = backStackEntry.arguments?.getString(AppRoutes.ARG_ITEM_ID)

            // Trouver l'item correspondant en utilisant itemIdFromArgs
            // ATTENTION: S'assurer que 'it.title' est bien l'identifiant unique passé dans la route.
            // Si vous avez un champ 'id' dans ItemsModel, utilisez `it.id == itemIdFromArgs` (après conversion si nécessaire).
            val selectedItem = allItems.find { it.title == itemIdFromArgs }

            if (itemIdFromArgs != null && selectedItem != null) {
                ProductDetailScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    // cartViewModel sera obtenu par ProductDetailScreen via viewModel()
                    item = selectedItem
                )
            } else {
                // Gérer le cas où l'item n'est pas trouvé ou l'ID est null
                Log.e("AppNavigationGraph", "Item not found or itemId is null. Navigated with itemId: $itemIdFromArgs")
                // Optionnel : Naviguer vers un écran d'erreur ou simplement revenir en arrière.
                navController.popBackStack()
            }
        }

        composable(AppRoutes.LOGIN_SCREEN) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(AppRoutes.REGISTER_SCREEN) {
            RegisterScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(AppRoutes.CART_SCREEN) {
            // Modifier l'appel à CartScreen pour qu'il puisse naviguer vers CheckoutScreen
            // CartScreen naviguera vers CheckoutScreen depuis son bouton "Procéder au paiement"
            CartScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(AppRoutes.CHECKOUT_SCREEN) {
            CheckoutScreen(navController = navController, authViewModel = authViewModel)
            // cartViewModel sera obtenu par CheckoutScreen via viewModel()
        }

        // Nouvelle destination pour le succès de la commande
        composable(AppRoutes.ORDER_SUCCESS_SCREEN) {
            ValidatePaymentScreen(navController = navController) // APPELLE VOTRE ÉCRAN RENOMMÉ/MODIFIÉ
        }
        composable(AppRoutes.PROFILE_SCREEN) {
            ProfileScreen(navController = navController, authViewModel = authViewModel)
        }
    }
}