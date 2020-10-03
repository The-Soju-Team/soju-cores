package com.hh.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
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
import org.apache.poi.ss.formula.functions.T;

public class KafkaUtils<K, V> {

    private static final Logger LOG = Logger.getLogger(KafkaUtils.class);
    // Begin configs producer
    protected static String BOOSTRAP_SERVER;
    protected static String CLIENT_ID;
    protected static String REQUEST_TIMEOUT_MS_CONFIG;
    // End configs producer
    // Begin configs consumer
    protected static String HEARTBEAT_INTERVAL_MS_CONFIG;
    protected static String MAX_POLL_INTERVAL_MS_CONFIG;
    // End configs consumer
    protected static Map<String,
    TreeMap<Properties, Set<T>>> kafkaManager;

    static {
        BOOSTRAP_SERVER = "localhost:9092";
        HEARTBEAT_INTERVAL_MS_CONFIG = "";
        MAX_POLL_INTERVAL_MS_CONFIG = "";
        kafkaManager = new HashMap<String, TreeMap<Properties, Set<T>>>();
        kafkaManager.put("producer", new TreeMap<Properties, Set<T>>());
        kafkaManager.put("connsumer", new TreeMap<Properties, Set<T>>());
    }


    public Producer<K, V> createProducer(Properties configs) throws Exception {
        // Begin configs kafka producer
        // Properties configs = new Properties();
        // configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOSTRAP_SERVER);
        // configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // configs.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS_CONFIG);
        // End configs kafka producer
        // create new producer with configurations
        return new ProducerSession.Builder<K, V>(configs).build();
        // else
    }

    public Consumer<K, V> createConsumer(Properties configs) {
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
        return new ConsumerSession.Builder<K, V>(configs).build();
        // else
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
}
