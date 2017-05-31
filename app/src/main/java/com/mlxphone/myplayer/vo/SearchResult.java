package com.mlxphone.myplayer.vo;

/**
 * Created by MLXPHONE on 2016/6/20 0020.
 * 搜索音乐的对象
 */
public class SearchResult {
    private String musicName;
    private String url;
    private String artist;
    private String album;

    public String getMusicName() {
        return musicName;
    }

    public String getUrl() {
        return url;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public String toString() {
        return "SearchResult [musicName="+musicName+"url="+url+
        ",artist="+artist+",album="+album+"]";
    }
}
