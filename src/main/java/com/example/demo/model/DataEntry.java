package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Created at 17.05.2023 by Dan.
 */
@AllArgsConstructor
@Getter
@Setter
public class DataEntry {
    private long id;
    private String total;
    private String voltage;
    private String yesterday;
    private String today;
    private String powerNow;
    private LocalDateTime timestamp;
}
