package com.tencent.memory.controller;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.model.ApiResult;
import com.tencent.memory.model.ApiResultBuilder;
import com.tencent.memory.service.ImageService;
import com.tencent.memory.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LikeController {

    private final LikeService likeService;
    private final ImageService imageService;

    @Autowired
    public LikeController(LikeService likeService, ImageService imageService) {
        this.likeService = likeService;
        this.imageService = imageService;
    }

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
        return new ApiResultBuilder<Long>().success(count).build();
    }

    @GetMapping("/likes/{imageId}")
    public ApiResult<Long> LikesCount(@PathVariable("imageId") long imageId) {
        long count = likeService.likesCount(imageId);
        return new ApiResultBuilder<Long>().success(count).build();
    }
}
