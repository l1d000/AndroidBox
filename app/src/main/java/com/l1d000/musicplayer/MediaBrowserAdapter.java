/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.l1d000.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.l1d000.androidbox.R;
import com.l1d000.musicplayer.files.AllSongs;
import com.l1d000.musicplayer.files.HtcSong;

import java.util.ArrayList;
import java.util.List;


public class MediaBrowserAdapter extends RecyclerView.Adapter<MediaBrowserAdapter.MyMusicPlayerViewHolder> {
   private static ArrayList<HtcSong> allsongs;
    private Context mContext;
    private static final String TAG = MediaBrowserAdapter.class.getSimpleName();
    private final List<MediaBrowserInterface.MediaBrowserChangeListener> mListeners = new ArrayList<>();
    private MediaBrowserCompat mMediaBrowser;
    @Nullable
    private MediaControllerCompat mMediaController;
    private MediaBrowserInterface.OnItemClickListener mOnItemClickListener;
    private final MediaBrowserConnectionCallback mMediaBrowserConnectionCallback =
            new MediaBrowserConnectionCallback();
    private final MediaControllerCallback mMediaControllerCallback =
            new MediaControllerCallback();
    private final MediaBrowserSubscriptionCallback mMediaBrowserSubscriptionCallback =
            new MediaBrowserSubscriptionCallback();

    public MediaBrowserAdapter(Context mContext) {
        this.mContext = mContext;
        if(allsongs==null) {
            allsongs = (ArrayList<HtcSong>) AllSongs.getAllSongs(mContext);

        }
    }

    /*ListView adapter*/
    @Override
    public MyMusicPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyMusicPlayerViewHolder holder
                = new MyMusicPlayerViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.music_player_lists_itme, parent,
                false));
        Log.i("main","hello");

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyMusicPlayerViewHolder holder, int position) {
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


   /*MainActivity */
    public void setOnItemClickListener(MediaBrowserInterface.OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void onStart() {

        if (mMediaBrowser == null) {
            mMediaBrowser =
                    new MediaBrowserCompat(
                            mContext,
                            new ComponentName(mContext, MediaBrowserService.class),
                            mMediaBrowserConnectionCallback,
                            null);
            mMediaBrowser.connect();
        }
        Log.d(TAG, "onStart: Creating MediaBrowser, and connecting");
    }

    public void onStop() {
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mMediaControllerCallback);
            mMediaController = null;
        }
        if (mMediaBrowser != null && mMediaBrowser.isConnected()) {
            mMediaBrowser.disconnect();
            mMediaBrowser = null;
        }

        Log.d(TAG, "onStop: Releasing MediaController, Disconnecting from MediaBrowser");
    }

    public ArrayList<HtcSong> getAllSongs(){
        return allsongs;
    }

    public void addListener(MediaBrowserInterface.MediaBrowserChangeListener listener) {
        if (listener != null) {
            mListeners.add(listener);
        }
    }

    public void removeListener(MediaBrowserInterface.MediaBrowserChangeListener listener) {
        if (listener != null) {
            if (mListeners.contains(listener)) {
                mListeners.remove(listener);
            }
        }
    }

    public void performOnAllListeners(@NonNull MediaBrowserInterface.ListenerCommand command) {
        for (MediaBrowserInterface.MediaBrowserChangeListener  listener : mListeners) {
            if (listener != null) {
                try {
                    command.perform(listener);
                } catch (Exception e) {
                    removeListener(listener);
                }
            }
        }
    }


    public MediaControllerCompat.TransportControls getTransportControls() {
        if (mMediaController == null) {
            Log.d(TAG, "getTransportControls: MediaController is null!");
            throw new IllegalStateException();
        }
        return mMediaController.getTransportControls();
    }

    public int  getPlaybackState(){
        if (mMediaController.getPlaybackState() !=null) {
            Log.d(TAG, mMediaController.getPlaybackState().getState()+"");
            return mMediaController.getPlaybackState().getState();
        }else
            return 0;
    }

    /*class */
    public class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

        // Happens as a result of onStart().
        @Override
        public void onConnected() {
            try {
                // Get a MediaController for the MediaSession.
                mMediaController = new MediaControllerCompat(mContext,
                        mMediaBrowser.getSessionToken());
                mMediaController.registerCallback(mMediaControllerCallback);

                // Sync existing MediaSession state to the UI.
                mMediaControllerCallback.onMetadataChanged(
                        mMediaController.getMetadata());
                mMediaControllerCallback
                        .onPlaybackStateChanged(mMediaController.getPlaybackState());


            } catch (RemoteException e) {
                Log.d(TAG, String.format("onConnected: Problem: %s", e.toString()));
                throw new RuntimeException(e);
            }

            mMediaBrowser.subscribe(mMediaBrowser.getRoot(), mMediaBrowserSubscriptionCallback);
        }
    }

    public class MediaControllerCallback extends MediaControllerCompat.Callback {

        @Override
        public void onMetadataChanged(final MediaMetadataCompat metadata) {

        }

        @Override
        public void onPlaybackStateChanged(@Nullable final PlaybackStateCompat state) {
            Log.d(TAG,  "LIDONG"+state+"");
        }

        @Override
        public void onSessionDestroyed() {

        }


    }


    public class MediaBrowserSubscriptionCallback extends MediaBrowserCompat.SubscriptionCallback {

        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                                     @NonNull List<MediaBrowserCompat.MediaItem> children) {
            assert mMediaController != null;

        }
    }
}