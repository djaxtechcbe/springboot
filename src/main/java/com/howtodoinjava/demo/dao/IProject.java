package com.howtodoinjava.demo.dao;

import java.util.List;

import com.howtodoinjava.demo.model.Dashboard;
import com.howtodoinjava.demo.model.Project;
import com.howtodoinjava.demo.model.ProjectSummary;

public interface IProject {

	Project createProject(Project project);

	Project assignManager(Integer mId, Integer pId);

	List<Project> getProjects();

	Project getProjectById(Integer pId);

	List<ProjectSummary> aggregateByDomain(Long minCost, Long maxCost, String domain);

	List<ProjectSummary> aggregateAll();

	List<Dashboard> getReport();
}
