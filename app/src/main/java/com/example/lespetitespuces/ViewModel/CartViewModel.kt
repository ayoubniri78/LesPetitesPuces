// Créez ce fichier dans votre package viewmodel
// fichier: com/example/lespetitespuces/viewmodel/CartViewModel.kt
package com.example.lespetitespuces.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lespetitespuces.Repository.MainRepository
import com.example.lespetitespuces.model.CartItemModel
import com.example.lespetitespuces.model.ItemsModel
import kotlinx.coroutines.launch

sealed class CartActionResult {
    object Success : CartActionResult()
    data class Error(val message: String) : CartActionResult()
    object Loading : CartActionResult()
    object Idle : CartActionResult()
}

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MainRepository() // Assurez-vous que votre MainRepository est accessible

    val userCart: LiveData<List<CartItemModel>> = repository.getUserCart()

    private val _cartActionResult = MutableLiveData<CartActionResult>(CartActionResult.Idle)
    val cartActionResult: LiveData<CartActionResult> = _cartActionResult

    fun addItemToCart(item: ItemsModel, quantity: Int) {
        viewModelScope.launch {
            _cartActionResult.value = CartActionResult.Loading
            if (quantity <= 0) {
                _cartActionResult.value = CartActionResult.Error("La quantité doit être positive.")
                return@launch
            }
            if (quantity > item.stock) {
                _cartActionResult.value = CartActionResult.Error("Stock insuffisant. Disponible : ${item.stock}")
                return@launch
            }

            // Créer un CartItemModel. item.title est utilisé comme itemId ici.
            // Idéalement, ItemsModel devrait avoir un champ id unique.
            val cartItem = CartItemModel(
                itemId = item.title, // ATTENTION : Doit être un ID unique !
                title = item.title,
                price = item.price,
                picUrl = item.picUrl.firstOrNull() ?: "",
                quantity = quantity,
                stock = item.stock // Stocker le stock actuel pour des vérifications rapides
            )
            try {
                repository.addItemToCart(cartItem)
                _cartActionResult.value = CartActionResult.Success
            } catch (e: Exception) {
                _cartActionResult.value = CartActionResult.Error(e.localizedMessage ?: "Erreur inconnue")
            }
        }
    }

    fun updateCartItemQuantity(itemId: String, newQuantity: Int, currentStock: Int) {
        viewModelScope.launch {
            _cartActionResult.value = CartActionResult.Loading
            if (newQuantity <= 0) {
                removeItemFromCart(itemId) // Supprimer si la quantité est 0 ou moins
                return@launch
            }
            if (newQuantity > currentStock) {
                _cartActionResult.value = CartActionResult.Error("Stock insuffisant. Disponible : $currentStock")
                return@launch
            }
            try {
                repository.updateCartItemQuantity(itemId, newQuantity)
                _cartActionResult.value = CartActionResult.Success
            } catch (e: Exception) {
                _cartActionResult.value = CartActionResult.Error(e.localizedMessage ?: "Erreur inconnue")
            }
        }
    }

    fun removeItemFromCart(itemId: String) {
        viewModelScope.launch {
            _cartActionResult.value = CartActionResult.Loading
            try {
                repository.removeItemFromCart(itemId)
                _cartActionResult.value = CartActionResult.Success
            } catch (e: Exception) {
                _cartActionResult.value = CartActionResult.Error(e.localizedMessage ?: "Erreur inconnue")
            }
        }
    }

    fun confirmPurchaseAndDecreaseStock(cartItems: List<CartItemModel>) {
        viewModelScope.launch {
            _cartActionResult.value = CartActionResult.Loading
            var allStockUpdatesSuccessful = true
            val itemsToUpdateStock = mutableMapOf<String, Int>() // itemId to newStock

            // Étape 1: Vérifier le stock et calculer les nouveaux stocks
            for (cartItem in cartItems) {
                // Vous devrez peut-être recharger l'item de la base de données pour obtenir le stock le plus récent
                // avant la transaction, surtout dans un environnement multi-utilisateur.
                // Pour simplifier ici, nous utilisons le stock du cartItem (qui vient de ItemsModel au moment de l'ajout).
                // Une meilleure approche impliquerait une transaction Firebase pour lire et écrire le stock atomiquement.
                val currentItemStockInDb = cartItem.stock // Stock au moment de l'ajout au panier

                if (cartItem.quantity > currentItemStockInDb) {
                    _cartActionResult.value = CartActionResult.Error("Stock insuffisant pour ${cartItem.title}. Disponible: $currentItemStockInDb")
                    allStockUpdatesSuccessful = false
                    break
                }
                itemsToUpdateStock[cartItem.itemId] = currentItemStockInDb - cartItem.quantity
            }

            if (!allStockUpdatesSuccessful) {
                // Ne pas continuer si un item n'a pas assez de stock
                _cartActionResult.value = CartActionResult.Error("Un ou plusieurs articles ne sont plus en stock suffisant.")
                return@launch
            }

            // Étape 2: Mettre à jour le stock pour tous les items
            for ((itemId, newStock) in itemsToUpdateStock) {
                val success = repository.updateItemStock(itemId, newStock)
                if (!success) {
                    allStockUpdatesSuccessful = false
                    // Gérer l'erreur (peut-être essayer de rollback ou notifier l'admin)
                    _cartActionResult.value = CartActionResult.Error("Erreur lors de la mise à jour du stock pour $itemId.")
                    break // Arrêter si une mise à jour échoue
                }
            }

            if (allStockUpdatesSuccessful) {
                // Étape 3: Vider le panier de l'utilisateur
                try {
                    repository.clearUserCart()
                    _cartActionResult.value = CartActionResult.Success // Achat complet
                } catch (e: Exception) {
                    // Le stock a été mis à jour, mais le panier n'a pas pu être vidé. Situation à gérer.
                    _cartActionResult.value = CartActionResult.Error("Achat effectué, stock mis à jour, mais erreur lors du vidage du panier.")
                }
            } else {
                // Si une mise à jour de stock a échoué, il faudrait idéalement un mécanisme de rollback
                // pour les items dont le stock a déjà été mis à jour. C'est complexe.
                // Pour l'instant, on signale juste l'erreur.
            }
        }
    }

    fun resetCartActionResult() {
        _cartActionResult.value = CartActionResult.Idle
    }
}