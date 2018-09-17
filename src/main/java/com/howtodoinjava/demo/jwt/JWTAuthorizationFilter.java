//package com.howtodoinjava.demo.jwt;
//
//import io.jsonwebtoken.Jwts;
//
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//import static com.howtodoinjava.demo.constants.SecurityConstants.HEADER_STRING;
//import static com.howtodoinjava.demo.constants.SecurityConstants.SECRET;
//import static com.howtodoinjava.demo.constants.SecurityConstants.TOKEN_PREFIX;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
//	
//	private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthorizationFilter.class);
//
//    public JWTAuthorizationFilter(AuthenticationManager authManager) {
//		super(authManager);
//		LOGGER.info("JWTAuthorizationFilter param cons, " + authManager);
//    }
//	
//	// Get request and do validate JWT, if yes process continus and set user info to context
//    @Override
//    protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) 
//			throws IOException, ServletException {
//		LOGGER.info("JWTAuthorizationFilter doFilterInternal, ");
//        String header = req.getHeader(HEADER_STRING);
//        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
//            chain.doFilter(req, res);
//            return;
//        }
//        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        chain.doFilter(req, res);
//    }
//	
//	// Validate JWT token here
//    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
//		LOGGER.info("JWTAuthorizationFilter getAuthentication, ");
//        String token = request.getHeader(HEADER_STRING);
//        if (token != null) {
//            // parse the token.
//            String user = Jwts.parser()
//                    .setSigningKey(SECRET)
//                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
//                    .getBody()
//                    .getSubject();
//            if (user != null) {
//                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
//            }
//            return null;
//        }
//        return null;
//    }
//}
