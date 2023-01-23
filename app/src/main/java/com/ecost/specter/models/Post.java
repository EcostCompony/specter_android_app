package com.ecost.specter.models;

public class Post {

    public String author;
    public String time;
    public String context;

    public Post() {}

    public Post(String author, String time, String context) {
        this.author = author;
        this.time = time;
        this.context = context;
    }

}