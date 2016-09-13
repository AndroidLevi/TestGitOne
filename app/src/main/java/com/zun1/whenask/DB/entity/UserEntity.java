package com.zun1.whenask.DB.entity;

/**
 * Created by Administrator on 2016/6/27.
 */
public class UserEntity {

    private int id;
    private String userName;
    private String realName;
    private String phone;
    private String password;
    private String email;
    private String address;

    public UserEntity(){}

    public UserEntity(int id,String userName, String realName, String phone, String password, String email, String address) {
        this.id = id;
        this.userName = userName;
        this.realName = realName;
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.address = address;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
