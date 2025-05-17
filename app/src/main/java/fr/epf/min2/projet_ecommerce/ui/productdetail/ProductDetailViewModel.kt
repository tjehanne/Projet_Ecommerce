package fr.epf.min2.projet_ecommerce.ui.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.epf.min2.projet_ecommerce.data.Product
import fr.epf.min2.projet_ecommerce.repository.StoreRepository
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {

    private val repository = StoreRepository()

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadProductDetails(productId: Int) {
        _isLoading.value = true
        _error.value = ""

        viewModelScope.launch {
            try {
                val productDetails = repository.getProductById(productId)
                if (productDetails != null) {
                    _product.postValue(productDetails)
                } else {
                    _error.postValue("Produit non trouvé")
                }
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _error.postValue("Erreur lors du chargement du produit: ${e.message}")
                _isLoading.postValue(false)
            }
        }
    }

    fun addToCart(productId: Int, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                android.util.Log.d(
                    "ProductDetailViewModel",
                    "Ajout au panier: id=$productId, qty=$quantity"
                )
                repository.addToCart(productId, quantity)

                // Afficher le contenu du panier après l'ajout pour déboguer
                val cart = repository.getCart()
                android.util.Log.d(
                    "ProductDetailViewModel",
                    "Panier après ajout: ${cart.size} articles"
                )
                for (item in cart) {
                    android.util.Log.d(
                        "ProductDetailViewModel",
                        "Article dans panier: id=${item.productId}, qty=${item.quantity}"
                    )
                }

                _error.postValue("") // Effacer les erreurs précédentes
            } catch (e: Exception) {
                android.util.Log.e("ProductDetailViewModel", "Erreur lors de l'ajout au panier", e)
                _error.postValue("Erreur lors de l'ajout au panier: ${e.message}")
            }
        }
    }
}