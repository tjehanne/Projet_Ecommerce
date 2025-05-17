package fr.epf.min2.projet_ecommerce.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fr.epf.min2.projet_ecommerce.databinding.FragmentCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCheckoutButton()
        observeViewModel()

        // Charger le panier
        cartViewModel.loadCart()
    }

    override fun onResume() {
        super.onResume()
        // Recharger le panier chaque fois que le fragment devient visible
        cartViewModel.loadCart()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onIncreaseQuantity = { productId ->
                cartViewModel.updateCartItemQuantity(productId, 1)
            },
            onDecreaseQuantity = { productId ->
                cartViewModel.updateCartItemQuantity(productId, -1)
            },
            onRemoveItem = { productId ->
                cartViewModel.removeFromCart(productId)
            }
        )

        binding.cartItemsRecyclerView.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupCheckoutButton() {
        binding.checkoutButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Commande validée ! Merci pour votre achat.",
                Toast.LENGTH_LONG
            ).show()
            cartViewModel.clearCart()
        }
    }

    private fun observeViewModel() {
        cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.submitList(cartItems)

            // Mettre à jour la visibilité des éléments en fonction du panier
            if (cartItems.isEmpty()) {
                binding.emptyCartMessage.visibility = View.VISIBLE
                binding.cartItemsRecyclerView.visibility = View.GONE
                binding.cartSummaryLayout.visibility = View.GONE
                binding.checkoutButton.visibility = View.GONE
            } else {
                binding.emptyCartMessage.visibility = View.GONE
                binding.cartItemsRecyclerView.visibility = View.VISIBLE
                binding.cartSummaryLayout.visibility = View.VISIBLE
                binding.checkoutButton.visibility = View.VISIBLE

                updateCartSummary(cartItems)
            }
        }

        cartViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun updateCartSummary(cartItems: List<CartItemWithProduct>) {
        val formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE)

        // Calculer le sous-total
        val subtotal = cartItems.sumOf { item ->
            (item.product?.price ?: 0.0) * item.quantity
        }

        // Frais de livraison fixes pour cet exemple
        val shipping = if (subtotal > 0) 5.99 else 0.0

        // Total
        val total = subtotal + shipping

        // Afficher les valeurs
        binding.subtotalTextView.text = formatter.format(subtotal)
        binding.shippingTextView.text = formatter.format(shipping)
        binding.totalTextView.text = formatter.format(total)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}