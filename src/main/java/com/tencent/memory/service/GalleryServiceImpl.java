package com.tencent.memory.service;


import com.tencent.memory.dao.GalleryMapper;
import com.tencent.memory.dao.ImageMapper;
import com.tencent.memory.dao.UserGalleryMapper;
import com.tencent.memory.dao.UserMapper;
import com.tencent.memory.model.*;
import com.tencent.memory.util.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GalleryServiceImpl implements GalleryService {

    private final GalleryMapper galleryMapper;
    private final UserGalleryMapper userGalleryMapper;
    private final ImageMapper imageMapper;
    private final UserMapper userMapper;

    @Autowired
    public GalleryServiceImpl(GalleryMapper galleryMapper,
                              UserGalleryMapper userGalleryMapper,
                              ImageMapper imageMapper,
                              UserMapper userMapper) {
        this.galleryMapper = galleryMapper;
        this.userGalleryMapper = userGalleryMapper;
        this.imageMapper = imageMapper;
        this.userMapper = userMapper;
    }

    // 创建相册
    // 1.插入 user_gallery
    @Override
    public int createGallery(Gallery gallery) {
        int i = galleryMapper.insert(gallery);
        if (i > 0) {
            // 把创建者插入到user_gallery
            userGalleryMapper.addGallery(gallery.creater.id, gallery.id);
        }
        return i;
    }

    @Override
    public int updateGallery(Gallery gallery) {

        return galleryMapper.update(gallery);
    }

    @Override
    public List<User> loadMembers(long galleryId) {
        return userMapper.getGalleryAll(galleryId);
    }

    // 数据库分页
    // 代码分组
    @Override
    public Gallery loadGallery(long galleryId, long uid, Paging paging) {
        Gallery gallery = galleryMapper.findByIdWithExited(galleryId, uid);
        if (gallery != null) {
            List<Image> images = imageMapper.getGroupsByGallery(galleryId, uid, paging.start, paging.size, Order.DESC.value);
            //List<Image> images = imageMapper.getAllByGallery(galleryId, Order.DESC.value);
            //代码分组
            List<ImageGroup> imageGroups = new ArrayList<>();
            for (Image image : images) {
                if (imageGroups.size() > 0 && imageGroups.get(imageGroups.size() - 1).id == image.groupId) {
                    imageGroups.get(imageGroups.size() - 1).addImage(image);
                } else {
                    ImageGroup g = new ImageGroup();
                    g.addImage(image);
                    g.creater = image.creater;
                    imageGroups.add(g);
                }
            }
            gallery.groups = imageGroups;
        }

        return gallery;
    }

    @Override
    public Gallery loadGalleryWithoutImage(long galleryId, long uid) {
        Gallery gallery;
        if (uid <= 0) {
            gallery = galleryMapper.findById(galleryId);
        } else {
            gallery = galleryMapper.findByIdWithExited(galleryId, uid);
        }
        return gallery;
    }

    @Override
    public List<Gallery> loadMyGalleries(long uid, Paging paging, Order order) {
        return galleryMapper.getAllByCreated(uid, paging.start, paging.size, order.value);
    }

    @Override
    public List<Gallery> searchMyGalleries(long uid, String query, Paging paging, Order order) {
        return galleryMapper.searchByName(uid, query, paging.start, paging.size, order.value);
    }

    @Override
    public int removeGallery(boolean keep, long uid, long galleryId) {
        if (keep) {
            return userGalleryMapper.setGalleryDel(uid, galleryId);
        } else {
            return userGalleryMapper.removeGallery(uid, galleryId);
        }

    }

    @Override
    public int addGallery(long uid, long galleryId) {
        return userGalleryMapper.addGallery(uid, galleryId);
    }

}
