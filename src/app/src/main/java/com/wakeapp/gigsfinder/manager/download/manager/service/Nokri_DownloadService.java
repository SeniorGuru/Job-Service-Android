package com.wakeapp.gigsfinder.manager.download.manager.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.URLUtil;

import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.manager.download.manager.model.Nokri_Download;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class Nokri_DownloadService extends IntentService {

    private NotificationCompat.Builder notifictionBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;
    private String baseUrl,fileName;
    public Nokri_DownloadService() {
        super("Nokri_DownloadService");
    }
    private File path;
    private ResultReceiver resultReceiver;
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Bundle bundle =  intent.getExtras();
        resultReceiver = (ResultReceiver) bundle.get("result");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        baseUrl = intent.getStringExtra("url");
        fileName = intent.getStringExtra("filename");
        if(URLUtil.isValidUrl(baseUrl)) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notifictionBuilder = new NotificationCompat.Builder(this).setContentTitle("Download").setContentText("Downloading File").setSmallIcon(R.drawable.logo).setAutoCancel(true);
            notificationManager.notify(0, notifictionBuilder.build());


            nokri_initDownload();
        }
        else{
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Nokri_ToastManager.showShortToast(getApplicationContext(), Nokri_Globals.INVALID_URL);
                }
            });
        }

        }

    private void nokri_initDownload() {
        try {
            URL url = new URL(baseUrl);
            String path = url.getFile().substring(0, url.getFile().lastIndexOf('/'));
            String base = url.getProtocol() + "://" + url.getHost() + path;
            Log.d("Logggggggg",base);
            Retrofit retrofit = new Retrofit.Builder().baseUrl(base).build();


            RestService restService = retrofit.create(RestService.class);
        Call<ResponseBody>myCall = restService.downloadFile(getDynamicUrl(url.getFile().lastIndexOf('/')+1,baseUrl));
            nokri_downloadFile(myCall.execute().body());
        } catch (IOException e) {

            e.printStackTrace();
        }
        catch (IllegalArgumentException ex){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Nokri_ToastManager.showShortToast(getApplicationContext(), Nokri_Globals.INVALID_URL);
                }
            });
        }
    }

    private String getBaseUrl(String url){

        return url.substring(0,url.indexOf(".com")+5);

    }
    private String getDynamicUrl(int index,String url)
    {
        return url.substring(index+4,url.length());
    }

    private void nokri_downloadFile(ResponseBody body) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        path = outputFile;
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;

        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Nokri_Download download = new Nokri_Download();
            download.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                nokri_sendNotification(download);
                timeCount++;
            }

            output.write(data, 0, count);
        }
        nokri_onDownloadComplete();
        output.flush();
        output.close();
        bis.close();

    }

    private void nokri_onDownloadComplete() {
        Nokri_Download download = new Nokri_Download();
        download.setProgress(100);


        Intent install = new Intent(Intent.ACTION_VIEW);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Uri apkURI = FileProvider.getUriForFile(
                    getBaseContext(),
                    getApplicationContext()
                            .getPackageName() + ".provider", new File(path.getAbsolutePath()));
            install.setDataAndType(apkURI, Nokri_Utils.getMimeType(path.getAbsolutePath()));
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        else {
            install.setDataAndType(Uri.fromFile(new File(path.getAbsolutePath())),

                    Nokri_Utils.getMimeType(path.getAbsolutePath()));
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,install,0);

        notificationManager.cancel(0);
        notifictionBuilder.setProgress(0,0,false);
        notifictionBuilder.setContentText("File Downloaded");
        notifictionBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(0, notifictionBuilder.build());
        Bundle bundle = new Bundle();
        bundle.putBoolean("state",true);
        bundle.putString("path",path.getAbsolutePath());
        if(resultReceiver!=null)
        resultReceiver.send(1000,bundle);
    }

    private void nokri_sendNotification(Nokri_Download download) {
        notifictionBuilder.setProgress(100,download.getProgress(),false);
        notifictionBuilder.setContentText("Downloading file "+ download.getCurrentFileSize() +"/"+totalFileSize +" MB");
        notificationManager.notify(0, notifictionBuilder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}



