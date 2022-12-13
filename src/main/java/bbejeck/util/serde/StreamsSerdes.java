package bbejeck.util.serde;

import bbejeck.collectors.FixedSizePriorityQueue;
import bbejeck.model.ClickEvent;
import bbejeck.model.CustomerTransactions;
import bbejeck.model.Purchase;
import bbejeck.model.PurchaseKey;
import bbejeck.model.PurchasePattern;
import bbejeck.model.RewardAccumulator;
import bbejeck.model.ShareVolume;
import bbejeck.model.StockPerformance;
import bbejeck.model.StockTickerData;
import bbejeck.model.StockTransaction;
import bbejeck.model.TransactionSummary;
import bbejeck.util.collection.Tuple;
import bbejeck.util.serializer.JsonDeserializer;
import bbejeck.util.serializer.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serdes.WrapperSerde;

import java.lang.reflect.Type;
import java.util.List;


public class StreamsSerdes {

    public static Serde<PurchasePattern> PurchasePatternSerde() {
             return new PurchasePatternsSerde();
    }

    public static Serde<RewardAccumulator> RewardAccumulatorSerde() {
        return new RewardAccumulatorSerde();
    }

    public static Serde<Purchase> PurchaseSerde() {
        return new PurchaseSerde();
    }

    public static Serde<StockTickerData> StockTickerSerde() {
        return  new StockTickerSerde();
    }

    public static Serde<StockTransaction> StockTransactionSerde() {
        return new StockTransactionSerde();
    }

    public static Serde<FixedSizePriorityQueue> FixedSizePriorityQueueSerde() {
        return new FixedSizePriorityQueueSerde();
    }

    public static Serde<TransactionSummary> TransactionSummarySerde() {
        return new TransactionSummarySerde();
    }

    public static Serde<ShareVolume> ShareVolumeSerde() {
        return new ShareVolumeSerde();
    }

    public static Serde<StockPerformance> StockPerformanceSerde() {
        return new StockPerformanceSerde();
    }

    public static Serde<CustomerTransactions> CustomerTransactionsSerde() {
        return new CustomerTransactionsSerde();
    }

    public static Serde<Tuple<List<ClickEvent>, List<StockTransaction>>> EventTransactionTupleSerde() {
        return new EventTransactionTupleSerde();
    }

    public static Serde<ClickEvent> ClickEventSerde() {
        return new ClickEventSerde();
    }

    public static Serde<List<ClickEvent>> EventListSerde() {
        return new EventsListSerde();
    }

    public static Serde<List<StockTransaction>> TransactionsListSerde() {
        return  new TransactionsListSerde();
    }


    public static Serde<PurchaseKey> purchaseKeySerde() {
        return new PurchaseKeySerde();
    }

    public static final class PurchaseKeySerde extends WrapperSerde<PurchaseKey> {
        public PurchaseKeySerde(){
            super(new JsonSerializer<>(), new JsonDeserializer<>(PurchaseKey.class) );
        }
    }

    public static final class PurchasePatternsSerde extends WrapperSerde<PurchasePattern> {
        public PurchasePatternsSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(PurchasePattern.class));
        }
    }

    public static final class RewardAccumulatorSerde extends WrapperSerde<RewardAccumulator> {
        public RewardAccumulatorSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(RewardAccumulator.class));
        }
    }

    public static final class PurchaseSerde extends WrapperSerde<Purchase> {
        public PurchaseSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(Purchase.class));
        }
    }

    public static final class StockTickerSerde extends WrapperSerde<StockTickerData> {
        public StockTickerSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(StockTickerData.class));
        }
    }

    public static final class StockTransactionSerde extends WrapperSerde<StockTransaction> {
        public StockTransactionSerde(){
            super(new JsonSerializer<>(), new JsonDeserializer<>(StockTransaction.class));
        }
    }

    public static final class CustomerTransactionsSerde extends WrapperSerde<CustomerTransactions> {
         public CustomerTransactionsSerde() {
             super(new JsonSerializer<>(), new JsonDeserializer<>(CustomerTransactions.class));
         }

    }

    public static final class FixedSizePriorityQueueSerde extends WrapperSerde<FixedSizePriorityQueue> {
        public FixedSizePriorityQueueSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(FixedSizePriorityQueue.class));
        }
    }

    public static final class ShareVolumeSerde extends WrapperSerde<ShareVolume> {
        public ShareVolumeSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(ShareVolume.class));
        }
    }

    public static final class TransactionSummarySerde extends WrapperSerde<TransactionSummary> {
         public TransactionSummarySerde() {
             super(new JsonSerializer<>(), new JsonDeserializer<>(TransactionSummary.class));
         }
    }

    public static final class StockPerformanceSerde extends Serdes.WrapperSerde<StockPerformance> {
        public StockPerformanceSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(StockPerformance.class));
        }
    }

    public static final class EventTransactionTupleSerde extends WrapperSerde<Tuple<List<ClickEvent>, List<StockTransaction>>> {
            private static final Type tupleType = new TypeToken<Tuple<List<ClickEvent>, List<StockTransaction>>>(){}.getType();
        public EventTransactionTupleSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(tupleType));
        }
    }

    public static final class ClickEventSerde extends Serdes.WrapperSerde<ClickEvent> {
        public ClickEventSerde () {
            super(new JsonSerializer<>(), new JsonDeserializer<>(ClickEvent.class));
        }
    }

    public static final class TransactionsListSerde extends Serdes.WrapperSerde<List<StockTransaction>> {
        private static final Type listType = new TypeToken<List<StockTransaction>>(){}.getType();
        public TransactionsListSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(listType));
        }
    }

    public static final class EventsListSerde extends Serdes.WrapperSerde<List<ClickEvent>> {
        private static final Type listType = new TypeToken<List<ClickEvent>>(){}.getType();
        public EventsListSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(listType));
        }
    }
}
