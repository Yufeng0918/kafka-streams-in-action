#!/usr/bin/env bash


#KAFKA_HOME=$1
ZK_HOST=127.0.0.1
ZK_PORT=9092

topics="src-topic patterns rewards purchases stocks stocks-out transaction-summary coffee electronics transactions customer_transactions events cogrouped-results stock-performance stock-transactions stock-ticker-table stock-ticker-stream stock-volume-by-company companies clients financial-news pops-hops-purchases international-sales domestic-sales stock-counts transaction-count session-transactions sector-transaction-counts"

for topic in ${topics}; do
     echo "attempting to create topic ${topic}"
     kafka-topics.sh --create --topic ${topic} --partitions 1 --replication-factor 1 --bootstrap-server ${ZK_HOST}:${ZK_PORT}
done
