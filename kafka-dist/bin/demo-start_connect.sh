#!/usr/bin/env bash

KAFKA_HEAP_OPTS="-Xmx512m -Xms512m -XX:+UseCompressedOops -Djava.net.preferIPv4Stack=true"

rm ../connect.offsets.shop
rm connect.offsets.shop

bin/connect-standalone.sh \
config/connect-standalone-shop.properties \
connectors/debezium_localdb.shop.properties \
connectors/redis_cache.properties \
connectors/elasticsearch.properties
