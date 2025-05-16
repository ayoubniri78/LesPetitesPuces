// Créez ce fichier dans votre package model
// fichier: com/example/lespetitespuces/model/CartItemModel.kt
package com.example.lespetitespuces.model

import java.io.Serializable

data class CartItemModel(
    val itemId: String = "", // ID unique de l'ItemsModel (pourrait être son titre si unique, ou une vraie clé)
    var title: String = "",
    var price: Double = 0.0,
    var picUrl: String = "", // Juste la première URL pour l'affichage dans le panier
    var quantity: Int = 1,
    var stock: Int = 0 // Utile pour vérifier la disponibilité avant d'ajouter plus
) : Serializable {
    // Constructeur vide requis par Firebase
    constructor() : this("", "", 0.0, "", 1, 0)
}