//package com.howtodoinjava.demo.authenticate;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//public class PasswordEncoderGenerator {
//
//	public String bcryptEncoder(String password) {
//		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//		String hashedPassword = passwordEncoder.encode(password);
//		return hashedPassword;
//	}
//
//	public static void main(String[] args) {
//		PasswordEncoderGenerator obj = new PasswordEncoderGenerator();
//		obj.bcryptEncoder("admin_2018");
//	}
//}
