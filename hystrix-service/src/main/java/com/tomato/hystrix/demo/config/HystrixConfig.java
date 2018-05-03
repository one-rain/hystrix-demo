package com.tomato.hystrix.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "hystrix")
public class HystrixConfig {

    private int timeoutInMillions;

}
