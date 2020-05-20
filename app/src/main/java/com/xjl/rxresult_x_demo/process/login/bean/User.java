package com.xjl.rxresult_x_demo.process.login.bean;

import java.io.Serializable;

public class User implements Serializable {

    public String id;
    public String nickName;
    public String phone;

    public String address;
    public int age;
    public boolean hasAuth=false;

    public User(String id, String nickName, String phone) {
        this.id = id;
        this.nickName = nickName;
        this.phone = phone;
    }

    public User(String id, String nickName, String phone, String address, int age, boolean hasAuth) {
        this.id = id;
        this.nickName = nickName;
        this.phone = phone;
        this.address = address;
        this.age = age;
        this.hasAuth = hasAuth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
