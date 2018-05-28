package com.tencent.memory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpStatus;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

// 邀请码设计
public class InviteCode {

    // 邀请目标相册
    @JsonProperty("g")
    public long galleryId;
    // 邀请人
    @JsonProperty("u")
    public long uid;

    @JsonProperty("e")
    public long expires;//过期时间  毫秒数

    public InviteCode() {
    }

    public InviteCode(long galleryId, long uid, long expires) {
        this.galleryId = galleryId;
        this.uid = uid;
        this.expires = System.currentTimeMillis() + expires;
    }

    //生成TOKEN base64(data+hmac(data,SecretKey))
    public String genInviteCode(String secretKey) {
        try {
            String json = new ObjectMapper().writeValueAsString(this);
            SecretKey secret = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secret);
            byte[] hmacData = mac.doFinal(json.getBytes());
            //byte[] hexB = new Hex().encode(hmacData);
            String signature = Hex.encodeHexString(hmacData);
            return Base64.encodeBase64URLSafeString((json + signature).getBytes());
        } catch (NoSuchAlgorithmException | InvalidKeyException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    //验证token 签名是否一致
    public static InviteCode decode(String token, String secretKey) throws IOException {
        String data = valid(token, secretKey);
        return new ObjectMapper().readValue(data, InviteCode.class);
    }

    protected static String valid(String inviteCode, String secretKey) {
        if (inviteCode == null || inviteCode.length() < 32) throw new MyException(HttpStatus.BAD_REQUEST, "邀请码不合法");
        byte[] datas = Base64.decodeBase64(inviteCode);
        //split request into signature and data
        if (datas.length < 64) throw new MyException(HttpStatus.BAD_REQUEST, "邀请码不合法");

        String data = null;
        try {
            data = new String(datas, 0, datas.length - 64);
            String signature = new String(datas, datas.length - 64, 64);

            //再进行一次签名匹配数据
            SecretKey secret = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secret);
            byte[] hmacData = mac.doFinal(data.getBytes());
            //byte[] hexB = new Hex().encode(hmacData);
            String signature2 = Hex.encodeHexString(hmacData);
            if (!Objects.equals(signature2, signature)) { //签名不品牌
                throw new MyException(HttpStatus.UNAUTHORIZED, "签名不相等 邀请码无效");
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new MyException(e);
        }

        return data;
    }

    @JsonIgnore
    public boolean isExpired() {
        return expires < System.currentTimeMillis();
    }
}
