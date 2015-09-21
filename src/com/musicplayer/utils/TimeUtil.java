package com.musicplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xiaoliucool
 * @date 2015年9月21日 下午4:03:00
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class TimeUtil {
	public static String msec2date(long time) {
		String newTime = "";
		Date date = new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"mm:ss");
		newTime = dateFormat.format(date);
		return newTime;
	}
}
