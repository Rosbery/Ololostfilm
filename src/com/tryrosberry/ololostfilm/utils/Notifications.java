package com.tryrosberry.ololostfilm.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.tryrosberry.ololostfilm.LostFilmApp;
import com.tryrosberry.ololostfilm.R;
import com.tryrosberry.ololostfilm.ui.activities.MainActivity;

public class Notifications {

    private LostFilmApp mApp;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotifBuilderComp;
    private Intent mMainIntent;

    public Notifications(LostFilmApp app) {
        mApp = app;
        mMainIntent = new Intent(mApp, MainActivity.class);
        init();
    }

    private void init() {
        mNotificationManager = (NotificationManager)mApp.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifBuilderComp = new NotificationCompat.Builder(mApp)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentText(mApp.getResources().getString(R.string.app_name))
                .setContentIntent(PendingIntent.getActivity(mApp, 0, mMainIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(mApp.getResources().getString(R.string.app_name));

    }

    /*public void createBufferingNotification(Event party){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mApp);
        mBuilder.setContentTitle(party.etitle)
                .setContentText("Buffering from soundcloud in progress")
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(mApp, 0, new Intent(), 0))
                .setProgress(0, 0, true);
        mNotificationManager.notify(1, mBuilder.build());
    }

    public void  createPlayerNotification(Event party){

        String title = party.etitle;
        String text = party.venue;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mApp);

        RemoteViews contentView = new RemoteViews(mApp.getPackageName(), R.layout.notification_player);
        Intent stopIntent = new Intent();
        contentView.setImageViewResource(R.id.notifyPlayerPlay, android.R.drawable.ic_media_pause);
        stopIntent.setAction(AudioPlayer.AUDIO_STOP_INTENT);
        contentView.setOnClickPendingIntent(R.id.notifyPlayerPlay, PendingIntent.getBroadcast(mApp, 0, stopIntent,0));
        contentView.setTextViewText(R.id.notifyPlayerAuthor, title);
        contentView.setTextViewText(R.id.notifyPlayerSongTitle, text);
        PendingIntent contentIntent = PendingIntent.getActivity(mApp, 0, new Intent(), 0);

        builder
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis())
                .setTicker(title + " " + text + " Party Mix is playing")
                .setContent(contentView);

        Notification not = builder.build();
        not.contentView = contentView;
        not.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_ONLY_ALERT_ONCE;
        mNotificationManager.notify(1, not);

    }*/

    public void remove() {
        remove(0);
    }

    public void remove(int id) {
        mNotificationManager.cancel(id);
    }

    private Notification getNotifications(){
        return mNotifBuilderComp.build();
    }


}
