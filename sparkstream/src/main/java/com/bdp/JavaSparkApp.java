package com.bdp;

import java.util.concurrent.TimeoutException;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

public class JavaSparkApp {
    public static void main(String[] args) throws InterruptedException, TimeoutException, StreamingQueryException {
        System.setProperty("hadoop.home.dir", "/");

        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local[*]");

        sparkConf.setAppName("BDPStreaming");
        JavaStreamingContext jsc = new JavaStreamingContext(sparkConf,
                Durations.seconds(1));

        ProcessFileBeats.process(jsc);
        ProcessMetricBeats.process(jsc);

        jsc.start();
        jsc.awaitTermination();
    }
}
