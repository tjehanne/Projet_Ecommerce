package fr.epf.min2.projet_ecommerce.ui.cart

import fr.epf.min2.projet_ecommerce.data.Product

data class CartItemWithProduct(
    val productId: Int,
    val quantity: Int,
    val product: Product?
)