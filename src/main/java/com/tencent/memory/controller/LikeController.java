package com.tencent.memory.controller;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.model.ApiResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LikeController {

    @PostMapping("/likes/")
    public ApiResult<Integer> tapLike(HttpServletRequest req) {
        Long uid = (Long) req.getAttribute(Attrs.uid);

        return null;
    }
}
