package com.example;

import java.io.Serializable;

public class Campaign implements Serializable {

    String region;
    String focus;
    int length;

    public Campaign(String region, String focus, int length) {
        this.region = region;
        this.focus = focus;
        this.length = length;
    }
}
