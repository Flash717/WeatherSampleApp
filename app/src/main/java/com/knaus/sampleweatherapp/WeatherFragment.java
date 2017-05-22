package com.knaus.sampleweatherapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";

    Typeface weatherFont;

    TextView locationText;
    TextView lastUpdatedText;
    TextView detailsText;
    TextView currentTempText;
    TextView weatherIcon;

    LinearLayout forecastLayout;

    Handler handler;

    public WeatherFragment() {
        handler = new Handler();
    }

    public static WeatherFragment newInstance() {
        WeatherFragment fragment = new WeatherFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set weather font typeface to display weather icon and fahrenheit symbol
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weathericons.ttf");
        updateWeatherData(new LocationPreference(getActivity()).getLocation());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        locationText = (TextView) view.findViewById(R.id.location_text);
        lastUpdatedText = (TextView) view.findViewById(R.id.last_update_text);
        detailsText = (TextView) view.findViewById(R.id.details_text);
        currentTempText = (TextView) view.findViewById(R.id.current_temp_text);
        weatherIcon = (TextView) view.findViewById(R.id.weather_icon);
        forecastLayout = (LinearLayout) view.findViewById(R.id.forecastLayout);

        currentTempText.setTypeface(weatherFont);
        weatherIcon.setTypeface(weatherFont);

        return  view;
    }

    /**
     * Retrieve weather data and update the UI
     * @param location
     */
    private void updateWeatherData(final String location) {
        new Thread() {
            public void run() {
                // get weather information as JSONObject
                final JSONObject json = GetWeather.getJSON(getActivity(), location);
                if (json != null) {
                    // if we have data then render the UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(json);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.location_not_found), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }.start();
    }

    /**
     * Update the layout with the weather JSON object
     * @param json
     */
    private void renderWeather(JSONObject json) {
        try {
            JSONObject channel = json.getJSONObject("query").getJSONObject("results").getJSONObject("channel");
            JSONObject location = channel.getJSONObject("location");
            JSONObject units = channel.getJSONObject("units");
            JSONObject atmosphere = channel.getJSONObject("atmosphere");
            JSONObject item = channel.getJSONObject("item");
            JSONObject condition = item.getJSONObject("condition");
            JSONArray forecast = item.getJSONArray("forecast");
            locationText.setText(location.getString("city").toUpperCase(Locale.US) + ", " + location.getString("region"));
            detailsText.setText(String.format("%s \nHumidity: %d%% \nPressure: %d %s", condition.getString("text"), atmosphere.getInt("humidity"), atmosphere.getInt("pressure"), units.getString("pressure")));
            String tempUnit = units.getString("temperature").equals("F") ? getActivity().getString(R.string.weather_fahrenheit) : getActivity().getString(R.string.weather_celsius);
            currentTempText.setText(String.format("%d %s", condition.getInt("temp"), tempUnit));
            lastUpdatedText.setText("As of " + channel.getString("lastBuildDate"));

            weatherIcon.setText(getWeatherIcon(item.getJSONObject("condition").getInt("code")));

            addForecastData(forecast);
        } catch (Exception e) {
            Log.e(TAG, "Error rendering weather: " + e.getMessage());
        }
    }

    /**
     * Add each forecast item as WeatherForecastFragment to HorizontalScrollView
     */
    private void addForecastData(JSONArray forecast) {
        forecastLayout.removeAllViews();
        for(int i = 0; i < forecast.length(); i++) {
            try {
                JSONObject item = forecast.getJSONObject(i);
                WeatherForecast fc = new WeatherForecast(item.getString("date"), item.getInt("high"), item.getInt("low"), getWeatherIcon(item.getInt("code")), item.getString("text"));
                WeatherForecastFragment wff = WeatherForecastFragment.newInstance(fc);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.forecastLayout, wff, String.format("forecast_%d", i)).commit();

            } catch (Exception e) {
                Log.e(TAG, "Error adding forecast fragments: " + e.getMessage());
            }
        }
    }

    /*
     * Gets weather icon based on response condition code
     * see also condition codes at https://developer.yahoo.com/weather/documentation.html
     */
    private String getWeatherIcon(int id){
        String icon = "";
        switch(id) {
            case 0 : icon = getActivity().getString(R.string.weather_tornado);
                break;
            case 1 : icon = getActivity().getString(R.string.weather_tropical_storm);
                break;
            case 2 : icon = getActivity().getString(R.string.weather_hurricane);
                break;
            case 3 : icon = getActivity().getString(R.string.weather_severe_thunderstorms);
                break;
            case 4 : icon = getActivity().getString(R.string.weather_thunderstorms);
                break;
            case 5 : icon = getActivity().getString(R.string.weather_mixed_rain_and_snow);
                break;
            case 6 : icon = getActivity().getString(R.string.weather_mixed_rain_and_sleet);
                break;
            case 7 : icon = getActivity().getString(R.string.weather_mixed_snow_and_sleet);
                break;
            case 8 : icon = getActivity().getString(R.string.weather_freezing_drizzle);
                break;
            case 9 : icon = getActivity().getString(R.string.weather_drizzle);
                break;
            case 10 : icon = getActivity().getString(R.string.weather_freezing_rain);
                break;
            case 11 : icon = getActivity().getString(R.string.weather_showers);
                break;
            case 12 : icon = getActivity().getString(R.string.weather_showers2);
                break;
            case 13 : icon = getActivity().getString(R.string.weather_snow_flurries);
                break;
            case 14 : icon = getActivity().getString(R.string.weather_light_snow_shower);
                break;
            case 15 : icon = getActivity().getString(R.string.weather_blowing_snow);
                break;
            case 16 : icon = getActivity().getString(R.string.weather_snow);
                break;
            case 17 : icon = getActivity().getString(R.string.weather_hail);
                break;
            case 18 : icon = getActivity().getString(R.string.weather_sleet);
                break;
            case 19 : icon = getActivity().getString(R.string.weather_dust);
                break;
            case 20 : icon = getActivity().getString(R.string.weather_foggy);
                break;
            case 21 : icon = getActivity().getString(R.string.weather_haze);
                break;
            case 22 : icon = getActivity().getString(R.string.weather_smoky);
                break;
            case 23 : icon = getActivity().getString(R.string.weather_blustery);
                break;
            case 24 : icon = getActivity().getString(R.string.weather_windy);
                break;
            case 25 : icon = getActivity().getString(R.string.weather_cold);
                break;
            case 26 : icon = getActivity().getString(R.string.weather_cloudy);
                break;
            case 27 : icon = getActivity().getString(R.string.weather_mostly_cloudy_night);
                break;
            case 28 : icon = getActivity().getString(R.string.weather_mostly_cloudy_day);
                break;
            case 29 : icon = getActivity().getString(R.string.weather_partly_cloudy_night);
                break;
            case 30 : icon = getActivity().getString(R.string.weather_partly_cloudy_day);
                break;
            case 31: icon = getActivity().getString(R.string.weather_clear_night);
                break;
            case 32: icon = getActivity().getString(R.string.weather_sunny);
                break;
            case 33 : icon = getActivity().getString(R.string.weather_fair_night);
                break;
            case 34 : icon = getActivity().getString(R.string.weather_fair_day);
                break;
            case 35 : icon = getActivity().getString(R.string.weather_mixed_rain_and_hail);
                break;
            case 36 : icon = getActivity().getString(R.string.weather_hot);
                break;
            case 37 : icon = getActivity().getString(R.string.weather_isolated_thuderstorms);
                break;
            case 38 : icon = getActivity().getString(R.string.weather_scattered_thunderstorms);
                break;
            case 39 : icon = getActivity().getString(R.string.weather_scattered_thunderstorms2);
                break;
            case 40 : icon = getActivity().getString(R.string.weather_scattered_showers);
                break;
            case 41 : icon = getActivity().getString(R.string.weather_heavy_snow);
                break;
            case 42 : icon = getActivity().getString(R.string.weather_scattered_snow_showers);
                break;
            case 43 : icon = getActivity().getString(R.string.weather_heavy_snow2);
                break;
            case 44 : icon = getActivity().getString(R.string.weather_partly_cloudy);
                break;
            case 45 : icon = getActivity().getString(R.string.weather_thundershowers);
                break;
            case 46 : icon = getActivity().getString(R.string.weather_snow_showers);
                break;
            case 47 : icon = getActivity().getString(R.string.weather_isolated_thundershowers);
                break;
            default:
                icon = getActivity().getString(R.string.weather_not_available);
        }
        return icon;
    }

    public void changeLocation(String location) {
        updateWeatherData(location);
    }

}
