package com.example.lespetitespuces.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lespetitespuces.Model.CategoryModel
import com.example.lespetitespuces.model.ItemsModel // Assurez-vous que l'import est correct
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainRepository {
    private val database = FirebaseDatabase.getInstance()

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        val listData = MutableLiveData<MutableList<CategoryModel>>()
        val ref = database.getReference("Category") // Chemin vers vos catégories

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
                listData.value = mutableListOf() // Gérer l'erreur en retournant une liste vide
            }
        })
        return listData
    }

    // Nouvelle fonction pour charger les items
    fun loadItems(): LiveData<MutableList<ItemsModel>> {
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        // Assurez-vous que "Items" est le nom correct de votre nœud Firebase pour les items
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
                listData.value = mutableListOf() // Gérer l'erreur
            }
        })
        return listData
    }
}