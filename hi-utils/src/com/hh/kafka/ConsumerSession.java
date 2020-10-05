package com.hh.kafka;

import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

public class ConsumerSession<K, V> {
    private static Logger LOG = Logger.getLogger(ConsumerSession.class.getSimpleName());
    private Properties configs;

    public static class Builder<K, V> {
        private Properties configs;

        public Builder() {
        }

        public Builder(Properties configs) {
            this.configs = configs;
        }

        public Builder<K, V> configs(Properties configs) {
            this.configs = configs;
            return this;
        }

        public Builder<K, V> configs(String key, String value) {
            this.configs.put(key, value);
            return this;
        }

        public Consumer<K, V> getOrCreate() {
            return new ConsumerSession<K, V>().builder(this);
        }
    }

    @SuppressWarnings("unchecked")
    private Consumer<K, V> builder(Builder<?, ?> builder) {
        // If consumer is exist then return it
        // else create a new consumer
        boolean isExist = KafkaUtils.consumerManager.containsKey(builder.configs);
        if (isExist) {
            LOG.info("Consumer configuration is exist -> return consumer session exist");
            return (Consumer<K, V>) KafkaUtils.consumerManager.get(builder.configs);
        }
        LOG.info("Create new consumer with new a configuration");
        this.configs = (Properties) builder.configs.clone();
        Consumer<K, V> consumer = new KafkaConsumer<K, V>(this.configs);
        KafkaUtils.consumerManager.put(this.configs, consumer);

        return consumer;
    }
}
