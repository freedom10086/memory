package com.tencent.memory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tencent.memory.config.Config;

import java.time.LocalDateTime;

/**
 * 相册里面的的单张图片
 */
public class Image {
    public long id;

    public long galleryId;

    @JsonIgnore
    public long groupId;

    public String url;
    public User creater;
    public String description;

    public int likes;
    public int comments;

    public LocalDateTime created;

    public Image() {
        created = LocalDateTime.now();
    }

    public String getUrl() {
        return Config.bucketPathCdnPrefix + url;
    }
}
