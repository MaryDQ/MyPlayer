package com.mlxphone.myplayer.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.mlxphone.myplayer.application.MyplayerApp;
import com.mlxphone.myplayer.R;
import com.mlxphone.myplayer.service.PlayService;
import com.mlxphone.myplayer.utils.MediaUtils;
import com.mlxphone.myplayer.vo.Mp3Info;

import java.util.ArrayList;

public class PlayActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private TextView textView1_title;
    private ImageView imageView1_album, imageView1_play_mode, imageView1_next, imageView2_play_pause, imageView3_previous, imageView1_favorite;
    private SeekBar seekBar1;
    private ViewPager viewPager;
    private TextView textView1_start_time, textView1_end_time;
    private ArrayList<Mp3Info> mp3Infos;
    private boolean isPause = false;
    private static final int UPDATE_TIME = 0x1;//更新播放时间标记
    private MyplayerApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        textView1_title = (TextView) findViewById(R.id.textView1_title);
        textView1_end_time = (TextView) findViewById(R.id.textView1_end_time);
        textView1_start_time = (TextView) findViewById(R.id.textView1_start_time);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        imageView1_album = (ImageView) findViewById(R.id.imageView_album);
        imageView1_next = (ImageView) findViewById(R.id.imageView1_next);
        imageView2_play_pause = (ImageView) findViewById(R.id.imageView2_play_pause);
        imageView3_previous = (ImageView) findViewById(R.id.imageView3_previous);
        imageView1_play_mode = (ImageView) findViewById(R.id.imageView1_play_mode);
        imageView1_favorite = (ImageView) findViewById(R.id.imageView1_favorite);

        imageView2_play_pause.setOnClickListener(this);
        imageView3_previous.setOnClickListener(this);
        imageView1_play_mode.setOnClickListener(this);
        imageView1_next.setOnClickListener(this);
        mp3Infos = MediaUtils.getMp3Infos(this);
        seekBar1.setOnSeekBarChangeListener(this);
        imageView1_favorite.setOnClickListener(this);
        bindPlayService();

        /*viewPager=(ViewPager)(findViewById(R.id.viewPager);
        initViewPager();*/

        myHandler = new MyHandler(this);
        isPause = getIntent().getBooleanExtra("isPause", false);

        app = (MyplayerApp) getApplication();
    }

    /*private void initViewPager(){
        View album_image_layout=getLayoutInflater().inflate(R.layout.album_image_layout,null);
        imageView1_album=(ImageView)album_image_layout.findViewById(R.id.imageView1_album);
        textView1_title=(TextView)album_image_layout.findViewById(R.id.textView1_title);
        views.add(album_image_layout);
        views.add(getLayoutInflater().inflate(R.layout.lrc_layout,null));
        viewPager.setAdapter(new MainActivity.MyPagerAdapter());


    }*/

    /*@Override
    public void onPageScrolled(int position,float positionOffset,int positionOffsetPixels){

    }

    @Override
    public void onPageSelected(int position){

    }

    @Override
    public void onPageScrollStateChanged(int state){

    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindPlayService();
    }

    private static MyHandler myHandler;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            playService.pause();
            playService.seekTo(progress);
            playService.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    static class MyHandler extends Handler {
        private PlayActivity playActivity;

        public MyHandler(PlayActivity playActivity) {
            this.playActivity = playActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (playActivity != null) {
                switch (msg.what) {
                    case UPDATE_TIME:
                        playActivity.textView1_start_time.setText(MediaUtils.formatTime(msg.arg1));
                        break;
                }
            }
        }
    }


    @Override
    public void publish(int progress) {
        //textView1_start_time.setText(MediaUtils.formatTime(progress));
        seekBar1.setProgress(progress);
        Message msg = myHandler.obtainMessage(UPDATE_TIME);
        msg.arg1 = progress;
        myHandler.sendMessage(msg);
    }

    @Override
    public void change(int position) {
//        if (this.playService.isPlaying()) {
        Mp3Info mp3Info = mp3Infos.get(position);
        textView1_title.setText(mp3Info.getTitle());

        textView1_end_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
        seekBar1.setProgress(0);
        seekBar1.setMax((int) mp3Info.getDuration());
        if (playService.isPlaying()) {
            imageView2_play_pause.setImageResource(R.mipmap.app_pause);
        } else {
            imageView2_play_pause.setImageResource(R.mipmap.app_play);
        }

        switch (playService.getPlay_mode()) {
            case PlayService.ORDER_PLAY:
                imageView1_play_mode.setImageResource(R.mipmap.order_play);
                imageView1_play_mode.setTag(PlayService.ORDER_PLAY);
                break;
            case PlayService.SINGLE_PLAY:
                imageView1_play_mode.setImageResource(R.mipmap.single_play);
                imageView1_play_mode.setTag(PlayService.SINGLE_PLAY);
                break;
            case PlayService.RANDOM_PLAY:
                imageView1_play_mode.setImageResource(R.mipmap.random_play);
                imageView1_play_mode.setTag(PlayService.RANDOM_PLAY);
                break;
            default:
                break;
        }

            //imageView1_album.setImageResource(R.mipmap.app_logo);
        //Bitmap albumBitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
        //imageView1_album.setImageBitmap(albumBitmap);

        //初始化收藏状态
        try {
            Mp3Info LikeMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId","=",mp3Info.getId()));
            if (LikeMp3Info!=null){
                imageView1_favorite.setImageResource(R.mipmap.favorite1);
            }else {
                imageView1_favorite.setImageResource(R.mipmap.favorite);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView2_play_pause: {
                if (playService.isPlaying()) {
                    imageView2_play_pause.setImageResource(R.mipmap.app_play);
                    playService.pause();

                } else {
                    if (playService.isPause()) {
                        imageView2_play_pause.setImageResource(R.mipmap.app_pause);
                        playService.start();
                    } else {
                        playService.play(playService.getCurrentPosition());
                    }
                }
                break;
            }
            case R.id.imageView1_next: {
                playService.next();
                break;
            }
            case R.id.imageView3_previous: {
                playService.prev();
                break;
            }
            case R.id.imageView1_play_mode: {
                int mode = (int) imageView1_play_mode.getTag();
                switch (mode) {
                    case PlayService.ORDER_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.random_play);
                        imageView1_play_mode.setTag(PlayService.RANDOM_PLAY);
                        playService.setPlay_mode(PlayService.RANDOM_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.random_play), Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.RANDOM_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.single_play);
                        imageView1_play_mode.setTag(PlayService.SINGLE_PLAY);
                        playService.setPlay_mode(PlayService.SINGLE_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.single_play), Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.SINGLE_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.order_play);
                        imageView1_play_mode.setTag(PlayService.ORDER_PLAY);
                        playService.setPlay_mode(PlayService.ORDER_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.order_play), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
            }
            case R.id.imageView1_favorite: {
                Mp3Info mp3Info = mp3Infos.get(playService.getCurrentPosition());
                System.out.println(mp3Info.getId());
                try {

                    Mp3Info LikeMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId","=",mp3Info.getId()));
                    System.out.println(LikeMp3Info);
                    if (LikeMp3Info == null) {
                        mp3Info.setMp3InfoId(mp3Info.getId());
                        System.out.println(mp3Info);
                        app.dbUtils.save(mp3Info);
                        System.out.println("save");
                        imageView1_favorite.setImageResource(R.mipmap.favorite1);
                    } else {
                        app.dbUtils.deleteById(Mp3Info.class, LikeMp3Info.getId());
                        System.out.println("delete");
                        imageView1_favorite.setImageResource(R.mipmap.favorite);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            }
            default:
                break;
        }
    }

   /* class MyPagerAdapter extends PagerAdapter{
        @Override
        public int getCount() {
             return views.size();
        }

        //实例化选项卡
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v=views.get(position);
            container.addView(v);
            return v;
        }

        //删除选项卡
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get((position)));
        }

        //判断视图是否为返回的对象
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==0;
        }
    }*/


}
