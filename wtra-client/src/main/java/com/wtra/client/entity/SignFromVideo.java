package com.wtra.client.entity;

public class SignFromVideo {

    private Integer videoStamp = -1;
    private Location location = new Location();

    private Sign sign = new Sign();
    private String moreAt = "";

    public Integer getVideoStamp() {
        return videoStamp;
    }

    public void setVideoStamp(Integer videoStamp) {
        this.videoStamp = videoStamp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public String getMoreAt() {
        return moreAt;
    }

    public void setMoreAt(String moreAt) {
        this.moreAt = moreAt;
    }
}
