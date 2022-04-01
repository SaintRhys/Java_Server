package com.example;

import java.io.Serializable;
import java.time.LocalDate;

public class Packet implements Serializable {
    // serializable object to be sent between client/server
    String message;
    LocalDate date;

    public Packet(String message) {
        // assign message current date time to packet
        this.message = message;
        this.date = LocalDate.now();
    }

}
