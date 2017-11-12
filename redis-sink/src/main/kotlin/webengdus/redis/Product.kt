package webengdus.redis

import java.io.Serializable

data class Product(
        var sku: Long? = null,
        var productName: String? = null,
        var productDescription: String? = null,
        var price: Double? = null,
        var stock: Int = 0): Serializable