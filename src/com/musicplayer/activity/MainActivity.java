package com.musicplayer.activity;

import com.musicplayer.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 *@author xiaoliucool
 *@date 2015��9��16�� ����5:48:10
 *@version 1.0
 *Copyright 2015 xiaoliu 
 *All     right    reserved
 */
public class MainActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);
	}
}
