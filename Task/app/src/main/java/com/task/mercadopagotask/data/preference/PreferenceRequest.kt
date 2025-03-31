package com.task.mercadopagotask.data.preference

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

data class PreferenceRequest(
    @SerializedName("items") val items: List<Item>,
    @SerializedName("back_urls") val backUrls: BackUrls? = null,
    @SerializedName("auto_return") val autoReturn: String? = "all"
)

data class Item(
    @SerializedName("title") val title: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unit_price") val unitPrice: BigDecimal
)

data class BackUrls(
    @SerializedName("success") val success: String,
    @SerializedName("failure") val failure: String,
    @SerializedName("pending") val pending: String
)

data class PreferenceResponse(
    @SerializedName("id") val id: String,
    @SerializedName("init_point") val initPoint: String
)

interface MercadoPagoApiService {
    @POST("checkout/preferences")
    suspend fun createPreference(
        @Header("Authorization") authorization: String,
        @Body preferenceRequest: PreferenceRequest
    ): PreferenceResponse
}

class MercadoPagoService {
    private val apiService: MercadoPagoApiService

    private val accessToken = "APP_USR-3370587752120611-033100-dfbc6e7f7dc01d3ac75db8168fa2abe1-2363575480"

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.mercadopago.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(MercadoPagoApiService::class.java)
    }

    suspend fun createPaymentPreference(
        amount: BigDecimal,
        description: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {

            val item = Item(
                title = description,
                quantity = 1,
                unitPrice = amount
            )

            val request = PreferenceRequest(
                items = listOf(item),
                backUrls = BackUrls(
                    success = "yourapp://congrats/success",
                    failure = "yourapp://congrats/failure",
                    pending = "yourapp://congrats/pending"
                ),
                autoReturn = "all"
            )

            val response = apiService.createPreference("Bearer $accessToken", request)

            Result.success(response.initPoint)

        } catch (e: Exception) {
            Result.failure(Exception("Erro ao criar preferência: ${e.message}"))
        }
    }
}