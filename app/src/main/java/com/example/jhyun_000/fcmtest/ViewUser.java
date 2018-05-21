package com.example.jhyun_000.fcmtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by jhyun_000 on 2018-04-16.
 */

public class ViewUser extends AppCompatActivity {
    ImageView imageView_alarm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        init();
    }

    void init(){
        imageView_alarm = (ImageView)findViewById(R.id.imageView_alarm);
        imageView_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewUser.this, Timer.class);
                stopService(intent);

                MyDBHandler myDBHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
                myDBHandler.deleteAll();

                Intent intent1 = new Intent(ViewUser.this, MainActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }
}
