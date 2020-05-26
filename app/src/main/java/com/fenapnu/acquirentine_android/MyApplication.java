package com.fenapnu.acquirentine_android;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;


public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {


    private static MyApplication sInstance;

    public static MyApplication getContext() {
        return sInstance;
    }

    public MyApplication() {
        sInstance = this;
    }

    public static MyApplication get() {
        return sInstance;
    }


    //Foreground / Background State change variables
    public boolean background = false;
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;


    public boolean getBackground(){return background;}

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(this);

        sInstance = this;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            this.background = false;
        }
    }


    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            this.background = true;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
//    synchronized public Tracker getDefaultTracker() {
//        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
//        if (sTracker == null) {
//            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
//        }
//
//        return sTracker;
//    }

}
