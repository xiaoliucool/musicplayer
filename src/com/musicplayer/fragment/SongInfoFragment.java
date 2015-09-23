package com.musicplayer.fragment;

import java.util.List;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.musicplayer.R;
import com.musicplayer.activity.PlayingActivity;
import com.musicplayer.adapter.CommonAdapter;
import com.musicplayer.adapter.ViewHolder;
import com.musicplayer.model.Song;
import com.musicplayer.utils.AudioUtil;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xiaoliucool
 * @date 2015年9月17日 下午5:09:42
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class SongInfoFragment extends Fragment {

	private CommonAdapter<Song> listAdapter;
	private View songInfo;
	private SwipeMenuListView songList;
	private List<Song> songs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		songs = AudioUtil.getAllSongs(getActivity());
		songInfo = inflater.inflate(R.layout.song_fragment, container, false);
		songList = (SwipeMenuListView) songInfo.findViewById(R.id.song_list);
		listAdapter = new CommonAdapter<Song>(getActivity(), songs,
				R.layout.song_item) {

			@Override
			public void setView(ViewHolder holder, Song item) {
				TextView songName = holder.getViewFromItem(R.id.song_name);
				TextView songSinger = holder.getViewFromItem(R.id.song_singer);
				songName.setText(item.getTitle());
				songSinger.setText(item.getSinger());
			}
		};
		songList.setMenuCreator(initMenu());
		songList.setAdapter(listAdapter);
		songList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(getActivity(),PlayingActivity.class);
				intent.putExtra("id", position);
				intent.putExtra("isFromPre", true);
				startActivity(intent);

			}
		});
		songList.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		    @Override
		    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
		        switch (index) {
		        case 0:
		            Toast.makeText(getActivity(), "啊...啊...用力..", Toast.LENGTH_SHORT).show();
		            break;
		        case 1:
		        	Toast.makeText(getActivity(), "再加把劲...用力..", Toast.LENGTH_SHORT).show();
		            break;
		        }
		        // false : close the menu; true : not close the menu
		        return false;
		    }
		});

		Log.i("musicplayer", "songInfoFragment 控件初始化完毕");
		return songInfo;
	}

	private SwipeMenuCreator initMenu() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
				// set item background
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				// set item width

				openItem.setWidth(dp2px(getActivity(), 90));
				// set item title
				openItem.setTitle("播放");
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(getActivity(), 90));
				//set item title and color
				deleteItem.setTitle("删除");
				deleteItem.setTitleSize(18);
				deleteItem.setTitleColor(Color.WHITE);
				// set a icon
				// deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}

		};
		return creator;
	}

	private int dp2px(Context context, float dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, context.getResources().getDisplayMetrics());
	}
}
