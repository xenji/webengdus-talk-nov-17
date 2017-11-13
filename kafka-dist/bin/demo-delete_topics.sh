#!/usr/bin/env bash
bin/kafka-topics.sh --zookeeper=localhost:2181 --delete --topic localdb.shop.product
bin/kafka-topics.sh --zookeeper=localhost:2181 --delete --topic debezium__localdb_shop
bin/kafka-topics.sh --zookeeper=localhost:2181 --delete --topic shop.purchases