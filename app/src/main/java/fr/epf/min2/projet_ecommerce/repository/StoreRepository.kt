package fr.epf.min2.projet_ecommerce.repository

import fr.epf.min2.projet_ecommerce.api.RetrofitClient
import fr.epf.min2.projet_ecommerce.data.Cart
import fr.epf.min2.projet_ecommerce.data.CartItem
import fr.epf.min2.projet_ecommerce.data.CartItemRequest
import fr.epf.min2.projet_ecommerce.data.CartRequest
import fr.epf.min2.projet_ecommerce.data.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.util.Log

class StoreRepository {
    private val apiService = RetrofitClient.storeApiService

    // Utilisateur fictif pour le panier
    private val userId = 1

    // Utilisation d'un companion object pour partager le panier entre les instances
    companion object {
        private val localCart = mutableListOf<CartItem>()
    }

    suspend fun getAllProducts(): List<Product> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getAllProducts()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

    suspend fun getProductById(productId: Int): Product? {
        return withContext(Dispatchers.IO) {
            val response = apiService.getProductById(productId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        }
    }

    suspend fun getProductsByCategory(category: String): List<Product> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getProductsByCategory(category)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

    suspend fun getCategories(): List<String> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getCategories()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

    // Recherche locale de produits
    suspend fun searchProducts(query: String): List<Product> {
        val allProducts = getAllProducts()
        return allProducts.filter { product ->
            product.title.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true)
        }
    }

    // Gestion du panier en local
    fun getCart(): List<CartItem> {
        // Log debug pour afficher le contenu du panier
        Log.d("StoreRepository", "getCart(): ${localCart.size} articles dans le panier")
        return localCart.toList()  // Retourne une copie de la liste
    }

    suspend fun addToCart(productId: Int, quantity: Int = 1) {
        val existingItem = localCart.find { it.productId == productId }
        Log.d(
            "StoreRepository",
            "addToCart: id=$productId, qty=$quantity, existingItem=${existingItem != null}"
        )

        if (existingItem != null) {
            // Mettre à jour la quantité si le produit existe déjà
            existingItem.quantity += quantity
            Log.d("StoreRepository", "Quantité mise à jour: ${existingItem.quantity}")
        } else {
            // Ajouter un nouveau produit
            val product = getProductById(productId)
            val cartItem = CartItem(productId, quantity, product)
            localCart.add(cartItem)
            Log.d("StoreRepository", "Nouvel article ajouté, taille du panier: ${localCart.size}")
        }
    }

    fun removeFromCart(productId: Int) {
        val initialSize = localCart.size
        localCart.removeIf { it.productId == productId }
        Log.d(
            "StoreRepository",
            "removeFromCart: id=$productId, taille avant=$initialSize, taille après=${localCart.size}"
        )
    }

    fun clearCart() {
        val initialSize = localCart.size
        localCart.clear()
        Log.d(
            "StoreRepository",
            "clearCart: taille avant=$initialSize, taille après=${localCart.size}"
        )
    }

    suspend fun syncCartWithServer() {
        // Dans une application réelle, on synchroniserait avec le serveur
        // Ici, on simule l'envoi au serveur
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val currentDate = LocalDateTime.now().format(formatter)

        // Création de la requête panier (sera utilisée dans une implémentation réelle)
        @Suppress("UNUSED_VARIABLE")
        val cartRequest = CartRequest(
            userId = userId,
            date = currentDate,
            products = localCart.map { CartItemRequest(it.productId, it.quantity) }
        )

        withContext(Dispatchers.IO) {
            try {
                // On pourrait utiliser cette implémentation dans une application réelle
                // apiService.createCart(cartRequest)
                Log.d("StoreRepository", "Panier synchronisé avec le serveur")
            } catch (e: Exception) {
                Log.e("StoreRepository", "Erreur lors de la synchronisation du panier", e)
            }
        }
    }
}