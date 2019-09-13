package com.example.antonellab.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public static final String Playlist = "downloadPlaylist";
    public static final String Music = "downloadMusic";
    public static final int HTTP_OK = 200;

    public static final String downloadingState = "Download";
    public static final String playingState = "Play";

    public static ListView listView;
    public static Context appContext;
    public static ArrayList<String> localSongs;

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

//        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
//
//        // Start lengthy operation in a background thread
//        new Thread(new Runnable() {
//            public void run() {
//                while (mProgressStatus < 100) {
//                    //mProgressStatus = doWork();
//
//                    // Update the progress bar
//                    mHandler.post(new Runnable() {
//                        public void run() {
//                            mProgress.setProgress(mProgressStatus);
//                        }
//                    });
//                }
//            }
//        }).start();

//        Intent downloadIntent = new Intent (this , DownloadService.class );
//        downloadIntent.putExtra (" receiver ", new DownloadReceiver (new Handler ()));
//
//        downloadIntent.putExtra ("url ","http://10.0.2.2:8080/downloadMusic?basename =" + "happy_music.mp3" );
//        startService ( downloadIntent );
//
//        //interogare director public de pe storage-ul extern si creere acolo a unui director pentru melodii
//        File musicDirectory = new File( Environment . getExternalStoragePublicDirectory (Environment.DIRECTORY_MUSIC ), " music ");
//        if (! musicDirectory . exists ())
//            musicDirectory . mkdirs ();
//        File musicFile = new File ( musicDirectory , "happy_music.mp3" );

        listView = (ListView) findViewById(R.id.list_view);
        localSongs = listAllSongs();
        appContext = getBaseContext();
        Intent downloadIntent = new Intent(this, DownloadService.class);
        downloadIntent.putExtra("url", "http://192.168.0.192:8080/downloadPlaylist");
        downloadIntent.putExtra("method", Playlist);
        downloadIntent.putExtra("receiver", new DownloadReceiver(new Handler()));
        startService(downloadIntent);
    }

    public void songButton(View v) {
        String buttonText= (String) ((Button) v).getText();
        ViewParent linearLayout = v.getParent();
        TextView textView = (TextView) ((View) linearLayout).findViewById(R.id.songName);
        String songName= (String) textView.getText();
        if(buttonText.equals(downloadingState)){
            int position=(Integer)v.getTag();
            Intent downloadIntent = new Intent(this, DownloadService.class);
            downloadIntent.putExtra("url", "http://192.168.0.192:8080/downloadMusic?basename=" + songName);
            downloadIntent.putExtra("position", position);
            downloadIntent.putExtra("method", Music);
            downloadIntent.putExtra("songName", songName);
            downloadIntent.putExtra("receiver", new DownloadReceiver(new Handler()));
            startService(downloadIntent);
        }
        if(buttonText.equals(playingState)) {
//            Intent i=new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(this,"audio/*",file));
//            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivity(i);
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/music/") + songName);
            intent.setDataAndType(Uri.fromFile(file), "audio/*");
            startActivity(intent);

        }
    }
    public static void setListView(List<String> songs){
        ArrayList<PlaylistItem> playlist = new ArrayList<>();
        PlaylistItem item;
        for(String song : songs) {
            item = new PlaylistItem(song,false);
            playlist.add(item);
        }
        PlaylistAdapter adapter = new PlaylistAdapter(appContext, R.layout.afisare_playlist, playlist);
        listView.setAdapter(adapter);
    }
    public static void setProgressBar(int progress, int position){
        ProgressBar progressBar= (ProgressBar) listView.getChildAt(position).findViewById(R.id.progressBarID);
        progressBar.setProgress(progress);
        progressBar.setVisibility(View.VISIBLE);

        if(progress==100){
            Button songButton= (Button) listView.getChildAt(position).findViewById(R.id.songButton);
            songButton.setText(R.string.play_button);
        }
    }

    public ArrayList<String>listAllSongs(){
        File directory = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));
        File[] allFiles = directory.listFiles();
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < allFiles.length; i++)
        {
            list.add(allFiles[i].getName());
        }
        return list;
    }
}
