package com.musicplayer.activity;

import com.musicplayer.R;

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
	private TextView allSong;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);
		allSong = (TextView) findViewById(R.id.all_song);
		allSong.setOnClickListener(this);
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
