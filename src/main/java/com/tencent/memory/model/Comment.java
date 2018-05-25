package com.tencent.memory.model;

/**
 * 对某个相片的评论
 */
public class Comment {
    public long id;
    public long imageId;
    public String content;
    public User creater;
    public String created;

    public Comment() {
    }
}
