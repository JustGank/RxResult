package com.xjl.rxresult_x_demo.process.login.bean;

public class LoginInfo {

    public static User currentLoginUser = null;

    public static boolean isLogin() {
        return currentLoginUser != null;
    }

}
