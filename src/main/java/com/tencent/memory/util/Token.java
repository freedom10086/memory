package com.tencent.memory.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.memory.model.MyException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpStatus;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Random;

public class Token {
    public static final long minute = 60 * 1000;
    public static final long hour = 60 * minute;
    public static final long day = hour * 24;

    @JsonProperty("u")
    public long uid; //用户id //当authority = 5时此为项目id
    @JsonProperty("s")
    public String salt; //盐
    @JsonProperty("e")
    public long expires;//过期时间  毫秒数

    private static final String charset = "utf-8";

    public Token() {
    }

    public Token(long uid, long expires) {
        this.uid = uid;
        this.expires = System.currentTimeMillis() + expires;
    }

    //生成TOKEN base64(data+hmac(data,SecretKey))
    public String genToken(String secretKey) {
        salt = rand(3);
        try {
            String json = new ObjectMapper().writeValueAsString(this);
            SecretKey secret = new SecretKeySpec(secretKey.getBytes(charset), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secret);
            byte[] hmacData = mac.doFinal(json.getBytes(charset));
            //byte[] hexB = new Hex().encode(hmacData);
            String signature = Hex.encodeHexString(hmacData);
            return Base64.encodeBase64URLSafeString((json + signature).getBytes(charset));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    //验证token 签名是否一致
    public static Token decodeToken(String token, String secretKey) {
        String data = valid(token, secretKey);
        try {
            return new ObjectMapper().readValue(data, Token.class);
        } catch (IOException e) {
            throw new MyException(e);
        }
    }

    protected static String valid(String token, String secretKey) {
        if (token == null || token.length() < 32) throw new MyException(HttpStatus.BAD_REQUEST, "token不合法");
        byte[] datas = Base64.decodeBase64(token);
        //split request into signature and data
        if (datas.length < 64) throw new MyException(HttpStatus.BAD_REQUEST, "token不合法");

        String data = null;
        try {
            data = new String(datas, 0, datas.length - 64, charset);
            String signature = new String(datas, datas.length - 64, 64, charset);

            //再进行一次签名匹配数据
            SecretKey secret = new SecretKeySpec(secretKey.getBytes(charset), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secret);
            byte[] hmacData = mac.doFinal(data.getBytes(charset));
            //byte[] hexB = new Hex().encode(hmacData);
            String signature2 = Hex.encodeHexString(hmacData);
            if (!Objects.equals(signature2, signature)) { //签名不品牌
                throw new MyException(HttpStatus.UNAUTHORIZED, "签名不相等 token无效");
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            throw new MyException(e);
        }

        return data;
    }

    @JsonIgnore
    public boolean isExpired() {
        return expires < System.currentTimeMillis();
    }


    // 随机字符串
    private static String rand(int len) {
        int[][] kinds = new int[][]{{10, 48}, {26, 97}, {26, 65}};
        Random random = new Random();
        random.setSeed(System.nanoTime());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int j = random.nextInt(3);
            int scope = kinds[j][0];
            int base = kinds[j][1];
            sb.append(base + random.nextInt(scope));
        }
        return sb.toString();
    }
}
