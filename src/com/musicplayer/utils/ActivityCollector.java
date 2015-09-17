package com.musicplayer.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * 活动管理类
 * @author xiaoliucool
 * @date 2015年9月17日 下午4:09:57
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class ActivityCollector {
	
	private static List<Activity> collector = new ArrayList<Activity>();

	public static void add(Activity activity) {
		collector.add(activity);
	}

	public static void remove(Activity activity) {
		collector.remove(activity);
	}

	public static void finishAll() {
		if (!collector.isEmpty()) {
			for (Activity activity : collector) {
				if (!activity.isFinishing()) {
					activity.finish();
				}
			}
		}
	}
}
