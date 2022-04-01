package com.example;

import java.io.Serializable;

public class Campaign implements Serializable {

    // campgain class, also serializable
    String region;
    String focus;
    int length;

    public Campaign(String region, String focus, int length) {
        // initiate class and assign vars
        this.region = region;
        this.focus = focus;
        this.length = length;
    }
}
