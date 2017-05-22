package com.knaus.sampleweatherapp;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Helper class to save location preference (most recently used location)
 */

public class LocationPreference {
    private static final String TAG = "LocationPreference";

    SharedPreferences prefs;

    private static String DEFAULT_LOCATION;

    public LocationPreference(Activity activity) {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
        DEFAULT_LOCATION = activity.getResources().getString(R.string.default_location);
    }

    String getLocation() {
        return prefs.getString("location", DEFAULT_LOCATION);
    }

    void setLocation(String location) {
        prefs.edit().putString("location", location).commit();
    }

}
