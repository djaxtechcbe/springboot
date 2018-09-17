package com.howtodoinjava.demo.authenticate;

import java.util.Base64;
import java.io.Serializable;

public class BaseEncodeDecode implements Serializable{
	
	// username:password --> admin:admin_2018 --> YWRtaW46YWRtaW5fMjAxOA==
	// username:password --> praveen:praveen_2018 --> cHJhdmVlbjpwcmF2ZWVuXzIwMTg=
	
	public String encodeText(String password) {
		try {
			return Base64.getEncoder().encodeToString(password.getBytes("utf-8"));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String decodeText(String encodeText) {
		try {
			return new String(Base64.getDecoder().decode(encodeText));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	/*public static void main(String[] args) throws Exception {
		BaseEncodeDecode obj = new BaseEncodeDecode();
		String encodeText = obj.encodeText("Praveen");
		System.out.println(encodeText);
		System.out.println(obj.decodeText(encodeText));
	}*/
}
