package com.bdp;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import org.bson.Document;

public class ProcessMetricBeats {
  static void process(JavaStreamingContext jsc) {
    Map<String, Object> kafkaParams = KafkaParams.getKafkaParams();
    Collection<String> topics = Arrays.asList("metricbeat");

    JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(jsc,
        LocationStrategies.PreferConsistent(),
        ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));

    JavaDStream<String> data = messages.map(e -> e.value());

    MongoDatabase database = MyMongoClient.getMongoDatabase();

    MongoCollection<Document> filebeatsCollection = database
        .getCollection("metricbeat");

    data.foreachRDD(d -> {
      if (d != null) {
        List<String> result = d.collect();
        for (String temp : result) {
          final Document doc = Document.parse(temp);
          doc.append("source", "beats");
          filebeatsCollection.insertOne(doc);
        }
        if (result.size() != 0) {
          System.out.println("metricbeat : Inserted Data Done");
        } else {
          System.out.println("metricbeat : Got no data in this window");
        }

      } else {
        System.out.println("metricbeat : Got no data in this window");
      }

    });
  }
}
