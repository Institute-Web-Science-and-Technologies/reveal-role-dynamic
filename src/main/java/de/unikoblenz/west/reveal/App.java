package de.unikoblenz.west.reveal;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class App {
    public static void main( String[] args ) throws FileNotFoundException {
    	try ( PrintWriter outputFile = new PrintWriter("mongo-data.txt") ) {
	    	MongoClient mongoClient = new MongoClient("social1.atc.gr");
	    	MongoDatabase db = mongoClient.getDatabase("us_elections");
	    	
	        System.out.println("Post number : " + db.getCollection("Post").count());
	        FindIterable<Document> iterable = db.getCollection("Post").find();

	        iterable.forEach(new Block<Document>() {
	            public void apply(final Document document) {
	            	String id = document.get("_id").toString().split("#")[1];
	            	String title = document.get("title").toString();
	            	String contributor = document.get("contributor").toString();
	            	String shared = null;
	            	if (document.get("shared") != null)
	            		shared = document.get("shared").toString();
	            	
	            	// parsing data
	            	try {
		            	String contributorId = contributor.split("id\" : \"| }")[1];
		            	String sharedId = null;
		            	if (shared != null)
		            		sharedId = shared.split("id\" : \"| }")[1];
		            	
		            	JSONObject json = new JSONObject();
		            	JSONObject user = new JSONObject();
		            	
		            	user.put("id", contributorId);

		            	json.put("id", id);
		            	json.put("user", user);
		            	json.put("text", title);
		            	if (sharedId != null)
		            		json.put("in_reply_to_user_id", sharedId);
		    
		                System.out.println(json.toString());
		     
	            	} catch(ArrayIndexOutOfBoundsException ex) {
	            		System.out.println(contributor);
	            		System.err.println(ex.getMessage());
	            	}
	            }
	        });
	
	        mongoClient.close();
    	}
    }
}
