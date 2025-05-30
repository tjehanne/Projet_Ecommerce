package fr.epf.min2.projet_ecommerce.ui.products

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.View.VISIBLE
import android.view.View.GONE
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import fr.epf.min2.projet_ecommerce.R
import fr.epf.min2.projet_ecommerce.databinding.FragmentProductsBinding
import fr.epf.min2.projet_ecommerce.ui.adapters.ProductAdapter
import fr.epf.min2.projet_ecommerce.data.FilterOptions
import fr.epf.min2.projet_ecommerce.data.SortOption
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import java.text.NumberFormat
import java.util.Locale

class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var productsViewModel: ProductsViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productsViewModel = ViewModelProvider(requireActivity())[ProductsViewModel::class.java]
        
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearch()
        observeViewModel()

        // Configurer le bouton de filtre
        binding.filterButton.setOnClickListener { _ ->
            showFilterBottomSheet()
        }

        // Charger les produits au démarrage
        productsViewModel.loadProducts()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { productId ->
            val bundle = Bundle().apply {
                putInt("productId", productId)
            }
            findNavController().navigate(R.id.nav_product_detail, bundle)
        }
        
        binding.productsRecyclerView.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupSearch() {
        // Référence au layout de recherche personnalisée
        val customSearchView = binding.searchCard.findViewById<View>(R.id.customSearchLayout)

        // Rendre toute la carte de recherche cliquable
        binding.searchCard.setOnClickListener {
            // Activer la SearchView réelle et cacher notre interface personnalisée
            customSearchView.visibility = View.GONE
            binding.searchView.visibility = View.VISIBLE

            // Obtenir la référence au EditText à l'intérieur du SearchView
            val searchEditText =
                binding.searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)

            // Forcer le focus et afficher le clavier
            searchEditText.requestFocus()
            showKeyboard(searchEditText)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        productsViewModel.searchProducts(it)
                    } else {
                        productsViewModel.loadProducts()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) {
                        // Réinitialiser la liste si la recherche est vide
                        productsViewModel.loadProducts()
                    } else if (it.length >= 2) {
                        // Recherche à partir de 2 caractères
                        productsViewModel.searchProducts(it)
                    }
                }
                return true
            }
        })

        // Gérer le retour à l'interface personnalisée lorsque la recherche perd le focus
        binding.searchView.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                // Si la recherche est vide, revenir à l'interface personnalisée
                if (binding.searchView.query.isEmpty()) {
                    customSearchView.visibility = View.VISIBLE
                    binding.searchView.visibility = View.GONE
                }
            }
        }

        // S'assurer que l'interface personnalisée est visible au début
        customSearchView.visibility = View.VISIBLE
        binding.searchView.visibility = View.GONE
    }
    
    private fun observeViewModel() {
        productsViewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
        }
        
        productsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) VISIBLE else GONE
        }
        
        productsViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                binding.errorMessage.visibility = VISIBLE
                binding.errorMessage.text = errorMessage
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            } else {
                binding.errorMessage.visibility = GONE
            }
        }

        productsViewModel.categories.observe(viewLifecycleOwner) { _ ->
            // Les catégories sont chargées, prêtes pour le filtre
        }

        // Observer les changements dans les options de filtrage
        productsViewModel.filterOptions.observe(viewLifecycleOwner) { filterOptions ->
            updateActiveFiltersChips(filterOptions)
        }
    }

    /**
     * Met à jour les chips de filtres actifs en fonction des options de filtrage
     */
    private fun updateActiveFiltersChips(filterOptions: FilterOptions) {
        // Effacer toutes les chips existantes
        binding.activeFiltersChipGroup.removeAllViews()

        val activeFilters = mutableListOf<Pair<String, () -> Unit>>()

        // Ajouter un filtre pour la catégorie si sélectionnée
        if (filterOptions.category.isNotEmpty()) {
            activeFilters.add(Pair("Catégorie: ${filterOptions.category}") {
                // Action pour supprimer le filtre de catégorie
                productsViewModel.updateFilterOptions(
                    filterOptions.copy(category = "")
                )
            })
        }

        // Vérifier si le prix a été manuellement modifié par l'utilisateur et n'est pas à ses valeurs par défaut
        val defaultMinPrice = productsViewModel.minPrice.value ?: 0.0
        val defaultMaxPrice = productsViewModel.maxPrice.value ?: 1000.0

        // N'afficher le filtre de prix que si l'utilisateur l'a défini manuellement
        if (filterOptions.isPriceManuallySet &&
            (filterOptions.minPrice > defaultMinPrice || filterOptions.maxPrice < defaultMaxPrice)
        ) {
            val formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE)
            activeFilters.add(
                Pair(
                    "Prix: ${formatter.format(filterOptions.minPrice)} - ${
                        formatter.format(
                            filterOptions.maxPrice
                        )
                    }"
                ) {
                    // Action pour réinitialiser le filtre de prix
                    productsViewModel.updateFilterOptions(
                        filterOptions.copy(
                            minPrice = defaultMinPrice,
                            maxPrice = defaultMaxPrice,
                            isPriceManuallySet = false
                        )
                    )
                })
        }

        // Ajouter un filtre pour le tri s'il n'est pas par défaut
        if (filterOptions.sortBy != SortOption.DEFAULT) {
            val sortText = when (filterOptions.sortBy) {
                SortOption.PRICE_LOW_TO_HIGH -> "Prix croissant"
                SortOption.PRICE_HIGH_TO_LOW -> "Prix décroissant"
                SortOption.RATING -> "Notes"
                else -> ""
            }

            if (sortText.isNotEmpty()) {
                activeFilters.add(Pair("Tri: $sortText") {
                    // Action pour réinitialiser le tri
                    productsViewModel.updateFilterOptions(
                        filterOptions.copy(sortBy = SortOption.DEFAULT)
                    )
                })
            }
        }

        // Ajouter les chips pour chaque filtre actif
        if (activeFilters.isNotEmpty()) {
            binding.activeFiltersScrollView.visibility = VISIBLE

            // Ajouter une chip "Effacer tout" si plusieurs filtres sont actifs
            if (activeFilters.size > 1) {
                val clearAllChip = createFilterChip("Effacer tout") {
                    productsViewModel.resetFilters()
                }
                binding.activeFiltersChipGroup.addView(clearAllChip)
            }

            // Ajouter les chips de filtres individuels
            activeFilters.forEach { (text, action) ->
                val chip = createFilterChip(text, action)
                binding.activeFiltersChipGroup.addView(chip)
            }
        } else {
            binding.activeFiltersScrollView.visibility = GONE
        }
    }

    /**
     * Crée une chip pour un filtre actif
     */
    private fun createFilterChip(text: String, onCloseAction: () -> Unit): Chip {
        return Chip(requireContext()).apply {
            this.text = text
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                onCloseAction()
            }

            // Styling des chips en utilisant les couleurs du thème
            isCheckable = false

            // Utiliser TypedValue pour récupérer les couleurs du thème
            val typedValue = android.util.TypedValue()
            val theme = requireContext().theme

            // Récupérer la couleur de surface variant
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorSurfaceVariant,
                typedValue,
                true
            )
            val backgroundColor = if (typedValue.resourceId != 0) {
                androidx.core.content.ContextCompat.getColor(
                    requireContext(),
                    typedValue.resourceId
                )
            } else {
                typedValue.data
            }

            // Récupérer la couleur de texte sur surface variant
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnSurfaceVariant,
                typedValue,
                true
            )
            val textColor = if (typedValue.resourceId != 0) {
                androidx.core.content.ContextCompat.getColor(
                    requireContext(),
                    typedValue.resourceId
                )
            } else {
                typedValue.data
            }

            // Récupérer la couleur primaire
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorPrimary,
                typedValue,
                true
            )
            val strokeColor = if (typedValue.resourceId != 0) {
                androidx.core.content.ContextCompat.getColor(
                    requireContext(),
                    typedValue.resourceId
                )
            } else {
                typedValue.data
            }

            setChipBackgroundColor(android.content.res.ColorStateList.valueOf(backgroundColor))
            setTextColor(textColor)
            setCloseIconTint(android.content.res.ColorStateList.valueOf(strokeColor))
            chipStrokeWidth = resources.getDimension(R.dimen.chip_stroke_width)
            setChipStrokeColor(android.content.res.ColorStateList.valueOf(strokeColor))

            // Marge pour espacer les chips
            (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                marginEnd = resources.getDimensionPixelSize(R.dimen.chip_margin)
            }
        }
    }

    private fun showFilterBottomSheet() {
        val filterOptions = productsViewModel.filterOptions.value ?: FilterOptions()
        val categories = productsViewModel.categories.value ?: listOf()
        val maxPrice = productsViewModel.maxPrice.value ?: 1000.0
        val minPrice = productsViewModel.minPrice.value ?: 0.0

        val filterBottomSheet = FilterBottomSheetFragment.newInstance(
            categories = categories,
            currentOptions = filterOptions,
            minPrice = minPrice,
            maxPrice = maxPrice,
            onFilterApplied = { options ->
                productsViewModel.updateFilterOptions(options)
            },
            onCategoryChanged = { category ->
                // Retourner les bornes de prix pour la catégorie sélectionnée
                productsViewModel.getPriceRangeForCategory(category)
            }
        )

        filterBottomSheet.show(parentFragmentManager, FilterBottomSheetFragment.TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Fonction utilitaire pour forcer l'affichage du clavier
     */
    private fun showKeyboard(view: View) {
        view.post {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}
