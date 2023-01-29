package com.ecost.specter.models;

public class Post {

    public int id;
    public String author;
    public String time;
    public String context;

    public Post() {}

    public Post(int id, String author, String time, String context) {
        this.id = id;
        this.author = author;
        this.time = time;
        this.context = context;
    }

}