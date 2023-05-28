package com.ecost.specter.api;

import com.ecost.specter.models.Channel;
import com.ecost.specter.models.Post;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Response {

    private Object res;
    private ResponseError error;

    public ResponseRes getRes() {
        return new ObjectMapper().convertValue(res, ResponseRes.class);
    }

    public Channel getChannelRes() {
        return new ObjectMapper().convertValue(res, Channel.class);
    }

    public Channel[] getChannelsRes() {
        return new ObjectMapper().convertValue(res, Channel[].class);
    }

    public Post[] getPostsRes() {
        return new ObjectMapper().convertValue(res, Post[].class);
    }

    public Post getPostRes() {
        return new ObjectMapper().convertValue(res, Post.class);
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