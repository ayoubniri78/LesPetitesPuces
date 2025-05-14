package com.example.lespetitespuces.ui.theme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lespetitespuces.Model.CategoryModel
import com.example.lespetitespuces.Repository.MainRepository

class MainViewModel : ViewModel() {
    private val repository = MainRepository()
    fun loadCategory(): LiveData<MutableList<CategoryModel>>{
        return repository.loadCategory()
    }
}