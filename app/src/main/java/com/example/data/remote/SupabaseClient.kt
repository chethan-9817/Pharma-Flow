package com.example.data.remote

import android.util.Log
import com.example.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.concurrent.TimeUnit

object SupabaseClient {
    private const val TAG = "SupabaseClient"
    private const val SUPABASE_URL = "https://oxzrzmwixhfcssspofpk.supabase.co/rest/v1"
    
    // Public anon key placeholder or fallback anon authorization
    private const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.e30.placeholder"

    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .build()

    /**
     * Attempts to fetch products from Supabase REST endpoint.
     * Returns null gracefully if table does not exist or network is offline,
     * allowing instant Room database fallback.
     */
    suspend fun fetchProductsFromSupabase(): List<Product>? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$SUPABASE_URL/products?select=*")
                .addHeader("apikey", ANON_KEY)
                .addHeader("Authorization", "Bearer $ANON_KEY")
                .addHeader("Accept", "application/json")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: return@withContext null
                    val jsonArray = JSONArray(bodyString)
                    val products = mutableListOf<Product>()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        products.add(
                            Product(
                                id = obj.optString("id", "prod_$i"),
                                title = obj.optString("title", "Product $i"),
                                description = obj.optString("description", ""),
                                category = obj.optString("category", "General"),
                                price = obj.optDouble("price", 19.99),
                                originalPrice = obj.optDouble("original_price", 29.99),
                                rating = obj.optDouble("rating", 4.5).toFloat(),
                                reviewCount = obj.optInt("review_count", 120),
                                badge = obj.optString("badge", ""),
                                iconKey = obj.optString("icon_key", "gadget")
                            )
                        )
                    }
                    if (products.isNotEmpty()) {
                        Log.d(TAG, "Successfully loaded ${products.size} products from Supabase")
                        return@withContext products
                    }
                } else {
                    Log.d(TAG, "Supabase table not found or unavailable (HTTP ${response.code}), using offline Room cache")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Supabase fetch bypassed smoothly (offline/latency): ${e.message}")
        }
        null
    }
}
