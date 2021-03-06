package com.tencent.memory.controller;

import com.tencent.memory.api.ApiResult;
import com.tencent.memory.api.ApiResultBuilder;
import com.tencent.memory.config.Attrs;
import com.tencent.memory.model.*;
import com.tencent.memory.service.CommentService;
import com.tencent.memory.service.ImageService;
import com.tencent.memory.service.MessageService;
import com.tencent.memory.util.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final ImageService imageService;
    private final CommentService commentService;
    private final MessageService messageService;

    @Autowired
    public CommentController(ImageService imageService,
                             CommentService commentService,
                             MessageService messageService) {
        this.imageService = imageService;
        this.commentService = commentService;
        this.messageService = messageService;
    }

    // 查询评论列表
    @GetMapping("/images/{imageId}/comments/")
    public ApiResult<List<Comment>> getsComments(HttpServletRequest req,
                                                 @PathVariable("imageId") long imageId) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        Paging paging = (Paging) req.getAttribute(Attrs.paging);
        List<Comment> comments = commentService.getComments(imageId, paging, Order.ASC);
        return new ApiResultBuilder<List<Comment>>().success(comments).build();
    }

    // 提交评论
    @PostMapping("/images/{imageId}/comments/")
    public ApiResult<Comment> addComment(HttpServletRequest req,
                                         @PathVariable("imageId") long imageId,
                                         @RequestParam("content") String content) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        boolean exist = imageService.isExist(imageId);
        if (!exist) {
            return new ApiResultBuilder<Comment>().error(HttpStatus.BAD_REQUEST.value(),
                    "图片不存在:" + imageId).build();
        }

        Comment comment = new Comment();
        comment.creater = new User();
        comment.creater.id = uid;

        comment.content = content;
        comment.imageId = imageId;
        comment.created = LocalDateTime.now();

        int i = commentService.addComment(comment);
        if (i > 0) {
            logger.debug("add comment success {} add message to user", comment.id);
            Message message = new Message();
            message.content = comment.content;
            message.type = 1;
            message.creater = comment.creater;
            message.imageId = comment.imageId;

            messageService.addMessage(message);
            logger.debug("add message success id:{}", message.id);
        }

        return new ApiResultBuilder<Comment>().success(comment).build();
    }

    // 删除评论
    // 只有评论人才能删除自己的评论
    @DeleteMapping("/comments/{commentId}")
    public ApiResult<Integer> deleteComment(HttpServletRequest req,
                                            @PathVariable("commentId") long commentId) {

        Long uid = (Long) req.getAttribute(Attrs.uid);

        Comment comment = commentService.findById(commentId);
        if (comment == null) {
            return new ApiResultBuilder<Integer>().error(HttpStatus.BAD_REQUEST.value(),
                    "评论不存在:" + commentId).build();
        }

        if (comment.creater.id != uid) {
            return new ApiResultBuilder<Integer>().error(HttpStatus.FORBIDDEN.value(),
                    "你不能删除别人的评论").build();
        }

        int i = commentService.delComment(uid, commentId);
        return new ApiResultBuilder<Integer>().success(i).build();
    }
}
