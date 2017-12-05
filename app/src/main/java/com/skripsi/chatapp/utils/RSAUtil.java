package com.skripsi.chatapp.utils;

import android.util.Base64;


import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by firma on 03-Dec-17.
 */

public class RSAUtil {
    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public static KeyPair getKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException{
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.genKeyPair();
    }
    public static String decrypt(String message, String key){
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytes = Base64.decode(key, Base64.DEFAULT);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);


            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.decode(message, Base64.DEFAULT)), "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static String encrypt(String message, String key){
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytes = Base64.decode(key, Base64.DEFAULT);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeToString(cipher.doFinal(message.getBytes()), Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
