package com.howtodoinjava.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import com.howtodoinjava.demo.cascade.CascadeSave; // Custom annotation

import java.io.Serializable;

@Document
public class Employee implements Serializable {

	@Id
	private Integer id;
	@DBRef
	@CascadeSave
	private Project project;
	@DBRef
	@CascadeSave
	private Manager manager;
	@DBRef
	@CascadeSave	// Cascade works 
	private Address address;
	private String firstName;
	private String lastName;
	private String email;
	private Integer pId;	// Project ID
	private Integer mId;	// Manager ID
	
	public Employee() {
		
	}
	public Employee(Integer id, String firstName, String lastName, String email,Project project,Manager manager,
					Integer pId,Integer mId,Address address) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.project = project;
		this.manager = manager;
		this.pId = pId;
		this.mId = mId;
		this.address = address;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getPId() {
		return pId;
	}
	public void setPId(Integer pId) {
		this.pId = pId;
	}
	public Integer getMId() {
		return mId;
	}
	public void setMId(Integer mId) {
		this.mId = mId;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public Manager getManager() {
		return manager;
	}
	public void setManager(Manager manager) {
		this.manager = manager;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "Employee [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + "]";
	}
}
