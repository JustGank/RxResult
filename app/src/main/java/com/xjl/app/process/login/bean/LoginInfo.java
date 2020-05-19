package com.xjl.app.process.login.bean;

public class LoginInfo {

    public static User currentLoginUser = null;

    public static boolean isLogin() {
        return currentLoginUser != null;
    }

}
