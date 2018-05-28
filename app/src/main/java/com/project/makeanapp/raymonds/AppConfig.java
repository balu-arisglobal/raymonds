package com.project.makeanapp.raymonds;

/**
 * Created by balu on 4/1/2017.
 */
public class AppConfig {

    //Fetching url from DB
    public static String FETCH_URL;


    public static void setFetchUrl(String fetchUrl) {
        FETCH_URL = fetchUrl;
    }

    public static String getFetchUrl() {
        return FETCH_URL;
    }
}
