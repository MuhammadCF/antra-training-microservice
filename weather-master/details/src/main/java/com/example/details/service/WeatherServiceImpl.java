package com.example.details.service;



import com.example.details.config.EndpointConfig;
import com.example.details.pojo.City;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@Service
public class WeatherServiceImpl implements WeatherService{

    private final RestTemplate restTemplate;

    @Value("${my.weather.bycity:}")
    private String weatherByCity;

    @Value("${my.weather.byid:}")
    private String weatherById;


    public WeatherServiceImpl(RestTemplate getRestTemplate) {
        this.restTemplate = getRestTemplate;
    }

    @Override
    @Retryable(include = IllegalAccessError.class)
    @Cacheable("findCityIdByName")
    @HystrixCommand(fallbackMethod = "findCityIdByNameFallback",
    commandProperties= {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value= "2500"),
            @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value="6"),
            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value="50"),
            @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value="6000"),
    })
    public List<Integer> findCityIdByName(String city) {
        City[] cities = restTemplate.getForObject(weatherByCity + city, City[].class);
        List<Integer> ans = new ArrayList<>();
        for(City c: cities) {
            if(c != null && c.getWoeid() != null) {
                ans.add(c.getWoeid());
            }
        }
        return ans;
    }

    public List<Integer> findCityIdByNameFallback(String city) {

        return Collections.singleton(0);
    }


    //change findcitynamebyid => find weather details by id
    @Override
    @Cacheable("findCityNameById")
    @HystrixCommand(fallbackMethod = "findCityNameByIdFallback",
            commandProperties= {
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value= "2500"),
                    @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value="6"),
                    @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value="50"),
                    @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value="6000"),
            })
    public Map<String, Map> findCityNameById(int id) {
        Map<String, Map> ans = restTemplate.getForObject(weatherById + id, HashMap.class);
        return ans;
    }

    public Map<String, Map> findCityNameByIdFallback(int id) {
        Map<String, Map> ans = new HashMap<>();
        Map<String, Map> inner = new HashMap<>();
        ans.put("access failure, Id not found", inner);
        return ans;
    }
}
