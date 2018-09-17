package com.howtodoinjava.demo.model;

import java.util.List;
import java.io.Serializable;

public class ProjectSummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5519265951751029528L;
	private List<Integer> projects;
	private Long avgSpentAmount;
	private Long totalRevenue;
	private String domain;
	private String country;
	private String test;
	private int totalcount;

	public int getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public List<Integer> getProjects() {
		return projects;
	}

	public void setProjects(List<Integer> projects) {
		this.projects = projects;
	}

	public Long getAvgSpentAmount() {
		return avgSpentAmount;
	}

	public void setAvgSpentAmount(Long avgSpentAmount) {
		this.avgSpentAmount = avgSpentAmount;
	}

	public Long getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(Long totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
