package sps.explorer.sink.config

import org.apache.kafka.common.config.AbstractConfig
import org.apache.kafka.common.config.ConfigDef

class ElasticsearchConfigDef private constructor(config: Map<*, *>, configDef: ConfigDef) : AbstractConfig(configDef, config) {

    companion object {
        const val ELASTICSEARCH_HOST_NAME: String = "elasticsearch.host"
        const val ELASTICSEARCH_PORT_NAME: String = "elasticsearch.port"
        const val ELASTICSEARCH_INDEX_NAME: String = "elasticsearch.index"
        const val ELASTICSEARCH_TYPE_NAME: String = "elasticsearch.type"

        val configDef = ConfigDef()
                .define(ELASTICSEARCH_HOST_NAME, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE, ConfigDef.Importance.HIGH, "")
                .define(ELASTICSEARCH_PORT_NAME, ConfigDef.Type.INT, ConfigDef.NO_DEFAULT_VALUE, ConfigDef.Importance.HIGH, "")
                .define(ELASTICSEARCH_INDEX_NAME, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE, ConfigDef.Importance.HIGH, "")
                .define(ELASTICSEARCH_TYPE_NAME, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE, ConfigDef.Importance.HIGH, "")

        fun create(config: Map<*, *>): ElasticsearchConfigDef = ElasticsearchConfigDef(config, configDef)
    }
}