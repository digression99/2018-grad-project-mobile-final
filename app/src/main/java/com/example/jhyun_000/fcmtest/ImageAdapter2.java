package com.example.jhyun_000.fcmtest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.Response;

import static com.example.jhyun_000.fcmtest.Constants.server_url_blacklist_register;
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

        textViewResult.setText(result[position]+" ");
//        GlideApp.with();

//        imageView.setImageResource(R.drawable.firebase_lockup_400);
        imageView.getLayoutParams().height = 500;
        imageView.getLayoutParams().width = 400;
        Glide.with(context)
                .load(imageUrls[position])
//                .into((ImageView)convertView);
                .into(imageView);


        button_blacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText editText = new EditText(getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage("블랙리스트에 추가할 이유를 적어주세요")
                        .setTitle("블랙리스트 추가하기")
                        .setView(editText)
                        .setPositiveButton("전송", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String reason = editText.getText().toString();
                                sendBlackList(imagePath[position], reason);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        return convertView;
    }

    void sendBlackList(final String key, final String reasons) {
//        new Thread() {
//            public void run() {
//                OkHttpClient client = new OkHttpClient();
//
//                RequestBody body = RequestBody.create(JSON, "{\"uuid\": \"" + uuid + "\", \"reason\" : \"" + reasons + "\"}");
//
//                Request request = new Request.Builder()
//                        .url(server_url_blacklist_register)
//                        .post(body)
//                        .build();
//
//                try {
//                    client.newCall(request).execute();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();


        JSONObject object = new JSONObject();
        try {
            object.put("email", user_email);
            object.put("key", key);
            object.put("reason", reasons);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String json = object.toString();
        Log.i("adapter", "json : "+json);

        BlackListHttp blackListHttp = new BlackListHttp();
        try {
            Response response = blackListHttp.execute(server_url_blacklist_register,json).get();
            Log.i("blacklist", "response : "+response.body().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }



    public class BlackListHttp extends AsyncTask<String, String, Response> {
        RequestHttp requestHttp;
        String response;


        @Override
        protected void onPreExecute() {
            requestHttp = new RequestHttp();
        }

        @Override
        protected Response doInBackground(String... url) {
//            String res = null;
            Response response = null;
            try {
                response = requestHttp.postResponse(url[0], url[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response res) {
            Log.i("BlackList", res.body().toString());

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            //http status code
            if(res.code()/100 == 2) {           //정상
                builder.setMessage("블랙리스트에 해당 이유로 등록되었습니다")
                        .setTitle("전송 완료")
                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });
            }

            else{           //오류
                builder.setMessage("에러발생으로 등록이 되지 않았습니다")
                        .setTitle("오류")
                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });
            }
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
