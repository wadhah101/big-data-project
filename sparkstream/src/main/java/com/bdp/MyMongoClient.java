package com.bdp;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MyMongoClient {
  static MongoDatabase getMongoDatabase() {
    String uri = "mongodb://bdp:password@localhost:27017";

    MongoClient mongoClient = MongoClients.create(uri);

    MongoDatabase database = mongoClient.getDatabase("beats");

    return database;
  }
}
