package com.ecost.specter.models;

import java.util.List;

public class Channel {

    public Integer id, author, postsNumber;
    public String title, body, shortLink;
    public Boolean markBody;
    public List<Integer> subscribers;

    public Channel() { }

    public Channel(Integer id, String shortLink, Integer author, Integer postsNumber, String title, String body, Boolean markBody, List<Integer> subscribers) {
        this.id = id;
        this.shortLink = shortLink;
        this.author = author;
        this.postsNumber = postsNumber;
        this.title = title;
        this.body = body;
        this.markBody = markBody;
        this.subscribers = subscribers;
    }

}