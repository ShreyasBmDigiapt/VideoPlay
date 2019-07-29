package com.example.videoplay;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Surface;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

public class PlayerEventListn implements VideoRendererEventListener, Player.EventListener {

    private Context context;
    private com.google.android.exoplayer2.ExoPlayer player;
    private static final String TAG = "PlayerEventListn11";
    boolean playerRed ;
    private ImageButton mBtnExoControl;

    public PlayerEventListn(Context context, ExoPlayer player, ImageButton mBtnExoControl) {
        this.context = context;
        this.player = player;
        this.mBtnExoControl = mBtnExoControl;
    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        Log.d(TAG, "onVideoSizeChanged: "+ width+"         "+ height);
        if (width >= height) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Alert!!!!!").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }

    }

    @Override
    public void onRenderedFirstFrame(@Nullable Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int state) {
        playerRed = playWhenReady;

        switch (state) {
            case 1:
                break;

            case 2:
                break;

            case 3:
                break;

            case 4:
//                Log.d("statee", "onPlayerStateChanged: " + ((int) player.getCurrentPosition() - (int) player.getDuration()) / 10);
//                        if ((( player.getCurrentPosition() -  player.getDuration()) / 10) == 0) {
//                            player.seekTo(0);
//                            mBtnExoControl.setImageResource(R.drawable.exo_play);
//                            player.setPlayWhenReady(false);
//                        }
                break;
        }
    }
}
