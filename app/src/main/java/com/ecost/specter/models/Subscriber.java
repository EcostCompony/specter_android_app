package com.ecost.specter.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscriber {

    private Integer user_id;
    private String name, short_link;
    private boolean admin;

    public Integer getUserId() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortLink() {
        return short_link;
    }

    public void setShort_link(String short_link) {
        this.short_link = short_link;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}