package com.razarahim.connectedboard.Models;

public class FaqsModel {
    String id, title,message,name;
    long time;

    public FaqsModel(String id, String title, String message, String name, long time) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.name = name;
        this.time = time;
    }

    public FaqsModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
