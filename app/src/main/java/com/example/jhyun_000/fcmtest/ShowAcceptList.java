package com.example.jhyun_000.fcmtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jhyun_000 on 2018-03-15.
 */

public class ShowAcceptList extends AppCompatActivity {
    String acceptedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_accept_list);

        if(savedInstanceState != null) {
            Bundle bundle = getIntent().getExtras();
            acceptedEmail = (String) bundle.get("acceptedEmail");
        }

        AlertDialog LDialog = new AlertDialog.Builder(this)
                .setTitle("도움수락한 회원")
                .setMessage("아이디 : "+acceptedEmail)
//                .setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//                        if(!ShowAcceptList.this.isFinishing()){
//                            finish();
//                        }
//                    }
//                })
//                .setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        if(!ShowAcceptList.this.isFinishing()){
//                            finish();
//                        }
//                    }
//                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      finish();
                    }
                }).create();
        LDialog.show();
    }

}
