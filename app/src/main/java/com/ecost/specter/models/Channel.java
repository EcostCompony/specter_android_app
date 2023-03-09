package com.ecost.specter.models;

import java.util.List;

public class Channel {

    public Integer id, author, postsNumber, categoryId, subNumber;
    public String title, shortLink, body, description;
    public Boolean markBody;
    public List<Integer> subscribers;

    public Channel() { }

    public Channel(Integer id, String shortLink, Integer author, Integer postsNumber, String title, Integer categoryId, String description, String body, Boolean markBody, List<Integer> subscribers, Integer subNumber) {
        this.id = id;
        this.shortLink = shortLink;
        this.author = author;
        this.postsNumber = postsNumber;
        this.title = title;
        this.categoryId = categoryId;
        this.description = description;
        this.body = body;
        this.markBody = markBody;
        this.subscribers = subscribers;
        this.subNumber = subNumber;
    }

}