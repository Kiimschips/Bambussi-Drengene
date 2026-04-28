package com.example.bambussi;

import android.media.MediaPlayer;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class BaseMusicActivity extends AppCompatActivity {
    protected static boolean isMuted = false;
    protected MediaPlayer mediaPlayer;
    private LoudnessEnhancer enhancer;
    private int currentResId;

    public void startMusic(int resId) {
        currentResId = resId;
        if (isMuted) return;

        releaseMusic();

        mediaPlayer = MediaPlayer.create(this, resId);
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(1.0f, 1.0f);
            
            // Tilføj lyd-boost (Loudness Enhancer)
            try {
                enhancer = new LoudnessEnhancer(mediaPlayer.getAudioSessionId());
                enhancer.setTargetGain(2000); // Giver et kraftigt boost
                enhancer.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    protected void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            releaseMusic();
        } else {
            startMusic(currentResId);
        }
    }

    private void releaseMusic() {
        if (enhancer != null) {
            enhancer.release();
            enhancer = null;
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Vi kalder ikke startMusic her automatisk, 
        // da hver skærm selv vælger hvilken sang den vil spille.
    }
}