package fr.epf.min2.projet_ecommerce.ui.productdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import fr.epf.min2.projet_ecommerce.databinding.FragmentProductDetailBinding
import java.text.NumberFormat
import java.util.Locale

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ProductDetailViewModel::class.java]

        // Récupérer l'identifiant du produit passé en argument
        val productId = arguments?.getInt("productId", -1) ?: -1
        if (productId == -1) {
            binding.errorMessage.visibility = View.VISIBLE
            return
        }

        setupObservers()

        // Charger les détails du produit
        viewModel.loadProductDetails(productId)

        // Variables pour la gestion de la quantité
        var quantity = 1

        // Configuration des boutons de quantité
        binding.decreaseButton.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.quantityText.text = quantity.toString()
            }
        }

        binding.increaseButton.setOnClickListener {
            quantity++
            binding.quantityText.text = quantity.toString()
        }

        // Configuration du bouton d'ajout au panier
        binding.addToCartButton.setOnClickListener {
            viewModel.addToCart(productId, quantity)
            Toast.makeText(
                requireContext(),
                "Ajout de $quantity article(s) au panier",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupObservers() {
        viewModel.product.observe(viewLifecycleOwner) { product ->
            product?.let {
                // Formater le prix en EUR
                val formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE)

                binding.apply {
                    productTitle.text = it.title
                    productPrice.text = formatter.format(it.price)
                    productCategory.text = it.category
                    productDescription.text = it.description
                    productRating.rating = it.rating.rate.toFloat()
                    ratingCount.text = "(${it.rating.count} avis)"

                    // Charger l'image avec Glide
                    Glide.with(requireContext())
                        .load(it.imageUrl)
                        .into(productImage)
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = errorMessage
            } else {
                binding.errorMessage.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}