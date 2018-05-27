package com.tencent.memory.controller;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.model.ApiResult;
import com.tencent.memory.model.ApiResultBuilder;
import com.tencent.memory.model.Gallery;
import com.tencent.memory.model.Order;
import com.tencent.memory.service.GalleryService;
import com.tencent.memory.util.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 处理相册Controller
 * 对相册的增删该查
 */
@RestController
public class GalleriesController {

    private final GalleryService galleryService;

    @Autowired
    public GalleriesController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    // 获取我的相册列表
    @GetMapping("/galleries/")
    public ApiResult<List<Gallery>> getGalleries(HttpServletRequest req) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        Paging paging = (Paging) req.getAttribute(Attrs.paging);
        List<Gallery> galleries = galleryService.loadMyGalleries(uid, paging, Order.DESC);
        return new ApiResultBuilder<List<Gallery>>().success(galleries).build();
    }

    //搜索我的相册
    // 获取我的相册列表
    @GetMapping("/search/galleries/")
    public ApiResult<List<Gallery>> getGalleries(HttpServletRequest req,
                                                 @RequestParam("query") String query) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        Paging paging = (Paging) req.getAttribute(Attrs.paging);
        List<Gallery> galleries = galleryService.searchMyGalleries(uid, query, paging, Order.DESC);
        return new ApiResultBuilder<List<Gallery>>().success(galleries).build();
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
        gallery.creater.id = uid;
        gallery.type = type;
        galleryService.createGallery(gallery);

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


    // 接受邀请添加相册
    @PostMapping(value = "/galleries/join" ,produces = "application/json")
    public ApiResult<Gallery> addGallery(HttpServletRequest req,
                                         @RequestParam("invitecode") String inviteCode) {
        // TODO

        return null;
    }


    // 接受邀请添加相册
    @GetMapping("/galleries/join")
    public ApiResult<Gallery> addGallery1(HttpServletRequest req,
                                         @RequestParam("invitecode") String inviteCode) {
        // TODO

        return null;
    }

    // 获取指定相册
    @GetMapping("/galleries/{galleryId}")
    public Gallery getGallery(@PathVariable("galleryId") long galleryId) {

        return null;
    }
}
