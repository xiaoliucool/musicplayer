package com.musicplayer.activity;

import java.util.List;

import com.musicplayer.R;
import com.musicplayer.model.Song;
import com.musicplayer.utils.AudioUtil;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;

/**
 *@author xiaoliucool
 *@date 2015年9月16日 下午3:58:22
 *@version 1.0
 *Copyright 2015 xiaoliu 
 *All     right    reserved
 */
public class WelcomActivity extends Activity{
	private AudioManager audioManager;
	private List<Song> songs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		final Intent intent = new Intent(getApplication(),MainActivity.class);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		songs = AudioUtil.getAllSongs(getApplication());
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (audioManager.isMusicActive()) {
						Intent intent = new Intent(getApplication(), PlayingActivity.class);
						startActivity(intent);
					}else {
						Thread.sleep(500);
						startActivity(intent);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	@Override
	protected void onStop() {
		finish();
		super.onStop();
	}
}
