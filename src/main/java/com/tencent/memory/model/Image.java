package com.tencent.memory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tencent.memory.config.Config;
import com.tencent.memory.config.JsonDateSerializer;
import com.tencent.memory.util.TextUtils;

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

    @JsonSerialize(using = JsonDateSerializer.class)
    public LocalDateTime created;

    public Image() {
    }

    public String getUrl() {
        if (TextUtils.isEmpty(url)) return null;
        return Config.bucketPathCdnPrefix + url;
    }
}
