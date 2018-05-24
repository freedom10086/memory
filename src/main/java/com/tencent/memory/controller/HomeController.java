package com.tencent.memory.controller;


import com.tencent.memory.config.Config;
import com.tencent.memory.util.Sign;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
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
    @RequestMapping(value = "/image_valid", produces = "application/json")
    public String imageValid() throws Exception {

        //SimpleUploadFileDemo.SimpleUploadFileFromLocal();

        throw new RuntimeException("test");

        //return "ok";
    }

}
