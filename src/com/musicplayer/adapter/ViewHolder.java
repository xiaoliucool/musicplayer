package com.musicplayer.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author xiaoliucool
 * @date 2015��9��17�� ����3:34:16
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class ViewHolder {
	// �洢ListItem�е�View�ؼ�
	private SparseArray<View> itemViews;
	// ��ֹlistItem�����¼���,������Ҫ���صĲ����ļ���Ӧ��View����
	private View mConvertView;

	private ViewHolder(Context context, ViewGroup parent, int resouceId,
			int position) {
		itemViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(resouceId, parent,
				false);
		mConvertView.setTag(this);
	}
	/**
	 * ��ȡViewHolder�������еĲ�������ͨ����ִ��
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
	 * ����view����
	 * @return
	 */
	public View getmConvertView() {
		return mConvertView;
	}
	
	/**
	 * �����趨�Լ�һЩ���õķ���
	 * @param viewId
	 * @param data
	 */
	public void setText(int viewId, String text){
		TextView view = getViewFromItem(viewId);
		view.setText(text);
	}
}
