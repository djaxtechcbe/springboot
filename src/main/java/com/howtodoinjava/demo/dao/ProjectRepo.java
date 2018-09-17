package com.howtodoinjava.demo.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.howtodoinjava.demo.model.Project;

@Repository
public interface ProjectRepo extends MongoRepository<Project, Integer> {
}
