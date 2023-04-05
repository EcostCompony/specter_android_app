package com.ecost.specter.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Channel {

    public Integer id, author, postsNumber, categoryId;
    public String title, shortLink, body, description;
    public Boolean markBody;
    public HashMap<String, User> subscribers = new HashMap<>();

    public Channel() { }

    public Channel(Integer id, String shortLink, Integer author, Integer postsNumber, String title, Integer categoryId, String description, String body, Boolean markBody) {
        this.id = id;
        this.shortLink = shortLink;
        this.author = author;
        this.postsNumber = postsNumber;
        this.title = title;
        this.categoryId = categoryId;
        this.description = description;
        this.body = body;
        this.markBody = markBody;
    }

}