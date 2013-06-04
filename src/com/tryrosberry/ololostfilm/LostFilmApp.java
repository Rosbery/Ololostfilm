package com.tryrosberry.ololostfilm;


import android.app.Application;
import android.util.DisplayMetrics;

import com.tryrosberry.ololostfilm.debug.DebugHandler;
import com.tryrosberry.ololostfilm.logic.storage.Settings;
import com.tryrosberry.ololostfilm.utils.Notifications;

public class LostFilmApp extends Application {

    public static final String TAG = "LostFilm";

    public static final boolean DEBUGGING = true;
    public static final boolean USER_DEBUGGING = !DEBUGGING;

    private static LostFilmApp instance;
    private Settings mSettings;
    private DisplayMetrics mDisplay;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mDisplay = getResources().getDisplayMetrics();
        if(USER_DEBUGGING)Thread.setDefaultUncaughtExceptionHandler(new DebugHandler(instance,"bezjamper@gmail.com"));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static LostFilmApp getInstance() {
        return instance;
    }

    public final Settings getSettings() {
        if (mSettings == null) mSettings = new Settings(this);
        return mSettings;
    }

    private Notifications notifications = null;
    /**
     * Get notifications manager
     */
    public final Notifications getNotifications() {
        if (notifications == null) notifications = new Notifications(this);
        return notifications;
    }

    public DisplayMetrics getMetrics(){
        return mDisplay;
    }

}
