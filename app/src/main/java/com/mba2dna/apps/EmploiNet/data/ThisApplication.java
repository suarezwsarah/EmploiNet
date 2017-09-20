package com.mba2dna.apps.EmploiNet.data;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.utils.Tools;

import greco.lorenzo.com.lgsnackbar.LGSnackbarManager;
import greco.lorenzo.com.lgsnackbar.style.LGSnackBarThemeManager;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class ThisApplication extends Application {

    private static ThisApplication mInstance;
    //private Tracker tracker;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Constant.LOG_TAG, "onCreate : ThisApplication");
        mInstance = this;
        SQLiteHandler db=new SQLiteHandler(mInstance);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("nexalight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        LGSnackbarManager.prepare(getApplicationContext(),
                LGSnackBarThemeManager.LGSnackbarThemeName.SHINE);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2041047395483384~5100683751");
        //init photo loader
        Tools.initImageLoader(getApplicationContext());

        // activate analytics tracker
        // getGoogleAnalyticsTracker();
    }

    public static synchronized ThisApplication getInstance() {
        return mInstance;
    }


    /**
     * --------------------------------------------------------------------------------------------
     * For Google Analytics
     */
   /* public synchronized Tracker getGoogleAnalyticsTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.setDryRun(!AppConfig.ENABLE_ANALYTICS);
            tracker = analytics.newTracker(R.xml.app_tracker);
        }
        return tracker;
    }

    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();
        // Set screen title.
        t.setScreenName(screenName);
        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();
            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(new StandardExceptionParser(this, null).getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }*/
    public void trackEvent(String category, String action, String label) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.LEVEL, action);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, label);
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "String");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    /** ---------------------------------------- End of analytics --------------------------------- */
}
