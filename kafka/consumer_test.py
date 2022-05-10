import pandas as pd
import kafka
import json
from kafka.admin import KafkaAdminClient, NewTopic

consumer = kafka.KafkaConsumer(
    'filebeasts',
    value_deserializer=lambda m: json.loads(m.decode('utf-8')),
    bootstrap_servers=['localhost:9093'])

for message in consumer:
    print(message)
