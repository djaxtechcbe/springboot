package com.howtodoinjava.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.howtodoinjava.demo.model.Employee;
import com.howtodoinjava.demo.model.Project;

import java.util.List;
import java.io.Serializable;

@Document
public class Manager implements Serializable {

	@Id
	private Integer id;
	private String firstName;
	private String lastName;
	private String email;
	@DBRef
	private List<Employee> employees;
	@DBRef
	private Project project;
	
	public Manager() {
		
	}
	public Manager(Integer id, String firstName, String lastName, String email,List<Employee> employees,Project project) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.project = project;
		this.employees = employees;
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
	public List<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	@Override
	public String toString() {
		return "Manager [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + "]";
	}
}
