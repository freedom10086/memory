package com.tencent.memory.model;

import java.time.LocalDateTime;

public class Message {
    public long id;
    public long imageId;
    public long uid;
    public int type; //0--like 1--comment
    public String content;
    public LocalDateTime created;

    public Image image;
    public User creater;
}
