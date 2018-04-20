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

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;


import com.l1d000.musicplayer.files.AllSongs;
import com.l1d000.musicplayer.files.HtcSong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediaBrowserService extends MediaBrowserServiceCompat {

    private static final String TAG = MediaBrowserService.class.getSimpleName();

    private MediaSessionCompat mSession;
 //   private PlayerAdapter mPlayback;
  //  private MediaNotificationManager mMediaNotificationManager;
    private MediaSessionCallback mCallback;
    private boolean mServiceInStartedState;
    private static ArrayList<HtcSong> allsongs;
    private static MediaPlayer mediaPlayer = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        // Create a new MediaSession.
        mSession = new MediaSessionCompat(this, "MusicService");
        mCallback = new MediaSessionCallback();
        mSession.setCallback(mCallback);
        mSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mSession.getSessionToken());

        if(allsongs==null) {
            allsongs = (ArrayList<HtcSong>) AllSongs.getAllSongs(getBaseContext());

        }
 //       mMediaNotificationManager = new MediaNotificationManager(this);

 //       mPlayback = new MediaPlayerAdapter(this, new MediaPlayerListener());

        Log.d(TAG, "onCreate: MusicService creating MediaSession, and MediaNotificationManager");
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved: ");
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
    //    mMediaNotificationManager.onDestroy();
     //   mPlayback.stop();
        mSession.release();
        Log.d(TAG, "onDestroy: MediaPlayerAdapter stopped, and MediaSession released");
    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                                 int clientUid,
                                 Bundle rootHints) {
        Log.i(TAG, "onGetRoot: String = " + clientUid + "  clientUid = " + clientUid + "  rootHints = " + rootHints);
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(
            @NonNull final String parentMediaId,
            @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.i(TAG, "onLoadChildren: parentMediaId = " + parentMediaId + "  result = " + result);
        List<MediaBrowserCompat.MediaItem> songs = new ArrayList<>();


        Log.d(TAG, "updateMetaData");
        for(HtcSong song:allsongs) {

            MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.getId().toString())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getAuthor())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.getAlbum())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getDuration()).build();
            // .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getCoverBitmap(songInfo));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            }
            songs.add( new MediaBrowserCompat.MediaItem(
                    metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        if (mediaPlayer == null) mediaPlayer = new MediaPlayer();
        result.sendResult(songs);
    }

    // MediaSession Callback: Transport Controls -> MediaPlayerAdapter
    public class MediaSessionCallback extends MediaSessionCompat.Callback {
        private final List<MediaSessionCompat.QueueItem> mPlaylist = new ArrayList<>();
        private int mQueueIndex = -1;
        private MediaMetadataCompat mPreparedMedia;

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            mPlaylist.add(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mQueueIndex == -1) ? 0 : mQueueIndex;
            Log.i(TAG, "onAddQueueItem: description = " + description);
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            mPlaylist.remove(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mPlaylist.isEmpty()) ? -1 : mQueueIndex;
            Log.i(TAG, "onRemoveQueueItem: description = " + description);
        }

        @Override
        public void onPrepare() {
            Log.i(TAG, "onPrepare: ");
            if (mQueueIndex < 0 && mPlaylist.isEmpty()) {
                // Nothing to play.
                return;
            }

            final String mediaId = mPlaylist.get(mQueueIndex).getDescription().getMediaId();
     //       mPreparedMedia = MusicLibrary.getMetadata(MusicService.this, mediaId);
            mSession.setMetadata(mPreparedMedia);

            if (!mSession.isActive()) {
                mSession.setActive(true);
            }
        }

        @Override
        public void onPlay() {
            Log.i(TAG, "onPlay: ");
            mediaPlayer.start();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            Log.d(TAG, "Hello"+mediaId);
            mediaPlayer.reset();    // 可以使播放器从Error状态中恢复过来，重新会到Idle状态
            HtcSong song = allsongs.get(extras.getInt("song", 0));
            try {
                mediaPlayer.setDataSource(song.getMusicPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mediaPlayer.prepare();  // &#x51c6;&#x5907;(&#x540c;&#x6b65;)
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            super.onPlayFromMediaId(mediaId, extras);
        }

        @Override
        public void onPause() {
            Log.i(TAG, "onPause: ");
            mediaPlayer.pause();
      //      mPlayback.pause();
        }

        @Override
        public void onStop() {
            Log.i(TAG, "onStop: ");
      //      mPlayback.stop();
            mSession.setActive(false);
        }

        @Override
        public void onSkipToNext() {
            Log.i(TAG, "onSkipToNext: ");
            mQueueIndex = (++mQueueIndex % mPlaylist.size());
            mPreparedMedia = null;
            onPlay();
        }

        @Override
        public void onSkipToPrevious() {
            Log.i(TAG, "onSkipToPrevious: ");
            mQueueIndex = mQueueIndex > 0 ? mQueueIndex - 1 : mPlaylist.size() - 1;
            mPreparedMedia = null;
            onPlay();
        }

        @Override
        public void onSeekTo(long pos) {
            Log.i(TAG, "onSeekTo: pos " + pos);
    //        mPlayback.seekTo(pos);
        }

        private boolean isReadyToPlay() {
            Log.i(TAG, "isReadyToPlay: ");
            return (!mPlaylist.isEmpty());
        }
    }


}