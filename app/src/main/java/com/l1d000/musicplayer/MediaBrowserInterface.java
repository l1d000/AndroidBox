package com.l1d000.musicplayer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;

/**
 * Created by lidongzhou on 18-4-13.
 */

public class MediaBrowserInterface {


    // 自定义RecycleView内的点击事件(1)
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public interface MediaBrowserChangeListener {

        void onConnected(@Nullable MediaControllerCompat mediaController);
        void onMetadataChanged(@Nullable MediaMetadataCompat mediaMetadata) ;
        void onPlaybackStateChanged(@Nullable PlaybackStateCompat playbackState);
        void onChildrenLoaded(@Nullable Boolean onloadStatus);
    }

    public interface ListenerCommand {

        void perform(@NonNull MediaBrowserChangeListener listener);
    }
}
