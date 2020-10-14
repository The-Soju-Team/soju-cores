package com.hh.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import com.hh.constant.Constants;

public class KafkaUtils<K, V> {

    private static final Logger LOG = Logger.getLogger(KafkaUtils.class);
    // Begin configs producer
    protected static String BOOSTRAP_SERVER_DEFAULT;
    protected static String CLIENT_ID;
    public static final String REQUEST_TIMEOUT_MS_CONFIG;
    public static final String ACKS;
    public static final int RETRIES;
    public static final int BATCH_SIZE;
    public static final int LINGER_MS;
    public static final int BUFFER_MEMORY;
    // End configs producer
    // Begin configs consumer
    public static final String HEARTBEAT_INTERVAL_MS_CONFIG;
    public static final String MAX_POLL_INTERVAL_MS_CONFIG;
    public static final String ENABLE_AUTO_COMMIT;
    public static final String AUTO_COMMIT_INTERVAL_MS;
    public static final String SESSION_TIMEOUT_MS;
    // End configs consumer

    protected static Map<Properties, Producer<?, ?>> producerManager;
    protected static Map<Properties, Consumer<?, ?>> consumerManager;

    static {
        String request_timeout_ms_config = "";
        String heartbeat_interval_ms_config = "";
        String max_poll_interval_ms_config = "";
        String acks = "";
        int retries = 0;
        int batch_size = 16384;
        int linger_ms = 1;
        int buffer_memory = 33554432;
        String enable_auto_commit = "true";
        String auto_commit_interval_ms = "1000";
        String session_timeout_ms = "30000";

        try {
            BOOSTRAP_SERVER_DEFAULT = Constants.config.getConfig("boostrap_server");
            CLIENT_ID = Constants.config.getConfig("client_id");
            request_timeout_ms_config = Constants.config.getConfig("request_timeout_ms_config");
            heartbeat_interval_ms_config = Constants.config.getConfig("heartbeat_interval_ms_config");
            max_poll_interval_ms_config = Constants.config.getConfig("max_poll_interval_ms_config");
            acks = Constants.config.getConfig("request_timeout_ms_config");
            retries = 0;
            batch_size = 16384;
            linger_ms = 1;
            buffer_memory = 33554432;
            enable_auto_commit = Constants.config.getConfig("request_timeout_ms_config");
            auto_commit_interval_ms = Constants.config.getConfig("request_timeout_ms_config");
            session_timeout_ms = Constants.config.getConfig("request_timeout_ms_config");
        } catch (Exception e) {
            LOG.info("ERROR WHILE READ CONFIGURATION -> GET DEFAULT CONFIGURATION");
            request_timeout_ms_config = "480000";
            heartbeat_interval_ms_config = "30";
            max_poll_interval_ms_config = "480000";
            acks = "all";
            retries = 0;
            batch_size = 16384;
            linger_ms = 1;
            buffer_memory = 33554432;
            enable_auto_commit = "true";
            auto_commit_interval_ms = "1000";
            session_timeout_ms = "30000";
            e.printStackTrace();
        }

        BOOSTRAP_SERVER_DEFAULT = "localhost:9092";
        CLIENT_ID = "-1";
        REQUEST_TIMEOUT_MS_CONFIG = request_timeout_ms_config;
        HEARTBEAT_INTERVAL_MS_CONFIG = heartbeat_interval_ms_config;
        MAX_POLL_INTERVAL_MS_CONFIG = max_poll_interval_ms_config;

        ACKS = acks;
        RETRIES = retries;
        BATCH_SIZE = batch_size;
        LINGER_MS = linger_ms;
        BUFFER_MEMORY = buffer_memory;

        ENABLE_AUTO_COMMIT = enable_auto_commit;
        AUTO_COMMIT_INTERVAL_MS = auto_commit_interval_ms;
        SESSION_TIMEOUT_MS = session_timeout_ms;

        producerManager = new TreeMap<Properties, Producer<?, ?>>(new Comparator<Properties>() {
            @Override
            public int compare(Properties p1, Properties p2) {
                return KafkaUtils.compare(p1, p2);
            }
        });
        consumerManager = new TreeMap<Properties, Consumer<?, ?>>(new Comparator<Properties>() {
            @Override
            public int compare(Properties p1, Properties p2) {
                return KafkaUtils.compare(p1, p2);
            }
        });
    }

    public Producer<K, V> getOrCreateProducer(Properties configs) {
        // Begin configs kafka producer
        // Properties configs = new Properties();
        // configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOSTRAP_SERVER);
        // configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // configs.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS_CONFIG);
        // End configs kafka producer
        return new ProducerSession.Builder<K, V>(configs).getOrCreate();
    }

    public Consumer<K, V> getOrCreateConsumer(Properties configs) {
        // Begin configs kafka consumer
        // Properties configs = new Properties();
        // configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOSTRAP_SERVER);
        // configs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //
        // configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // configs.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, HEARTBEAT_INTERVAL_MS_CONFIG);
        // configs.put(ConsumerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
        // configs.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, MAX_POLL_INTERVAL_MS_CONFIG);
        // End configs kafka consumer
        return new ConsumerSession.Builder<K, V>(configs).getOrCreate();
    }

    public boolean push(Producer<K, V> producer, String topic, K key, V value) {
        long start = System.nanoTime();
        final ProducerRecord<K, V> record = new ProducerRecord<>(topic, key, value);
        try {
            RecordMetadata metadata = producer.send(record).get();
            LOG.info(String.format(
                    "TOGREP | Sent record(key=%s, value=%s)\nmeta(partition=%d, offset=%d)\nTotal time: %s milisecond(s)",
                    record.key(), record.value(), metadata.partition(), metadata.offset(),
                    (System.nanoTime() - start) / Math.pow(10, 6)));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != producer) {
                producer.flush();
            }
        }
        return true;
    }

    public void runConsumer(Consumer<K, V> consumer, String groupId) {
        consumer.subscribe(Collections.singletonList(groupId));
        while (true) {
            final ConsumerRecords<K, V> consumerRecords = consumer.poll(Duration.ofMillis(1000));
            if (consumerRecords.count() != 0) {
                consumerRecords.forEach(record -> {
                    LOG.info(record);
                });
            }
        }
    }

    public void stop(Consumer<K, V> consumer) {
        LOG.info("==========Begin STOPPING CONSUMER ==========");
        LOG.info("Consumer info: " + consumer
                + " \nWaiting for up to the default timeout of 30 seconds for any needed cleanup. ");
        if (consumer != null) {
            consumer.close();
        }
        LOG.info("==========End STOPPING CONSUMER ==========");
    }

    public void stop(Producer<K, V> producer) {
        LOG.info("==========Begin STOPPING PRODUCER ==========");
        LOG.info("producer info: " + producer);
        if (producer != null) {
            producer.close();
        }
        LOG.info("==========End STOPPING PRODUCER ==========");
    }

    public void stop(Producer<K, V> producer, Consumer<K, V> consumer) {
        LOG.info("==========Begin STOPPING CONSUMER ==========");
        LOG.info("Consumer info: " + consumer
                + " \nWaiting for up to the default timeout of 30 seconds for any needed cleanup. ");
        if (consumer != null) {
            consumer.close();
        }
        LOG.info("==========End STOPPING CONSUMER ==========");
        LOG.info("==========Begin STOPPING PRODUCER ==========");
        LOG.info("producer info: " + producer);
        if (producer != null) {
            producer.close();
        }
        LOG.info("==========End STOPPING PRODUCER ==========");
    }

    private static int compare(Properties p1, Properties p2) {
        if (p1 == null || p2 == null) {
            return -1;
        }
        Set<Object> s1 = p1.keySet();
        Set<Object> s2 = p2.keySet();
        if (s1.size() != s2.size()) {
            return -2;
        }
        if (p1.equals(p2)) {
            return 0;
        }
        return -1;
    }
}
