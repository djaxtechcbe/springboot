package com.howtodoinjava.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.howtodoinjava.demo.dao.AddressRepo;
import com.howtodoinjava.demo.dao.EmployeeRepo;
import com.howtodoinjava.demo.dao.ManagerRepo;
import com.howtodoinjava.demo.dao.UserRepo;
import com.howtodoinjava.demo.daoImpl.ProjectImpl;
import com.howtodoinjava.demo.jwtauth.LoginRequest;
import com.howtodoinjava.demo.model.Address;
import com.howtodoinjava.demo.model.Dashboard;
import com.howtodoinjava.demo.model.Employee;
import com.howtodoinjava.demo.model.Manager;
import com.howtodoinjava.demo.model.Project;
import com.howtodoinjava.demo.model.ProjectSummary;
import com.howtodoinjava.demo.model.Users;
import com.howtodoinjava.demo.sequence.SequenceGenerateUtil;

@RestController
// @PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/emp")
public class EmployeeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);

	@Value("${sequence.key}")
	private String sequenceKey;

	@Autowired
	private EmployeeRepo emp;
	@Autowired
	private ManagerRepo mgr;
	@Autowired
	private ProjectImpl pjt;
	@Autowired
	private UserRepo userepo;
	@Autowired
	private SequenceGenerateUtil seqUtil;
	@Autowired
	private AddressRepo addrepo;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Employee addEmp(@RequestBody Employee employee) {
		LOGGER.info("Entered create service");
		employee.setId(seqUtil.getNextSequenceId(sequenceKey));
		employee.getManager().setId(seqUtil.getNextSequenceId(sequenceKey));
		employee.getProject().setId(seqUtil.getNextSequenceId(sequenceKey));
		employee.getAddress().setId(seqUtil.getNextSequenceId(sequenceKey));
		LOGGER.info("Employee sequence id, " + employee.getId());
		return emp.save(employee);
	}

	@RequestMapping(value = "/createaddress", method = RequestMethod.POST)
	public Address addEmpAddress(@RequestBody Address address) {
		address.setId(seqUtil.getNextSequenceId(sequenceKey));
		return addrepo.save(address);
	}

	// @PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public List<Employee> getAllEmployee() {
		LOGGER.info("Entered retrieve service");
		return emp.findAll();
	}

	// The value of key can be method argument
	// Given multiple cache names to store data
	// @PreAuthorize("hasRole('USER')")
	@Cacheable(cacheNames = { "primary", "secondary" }, key = "#empId")
	@RequestMapping(value = "/{empId}", method = RequestMethod.GET)
	public Employee getEmployee(@PathVariable int empId) {
		if (emp.existsById(empId)) {
			Employee employee = emp.findById(empId).get();
			LOGGER.info("emp info ," + employee);
			return employee;
		} else {
			LOGGER.info("Unable to Find employee");
			return new Employee();
		}
		// return emp.findOne(adId);
		// LOGGER.info("Entered retrieve specific service");
		// return null;
	}

	@CacheEvict(value = "employee", key = "#empId", allEntries = true)
	@RequestMapping(value = "/{empId}", method = RequestMethod.DELETE)
	public String delete(@PathVariable int empId) {
		LOGGER.info("Entered delete service");
		if (emp.existsById(empId)) {
			Employee employee = emp.findById(empId).get();
			LOGGER.info("emp info ," + employee);
			emp.delete(employee);
		} else {
			LOGGER.info("Unable to Find employee");
			return "Deletion process is not possible";
		}
		return "Deleted Successfully";
	}

	@Caching(put = { @CachePut(value = "primary", key = "#empId") }, evict = {
			@CacheEvict(value = "secondary", key = "#empId") })
	// @CachePut(value = "employee",key = "#empId") -- working code
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public Employee update(@RequestParam int empId, @RequestParam Integer mId) {
		Employee employee = null;
		LOGGER.info("Entered update service");
		if (emp.existsById(empId)) {
			employee = emp.findById(empId).get();
			employee.setMId(mId);
			LOGGER.info("emp info ," + employee);
			emp.save(employee);
		} else {
			LOGGER.info("Unable to Find employee");
			return new Employee();
		}
		return employee;
	}

	@RequestMapping(value = "/mgr/createmgr", method = RequestMethod.POST)
	public Manager createMgr(@RequestBody Manager manager) {
		LOGGER.info("Entered create manager service");
		return mgr.save(manager);
	}

	@RequestMapping(value = "/pjt/createproject", method = RequestMethod.POST)
	public Project createProject(@RequestBody Project project) {
		LOGGER.info("Entered create project service");
		return pjt.createProject(project);
	}

	// @PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/pjt/getProjects", method = RequestMethod.GET)
	public List<Project> getProjects() {
		LOGGER.info("Entered get all projects service");
		return pjt.getProjects();
	}

	// In the above mapping, getProjectById method will put project into a cache
	// named as ‘projects’,
	// identifies that project by the key as ‘projectId' and will only store a
	// project with cost greater than 100000
	// @Cacheable(value = "projects", key = "#p0", unless = "#result.cost < 100000")
	@Cacheable(value = "projects", key = "#pId", unless = "#result.cost > 100000")
	@RequestMapping(value = "/pjt/getProjects/{pId}", method = RequestMethod.GET)
	public Project getProjectById(@PathVariable Integer pId) {
		return pjt.getProjectById(pId);
	}

	@RequestMapping(value = "/mgr/assignManager", method = RequestMethod.GET)
	public Project assignManager(@RequestParam Integer mId, @RequestParam Integer pId) {
		return pjt.assignManager(mId, pId);
	}

	// @PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/getEmp/{lastName}", method = RequestMethod.GET)
	public Employee getEmpByLastName(@PathVariable String lastName) {
		return emp.findByLastName(lastName);
	}

/*	// @PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/getEmpByMgr/{mId}", method = RequestMethod.GET)
	public List<Employee> getEmpByMgr(@PathVariable Integer mId) {
		Sort sort = new Sort(new Sort.Order(Direction.DESC, "firstName"));
		// Pageable pageable = new PageRequest(0,10,Direction.DESC,"firstName");
		Pageable pageable = new PageRequest(0, 10, sort);
		return emp.findByMId(mId, pageable);
	}

	@RequestMapping(value = "/getEmpByQuery/{firstName}/{mId}/{email}", method = RequestMethod.GET)
	public List<Employee> getEmpByQuery(@PathVariable String firstName, @PathVariable Integer mId,
			@PathVariable String email) {
		// Sort sort = new Sort(new Sort.Order(Direction.DESC, "firstName"));
		// Pageable pageable = new PageRequest(0,10,Direction.DESC,"firstName");
		Pageable pageable = new PageRequest(0, 10);
		List<Employee> employees = emp.findEmp(firstName, mId, email, pageable);
		LOGGER.info("Employess count, " + employees.size());
		return employees;
	}

	@RequestMapping(value = "/searchEmp/{empId}/{email}/{state}/{country}", method = RequestMethod.GET)
	public List<Employee> searchEmployeeInfo(@PathVariable Integer empId, @PathVariable String email,
			@PathVariable String state, @PathVariable String country) {
		Pageable pageable = new PageRequest(0, 10);
		return emp.searchEmployeeInfo(empId, email, state, country, pageable);
	}
*/
	// Aggergation logic is done here
	@RequestMapping(value = "/pjt/aggregateAll", method = RequestMethod.GET)
	public List<ProjectSummary> aggregateAll() {
		return pjt.aggregateAll();
	}

	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public @ResponseBody List<Dashboard> dashboardEntryReport() {
		try {
			// LOGGER.info("Entry point of dashboardEntryReport method");
			return pjt.getReport();
			// LOGGER.info("repsonse " + response.getSuccessResponse());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	// Aggergation logic is done here
	@RequestMapping(value = "/pjt/aggregateByDomain/{minCost}/{maxCost}/{domain}", method = RequestMethod.GET)
	public List<ProjectSummary> aggregateByDomain(@PathVariable Long minCost, @PathVariable Long maxCost,
			@PathVariable String domain) {
		return pjt.aggregateByDomain(minCost, maxCost, domain);
	}

	/*@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		LOGGER.info("Entered emp login service");
		Users user = userepo.findByUserName(loginRequest.getUsername());
		if (user != null) {
			LOGGER.info("User info :" + user.getUserName() + "," + user.getPassword() + "," + user.getUserId());
			LOGGER.info("User is found here");
			return ResponseEntity.ok(user);
		} else {
			LOGGER.info("User is not found here");
		}
		return null;
	}*/
}
