package com.tencent.memory.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tencent.memory.config.JsonDateSerializer;

import java.time.LocalDateTime;

/**
 * 对某个相片的评论
 */
public class Comment {
    public long id;
    public long imageId;
    public String content;
    public User creater;

    @JsonSerialize(using = JsonDateSerializer.class)
    public LocalDateTime created;

    public Comment() {

    }
}
