package fr.epf.min2.projet_ecommerce.ui.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.epf.min2.projet_ecommerce.data.CartItem
import fr.epf.min2.projet_ecommerce.repository.StoreRepository
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val repository = StoreRepository()

    private val _cartItems = MutableLiveData<List<CartItemWithProduct>>()
    val cartItems: LiveData<List<CartItemWithProduct>> = _cartItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCart() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val cart = repository.getCart()

                // Log debug pour afficher le contenu du panier
                android.util.Log.d(
                    "CartViewModel",
                    "Nombre d'articles dans le panier: ${cart.size}"
                )
                for (item in cart) {
                    android.util.Log.d(
                        "CartViewModel",
                        "Article: id=${item.productId}, qty=${item.quantity}, product=${item.product?.title}"
                    )
                }

                // Transformation CartItem -> CartItemWithProduct
                val cartWithProducts = cart.map { item ->
                    CartItemWithProduct(
                        productId = item.productId,
                        quantity = item.quantity,
                        product = item.product
                    )
                }

                _cartItems.value = cartWithProducts
                _isLoading.value = false
            } catch (e: Exception) {
                android.util.Log.e("CartViewModel", "Erreur lors du chargement du panier", e)
                _cartItems.value = emptyList()
                _isLoading.value = false
            }
        }
    }

    fun updateCartItemQuantity(productId: Int, change: Int) {
        val currentList = _cartItems.value?.toMutableList() ?: mutableListOf()

        val itemIndex = currentList.indexOfFirst { it.productId == productId }
        if (itemIndex != -1) {
            val currentItem = currentList[itemIndex]
            val newQuantity = currentItem.quantity + change

            if (newQuantity <= 0) {
                // Si la quantité est 0 ou moins, supprimer l'article
                removeFromCart(productId)
            } else {
                // Mettre à jour la quantité
                val updatedItem = currentItem.copy(quantity = newQuantity)
                currentList[itemIndex] = updatedItem
                _cartItems.value = currentList

                // Mettre à jour le panier dans le repository
                viewModelScope.launch {
                    try {
                        repository.addToCart(productId, change)
                    } catch (e: Exception) {
                        // Rollback en cas d'erreur
                        loadCart()
                    }
                }
            }
        }
    }

    fun removeFromCart(productId: Int) {
        val currentList = _cartItems.value?.toMutableList() ?: mutableListOf()

        // Supprimer l'article de la liste locale
        currentList.removeIf { it.productId == productId }
        _cartItems.value = currentList

        // Mettre à jour le panier dans le repository
        viewModelScope.launch {
            try {
                repository.removeFromCart(productId)
            } catch (e: Exception) {
                // Rollback en cas d'erreur
                loadCart()
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()

        viewModelScope.launch {
            try {
                repository.clearCart()
            } catch (e: Exception) {
                loadCart()
            }
        }
    }
}