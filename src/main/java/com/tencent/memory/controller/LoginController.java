package com.tencent.memory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.memory.config.Config;
import com.tencent.memory.dao.UserMapper;
import com.tencent.memory.model.*;
import com.tencent.memory.util.Token;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final UserMapper userMapper;

    @Autowired
    public LoginController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    private static final Map<String, Call> requests = new ConcurrentHashMap<>();

    @PostMapping("/login")
    @ResponseBody
    public ApiResult<User> login(@RequestParam("openid") String openid,
                                 @RequestParam("access_token") String accessToken) {
        User user = userMapper.findByOpenId(openid);
        if (user == null) {
            //用户不存在创建用户
            QQUserInfo info = getUserInfo(openid, accessToken);
            user = new User();
            user.name = info.nickname;
            user.avatar = info.figureurl_qq_2;
            user.gender = info.gender;
            user.openId = openid;

            logger.info("create user openid: {}", openid);

            userMapper.insert(user);
        }

        UserWithToken result = new UserWithToken(user);
        result.token = new Token(user.id, Config.DAY).genToken(Config.tokenSecretKey);

        return new ApiResultBuilder<User>().success(result).build();
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
                    logger.info("load user info:{}", userInfo.toString());
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
