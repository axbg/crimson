package com.axbg.crimson.network;

public class NetworkUtil {
    public static String buildSearchUrl(String title, int page) {
        return "https://openlibrary.org/search.json?limit=20&title=" + title + "&page=" + page;
    }

    public static String buildCoverUrl(String edition) {
        return "https://covers.openlibrary.org/b/olid/" + edition + "-M.jpg";
    }
}
