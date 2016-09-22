package de.unikoblenz.west.reveal;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class App 
{
    public static void main( String[] args )
    {
    	MongoClient mongoClient = new MongoClient("social1.atc.gr");
    	MongoDatabase db = mongoClient.getDatabase("us_elections");
    	
        System.out.println(db.getCollection("Post").count());
        mongoClient.close();
    }
}
