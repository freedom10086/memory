package com.tencent.memory.service;

import com.tencent.memory.model.Gallery;
import com.tencent.memory.model.GalleryUsersAndCode;
import com.tencent.memory.model.Order;
import com.tencent.memory.model.User;
import com.tencent.memory.util.Paging;

import java.util.List;

public interface GalleryService {

    //imageSize 图片数目
    Gallery loadGallery(long galleryId, long uid, Paging paging);

    Gallery loadGalleryWithoutImage(long galleryId, long uid);

    List<Gallery> loadMyGalleries(long uid, Paging paging, Order order);

    List<Gallery> searchMyGalleries(long uid, String query, Paging paging, Order order);

    int createGallery(Gallery gallery);

    int updateGallery(Gallery gallery);

    List<User> loadMembers(long galleryId);

    // 移除非删除
    int removeGallery(boolean keep, long uid, long galleryId);

    // 添加非新建
    int addGallery(long uid, long galleryId);
}
