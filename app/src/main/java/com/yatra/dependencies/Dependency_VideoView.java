package com.yatra.dependencies;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;



import java.util.ArrayList;


public class Dependency_VideoView extends AppCompatActivity {
    VideoView videoView;
    Toolbar toolbar;

    String token,Url,time,eventNo,event_name,deviceName,position1,MobileDeviceId;
    String result_responce="";
    String REGISTER_URL,MSG_REGISTER_URL;
    EditText Notes;
    public static ArrayList<String> DropdwonArrayList;
    ImageView fullscreen;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView sentmsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloaded_videoview);


        videoView = (VideoView)findViewById(R.id.escavideoView);
        //fullscreen = (ImageView) findViewById(R.id.fullscreen);
        MediaController mediaController = new MediaController(this);

     //   time = getIntent().getExtras().getString("position1");
        event_name = getIntent().getExtras().getString("name");
        Url = getIntent().getExtras().getString("path");
      //  eventNo = getIntent().getExtras().getString("eventno");
       // time = getIntent().getExtras().getString("time");



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor (Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        setSupportActionBar (toolbar);
        getSupportActionBar().setTitle(event_name);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mediaController.setAnchorView(videoView);
        Uri uri = Uri.parse(Url);
        videoView.setVideoURI(uri);
        videoView.setMediaController(mediaController);
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.start();
                //  pdialog.dismiss();
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                        mp.start();
                    }
                });
            }
        });

     /*   fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });*/
    }
    @Override
    protected void onStart() {
        super.onStart();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}