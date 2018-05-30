package com.tencent.memory.controller;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.model.ApiResult;
import com.tencent.memory.model.ApiResultBuilder;
import com.tencent.memory.model.Gallery;
import com.tencent.memory.model.User;
import com.tencent.memory.service.GalleryService;
import com.tencent.memory.util.Paging;
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
        gallery.description = description;
        gallery.creater = new User();
        gallery.creater.id = uid;
        gallery.type = type;
        gallery.created = LocalDateTime.now();
        galleryService.createGallery(gallery);
        gallery.creater = null;

        return new ApiResultBuilder<Gallery>().success(gallery).build();
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
        Paging paging = (Paging) req.getAttribute(Attrs.paging);
        Gallery gallery = galleryService.loadGallery(galleryId, paging);
        if (gallery == null) {
            return new ApiResultBuilder<Gallery>()
                    .error(HttpStatus.NOT_MODIFIED.value(), "相册不存在:" + galleryId).build();
        } else {
            return new ApiResultBuilder<Gallery>()
                    .success(gallery).build();
        }
    }
}
