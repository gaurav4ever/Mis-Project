package com.example.gauravpc.mis;

import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.IOException;

public class mainbody extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    MediaPlayer mp;
    RelativeLayout playOrPauseLayout;
    ImageView playOrPauseImg,lyricsImage,TemplesImage,mainImage;
    TextView songTotalDurationLabel,songCurrentDurationLabel,infoTextView;
    int playOrPauseFlag=0,stopFlag=0;
    private SeekBar songProgressBar;
    private Handler mHandler = new Handler();
    private Utilities utils;
    Animation Fade_in,Fade_out;
    int images[]={R.drawable.one,R.drawable.two,R.drawable.three,R.drawable.four};
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainbody);

        mp = MediaPlayer.create(getApplicationContext(), R.raw.song2);
        utils = new Utilities();
        playOrPauseLayout=(RelativeLayout)findViewById(R.id.playOrPause);
        playOrPauseImg=(ImageView)findViewById(R.id.playOrPauseImg);
        songProgressBar = (SeekBar) findViewById(R.id.seekBar);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        playOrPauseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
                        // Changing button image to play button
                        playOrPauseImg.setImageResource(R.drawable.play);
                        mHandler.postDelayed(mUpdateTimeTask, 100);
                    }
                } else {
                    // Resume song
                    if (mp != null) {
                        mp.start();
                        // Changing button image to pause button
                        songProgressBar.setProgress(0);
                        songProgressBar.setMax(100);

                        // Updating progress bar
                        updateProgressBar();
                        playOrPauseImg.setImageResource(R.drawable.pause);
                    }
                }
            }
        });
        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            public void onBufferingUpdate(MediaPlayer mp, int percent) {

                Log.e("onBufferingUpdate", "" + percent);

            }
        });
        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);


        mainImage=(ImageView)findViewById(R.id.img);
        mHandler.post(updateTextRunnable);

        lyricsImage=(ImageView)findViewById(R.id.lyricsImg);
        lyricsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mainbody.this,Lyrics.class);
                startActivity(intent);
            }
        });
        TemplesImage=(ImageView)findViewById(R.id.templesImage);
        TemplesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mainbody.this,temples.class);
                startActivity(intent);
            }
        });
    }

    Runnable updateTextRunnable=new Runnable(){
        public void run() {
            if(count==images.length){
                count=0;
                mainImage.setImageResource(images[count]);
            }else{
                mainImage.setImageResource(images[count]);
                count++;
            }
            mHandler.postDelayed(this, 3000);
        }
    };
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();



            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar

    }

    /**
     * When user stops moving the progress handler
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playOrPauseImg.setImageResource(R.drawable.play);
    }
}
