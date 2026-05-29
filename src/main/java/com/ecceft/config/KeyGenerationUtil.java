package com.ecceft.config;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyGenerationUtil {

    public static void main(String[] args) throws Exception {

    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    
    keyGen.init(256);
    
    SecretKey secretKey = keyGen.generateKey();
    
    System.out.println(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    	
    }
}