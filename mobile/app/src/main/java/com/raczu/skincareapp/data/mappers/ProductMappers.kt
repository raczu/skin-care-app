package com.raczu.skincareapp.data.mappers

import com.raczu.skincareapp.data.domain.models.product.Product
import com.raczu.skincareapp.data.domain.models.product.ProductCreate
import com.raczu.skincareapp.data.domain.models.product.ProductUpdate
import com.raczu.skincareapp.data.remote.dto.product.ProductCreateRequest
import com.raczu.skincareapp.data.remote.dto.product.ProductResponse
import com.raczu.skincareapp.data.remote.dto.product.ProductUpdateRequest

fun ProductResponse.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        brand = this.brand,
        purpose = this.purpose,
        description = this.description,
    )
}

fun ProductCreate.toRequest(): ProductCreateRequest {
    return ProductCreateRequest(
        name = this.name,
        brand = this.brand,
        purpose = this.purpose,
        description = this.description
    )
}

fun ProductUpdate.toRequest(): ProductUpdateRequest {
    return ProductUpdateRequest(
        name = this.name,
        brand = this.brand,
        purpose = this.purpose,
        description = this.description
    )
}
