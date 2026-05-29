package com.ecceft.service;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class EccCryptoService {

    private static final String CURVE_NAME = "secp256r1";
    private static final String ALGORITHM = "ECDSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

  

    public KeyPair generateEccKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
        ECGenParameterSpec spec = new ECGenParameterSpec("secp256r1");
        keyGen.initialize(spec, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    public String publicKeyToHex(PublicKey publicKey) {
    	return Hex.toHexString(publicKey.getEncoded());
//        byte[] encoded = key.getEncoded();
        // X.509 encoded format: remove ASN.1 header to get raw key
        // For secp256r1, the raw key is 65 bytes (uncompressed) or 33 bytes (compressed)
        // We'll use the full encoded form for simplicity, or extract raw
//        return bytesToHex(encoded);
    }

    public String privateKeyToHex(PrivateKey privateKey) {
    	return Hex.toHexString(privateKey.getEncoded());
//        byte[] encoded = key.getEncoded();
//        return bytesToHex(encoded);
    }
    
    
    public PublicKey hexToPublicKey(String hex)
            throws Exception {

        byte[] keyBytes =
                org.bouncycastle.util.encoders.Hex.decode(hex);

        KeyFactory keyFactory =
                KeyFactory.getInstance("EC", "BC");

        return keyFactory.generatePublic(
                new java.security.spec.X509EncodedKeySpec(keyBytes)
        );
    }
    
    public boolean verifySignature(
            String publicKeyHex,
            String canonicalData,
            String signatureR,
            String signatureS
    ) throws Exception {

        PublicKey publicKey =
                hexToPublicKey(publicKeyHex);

        Signature verifier =
                Signature.getInstance(
                        "SHA256withECDSA",
                        "BC"
                );

        verifier.initVerify(publicKey);

        verifier.update(canonicalData.getBytes());

        java.math.BigInteger r =
                new java.math.BigInteger(signatureR, 16);

        java.math.BigInteger s =
                new java.math.BigInteger(signatureS, 16);

        org.bouncycastle.asn1.sec.ECPrivateKey ignored = null;

        org.bouncycastle.asn1.ASN1EncodableVector vector =
                new org.bouncycastle.asn1.ASN1EncodableVector();

        vector.add(
                new org.bouncycastle.asn1.ASN1Integer(r)
        );

        vector.add(
                new org.bouncycastle.asn1.ASN1Integer(s)
        );

        byte[] derSignature =
                new org.bouncycastle.asn1.DERSequence(vector)
                        .getEncoded();

        return verifier.verify(derSignature);
    }

//    public PublicKey hexToPublicKey(String hex) throws Exception {
//        byte[] encoded = hexToBytes(hex);
//        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, "BC");
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
//        return keyFactory.generatePublic(keySpec);
//    }
//
//    public PrivateKey hexToPrivateKey(String hex) throws Exception {
//        byte[] encoded = hexToBytes(hex);
//        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, "BC");
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
//        return keyFactory.generatePrivate(keySpec);
//    }
//
//    public boolean verifySignature(String publicKeyHex, String dataToVerify, String signatureR, String signatureS) {
//        try {
//            // Parse the public key
//            PublicKey publicKey = hexToPublicKey(publicKeyHex);
//
//            // Reconstruct the signature from r and s components
//            BigInteger r = new BigInteger(signatureR, 16);
//            BigInteger s = new BigInteger(signatureS, 16);
//
//            // Create ECDSA signature
//            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM, "BC");
//            signature.initVerify(publicKey);
//            signature.update(dataToVerify.getBytes());
//
//            // Convert r,s to DER format for verification
//            byte[] derSignature = encodeSignatureToDER(r, s);
//
//            return signature.verify(derSignature);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    private byte[] encodeSignatureToDER(BigInteger r, BigInteger s) {
//        // Encode r and s as ASN.1 DER sequence
//        byte[] rBytes = r.toByteArray();
//        byte[] sBytes = s.toByteArray();
//
//        // Remove leading zero if positive and high bit set
//        if (rBytes[0] == 0 && (rBytes[1] & 0x80) != 0) {
//            byte[] tmp = new byte[rBytes.length - 1];
//            System.arraycopy(rBytes, 1, tmp, 0, tmp.length);
//            rBytes = tmp;
//        }
//        if (sBytes[0] == 0 && (sBytes[1] & 0x80) != 0) {
//            byte[] tmp = new byte[sBytes.length - 1];
//            System.arraycopy(sBytes, 1, tmp, 0, tmp.length);
//            sBytes = tmp;
//        }
//
//        int rLen = rBytes.length;
//        int sLen = sBytes.length;
//        int seqLen = 2 + rLen + 2 + sLen;
//        int totalLen = 2 + seqLen;
//
//        byte[] der = new byte[totalLen];
//        int idx = 0;
//        der[idx++] = 0x30; // SEQUENCE
//        der[idx++] = (byte) seqLen;
//        der[idx++] = 0x02; // INTEGER
//        der[idx++] = (byte) rLen;
//        System.arraycopy(rBytes, 0, der, idx, rLen);
//        idx += rLen;
//        der[idx++] = 0x02; // INTEGER
//        der[idx++] = (byte) sLen;
//        System.arraycopy(sBytes, 0, der, idx, sLen);
//
//        return der;
//    }
//
//    private String bytesToHex(byte[] bytes) {
//        StringBuilder sb = new StringBuilder();
//        for (byte b : bytes) {
//            sb.append(String.format("%02x", b));
//        }
//        return sb.toString();
//    }
//
//    private byte[] hexToBytes(String hex) {
//        int len = hex.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
//                    + Character.digit(hex.charAt(i + 1), 16));
//        }
//        return data;
//    }
//
//    public String getCanonicalData(String senderAccountId, String recipientAccountNumber, 
//                                    java.math.BigDecimal amount, String timestamp, String nonce) {
//        return senderAccountId + "|" + recipientAccountNumber + "|" + amount.toPlainString() + "|" + timestamp + "|" + nonce;
//    }
}
