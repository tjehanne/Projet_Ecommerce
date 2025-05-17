package fr.epf.min2.projet_ecommerce.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.min2.projet_ecommerce.databinding.ItemCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val onIncreaseQuantity: (Int) -> Unit,
    private val onDecreaseQuantity: (Int) -> Unit,
    private val onRemoveItem: (Int) -> Unit
) : ListAdapter<CartItemWithProduct, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItemWithProduct) {
            val product = item.product
            val formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE)

            binding.apply {
                productTitle.text = product?.title ?: "Produit inconnu"
                productPrice.text = product?.price?.let { formatter.format(it) } ?: "N/A"
                quantityText.text = item.quantity.toString()

                // Bouton pour diminuer la quantité
                decreaseButton.setOnClickListener {
                    onDecreaseQuantity(item.productId)
                }

                // Bouton pour augmenter la quantité
                increaseButton.setOnClickListener {
                    onIncreaseQuantity(item.productId)
                }

                // Bouton pour supprimer l'article
                deleteButton.setOnClickListener {
                    onRemoveItem(item.productId)
                }

                // Charger l'image avec Glide si le produit existe
                product?.let {
                    Glide.with(productImage)
                        .load(it.imageUrl)
                        .centerCrop()
                        .into(productImage)
                }
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItemWithProduct>() {
        override fun areItemsTheSame(
            oldItem: CartItemWithProduct,
            newItem: CartItemWithProduct
        ): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(
            oldItem: CartItemWithProduct,
            newItem: CartItemWithProduct
        ): Boolean {
            return oldItem == newItem
        }
    }
}