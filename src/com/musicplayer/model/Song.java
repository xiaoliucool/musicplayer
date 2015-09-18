package com.musicplayer.model;

import android.graphics.Bitmap;

/**
 * 歌曲实体类
 *@author xiaoliucool
 *@date 2015年9月17日 下午2:33:08
 *@version 1.0
 *Copyright 2015 xiaoliu 
 *All     right    reserved
 */
public class Song {
	//文件名
	private String fileName;
	//歌曲名
	private String title;
	//时长
	private int duration;
	//歌手
	private String singer;
	//所属专辑
	private String album;
	//年代
	private String year;
	//类型
	private String type;
	//大小
	private String size;
	//文件路径
	private String fileUrl;
	//专辑图片
	private Bitmap albumPic;

	public Bitmap getAlbumPic() {
		return albumPic;
	}

	public void setAlbumPic(Bitmap albumPic) {
		this.albumPic = albumPic;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public Song() {
		super();
	}

	public Song(String fileName, String title, int duration, String singer,
			String album, String year, String type, String size, String fileUrl) {
		this.fileName = fileName;
		this.title = title;
		this.duration = duration;
		this.singer = singer;
		this.album = album;
		this.year = year;
		this.type = type;
		this.size = size;
		this.fileUrl = fileUrl;
	}

	@Override
	public String toString() {
		return "Song [fileName=" + fileName + ", title=" + title
				+ ", duration=" + duration + ", singer=" + singer + ", album="
				+ album + ", year=" + year + ", type=" + type + ", size="
				+ size + ", fileUrl=" + fileUrl + "]";
	}

}
