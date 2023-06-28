package com.ecost.specter.api;

import com.ecost.specter.models.Channel;
import com.ecost.specter.models.Post;
import com.ecost.specter.models.Subscriber;
import com.ecost.specter.models.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    private Object res;
    private ResponseError error;

    public ResponseRes getRes() {
        return new ObjectMapper().convertValue(res, ResponseRes.class);
    }

    public Channel getChannel() {
        return new ObjectMapper().convertValue(res, Channel.class);
    }

    public Post getPost() {
        return new ObjectMapper().convertValue(res, Post.class);
    }

    public Post[] getPosts() {
        return new ObjectMapper().convertValue(res, Post[].class);
    }

    public Subscriber[] getSubscribers() {
        return new ObjectMapper().convertValue(res, Subscriber[].class);
    }

    public Channel[] getChannels() {
        return new ObjectMapper().convertValue(res, Channel[].class);
    }

    public User getUser() {
        return new ObjectMapper().convertValue(res, User.class);
    }

    public ResponseError getError() {
        return error;
    }

    public void setRes(Object res) {
        this.res = res;
    }

    public void setError(ResponseError error) {
        this.error = error;
    }

}