package com.tencent.memory.controller;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.model.ApiResult;
import com.tencent.memory.model.Comment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CommentController {

    @GetMapping("/comments/")
    public ApiResult<Comment> getsComments(HttpServletRequest req,
                                           @RequestParam("imageId") long imageId) {
        Long uid = (Long) req.getAttribute(Attrs.uid);

        return null;
    }

    @PostMapping("/comments/")
    public ApiResult<Comment> addComment(HttpServletRequest req) {
        Long uid = (Long) req.getAttribute(Attrs.uid);

        return null;
    }
}
