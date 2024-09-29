package com.example.project;

public class EmergencyService {
    private String name;
    private String number;

    public EmergencyService(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}