package com.wakeapp.gigsfinder;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;

import com.wakeapp.gigsfinder.manager.Nokri_AppLifeCycleManager;
import com.wakeapp.gigsfinder.utils.Nokri_LanguageSupport;

/**
 * Created by GlixenTech on 3/22/2018.
 */

public class Nokri_Application extends Application {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Nokri_AppLifeCycleManager.init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Nokri_LanguageSupport.onAttach(base, "en"));
        MultiDex.install(this);
    }
}
