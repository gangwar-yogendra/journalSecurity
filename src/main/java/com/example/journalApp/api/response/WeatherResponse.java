package com.example.journalApp.api.response;

//public class WeatherResponse {
//}

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Getter
@Setter
@Slf4j

// This class is being converted into POJO code because an external API
// response we were getting as JSON and converted as POJO code and get response here
public class WeatherResponse {
    private Request request;
    private Location location;
    private Current current;

    public String getWeatherDescription() {
        if(current == null)
        {
            log.error("Current weather information is null");
        }

        if(current.weatherDescriptions == null)
        {
            log.error("Weather descriptions are null");
        }

        if(current.weatherDescriptions.isEmpty())
        {
            log.error("Weather descriptions list is empty");
        }

        if (current != null && current.weatherDescriptions != null && !current.weatherDescriptions.isEmpty()) {
            log.info("Weather description retrieved successfully: {}", current.weatherDescriptions.get(0));
            return current.weatherDescriptions.get(0);
        }

        log.warn("No weather description available");

        return "No weather description available";
    }

    @Getter
    @Setter
    public class Current{
        // @JsonProperty is being used when we will get the response from external API
        // it will get in observation_time and then it will store value in observationTime variable
        @JsonProperty("observation_time")
        private String observationTime;
        private int temperature;
        @JsonProperty("weather_code")
        private int weatherCode;
        @JsonProperty("weather_icons")
        private ArrayList<String> weatherIcons;
        @JsonProperty("weather_descriptions")
        private ArrayList<String> weatherDescriptions;
        private Astro astro;
        @JsonProperty("air_quality")
        private AirQuality airQuality;
        @JsonProperty("wind_speed")
        private int windSpeed;
        @JsonProperty("wind_degree")
        private int windDegree;
        @JsonProperty("wind_dir")
        private String windDir;
        private int pressure;
        private double precip;
        private int humidity;
        private int cloudcover;
        private int feelslike;
        @JsonProperty("uv_index")
        private int uvIndex;
        public int visibility;
        @JsonProperty("is_day")
        public String isDay;



        // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
        // import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
        /* ObjectMapper om = new ObjectMapper();
        Root root = om.readValue(myJsonString, Root.class); */
        @Getter
        @Setter
        public class AirQuality{
            private String co;
            private String no2;
            private String o3;
            private String so2;
            @JsonProperty("pm2_5")
            private String pm2_5;
            private String pm10;
            @JsonProperty("us_epa_index")
            private String usEpaIndex;
            @JsonProperty("gb_defra_index")
            private String gbDefraIndex;
        }

        @Getter
        @Setter
        public class Astro{
            private String sunrise;
            private String sunset;
            private String moonrise;
            private String moonset;
            @JsonProperty("moon_phase")
            private String moonPhase;
            @JsonProperty("moon_illumination")
            private int moonIllumination;
        }

    }

    @Getter
    @Setter
    public class Location{
        private String name;
        private String country;
        private String region;
        private String lat;
        private String lon;
        @JsonProperty("timezone_id")
        private String timezoneId;
        private String localtime;
        @JsonProperty("localtime_epoch")
        private int localtimeEpoch;
        @JsonProperty("utc_offset")
        private String utcOffset;
    }

    @Getter
    @Setter
    public class Request{
        private String type;
        private String query;
        private String language;
        private String unit;
    }
}