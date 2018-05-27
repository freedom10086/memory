package com.tencent.memory.dao;

import com.tencent.memory.model.Image;
import com.tencent.memory.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ImageGroup {
    public long id;
    public long galleryId;
    public String description;
    public User creater;
    public int size;
    public LocalDateTime created;

    public List<Image> images;
}
