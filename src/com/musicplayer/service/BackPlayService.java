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
	//歌曲列表
	private List<Song> songs;
	//歌曲实体
	private Song song;
	//歌曲路径
	private String filePath;
	//判断歌曲是否在播放
	private boolean isPlaying ;
	private boolean isContinue;
	//媒体播放器
	private MediaPlayer mediaPlayer;
	//binder对象与相绑定的activity传递数据
	private final IBinder binder = new AudioBinder();
	//进度条的进度
	private int progress;
	//广播接收者，用来监听播放界面的进度条是否发生变化
	private IntentFilter filter;
	private SeekToReceiver receiver;
	private int cur;
	//判断下一首是自动播放的还是点击播放的
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
	/**
	 * 获取歌曲的url
	 */
	private void getSongUrl() {
		filePath = song.getFileUrl();
	}

	/**
	 * 初始化媒体播放器的状态
	 * 
	 * @param filePath
	 */
	private void initMediaPlayer(String filePath) {
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
		isContinue = intent.getBooleanExtra("isContinue", false);
		//当前播放的音乐的id
		cur = intent.getIntExtra("id", cur);
		//判断当前是否有音乐在播放，如果没有，则从头初始化，如果有，则暂停，与playMusic()方法结合完成
		if (!isPlaying) {
			if (mediaPlayer!=null) {
				mediaPlayer.release();
			}
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
	 * 开始播放音乐，媒体播放器没有播放就开始播放，如果在播放就暂停
	 */
	private void playMusic() {
		if (!mediaPlayer.isPlaying()) {
			mediaPlayer.start();
			Log.i("musicplayer", "开始播放音乐");
		}else{
			if (isContinue) {
				return ;
			}
			mediaPlayer.pause();
		}
	}

	@Override
	public void onDestroy() {
		//相关的释放工作
		unregisterReceiver(receiver);
		if (mediaPlayer!=null) {
			mediaPlayer.release();
		}
		super.onDestroy();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		//如果服务自己播放完了，就自动进入下一首，并通知界面跟新UI
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
	/**
	 * binder对象，与UI交互的桥梁
	 * @author Administrator
	 *
	 */
	public class AudioBinder extends Binder {
		/**
		 * 通知UI跟新进度条
		 * @return
		 */
		public int getProgress(){
			return mediaPlayer.getCurrentPosition();
		}
		/**
		 * 通知UI，后台自动进入下一首
		 * @return
		 */
		public boolean setNextByService(){
			return nextFromService;
		}
		/**
		 * 获得当前播放的音乐的id
		 * @return
		 */
		public int getCurSongId(){
			return cur;
		}
		// 返回Service对象
		public BackPlayService getService() {
			return BackPlayService.this;
		}
	}
}
