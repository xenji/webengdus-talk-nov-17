package webengdus.shopservice

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfiguration {

    @Bean
    @Primary
    fun redisTemplate(rcf: RedisConnectionFactory, redisProductSerializer: RedisProductSerializer): RedisTemplate<String, String> {
        val rt = RedisTemplate<String, String>()
        rt.connectionFactory = rcf
        rt.keySerializer = StringRedisSerializer()
        rt.valueSerializer = StringRedisSerializer()
        rt.hashKeySerializer = StringRedisSerializer()
        rt.hashValueSerializer = redisProductSerializer
        rt.afterPropertiesSet()
        return rt
    }
}

@Configuration
class KafkaProducerConfig {

    @Bean(destroyMethod = "close")
    fun kafkaProducer(): KafkaProducer<Bytes, String> =
            KafkaProducer<Bytes, String>(
                    mapOf(
                            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
                            ProducerConfig.CLIENT_ID_CONFIG to "shop-service",
                            ProducerConfig.LINGER_MS_CONFIG to "50"),
                    Serdes.Bytes().serializer(),
                    Serdes.StringSerde().serializer())
}

