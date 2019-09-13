package com.example.antonellab.music;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by AntonellaB on 30-Nov-16.
 */

class DownloadReceiver extends ResultReceiver { ;
    public static final String Result = "result";

    public DownloadReceiver (Handler handler ) {
        super ( handler );
//        Intent downloadIntent = new Intent();
//        downloadIntent.putExtra(" receiver ", new DownloadReceiver (new Handler ()));

    }

    @Override
    protected void onReceiveResult (int resultCode , Bundle resultData ) {
        super.onReceiveResult ( resultCode , resultData );

        if ( resultCode == DownloadService.UPDATE_PROGRESS )
        {
            // vom updata progressbar -ul
            MainActivity.setProgressBar(resultData.getInt("progress"), resultData.getInt("position"));
        }

        if ( resultCode == DownloadService.PLAYLIST_READY )
        {
            // vom popula ListView -ul cu lista de melodii
            MainActivity.setListView(resultData.getStringArrayList(Result));
        }
    }
}