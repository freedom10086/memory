package com.tencent.memory.service;

import com.tencent.memory.model.Image;

import java.util.List;

public interface ImageService {

    Image findById(long imageId);


    // 创建group 再把这些图片插入
    int addImagesToGallery(long galleryId, long uid, Image[] images, String description);

    // 删除图片
    int delImageFromGallery(long galleryId, long uid, long id);

    // 删除图片组
    int delImageGroup(long galleryId, long uid, long groupId);
}
