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
 * @date 2015��9��20�� ����5:20:01
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class PlayingActivity extends Activity implements OnClickListener,
		OnSeekBarChangeListener {
	//���Ž����ϵĸ���ؼ�
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
	
	//ý�岥����
	private MediaPlayer mediaPlayer;
	
	//�����жϲ��Ű�ť��״̬
	private boolean isPlaying;
	
	//����ʵ�������Ա
	private String songName;
	private String singerName;
	private String albumName;
	private String filePath;
	private Bitmap albumP;
	private int songDuration;
	//�������б�
	private List<Song> songs;
	//������id����֮ǰ��listview�йأ�Ҳ�������ƽ�����
	private int location;
	//�ж��Ƿ�����listview������뵽�Ĳ��Ž���
	private boolean isFromPreActivity;
	//�ж�home���Ƿ񱻴�����ͨ���㲥��������ȷ��
	private boolean isWatchingHomeKey = true;
	//�ж�back���Ƿ񱻴���
	private boolean isFromBackPress = true;
	//��Ƶ������
	private AudioManager audioManager;
	//�ж���һ�׸������ɺ�̨�Լ������
	private boolean nextFromService;
	
	//�����������ͬ��
	private Handler handler = new Handler();
	private Runnable updateThread = new Runnable() {
		public void run() {
			// Log.i("���߳̿�ʼִ��", "���½�����");
			// ��ø������ڲ���λ�ò����óɲ��Ž�������ֵ
			seekBar.setProgress(playBinder.getProgress());
			playTimeText.setText(TimeUtil.msec2date(seekBar.getProgress()));
			// ÿ���ӳ�100�����������߳�
			handler.postDelayed(updateThread, 100);
			// Log.i("���߳��ӳ�100msִ��", "���½�����");
		}
	};
	//���������İ󶨣���Ҫ�Ǵ����̨�벥�Ž��������ͬ�����⣬�������handler����ʹ��
	private BackPlayService.AudioBinder playBinder;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			playBinder = (BackPlayService.AudioBinder) service;
			Log.i("musicplayer", "�ɹ�����binder����");
			if (playBinder!=null) {
				Log.i("musicplayer", "����UI");
				location = playBinder.getCurSongId();
				getSongInfo(location);
				setComponet();
				setPlayButton();
			}
			handler.post(updateThread);
		}
	};
	//������ֹ㲥�¼���ͨ����̬�󶨵ķ�ʽ��ʵ��
	private IntentFilter filter;
	private HomeWatcherReciever receiver;

	class HomeWatcherReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//����home���Ĺ㲥�¼�
			isWatchingHomeKey = false;
			Log.i("musicplayer", "������home��");
			//������񷢳���next�㲥
			if (intent.getAction().equals("com.xiaoliu.musicplayer.NEXT")) {
				Log.i("musicplayer", "���������������ˣ��л�����һ��");
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
		//���ù㲥�Ľ������ͣ�home���Լ������Զ���㲥
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		filter.addAction("com.xiaoliu.musicplayer.NEXT");
		receiver = new HomeWatcherReciever();
		registerReceiver(receiver, filter);
		//�жϵ�ǰ�Ƿ��������ڲ���
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		init();
		//���ݻָ�
		//recoverInstanceState(savedInstanceState);
	}
	//������ʱ���棬home���ᴥ��������back�����ᴥ��
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("location", location);
		outState.putBoolean("backpress", false);
		Log.i("musicplayer", "�������ݳɹ�");
	}
	//�ָ�����
	protected void recoverInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			Log.i("musicplayer", "��ʼ�ָ�����");
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
		//ǰһ��ҳ�津��
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
	 * ��ʾ�ؼ�����
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
	 * ��ʼ��ѡ�еĸ�����Ϣ �����ݿ��ȡ
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
	 * ��ʼ���ؼ��������õ���¼�
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
		//���Ű�ť
		case R.id.playing:
			service.putExtra("isPlaying", true);
			Log.i("musicplayer", "����״̬--����ͣ״̬");
			setPlayButton();
			break;
		//ǰһ��
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
		//��һ��
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
		//��������--���󶨷���
		startService(service);
		bindService(service, conn, BIND_AUTO_CREATE);
	}

	/**
	 * ���ò��Ű�����״̬
	 */
	private void setPlayButton() {
		if (!isPlaying) {
			//û�в���
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
		//back��������ֹͣ���½�����
		handler.removeCallbacks(updateThread);
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		Log.i("musicplayer", "onDestroy");
		super.onDestroy();
		//ȡ���󶨷���
		if (conn!=null) {
			unbindService(conn);
		}
		//���ע��
		unregisterReceiver(receiver);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// fromUser�ж����û��ı�Ļ����ֵ
		if (fromUser == true) {
			Log.i("musicplayer", "�������϶��ˣ���ʼ���͹㲥");
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
