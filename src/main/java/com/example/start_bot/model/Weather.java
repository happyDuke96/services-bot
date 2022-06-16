package com.example.start_bot.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Weather {
    private String city;
    private WeatherCache data;
}
