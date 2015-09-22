package com.musicplayer.activity;

import java.util.List;

import com.musicplayer.R;
import com.musicplayer.model.Song;
import com.musicplayer.utils.AudioUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

/**
 *@author xiaoliucool
 *@date 2015年9月16日 下午5:48:10
 *@version 1.0
 *Copyright 2015 xiaoliu 
 *All     right    reserved
 */
public class MainActivity extends Activity implements OnClickListener{
	private List<Song> songs ;
	private TextView allSong;
	private TextView localSongs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);
		songs = AudioUtil.getAllSongs(getApplication());
		init();
		setComponent();
		allSong.setOnClickListener(this);
	}
	private void init() {
		allSong = (TextView) findViewById(R.id.all_song);
		localSongs = (TextView) findViewById(R.id.local_song_nums);
	}
	private void setComponent(){
		allSong.setText(songs.size()+"首歌曲  >");
		localSongs.setText(songs.size()+"首歌曲  >");
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.all_song:
			Intent intent = new Intent(this, SongInfoActivity.class);
			startActivity(intent);
			break;
		}
		
	}
}
