package com.musicplayer.activity;

import java.io.IOException;
import java.util.List;

import com.musicplayer.R;
import com.musicplayer.model.Song;
import com.musicplayer.service.BackPlayService;
import com.musicplayer.utils.AudioUtil;
import com.musicplayer.utils.TimeUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * @author xiaoliucool
 * @date 2015年9月20日 下午5:20:01
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class PlayingActivity extends Activity implements OnClickListener,
		OnSeekBarChangeListener {
	//播放界面上的各类控件
	private TextView songText;
	private TextView singerText;
	private TextView albumText;

	private ImageView albumPic;

	private ImageButton preButton;
	private ImageButton playingButton;
	private ImageButton nextButton;

	private SeekBar seekBar;
	private TextView playTimeText;
	private TextView songTimeText;
	
	//媒体播放器
	private MediaPlayer mediaPlayer;
	
	//用来判断播放按钮的状态
	private boolean isPlaying;
	
	//歌曲实体类各成员
	private String songName;
	private String singerName;
	private String albumName;
	private String filePath;
	private Bitmap albumP;
	private int songDuration;
	//歌曲的列表
	private List<Song> songs;
	//歌曲的id，与之前的listview有关，也用来控制进、退
	private int location;
	//判断是否是由listview点击进入到的播放界面
	private boolean isFromPreActivity;
	//判断home键是否被触发，通过广播接收者来确定
	private boolean isWatchingHomeKey = true;
	//判断back键是否被触发
	private boolean isFromBackPress = true;
	//音频控制器
	private AudioManager audioManager;
	//判断下一首歌曲是由后台自己进入的
	private boolean nextFromService;
	
	//处理进度条的同步
	private Handler handler = new Handler();
	private Runnable updateThread = new Runnable() {
		public void run() {
			// Log.i("子线程开始执行", "更新进度条");
			// 获得歌曲现在播放位置并设置成播放进度条的值
			seekBar.setProgress(playBinder.getProgress());
			playTimeText.setText(TimeUtil.msec2date(seekBar.getProgress()));
			// 每次延迟100毫秒再启动线程
			handler.postDelayed(updateThread, 100);
			// Log.i("子线程延迟100ms执行", "更新进度条");
		}
	};
	//处理与服务的绑定，主要是处理后台与播放界面的内容同步问题，与上面的handler相结合使用
	private BackPlayService.AudioBinder playBinder;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			playBinder = (BackPlayService.AudioBinder) service;
			Log.i("musicplayer", "成功返回binder对象");
			if (playBinder!=null) {
				Log.i("musicplayer", "重置UI");
				location = playBinder.getCurSongId();
				getSongInfo(location);
				setComponet();
				setPlayButton();
			}
			handler.post(updateThread);
		}
	};
	//处理各种广播事件，通过动态绑定的方式来实现
	private IntentFilter filter;
	private HomeWatcherReciever receiver;

	class HomeWatcherReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//处理home键的广播事件
			isWatchingHomeKey = false;
			Log.i("musicplayer", "监听到home键");
			//处理服务发出的next广播
			if (intent.getAction().equals("com.xiaoliu.musicplayer.NEXT")) {
				Log.i("musicplayer", "监听到歌曲播完了，切换到下一首");
				if (location + 1 >= songs.size()) {
					location = 0;
				} else {
					location++;
				}
				getSongInfo(location);
				setComponet();
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("musicplayer", "onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.song_play);
		songs = AudioUtil.getAllSongs(getApplication());
		Intent intent = getIntent();
		location = intent.getIntExtra("id", 0);
		//设置广播的接收类型：home键以及服务自定义广播
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		filter.addAction("com.xiaoliu.musicplayer.NEXT");
		receiver = new HomeWatcherReciever();
		registerReceiver(receiver, filter);
		//判断当前是否有音乐在播放
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		init();
		//数据恢复
		//recoverInstanceState(savedInstanceState);
	}
	//数据暂时保存，home键会触发，但是back键不会触发
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("location", location);
		outState.putBoolean("backpress", false);
		Log.i("musicplayer", "保存数据成功");
	}
	//恢复数据
	protected void recoverInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			Log.i("musicplayer", "开始恢复数据");
			location = savedInstanceState.getInt("location");
			isFromBackPress = savedInstanceState.getBoolean("backpress");
			getSongInfo(location);
			setComponet();
			Intent service = new Intent(getApplication(), BackPlayService.class);
			service.putExtra("id", location);
			service.putExtra("isPlaying", true);
			startService(service);
			bindService(service, conn, BIND_AUTO_CREATE);
			isPlaying = false;
			setPlayButton();
		}
	}
	@Override
	protected void onStart() {
		Log.i("musicplayer", "onStart");
		isFromPreActivity = getIntent().getBooleanExtra("isFromPre", false);
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.i("musicplayer", "onResume");
		super.onResume();
		//前一个页面触发
		if (isFromPreActivity && isWatchingHomeKey && isFromBackPress) {
			getSongInfo(location);
			setComponet();
			Intent service = new Intent(getApplication(), BackPlayService.class);
			service.putExtra("id", location);
			service.putExtra("isPlaying", false);
			startService(service);
			bindService(service, conn, BIND_AUTO_CREATE);
			isPlaying = false;
			setPlayButton();
		}
		if (!isFromPreActivity) {
			Intent service = new Intent(getApplication(), BackPlayService.class);
			service.putExtra("isPlaying", true);
			service.putExtra("isContinue", true);
			startService(service);
			bindService(service, conn, BIND_AUTO_CREATE);
			getSongInfo(location);
			setComponet();
			isPlaying = false;
			setPlayButton();
		}
	}

	/**
	 * 显示控件内容
	 */
	private void setComponet() {
		songText.setText(songName);
		singerText.setText(singerName);
		albumText.setText(albumName);
		albumPic.setImageBitmap(albumP);
		seekBar.setMax(songDuration);
		songTimeText.setText(TimeUtil.msec2date(songDuration));
		playingButton.setBackgroundResource(R.drawable.pause_button);
		isPlaying = false;
	}

	/**
	 * 初始化选中的歌曲信息 从数据库读取
	 */
	private void getSongInfo(int position) {
		Song song = songs.get(position);
		songName = song.getTitle();
		singerName = song.getSinger();
		albumName = song.getAlbum();
		filePath = song.getFileUrl();
		albumP = song.getAlbumPic();
		songDuration = song.getDuration();
	}

	/**
	 * 初始化控件，并设置点击事件
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
		playTimeText = (TextView) findViewById(R.id.play_time);
		songTimeText = (TextView) findViewById(R.id.song_time);
		playingButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		preButton.setOnClickListener(this);
		seekBar.setOnSeekBarChangeListener(this);
	}
	
	@Override
	public void onClick(View v) {
		Intent service = new Intent(getApplication(), BackPlayService.class);
		switch (v.getId()) {
		//播放按钮
		case R.id.playing:
			service.putExtra("isPlaying", true);
			Log.i("musicplayer", "播放状态--》暂停状态");
			setPlayButton();
			break;
		//前一首
		case R.id.playing_pre:
			if (location - 1 < 0) {
				location = songs.size() - 1;
			} else {
				location--;
			}
			service.putExtra("isPlaying", false);
			getSongInfo(location);
			setComponet();
			break;
		//后一首
		case R.id.playing_next:
			if (location + 1 >= songs.size()) {
				location = 0;
			} else {
				location++;
			}
			service.putExtra("isPlaying", false);
			getSongInfo(location);
			setComponet();
			break;
		}
		service.putExtra("id", location);
		//开启服务--》绑定服务
		startService(service);
		bindService(service, conn, BIND_AUTO_CREATE);
	}

	/**
	 * 设置播放按键的状态
	 */
	private void setPlayButton() {
		if (!isPlaying) {
			//没有播放
			isPlaying = true;
			playingButton.setBackgroundResource(R.drawable.pause_button);
		} else {
			isPlaying = false;
			playingButton.setBackgroundResource(R.drawable.play_button);
		}
	}
	@Override
	protected void onPause() {
		Log.i("musicplayer", "onPause");
		super.onPause();
	}
	@Override
	protected void onStop() {
		Log.i("musicplayer", "onStop");
		super.onStop();
	}
	@Override
	public void onBackPressed() {
		//back键触发后，停止更新进度条
		handler.removeCallbacks(updateThread);
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		Log.i("musicplayer", "onDestroy");
		super.onDestroy();
		//取消绑定服务
		if (conn!=null) {
			unbindService(conn);
		}
		//解除注册
		unregisterReceiver(receiver);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// fromUser判断是用户改变的滑块的值
		if (fromUser == true) {
			Log.i("musicplayer", "进度条拖动了，开始发送广播");
			Intent broadcast = new Intent("com.xiaoliu.musicplayer");
			broadcast.putExtra("seekTo", progress);
			sendBroadcast(broadcast);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}
}
