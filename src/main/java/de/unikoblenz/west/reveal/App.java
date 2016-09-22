package de.unikoblenz.west.reveal;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;

public class App 
{
    public static void main( String[] args ) {
    	MongoClient mongoClient = new MongoClient("social1.atc.gr");
    	MongoDatabase db = mongoClient.getDatabase("us_elections");
    	
        System.out.println("Post number : " + db.getCollection("Post").count());
        FindIterable<Document> iterable = db.getCollection("Post").find();
        iterable.forEach(new Block<Document>() {
            public void apply(final Document document) {
            	String contributor = document.get("contributor").toString();
            	try {
	            	String contributorId = contributor.split("#| }")[1];
	                System.out.println(contributorId);
            	} catch(ArrayIndexOutOfBoundsException ex) {
            		System.out.println(contributor);
            		System.err.println(ex.getMessage());
            	}
            }
        });

        mongoClient.close();
    }
}
