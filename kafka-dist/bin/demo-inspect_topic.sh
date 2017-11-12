#!/usr/bin/env bash
bin/kafka-console-consumer.sh \
--bootstrap-server localhost:9092 \
--property print.key=true \
--property key.separator="\t" \
--topic localdb.shop.product --from-beginning
