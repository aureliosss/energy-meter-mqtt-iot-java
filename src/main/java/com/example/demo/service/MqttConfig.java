package com.example.demo.service;

import com.example.demo.model.DataEntry;
import com.example.demo.model.MqttProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created at 17.05.2023 by Dan.
 */
@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class MqttConfig implements MqttCallback {
    private MqttProperties mqttProperties;
    private MqttClient client;
    @Getter
    private JsonNode energyMeterJson;
    @Getter
    private List<DataEntry> energyMeterData;
    private int dataEntryCount;
    @Autowired
    public MqttConfig(MqttProperties mqttProperties) {
        this.mqttProperties = mqttProperties;
        this.energyMeterData = new ArrayList<>();
        this.dataEntryCount = 0;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.info("ConnectionLost: " + throwable.getMessage());
    }

    private MqttConnectOptions createMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(mqttProperties.getUsername());
        options.setPassword(mqttProperties.getPassword().toCharArray());
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(60);
        return options;
    }

    @PostConstruct
    public void connectAndSubscribe() {
        try {
            client = new MqttClient(mqttProperties.getBroker(), mqttProperties.getClientId(), new MemoryPersistence());
            MqttConnectOptions options = createMqttConnectOptions();
            client.setCallback(this);
            client.connect(options);
            client.subscribe(mqttProperties.getTopic(), mqttProperties.getQos());
            log.info("connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonMessage = mapper.readTree(new String(message.getPayload()));

        if (jsonMessage.has("ENERGY")) {
            JsonNode energyNode = jsonMessage.get("ENERGY");
            String total = energyNode.get("Total").asText() + " KW";
            String voltage = energyNode.get("Voltage").asText() + " V";
            String yesterday = energyNode.get("Yesterday").asText() + " KW";
            String today = energyNode.get("Today").asText() + " KW";
            String powerNow = energyNode.get("Power").asText() + " W/h";
            LocalDateTime timestamp = LocalDateTime.parse(jsonMessage.get("Time").asText());

            DataEntry dataEntry = new DataEntry(0L, total, voltage, yesterday, today, powerNow, timestamp);
            energyMeterData.add(dataEntry);
            if (dataEntryCount >= 10) {
                energyMeterData.remove(0);
            } else {
                dataEntryCount++;
            }
            energyMeterData.add(dataEntry);
        }

        log.info("ENERGY_METER: " + jsonMessage);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        log.info("deliveryComplete: " + iMqttDeliveryToken.isComplete());

    }
}
