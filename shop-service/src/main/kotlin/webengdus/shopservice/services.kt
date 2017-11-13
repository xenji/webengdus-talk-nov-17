package webengdus.shopservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ProductService(private val objectMapper: ObjectMapper,
                     private val redisTemplate: RedisTemplate<String, String>) {
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

    /**
     * Get the product, map the purchase statistics on the fly and return it.
     */
    fun getProduct(sku: Long): Product? =
            hmOps.get("products", sku.toString())?.apply {
                soldOverTime = valOps.get("sold-$sku")?.toInt() ?: 0
                otherPeopleBought = setOps.members("compliment-$sku").let {
                    valOps.multiGet(it).map { objectMapper.readValue(it, Product::class.java) }.toSet()
                }
            }

}

@Service
class PurchaseAnalyticsService(
        private val objectMapper: ObjectMapper,
        private val kafkaProducer: KafkaProducer<Bytes, String>) {

    fun emitRecord(analyticsRecord: AnalyticsRecord) =
            kafkaProducer.send(ProducerRecord("shop.purchases", objectMapper.writeValueAsString(analyticsRecord))).get()

}
