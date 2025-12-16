package com.ucop.util;

import java.security.MessageDigest;

public class HashUtil {

    // Use MD5 for password hashing (matches database passwords)
    public static String md5(String input){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for(byte b : hash){
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // Keep SHA256 for backward compatibility
    public static String sha256(String input){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for(byte b : hash){
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
