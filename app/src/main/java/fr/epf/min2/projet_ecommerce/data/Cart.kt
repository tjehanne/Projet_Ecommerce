package fr.epf.min2.projet_ecommerce.data

import com.google.gson.annotations.SerializedName

data class Cart(
    val id: Int,
    val userId: Int,
    val date: String,
    val products: List<CartItem>,
    @SerializedName("__v")
    val version: Int
)

data class CartItem(
    val productId: Int,
    var quantity: Int,
    // Champs additionnels pour stocker les détails du produit en local
    var product: Product? = null
)

// Pour créer un nouvel article de panier localement
data class CartRequest(
    val userId: Int,
    val date: String,
    val products: List<CartItemRequest>
)

data class CartItemRequest(
    @SerializedName("productId")
    val productId: Int,
    val quantity: Int
)