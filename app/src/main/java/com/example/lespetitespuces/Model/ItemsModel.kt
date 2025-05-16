package com.example.lespetitespuces.model

import java.io.Serializable

data class ItemsModel(
    var title: String = "",
    var description: String = "",
    var picUrl: ArrayList<String> = ArrayList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var categoryId: String = "",
    var stock: Int= 1
) : Serializable
