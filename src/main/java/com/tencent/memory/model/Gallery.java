package com.tencent.memory.model;

import com.tencent.memory.config.Config;

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

    public int images; //图片数
    public int users; //用户数

    // 查询Gallery此字段为空
    public List<ImageGroup> groups;

    public LocalDateTime created;
    public LocalDateTime updated;

    public String getCover() {
        return Config.bucketPathCdnPrefix + cover;
    }

    public Gallery() {

    }
}
