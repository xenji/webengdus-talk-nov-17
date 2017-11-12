package webengdus.shopservice

import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SetOperations
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ProductService(private val redisTemplate: RedisTemplate<String, String>) {
    private lateinit var setOps: SetOperations<String, String>
    private lateinit var hmOps: HashOperations<String, String, Product>

    @PostConstruct
    fun init() {
        setOps = redisTemplate.opsForSet()
        hmOps = redisTemplate.opsForHash<String, Product>()
    }

    fun getProducts(): List<Product> =
            setOps.members("sku-list").let { hmOps.multiGet("products", it) }
}

