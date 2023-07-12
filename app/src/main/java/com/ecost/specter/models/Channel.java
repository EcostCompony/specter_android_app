package com.ecost.specter.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {

    private Integer id, category, is_subscriber, is_admin, subscribers_count, inactive;
    private String title, short_link, description, body;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortLink() {
        return short_link;
    }

    public void setShort_link(String short_link) {
        this.short_link = short_link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSubscribersCount() {
        return subscribers_count;
    }

    public void setSubscribers_count(Integer subscribers_count) {
        this.subscribers_count = subscribers_count;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getIsSubscriber() {
        return is_subscriber;
    }

    public void setIs_subscriber(Integer is_subscriber) {
        this.is_subscriber = is_subscriber;
    }

    public Integer getIsAdmin() {
        return is_admin;
    }

    public void setIs_admin(Integer is_admin) {
        this.is_admin = is_admin;
    }

    public void setInactive(Integer inactive) {
        this.inactive = inactive;
    }

    public Integer getInactive() {
        return inactive;
    }

}