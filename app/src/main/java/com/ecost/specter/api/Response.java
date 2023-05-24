package com.ecost.specter.api;

public class Response {

    private ResponseRes res;
    private ResponseError error;

    public ResponseRes getRes() {
        return res;
    }

    public ResponseError getError() {
        return error;
    }

    public void setRes(ResponseRes res) {
        this.res = res;
    }

    public void setError(ResponseError error) {
        this.error = error;
    }

}