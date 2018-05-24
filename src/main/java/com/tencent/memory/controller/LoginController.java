package com.tencent.memory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.memory.config.Config;
import com.tencent.memory.db.UserDao;
import com.tencent.memory.db.UserMapper;
import com.tencent.memory.model.*;
import com.tencent.memory.util.Token;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class LoginController {

    private final UserMapper userMapper;

    private final UserDao userDao;

    @Autowired
    public LoginController(UserMapper userMapper, UserDao userDao) {
        this.userMapper = userMapper;
        this.userDao = userDao;
    }

    //用一个map维护在线的用户 不行分布式
    //用redis 维护 ？复杂
    //or token ？简单

    private static final Map<String, Call> requests = new ConcurrentHashMap<>();

    @PostMapping("/login")
    @ResponseBody
    public ApiResult<User> login(@RequestParam("openid") String openid,
                                 @RequestParam("access_token") String accessToken) {

        User user;
        try {
            user = userMapper.findByOpenId(openid);
        } catch (EmptyResultDataAccessException e) {
            //用户不存在创建用户
            QQUserInfo info = getUserInfo(openid, accessToken);

            user = new User();
            user.name = info.nickname;
            user.avatar = info.figureurl_qq_2;
            user.gender = info.gender;
            user.openId = openid;

            userMapper.insert(user);
        }


        Token token = new Token(user.id, Config.DAY);
        user.token = token.genToken(Config.tokenSecretKey);
        return new ApiResultBuilder<User>().success(user).build();
    }

    @ExceptionHandler(MyException.class)
    public ResponseEntity<Object> handleException(MyException ex) {
        ApiResult<String> result = new ApiResultBuilder<String>().error(ex.status.value(), ex.message).build();
        return new ResponseEntity<>(result, ex.status);
    }

    // 查询qq开放接口获取用户信息
    public QQUserInfo getUserInfo(String openId, String accessToken) {
        if (requests.containsKey(openId)) {
            requests.get(openId).cancel();
        }

        OkHttpClient client = new OkHttpClient();
        String url = String.format("https://graph.qq.com/user/get_simple_userinfo?" +
                        "access_token=%s&" +
                        "oauth_consumer_key=%s&" +
                        "openid=%s&format=json",
                accessToken, Config.qqAndroidAppId, openId);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        requests.put(openId, call);

        Response response;
        IOException e = null;
        try {
            response = call.execute();
            if (response.isSuccessful()) {
                QQUserInfo userInfo = new ObjectMapper().readValue(response.body().bytes(), QQUserInfo.class);
                if (userInfo.ret == 0) {
                    return userInfo;
                } else {
                    throw new MyException(HttpStatus.UNAUTHORIZED, userInfo.msg);
                }
            }
        } catch (IOException e1) {
            e = e1;
        } finally {
            requests.remove(openId);
        }

        throw new MyException(HttpStatus.UNAUTHORIZED, e == null ? "request failed please try again" : e.getMessage());
    }
}
