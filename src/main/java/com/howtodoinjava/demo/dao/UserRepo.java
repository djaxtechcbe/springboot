package com.howtodoinjava.demo.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.howtodoinjava.demo.model.Users;

@Repository
public interface UserRepo extends MongoRepository<Users, Integer> {	
	
	Users findByUserName(String userName);
	
}
