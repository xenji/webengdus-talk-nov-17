package webengdus.elasticsearch

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import sps.explorer.sink.config.ElasticsearchConfigDef

object ElasticsearchClientFactory {
    fun fromConfig(props: Map<String, String>): RestHighLevelClient {
        val httpHost = HttpHost(
                props[ElasticsearchConfigDef.ELASTICSEARCH_HOST_NAME],
                props[ElasticsearchConfigDef.ELASTICSEARCH_PORT_NAME]!!.toInt())
        return RestHighLevelClient(RestClient.builder(httpHost).build())
    }
}