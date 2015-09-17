package com.musicplayer.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author xiaoliucool
 * @date 2015年9月17日 下午3:34:16
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class ViewHolder {
	// 存储ListItem中的View控件
	private SparseArray<View> itemViews;
	// 防止listItem被重新加载,是整个要加载的布局文件对应的View对象
	private View mConvertView;

	private ViewHolder(Context context, ViewGroup parent, int resouceId,
			int position) {
		itemViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(resouceId, parent,
				false);
		mConvertView.setTag(this);
	}
	/**
	 * 获取ViewHolder对象，所有的操作均是通过其执行
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param resouceId
	 * @param position
	 * @return
	 */
	public static ViewHolder getViewHolder(Context context, View convertView,
			ViewGroup parent, int resouceId, int position) {
		if (convertView == null) {
			return new ViewHolder(context, parent, resouceId, position);
		} else {
			return (ViewHolder) convertView.getTag();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends View> T getViewFromItem(int ViewId){
		View view = itemViews.get(ViewId);
		if (view == null) {
			view = mConvertView.findViewById(ViewId);
			itemViews.put(ViewId, view);
		}
		return (T) view;
	}
	
	/**
	 * 返回view对象
	 * @return
	 */
	public View getmConvertView() {
		return mConvertView;
	}
	
	/**
	 * 可以设定自己一些常用的方法
	 * @param viewId
	 * @param data
	 */
	public void setText(int viewId, String text){
		TextView view = getViewFromItem(viewId);
		view.setText(text);
	}
}
