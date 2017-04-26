package com.example.ontidz.jigsaw.common;

import java.util.HashMap;

/**
 * Created by ontidz on 2017/4/12.
 */

public class Login {

    private static boolean isLogin = false;

    private static String userName;

//    private static HashMap<String, String> grade = new HashMap<>();
//
//    public static void setGrade(HashMap<String, String> grade) {
//        Login.grade = grade;
//    }
//
//    public static HashMap<String, String> getGrade() {
//        return grade;
//    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        Login.userName = userName;
    }


    public static boolean isLogin() {
        return isLogin;
    }

    public static void setIsLogin(boolean isLogin) {
        Login.isLogin = isLogin;
    }
}
