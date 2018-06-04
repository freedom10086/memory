package com.tencent.memory.controller;


import com.tencent.memory.config.Attrs;
import com.tencent.memory.api.ApiResult;
import com.tencent.memory.api.ApiResultBuilder;
import com.tencent.memory.service.UserService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/users/")
    public ApiResult<Integer> updateUserInfo(
            ServletRequest req, @RequestParam("name") String name) {

        Long uid = (Long) req.getAttribute(Attrs.uid);

        int i = userService.updateUserInfo(uid,name);

        return new ApiResultBuilder<Integer>().success(i).build();
    }
}
