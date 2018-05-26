package com.example.jhyun_000.fcmtest;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.example.jhyun_000.fcmtest.Constants.server_url_accept_help;
import static com.example.jhyun_000.fcmtest.EmailPasswordActivity.user_email;

/**
 * Created by jhyun_000 on 2018-04-17.
 */

public class DeliveredHelp extends AppCompatActivity implements OnMapReadyCallback {
    Button button_ok;
    Button button_cancel;
    MapView mapView;
    String requestedEmail = "";
    Double latitude = 37.56;
    Double longitude = 126.97;

    private GoogleMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        if (savedInstanceState != null) {
            Bundle bundle = getIntent().getExtras();
            requestedEmail = (String) bundle.get("requestedEmail");
            latitude = (Double) bundle.get("lat");
            longitude = (Double) bundle.get("lng");
        }

        init();
    }

    void init() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
//        mapView = (MapView)findViewById(R.id.mapView);
        button_ok = (Button) findViewById(R.id.button_ok);
        button_cancel = (Button) findViewById(R.id.button_cancel);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send server 내가 도움 줄거라는걸
                JSONObject jobject = new JSONObject();
                try {
                    jobject.put("requestedEmail", requestedEmail);
                    jobject.put("acceptedEmail", user_email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CallRequestHttp callRequestHttp = new CallRequestHttp();
                String json = jobject.toString();

                Log.i("Help", "json : " + json);
                try {
                    String response = callRequestHttp.execute(server_url_accept_help, json).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear backstack
                int count = getSupportFragmentManager().getBackStackEntryCount();
                if (count > 0) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                //goto  mainactivity
                Intent intent = new Intent(DeliveredHelp.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
//        GoogleMapOptions options = new GoogleMapOptions();
//        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
//                .compassEnabled(true)
//                .zoomControlsEnabled(true)
//                .zoomGesturesEnabled(true);
//
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//
//        UiSettings uiSettings = googleMap.getUiSettings();
//        uiSettings.setMyLocationButtonEnabled(true);
//        uiSettings.setMapToolbarEnabled(true);
//        uiSettings.setZoomGesturesEnabled(true);
//        uiSettings.setScrollGesturesEnabled(true);
//
////        latitude, longitude 순서
//        LatLng helpLocation = new LatLng(latitude, longitude);
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(helpLocation)
//                .title("HELP")
//                .snippet("도움 바람");
////        markerOptions.draggable(true);
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//
//        Marker marker = googleMap.addMarker(markerOptions);
//
//        marker.showInfoWindow();
//
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(helpLocation));

        mMap = googleMap;

        UiSettings mUiSettings = mMap.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

//        latitude, longitude 순서
        LatLng helpLocation = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(helpLocation)
                .title("HELP")
                .snippet("도움 바람");
//        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        Marker marker = mMap.addMarker(markerOptions);

        marker.showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(helpLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }


    public class CallRequestHttp extends AsyncTask<String, String, String> {
        RequestHttp requestHttp;
//        String json = "{\"email\": \"" + user_email + "\", \"duration\": " + 3600 * 24 * 7 * 1000 + "}";
        String response;

        @Override
        protected void onPreExecute() {
            requestHttp = new RequestHttp();
        }

        @Override
        protected String doInBackground(String... url) {
            String res = null;
            try {
                res = requestHttp.post(url[0], url[1]);
                Log.i("ResponseHelp", res);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            response = s;
            Log.i("Response-postexecute-help", s);
            Log.i("Response-postexecute-help-response", response);

            AlertDialog.Builder builder = new AlertDialog.Builder(DeliveredHelp.this);

            builder.setMessage("요청 수락 완료")
                    .setTitle("요청 수락 완료")
                    .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //다이얼로그를 취소한다
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
}
