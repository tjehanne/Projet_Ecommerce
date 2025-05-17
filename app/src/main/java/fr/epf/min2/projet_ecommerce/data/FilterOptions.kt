package fr.epf.min2.projet_ecommerce.data

/**
 * Classe qui représente les options de filtrage pour les produits
 */
data class FilterOptions(
    var category: String = "",
    var minPrice: Double = 0.0,
    var maxPrice: Double = 1000.0,
    var sortBy: SortOption = SortOption.DEFAULT,
    var isPriceManuallySet: Boolean = false
)

/**
 * Options de tri disponibles
 */
enum class SortOption {
    DEFAULT,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    RATING
}