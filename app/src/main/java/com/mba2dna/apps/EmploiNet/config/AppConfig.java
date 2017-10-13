package com.mba2dna.apps.EmploiNet.config;

public class AppConfig {

    // flag for display ads
    public static final boolean ENABLE_ADSENSE = true;
    // flag for save photo offline
    public static final boolean IMAGE_CACHE = true;
    // if you place config more than 200 items please set TRUE
    public static final boolean LAZY_LOAD = false;
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";


}
