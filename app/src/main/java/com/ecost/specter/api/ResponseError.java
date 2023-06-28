package com.ecost.specter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseError {

    private int code, error_code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getErrorCode() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

}