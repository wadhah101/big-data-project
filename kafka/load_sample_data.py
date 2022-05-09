import pandas as pd
import kafka
import json

producer = kafka.KafkaProducer(
    value_serializer=lambda v: json.dumps(v).encode('utf-8'),
    bootstrap_servers='localhost:9092')


def load_json():
    with open("samples/filebeats/temp.json", "r") as f:
        data: list = json.load(f)

    for i in data:
        producer.send('sample', i)

        # codec => json_lines {
        #   target => "[document]"
        #   delimiter => ","
        # }


load_json()
