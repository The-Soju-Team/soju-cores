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

        public Builder() {
            // do nothing
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

        public Producer<K, V> getOrCreate() {
            return new ProducerSession<K, V>().builder(this);
        }
    }

    @SuppressWarnings("unchecked")
    private Producer<K, V> builder(Builder<?, ?> builder) {
        // If producer is exist then return it
        // else create a new producer
        boolean isExist = KafkaUtils.producerManager.containsKey(builder.configs);
        if (isExist) {
            LOG.info("Producer configuration is exist -> return producer session exist");
            return (Producer<K, V>) KafkaUtils.producerManager.get(builder.configs);
        }
        LOG.info("Create new producer with new a configuration");
        this.configs = (Properties) builder.configs.clone();
        Producer<K, V> producer = new KafkaProducer<K, V>(this.configs);
        KafkaUtils.producerManager.put(this.configs, producer);

        return producer;
    }
}
