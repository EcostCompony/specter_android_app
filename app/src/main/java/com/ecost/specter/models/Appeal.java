package com.ecost.specter.models;

public class Appeal {

    public Integer id, author, posts_number;
    public String topic, body;

    public Appeal() { }

    public Appeal(Integer id, String topic, Integer author, Integer posts_number, String body) {
        this.id = id;
        this.topic = topic;
        this.author = author;
        this.posts_number = posts_number;
        this.body = body;
    }

}