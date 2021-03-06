package com.howtodoinjava.demo.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.howtodoinjava.demo.model.Manager;

@Repository
public interface ManagerRepo extends MongoRepository<Manager, Integer> {
}
