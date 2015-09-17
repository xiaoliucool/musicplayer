package com.musicplayer.fragment;

import com.musicplayer.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *@author xiaoliucool
 *@date 2015年9月17日 下午5:21:10
 *@version 1.0
 *Copyright 2015 xiaoliu 
 *All     right    reserved
 */
public class SingerInfoFragment extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View singerInfo = inflater.inflate(R.layout.singer_fragment, container, false);
		return singerInfo;
	}
}
