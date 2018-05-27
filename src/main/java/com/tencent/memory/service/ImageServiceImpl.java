package com.tencent.memory.service;

import com.tencent.memory.dao.ImageDao;
import com.tencent.memory.dao.ImageMapper;
import com.tencent.memory.model.Image;
import com.tencent.memory.model.ImageGroup;
import com.tencent.memory.model.Order;
import com.tencent.memory.util.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageMapper imageMapper;
    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(ImageMapper imageMapper, ImageDao imageDao) {
        this.imageMapper = imageMapper;
        this.imageDao = imageDao;
    }

    @Override
    public Image findById(long imageId) {
        // TODO comments
        return imageMapper.findById(imageId);
    }

    @Override
    public boolean isExist(long imageId) {
        return imageMapper.isExist(imageId) == 1;
    }

    @Override
    public List<ImageGroup> getImageGroupsFromGallery(long galleryId, Paging paging) {
        List<Image> images = imageMapper.getGroupsByGallery(galleryId, paging.start, paging.size, Order.DESC.value);
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

        return imageGroups;
    }

    @Override
    public List<ImageGroup> getNewsImageGroups(long uid, Paging paging) {
        List<Image> images = imageMapper.getGroupsAll(uid, paging.start, paging.size, Order.DESC.value);
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

        return imageGroups;
    }


    // TODO checkPermission
    @Override
    public int addImagesToGallery(long galleryId, long uid, Image[] images, String description) {
        return imageDao.addImagesToGallery(galleryId, uid, description, images);
    }

    // TODO checkPermission
    @Override
    public int delImageFromGallery(long galleryId, long uid, long id) {
        return imageMapper.delete(id);
    }

    @Override
    public int delImageGroup(long galleryId, long uid, long groupId) {
        return 0;
    }

}
