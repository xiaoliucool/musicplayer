package com.musicplayer.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.musicplayer.model.Song;

/**
 * ��Ƶ�ļ���ȡ������
 *
 * @author xiaoliucool
 * @date 2015��9��17�� ����3:16:51
 * @version 1.0 Copyright 2015 xiaoliu All right reserved
 */
public class AudioUtil {
	/**
	 * ��ȡ����sd���ϵ���Ƶ�ļ���Ϣ ͨ��ʹ�������ṩ�ߣ����ʹ������ݣ����е���Ƶ��Ϣ��ϵͳ������뵽һ��sqlite���ݿ�
	 * 
	 * @param context
	 * @return
	 */
	public static List<Song> getAllSongs(Context context) {
		List<Song> songs = null;
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.YEAR,
						MediaStore.Audio.Media.MIME_TYPE,
						MediaStore.Audio.Media.SIZE,
						MediaStore.Audio.Media.DATA },
				MediaStore.Audio.Media.MIME_TYPE + "=? or "
						+ MediaStore.Audio.Media.MIME_TYPE + "=?",
				new String[] { "audio/mpeg", "audio/x-ms-wma" }, null);
		
		songs = new ArrayList<Song>();

		if (cursor.moveToFirst()) {

			Song song = null;

			do {
				song = new Song();
				// �ļ���
				song.setFileName(cursor.getString(1));
				// ������
				song.setTitle(cursor.getString(2));
				// ʱ��
				song.setDuration(cursor.getInt(3));
				// ������
				song.setSinger(cursor.getString(4));
				// ר����
				song.setAlbum(cursor.getString(5));
				// ���
				if (cursor.getString(6) != null) {
					song.setYear(cursor.getString(6));
				} else {
					song.setYear("δ֪");
				}
				// ������ʽ
				if ("audio/mpeg".equals(cursor.getString(7).trim())) {
					song.setType("mp3");
				} else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
					song.setType("wma");
				}
				// �ļ���С
				if (cursor.getString(8) != null) {
					float size = cursor.getInt(8) / 1024f / 1024f;
					song.setSize((size + "").substring(0, 4) + "M");
				} else {
					song.setSize("δ֪");
				}
				// �ļ�·��
				if (cursor.getString(9) != null) {
					song.setFileUrl(cursor.getString(9));
				}
				songs.add(song);
			} while (cursor.moveToNext());

			cursor.close();

		}
		return songs;
	}
}
