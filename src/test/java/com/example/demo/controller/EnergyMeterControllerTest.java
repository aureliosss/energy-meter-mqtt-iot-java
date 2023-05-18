package com.example.demo.controller;

import com.example.demo.model.DataEntry;
import com.example.demo.service.MqttConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnergyMeterController")
class EnergyMeterControllerTest {
    @Mock
    private MqttConfig mqttConfig;

    @Mock
    private Model model;

    @InjectMocks
    private EnergyMeterController energyMeterController;

    @Test
    @DisplayName("Should return index page with empty data when energy meter data is not available")
    void getIndexPageWhenEnergyMeterDataIsNotAvailable() {
        when(mqttConfig.getEnergyMeterData()).thenReturn(new ArrayList<>());

        String result = energyMeterController.getIndexPage(model);

        assertEquals("index", result);
        verify(model, times(1)).addAttribute(eq("data"), anyList());
        verify(mqttConfig, times(1)).getEnergyMeterData();
    }

    @Test
    @DisplayName("Should return index page with limited data when energy meter data is available")
    void getIndexPageWhenEnergyMeterDataIsAvailable() {
        List<DataEntry> energyMeterData = new ArrayList<>();
        LocalDateTime timestamp = LocalDateTime.now();
        DataEntry dataEntry1 = new DataEntry(1L, "100", "220", "50", "60", "500", timestamp);
        DataEntry dataEntry2 =
                new DataEntry(2L, "200", "220", "60", "70", "600", timestamp.plusMinutes(1));
        energyMeterData.add(dataEntry1);
        energyMeterData.add(dataEntry2);

        when(mqttConfig.getEnergyMeterData()).thenReturn(energyMeterData);

        String result = energyMeterController.getIndexPage(model);

        assertEquals("index", result);
        verify(model, times(1)).addAttribute(eq("data"), anyList());
        verify(mqttConfig, times(1)).getEnergyMeterData();
    }
}