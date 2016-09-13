package com.zun1.whenask.ToolsClass;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by zun1user7 on 2016/7/28.
 */
public class Player implements MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener{
    public MediaPlayer mediaPlayer;
    public Context mContext;
    public Player(){
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnCompletionListener(this);
        }
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
    }
    public void play(){
        mediaPlayer.start();
    }
    public void playUrl(String voiceUrl,Context context) throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(context,Uri.parse(voiceUrl));
//        mediaPlayer.prepare();
        mediaPlayer.prepareAsync();
        mediaPlayer.getDuration();
        Log.i("mediaPlayer.getDuration", String.valueOf(mediaPlayer.getDuration()));
    }
    public void pause(){
        mediaPlayer.pause();
    }
    public void stop(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //mediaPlayer.release();
        Log.e("mediaPlayer", "onCompletion");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
       play();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.i("xxxxxxxxxxxx", String.valueOf(extra));
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.i("xxxxxxxxxxxx", String.valueOf(extra));

                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.i("xxxxxxxxxxxx", String.valueOf(extra));
                break;
        }
        return false;
    }
}
