package com.tencent.memory.controller;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.api.ApiResult;
import com.tencent.memory.api.ApiResultBuilder;
import com.tencent.memory.model.Gallery;
import com.tencent.memory.model.Order;
import com.tencent.memory.service.GalleryService;
import com.tencent.memory.util.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
