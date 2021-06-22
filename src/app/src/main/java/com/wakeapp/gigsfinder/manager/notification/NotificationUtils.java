package com.wakeapp.gigsfinder.manager.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.wakeapp.gigsfinder.candidate.dashboard.Nokri_CandidateDashboardActivity;
import com.wakeapp.gigsfinder.employeer.dashboard.Nokri_EmployeerDashboardActivity;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.guest.dashboard.Nokri_GuestDashboardActivity;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class NotificationUtils {
    private static String TAG = NotificationUtils.class.getSimpleName();
//    SettingsMain settingsMain;
    private Context mContext;

    NotificationUtils(Context mContext) {
        this.mContext = mContext;
  //      settingsMain = new SettingsMain(mContext);
    }

    /**
     * Method checks if the app is in background or not
     */
//    public static boolean isAppIsInBackground(Context context) {
//        boolean isInBackground = true;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
//            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    for (String activeProcess : processInfo.pkgList) {
//                        if (activeProcess.equals(context.getPackageName())) {
//                            isInBackground = false;
//                        }
//                    }
//                }
//            }
//        } else {
//            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            ComponentName componentInfo = taskInfo.get(0).topActivity;
//            if (componentInfo.getPackageName().equals(context.getPackageName())) {
//                isInBackground = false;
//            }
//        }
//
//        return isInBackground;
//    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    void showNotificationMessage(final String title, final String message, final Date timeStamp, String notification_id, String imageURL) {
//        if (TextUtils.isEmpty(message))
//            return;

        // notification icon
        PendingIntent resultPendingIntent = null;
        final int icon = R.drawable.logo;
            if (Nokri_Utils.isAppinBackground(mContext)) {
                Intent in = null;
                if(Nokri_SharedPrefManager.isAccountCandidate(mContext)) {
                    in = new Intent(mContext, Nokri_CandidateDashboardActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                else if(Nokri_SharedPrefManager.isAccountEmployeer(mContext)) {
                    in = new Intent(mContext, Nokri_EmployeerDashboardActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                else if(Nokri_SharedPrefManager.isAccountPublic(mContext)) {
                    in = new Intent(mContext, Nokri_GuestDashboardActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                //Intent in = new Intent(mContext, HomeActivity.class);
                //settingsMain.setNotificationTitle(title);
                Nokri_SharedPrefManager.saveFirebaseNotificationTitle(title,mContext);
                //settingsMain.setNotificationMessage(message);
                Nokri_SharedPrefManager.saveFirebaseNotificationMessage(message,mContext);
                resultPendingIntent = PendingIntent.getActivity(mContext, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);

          }
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext);
            if (!TextUtils.isEmpty(imageURL)) {

                if (imageURL != null && imageURL.length() > 4 && Patterns.WEB_URL.matcher(imageURL).matches()) {

                    Bitmap bitmap = getBitmapFromURL(imageURL);

                    if (bitmap != null) {
                        showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, notification_id);
                    } else {
                        showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, notification_id);
                    }
                }
            } else {
                showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, notification_id);
            }

    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, Date timeStamp, PendingIntent resultPendingIntent, String ad_ID) {

        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();

        inboxStyle.bigText(message);
//        inboxStyle.addLine(message);

        Notification notification;

     if(!Nokri_Utils.isAppinBackground(mContext)){

         notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                 .setAutoCancel(true)
                 .setContentTitle(title)

                 .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setStyle(inboxStyle)
                 .setWhen(timeStamp.getTime())
                 .setSmallIcon(R.drawable.logo)
                 .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                 .setContentText(message)
//                .setContentInfo("contentinfo")
//                .setSubText(message)
                 .build();
     }

     else { Log.d("tagggg","not running");
         notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                 .setAutoCancel(true)
                 .setContentTitle(title)
                 .setContentIntent(resultPendingIntent)
                 .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setStyle(inboxStyle)
                 .setWhen(timeStamp.getTime())
                 .setSmallIcon(R.drawable.logo)
                 .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                 .setContentText(message)
//                .setContentInfo("contentinfo")
//                .setSubText(message)
                 .build();
     }
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(ad_ID), notification);
        Nokri_Globals.SHOULD_HOW_FIREBASE_NOTIFICATION = true;
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title,
                                     String message, Date timeStamp, PendingIntent resultPendingIntent, String notification_id) {

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);

        Notification notification;
        if(!Nokri_Utils.isAppinBackground(mContext))
        {
            Log.d("tagggg","running");
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)

                    .setStyle(bigPictureStyle)
                    .setWhen(timeStamp.getTime())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .build();

        }
        else { Log.d("tagggg","not running");
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setStyle(bigPictureStyle)
                    .setWhen(timeStamp.getTime())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .build();
        }
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(notification_id), notification);
        Nokri_Globals.SHOULD_HOW_FIREBASE_NOTIFICATION = true;
    }

    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    void playNotificationSound() {
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(mContext, defaultRingtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + mContext.getPackageName() + "/raw/notification");
//            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
//            r.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
