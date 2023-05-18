package com.example.demo.controller;

import com.example.demo.model.DataEntry;
import com.example.demo.service.MqttConfig;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created at 17.05.2023 by Dan.
 */
@Controller
@AllArgsConstructor
@RequestMapping(path="/api/v1/energymeter")
public class EnergyMeterController {
    private final MqttConfig mqttConfig;
    @GetMapping
    public ResponseEntity<JsonNode> getEnergyMeter() {
        JsonNode energyMeterJson = mqttConfig.getEnergyMeterJson();
        if (energyMeterJson != null) {
            return ResponseEntity.ok(energyMeterJson);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping(path = "/info")
    public String getIndexPage(Model model) {
        List<DataEntry> energyMeterData = mqttConfig.getEnergyMeterData();
        List<DataEntry> limitedData = energyMeterData.stream()
                .limit(10)
                .toList();
        model.addAttribute("data", limitedData);
        return "index";
    }
}

