package com.knaus.sampleweatherapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Helper class to retrieve weather information from yahoo's weather api
 */
public class GetWeather {
    private static final String TAG = "GetWeather";
    private static final String WEATHER_API_BASE_URI = "https://query.yahooapis.com/v1/public/yql?q=%s&format=json";
    private static final String WEATHER_API_QUERY = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\")";

    /**
     * Get weather information as JSONObject from Yahoo Weather API
     * @param context
     * @param location
     * @return
     */
    public static JSONObject getJSON(Context context, String location) {
        try {
            String query = URLEncoder.encode(String.format(WEATHER_API_QUERY, location), "UTF-8");
            URL url = new URL(String.format(WEATHER_API_BASE_URI, query));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            return data;
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}
