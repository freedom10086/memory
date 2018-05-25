package com.tencent.memory.model;

import java.time.LocalDateTime;

public class Gallery {
    public long id;
    public String name;
    public String description;
    public User creater;
    public LocalDateTime created;
    public LocalDateTime updated;

    public Gallery() {
    }
}
