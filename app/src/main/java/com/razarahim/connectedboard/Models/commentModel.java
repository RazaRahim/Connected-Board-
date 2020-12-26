package com.razarahim.connectedboard.Models;

public class commentModel {

    commentModel(){

    }
    String uid,username,userImage,usermsg,date,Time;

    public commentModel(String uid, String username, String userImage, String usermsg, String date, String time) {
        this.uid = uid;
        this.username = username;
        this.userImage = userImage;
        this.usermsg = usermsg;
        this.date = date;
        Time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUsermsg() {
        return usermsg;
    }

    public void setUsermsg(String usermsg) {
        this.usermsg = usermsg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;

    }
}
