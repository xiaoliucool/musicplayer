package com.musicplayer.activity;

import java.util.List;

import com.musicplayer.R;
import com.musicplayer.adapter.CommonAdapter;
import com.musicplayer.adapter.ViewHolder;
import com.musicplayer.fragment.SingerInfoFragment;
import com.musicplayer.fragment.SongInfoFragment;
import com.musicplayer.model.Song;
import com.musicplayer.utils.AudioUtil;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author xiaoliucool
 * @date 2015年9月17日 下午5:22:54
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class SongInfoActivity extends Activity implements OnClickListener{

	private TextView songInfo;
	private TextView singerInfo;
	
	private FragmentManager manager;
	
	private SongInfoFragment songFragment;
	private SingerInfoFragment singerFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.song_info);
		manager = getFragmentManager();
		init();
		songInfo.setOnClickListener(this);
		singerInfo.setOnClickListener(this);
		select(0);
	}

	private void init() {
		songInfo = (TextView) findViewById(R.id.song);
		singerInfo = (TextView) findViewById(R.id.singer);
		Log.i("musicplayer", "songInfo主页 控件初始化完毕");
	}

	private void select(int position) {
		FragmentTransaction transaction = manager.beginTransaction();
		hideFragments(transaction);
		switch (position){
		case 0:
			if (null == songFragment) {
				songFragment = new SongInfoFragment();
				transaction.add(R.id.content, songFragment);
			} else {
				transaction.show(songFragment);
			}
			break;
		case 1:
			if (null == singerFragment) {
				singerFragment = new SingerInfoFragment();
				transaction.add(R.id.content, singerFragment);
			} else {
				transaction.show(singerFragment);
			}
			break;

		}
		transaction.commit();
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (songFragment!=null) {
			transaction.hide(songFragment);
		}
		if (singerFragment!=null) {
			transaction.hide(singerFragment);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.song:
			select(0);
			break;

		case R.id.singer:
			select(1);
			break;
		}
		
	}
}
