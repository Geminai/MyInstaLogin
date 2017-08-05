package com.elluminati.myinstalogin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ImageView btnInstaLogin;
    private InstagramApp mApp;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
                displayInfoDialogView();
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(MainActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnInstaLogin = (ImageView) findViewById(R.id.btnInstaLogin);
        btnInstaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectOrDisconnectUser();
            }
        });
        mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                mApp.fetchUserName(handler);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void connectOrDisconnectUser() {
        if (mApp.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mApp.resetAccessToken();
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mApp.authorize();
        }
    }

    private void displayInfoDialogView() {
        Toast.makeText(this, "" +
                        userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE) + "\n" +
                        userInfoHashmap.get(InstagramApp.TAG_USERNAME) + "\n" +
                        userInfoHashmap.get(InstagramApp.TAG_FOLLOWS) + "\n" +
                        userInfoHashmap.get(InstagramApp.TAG_FOLLOWED_BY)
                , Toast.LENGTH_SHORT).show();
    }
}