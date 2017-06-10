package com.mmtechbd.remotehealthmonitor.model;

/**
 * Created by Roaim on 27-Nov-16.
 */

public class RequestToken {
    private String token, tokenSecret;

    public RequestToken(String token, String tokenSecret) {
        this.token = token;
        this.tokenSecret = tokenSecret;
    }

    public String getToken() {
        return token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }
}
