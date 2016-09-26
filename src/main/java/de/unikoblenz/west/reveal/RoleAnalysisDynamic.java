package de.unikoblenz.west.reveal;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;

import java.io.IOException;
import java.io.PrintWriter;

public class RoleAnalysisDynamic {
	
	private static String convertToValidDigits(String s) {
		s = s.replaceAll("[^0-9]", "");
		if (s.length() > 18)
			return s.substring(s.length() - 18);
		
		return s;
	}
	
    public static void main( String[] args ) throws IOException {
    	
    	String database = "us_elections";
    	if (args.length > 0)
    		database = args[0];

		final PrintWriter outputFile = new PrintWriter("mongo-data-" + database);
    	try {
	    	MongoClient mongoClient = new MongoClient("social1.atc.gr");
	    	MongoDatabase db = mongoClient.getDatabase(database);
	    	
	        System.out.println("Post number : " + db.getCollection("Post").count());
	        FindIterable<Document> iterable = db.getCollection("Post").find();

	        iterable.forEach(new Block<Document>() {
	            public void apply(final Document document) {
	            	String strId = document.get("_id").toString();
	            	String title = document.get("title").toString();
	            	String contributor = document.get("contributor").toString();
	            	
	            	String shared = null;
	            	if (document.get("shared") != null)
	            		shared = document.get("shared").toString();
	            	
	            	// parsing data
	            	try {
		            	String strContributorId = contributor.split("id\" : \"| }")[1];
		            	String strSharedId = "0";
		            	if (shared != null)
		            		strSharedId = shared.split("id\" : \"| }")[1];
		            	
		            	// cast String to Long
		            	Long id = 0L;
		            	try {
		            		id = Long.parseLong(convertToValidDigits(strId));
		            	} catch (NumberFormatException ex) {
		            		System.err.println("Invalid ID, " + ex.getMessage());
		            	}
		            	Long contributorId = 0L;
		            	try {
		            		contributorId = Long.parseLong(convertToValidDigits(strContributorId));
		            	} catch (NumberFormatException ex) {
		            		System.err.println("Invalid ID, " + ex.getMessage());
		            	}
		            	Long sharedId = 0L;
		            	try {
		            		sharedId = Long.parseLong(convertToValidDigits(strSharedId));
		            	} catch (NumberFormatException ex) {
		            		System.err.println("Invalid ID, " + ex.getMessage());
		            	}
		            	
		            	JSONObject json = new JSONObject();
		            	JSONObject user = new JSONObject();
		            	
		            	user.put("id", contributorId);
		            	user.put("name", strContributorId);
		            	
		            	json.put("id", id);		// hack
		            	json.put("user", user);
		            	json.put("text", title);
		            	if (shared != null) {
		            		json.put("in_reply_to_user_id", sharedId);
		            		json.put("in_reply_to_status_id", sharedId);
		            		json.put("in_reply_to_screen_name", strSharedId);
		            	}
		    
		                outputFile.println(json.toString());
		     
	            	} catch (ArrayIndexOutOfBoundsException ex) {
	            		System.err.println("Array index out of bounds, " + ex.getMessage());
	            	}
	            }
	        });
	
	        mongoClient.close();
    	} finally {  
    		outputFile.close();
    	}
    	
    	RoleAnalysis.update(database);
    }
}
