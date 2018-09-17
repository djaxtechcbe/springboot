package com.howtodoinjava.demo.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/***
 * Dash board report modal
 * 
 * @author djaxtech
 *
 */
// @Document
// @Indexed
public class Dashboard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1372624600554487520L;

	// @Id
	private Long reportId;
	private Long idsite;
	// @Indexed(unique = true)
	private String idvisitor;
	private Integer totalVisitors;
	private Integer newVisitors;
	private Integer pageViews;

	private Map<String, Integer> visitorByGroup;
	private Map<String, Integer> uniqueVisitorByGroup;
	private Map<String, Integer> actionByGroup;
	private Map<String, Integer> geoChartByCountry;
	private Map<String, Integer> geoChartByCity;

	public Map<String, Integer> getGeoChartByCountry() {
		return geoChartByCountry;
	}

	public void setGeoChartByCountry(Map<String, Integer> geoChartByCountry) {
		this.geoChartByCountry = geoChartByCountry;
	}

	public Map<String, Integer> getGeoChartByCity() {
		return geoChartByCity;
	}

	public void setGeoChartByCity(Map<String, Integer> geoChartByCity) {
		this.geoChartByCity = geoChartByCity;
	}

	public Map<String, Integer> getVisitorByGroup() {
		return visitorByGroup;
	}

	public void setVisitorByGroup(Map<String, Integer> visitorByGroup) {
		this.visitorByGroup = visitorByGroup;
	}

	public Map<String, Integer> getUniqueVisitorByGroup() {
		return uniqueVisitorByGroup;
	}

	public void setUniqueVisitorByGroup(Map<String, Integer> uniqueVisitorByGroup) {
		this.uniqueVisitorByGroup = uniqueVisitorByGroup;
	}

	public Map<String, Integer> getActionByGroup() {
		return actionByGroup;
	}

	public void setActionByGroup(Map<String, Integer> actionByGroup) {
		this.actionByGroup = actionByGroup;
	}

	public Long getIdsite() {
		return idsite;
	}

	public void setIdsite(Long idsite) {
		this.idsite = idsite;
	}

	public String getIdvisitor() {
		return idvisitor;
	}

	public void setIdvisitor(String idvisitor) {
		this.idvisitor = idvisitor;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Integer getTotalVisitors() {
		return totalVisitors;
	}

	public void setTotalVisitors(Integer totalVisitors) {
		this.totalVisitors = totalVisitors;
	}

	public Integer getNewVisitors() {
		return newVisitors;
	}

	public void setNewVisitors(Integer newVisitors) {
		this.newVisitors = newVisitors;
	}

	public Integer getPageViews() {
		return pageViews;
	}

	public void setPageViews(Integer pageViews) {
		this.pageViews = pageViews;
	}
}
