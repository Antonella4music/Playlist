package com.example.antonellab.music;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AntonellaB on 30-Nov-16.
 */
public class DownloadService extends IntentService {
    public static final String Result = "result";
    public static final int UPDATE_PROGRESS = 1000;
    public static final int PLAYLIST_READY = 2000;
    private ResultReceiver receiver ;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent ( Intent intent ) {
        String urlToDownload = intent.getStringExtra ("url");
        String method = intent.getStringExtra ("method").toString();
        receiver = ( ResultReceiver ) intent.getParcelableExtra ("receiver");
        List<String> allSongs = null;
        Bundle progressData = new Bundle();

        if(method.equals(MainActivity.Playlist)){
            allSongs = downloadPlaylist(urlToDownload);
            progressData.putStringArrayList(Result, (ArrayList<String>) allSongs);
            receiver.send(PLAYLIST_READY, progressData);
        } else if(method.equals(MainActivity.Music)) {
            int position= intent.getIntExtra("position", 0);
            String songName= intent.getStringExtra("songName");
            downloadMusic(urlToDownload, position, songName);
        }

            // se apeleaza ori downloadPlaylist ori downloadMusic in functie de ce url am primit in intent
//            downloadPlaylist("http://10.0.2.2:8080/downloadPlaylist");
//            downloadMusic("");
//            long downloadedSoFar = 0;
//            int count;
//            Bundle progressData = new Bundle ();
//            progressData.putInt(" progress " ,(int )( downloadedSoFar * 100 /fileLength ));
//            receiver.send ( UPDATE_PROGRESS , progressData );

    }

    private List downloadPlaylist ( String urlToDownload )
    {
        URL url = null;
        List<String> result = null;
        try {
            url = new URL(urlToDownload);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if ( urlConnection.getResponseCode() == MainActivity.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result = readBuffer(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return result;
    }
    private void downloadMusic(String urlToDownload, int position, String songName)
    {
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlToDownload);
            urlConnection = (HttpURLConnection) url.openConnection();
            if ( urlConnection.getResponseCode() == MainActivity.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                downloadSong(in, urlConnection, songName, position);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> readBuffer(InputStream in) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        List<String> output = new ArrayList<>();
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                output.add(line);
            }
            if (null != in) {
                in.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    private void downloadSong(InputStream in,HttpURLConnection connection, String songName, int position){
        OutputStream output= null;
        InputStream input= null;
        File musicDirectory = new File(Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_MUSIC ),"music");
        if (! musicDirectory.exists ())
            musicDirectory.mkdirs();
        File musicFile = new File ( musicDirectory , songName);
        int fileLength = connection.getContentLength();

        // download the file
        try{
            input = connection.getInputStream();
            output = new FileOutputStream(musicFile);

            byte chunk_size[] = new byte[4096]; //chunk_size in music.py = 4096
            long total_size = 0;
            int count_size;
            Bundle progressData = new Bundle();

            while ((count_size = input.read(chunk_size)) != -1) {
                total_size += count_size;
                // show the progress only if total length is known
                if (fileLength > 0) {
                    progressData.putInt("progress", (int) (total_size * 100 / fileLength));
                }
                progressData.putInt("position", position );
                output.write(chunk_size, 0, count_size);
                receiver.send(UPDATE_PROGRESS, progressData);

            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {}

            if (connection != null)
                connection.disconnect();
        }
    }

}