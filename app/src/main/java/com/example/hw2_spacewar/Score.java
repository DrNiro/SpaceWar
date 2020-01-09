package com.example.hw2_spacewar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Score implements Comparable<Score> {

    private String name;
    private int distance;
    private int money;
    private double latitude;
    private double longitude;
    private String date;

    public Score() {
    }

    public Score(String name, int distance, int money, double latitude, double longitude) {
        setName(name);
        setDistance(distance);
        setMoney(money);
        setLatitude(latitude);
        setLongitude(longitude);
        setDate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        this.date = sdf.format(currentTime);
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    @Override
    public int compareTo(Score score) {
        if(this.getDistance() > score.getDistance()) {
            return -1;
        } else if(this.getDistance() < score.getDistance()) {
            return 1;
        } else {
            return 0;
        }
    }
}
