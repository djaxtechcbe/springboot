package com.howtodoinjava.demo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "visit_data")
// @Indexed
public class VisitData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1681780072488990675L;

	@Id
	private String idvisit;

	private Integer idsite;

	private String idvisitor;

	private String config_id;

	private Integer config_cookie;

	private Integer visitor_count_visits;

	private Date visit_first_action_time;

	private Date visit_last_action_time;

	private Integer visitor_days_since_first;

	private Integer visitor_days_since_last;

	private Integer visitor_returning;

	private String visitor_localtime;

	private String referer_keyword;

	private Integer visit_total_time;

	private Integer visit_entry_idaction_name;

	private Integer visit_entry_idaction_url;

	private Integer visit_exit_idaction_url;

	private Integer visit_exit_idaction_name;

	private Integer visit_total_actions;

	private Integer visit_total_interactions;

	private Integer visit_total_searches;

	private Integer visit_total_events;

	private Date createdOn;

	private Date updatedOn;

	private List<Integer> site_id;

	private List<String> ip_list;

	public List<Integer> getSite_id() {
		return site_id;
	}

	public void setSite_id(List<Integer> site_id) {
		this.site_id = site_id;
	}

	public List<String> getIp_list() {
		return ip_list;
	}

	public void setIp_list(List<String> ip_list) {
		this.ip_list = ip_list;
	}

	public String getIdvisit() {
		return idvisit;
	}

	public void setIdvisit(String idvisit) {
		this.idvisit = idvisit;
	}

	public Integer getIdsite() {
		return idsite;
	}

	public void setIdsite(Integer idsite) {
		this.idsite = idsite;
	}

	public String getIdvisitor() {
		return idvisitor;
	}

	public void setIdvisitor(String idvisitor) {
		this.idvisitor = idvisitor;
	}

	public String getConfig_id() {
		return config_id;
	}

	public void setConfig_id(String config_id) {
		this.config_id = config_id;
	}

	public Integer getConfig_cookie() {
		return config_cookie;
	}

	public void setConfig_cookie(Integer config_cookie) {
		this.config_cookie = config_cookie;
	}

	public Integer getVisitor_count_visits() {
		return visitor_count_visits;
	}

	public void setVisitor_count_visits(Integer visitor_count_visits) {
		this.visitor_count_visits = visitor_count_visits;
	}

	public Date getVisit_first_action_time() {
		return visit_first_action_time;
	}

	public void setVisit_first_action_time(Date visit_first_action_time) {
		this.visit_first_action_time = visit_first_action_time;
	}

	public Date getVisit_last_action_time() {
		return visit_last_action_time;
	}

	public void setVisit_last_action_time(Date visit_last_action_time) {
		this.visit_last_action_time = visit_last_action_time;
	}

	public Integer getVisitor_days_since_first() {
		return visitor_days_since_first;
	}

	public void setVisitor_days_since_first(Integer visitor_days_since_first) {
		this.visitor_days_since_first = visitor_days_since_first;
	}

	public Integer getVisitor_days_since_last() {
		return visitor_days_since_last;
	}

	public void setVisitor_days_since_last(Integer visitor_days_since_last) {
		this.visitor_days_since_last = visitor_days_since_last;
	}

	public Integer getVisitor_returning() {
		return visitor_returning;
	}

	public void setVisitor_returning(Integer visitor_returning) {
		this.visitor_returning = visitor_returning;
	}

	public String getVisitor_localtime() {
		return visitor_localtime;
	}

	public void setVisitor_localtime(String visitor_localtime) {
		this.visitor_localtime = visitor_localtime;
	}

	public String getReferer_keyword() {
		return referer_keyword;
	}

	public void setReferer_keyword(String referer_keyword) {
		this.referer_keyword = referer_keyword;
	}

	public Integer getVisit_total_time() {
		return visit_total_time;
	}

	public void setVisit_total_time(Integer visit_total_time) {
		this.visit_total_time = visit_total_time;
	}

	public Integer getVisit_entry_idaction_name() {
		return visit_entry_idaction_name;
	}

	public void setVisit_entry_idaction_name(Integer visit_entry_idaction_name) {
		this.visit_entry_idaction_name = visit_entry_idaction_name;
	}

	public Integer getVisit_entry_idaction_url() {
		return visit_entry_idaction_url;
	}

	public void setVisit_entry_idaction_url(Integer visit_entry_idaction_url) {
		this.visit_entry_idaction_url = visit_entry_idaction_url;
	}

	public Integer getVisit_exit_idaction_url() {
		return visit_exit_idaction_url;
	}

	public void setVisit_exit_idaction_url(Integer visit_exit_idaction_url) {
		this.visit_exit_idaction_url = visit_exit_idaction_url;
	}

	public Integer getVisit_exit_idaction_name() {
		return visit_exit_idaction_name;
	}

	public void setVisit_exit_idaction_name(Integer visit_exit_idaction_name) {
		this.visit_exit_idaction_name = visit_exit_idaction_name;
	}

	public Integer getVisit_total_actions() {
		return visit_total_actions;
	}

	public void setVisit_total_actions(Integer visit_total_actions) {
		this.visit_total_actions = visit_total_actions;
	}

	public Integer getVisit_total_interactions() {
		return visit_total_interactions;
	}

	public void setVisit_total_interactions(Integer visit_total_interactions) {
		this.visit_total_interactions = visit_total_interactions;
	}

	public Integer getVisit_total_searches() {
		return visit_total_searches;
	}

	public void setVisit_total_searches(Integer visit_total_searches) {
		this.visit_total_searches = visit_total_searches;
	}

	public Integer getVisit_total_events() {
		return visit_total_events;
	}

	public void setVisit_total_events(Integer visit_total_events) {
		this.visit_total_events = visit_total_events;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
}
