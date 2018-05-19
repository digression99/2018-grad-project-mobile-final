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


public class ImageAdapter2 extends ArrayAdapter {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //ArrayAdapter
    private Context context;
    private LayoutInflater inflater;

    private String[] imagePath;
    private String[] imageUrls;
    private String[] timestamp;
    private String[] result;
    ImageView imageView;
    TextView textViewResult;
    TextView textViewTimestamp;
    Button button_blacklist;


    public ImageAdapter2(@NonNull Context context, @NonNull String[] imagePath, @Nullable String[] timestamp, @NonNull String[] result) {
//        public ImageAdapter(@NonNull Context context, @NonNull ArrayList<String> imageUrls) {
        super(context, R.layout.listview_item_image, imagePath);
        this.context = context;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
        this.result = result;

        this.imageUrls = generateUrls(imagePath);

        Log.i("ImageAdapter", "imageadapter constructor");
        inflater = LayoutInflater.from(context);
    }

    private String[] generateUrls(String[] imagePath) {
        String[] urls = new String[imagePath.length];
        String email_changed = user_email.replaceAll("[@.]", "-");

        Log.i("email changed", email_changed);
        for (int i = 0; i < imagePath.length; i++) {
            urls[i] = "https://s3.amazonaws.com/androidprojectapp-userfiles-mobilehub-1711223959/" + imagePath[i] + ".jpg";
        }
        Log.i("Logs", "imagepath: " + imagePath[0]);
        Log.i("Logs", "urls[0]: " + urls[0]);

        return urls;
    }

    @Override
    public int getCount() {
        Log.i("getCount", String.valueOf(imagePath.length));
        return imagePath.length;
    }

    @Override
    public Object getItem(int position) {
        Log.i("getItem", String.valueOf(position));
        return imagePath[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = inflater.inflate(R.layout.listview_item_image, parent, false);
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

        textViewResult.setText(result[position]);
//        GlideApp.with();

//        imageView.setImageResource(R.drawable.firebase_lockup_400);
        imageView.getLayoutParams().height = 300;
        imageView.getLayoutParams().width = 400;
        Glide.with(context)
                .load(imageUrls[position])
//                .into((ImageView)convertView);
                .into(imageView);


        button_blacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlackList(imagePath[position], "그냥");
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
