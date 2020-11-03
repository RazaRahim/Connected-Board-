package com.razarahim.connectedboard.ui;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

import com.razarahim.connectedboard.Utils.ApplicationClass;

import static android.content.Context.DOWNLOAD_SERVICE;


/**
 * Created by AliAh on 03/05/2018.
 */

public class DownloadFile {

    private static long downloadID;

    private DownloadFile() {
    }

    public static void fromUrl(String Url,String filename) {

        DownloadManager downloadManager = (DownloadManager) ApplicationClass.getInstance().getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(Url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        Long referene = downloadManager.enqueue(request);
    }


}
