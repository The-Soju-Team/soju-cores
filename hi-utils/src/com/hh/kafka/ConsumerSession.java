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

        public Consumer<K, V> build() {
            return new ConsumerSession<K, V>().builder(this);
        }
    }

    private Consumer<K, V> builder(Builder<?, ?> builder) {
        // Neu da ton tai thi tra ve luon
        boolean isExist = KafkaUtils.kafkaManager.get("producer").containsKey(builder.configs);
        if (isExist) {
            LOG.info("Da ton tai config nhe - tra ve producer san co");
            return (Consumer<K, V>) KafkaUtils.kafkaManager.get("consumer").get(builder);
        }
        // else
        this.configs = builder.configs;
        Consumer<K, V> consumer = new KafkaConsumer<K, V>(this.configs);
        return consumer;
    }
}
