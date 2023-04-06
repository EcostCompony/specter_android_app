package com.ecost.specter.models;

public class User {

    public Integer id, ecost_id;
    public String name, link;
    public boolean channel_admin;

    public User() {}

    public User(Integer id, Integer ecost_id, String name, String link) {
        this.id = id;
        this.ecost_id = ecost_id;
        this.name = name;
        this.link = link;
    }

    public User(Integer id, boolean channel_admin, Integer ecost_id, String name, String link) {
        this.id = id;
        this.channel_admin = channel_admin;
        this.ecost_id = ecost_id;
        this.name = name;
        this.link = link;
    }

}