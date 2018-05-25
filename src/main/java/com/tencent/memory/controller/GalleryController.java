package com.tencent.memory.controller;

import com.tencent.memory.model.Gallery;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  处理相册Controller
 *  对相册的增删该查
 */
@RestController
public class GalleryController {

    // 获取我的相册列表
    @GetMapping("/galleries")
    public List<Gallery> getGalleries() {
        return null;
    }

    // 获取指定相册
    @GetMapping("galleries/{galleryId}")
    public Gallery getGallery(@PathVariable("galleryId") long galleryId) {

        return null;
    }


    // 新建相册
    @PostMapping("/galleries")
    public Gallery newGallery() {

        return null;
    }

    // 删除相册
    @DeleteMapping("/galleries/{galleryId}")
    public Gallery delGallery(@PathVariable("galleryId") long galleryId) {
        return null;
    }

}
