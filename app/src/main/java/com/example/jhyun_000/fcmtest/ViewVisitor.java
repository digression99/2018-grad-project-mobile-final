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
    TextView text_reason;
    int uuid_position;      //0인경우 사용자, 친구 ,unknown  / 1인 경우 blacklist
    String reason="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_visitor);

//        uuids = (String[])savedInstanceState.get("uuids");
        Bundle bundle = getIntent().getExtras();
        uuids = (String) bundle.get("uuids");
        result = (String) bundle.get("result");

        if(bundle.containsKey("reason")){
            reason = (String)bundle.get("reason");
        }
         uuid_position = 0;

        init();
        getImageFromS3(user_email, uuids, uuid_position);

    }

    public void init() {
        gridView = (GridView) findViewById(R.id.view_visitor_gridview);
//        gridView.setAdapter(new ImageAdapter(this, urls));
//        gridView.setAdapter(new ImageAdapter(this, uuids, null));

        text_result = (TextView) findViewById(R.id.text_result);
        text_reason = (TextView) findViewById(R.id.text_reason);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (result.equals("unknown")) {
            text_result.setText("외부인입니다");
            uuid_position = 0;
        } else if(result.equals("blacklist")){
            text_result.setText("위험인물 입니다");
            text_reason.setText(reason);
            uuid_position = 1;

        }else{
            text_result.setText(result);
            uuid_position = 0;
        }


    }

    void getImageFromS3(String email, String uuid, int position) {

        String email_changed = user_email.replaceAll("[@.]", "-");
        String url = null;

        if (position == 0) {
            url = "https://s3.amazonaws.com/androidprojectapp-userfiles-mobilehub-1711223959/" +
                    email_changed + "/user/" + uuid + ".jpg";
        } else if (position == 1) {     //blacklist
            url = "https://s3.amazonaws.com/androidprojectapp-userfiles-mobilehub-1711223959/"
                    +email_changed + "/detected/" + uuid + ".jpg";
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
