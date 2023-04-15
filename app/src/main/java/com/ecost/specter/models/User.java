package com.ecost.specter.models;

public class User {

    Integer id, ecost_id;
    String name, short_link;
    boolean channel_admin;

    public User() {}

    public User(Integer id, Integer ecost_id, String name, String link) {
        this.id = id;
        this.ecost_id = ecost_id;
        this.name = name;
        this.short_link = link;
    }

    public User(Integer id, boolean channel_admin, Integer ecost_id, String name, String link) {
        this.id = id;
        this.channel_admin = channel_admin;
        this.ecost_id = ecost_id;
        this.name = name;
        this.short_link = link;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortLink() {
        return short_link;
    }

    public boolean getChannelAdmin() {
        return channel_admin;
    }

}