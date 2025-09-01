package com.minegolem.promoter.scraper;

import java.net.HttpURLConnection;
import java.net.URL;

public class TikTokChecker {

    public static String resolveFinalUrl(String urlString) throws Exception {
        HttpURLConnection connection;
        String finalUrl = urlString;

        while (true) {
            URL url = new URL(finalUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.connect();

            int status = connection.getResponseCode();

            if (status != HttpURLConnection.HTTP_MOVED_PERM || status != HttpURLConnection.HTTP_MOVED_TEMP) {
                String location = connection.getHeaderField("Location");

                if (location == null) break;

                finalUrl = location;
            } else {
                break;
            }
        }
        return finalUrl;
    }

    public static String extractVideoId(String url) {
        String regex = ".*/video/(\\d+).*";
        return url.matches(regex) ? url.replaceAll(regex, "$1") : null;
    }



}
