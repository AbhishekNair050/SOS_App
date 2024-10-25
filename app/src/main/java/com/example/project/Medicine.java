// app/src/main/java/com/example/project/Medicine.java
package com.example.project;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Medicine {
    private String name;
    private String dosage;
    private int timesPerDay;
    private ArrayList<String> timings;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public int getTimesPerDay() {
        return timesPerDay;
    }

    public void setTimesPerDay(int timesPerDay) {
        this.timesPerDay = timesPerDay;
    }

    public ArrayList<String> getTimings() {
        return timings;
    }

    public void setTimings(ArrayList<String> timings) {
        this.timings = timings;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " - " + dosage + " - " + timesPerDay + " times a day";
    }
}