package com.howtodoinjava.demo.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.howtodoinjava.demo.model.Role;

@Repository
public interface RoleRepo extends MongoRepository<Role, Integer> {		
}
