package com.example.jhyun_000.fcmtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import static com.example.jhyun_000.fcmtest.EmailPasswordActivity.user_email;

/**
 * Created by jhyun_000 on 2018-04-09.
 */

public class ViewVisitor extends AppCompatActivity {
    ListView listView;
    GridView gridView;
    //    String uuids[];
    String uuids;
    String result;
    ImageView imageView;
    TextView text_result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_visitor);

//        uuids = (String[])savedInstanceState.get("uuids");
        Bundle bundle = getIntent().getExtras();
        uuids = (String) bundle.get("uuids");
        result = (String) bundle.get("result");
        int uuid_position = 0; //0인경우 사용자, 친구 / 1인 경우 unknown

        if (result.equals("unknown")) {
            uuid_position = 1;
        }

        init();
        getImageFromS3(user_email, uuids, uuid_position);

    }

    public void init() {
        gridView = (GridView) findViewById(R.id.view_visitor_gridview);
//        gridView.setAdapter(new ImageAdapter(this, urls));
//        gridView.setAdapter(new ImageAdapter(this, uuids, null));

        text_result = (TextView) findViewById(R.id.text_result);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (result.equals("unknown")) {
            text_result.setText("외부인입니다");
        } else {
            text_result.setText(result);
        }


    }

    void getImageFromS3(String email, String uuid, int position) {

        String email_changed = user_email.replaceAll("[@.]", "-");
        String url = null;

        if (position == 0) {
            url = "https://s3.amazonaws.com/androidprojectapp-userfiles-mobilehub-1711223959/" +
                    email_changed + "/user/" + uuid + ".jpg";
        } else if (position == 1) {
            url = "https://s3.amazonaws.com/androidprojectapp-userfiles-mobilehub-1711223959/" +
                    email_changed + "/detected/" + uuid + ".jpg";
        }

////        Picasso
//        Glide.with(this)
//                .load(url[position])
//                .into(imageView);

//        Glide.with(context).clear(imageView);

        Log.i("visitor_url", url);
        imageView.getLayoutParams().height = 800;
        imageView.getLayoutParams().width = 900;

        Glide.with(ViewVisitor.this)
                .load(url)
//                .into((ImageView)convertView);
                .into(imageView);
    }
}
