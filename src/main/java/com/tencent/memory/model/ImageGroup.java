package com.tencent.memory.model;

import java.util.ArrayList;
import java.util.List;

public class ImageGroup {
    public long id;
    public long galleryId;
    public String description;
    public User creater;
    public List<Image> images;

    public void addImage(Image image) {
        if (images == null) {
            images = new ArrayList<>();
        }

        images.add(image);
    }
}
