package com.tencent.memory.service;


import com.tencent.memory.dao.GalleryMapper;
import com.tencent.memory.dao.ImageMapper;
import com.tencent.memory.dao.UserGalleryMapper;
import com.tencent.memory.model.Gallery;
import com.tencent.memory.model.Image;
import com.tencent.memory.model.ImageGroup;
import com.tencent.memory.model.Order;
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


    @Autowired
    public GalleryServiceImpl(GalleryMapper galleryMapper,
                              UserGalleryMapper userGalleryMapper,
                              ImageMapper imageMapper) {
        this.galleryMapper = galleryMapper;
        this.userGalleryMapper = userGalleryMapper;
        this.imageMapper = imageMapper;
    }

    // 数据库不分页
    // 代码分页
    @Override
    public Gallery loadGallery(long galleryId) {
        Gallery gallery = galleryMapper.findById(galleryId);
        if (gallery != null) {
            List<Image> images = imageMapper.getAllByGallery(galleryId, Order.DESC.value);
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
            gallery.images = imageGroups;
        }

        return gallery;
    }

    @Override
    public List<Gallery> loadMyGalleries(long uid, Paging paging, Order order) {
        List<Gallery> galleries = galleryMapper.getAllByCreated(uid, paging.start, paging.size, order.value);

        return galleries;
    }

    @Override
    public List<Gallery> searchMyGalleries(long uid, String query, Paging paging, Order order) {
        return galleryMapper.searchByName(uid, query, paging.start, paging.size, order.value);
    }

    @Override
    public int createGallery(Gallery gallery) {
        return galleryMapper.insert(gallery);
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
