import pandas as pd
import kafka
import json
from kafka.admin import KafkaAdminClient, NewTopic
import time

producer = kafka.KafkaProducer(
    bootstrap_servers=['localhost:9093'],
    value_serializer=lambda x: json.dumps(x).encode('utf-8'))

def load_json_batch():
    with open("samples/filebeats/temp.json", "r") as f:
        data: list = json.load(f)

    for i in data:
        producer.send('sample', i)


def load_json_continue():
    with open("logstash/output/filebeat.json", "r") as f:
        data: list = json.load(f)

    while True :
      for i in data:
        producer.send('filebeasts', i)
        time.sleep(1)


load_json_continue()

