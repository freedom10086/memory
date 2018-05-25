package com.tencent.memory.model;

/**
 * 相册里面的的单张图片
 */
public class Image {
    public long id;
    public String url;
    public String created;
    public String description;
    public User creater;


    public int likes;
    public int comments;
}
