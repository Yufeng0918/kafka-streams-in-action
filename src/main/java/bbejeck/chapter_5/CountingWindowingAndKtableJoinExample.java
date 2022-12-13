package bbejeck.chapter_5;


import bbejeck.chapter_5.timestamp_extractor.StockTransactionTimestampExtractor;
import bbejeck.clients.producer.MockDataProducer;
import bbejeck.model.CustomerTransactions;
import bbejeck.model.StockTransaction;
import bbejeck.model.TransactionSummary;
import bbejeck.util.datagen.CustomDateGenerator;
import bbejeck.util.datagen.DataGenerator;
import bbejeck.util.serde.StreamsSerdes;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.state.SessionStore;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.internals.SessionStoreBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;

import static bbejeck.clients.producer.MockDataProducer.STOCK_TRANSACTIONS_TOPIC;
import static org.apache.kafka.streams.Topology.AutoOffsetReset.EARLIEST;
import static org.apache.kafka.streams.Topology.AutoOffsetReset.LATEST;


public class CountingWindowingAndKtableJoinExample {

    private static final Logger LOG = LoggerFactory.getLogger(CountingWindowingAndKtableJoinExample.class);

    public static void main(String[] args) throws Exception {


        StreamsConfig streamsConfig = new StreamsConfig(getProperties());

        Serde<String> stringSerde = Serdes.String();
        Serde<StockTransaction> transactionSerde = StreamsSerdes.StockTransactionSerde();
        Serde<TransactionSummary> transactionKeySerde = StreamsSerdes.TransactionSummarySerde();

        StreamsBuilder builder = new StreamsBuilder();
        KTable<Windowed<TransactionSummary>, Long> customerTransactionCounts =
                 builder.stream(STOCK_TRANSACTIONS_TOPIC, Consumed.with(stringSerde, transactionSerde).withOffsetResetPolicy(LATEST))
                .groupBy((noKey, transaction) -> TransactionSummary.from(transaction),
                        Grouped.with(transactionKeySerde, transactionSerde))
//                 // session window comment line below and uncomment another line below for a different window example
//                .windowedBy(SessionWindows.with(Duration.ofSeconds(20L)).grace(Duration.ofSeconds(20L)))
//                         .count(Materialized.<TransactionSummary, Long, SessionStore<Bytes, byte[]>>as("count")
//                                 .withKeySerde(transactionKeySerde).withValueSerde(Serdes.Long()).withRetention(Duration.ofSeconds(15L * 60)));

                //The following are examples of different windows examples

                //Tumbling window with timeout 15 minutes
//                .windowedBy(TimeWindows.of(Duration.ofSeconds(20L)).grace(Duration.ofSeconds(5L)))
//                         .count(Materialized.<TransactionSummary, Long, WindowStore<Bytes, byte[]>>as("count")
//                                 .withKeySerde(transactionKeySerde).withValueSerde(Serdes.Long()).withRetention(Duration.ofSeconds(15L * 60)));

                //Tumbling window with default timeout 24 hours
                //.windowedBy(TimeWindows.of(twentySeconds)).count();

                //Hopping window 
                .windowedBy(TimeWindows.of(Duration.ofSeconds(20L)).grace(Duration.ofSeconds(5L)).advanceBy(Duration.ofSeconds(5L)))
                         .count(Materialized.<TransactionSummary, Long, WindowStore<Bytes, byte[]>>as("count")
                                 .withKeySerde(transactionKeySerde).withValueSerde(Serdes.Long()).withRetention(Duration.ofSeconds(15L * 60)));

        customerTransactionCounts.toStream().print(Printed.<Windowed<TransactionSummary>, Long>toSysOut().withLabel("Customer Transactions Counts"));

        KStream<String, TransactionSummary> countStream = customerTransactionCounts.toStream().map((window, count) -> {
                      TransactionSummary transactionSummary = window.key();
                      String newKey = transactionSummary.getIndustry();
                      transactionSummary.setSummaryCount(count);
                      return KeyValue.pair(newKey, transactionSummary);
        });

        KTable<String, String> financialNews = builder.table( "financial-news", Consumed.with(EARLIEST));


        ValueJoiner<TransactionSummary, String, String> valueJoiner = (txnct, news) ->
                String.format("%d shares purchased %s related news [%s]", txnct.getSummaryCount(), txnct.getStockTicker(), news);

        KStream<String,String> joined = countStream.leftJoin(financialNews, valueJoiner, Joined.with(stringSerde, transactionKeySerde, stringSerde));

        joined.print(Printed.<String, String>toSysOut().withLabel("Transactions and News"));



        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamsConfig);
        kafkaStreams.cleanUp();
        
        kafkaStreams.setUncaughtExceptionHandler((t, e) -> {
            LOG.error("had exception ", e);
        });
        CustomDateGenerator dateGenerator = CustomDateGenerator.withTimestampsIncreasingBy(Duration.ofMillis(750));
        
        DataGenerator.setTimestampGenerator(dateGenerator::get);
        
        MockDataProducer.produceStockTransactions(2, 5, 3, false);

        LOG.info("Starting CountingWindowing and KTableJoins Example");
        kafkaStreams.cleanUp();
        kafkaStreams.start();
        Thread.sleep(65000);
        LOG.info("Shutting down the CountingWindowing and KTableJoins Example Application now");
        kafkaStreams.close();
        MockDataProducer.shutdown();
    }


    private static Properties getProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Windowing-counting-ktable-joins");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "Windowing-counting-ktable-joins");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "Windowing-counting-ktable-joins");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "30000");
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "10000");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "1");
        props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, "10000");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, StockTransactionTimestampExtractor.class);
        return props;

    }

}
