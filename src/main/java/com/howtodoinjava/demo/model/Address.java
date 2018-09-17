package com.howtodoinjava.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;

@Document(collection = "empaddress")
public class Address implements Serializable {

	@Id
	private Integer id;
	private String street;
	private Integer doorNum;
	private String city;
	private String state;
	private Long pincode;
	//@DBRef
	private Employee employee;
	
	public Address(Integer id, String street, Integer doorNum, String city,String state,Long pincode,Employee employee) {	
		this.id = id;
		this.street = street;
		this.doorNum = doorNum;
		this.city = city;
		this.state = state;
		this.pincode = pincode;
		this.employee = employee;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public Integer getDoorNum() {
		return doorNum;
	}
	public void setDoorNum(Integer doorNum) {
		this.doorNum = doorNum;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Long getPincode() {
		return pincode;
	}
	public void setPincode(Long pincode) {
		this.pincode = pincode;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}	
}
