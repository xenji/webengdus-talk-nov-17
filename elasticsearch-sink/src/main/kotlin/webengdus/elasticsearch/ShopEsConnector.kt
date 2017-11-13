package webengdus.elasticsearch

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.sink.SinkConnector
import sps.explorer.sink.config.ElasticsearchConfigDef
import java.util.Collections

class ShopEsConnector : SinkConnector() {
    private lateinit var config: Map<String, String>

    override fun taskConfigs(maxTasks: Int): List<Map<String, String>> = Collections.nCopies(maxTasks, config)

    override fun start(props: Map<String, String>) {
        this.config = props
    }

    override fun stop() {
    }

    override fun version() = "1.0.0"
    override fun taskClass(): Class<out Task> = ShopEsSinkTask::class.java
    override fun config(): ConfigDef = ElasticsearchConfigDef.configDef
}