package com.howtodoinjava.demo.controller;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Value;

import com.howtodoinjava.demo.model.Users;
import com.howtodoinjava.demo.model.Role;
import com.howtodoinjava.demo.dao.UserRepo;
import com.howtodoinjava.demo.dao.RoleRepo;
import com.howtodoinjava.demo.jwtauth.LoginRequest;
import com.howtodoinjava.demo.jwtauth.JwtAuthenticationResponse;
//import com.howtodoinjava.demo.jwtauth.JwtTokenProvider;
//import com.howtodoinjava.demo.jwtauth.UserDetailsImpl;
import com.howtodoinjava.demo.sequence.SequenceGenerateUtil;

@RestController
@RequestMapping("/admin")
public class AdminController {

   private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
   
   @Value("${sequence.key}")
   private String sequenceKey;
   
//   @Autowired
//   AuthenticationManager authenticationManager;
   
//   @Autowired
//   JwtTokenProvider tokenProvider;
//	
//   @Autowired
//   private UserDetailsImpl userDetailsService;
//   
   @Autowired
   private SequenceGenerateUtil seqUtil;
   
   @Autowired
   private UserRepo userepo;
   
   @Autowired
   private RoleRepo roleRepo;
	
   	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public Users createUser(@RequestBody Users user) {
		LOGGER.info("Entered User create service");
		List<Role> roles = user.getRoles();
		for(Role role : roles) {
			role.setId(seqUtil.getNextSequenceId(sequenceKey));
		}		
		user.setUserId(seqUtil.getNextSequenceId(sequenceKey));
    	return userepo.save(user);
    }
    
    @RequestMapping(value = "/createrole", method = RequestMethod.POST)
    public Role createRole(@RequestBody Role role) {
		role.setId(seqUtil.getNextSequenceId(sequenceKey));
    	return roleRepo.save(role);
    }
    
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//		LOGGER.info("Entered admin login service");
//		Users user = userepo.findByUserName(loginRequest.getUsername());
//		if(user!= null) {
//			LOGGER.info("User info :" + user.getUserName() +","+user.getPassword() +","+user.getUserId());
//			LOGGER.info("User is found here");
//			Authentication authentication = authenticationManager.authenticate(
//					new UsernamePasswordAuthenticationToken(
//							loginRequest.getUsername(),
//							loginRequest.getPassword()
//					)
//			);
//			LOGGER.info("Authentication instance = " + authentication);
//			SecurityContextHolder.getContext().setAuthentication(authentication);
//			String jwt = tokenProvider.generateToken(user.getUserName());
//			LOGGER.info("JWT token =" + jwt);
//			return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
//		} else {
//			LOGGER.info("User is not found here");
//		}		
//		return null;		
//     }
}
