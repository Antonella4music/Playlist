package com.example.antonellab.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by AntonellaB on 30-Nov-16.
 */

public class PlaylistAdapter extends ArrayAdapter< PlaylistItem > {
    public PlaylistAdapter (Context context , int resource, ArrayList< PlaylistItem > playlist ) {
        super ( context , resource, playlist );
    }

    public View getView (int position , View convertView , ViewGroup parent ) {
        // populam TextView din convertView cu numele melodiei din
        // PlaylistItem -ul de la pozitia " position "
        // in functie de valoarea atributului " download " vom ascunde
        // butonul sau ProgressBar -ul

        PlaylistItem item=getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(MainActivity.appContext).inflate(R.layout.afisare_playlist, parent, false);
        }
        TextView songName = (TextView) convertView.findViewById(R.id.songName);
        Button songButton = (Button) convertView.findViewById(R.id.songButton);
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBarID);
        songName.setText(item.getSongName());
        songButton.setTag(position);
        if(!MainActivity.localSongs.contains(item.getSongName())) {
            songButton.setText(MainActivity.downloadingState);
        } else {
            songButton.setText(MainActivity.playingState);
            progressBar.setProgress(100);
        }
        return convertView ;

    }
}