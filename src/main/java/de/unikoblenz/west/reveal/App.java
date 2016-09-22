package de.unikoblenz.west.reveal;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;

public class App 
{
    public static void main( String[] args )
    {
    	MongoClient mongoClient = new MongoClient("social1.atc.gr");
    	MongoDatabase db = mongoClient.getDatabase("us_elections");
    	
        System.out.println("Post number : " + db.getCollection("Post").count());
        mongoClient.close();
    }
}
