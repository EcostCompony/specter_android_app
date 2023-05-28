package com.ecost.specter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseError {

    private int error_code;

    public int getErrorCode() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

}