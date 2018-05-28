package com.tencent.memory.controller;


import com.tencent.memory.config.Config;
import com.tencent.memory.model.InviteCode;
import com.tencent.memory.util.Sign;
import com.tencent.memory.util.Token;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @ResponseBody
    @RequestMapping("/")
    public String hello() {
        return "hello";
    }

    @ResponseBody
    @RequestMapping(value = "/auth_code", produces = "application/json")
    public String authCode() throws Exception {
        return Sign.appSign(Config.appId, Config.secretId, Config.secretKey, Config.bucketName, Config.DAY);
    }

    @ResponseBody
    @RequestMapping(value = "/gen_token")
    public String token() {
        Token token = new Token(1, Token.day);
        return token.genToken(Config.tokenSecretKey);
    }

    @ResponseBody
    @RequestMapping("/invite_code")
    public String inviteCode() {
        InviteCode inviteCode = new InviteCode(1, 1, Token.day * 3);
        return inviteCode.genInviteCode(Config.inviteSecreateKey);
    }
}
