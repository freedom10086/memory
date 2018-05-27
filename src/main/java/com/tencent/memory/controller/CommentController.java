package com.tencent.memory.controller;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.model.ApiResult;
import com.tencent.memory.model.Comment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CommentController {

    // 查询评论列表
    @GetMapping("/images/{imageId}/comments/")
    public ApiResult<Comment> getsComments(HttpServletRequest req,
                                           @PathVariable("imageId") long imageId) {
        Long uid = (Long) req.getAttribute(Attrs.uid);

        return null;
    }

    // 提交评论
    @PostMapping("/images/{imageId}/comments/")
    public ApiResult<Comment> addComment(HttpServletRequest req,
                                         @PathVariable("imageId") long imageId) {
        Long uid = (Long) req.getAttribute(Attrs.uid);

        return null;
    }
}
