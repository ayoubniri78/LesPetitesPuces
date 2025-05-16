package com.example.lespetitespuces.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lespetitespuces.Model.CategoryModel
import com.example.lespetitespuces.model.CartItemModel
import com.example.lespetitespuces.model.ItemsModel


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await

class MainRepository {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ... (loadCategory et loadItems restent les mêmes) ...
    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        val listData = MutableLiveData<MutableList<CategoryModel>>()
        val ref = database.getReference("Category")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(CategoryModel::class.java)
                    item?.let { lists.add(it) }
                }
                listData.value = lists
            }
            override fun onCancelled(error: DatabaseError) {
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    fun loadItems(): LiveData<MutableList<ItemsModel>> {
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = database.getReference("Items")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemsList = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { itemsList.add(it) }
                }
                listData.value = itemsList
            }
            override fun onCancelled(error: DatabaseError) {
                listData.value = mutableListOf()
            }
        })
        return listData
    }


    // --- Fonctions pour le Panier ---
    private fun getUserCartRef() = auth.currentUser?.uid?.let { userId ->
        database.getReference("carts").child(userId)
    }

    suspend fun addItemToCart(cartItem: CartItemModel) {
        getUserCartRef()?.child(cartItem.itemId)?.setValue(cartItem)?.await()
    }

    suspend fun updateCartItemQuantity(itemId: String, newQuantity: Int) {
        getUserCartRef()?.child(itemId)?.child("quantity")?.setValue(newQuantity)?.await()
    }

    suspend fun removeItemFromCart(itemId: String) {
        getUserCartRef()?.child(itemId)?.removeValue()?.await()
    }

    suspend fun clearUserCart() {
        getUserCartRef()?.removeValue()?.await()
    }

    fun getUserCart(): LiveData<List<CartItemModel>> {
        val cartLiveData = MutableLiveData<List<CartItemModel>>()
        val cartRef = getUserCartRef()

        if (cartRef == null) {
            cartLiveData.value = emptyList() // Pas d'utilisateur connecté
            return cartLiveData
        }

        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartItems = mutableListOf<CartItemModel>()
                snapshot.children.forEach { childSnapshot ->
                    childSnapshot.getValue<CartItemModel>()?.let {
                        cartItems.add(it)
                    }
                }
                cartLiveData.value = cartItems
            }

            override fun onCancelled(error: DatabaseError) {
                cartLiveData.value = emptyList() // Erreur ou pas d'accès
            }
        })
        return cartLiveData
    }

    // --- Fonctions pour le Stock ---
    suspend fun updateItemStock(itemId: String, newStock: Int): Boolean {
        // ATTENTION : itemId ici doit correspondre à la clé de l'item dans le noeud "Items"
        // Si vos Items sont stockés avec des clés auto-générées, vous devrez trouver l'item
        // par son `title` ou un autre champ unique pour obtenir sa clé Firebase.
        // Pour cet exemple, je suppose que `itemId` est la clé Firebase de l'item.
        // Si ce n'est pas le cas, cette logique doit être adaptée.
        return try {
            database.getReference("Items").child(itemId).child("stock").setValue(newStock).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Fonction pour obtenir un item spécifique (utile pour vérifier le stock avant d'ajouter au panier)
    fun getSpecificItem(itemId: String): LiveData<ItemsModel?> {
        val itemLiveData = MutableLiveData<ItemsModel?>()
        // Similaire à updateItemStock, itemId doit être la clé Firebase.
        // Si vous stockez vos items sous un ID généré par Firebase et que `itemId` est, par exemple, le titre,
        // vous devrez interroger la base de données différemment (par exemple, orderByChild("title").equalTo(itemId)).
        // Pour la simplicité, je suppose ici que itemId est la clé.
        val ref = database.getReference("Items").child(itemId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemLiveData.value = snapshot.getValue(ItemsModel::class.java)
            }
            override fun onCancelled(error: DatabaseError) {
                itemLiveData.value = null
            }
        })
        return itemLiveData
    }
}