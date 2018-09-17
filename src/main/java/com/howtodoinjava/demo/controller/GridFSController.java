package com.howtodoinjava.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.howtodoinjava.demo.exception.FileStorageException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.bson.types.ObjectId;

@RestController
@RequestMapping("/grid")
public class GridFSController {

   private static final Logger LOGGER = LoggerFactory.getLogger(GridFSController.class);
      
   @Autowired
   GridFsTemplate gridFsTemplate;    
   
   @RequestMapping("/store")
   public ResponseEntity<String> storeFile() {	
	   InputStream inputStream = null;
	   try {
			DBObject metaData = new BasicDBObject();
			metaData.put("admin", "admin");
			metaData.put("filetype", "image");
			
			inputStream = new FileInputStream("src/main/resources/image/test.png"); 
			String id = String.valueOf(gridFsTemplate.store(inputStream, "test.png", "image/png", metaData));
			
			metaData.put("filetype","text");
			inputStream = new FileInputStream("src/main/resources/file/SimplePiwikTracker.java"); 
			gridFsTemplate.store(inputStream, "sample.txt", "text/plain", metaData);
						
	   }catch(IOException ex) {
			throw new FileStorageException("Could not store file Please try again!", ex);
	   }
	   return new ResponseEntity<String>("Successfully stored",HttpStatus.OK);
   }    
}
