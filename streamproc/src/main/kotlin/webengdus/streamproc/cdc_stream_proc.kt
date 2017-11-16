package webengdus.streamproc

import com.fasterxml.jackson.databind.JsonNode
import com.lambdaworks.redis.RedisClient
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.connect.json.JsonDeserializer
import org.apache.kafka.connect.json.JsonSerializer
import org.apache.kafka.streams.Consumed
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Serialized
import org.apache.kafka.streams.kstream.SessionWindows
import java.util.Properties

const val APPLICATION_ID = "agg-sp-01"
const val BOOTSTRAP_SERVERS = "localhost:9092"
const val STATE_DIR = "./streamproc/"
const val AUTO_OFFSET_RESET = "earliest"
const val SOURCE_TOPIC_NAME = "localdb.shop.product"
const val REDIS_URL = "redis://localhost/0"

val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.info { "Initialize stream processor" }
    val redisClient = RedisClient.create(REDIS_URL)
    val redisConn = redisClient.connect().async()

    val jsonSerializer = JsonSerializer()
    val jsonDeserializer = JsonDeserializer()
    val jsonSerde = Serdes.serdeFrom<JsonNode>(jsonSerializer, jsonDeserializer)

    /* basic configuration */
    val props = Properties().apply {
        put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID)
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS)
        put(StreamsConfig.STATE_DIR_CONFIG, STATE_DIR)
        put(StreamsConfig.consumerPrefix(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG), AUTO_OFFSET_RESET)
        put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "200")
        put(StreamsConfig.POLL_MS_CONFIG, "10")
        put(StreamsConfig.producerPrefix(ProducerConfig.LINGER_MS_CONFIG), "5")
    }
    val streamsConfig = StreamsConfig(props)
    val streamBuilder = StreamsBuilder()

    // This is very, very ignorant. We would need to inspect if the event is really a sale
    // and if so, then process it. Also we would want to make this stateful over some materialized mechanism.
    streamBuilder.stream(SOURCE_TOPIC_NAME, Consumed.with(jsonSerde, jsonSerde))
            .filter({ _, v ->
                if (v == null) {
                    return@filter false
                }
                val payload = v["payload"]
                val op = payload["op"].asText()

                // Sells are updates to existing records. We only care for those.
                if (op != "u") {
                    return@filter false
                }

                val before = payload["before"]
                val after = payload["after"]

                return@filter after["stock"].asLong() < before["stock"].asLong()
            })
            .groupByKey(Serialized.with(jsonSerde, jsonSerde))
            .windowedBy(SessionWindows.with(60000))
            .count()
            .toStream({ k, _ -> k.key() })
            .foreach { key, value ->
                if (value == null) {
                    return@foreach
                }
                val k = "sold-${key["payload"]["sku"].longValue()}"
                redisConn.set(k, value.toString())
                redisConn.expire(k, 60L)
            }

    val topology = streamBuilder.build()
    val kafkaStreams = KafkaStreams(topology, streamsConfig)
    logger.info { "Starting stream application" }
    Runtime.getRuntime().addShutdownHook(Thread(Runnable {
        kafkaStreams.close()
        kafkaStreams.cleanUp()
    }))
    kafkaStreams.start()
}