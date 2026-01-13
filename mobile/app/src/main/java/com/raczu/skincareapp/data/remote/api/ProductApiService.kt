package com.raczu.skincareapp.data.remote.api

import com.raczu.skincareapp.data.remote.dto.PagedResponse
import com.raczu.skincareapp.data.remote.dto.product.ProductCreateRequest
import com.raczu.skincareapp.data.remote.dto.product.ProductResponse
import com.raczu.skincareapp.data.remote.dto.product.ProductUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @POST("products")
    suspend fun addProduct(@Body request: ProductCreateRequest): Response<ProductResponse>

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<PagedResponse<ProductResponse>>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") productId: String): Response<Unit>

    @PATCH("products/{id}")
    suspend fun updateProduct(
        @Path("id") productId: String,
        @Body request: ProductUpdateRequest
    ): Response<ProductResponse>

    @GET("products/{id}")
    suspend fun getProductDetails(@Path("id") productId: String): Response<ProductResponse>
}