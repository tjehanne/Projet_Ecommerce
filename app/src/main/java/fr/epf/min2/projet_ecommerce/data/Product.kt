package fr.epf.min2.projet_ecommerce.data

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    @SerializedName("image")
    val imageUrl: String,
    val rating: Rating
)

data class Rating(
    val rate: Double,
    val count: Int
)