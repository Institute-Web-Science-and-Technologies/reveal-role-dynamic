package de.unikoblenz.west.reveal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

import de.unikoblenz.west.reveal.analytics.CommunityAnalysis;
import de.unikoblenz.west.reveal.roles.RoleAssociation;
import de.unikoblenz.west.reveal.roles.UserWithFeatures;
import de.unikoblenz.west.reveal.roles.UserWithRole;
import de.unikoblenz.west.reveal.structures.Community;
import de.unikoblenz.west.reveal.twitter.snow.SnowCommunityFactory;

public class RoleAnalysis {

	public static void update(String database) throws IOException {
		// Setup for reading tweets in JSON format from input files 
		String path = "./";
		File inDir = new File(path);
		String[] jsonFiles = inDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("mongo-data");
			}
		});
		for (int i = 0; i< jsonFiles.length; i++) {
			jsonFiles[i] = path + jsonFiles[i];
		}

		// Calling the factory methods to create the internal community structures from the tweets in the files. 
		System.out.println("Constructing community ...");
		Community seCommunity = SnowCommunityFactory.parseCommunity("snow", jsonFiles);
		int minLimit = 1;
		System.out.println("Community Analysis, using minlimit: "+minLimit);
		HashSet<UserWithFeatures> uwf = CommunityAnalysis.analyseUserFeatures(seCommunity, minLimit);
		
		System.out.println("Converting users ...");
		// Convert into UserWithRole objects suitable for Role analysis
		HashSet<UserWithRole> users = new HashSet<UserWithRole>();
		for (UserWithFeatures userFeatures : uwf) {
			UserWithRole u = userFeatures.convertToUserWithRole();
			users.add(u);
		}
		
		// Actual role analysis
		System.out.println("Processing users (Role Analysis) ...");
		RoleAssociation ra = new RoleAssociation();
		ra.process(users);
		
		// Write results back to mongoDB
		System.out.println("Writing results to mongoDB, number of users : " + users.size());
		
		MongoClient mongoClient = new MongoClient("social1.atc.gr");
    	MongoDatabase db = mongoClient.getDatabase(database);
    	
    	MongoCollection<Document> coll = db.getCollection("Role");
    	List<WriteModel<Document>> writes = new ArrayList<WriteModel<Document>>();
    	
    	for (UserWithRole uwr : users) {
    		writes.add(
    		    new UpdateOneModel<Document>(
    		        new Document("account_id", uwr.username),	// filter
    		        new Document("$set", new Document("ukob_role", uwr.role)),		// update
    		    	new UpdateOptions().upsert(true))
    		);
    	}
    	
    	coll.bulkWrite(writes);
    	mongoClient.close();
	}
	
	public static void main(String[] args) throws IOException {
		
	}

}
