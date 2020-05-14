package com.xjl.app.login.login.bean;

public class LoginInfo {

    public static User currentLoginUser = null;

    public static boolean isLogin() {
        return currentLoginUser == null;
    }

}
