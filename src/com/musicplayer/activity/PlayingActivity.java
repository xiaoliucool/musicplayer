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

	private MediaPlayer mediaPlayer;
	private boolean isPlaying;
	private String songName;
	private String singerName;
	private String albumName;
	private String filePath;
	private Bitmap albumP;
	private int songDuration;

	private List<Song> songs;
	private int location;
	private boolean isFromPreActivity;
	private boolean isWatchingHomeKey = true;
	private boolean isFromBackPress = true;
	private AudioManager audioManager;
	private boolean nextFromService;

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
	private BackPlayService.AudioBinder playBinder;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			playBinder = (BackPlayService.AudioBinder) service;
			Log.i("musicplayer", "成功返回binder对象");
			handler.post(updateThread);
		}
	};
	private IntentFilter filter;
	private HomeWatcherReciever receiver;

	class HomeWatcherReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			isWatchingHomeKey = false;
			Log.i("musicplayer", "监听到home键");
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

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.song_play);
		songs = AudioUtil.getAllSongs(getApplication());
		Intent intent = getIntent();
		location = intent.getIntExtra("id", 0);
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		filter.addAction("com.xiaoliu.musicplayer.NEXT");
		receiver = new HomeWatcherReciever();
		registerReceiver(receiver, filter);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		init();
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
			playMusic();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("location", location);
		outState.putBoolean("backpress", false);
		Log.i("musicplayer", "保存数据成功");
	}

	@Override
	protected void onStart() {
		isFromPreActivity = getIntent().getBooleanExtra("isFromPre", false);
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isFromPreActivity && isWatchingHomeKey && isFromBackPress) {
			getSongInfo(location);
			setComponet();
			Intent service = new Intent(getApplication(), BackPlayService.class);
			service.putExtra("id", location);
			service.putExtra("isPlaying", false);
			startService(service);
			bindService(service, conn, BIND_AUTO_CREATE);
			isPlaying = false;
			playMusic();
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
		playTimeText = (TextView) findViewById(R.id.play_time);
		songTimeText = (TextView) findViewById(R.id.song_time);
		playingButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		preButton.setOnClickListener(this);
		seekBar.setOnSeekBarChangeListener(this);
	}

	/**
	 * 初始化媒体播放器的状态
	 * 
	 * @param filePath
	 */
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
		Intent service = new Intent(getApplication(), BackPlayService.class);
		switch (v.getId()) {
		case R.id.playing:
			service.putExtra("isPlaying", true);
			Log.i("musicplayer", "播放状态--》暂停状态");
			playMusic();
			break;
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
		startService(service);
		bindService(service, conn, BIND_AUTO_CREATE);
	}

	/**
	 * 开始播放音乐
	 */
	private void playMusic() {
		if (!isPlaying) {
			isPlaying = true;
			playingButton.setBackgroundResource(R.drawable.pause_button);
		} else {
			isPlaying = false;
			playingButton.setBackgroundResource(R.drawable.play_button);
		}
	}

	@Override
	public void onBackPressed() {
		handler.removeCallbacks(updateThread);
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
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
