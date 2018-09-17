package com.howtodoinjava.demo.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import com.howtodoinjava.demo.model.Address;

import java.util.List;

@Repository
public interface AddressRepo extends MongoRepository<Address, Integer> {
}
