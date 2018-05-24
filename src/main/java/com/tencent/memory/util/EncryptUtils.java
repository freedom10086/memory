package com.tencent.memory.util;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptUtils {
    public static String encodeBase64(String str) {
        // 对字节数组Base64编码
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(str.getBytes());
    }


    public static String decodeBase64(String str) {
        // 对字节数组Base64编码
        Base64.Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(str));
    }


    public static String encodeMd5(String input) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return Hex.encodeHexString(md5.digest(input.getBytes()));
    }
}
