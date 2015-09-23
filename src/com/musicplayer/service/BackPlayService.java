package com.musicplayer.service;

import java.io.IOException;
import java.util.List;

import com.musicplayer.model.Song;
import com.musicplayer.utils.AudioUtil;

import android.R.integer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
	private IntentFilter filter;
	private SeekToReceiver receiver;
	private int cur;
	private boolean nextFromService;
	class SeekToReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//Log.i("musicplayer", "进度更新了");
			progress = intent.getIntExtra("seekTo", 0);
			//Log.i("musicplayer", "新进度："+ String.valueOf(progress));
			mediaPlayer.seekTo(progress);
		}
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		Log.i("musicplayer", "服务创建");
		songs = AudioUtil.getAllSongs(getApplication());
		filter = new IntentFilter();
		filter.addAction("com.xiaoliu.musicplayer");
		receiver = new SeekToReceiver();
		registerReceiver(receiver, filter);
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
		mediaPlayer.setOnCompletionListener(this);
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
		cur = intent.getIntExtra("id", 0);
		if (!isPlaying) {
			song = songs.get(cur);
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
			mediaPlayer.start();
			Log.i("musicplayer", "开始播放音乐");
		
		}

		else{
			mediaPlayer.pause();
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		if (mediaPlayer!=null) {
			mediaPlayer.release();
		}
		super.onDestroy();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Intent next = new Intent("com.xiaoliu.musicplayer.NEXT");
		sendBroadcast(next);
		playNext();
	}
	/**
	 * 播放下一首歌曲
	 */
	private void playNext(){
		if (cur+1>=songs.size()) {
			cur = 0;
		}else {
			cur++;
		}
		song = songs.get(cur);
		getSongUrl();
		initMediaPlayer(filePath);
		mediaPlayer.start();
	}

	public class AudioBinder extends Binder {
		public int getProgress(){
			return mediaPlayer.getCurrentPosition();
		}
		
		public boolean setNextByService(){
			return nextFromService;
		}
		// 返回Service对象
		public BackPlayService getService() {
			return BackPlayService.this;
		}
	}
}
