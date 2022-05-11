/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example;

import beats.FileBeatsData;
import beats.FileBeatsDataDeserializer;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.mapping.Mapper;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.cassandra.CassandraSink;
import org.apache.flink.streaming.connectors.cassandra.ClusterBuilder;


public class DataStreamJob {

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


		KafkaSource<FileBeatsData> source = KafkaSource.<FileBeatsData>builder()
				.setBootstrapServers("localhost:9093")
				.setTopics("filebeasts")
				.setGroupId("flink")
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setValueOnlyDeserializer(new FileBeatsDataDeserializer())
				.build();

		DataStream<FileBeatsData> kafkaData = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Beats Kafka");

		DataStream<FileBeatsData> b = kafkaData.filter(e -> e.service.type.equals("system"));



		CassandraSink<FileBeatsData> sink = CassandraSink.addSink(kafkaData)

				.setHost("localhost" , 9042 )

				.setMapperOptions(() -> new Mapper.Option[]{Mapper.Option.saveNullFields(true)})
				.build();
		
		env.execute();

	}
}
