package com.example.videoplay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.DefaultEventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class ExoPlayer extends AppCompatActivity {

    private PlayerView mPlayerView;
    private com.google.android.exoplayer2.ExoPlayer player;
    private Uri uri;
    private String uri1;
    private AppCompatSeekBar mExoSeekBar;
    private TextView mTvStartPos, mTvEndPos;

    private ImageButton mBtnExoControl;

    private static final String TAG = "ExoPlayer1";
    private long playbackPosition;
    private int currentWindow;
    private boolean playReady;
    private Handler handler;
    private Runnable updatePlayer;

    private long delay = 500;
    boolean a = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);

        mBtnExoControl = findViewById(R.id.exoControlBtn);
        mExoSeekBar = findViewById(R.id.exoSeekBar);
        mTvStartPos = findViewById(R.id.exoStartposition);
        mTvEndPos = findViewById(R.id.exoEndposition);
//        mBtnClick = findViewById(R.id.btnClick);

        mPlayerView = findViewById(R.id.exoPlayerView);
        Intent intent = getIntent();
        uri1 = intent.getStringExtra("path");
        Log.d(TAG, "onCreate: " + uri1);

        handler = new Handler();
        handler.removeCallbacks(updatePlayer);


    }

    private void seekBarPosition() {
        mExoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress());
            }
        });
    }

    private void updateThePlayer() {
        updatePlayer = new Runnable() {
            @Override
            public void run() {

                @SuppressLint("DefaultLocale") String totDur = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(player.getDuration()),
                        TimeUnit.MILLISECONDS.toMinutes(player.getDuration()) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(player.getDuration())), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(player.getDuration()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.getDuration())));
                @SuppressLint("DefaultLocale") String curDur = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(player.getCurrentPosition()),
                        TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition()) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(player.getCurrentPosition())), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(player.getCurrentPosition()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition())));

                mTvStartPos.setText(curDur);
                mTvEndPos.setText(totDur);
                mExoSeekBar.setMax((int) player.getDuration());
                mExoSeekBar.setProgress((int) player.getCurrentPosition());
                handler.postDelayed(updatePlayer, delay);
            }
        };
    }


    private void playerEventListener() {
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int state) {

                playReady = playWhenReady;

                switch (state) {
                    case 1:
                        break;

                    case 2:
                        break;

                    case 3:
                        break;

                    case 4:
                        mBtnExoControl.setImageResource(R.drawable.exo_play);
                        player.seekTo(0);
                        player.setPlayWhenReady(false);
                        break;
                }
            }

        });
}

    private void exoControls() {
        mBtnExoControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (playReady) {
                    player.setPlayWhenReady(false);
                    mBtnExoControl.setImageResource(R.drawable.exo_play);
                } else {
                    player.setPlayWhenReady(true);
                    mBtnExoControl.setImageResource(R.drawable.exo_pause);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("activity", "onStart: ");
        initailizePlayer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
        Log.d("activity", "onResume: ");

        if (player != null) {
            updateThePlayer();
            seekBarPosition();
            initailizePlayer();

        } else {
            handler.removeCallbacks(updatePlayer);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("activity", "onPause: "+player.getCurrentPosition());
        releasePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("activity", "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("activity", "onDestroy: ");
        player.release();
        player = null;
    }

    private void initailizePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        mPlayerView.setPlayer(player);
        Log.d("position", "releasePlayer: "+playbackPosition);
        player.seekTo(playbackPosition);
        Log.d("position", "releasePlayer: "+playbackPosition);
        exoControls();
        playerEventListener();
//        Log.d(TAG, "initailizePlayer: " + playReady);


        handler.postDelayed(updatePlayer, delay);

        uri = Uri.fromFile(new File(uri1));
        String playerinfo = Util.getUserAgent(getApplicationContext(), "exoPlayerinfo");
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), playerinfo);
        MediaSource mediaSourceUri = buildMediaSourceUri(uri, dataSourceFactory);
        Log.d(TAG, "initailizePlayer: " + player.getCurrentPosition());
        player.prepare(mediaSourceUri, true, false);
    }

    private MediaSource buildMediaSourceUri(Uri uri, DefaultDataSourceFactory dataSourceFactory) {
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true);
        return new ExtractorMediaSource.Factory(dataSourceFactory).setExtractorsFactory(extractorsFactory).createMediaSource(uri);
    }

    private void hideSystemUi() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void releasePlayer() {
        if (player != null) {
            handler.removeCallbacks(updatePlayer);
            currentWindow = player.getCurrentWindowIndex();
            playReady = player.getPlayWhenReady();
            Log.d(TAG, "releasePlayer: " + playReady);
            player.release();
            player = null;

        }
    }
}
