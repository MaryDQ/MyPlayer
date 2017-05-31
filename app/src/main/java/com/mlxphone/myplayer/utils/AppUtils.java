package com.mlxphone.myplayer.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.mlxphone.myplayer.application.MyplayerApp;

/**
 * Created by MLXPHONE on 2016/6/21 0021.
 */
public class AppUtils {
    //隐藏输入法
    public static void hideInputMethod(View view){
        InputMethodManager imm= (InputMethodManager)MyplayerApp.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()){
            imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
