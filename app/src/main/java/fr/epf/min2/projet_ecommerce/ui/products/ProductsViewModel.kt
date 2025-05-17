package fr.epf.min2.projet_ecommerce.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.epf.min2.projet_ecommerce.data.FilterOptions
import fr.epf.min2.projet_ecommerce.data.Product
import fr.epf.min2.projet_ecommerce.data.SortOption
import fr.epf.min2.projet_ecommerce.repository.StoreRepository
import kotlinx.coroutines.launch

class ProductsViewModel : ViewModel() {

    private val repository = StoreRepository()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _minPrice = MutableLiveData<Double>(0.0)
    val minPrice: LiveData<Double> = _minPrice

    private val _maxPrice = MutableLiveData<Double>(1000.0)
    val maxPrice: LiveData<Double> = _maxPrice

    private val _filterOptions = MutableLiveData(FilterOptions())
    val filterOptions: LiveData<FilterOptions> = _filterOptions

    // Liste complète des produits pour filtrer localement
    private var allProductsList = listOf<Product>()

    // Map pour stocker les prix min et max par catégorie
    private val categoryPriceRanges = mutableMapOf<String, Pair<Double, Double>>()

    init {
        loadCategories()
    }

    fun loadProducts() {
        _isLoading.value = true
        _error.value = ""

        viewModelScope.launch {
            try {
                // Sauvegarder les filtres actuels avant de charger les données
                val currentFilters = _filterOptions.value ?: FilterOptions()

                val productsList = repository.getAllProducts()
                allProductsList = productsList

                if (productsList.isNotEmpty()) {
                    val highestPrice = productsList.maxOf { it.price }
                    val lowestPrice = productsList.minOf { it.price }

                    _maxPrice.value = highestPrice
                    _minPrice.value = lowestPrice

                    // Calculer et stocker les plages de prix par catégorie
                    calculatePriceRangesByCategory(productsList)

                    // Si on n'avait pas de filtres personnalisés, alors seulement initialiser avec les valeurs par défaut
                    if (currentFilters == FilterOptions()) {
                        _filterOptions.value = FilterOptions(
                            category = "",
                            minPrice = lowestPrice,
                            maxPrice = highestPrice,
                            sortBy = SortOption.DEFAULT,
                            isPriceManuallySet = false
                        )
                    } else {
                        _filterOptions.value = currentFilters
                    }
                }

                applyFilters()
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Erreur lors du chargement des produits: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun searchProducts(query: String) {
        _isLoading.value = true
        _error.value = ""

        viewModelScope.launch {
            try {
                val searchResults = repository.searchProducts(query)
                _products.value = searchResults
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Erreur lors de la recherche: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categoriesList = repository.getCategories()
                _categories.value = categoriesList
            } catch (e: Exception) {
                // En cas d'erreur, on ne fait rien
            }
        }
    }

    fun updateFilterOptions(newOptions: FilterOptions) {
        // Si une nouvelle catégorie est sélectionnée, mettre à jour les bornes de prix
        val previousCategory = _filterOptions.value?.category ?: ""
        if (previousCategory != newOptions.category && newOptions.category.isNotEmpty()) {
            categoryPriceRanges[newOptions.category]?.let { (min, max) ->
                // Utiliser les bornes de prix pour la catégorie sélectionnée
                _filterOptions.value = newOptions.copy(
                    minPrice = min,
                    maxPrice = max
                )
            } ?: run {
                // Si nous n'avons pas de données pour cette catégorie, utiliser les valeurs fournies
                _filterOptions.value = newOptions
            }
        } else {
            _filterOptions.value = newOptions
        }
        applyFilters()
    }

    fun resetFilters() {
        val globalMinPrice = _minPrice.value ?: 0.0
        val globalMaxPrice = _maxPrice.value ?: 1000.0

        _filterOptions.value = FilterOptions(
            category = "",
            minPrice = globalMinPrice,
            maxPrice = globalMaxPrice,
            sortBy = SortOption.DEFAULT,
            isPriceManuallySet = false
        )
        applyFilters()
    }

    private fun applyFilters() {
        val options = _filterOptions.value ?: return

        var filteredList = allProductsList

        // Filtrer par catégorie
        if (options.category.isNotEmpty()) {
            filteredList = filteredList.filter { it.category == options.category }
        }

        // Filtrer par prix
        filteredList = filteredList.filter { product ->
            product.price >= options.minPrice && product.price <= options.maxPrice
        }

        // Appliquer le tri
        filteredList = when (options.sortBy) {
            SortOption.PRICE_LOW_TO_HIGH -> filteredList.sortedBy { it.price }
            SortOption.PRICE_HIGH_TO_LOW -> filteredList.sortedByDescending { it.price }
            SortOption.RATING -> filteredList.sortedByDescending { it.rating.rate }
            SortOption.DEFAULT -> filteredList
        }

        _products.value = filteredList
    }

    /**
     * Calcule et stocke les plages de prix min/max par catégorie
     */
    private fun calculatePriceRangesByCategory(products: List<Product>) {
        // Grouper les produits par catégorie
        val productsByCategory = products.groupBy { it.category }

        // Pour chaque catégorie, calculer le prix min et max
        productsByCategory.forEach { (category, productsInCategory) ->
            if (productsInCategory.isNotEmpty()) {
                val minPrice = productsInCategory.minOf { it.price }
                val maxPrice = productsInCategory.maxOf { it.price }
                categoryPriceRanges[category] = Pair(minPrice, maxPrice)
            }
        }
    }

    /**
     * Retourne les bornes de prix pour une catégorie spécifique
     */
    fun getPriceRangeForCategory(category: String): Pair<Double, Double> {
        return if (category.isEmpty()) {
            Pair(_minPrice.value ?: 0.0, _maxPrice.value ?: 1000.0)
        } else {
            categoryPriceRanges[category] ?: Pair(_minPrice.value ?: 0.0, _maxPrice.value ?: 1000.0)
        }
    }
}