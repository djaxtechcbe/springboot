/*package com.howtodoinjava.demo.authenticate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.howtodoinjava.demo.model.Users;
import com.howtodoinjava.demo.dao.UserRepo;

@Component
public class SecurityService implements UserDetailsService { 
	
  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);
	
  public static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
  @Autowired
  private UserRepo userRepo;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	  
    Users user = userRepo.findByUserName(username);

    if(user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    
    LOGGER.info("User info " + user.getUserName() + ":" + user.getRole());
	
	List<GrantedAuthority> authList = getAuthorities(user.getRole());
	
	//should get the encoded password if given plain text string
    //String encodedPassword = passwordEncoder.encode(user.getPassword());
	
    //List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("user"));

    return new User(user.getUserName(), user.getPassword(), authList);
  }*/
  
  /**
   * Roles (as they are used in many examples) are just "permissions" with a naming convention that says that 
   * a role is a GrantedAuthority that starts with the prefix ROLE_
   * the ROLE_ prefix is used as a default
   * a role is just an authority with a special ROLE_ prefix. So in Spring security 3 
   * @PreAuthorize("hasRole('ROLE_XYZ')") is the same as 
   * @PreAuthorize("hasAuthority('ROLE_XYZ')") and in Spring security 4 
   * @PreAuthorize("hasRole('XYZ')") is the same as 
   * @PreAuthorize("hasAuthority('ROLE_XYZ')")
   * */
 /* private List<GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        authList.add(new SimpleGrantedAuthority("ROLE_USER"));

        //you can also add different roles here
        //for example, the user is also an admin of the site, then you can add ROLE_ADMIN
        //so that he can view pages that are ROLE_ADMIN specific
        if (role != null && role.trim().length() > 0) {
            if (role.equals("admin")) {
                authList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }
        return authList;
    }
}
*/
