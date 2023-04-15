package com.ecost.specter.models;

public class FAQPost {

    int type;
    String context;

    public FAQPost() {}

    public FAQPost(int type, String context) {
        this.type = type;
        this.context = context;
    }

    public int getType() {
        return type;
    }

    public String getContext() {
        return context;
    }

}