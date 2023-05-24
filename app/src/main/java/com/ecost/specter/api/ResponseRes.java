package com.ecost.specter.api;

import com.ecost.specter.models.Channel;

public class ResponseRes {

    private String confirmToken, signupToken, serviceAuthToken, accessToken, authToken;
    private Channel[] channels;

    public String getConfirmToken() {
        return confirmToken;
    }

    public String getSignupToken() {
        return signupToken;
    }

    public String getServiceAuthToken() {
        return serviceAuthToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Channel[] getChannels() {
        return channels;
    }

    public void setConfirm_token(String confirmToken) {
        this.confirmToken = confirmToken;
    }

    public void setSignup_token(String signupToken) {
        this.signupToken = signupToken;
    }

    public void setService_auth_token(String serviceAuthToken) {
        this.serviceAuthToken = serviceAuthToken;
    }

    public void setAuth_token(String authToken) {
        this.authToken = authToken;
    }

    public void setAccess_token(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setChannels(Channel[] channels) {
        this.channels = channels;
    }

}