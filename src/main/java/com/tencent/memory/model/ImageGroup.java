package com.tencent.memory.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tencent.memory.config.JsonDateSerializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ImageGroup {
    public long id;
    public long galleryId;
    public String description;
    public User creater;
    public List<Image> images;

    @JsonSerialize(using = JsonDateSerializer.class)
    public LocalDateTime created;

    public ImageGroup() {
    }

    public void addImage(Image image) {
        if (images == null) {
            images = new ArrayList<>();
        }

        images.add(image);
    }
}
