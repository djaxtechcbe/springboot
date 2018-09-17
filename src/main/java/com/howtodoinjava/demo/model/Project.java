package com.howtodoinjava.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.howtodoinjava.demo.model.Employee;

import java.util.List;
import java.io.Serializable;

@Document
@Indexed
public class Project implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3063225084411796325L;

	public Project() {

	}

	public Project(Integer id, String name, String clientName, String country, List<Employee> employees,
			Manager manager, Long cost, Long revenue) {
		super();
		this.id = id;
		this.name = name;
		this.clientName = clientName;
		this.country = country;
		this.employees = employees;
		this.manager = manager;
		this.cost = cost;
		this.revenue = revenue;
	}

	@Id
	private Integer id;
	private String name;
	private String clientName;
	private String country;
	private String domain;
	@DBRef
	private List<Employee> employees;
	@DBRef
	private Manager manager;
	private Long cost;
	private Long revenue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public Long getCost() {
		return cost;
	}

	public void setCost(Long cost) {
		this.cost = cost;
	}

	public Long getRevenue() {
		return revenue;
	}

	public void setRevenue(Long revenue) {
		this.revenue = revenue;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", Name=" + name + ", clientName=" + clientName + ", Country=" + country + "]";
	}
}
