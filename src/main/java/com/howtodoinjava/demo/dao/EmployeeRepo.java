package com.howtodoinjava.demo.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import com.howtodoinjava.demo.model.Employee;

import java.util.List;

@Repository
public interface EmployeeRepo extends MongoRepository<Employee, Integer> {
	
	Employee findByEmail(String email);
	
	Employee findByLastName(String lastName);
	
	List<Employee> findByMId(Integer mId,Pageable pageable);
	
	// Query equals to firstname and greater than manager id and email is basd on regular expression
	// Projection used in Mongorepository
	@Query("{'firstName': ?0 , 'mId' : {$gt : ?1} , 'email' : {$regex : ?2}}")
	//@Query("{'firstName': ?0 , 'mId' : {$gt : ?1}}")
	List<Employee> findEmp(String firstName,Integer mId,String email,Pageable pageable); // Works
	
	@Query(value = "{'id':{$gt:?0},'email':{$regex:?1},'address.state':{$ne : null},'project.country':?2}")
	List<Employee> searchEmployeeInfo(Integer empId,String email,String state,String country,Pageable pageable); 
}
