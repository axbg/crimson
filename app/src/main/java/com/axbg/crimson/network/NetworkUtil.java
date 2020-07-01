package com.axbg.crimson.network;

public class NetworkUtil {
    public static String buildSearchUrl(String title) {
        return "https://openlibrary.org/search.json?title=" + title;
    }

    public static String buildCoverUrl(String edition) {
        return "https://covers.openlibrary.org/b/olid/" + edition + "-M.jpg";
    }
}
