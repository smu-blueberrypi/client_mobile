package com.example.user.mainapp;

public class DroneGPS {
    private static double latitude;
    private static double longtitude;


    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        DroneGPS.latitude = latitude;
    }

    public static double getLongtitude() {
        return longtitude;
    }

    public static void setLongtitude(double longtitude) {
        DroneGPS.longtitude = longtitude;
    }
}
