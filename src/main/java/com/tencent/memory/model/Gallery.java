package com.tencent.memory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tencent.memory.config.Config;
import com.tencent.memory.config.JsonDateSerializer;
import com.tencent.memory.util.TextUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    @JsonSerialize(using = JsonDateSerializer.class)
    public LocalDateTime created;

    @JsonSerialize(using = JsonDateSerializer.class)
    public LocalDateTime updated;

    @JsonIgnore
    public String exited;

    //是否已经退出
    public boolean isExit() {
        if (exited == null) return false;
        return !Objects.equals(exited, "2038-01-19 03:14:07");
    }

    public String getCover() {
        if (cover == null) return null;
        if (TextUtils.isEmpty(cover)) return null;
        return Config.bucketPathCdnPrefix + cover;
    }

    public Gallery() {
    }
}
