package com.howtodoinjava.demo.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Indexed;

@Document(collection = "log_action")
@Indexed
public class LogAction {

	@Id
	private String idaction;

	private int id;

	private int siteId;

	private String name;

	private int hash;

	private int type;

	private int url_prefix;

	private Date createdOn;

	public String getIdaction() {
		return idaction;
	}

	public void setIdaction(String idaction) {
		this.idaction = idaction;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHash() {
		return hash;
	}

	public void setHash(int hash) {
		this.hash = hash;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUrl_prefix() {
		return url_prefix;
	}

	public void setUrl_prefix(int url_prefix) {
		this.url_prefix = url_prefix;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
}
