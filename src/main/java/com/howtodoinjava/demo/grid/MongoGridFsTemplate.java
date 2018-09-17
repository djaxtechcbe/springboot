/*package com.howtodoinjava.demo.grid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.ArrayList;
  
@Configuration
public class MongoGridFsTemplate extends AbstractMongoConfiguration{
	
	@Value("${spring.data.mongodb.host}")
	private String host; 
	
	@Value("${spring.data.mongodb.database}")
	private String db;
	
	@Value("${spring.data.mongodb.port}")
	private Integer port; 
	
	@Value("${spring.data.mongodb.username}")
	private String username;
	
	@Value("${spring.data.mongodb.password}")
	private String password; 
	
	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
	    return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}
	
	@Override
	protected String getDatabaseName() {
		return db;
	}
 
	@Override	
	public MongoClient mongoClient() {
		return new MongoClient(getServerAddress(), new ArrayList<MongoCredential>() {{ add(getMongoCredential()); }});
	}
	
	@Bean
	public ServerAddress getServerAddress() {
		return new ServerAddress(host,port);
	}
	
	@Bean
	public MongoCredential getMongoCredential() {
		return MongoCredential.createCredential(username, db, password.toCharArray());
	}	
}
*/
