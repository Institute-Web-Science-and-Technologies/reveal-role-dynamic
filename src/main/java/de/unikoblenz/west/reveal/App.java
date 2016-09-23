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
	
	private static String limitLongStringLength(String s) {
		if (s.length() > 18)
			return s.substring(s.length() - 18);
		
		return s;
	}
	
    public static void main( String[] args ) throws FileNotFoundException {
    	try ( PrintWriter outputFile = new PrintWriter("mongo-data.txt") ) {
	    	MongoClient mongoClient = new MongoClient("social1.atc.gr");
	    	MongoDatabase db = mongoClient.getDatabase("us_elections");
	    	
	        System.out.println("Post number : " + db.getCollection("Post").count());
	        FindIterable<Document> iterable = db.getCollection("Post").find();

	        iterable.forEach(new Block<Document>() {
	            public void apply(final Document document) {
	            	String strId = document.get("_id").toString().split("#")[1].replaceAll("[^0-9]", "");
	            	String title = document.get("title").toString();
	            	String contributor = document.get("contributor").toString();
	            	
	            	String shared = null;
	            	if (document.get("shared") != null)
	            		shared = document.get("shared").toString();
	            	
	            	// parsing data
	            	try {
		            	String strContributorId = contributor.split("#| }")[1].replaceAll("[^0-9]", "");
		            	String strSharedId = "0";
		            	if (shared != null)
		            		strSharedId = shared.split("#| }")[1].replaceAll("[^0-9]", "");
		            	
		            	// cast String to Long
		            	Long id = 0L;
		            	try {
		            		strId = limitLongStringLength(strId);
		            		id = Long.parseLong(strId);
		            	} catch (NumberFormatException ex) {
		            		System.err.println("Invalid ID, " + ex.getMessage());
		            	}
		            	Long contributorId = 0L;
		            	try {
		            		strContributorId = limitLongStringLength(strContributorId);
		            		contributorId = Long.parseLong(strContributorId);
		            	} catch (NumberFormatException ex) {
		            		System.err.println("Invalid ID, " + ex.getMessage());
		            	}
		            	Long sharedId = 0L;
		            	try {
		            		strSharedId = limitLongStringLength(strSharedId);
		            		sharedId = Long.parseLong(strSharedId);
		            	} catch (NumberFormatException ex) {
		            		System.err.println("Invalid ID, " + ex.getMessage());
		            	}
		            	
		            	JSONObject json = new JSONObject();
		            	JSONObject user = new JSONObject();
		            	
		            	user.put("id", contributorId);
		            	user.put("name", strContributorId);
		            	
		            	json.put("id", id);
		            	json.put("user", user);
		            	json.put("text", title);
		            	if (shared != null)
		            		json.put("in_reply_to_user_id", sharedId);
		    
		                outputFile.println(json.toString());
		     
	            	} catch (ArrayIndexOutOfBoundsException ex) {
	            		System.err.println("Array index out of bounds, " + ex.getMessage());
	            	}
	            }
	        });
	
	        mongoClient.close();
    	}
    }
}
