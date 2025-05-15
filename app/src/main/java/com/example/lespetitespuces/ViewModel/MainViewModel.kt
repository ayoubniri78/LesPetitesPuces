package com.example.lespetitespuces.ui.theme // Ou votre package ViewModel correct

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.lespetitespuces.Model.CategoryModel
import com.example.lespetitespuces.model.ItemsModel // Assurez-vous que l'import est correct
import com.example.lespetitespuces.Repository.MainRepository


class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        return repository.loadCategory()
    }

    // Nouvelle fonction pour exposer les items
    fun loadItems(): LiveData<MutableList<ItemsModel>> {
        return repository.loadItems()
    }
}