package com.bdp;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.bson.Document;

public class JavaSparkApp {
    public static void main(String[] args) throws InterruptedException, TimeoutException, StreamingQueryException {
        System.setProperty("hadoop.home.dir", "/");

        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local[*]");

        sparkConf.setAppName("BDPStreaming");
        JavaStreamingContext jsc = new JavaStreamingContext(sparkConf,
                Durations.seconds(1));

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "localhost:9093");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "bdpspark");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", true);

        Collection<String> topics = Arrays.asList("filebeats");

        JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(jsc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));

        JavaDStream<String> data = messages.map(e -> e.value());

        // data.print();

        String uri = "mongodb://bdp:password@localhost:27017";

        MongoClient mongoClient = MongoClients.create(uri);

        MongoDatabase database = mongoClient.getDatabase("mydb");

        MongoCollection<Document> filebeatsCollection = database
                .getCollection("filebeats");

        data.foreachRDD(d -> {
            if (d != null) {
                List<String> result = d.collect();
                for (String temp : result) {
                    final Document doc = Document.parse(temp);
                    doc.append("source", "beats");
                    filebeatsCollection.insertOne(doc);
                }
                System.out.println(result);
                System.out.println("Inserted Data Done");

            } else {
                System.out.println("Got no data in this window");
            }

        });

        jsc.start();
        jsc.awaitTermination();
    }
}
