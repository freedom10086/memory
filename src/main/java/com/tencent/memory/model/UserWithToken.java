package com.tencent.memory.model;

public class UserWithToken extends User {
    public String token;

    public UserWithToken(User user) {
        this.id = user.id;
        this.openId = user.openId;
        this.name = user.name;
        this.avatar = user.avatar;
        this.gender = user.gender;
        this.created = user.created;
    }

}
