package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created at 17.05.2023 by Dan.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {
    private String broker;
    private String topic;
    private String username;
    private String password;
    private String clientId;
    private int qos;
}
