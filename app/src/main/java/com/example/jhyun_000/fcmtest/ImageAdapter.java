package com.example.jhyun_000.fcmtest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.example.jhyun_000.fcmtest.EmailPasswordActivity.user_email;


/**
 * Created by jhyun_000 on 2018-04-09.
 */


public class ImageAdapter extends ArrayAdapter {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //ArrayAdapter
    private Context context;
    private LayoutInflater inflater;

    private String[] imageUuids;
    private String[] imageUrls;
    private String[] timestamp;
    private int[] uuid_location;
    ImageView imageView;
    TextView textViewResult;
    TextView textViewTimestamp;
    Button button_blacklist;


    public ImageAdapter(@NonNull Context context, @NonNull String[] imageUuids, @Nullable String[] timestamp, @NonNull int[] uuid_location) {
//        public ImageAdapter(@NonNull Context context, @NonNull ArrayList<String> imageUrls) {
        super(context, R.layout.listview_item_image, imageUuids);
        this.context = context;
        this.imageUuids = imageUuids;
        this.timestamp = timestamp;
        this.uuid_location = uuid_location;

        this.imageUrls = generateUrls(imageUuids, uuid_location);

        Log.i("ImageAdapter", "imageadapter constructor");
        inflater = LayoutInflater.from(context);
    }

    private String[] generateUrls(String[] imageUuids, int[] image_location) {
        String[] urls = new String[imageUuids.length];
        String email_changed = user_email.replaceAll("[@.]", "-");

        Log.i("email changed", email_changed);
        for (int i = 0; i < imageUuids.length; i++) {
            if (image_location[i] == 0) {
                urls[i] = "https://s3.amazonaws.com/androidprojectapp-userfiles-mobilehub-1711223959/" +
                        email_changed + "/user/" + imageUuids[i] + ".jpg";
            } else {
                urls[i] = "https://s3.amazonaws.com/androidprojectapp-userfiles-mobilehub-1711223959/" +
                        email_changed + "/detected/" + imageUuids[i] + ".jpg";
            }

        }

        return urls;
    }

    @Override
    public int getCount() {
        Log.i("getCount", String.valueOf(imageUuids.length));
        return imageUuids.length;
    }

    @Override
    public Object getItem(int position) {
        Log.i("getItem", String.valueOf(position));
        return imageUuids[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
//            convertView = inflater.inflate(R.layout.listview_item_image, parent, false);
//            convertView = inflater.inflate(R.layout.listview_item_image, parent, true);
            convertView = inflater.inflate(R.layout.listview_item_image, parent, false);
//            imageView = (ImageView)convertView.findViewById(R.id.item_imageview);
//            textView = (TextView)convertView.findViewById(R.id.item_textview);
        }
        Log.i("get view", "image url : " + imageUrls[position]);
        imageView = (ImageView) convertView.findViewById(R.id.item_imageview);
        textViewTimestamp = (TextView) convertView.findViewById(R.id.item_textview_timestamp);
        textViewResult = (TextView) convertView.findViewById(R.id.item_textview_result);
        button_blacklist = (Button) convertView.findViewById(R.id.button_blacklist);

//        textView.setText(imageUrls[position]);
        textViewTimestamp.setText(String.valueOf(position));
        if (timestamp != null) {
            textViewTimestamp.setText(timestamp[position]);
        }

//        GlideApp.with();

//        imageView.setImageResource(R.drawable.firebase_lockup_400);
        imageView.getLayoutParams().height = 300;
        imageView.getLayoutParams().width = 400;
        Glide.with(context)
                .load(imageUrls[position])
//                .into((ImageView)convertView);
                .into(imageView);


//        com.bumptech.glide.module.AppGlideModule GlideApp;
//        GlideApp = new AppGlideModule() {
//        };
//        GlideApp.applyOptions();

//        GlideApp
//                .with(context)
//                .load(imageUrls[position])
//                .into((ImageView) convertView);
//

        button_blacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlackList(imageUuids[position], "그냥");
            }
        });
        return convertView;
    }

    void sendBlackList(final String uuid, final String reasons) {
        new Thread() {
            public void run() {
                OkHttpClient client = new OkHttpClient();
//                     RequestBody body = new FormBody.Builder()
//                             .add("Token", FirebaseInstanceId.getInstance().getToken())
//                             .build();

                RequestBody body = RequestBody.create(JSON, "{\"uuid\": \"" + uuid + "\", \"reason\" : \"" + reasons + "\"}");

                Request request = new Request.Builder()
                        .url("https://grad-project-app.herokuapp.com/user/blacklist-register")
                        .post(body)
                        .build();

                try {
                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
