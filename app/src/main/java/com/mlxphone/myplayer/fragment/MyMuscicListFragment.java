package com.mlxphone.myplayer.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.andraskindler.quickscroll.QuickScroll;
import com.mlxphone.myplayer.R;
import com.mlxphone.myplayer.activities.MainActivity;
import com.mlxphone.myplayer.activities.PlayActivity;
import com.mlxphone.myplayer.adapter.MyMusicListAdapter;
import com.mlxphone.myplayer.utils.MediaUtils;
import com.mlxphone.myplayer.vo.Mp3Info;

import java.util.ArrayList;

/**
 * Created by MLXPHONE on 2016/5/30 0030.
 */
public class MyMuscicListFragment extends Fragment implements OnItemClickListener, View.OnClickListener {


    private ListView listView_my_music;
    private ArrayList<Mp3Info> mp3Infos;
    private MyMusicListAdapter myMusicListAdapter;
    private ImageView imageView_album;
    private TextView textView_songName;
    private TextView textView2_singer;
    private ImageView imageView2_play_pause, imageView3_next;

    private MainActivity mainActivity;

    private boolean isPause = false;

    private int position=0;

    private QuickScroll quickscroll;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    public static MyMuscicListFragment newInstance() {
        MyMuscicListFragment my = new MyMuscicListFragment();
        return my;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_musci_list_layout, null);
        listView_my_music = (ListView) view.findViewById(R.id.listView_my_music);
        imageView_album = (ImageView) view.findViewById(R.id.imageView_album);
        imageView2_play_pause = (ImageView) view.findViewById(R.id.imageView2_play_pause);
        imageView3_next = (ImageView) view.findViewById(R.id.imageView3_next);
        textView2_singer = (TextView) view.findViewById(R.id.textView2_singer);
        textView_songName = (TextView) view.findViewById(R.id.textView_songName);

        quickscroll= (QuickScroll) view.findViewById(R.id.quickscroll);
        listView_my_music.setOnItemClickListener(this);
        imageView2_play_pause.setOnClickListener(this);
        imageView3_next.setOnClickListener(this);
        imageView_album.setOnClickListener(this);
        loadData();

        return view;
    }

    //加载本地音乐列表
    private void loadData() {
        mp3Infos = MediaUtils.getMp3Infos(mainActivity);
        myMusicListAdapter = new MyMusicListAdapter(mainActivity, mp3Infos);
        listView_my_music.setAdapter(myMusicListAdapter);

        initQuickscroll();
    }

    private void initQuickscroll(){
        quickscroll.init(QuickScroll.TYPE_POPUP_WITH_HANDLE,listView_my_music,myMusicListAdapter,QuickScroll.STYLE_HOLO);
        quickscroll.setFixedSize(1);
        quickscroll.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
        quickscroll.setPopupColor(QuickScroll.BLUE_LIGHT,QuickScroll.BLUE_LIGHT_SEMITRANSPARENT,1, Color.WHITE,1);
    }

    @Override
    public void onResume() {
        super.onResume();
        //绑定服务
        System.out.println("myMusicListFragment   onresume...");
        mainActivity.bindPlayService();
    }

    @Override
    public void onPause() {
        super.onPause();
        //解除绑定服务
        mainActivity.unbindPlayService();
        System.out.println("myMusicListFragment   onPause....");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mainActivity.playService.play(position);
    }

    //回调播放状态下的UI设置
    public void changeUIStatusOnPlay(int position) {
        if (position >= 0 && position < mp3Infos.size()) {
            Mp3Info mp3Info = mp3Infos.get(position);
            textView_songName.setText(mp3Info.getTitle());
            textView2_singer.setText(mp3Info.getArtist());


            if (mainActivity.playService.isPlaying()){
                imageView2_play_pause.setImageResource(R.mipmap.app_pause);
            }else {
                imageView2_play_pause.setImageResource(R.mipmap.app_play);
            }



            Bitmap albumBitmap = MediaUtils.getArtwork(mainActivity, mp3Info.getId(), mp3Info.getAlbumId(), true, true);
            imageView_album.setImageBitmap(albumBitmap);
            this.position=position;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView2_play_pause:
            {
                if (mainActivity.playService.isPlaying()) {
                    imageView2_play_pause.setImageResource(R.mipmap.app_play);
                    mainActivity.playService.pause();
                } else {
                    if (mainActivity.playService.isPause()) {
                        imageView2_play_pause.setImageResource(R.mipmap.app_pause);
                        mainActivity.playService.start();
                    } else {
                        mainActivity.playService.play(mainActivity.playService.getCurrentPosition());
                    }
                }
                break;
            }
            case R.id.imageView3_next:
                mainActivity.playService.next();
                break;
            case R.id.imageView_album:{
                Intent intent=new Intent(mainActivity,PlayActivity.class);
                intent.putExtra("isPause",isPause);
                startActivity(intent);
                break;}
            default:
                break;
        }

    }
}
