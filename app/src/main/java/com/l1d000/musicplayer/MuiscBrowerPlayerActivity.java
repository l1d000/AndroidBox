package com.l1d000.musicplayer;

/**
 * Created by lidongzhou on 18-2-13.
 */

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.service.media.MediaBrowserService;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.l1d000.androidbox.R;
import com.l1d000.musicplayer.files.HtcSong;
import com.l1d000.musicplayer.files.PlayerConstants;
import com.l1d000.musicplayer.files.AllSongs;

import java.util.ArrayList;

public class MuiscBrowerPlayerActivity extends AppCompatActivity {

    private static final String TAG = "HTC-M MainActivity";
    private Toolbar main_toolbar;

    private RecyclerView recyclerView;               // 音乐列表
 //   private ArrayList<HtcSong> allSongs;              // 储存所有的音乐
    private ArrayList<Boolean> allSongs_state = new ArrayList<Boolean>();   // 标记所有音乐列表的状态
 //   private HtcSong current_song_OnService = null;                           // 标记当前正在musicService上播放的音乐
  //  private ProgressBar progressBar;
    private AppCompatImageView image_view_play_toggle;
    private ImageView image_view_logo;
    private TextView music_text_name;
    private TextView music_text_author;
    private boolean current_song_state = false;
    private RelativeLayout layout_root;
    private static ArrayList<HtcSong> allsongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.music_player_activity_main);
        if(allsongs==null) {
            allsongs = (ArrayList<HtcSong>) AllSongs.getAllSongs(MuiscBrowerPlayerActivity.this);

        }
        findAllViewById();
        loadMusicFiles();
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i("main","on create");

    }


    @Override
    protected  void onDestroy(){
        super.onDestroy();
 //       mMediaSessionManager.release();
  //      unbindService(connection);
        handler.removeMessages((new Message()).what=1);
        this.finish();
    }

    // 获取所有的组件
    private void  findAllViewById(){
       main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        main_toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(main_toolbar);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
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



    // 导入音乐文件
    private void loadMusicFiles(){
        MusicPlayerAdapter musicPlayerAdapter;   // 设置音乐列表中每一列的状态
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(musicPlayerAdapter=new MusicPlayerAdapter());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        musicPlayerAdapter.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(View view, int position)
            {

            }

            @Override
            public void onItemLongClick(View view, int position)
            {

            }
        });

    }

    // 自定义RecycleView内的点击事件(1)
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    class MusicPlayerAdapter extends RecyclerView.Adapter<MusicPlayerAdapter.MyMusicPlayerViewHolder>
    {
        // 自定义RecycleView内的点击事件(2)
        private OnItemClickListener mOnItemClickListener;

        // 自定义RecycleView内的点击事件(3)
        public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
        {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        @Override
        public MyMusicPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyMusicPlayerViewHolder holder = new MyMusicPlayerViewHolder(LayoutInflater.from(
                    MuiscBrowerPlayerActivity.this).inflate(R.layout.music_player_lists_itme, parent,
                    false));
            Log.i("main","hello");

            return holder;
        }

        @Override
        public void onBindViewHolder(final MyMusicPlayerViewHolder holder, int position)
        {

            HtcSong HtcSong = allsongs.get(position);
            holder.my_music_title.setText(HtcSong.getTitle());
            holder.my_music_author.setText(HtcSong.getAuthor());
            holder.my_music_duration.setText(AllSongs.formatTime(HtcSong.getDuration()));

            // 自定义RecycleView内的点击事件(4)
            // 如果设置了回调，则设置点击事件
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(holder.itemView, pos);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                        return false;
                    }
                });
            }

        }

        @Override
        public int getItemCount()
        {
            return allsongs.size();
        }


        class MyMusicPlayerViewHolder extends RecyclerView.ViewHolder
        {

            TextView my_music_title;
            TextView my_music_author;
            TextView my_music_duration;

            public MyMusicPlayerViewHolder(View view)
            {
                super(view);
                my_music_title = (TextView) view.findViewById(R.id.text_view_name);
                my_music_author = (TextView) view.findViewById(R.id.text_view_artist);
                my_music_duration = (TextView) view.findViewById(R.id.text_view_duration);
            }
        }


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
