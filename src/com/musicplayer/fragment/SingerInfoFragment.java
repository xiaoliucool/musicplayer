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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *@author xiaoliucool
 *@date 2015年9月17日 下午5:21:10
 *@version 1.0
 *Copyright 2015 xiaoliu 
 *All     right    reserved
 */
public class SingerInfoFragment extends Fragment{
	private View singerInfo;
	private CommonAdapter<Song> adapter;
	private GridView gridlist;
	private List<Song> datas;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		singerInfo = inflater.inflate(R.layout.singer_fragment, container, false);
		gridlist = (GridView) singerInfo.findViewById(R.id.gridview_singer);
		datas = AudioUtil.getAllSongs(getActivity());
		Log.i("musicplayer", "加载song数据成功");
		adapter = new CommonAdapter<Song>(getActivity(),datas,R.layout.grid_item) {

			@Override
			public void setView(ViewHolder holder, Song item) {
				ImageView albumPic = holder.getViewFromItem(R.id.singer_image);
				TextView singerName = holder.getViewFromItem(R.id.grid_singer_name);
				albumPic.setImageBitmap(item.getAlbumPic());
				singerName.setText(item.getSinger());
				Log.i("musicplayer", "专辑图片获取成功");
			}
		};
		gridlist.setAdapter(adapter);
		return singerInfo;
	}
}
