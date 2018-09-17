//package com.howtodoinjava.demo.jwt;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Date;
//
//import com.howtodoinjava.demo.model.Users;
//import com.howtodoinjava.demo.model.Role;
//
//import static com.howtodoinjava.demo.constants.SecurityConstants.EXPIRATION_TIME;
//import static com.howtodoinjava.demo.constants.SecurityConstants.HEADER_STRING;
//import static com.howtodoinjava.demo.constants.SecurityConstants.SECRET;
//import static com.howtodoinjava.demo.constants.SecurityConstants.TOKEN_PREFIX;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//	
//	private Users creds = null;
//    private AuthenticationManager authenticationManager;
//    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
//
//    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
//		LOGGER.info("JWTAuthenticationFilter parma const, " + authenticationManager);
//        this.authenticationManager = authenticationManager;
//    }
//	
//	// Given User credentials and send to Authentication Manager
//	// where we parse the user's credentials and issue them to the AuthenticationManager
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest req,HttpServletResponse res) 
//		throws AuthenticationException {
//		LOGGER.info("JWTAuthenticationFilter attemptAuthentication, ");
//        try {
//            creds = new ObjectMapper().readValue(req.getInputStream(), Users.class);
//            List<GrantedAuthority> authList = getAuthorities(creds.getRoles());
//            return authenticationManager.authenticate(
//				   new UsernamePasswordAuthenticationToken(creds.getUserName(),creds.getPassword(),authList));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//	
//	// JWT Token is generated here
//	// which is the method called when a user successfully logs in. We use this method to generate a JWT for this user.
//    @Override
//    protected void successfulAuthentication(HttpServletRequest req,HttpServletResponse res,
//                                            FilterChain chain,Authentication auth) throws IOException, ServletException {
//		LOGGER.info("JWTAuthenticationFilter successfulAuthentication, ");
//        String token = Jwts.builder()
//                .setSubject(((User) auth.getPrincipal()).getUsername())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SignatureAlgorithm.HS512, SECRET)
//                .compact();
//        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
//        creds.setAuthToken(token); // Set JWT token to user 
//    }
//    
//    private List<GrantedAuthority> getAuthorities(List<Role> roles) {		
//        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
//        authList.add(new SimpleGrantedAuthority("ROLE_USER"));
//
//        //you can also add different roles here
//        //for example, the user is also an admin of the site, then you can add ROLE_ADMIN
//        //so that he can view pages that are ROLE_ADMIN specific
//        for(Role role:roles){
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
