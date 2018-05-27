package com.tencent.memory.service;

import com.tencent.memory.dao.ImageDao;
import com.tencent.memory.dao.ImageMapper;
import com.tencent.memory.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
