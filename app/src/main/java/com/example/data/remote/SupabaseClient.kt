package com.example.data.remote

import android.util.Log
import com.example.data.model.Medicine
import com.example.data.model.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object SupabaseClient {
    private const val TAG = "SupabaseClient"
    private const val SUPABASE_URL = "https://oxzrzmwixhfcssspofpk.supabase.co/rest/v1"
    private const val ANON_KEY = "sb_publishable_D7z-EryNFkDzsFV8DPYcuA_5tfcTkun"

    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .build()

    var isLiveConnected: Boolean = true
        private set

    suspend fun insertOrderToSupabase(order: Order): Boolean = withContext(Dispatchers.IO) {
        try {
            val jsonObject = JSONObject().apply {
                put("id", order.id)
                put("customer_name", order.customerName)
                put("items", order.items)
                put("total_amount", order.totalAmount)
                put("eco_savings", order.ecoSavings)
                put("prescription_url", order.prescriptionUrl ?: "")
                put("status", order.status)
                put("created_at", System.currentTimeMillis())
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$SUPABASE_URL/orders")
                .addHeader("apikey", ANON_KEY)
                .addHeader("Authorization", "Bearer $ANON_KEY")
                .addHeader("Prefer", "return=minimal")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                isLiveConnected = response.isSuccessful
                if (response.isSuccessful) {
                    Log.d(TAG, "Order inserted to Supabase: ${order.orderNumber}")
                    return@withContext true
                } else {
                    Log.d(TAG, "Supabase order insert responded with HTTP ${response.code}, using local Room cache")
                }
            }
        } catch (e: Exception) {
            isLiveConnected = false
            Log.d(TAG, "Supabase network unavailable, proceeding seamlessly with local state: ${e.message}")
        }
        false
    }

    suspend fun fetchOrderStatusFromSupabase(orderId: String): String? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$SUPABASE_URL/orders?id=eq.$orderId&select=status")
                .addHeader("apikey", ANON_KEY)
                .addHeader("Authorization", "Bearer $ANON_KEY")
                .addHeader("Accept", "application/json")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                isLiveConnected = response.isSuccessful
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: return@withContext null
                    val jsonArray = JSONArray(bodyString)
                    if (jsonArray.length() > 0) {
                        return@withContext jsonArray.getJSONObject(0).optString("status")
                    }
                }
            }
        } catch (e: Exception) {
            isLiveConnected = false
            Log.d(TAG, "Supabase poll status bypassed: ${e.message}")
        }
        null
    }
}
