package com.tencent.memory.model;

import java.time.LocalDateTime;

/**
 * 对某个相片的评论
 */
public class Comment {
    public long id;
    public long imageId;
    public String content;
    public User creater;
    public LocalDateTime created;

    public Comment() {

    }
}
