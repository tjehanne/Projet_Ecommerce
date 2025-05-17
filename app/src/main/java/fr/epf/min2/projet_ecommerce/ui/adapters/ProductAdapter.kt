package fr.epf.min2.projet_ecommerce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.min2.projet_ecommerce.data.Product
import fr.epf.min2.projet_ecommerce.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(private val onProductClick: (productId: Int) -> Unit) :
    ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = getItem(position)
                    onProductClick(product.id)
                }
            }
        }

        fun bind(product: Product) {
            // Formater le prix en EUR
            val formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE)

            binding.apply {
                productTitle.text = product.title
                productPrice.text = formatter.format(product.price)
                productCategory.text = product.category
                productRating.rating = product.rating.rate.toFloat()
                ratingCount.text = "(${product.rating.count})"

                // Charger l'image avec Glide
                Glide.with(productImage)
                    .load(product.imageUrl)
                    .centerCrop()
                    .into(productImage)
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}