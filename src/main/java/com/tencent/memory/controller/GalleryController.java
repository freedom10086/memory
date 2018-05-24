package com.tencent.memory.controller;

import com.tencent.memory.model.Gallery;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 给相册添加图片
    @PostMapping("/gallerirs/{galleryId}")
    public Gallery addImageToGallery(@PathVariable("galleryId") long galleryId) {
        return null;
    }

    // 从相册删除图片
    @DeleteMapping("/galleries/{galleryId}/{imageId}")
    public Gallery delImageFromGallery(@PathVariable("galleryId") long galleryId,

                                       @PathVariable("imageId") long imageId) {
        return null;

    }




}
