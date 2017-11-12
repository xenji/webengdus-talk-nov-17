package webengdus.redis

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.sink.SinkConnector
import java.util.Collections

class ShopRedisConnector : SinkConnector() {

    lateinit var config: Map<String, String>

    override fun taskConfigs(maxTasks: Int): List<Map<String, String>> =
            Collections.nCopies(maxTasks, config)

    override fun start(props: MutableMap<String, String>) {
        config = props
    }

    override fun stop() {
    }

    override fun version(): String = "1.0.0"

    override fun taskClass(): Class<out Task> = ShopRedisSinkTask::class.java

    override fun config(): ConfigDef = ConfigDef()
            .define("redis.url", ConfigDef.Type.STRING, "redis://localhost", ConfigDef.Importance.HIGH, "The redis url")
}