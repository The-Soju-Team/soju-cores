/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.kafka;

import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Level;

import java.util.*;

/**
 * @author HienDM
 */
public class KafkaConnector {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(KafkaConnector.class);
    private KafkaProducer producer;
    private HashMap<String, KafkaConsumer> consumerMin = new HashMap();
    private HashMap<String, KafkaConsumer> consumerMax = new HashMap();
    private Properties consumerProps = new Properties();

    public KafkaConnector(String brokerConnect, String userName, String password) {
        org.apache.log4j.Logger.getLogger("kafka").setLevel(Level.ERROR);

        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", brokerConnect);
        kafkaProps.put("enable.auto.commit", "true");
        kafkaProps.put("auto.commit.interval.ms", "1000");
        kafkaProps.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + userName + "\" password=\"" + password + "\";");
        kafkaProps.put("security.protocol", "SASL_PLAINTEXT");
        kafkaProps.put("sasl.mechanism", "PLAIN");

        // for producer
        Properties pp = (Properties) kafkaProps.clone();
        pp.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class.getName());
        pp.put("value.serializer", org.apache.kafka.common.serialization.StringSerializer.class.getName());
        producer = new KafkaProducer<>(pp);

        // for consumer
        Properties cp = (Properties) kafkaProps.clone();
        cp.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        cp.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        cp.put("auto.offset.reset", "earliest");

        consumerProps = (Properties) cp.clone();
        consumerProps.put("max.poll.records", "1");

    }

    public void putList(String topic, List lstData) {
        StringBuilder content = new StringBuilder();
        content.append("\n");
        content.append("------------------- PUT TO KAFKA ------------------- \n");
        for (int i = 0; i < lstData.size(); i++) {
            Gson gson = new Gson();
            String json = gson.toJson(lstData.get(i));
            content.append("        Put");
            content.append(i + 1);
            content.append(" : ");
            content.append(json);
            content.append("\n");
            producer.send(new ProducerRecord(topic, json));
        }
        content.append("---------------------------------------------------- \n");
        logger.info(content);
    }

    public void put(String topic, HashMap message) {
        StringBuilder content = new StringBuilder();
        content.append("\n");
        content.append("------------------- PUT TO KAFKA ------------------- \n");
        Gson gson = new Gson();
        String json = gson.toJson(message);
        content.append("        Put message : ");
        content.append(json);
        content.append("\n");
        producer.send(new ProducerRecord(topic, json));
        content.append("---------------------------------------------------- \n");
        logger.info(content);
    }

    public List pop(String topic, int size) {
        List lstData = new ArrayList();
        if (consumerMin.get(topic) == null) {
            consumerProps.put("group.id", topic + "_group");
            KafkaConsumer kc = new KafkaConsumer(consumerProps);
            kc.subscribe(Arrays.asList(topic));
            consumerMin.put(topic, kc);
        }
        for (int i = 0; i < size; i++) {
            ConsumerRecords records;
            synchronized (consumerMin.get(topic)) {
                records = consumerMin.get(topic).poll(100);
            }
            for (ConsumerRecord record : (Iterable<ConsumerRecord>) records) {
                lstData.add(record);
                break;
            }
        }
        if (lstData.size() == (size - 1)) {
            ConsumerRecords records;
            synchronized (consumerMin.get(topic)) {
                records = consumerMin.get(topic).poll(100);
            }
            for (ConsumerRecord record : (Iterable<ConsumerRecord>) records) {
                lstData.add(record);
                break;
            }
        }
        return lstData;
    }

    public void stop() {
        producer.close();
        for (Map.Entry<String, KafkaConsumer> entry : consumerMin.entrySet()) {
            entry.getValue().close();
        }
    }
}
