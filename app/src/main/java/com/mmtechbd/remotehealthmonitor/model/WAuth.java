package com.mmtechbd.remotehealthmonitor.model;

/**
 * Created by Roaim on 28-Nov-16.
 */

public class WAuth extends RequestToken {
    private final String url;
    public WAuth(String url, RequestToken requestToken) {
        super(requestToken.getToken(), requestToken.getTokenSecret());
        this.url = url;
    }

    public String getAuthUrl() {
        return url;
    }
}
