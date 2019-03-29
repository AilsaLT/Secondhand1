package com.ghl.wuhan.secondhand.me_activity;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by chenmin on 2018/10/23.
 */

public class UserBO implements Serializable {
    private int opType;//操作类型
    private String uid;
    private String uname;
    private String upassword;
    private byte[] uimage;
    private int sex;
    private String uphone;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getUphone() {
        return uphone;
    }

    public void setUphone(String uphone) {
        this.uphone = uphone;
    }

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpassword() {
        return upassword;
    }

    public void setUpassword(String upassword) {
        this.upassword = upassword;
    }

    public byte[] getUimage() {
        return uimage;
    }

    public void setUimage(byte[] uimage) {
        this.uimage = uimage;
    }


    @Override
    public String toString() {
        return "UserBO{" +
                "opType=" + opType +
                ", uid='" + uid + '\'' +
                ", uname='" + uname + '\'' +
                ", upassword='" + upassword + '\'' +
                ", uimage=" + Arrays.toString(uimage) +
                ", sex=" + sex +
                ", uphone='" + uphone + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
