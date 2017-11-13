package webengdus.elasticsearch

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.sink.SinkRecord
import org.apache.kafka.connect.sink.SinkTask
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import sps.explorer.sink.config.ElasticsearchConfigDef

class ShopEsSinkTask : SinkTask() {

    private lateinit var config: Map<String, String>
    private lateinit var index: String
    private lateinit var type: String
    private lateinit var client: RestHighLevelClient
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    override fun version() = "1.0.0"

    override fun start(props: Map<String, String>) {
        this.config = props
        this.index = props[ElasticsearchConfigDef.ELASTICSEARCH_INDEX_NAME]!!
        this.type = props[ElasticsearchConfigDef.ELASTICSEARCH_TYPE_NAME]!!
        this.client = ElasticsearchClientFactory.fromConfig(props)
    }

    override fun stop() {
    }

    override fun put(records: Collection<SinkRecord>) {
        val bulkRequest = BulkRequest()
        records.forEach({
            val k = it.key() as Struct
            val v = it.value() as Struct? ?: return@forEach // We handle the "op": "d" and let the tombstone pass un-noticed.

            val sku = k.getInt64("sku")

            when (v.getString("op")) {
            // We've deleted the product from the inventory
                "d" -> {
                    val delRequest = DeleteRequest(index, type, sku.toString())
                    bulkRequest.add(delRequest)
                }
                else -> {
                    val after = v.getStruct("after")
                    when (after.getInt32("stock")) {
                    // We are out of stock, delete it form the index
                        0 -> {
                            val delRequest = DeleteRequest(index, type, sku.toString())
                            bulkRequest.add(delRequest)
                        }
                    // We have stock greater than zero, lets do this!
                        else -> {
                            val idxRequest = IndexRequest(index, type, sku.toString())
                            val doc = IndexableDocument(
                                    name = after.getString("product_name"),
                                    description = after.getString("product_description"),
                                    price = after.getFloat64("price"),
                                    stock = after.getInt32("stock"))
                            idxRequest.source(objectMapper.writeValueAsString(doc), XContentType.JSON)
                            bulkRequest.add(idxRequest)
                        }
                    }
                }
            }
        })
        if (bulkRequest.numberOfActions() > 0) {
            client.bulk(bulkRequest)
        }
    }
}