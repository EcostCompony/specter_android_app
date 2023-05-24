package com.ecost.specter.api;

public class ResponseError {

    private int error_code;
    private String error_msg;
    private ErrorDetails[] error_details;

    public int getErrorCode() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public void setError_details(ErrorDetails[] error_details) {
        this.error_details = error_details;
    }

}