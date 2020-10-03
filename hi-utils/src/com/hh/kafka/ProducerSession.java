package com.hh.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.log4j.Logger;

public class ProducerSession<K, V> {
    private static Logger LOG = Logger.getLogger(ProducerSession.class.getSimpleName());
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

        public Producer<K, V> build() {
            return new ProducerSession<K, V>().builder(this);
        }
    }

    private Producer<K, V> builder(Builder<?, ?> builder) {
        // Neu da ton tai thi tra ve luon
        boolean isExist = KafkaUtils.kafkaManager.get("producer").containsKey(builder.configs);
        if (isExist) {
            LOG.info("Da ton tai config nhe - tra ve producer san co");
            return (Producer<K, V>) KafkaUtils.kafkaManager.get("producer").get(builder);
        }
        // else
        this.configs = builder.configs;
        Producer<K, V> producer = new KafkaProducer<K, V>(this.configs);
        return producer;
    }
}
