package com.tencent.memory.controller;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.config.Config;
import com.tencent.memory.model.*;
import com.tencent.memory.service.GalleryService;
import com.tencent.memory.util.Paging;
import com.tencent.memory.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
public class GalleryController {

    private final GalleryService galleryService;

    @Autowired
    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    // 新建相册
    @PostMapping("/galleries/")
    public ApiResult<Gallery> newGallery(HttpServletRequest req, @RequestParam("name") String name,
                                         @RequestParam("description") String description,
                                         @RequestParam("type") int type) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        Gallery gallery = new Gallery();
        gallery.name = name;
        if (name.length() > 20) {
            return new ApiResultBuilder<Gallery>()
                    .error(HttpStatus.BAD_REQUEST.value(), "相册名称长度不能超过20")
                    .build();
        }
        gallery.description = description;
        if (description.length() > 100) {
            return new ApiResultBuilder<Gallery>()
                    .error(HttpStatus.BAD_REQUEST.value(), "相册描述长度不能超过100")
                    .build();
        }
        gallery.creater = new User();
        gallery.creater.id = uid;
        gallery.type = type;
        gallery.created = LocalDateTime.now();
        gallery.updated = LocalDateTime.now();
        galleryService.createGallery(gallery);
        gallery.creater = null;

        return new ApiResultBuilder<Gallery>().success(gallery).build();
    }

    @PutMapping("/galleries/{galleryId}")
    public ApiResult<Gallery> editGallery(HttpServletRequest req,
                                          @PathVariable("galleryId") long galleryId,
                                          @RequestParam("name") String name,
                                          @RequestParam("description") String description,
                                          @RequestParam("type") int type) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        Gallery gallery = galleryService.loadGalleryWithoutImage(galleryId, uid);
        if (gallery.creater.id != uid) {
            return new ApiResultBuilder<Gallery>()
                    .error(HttpStatus.FORBIDDEN.value(), "你无权修改不是你创建的相册")
                    .build();
        }
        gallery.name = name;
        gallery.description = description;
        gallery.type = type;

        int i = galleryService.updateGallery(gallery);
        return new ApiResultBuilder<Gallery>().success(gallery).build();
    }


    // 查询相册成员 和 生成邀请码
    @GetMapping("/galleries/{galleryId}/members/")
    public ApiResult<GalleryUsersAndCode> loadCodeAndMembers(HttpServletRequest req,
                                                             @PathVariable("galleryId") long galleryId) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        GalleryUsersAndCode res = new GalleryUsersAndCode();

        Gallery gallery = galleryService.loadGalleryWithoutImage(galleryId, 0);
        if (gallery == null) {
            return new ApiResultBuilder<GalleryUsersAndCode>()
                    .error(HttpStatus.BAD_REQUEST.value(), "相册不存在:" + galleryId).build();
        }

        if (gallery.creater.id == uid) { // can gen invite code
            InviteCode inviteCode = new InviteCode(galleryId, uid, Token.day);
            res.inviteCode = inviteCode.genInviteCode(Config.inviteSecreateKey);
        }

        res.users = galleryService.loadMembers(galleryId);

        return new ApiResultBuilder<GalleryUsersAndCode>().success(res).build();
    }


    // 删除相册
    // keep = true 保留
    // keep = false 删除
    @DeleteMapping("/galleries/{galleryId}")
    public ApiResult<Integer> delGallery(HttpServletRequest req,
                                         @PathVariable("galleryId") long galleryId,
                                         @RequestParam(value = "keep", defaultValue = "true") String keep) {
        boolean keepB = true;
        if ("false".equals(keep)) keepB = false;
        Long uid = (Long) req.getAttribute(Attrs.uid);
        Integer i = galleryService.removeGallery(keepB, uid, galleryId);
        if (i > 0) {
            return new ApiResultBuilder<Integer>().success(i).build();
        } else {
            return new ApiResultBuilder<Integer>()
                    .error(HttpStatus.NOT_MODIFIED.value(), "相册不存在:" + galleryId).build();
        }
    }

    // 获取指定相册
    // 同时返回里面的数据的分页
    @GetMapping("/galleries/{galleryId}")
    public ApiResult<Gallery> getGallery(HttpServletRequest req,
                                         @PathVariable("galleryId") long galleryId) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        Paging paging = (Paging) req.getAttribute(Attrs.paging);
        Gallery gallery = galleryService.loadGallery(galleryId, uid, paging);
        if (gallery == null) {
            return new ApiResultBuilder<Gallery>()
                    .error(HttpStatus.NOT_MODIFIED.value(), "相册不存在:" + galleryId).build();
        } else {
            return new ApiResultBuilder<Gallery>()
                    .success(gallery).build();
        }
    }
}
