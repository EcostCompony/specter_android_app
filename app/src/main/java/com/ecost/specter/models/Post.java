package com.ecost.specter.models;

public class Post {

    public int id, senderId;
    public String author, context;
    public Long date;

    public Post() {}

    public Post(int id, String author, Long date, String context) {
        this.id = id;
        this.author = author;
        this.date = date;
        this.context = context;
    }

    public Post(int id, int senderId, String author, Long date, String context) {
        this.id = id;
        this.senderId = senderId;
        this.author = author;
        this.date = date;
        this.context = context;
    }

}