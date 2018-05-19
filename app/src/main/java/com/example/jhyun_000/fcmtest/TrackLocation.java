package com.example.jhyun_000.fcmtest;

/**
 * Created by jhyun_000 on 2018-03-02.
 */

public class TrackLocation {
    private int _id;
    private double longitude;
    private double latitude;

    public TrackLocation(int _id, double longitude, double latitude) {
        this._id = _id;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public TrackLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
