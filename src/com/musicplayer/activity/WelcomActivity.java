package com.musicplayer.activity;

import com.musicplayer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

/**
 *@author xiaoliucool
 *@date 2015年9月16日 下午3:58:22
 *@version 1.0
 *Copyright 2015 xiaoliu 
 *All     right    reserved
 */
public class WelcomActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					Intent intent = new Intent(WelcomActivity.this,MainActivity.class);
					startActivity(intent);
					finish();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
