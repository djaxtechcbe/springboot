//package com.howtodoinjava.demo.jwtauth;
//
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.howtodoinjava.demo.model.Users;
//import com.howtodoinjava.demo.model.Role;
//import com.howtodoinjava.demo.dao.UserRepo;
//
//import java.util.List;
//import java.util.ArrayList;
//
//@Service
//public class UserDetailsImpl implements UserDetailsService {
//
//    @Autowired
//	private UserRepo userRepo;
//    
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Users users = userRepo.findByUserName(username);
//        if (users == null) {
//            throw new UsernameNotFoundException("User not found :" + username);
//        }
//        List<GrantedAuthority> authList = getAuthorities(users.getRoles());
//        return new User(users.getUserName(), users.getPassword(), authList);
//    }
//    
//    private List<GrantedAuthority> getAuthorities(List<Role> roles) {
//        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
//        authList.add(new SimpleGrantedAuthority("ROLE_USER"));
//
//        you can also add different roles here
//        //for example, the user is also an admin of the site, then you can add ROLE_ADMIN
//        //so that he can view pages that are ROLE_ADMIN specific
//        for(Role role : roles) {
//			if (role != null && role.getRole().trim().length() > 0) {
//				if (role.getRole().equals("admin")) {
//					authList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//				}
//				if (role.getRole().equals("user")) {
//					authList.add(new SimpleGrantedAuthority("ROLE_USER"));
//				}
//			}
//		}        
//        return authList;
//    }
//}
