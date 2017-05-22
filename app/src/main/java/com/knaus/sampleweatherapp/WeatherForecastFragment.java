package com.knaus.sampleweatherapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WeatherForecastFragment extends Fragment {
    private static final String TAG = "WeatherForecastFragment";
    private static final String ARG_FORECAST = "forecast";

    Typeface weatherFont;

    WeatherForecast forecast;

    TextView dateText;
    TextView tempHighText;
    TextView tempLowText;
    TextView conditionText;
    TextView weatherIcon;

    Handler handler;

    public WeatherForecastFragment() {
        handler = new Handler();
    }

    public static WeatherForecastFragment newInstance(WeatherForecast param1) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FORECAST, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weathericons.ttf");
        if (getArguments() != null) {
            forecast = (WeatherForecast) getArguments().getSerializable(ARG_FORECAST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather_forecast, container, false);

        dateText = (TextView) view.findViewById(R.id.forecast_date);
        tempHighText = (TextView) view.findViewById(R.id.forecast_temp_high);
        tempLowText = (TextView) view.findViewById(R.id.forecast_temp_low);
        conditionText = (TextView) view.findViewById(R.id.forecast_condition_text);
        weatherIcon = (TextView) view.findViewById(R.id.forecast_icon);

        // set weather font
        tempHighText.setTypeface(weatherFont);
        tempLowText.setTypeface(weatherFont);
        weatherIcon.setTypeface(weatherFont);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (forecast != null) {
            dateText.setText(forecast.sDate);
            tempHighText.setText(String.format("%d %s", forecast.iTempHigh, getActivity().getString(R.string.weather_fahrenheit)));
            tempLowText.setText(String.format("%d %s", forecast.iTempLow, getActivity().getString(R.string.weather_fahrenheit)));
            conditionText.setText(forecast.sConditionText);
        }
        weatherIcon.setText(forecast.sConditionIcon);
    }

}
