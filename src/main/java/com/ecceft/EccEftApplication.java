package com.ecceft;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EccEftApplication {
    public static void main(String[] args) {
    	
    	Security.addProvider(new BouncyCastleProvider());
    	
        SpringApplication.run(EccEftApplication.class, args);
    }
}
