package com.example.journalApp.cache;

import com.example.journalApp.entity.ConfigJournalAppEntity;
import com.example.journalApp.repository.ConfigJournalAppRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AppCache {

    // So we should not pass hardcode value in WeatherService
    public enum keys{
        WEATHER_API;
    }

    private Map<String, String> appCache;

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    // We will get the key and value here from "config_journal_app" collection from MongoDB
    // @PostConstruct annotation is used in Spring/Java to run a method automatically after
    // a bean has been created and its dependencies have been injected, but before the bean is used by the application
    // The typical lifecycle is:
    //    Spring creates object
    //       ↓
    //    Dependencies are injected
    //       ↓
    //    @PostConstruct method runs
    //       ↓
    //    Bean is ready to use
    @PostConstruct
    public void init()
    {
        appCache = new HashMap<>();
        List<ConfigJournalAppEntity> configList = configJournalAppRepository.findAll();
        appCache = configList
                .stream()
                .collect(Collectors.toMap(ConfigJournalAppEntity::getKey, ConfigJournalAppEntity::getValue));
        log.info("AppCache initialized with values: {}", appCache);
    }

    public String getValue(String key) {
        String value = appCache.get(key);
        log.info("Retrieved value from AppCache for key: {}: {}", key, value);
        return value;
    }
}
