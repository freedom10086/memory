package com.tencent.memory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tencent.memory.config.JsonDateSerializer;

import java.time.LocalDateTime;

public class User {

    public long id;
    @JsonIgnore
    public String openId;
    public String name;
    public String avatar;
    public String gender;

    @JsonSerialize(using = JsonDateSerializer.class)
    public LocalDateTime created;


    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", openId='" + openId + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                ", created=" + created +
                '}';
    }
}
