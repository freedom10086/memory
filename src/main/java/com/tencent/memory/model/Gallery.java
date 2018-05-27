package com.tencent.memory.model;

import java.time.LocalDateTime;
import java.util.List;

public class Gallery {
    public long id;
    public String name;
    public String description;
    public int type;

    //封面
    public String cover;
    public User creater;

    List<ImageGroup> images;

    public LocalDateTime created;
    public LocalDateTime updated;

    public Gallery() {

    }
}
