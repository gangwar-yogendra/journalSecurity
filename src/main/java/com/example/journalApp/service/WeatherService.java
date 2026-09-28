package com.example.journalApp.service;

import com.example.journalApp.api.response.WeatherResponse;
import com.example.journalApp.cache.AppCache;
import com.example.journalApp.constants.PlaceHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// This code is being used for calling external API (GET) and Consume API using (POST)
/*@Component*/
@Service
@Slf4j
public class WeatherService {
    // In stating we were doing hardcoding the api key in the class, which is not a good practice
    // So now we are commenting here in this class, and we will add this in yaml file
    // private static final String apiKey = "<someKey>"; // Replace with your actual API key

    // The above code is being commented because we are now storing the API key in the YAML configuration file
    @Value("${weather.api.key}")
    private String apiKey;

    //private static final String API = "http://api.weatherstack.com/current?access_key=API_KEY&query=City";
    // OR
//    @Value("${weather.api.url}")
//    private String apiUrl;

    // And above apiUrl we are storing in database and removing from here and application yml file
    // to understand the concept which is @PostConstruct, So we will create a package cache, and
    // create a collection in MongoDB Atlas "config_journal_app"
    /*
    {
        _id: ObjectId('6ab9660172e4a58022739f12'),
        key: "weather_api",
        value: "http://api.weatherstack.com/current?access_key=<apiKey>&query=<city>"
     }
     */
    /*
        And we have created AppCache so the key value pair will get at once and we will use it in this class,
        so we will remove the @Value("${weather.api.url}") and
     */

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    // This code is being called on GetRequest
    public WeatherResponse getWeather(String city)
    {

        /* Commented because of Appcache class storing in database and getting the value from there
        String url = apiUrl
                .replace("{apiKey}", apiKey)
                .replace("{city}", city);
        */

        String uri = appCache.getValue(AppCache.keys.WEATHER_API.name());
        //String url = uri.replace("<apiKey>", apiKey).replace("<city>", city);
        String url = uri.replace(PlaceHolder.API_KEY, apiKey).replace(PlaceHolder.CITY, city);

        log.info("Requesting weather data from API in getWeather(): {}", url);

        // This will get using HTTPMethod.GET
        ResponseEntity<WeatherResponse> exchange = restTemplate.exchange(url, HttpMethod.GET, null, WeatherResponse.class);
        WeatherResponse weatherResponse = exchange.getBody();

        log.info("Weather data retrieved successfully in getWeather(): {}", weatherResponse);

        return weatherResponse;
    }

    // This is being called on post request
    // But this code is being added w.r.t your this journal app microservice which
    // is running on some server and trying to update it into the API
    // there are some code is being added for weather to know how can we pass
    // the parameters, and this code is not being initiated from any controller
    public WeatherResponse updateWeather(String city)
    {
        //String finalApi = API.replace("City", city).replace("API_KEY", apiKey);
        // OR
        // While using application.yml file
        /*String url = apiUrl
                .replace("{apiKey}", apiKey)
                .replace("{city}", city);*/
        String uri = appCache.getValue("weather_api");
        String url = uri.replace("<apiKey>", apiKey).replace("<city>", city);
        log.info("Requesting weather data from API in updateWeather(): {}", url);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", "application/json"); // ("Key", "Value")

        UserDetails user = User.builder()
                .username("Ram")
                .password("Ram")
                .roles("USER")
                .build();
        HttpEntity<UserDetails> httpEntity = new HttpEntity<>(user, httpHeaders);

        ResponseEntity<WeatherResponse> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, WeatherResponse.class);
        WeatherResponse weatherResponse = exchange.getBody();

        log.info("Weather data retrieved successfully in updateWeather(): {}", weatherResponse);

        return weatherResponse;
    }
}
