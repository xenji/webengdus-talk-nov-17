#!/usr/bin/env bash
MY_IP=$(ifconfig en0 | grep inet | grep -v inet6 | awk '{print $2}')
docker run --rm -it -p 8000:8000 -e "CONNECT_URL=http://${MY_IP}:8083" landoop/kafka-connect-ui