package com.musicplayer.activity;

import java.io.IOException;
import java.util.List;

import com.musicplayer.R;
import com.musicplayer.model.Song;
import com.musicplayer.service.BackPlayService;
import com.musicplayer.utils.AudioUtil;
import com.musicplayer.utils.TimeUtil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
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
		Intent service = new Intent(getApplication(), BackPlayService.class);
		service.putExtra("id", location);
		service.putExtra("isPlaying", false);
		startService(service);
		bindService(service, conn, BIND_AUTO_CREATE);
		playMusic();
		//handler.post(updateThread);
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
		switch (v.getId()) {
		case R.id.playing:
			if (!isPlaying) {
				Log.i("musicplayer", "暂停状态--》播放状态");
				Intent service = new Intent(getApplication(),
						BackPlayService.class);
				service.putExtra("isPlaying", true);
				unbindService(conn);
				startService(service);
				bindService(service, conn, BIND_AUTO_CREATE);
				
			} else {
				Log.i("musicplayer", "播放状态--》暂停状态");
				Intent service = new Intent(getApplication(),
						BackPlayService.class);
				service.putExtra("isPlaying", true);
				unbindService(conn);
				startService(service);
				bindService(service, conn, BIND_AUTO_CREATE);
			}
			playMusic();
			break;

		case R.id.playing_pre:
			if (location == 0) {
				location = songs.size() - 1;
			} else {
				location--;
			}
			getSongInfo(location);
			setComponet();
			mediaPlayer.reset();
			initMediaPlayer(filePath);
			playMusic();
			break;
		case R.id.playing_next:
			if (location == songs.size() - 1) {
				location = 0;
			} else {
				location++;
			}
			getSongInfo(location);
			setComponet();
			mediaPlayer.reset();
			initMediaPlayer(filePath);
			playMusic();
			break;
		}

	}

	/**
	 * 开始播放音乐
	 */
	private void playMusic() {
		if (!isPlaying) {
			isPlaying = true;
			playingButton.setBackgroundResource(R.drawable.pause_button);
			// mediaPlayer.start();
			// Log.i("musicplayer", "开始播放音乐");
			// handler.post(updateThread);

		} else {
			isPlaying = false;
			playingButton.setBackgroundResource(R.drawable.play_button);
			// mediaPlayer.pause();
			// handler.removeCallbacks(updateThread);
		}
	}

	@Override
	public void onBackPressed() {
		handler.removeCallbacks(updateThread);
		// Intent intent = new Intent(this, BackPlayService.class);
		// startService(intent);
		// bindService(intent, conn, BIND_AUTO_CREATE);
		// finish();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// fromUser判断是用户改变的滑块的值
		if (fromUser == true) {
			mediaPlayer.seekTo(progress);
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
