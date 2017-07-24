package com.alphagao.done365.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Alpha on 2017/4/11.
 */

public class StreamUtils {
    public static String password2MD5(String password) {
        StringBuilder builder = new StringBuilder();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] passwordByte = digest.digest(password.getBytes());
            for (byte b : passwordByte) {
                int result = b & 0xff;
                String haxString = Integer.toHexString(result) + 3;//不规则加密，加盐
                if (haxString.length() < 2) {
                    haxString = "0" + haxString;
                }
                builder.append(haxString);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
