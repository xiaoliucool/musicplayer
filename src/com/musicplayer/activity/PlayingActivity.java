package com.musicplayer.activity;

import java.io.IOException;
import java.util.List;

import com.musicplayer.R;
import com.musicplayer.model.Song;
import com.musicplayer.utils.AudioUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 *@author xiaoliucool
 *@date 2015年9月20日 下午5:20:01
 *@version 1.0
 *Copyright 2015 xiaoliu 
 *All     right    reserved
 */
public class PlayingActivity extends Activity implements OnClickListener{
	
	private TextView songText;
	private TextView singerText;
	private TextView albumText;
	
	private ImageView albumPic;
	
	private ImageButton preButton;
	private ImageButton playingButton;
	private ImageButton nextButton;
	
	private SeekBar seekBar;
	
	private MediaPlayer mediaPlayer;
	private boolean isPlaying = false;
	private String songName;
	private String singerName;
	private String albumName;
	private String filePath;
	private Bitmap albumP;
	
	private List<Song> songs;
	private int location;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.song_play);
		songs = AudioUtil.getAllSongs(getApplication());
		Intent intent = getIntent();
		location = intent.getIntExtra("id", 0);
		init();
	}
	@Override
	protected void onResume() {
		super.onResume();
		getSongInfo(location);
		setComponet();
		initMediaPlayer(filePath);
	}
	private void setComponet() {
		songText.setText(songName);
		singerText.setText(singerName);
		albumText.setText(albumName);
		albumPic.setImageBitmap(albumP);
	}
	/**
	 * 初始化选中的歌曲信息
	 * 从数据库读取
	 */
	private void getSongInfo(int position) {
		Song song = songs.get(position);
		songName = song.getTitle();
		singerName = song.getSinger();
		albumName = song.getAlbum();
		filePath = song.getFileUrl();
		albumP = song.getAlbumPic();
	}
	/**
	 * 初始化控件
	 */
	private void init() {
		songText = (TextView) findViewById(R.id.playing_song_name);
		singerText = (TextView) findViewById(R.id.playing_song_singer);
		albumText = (TextView) findViewById(R.id.playing_song_album);
		albumPic = (ImageView) findViewById(R.id.playing_song_album_pic);
		preButton = (ImageButton) findViewById(R.id.playing_pre);
		playingButton = (ImageButton) findViewById(R.id.playing);
		nextButton = (ImageButton) findViewById(R.id.playing_next);
		seekBar = (SeekBar) findViewById(R.id.play_seekBar);
		playingButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		preButton.setOnClickListener(this);
	}
	private void initMediaPlayer(String filePath) {
		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
		} catch (IOException e) {
			Log.i("musicplayer", "歌曲路径出错了");
			e.printStackTrace();
		}

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.playing:
			if (!isPlaying) {
				if (!mediaPlayer.isPlaying()) {
					isPlaying = true;
					playingButton.setBackgroundResource(R.drawable.pause_button);
					mediaPlayer.start();
				}
			}else {
				if (mediaPlayer.isPlaying()) {
					isPlaying = false;
					playingButton.setBackgroundResource(R.drawable.play_button);
					mediaPlayer.pause();
				}
			}
			break;

		case R.id.playing_pre:
			if (location==0) {
				location=songs.size()-1;
			}else {
				location--;
			}
			getSongInfo(location);
			setComponet();
			mediaPlayer.reset();
			initMediaPlayer(filePath);
			break;
		case R.id.playing_next:
			if (location==songs.size()-1) {
				location=0;
			}else {
				location++;
			}
			getSongInfo(location);
			setComponet();
			mediaPlayer.reset();
			initMediaPlayer(filePath);
			break;
		}
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mediaPlayer!=null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
	}
}
