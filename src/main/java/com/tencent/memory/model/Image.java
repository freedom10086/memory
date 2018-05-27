package com.tencent.memory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 相册里面的的单张图片
 */
public class Image {
    public long id;

    @JsonIgnore
    public long groupId;

    public String url;
    public User creater;
    public String description;

    public int likes;
    public int comments;

    public String created;
}
