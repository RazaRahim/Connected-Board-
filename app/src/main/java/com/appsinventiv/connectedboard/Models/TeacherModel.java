package com.appsinventiv.connectedboard.Models;

public class TeacherModel {
    String name,username,phone,password,gender,picUrl,department;

    public TeacherModel() {
    }

    public TeacherModel(String name, String username, String phone, String password, String gender, String picUrl,String department) {
        this.name = name;
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.department = department;
        this.gender = gender;
        this.picUrl = picUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
