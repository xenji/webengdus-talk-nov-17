package webengdus.shopservice

import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ProductService(private val redisTemplate: RedisTemplate<String, String>) {
    private lateinit var setOps: SetOperations<String, String>
    private lateinit var hmOps: HashOperations<String, String, Product>
    private lateinit var valOps: ValueOperations<String, String>

    @PostConstruct
    fun init() {
        setOps = redisTemplate.opsForSet()
        hmOps = redisTemplate.opsForHash<String, Product>()
        valOps = redisTemplate.opsForValue()
    }

    fun getProducts(): List<Product> =
            setOps.members("sku-list").let { skuList ->
                val allKeys = skuList.map { "sold-$it" }
                val soldCount = allKeys.zip(valOps.multiGet(allKeys)).associate { it }
                hmOps.multiGet("products", skuList).map {
                    it.soldOverTime = soldCount["sold-${it.sku}"]?.toInt() ?: 0
                    it
                }
            }
}

