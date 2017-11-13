package webengdus.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.lambdaworks.redis.RedisClient
import com.lambdaworks.redis.api.StatefulRedisConnection
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.sink.SinkRecord
import org.apache.kafka.connect.sink.SinkTask

class ShopRedisSinkTask : SinkTask() {

    companion object {
        const val SKU_LIST_KEY = "sku-list"
        const val PRODUCT_HM_KEY = "products"
    }

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())
    private lateinit var config: Map<String, String>
    private lateinit var redisConnection: StatefulRedisConnection<String, String>
    override fun version(): String = "1.0.0"

    override fun start(props: Map<String, String>) {
        config = props
        val redisClient: RedisClient = RedisClient.create(config["redis.uri"])
        redisConnection = redisClient.connect()
    }

    override fun stop() {
        redisConnection.close()
    }

    override fun put(records: Collection<SinkRecord>) {
        records.forEach {
            val k = it.key() as Struct
            val v = it.value() as Struct? ?: return@forEach
            val sku = k.getInt64("sku")

            when (v.getString("op")) {
                "d" -> delete(sku)
                else -> {
                    val after = v.getStruct("after")
                    upsert(Product(
                            sku = after.getInt64("sku"),
                            productName = after.getString("product_name"),
                            productDescription = after.getString("product_description"),
                            price = after.getFloat64("price"),
                            stock = after.getInt32("stock")))
                }
            }
        }
    }

    private fun delete(sku: Long) {
        redisConnection.sync().apply {
            srem(SKU_LIST_KEY, sku.toString())
            hdel(PRODUCT_HM_KEY, sku.toString())
        }
    }

    private fun upsert(product: Product) {
        redisConnection.sync().apply {
            sadd(SKU_LIST_KEY, product.sku.toString())
            hset(PRODUCT_HM_KEY, product.sku.toString(), objectMapper.writeValueAsString(product))
        }
    }
}