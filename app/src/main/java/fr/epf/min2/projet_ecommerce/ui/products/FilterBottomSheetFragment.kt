package fr.epf.min2.projet_ecommerce.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import fr.epf.min2.projet_ecommerce.R
import fr.epf.min2.projet_ecommerce.data.FilterOptions
import fr.epf.min2.projet_ecommerce.data.SortOption
import fr.epf.min2.projet_ecommerce.databinding.LayoutFilterBottomSheetBinding

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: LayoutFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var onFilterApplied: ((FilterOptions) -> Unit)? = null
    private var currentFilterOptions = FilterOptions()
    private var categories = listOf<String>()
    private var minPrice = 0.0
    private var maxPrice = 1000.0
    private var onCategoryChanged: ((String) -> Pair<Double, Double>)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryChips()
        setupPriceRangeSlider()
        setupSortOptions()
        setupButtons()

        // Initialiser l'UI avec les options actuelles
        updateUIFromFilterOptions()
        updatePriceRangeForCategory(currentFilterOptions.category)
    }

    private fun setupCategoryChips() {
        binding.categoryChipGroup.removeAllViews()

        // Ajouter une puce pour "Toutes les catégories"
        val allCategoriesChip = createCategoryChip("Toutes")
        allCategoriesChip.isChecked = currentFilterOptions.category.isEmpty()
        updateChipTextColor(allCategoriesChip, allCategoriesChip.isChecked)
        allCategoriesChip.setOnCheckedChangeListener { chipView, isChecked ->
            updateChipTextColor(chipView as Chip, isChecked)
            if (isChecked) {
                currentFilterOptions.category = ""
                updatePriceRangeForCategory("")
            }
        }
        binding.categoryChipGroup.addView(allCategoriesChip)

        // Ajouter des puces pour chaque catégorie
        categories.forEach { category ->
            val chip = createCategoryChip(category)
            chip.isChecked = currentFilterOptions.category == category
            updateChipTextColor(chip, chip.isChecked)
            chip.setOnCheckedChangeListener { chipView, isChecked ->
                updateChipTextColor(chipView as Chip, isChecked)
                if (isChecked) {
                    currentFilterOptions.category = category
                    updatePriceRangeForCategory(category)
                }
            }
            binding.categoryChipGroup.addView(chip)
        }
    }

    private fun createCategoryChip(text: String): Chip {
        return Chip(requireContext()).apply {
            this.text = text
            isCheckable = true
            setChipBackgroundColorResource(R.color.filter_chip_background)
            setTextColor(resources.getColor(R.color.filter_chip_text, null))
            setCheckedIconVisible(true)
            setChipStrokeColorResource(R.color.filter_chip_selected)
            setChipStrokeWidthResource(R.dimen.chip_stroke_width)
        }
    }

    private fun setupPriceRangeSlider() {
        binding.priceRangeSlider.valueFrom = minPrice.toFloat()
        binding.priceRangeSlider.valueTo = maxPrice.toFloat()

        binding.priceRangeSlider.addOnChangeListener { slider, _, fromUser ->
            val values = slider.values
            currentFilterOptions.minPrice = values[0].toDouble()
            currentFilterOptions.maxPrice = values[1].toDouble()

            // Marquer que le prix a été manuellement modifié seulement si c'est un changement utilisateur
            if (fromUser) {
                currentFilterOptions.isPriceManuallySet = true
            }

            updatePriceRangeText()
        }
    }

    private fun setupSortOptions() {
        binding.sortRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            currentFilterOptions.sortBy = when (checkedId) {
                R.id.sortPriceLowRadio -> SortOption.PRICE_LOW_TO_HIGH
                R.id.sortPriceHighRadio -> SortOption.PRICE_HIGH_TO_LOW
                R.id.sortRatingRadio -> SortOption.RATING
                else -> SortOption.DEFAULT
            }
        }
    }

    private fun setupButtons() {
        binding.applyFiltersButton.setOnClickListener {
            onFilterApplied?.invoke(currentFilterOptions)
            dismiss()
        }

        binding.resetFiltersButton.setOnClickListener {
            currentFilterOptions = FilterOptions()
            currentFilterOptions.isPriceManuallySet = false
            updateUIFromFilterOptions()
        }
    }

    private fun updatePriceRangeText() {
        val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.FRANCE)
        binding.priceRangeText.text =
            "${formatter.format(currentFilterOptions.minPrice)} - ${
                formatter.format(
                    currentFilterOptions.maxPrice
                )
            }"
    }

    private fun updateUIFromFilterOptions() {
        for (i in 0 until binding.categoryChipGroup.childCount) {
            val chip = binding.categoryChipGroup.getChildAt(i) as Chip
            if (i == 0) {
                chip.isChecked = currentFilterOptions.category.isEmpty()
            } else {
                val category = categories.getOrNull(i - 1) ?: continue
                chip.isChecked = category == currentFilterOptions.category
            }
            updateChipTextColor(chip, chip.isChecked)
        }

        // Mettre à jour le slider de prix
        binding.priceRangeSlider.valueFrom = minPrice.toFloat()
        binding.priceRangeSlider.valueTo = maxPrice.toFloat()

        // S'assurer que minPrice dans les options n'est pas inférieur au minimum global
        if (currentFilterOptions.minPrice < minPrice) {
            currentFilterOptions.minPrice = minPrice
        }

        // S'assurer que maxPrice dans les options ne dépasse pas le maximum global
        if (currentFilterOptions.maxPrice > maxPrice) {
            currentFilterOptions.maxPrice = maxPrice
        }

        binding.priceRangeSlider.values = listOf(
            currentFilterOptions.minPrice.toFloat(),
            currentFilterOptions.maxPrice.toFloat()
        )
        updatePriceRangeText()

        // Mettre à jour les options de tri
        val radioButtonId = when (currentFilterOptions.sortBy) {
            SortOption.PRICE_LOW_TO_HIGH -> R.id.sortPriceLowRadio
            SortOption.PRICE_HIGH_TO_LOW -> R.id.sortPriceHighRadio
            SortOption.RATING -> R.id.sortRatingRadio
            SortOption.DEFAULT -> R.id.sortDefaultRadio
        }
        binding.sortRadioGroup.check(radioButtonId)
    }

    /**
     * Met à jour les bornes du slider de prix en fonction de la catégorie sélectionnée
     */
    private fun updatePriceRangeForCategory(category: String) {
        onCategoryChanged?.let { callback ->
            val (newMin, newMax) = callback(category)

            // Mettre à jour les bornes du slider
            binding.priceRangeSlider.valueFrom = newMin.toFloat()
            binding.priceRangeSlider.valueTo = newMax.toFloat()

            // Si le prix n'a pas été défini manuellement ou si on change de catégorie, on réinitialise à la plage de la catégorie
            if (!currentFilterOptions.isPriceManuallySet || category != currentFilterOptions.category) {
                // Mettre à jour les valeurs actuelles du filtre
                currentFilterOptions.minPrice = newMin
                currentFilterOptions.maxPrice = newMax

                // Réinitialiser le flag car c'est un changement automatique
                currentFilterOptions.isPriceManuallySet = false

                // Mettre à jour le slider avec les nouvelles valeurs
                binding.priceRangeSlider.values = listOf(newMin.toFloat(), newMax.toFloat())
            }

            // Mettre à jour le texte affiché
            updatePriceRangeText()
        }
    }

    private fun updateChipTextColor(chip: Chip, isChecked: Boolean) {
        if (isChecked) {
            chip.setTextColor(resources.getColor(R.color.filter_chip_text_selected, null))
        } else {
            chip.setTextColor(resources.getColor(R.color.filter_chip_text, null))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FilterBottomSheetFragment"

        fun newInstance(
            categories: List<String>,
            currentOptions: FilterOptions,
            minPrice: Double,
            maxPrice: Double,
            onFilterApplied: (FilterOptions) -> Unit,
            onCategoryChanged: (String) -> Pair<Double, Double>
        ): FilterBottomSheetFragment {
            return FilterBottomSheetFragment().apply {
                this.categories = categories
                this.currentFilterOptions = currentOptions.copy()
                this.minPrice = minPrice
                this.maxPrice = maxPrice
                this.onFilterApplied = onFilterApplied
                this.onCategoryChanged = onCategoryChanged
            }
        }
    }
}