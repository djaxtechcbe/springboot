package com.howtodoinjava.demo.daoImpl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.previousOperation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.howtodoinjava.demo.dao.IProject;
import com.howtodoinjava.demo.model.Dashboard;
import com.howtodoinjava.demo.model.Manager;
import com.howtodoinjava.demo.model.Project;
import com.howtodoinjava.demo.model.ProjectSummary;
import com.howtodoinjava.demo.model.VisitData;

/***
 * Projection is used here with help of Query Class
 */

@Repository
public class ProjectImpl implements IProject {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Project> getProjects() {
		return mongoTemplate.findAll(Project.class);
	}

	@Override
	public Project getProjectById(Integer pId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(pId));
		return mongoTemplate.findOne(query, Project.class);
	}

	@Override
	public Project createProject(Project project) {
		mongoTemplate.save(project);
		return project;
	}

	@Override
	public Project assignManager(Integer mId, Integer pId) {
		Project project = null;
		Map<Integer, Project> projects = new HashMap<Integer, Project>();
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(mId));
		Manager manager = mongoTemplate.findOne(query, Manager.class);
		if (manager != null) {
			query = new Query();
			query.addCriteria(Criteria.where("id").is(pId));
			project = mongoTemplate.findOne(query, Project.class);
			projects.put(manager.getId(), project);
			project.setManager(manager);
			mongoTemplate.save(project);
		} else {
			return new Project();
		}
		return project;
	}

	@Override
	public List<Dashboard> getReport() {

		AggregationResults<Dashboard> results = mongoTemplate.aggregate(
				newAggregation(project("idsite"), count().as("totalVisitors")), VisitData.class, Dashboard.class);
		int count = results.getUniqueMappedResult().getTotalVisitors();
		System.out.println(count);
		return results.getMappedResults();
	}

	@Override
	public List<ProjectSummary> aggregateAll() {
		Aggregation aggregation = newAggregation(getProjectionOperation(), count().as("totalcount"));

		AggregationResults<ProjectSummary> groupResults = mongoTemplate.aggregate(aggregation, Project.class,
				ProjectSummary.class);

		System.out.println(groupResults.getUniqueMappedResult().getTotalcount());

		List<ProjectSummary> summary = groupResults.getMappedResults();

		return summary;
	}

	private MatchOperation getMatchOperation(Long minCost, Long maxCost, String domain) {
		Criteria costCriteria = where("cost").gt(minCost).andOperator(where("cost").lt(maxCost))
				.orOperator(where("domain").regex(domain));
		return match(costCriteria);
	}

	private GroupOperation getGroupOperation() {
		return group("country", "domain").sum("revenue").as("totalRevenue").addToSet("id").as("projects").avg("cost")
				.as("avgSpentAmount");
	}

	private ProjectionOperation getProjectionOperation() {
		// return project("projects", "domain", "totalRevenue",
		// "avgSpentAmount").and("country").previousOperation();
		return project("domain");
	}

	private SortOperation getSortOperation() {
		return sort(Sort.Direction.ASC, previousOperation(), "domain");
	}

	@Override
	public List<ProjectSummary> aggregateByDomain(Long minCost, Long maxCost, String domain) {
		Aggregation aggregation = newAggregation(getMatchOperation(minCost, maxCost, domain), getGroupOperation(),
				getProjectionOperation(), getSortOperation());

		AggregationResults groupResults = mongoTemplate.aggregate(aggregation, Project.class, ProjectSummary.class);

		List<ProjectSummary> summary = groupResults.getMappedResults();

		return summary;
	}
	/*
	 * @Override public Object getAllUserSettings(String userId) { Query query = new
	 * Query(); query.addCriteria(Criteria.where("userId").is(userId)); User user =
	 * mongoTemplate.findOne(query, User.class); return user != null ?
	 * user.getUserSettings() : "User not found."; }
	 * 
	 * @Override public String getUserSetting(String userId, String key) { Query
	 * query = new Query(); query.fields().include("userSettings");
	 * query.addCriteria(Criteria.where("userId").is(userId).andOperator(Criteria.
	 * where("userSettings." + key).exists(true))); User user =
	 * mongoTemplate.findOne(query, User.class); return user != null ?
	 * user.getUserSettings().get(key) : "Not found."; }
	 * 
	 * @Override public String addUserSetting(String userId, String key, String
	 * value) { Query query = new Query();
	 * query.addCriteria(Criteria.where("userId").is(userId)); User user =
	 * mongoTemplate.findOne(query, User.class); if (user != null) {
	 * user.getUserSettings().put(key, value); mongoTemplate.save(user); return
	 * "Key added."; } else { return "User not found."; } }
	 */
}
