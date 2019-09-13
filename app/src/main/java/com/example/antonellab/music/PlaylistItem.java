package com.example.antonellab.music;

/**
 * Created by AntonellaB on 30-Nov-16.
 */

public class PlaylistItem {
    public String songName;
    public boolean downloaded ;

    public PlaylistItem (String songName, boolean downloaded )
    {
        this.songName = songName ;
        this.downloaded = downloaded ;
    }

    public String getSongName()
    {
        return this.songName;
    }
}