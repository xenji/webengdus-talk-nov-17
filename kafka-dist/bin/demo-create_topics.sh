#!/usr/bin/env bash
bin/kafka-topics.sh --zookeeper=localhost:2181 --create --topic localdb.shop.product --config cleanup.policy=compact --partitions=1 --replication-factor=1
bin/kafka-topics.sh --zookeeper=localhost:2181 --create --topic debezium__localdb_shop --config cleanup.policy=delete --config retention.ms=-1 --config retention.bytes=-1 --partitions=1 --replication-factor=1
bin/kafka-topics.sh --zookeeper=localhost:2181 --create --topic shop.purchases --partitions=1 --replication-factor=1