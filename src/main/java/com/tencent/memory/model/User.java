package com.tencent.memory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public class User {

    public long id;
    @JsonIgnore
    public String openId;
    public String name;
    public String avatar;
    public String gender;

    public LocalDateTime created;


    public User() {
        created = LocalDateTime.now();
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
