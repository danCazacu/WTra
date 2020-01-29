package com.wtra.client.entity;

import java.util.ArrayList;
import java.util.List;

public class Video {

    private String name = "";
    private String id = "";
    private String country = "";
    private Double duration = Double.valueOf(-1);
    private List<SignFromVideo> signs = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public List<SignFromVideo> getSigns() {
        return signs;
    }

    public void setSigns(List<SignFromVideo> signs) {
        this.signs = signs;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
