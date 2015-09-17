package com.musicplayer.adapter;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author xiaoliucool
 * @date 2015��9��17�� ����3:52:46
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
	// �������õ��ĳ�Ա
	private LayoutInflater inflater;
	private Context context;
	private List<T> datas;
	private int resourceId;

	public CommonAdapter(Context context, List<T> datas, int resourceId) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.datas = datas;
		this.resourceId = resourceId;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = ViewHolder.getViewHolder(context, convertView,
				parent, resourceId, position);
		setView(viewHolder, (T)getItem(position));
		return viewHolder.getmConvertView();
	}
	//��¶���ķ�������Ҫ�����Կؼ��������ò���
	public abstract void setView(ViewHolder holder, T item);
}
