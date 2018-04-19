package com.l1d000.musicplayer;

/**
 * Created by lidongzhou on 18-2-13.
 */

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.l1d000.androidbox.R;
import com.l1d000.musicplayer.files.PlayerConstants;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HTC-M MainActivity";
    private Toolbar main_toolbar;

    private RecyclerView list_view;               // 音乐列表

    private ArrayList<Boolean> allSongs_state = new ArrayList<Boolean>();   // 标记所有音乐列表的状态
 //   private HtcSong current_song_OnService = null;                           // 标记当前正在musicService上播放的音乐
  //  private ProgressBar progressBar;
    private AppCompatImageView image_view_play_toggle;
    private ImageView image_view_logo;
    private TextView music_text_name;
    private TextView music_text_author;
    private boolean current_song_state = false;
    private RelativeLayout layout_root;
    private MediaBrowserAdapter musicPlayerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.music_player_activity_main);
        musicPlayerAdapter=new MediaBrowserAdapter(MainActivity.this);
        musicPlayerAdapter.addListener(new MediaBrowserInterface.MediaBrowserChangeListener() {
            @Override
            public void onConnected(@Nullable MediaControllerCompat mediaController) {

            }

            @Override
            public void onMetadataChanged(@Nullable MediaMetadataCompat mediaMetadata) {

            }

            @Override
            public void onPlaybackStateChanged(@Nullable PlaybackStateCompat playbackState) {

            }

            @Override
            public void onChildrenLoaded(@Nullable Boolean onloadStatus) {
              if(onloadStatus){
                //  loadMusicFiles();
              }
            }
        });

        findAllViewById();

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i("main","on create");
        if (list_view == null)
            list_view = (RecyclerView)findViewById(R.id.recycler_view);
        musicPlayerAdapter.onStart(musicPlayerAdapter, list_view);
      //  loadMusicFiles();
    //    musicPlayerAdapter.getTransportControls().play();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: MainActivity");
        musicPlayerAdapter.onStop();
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        handler.removeMessages((new Message()).what=1);
        this.finish();
    }

    // 获取所有的组件
    private void  findAllViewById(){
        main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        main_toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(main_toolbar);

        list_view = (RecyclerView)findViewById(R.id.recycler_view);
    //    progressBar = (ProgressBar)findViewById(R.id.progress_bar);

        image_view_play_toggle = (AppCompatImageView) findViewById(R.id.image_view_play_toggle);
        music_text_name = (TextView) findViewById(R.id.music_text_view_name);
        music_text_author = (TextView) findViewById(R.id.music_text_view_artist);
        layout_root = (RelativeLayout) findViewById(R.id.layout_root);
        image_view_logo =(ImageView)findViewById(R.id.image_view_logo);
  //      mMediaSessionManager.MediaSessionInitMainActivity(image_view_play_toggle);
        Message msg = new Message();
        msg.what = PlayerConstants.LOGO_ANIMATION_2;
        handler.sendMessage(msg);
    }







    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            //旋转动画：围绕x轴旋转
            switch (msg.what) {

                case PlayerConstants.LOGO_ANIMATION_1:

             /*       //旋转动画：围绕x轴旋转
                    ObjectAnimator animator = ObjectAnimator.ofFloat(image_view_logo, "rotationX", 0, 360, 0);
                    animator.setDuration(3000);
                    animator.start();
                    message.what = 2;
                    handler.sendMessageDelayed(message,10000);*/
                    break;

                case PlayerConstants.LOGO_ANIMATION_2:
                    //旋转动画:围绕z轴旋转
                    int  val = (int)(Math.random()*4+2);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(image_view_logo, "rotation", 0, 360*val);
                    animator.setDuration(3000);
                    animator.start();
                    Message message = new Message();
                    message.what = PlayerConstants.LOGO_ANIMATION_2;
                    handler.sendMessageDelayed(message,10000);
                    break;
                default:
                    break;
            }
        }

    };


}
