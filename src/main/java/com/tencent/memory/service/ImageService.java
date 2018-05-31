package com.tencent.memory.service;

import com.tencent.memory.model.Image;
import com.tencent.memory.model.ImageGroup;
import com.tencent.memory.model.UploadRequest;
import com.tencent.memory.util.Paging;

import java.util.List;

public interface ImageService {

    Image findById(long imageId);

    boolean isExist(long imageId);

    List<ImageGroup> getImageGroupsFromGallery(long galleryId, Paging paging);

    List<ImageGroup> getNewsImageGroups(long uid, Paging paging);

    List<Image> getAllByGallery(long galleryId, Paging paging);

    // 创建group 再把这些图片插入
    int addImagesToGallery(long galleryId, long uid, UploadRequest[] images, String description);

    // 删除图片
    int delImageFromGallery(long galleryId, long uid, long id);

    // 删除图片组
    int delImageGroup(long galleryId, long uid, long groupId);
}
