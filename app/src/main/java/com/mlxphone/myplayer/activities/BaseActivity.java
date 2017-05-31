package com.mlxphone.myplayer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

import com.mlxphone.myplayer.service.PlayService;

/**
 * Created by MLXPHONE on 2016/6/1 0001.
 */
public abstract class BaseActivity extends FragmentActivity{

    public PlayService playService;

    private boolean isbound=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.PlayBinder playBinder=(PlayService.PlayBinder)service;
            playService=playBinder.getPlayService();
            playService.setMusicUpdateListener(musicUpdateListener);
            musicUpdateListener.onChange(playService.getCurrentPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService=null;
            isbound=false;
        }
    };

    private PlayService.MusicUpdateListener musicUpdateListener=new PlayService.MusicUpdateListener() {
        @Override
        public void onPublish(int progress) {
               publish(progress);
        }

        @Override
        public void onChange(int position) {
                change(position);
        }
    };

    public abstract void publish(int progress);
    public abstract void change(int position);


    //绑定服务
    public void bindPlayService(){
        if (isbound==false){
        Intent intent=new Intent(this,PlayService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
        isbound=true;
        }}

    //解除绑定服务
    public void unbindPlayService(){
        if (isbound==true){
            unbindService(conn);
            isbound=false;
        }

    }

}
