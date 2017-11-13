#!/usr/bin/env bash

bin/kafka-streams-application-reset.sh --application-id redis
bin/kafka-streams-application-reset.sh --application-id elasticsearch
bin/kafka-streams-application-reset.sh --application-id agg-sp-01