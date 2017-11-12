package webengdus.shopservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.stereotype.Component

@Component
class RedisProductSerializer(private val objectMapper: ObjectMapper) : RedisSerializer<Product> {
    override fun serialize(t: Product?): ByteArray =
            objectMapper.writeValueAsBytes(t)

    override fun deserialize(bytes: ByteArray?): Product =
            objectMapper.readValue(bytes, Product::class.java)
}