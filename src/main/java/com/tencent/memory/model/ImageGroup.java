package com.tencent.memory.model;

import java.time.LocalDateTime;
import java.util.List;

public class ImageGroup {
    public long id;
    public long galleryId;
    public String description;
    public User creater;
    public int size;
    List<Image> images;
    public LocalDateTime created;
}
