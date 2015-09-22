package com.musicplayer.service;

import java.io.IOException;
import java.util.List;

import com.musicplayer.model.Song;
import com.musicplayer.utils.AudioUtil;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * @author xiaoliucool
 * @date 2015年9月22日 下午3:57:47
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class BackPlayService extends Service implements OnCompletionListener {

	private List<Song> songs;
	private Song song;
	private String filePath;
	private boolean isPlaying ;
	private MediaPlayer mediaPlayer;
	private final IBinder binder = new AudioBinder();
	private int progress;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		Log.i("musicplayer", "服务创建");
		songs = AudioUtil.getAllSongs(getApplication());
		super.onCreate();
	}

	private void getSongUrl() {
		filePath = song.getFileUrl();
	}

	/**
	 * 初始化媒体播放器的状态
	 * 
	 * @param filePath
	 */
	private void initMediaPlayer(String filePath) {
		if (mediaPlayer!=null) {
			mediaPlayer.release();
		}
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
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("musicplayer", "服务开启了");
		isPlaying = intent.getBooleanExtra("isPlaying", false);
		if (!isPlaying) {
			song = songs.get(intent.getIntExtra("id", 0));
			getSongUrl();
			initMediaPlayer(filePath);
			playMusic();
		} else {
			playMusic();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 开始播放音乐
	 */
	private void playMusic() {

		if (!mediaPlayer.isPlaying()) {
			// isPlaying = true;
			mediaPlayer.start();
			Log.i("musicplayer", "开始播放音乐");
			// handler.post(updateThread);
		}

		else{
			// isPlaying = false;
			mediaPlayer.pause();
			// handler.removeCallbacks(updateThread);
		}
	}

	@Override
	public void onDestroy() {
		if (mediaPlayer!=null) {
			mediaPlayer.release();
		}
		super.onDestroy();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {

	}

	public class AudioBinder extends Binder {
		public int getProgress(){
			return mediaPlayer.getCurrentPosition();
		}
		// 返回Service对象
		public BackPlayService getService() {
			return BackPlayService.this;
		}
	}
}
