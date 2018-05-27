package com.tencent.memory.model;

public enum Order {

    ASC("asc"),
    DESC("desc");

    Order(String value) {
        this.value = value;
    }

    public String value;
}
