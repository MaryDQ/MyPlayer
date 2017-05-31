package com.mlxphone.myplayer.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.lidroid.xutils.DbUtils;
import com.mlxphone.myplayer.utils.Constant;

/**
 * Created by MLXPHONE on 2016/6/12 0012.
 */
public class MyplayerApp extends Application{

    public static SharedPreferences sp;
    public static DbUtils dbUtils;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        sp=getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        dbUtils=DbUtils.create(getApplicationContext(),Constant.DB_NAME);
        context=getApplicationContext();
    }
}
