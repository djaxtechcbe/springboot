package com.howtodoinjava.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;
import java.io.Serializable;

import com.howtodoinjava.demo.model.Role;
import com.howtodoinjava.demo.cascade.CascadeSave; // Custom annotation

@Document
@CompoundIndexes({
		@CompoundIndex(name = "multiindex", def = "{'userName' : 1, 'userId': 1}")
	})
public class Users implements Serializable {
	
	@DBRef
	@CascadeSave
	private List<Role> roles;
	//@Id
	private Integer userId;
	//@Indexed // Mark username as an index in mongodb collection
	private String userName;
	private String password;
	private String authToken;
		
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
}
