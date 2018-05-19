package com.example.jhyun_000.fcmtest;

/**
 * Created by jhyun_000 on 2018-01-28.
 */

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by filipp on 6/16/2016.
 */
public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager locationManager;

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_SEND_TO_SERVICE = 3;
    public static final int MSG_SEND_TO_ACTIVITY = 4;

    private Messenger mClient = null;   // Activity 에서 가져온 Messenger
    double longitude;
    double latitude;
    Messenger mMessenger;

    int REQUEST_FINE = 1;
    int REQUEST_COARSE = 2;
    int REQUEST_INTERNET = 3;

    @Override
    public void onCreate() {
        registerLocationListener();
        registerLocationManager();
        Log.d("Service", "service onCreate");

    }

    private void registerLocationManager() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        Log.d("Service", "registerLocationManager Start");


//        PermissionCheck();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            PermissionCheck();

            return;
        }

//        https://stackoverflow.com/questions/3993658/requestlocationupdates-interval-in-android
//        minTime, minDistance순서

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, listener);
//        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.d("Service", "registerLocationManager End");
    }

    private void registerLocationListener() {
        Log.d("onCreate", "register listener");
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                Intent i = new Intent("location_update");
//                i.putExtra("coordinates", location.getLongitude() + " " + location.getLatitude());
//                sendBroadcast(i);
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.d("Location", "location changed");

                Log.d("Service: Longitude", String.valueOf(longitude));
                Log.d("Service: Latitude", String.valueOf(latitude));
                Intent i = new Intent("location_update");
                i.putExtra("longitude", location.getLongitude());
                i.putExtra("latitude", location.getLatitude());
                sendBroadcast(i);

                TrackLocation loc = new TrackLocation(location.getLongitude(), location.getLatitude());
                MyDBHandler dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
                dbHandler.addLocation(loc);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("Location", "status changed");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d("Location", "provider enabled");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("Location", "provider disabled");
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
    }


    @Override
    public void onDestroy() {
        Log.d("Service", "location on destroy");
        super.onDestroy();
        if (locationManager != null) {
            //noinspection MissingPermission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(listener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void PermissionCheck() {
        int permission_fine_location = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permission_coarse_location = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission_internet = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
        if (permission_fine_location == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE);
        }

        if (permission_coarse_location == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE);
        }

        if (permission_internet == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_INTERNET);
        }
    }
}