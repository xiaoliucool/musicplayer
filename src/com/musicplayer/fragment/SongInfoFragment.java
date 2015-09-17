package com.musicplayer.fragment;

import java.util.List;

import com.musicplayer.R;
import com.musicplayer.adapter.CommonAdapter;
import com.musicplayer.adapter.ViewHolder;
import com.musicplayer.model.Song;
import com.musicplayer.utils.AudioUtil;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 *@author xiaoliucool
 *@date 2015年9月17日 下午5:09:42
 *@version 1.0
 *Copyright 2015 xiaoliu 
 *All     right    reserved
 */
public class SongInfoFragment extends Fragment{
	
	private CommonAdapter< Song> listAdapter;
	private View songInfo;
	private ListView songList;
	private List<Song> songs;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		songs = AudioUtil.getAllSongs(getActivity());
		songInfo = inflater.inflate(R.layout.song_fragment, container, false);
		songList = (ListView) songInfo.findViewById(R.id.song_list);
		listAdapter = new CommonAdapter<Song>(getActivity(), songs, R.layout.song_item) {

			@Override
			public void setView(ViewHolder holder, Song item) {
				TextView songName = holder.getViewFromItem(R.id.song_name);
				TextView songSinger = holder.getViewFromItem(R.id.song_singer);
				songName.setText(item.getTitle());
				songSinger.setText(item.getSinger());
			}
		};
		songList.setAdapter(listAdapter);
		songList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}
		});
		Log.i("musicplayer", "songInfoFragment 控件初始化完毕");
		return songInfo;
	}
}
