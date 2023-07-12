package com.ecost.specter.api;

import com.ecost.specter.models.Channel;
import com.ecost.specter.models.Post;
import com.ecost.specter.models.Subscriber;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseList {

    private Integer count, total_amount;
    private Object items;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Integer total_amount) {
        this.total_amount = total_amount;
    }

    public void setItems(Object items) {
        this.items = items;
    }

    public Channel[] getChannels() {
        return new ObjectMapper().convertValue(items, Channel[].class);
    }

    public Post[] getPosts() {
        return new ObjectMapper().convertValue(items, Post[].class);
    }

    public Subscriber[] getSubscribers() {
        return new ObjectMapper().convertValue(items, Subscriber[].class);
    }

}