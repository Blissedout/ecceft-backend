package com.ecceft.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AesKeyStorageService {
//
//    private static final String ALGORITHM = "AES";
//    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
//    private static final int GCM_IV_LENGTH = 12;
//    private static final int GCM_TAG_LENGTH = 128;

    @Value("${aes.master.key}")
    private String masterKeyBase64;
    
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(masterKeyBase64);
       secretKey = new SecretKeySpec(decodedKey, "AES");
    }

    public String encryptPrivateKey(String privateKey) throws Exception {
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encrypted = cipher.doFinal(privateKey.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + encrypted.length];
            
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            
            return Base64.getEncoder().encodeToString(combined);
    }

    public String decryptPrivateKey(String encryptedData) throws Exception {
    
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            
            byte[] iv = new byte[12];
            byte[] cipherText = new byte[decoded.length - 12];
          
            System.arraycopy(decoded, 0, iv, 0, 12);
            System.arraycopy(decoded, 12, cipherText, 0, cipherText.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decrypted = cipher.doFinal(cipherText);
            return new String(decrypted, StandardCharsets.UTF_8);
    }
}
