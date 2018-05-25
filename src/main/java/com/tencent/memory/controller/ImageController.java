package com.tencent.memory.controller;

import com.tencent.memory.model.Gallery;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  处理相册里面的
 *  图片的增删改查
 */
@RestController
public class ImageController {

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
