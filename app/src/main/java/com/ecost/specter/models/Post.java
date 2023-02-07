package com.ecost.specter.models;

public class Post {

    public int id, senderId;
    public String author, time, context;

    public Post() {}

    public Post(int id, String author, String time, String context) {
        this.id = id;
        this.author = author;
        this.time = time;
        this.context = context;
    }

    public Post(int id, int senderId, String author, String time, String context) {
        this.id = id;
        this.senderId = senderId;
        this.author = author;
        this.time = time;
        this.context = context;
    }

}