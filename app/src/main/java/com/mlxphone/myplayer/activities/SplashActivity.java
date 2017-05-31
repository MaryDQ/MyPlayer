package com.mlxphone.myplayer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.mlxphone.myplayer.R;
import com.mlxphone.myplayer.service.PlayService;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    private static final int START_ACTIVITY = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            //去标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent=new Intent(this,PlayService.class);
        startService(intent);

        setContentView(R.layout.activity_splash);

        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },3000);
    }


}
