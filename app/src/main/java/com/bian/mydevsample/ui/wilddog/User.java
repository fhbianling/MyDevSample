package com.bian.mydevsample.ui.wilddog;

/**
 * author 边凌
 * date 2017/12/13 14:34
 * 类描述：
 */

public class User {
    public static final int GIRL = 2;
    public static final int BOY = 1;
    private int sex;
    private String name;
    private long userId;

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String sex(){
        return sex==BOY?"男":"女";
    }

    @Override
    public String toString() {
        return "User{" +
                "sex=" + sex +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                '}';
    }
}
