package fr.epf.min2.projet_ecommerce.api

import fr.epf.min2.projet_ecommerce.data.Cart
import fr.epf.min2.projet_ecommerce.data.CartRequest
import fr.epf.min2.projet_ecommerce.data.Product
import retrofit2.Response
import retrofit2.http.*

interface StoreApiService {
    @GET("products")
    suspend fun getAllProducts(): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: Int): Response<Product>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): Response<List<Product>>

    @GET("products/categories")
    suspend fun getCategories(): Response<List<String>>

    // Recherche - Note: l'API FakeStoreAPI ne supporte pas directement la recherche
    // mais on peut filtrer les produits côté client après les avoir récupérés

    // Panier
    @GET("carts/user/{userId}")
    suspend fun getUserCart(@Path("userId") userId: Int): Response<List<Cart>>

    @POST("carts")
    suspend fun createCart(@Body cartRequest: CartRequest): Response<Cart>

    @PUT("carts/{id}")
    suspend fun updateCart(@Path("id") cartId: Int, @Body cartRequest: CartRequest): Response<Cart>

    @DELETE("carts/{id}")
    suspend fun deleteCart(@Path("id") cartId: Int): Response<Unit>
}