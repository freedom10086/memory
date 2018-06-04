package com.tencent.memory.controller;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.api.ApiResult;
import com.tencent.memory.api.ApiResultBuilder;
import com.tencent.memory.model.Message;
import com.tencent.memory.model.User;
import com.tencent.memory.service.ImageService;
import com.tencent.memory.service.LikeService;
import com.tencent.memory.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LikeController {

    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    private final LikeService likeService;
    private final ImageService imageService;
    private final MessageService messageService;

    @Autowired
    public LikeController(LikeService likeService,
                          ImageService imageService,
                          MessageService messageService) {
        this.likeService = likeService;
        this.imageService = imageService;
        this.messageService = messageService;
    }

    // 用户点击了喜欢
    // 注意 无法取消
    @PostMapping("/likes/{imageId}")
    public ApiResult<Long> tapLike(HttpServletRequest req,
                                   @PathVariable("imageId") long imageId) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        boolean exist = imageService.isExist(imageId);
        if (!exist) {
            return new ApiResultBuilder<Long>().error(HttpStatus.BAD_REQUEST.value(),
                    "图片不存在:" + imageId).build();
        }

        long count = likeService.dolike(imageId, uid);
        logger.debug("like success add message to user ");

        Message message = new Message();
        message.content = "";
        message.type = 0;
        message.creater = new User();
        message.creater.id = uid;
        message.imageId = imageId;
        messageService.addMessage(message);
        logger.debug("add message success id:{}", message.id);

        return new ApiResultBuilder<Long>().success(count).build();
    }

    @GetMapping("/likes/{imageId}")
    public ApiResult<Long> LikesCount(@PathVariable("imageId") long imageId) {
        long count = likeService.likesCount(imageId);
        return new ApiResultBuilder<Long>().success(count).build();
    }
}
