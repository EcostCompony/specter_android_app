package com.ecost.specter.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscriber {

    private int user_id;
    private int is_admin;

    public int getUserId() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getIsAdmin() {
        return is_admin;
    }

    public void setIs_admin(int is_admin) {
        this.is_admin = is_admin;
    }

}