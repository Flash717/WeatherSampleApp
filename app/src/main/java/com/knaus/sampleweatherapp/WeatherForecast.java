package com.knaus.sampleweatherapp;

import java.io.Serializable;

/**
 * Helper class for weather forecast information
 */

public class WeatherForecast implements Serializable {
    public String sDate;
    public int iTempHigh;
    public int iTempLow;
    public String sConditionIcon;
    public String sConditionText;

    public WeatherForecast() {

    }

    public WeatherForecast(String in_date, int in_tempHigh, int in_tempLow, String in_condIcon, String in_condText) {
        this.sDate = in_date;
        this.iTempHigh = in_tempHigh;
        this.iTempLow = in_tempLow;
        this.sConditionIcon = in_condIcon;
        this.sConditionText = in_condText;
    }

}
