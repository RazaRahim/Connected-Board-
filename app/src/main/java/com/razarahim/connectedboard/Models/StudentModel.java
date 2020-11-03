package com.razarahim.connectedboard.Models;

public class StudentModel {
    String name, rollNumber, whichClass, password, gender, picUrl, department,phone;

    public StudentModel() {
    }


    public StudentModel(String name, String rollNumber, String whichClass, String password, String gender, String picUrl,
                        String department,String phone) {

        this.name = name;
        this.rollNumber = rollNumber;
        this.whichClass = whichClass;
        this.password = password;
        this.gender = gender;
        this.picUrl = picUrl;
        this.department = department;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public String getWhichClass() {
        return whichClass;
    }

    public void setWhichClass(String whichClass) {
        this.whichClass = whichClass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
