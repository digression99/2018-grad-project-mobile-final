package com.example.jhyun_000.fcmtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by jhyun_000 on 2018-02-13.
 */

public class NotificationActivity extends AppCompatActivity {
    TextView nt_textview;
    String num1;
    String num2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        nt_textview = (TextView) findViewById(R.id.nt_textview);

        if (getIntent().getExtras() != null) {
            num1 = getIntent().getExtras().get("num1").toString();
//            num2 = getIntent().getExtras().get("num2").toString();
//
//            nt_textview.setText("num1 : " + num1 + ", num2 : " + num2);
            nt_textview.setText("num1 : " + num1);
        }
    }
}
