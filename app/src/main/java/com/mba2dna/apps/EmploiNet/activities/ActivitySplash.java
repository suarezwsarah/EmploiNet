package com.mba2dna.apps.EmploiNet.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.data.AppConfig;
import com.mba2dna.apps.EmploiNet.data.SharedPref;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.PermissionUtil;
import com.mba2dna.apps.EmploiNet.utils.Tools;


import java.util.Timer;
import java.util.TimerTask;


public class ActivitySplash extends AppCompatActivity {
    long Delay = 3000;
    private SharedPref sharedPref;
    private View parent_view;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    final TimerTask task = new TimerTask() {
        @Override
        public void run() {
            startMainActivity();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        parent_view = findViewById(R.id.parent_view);
        FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.TOPIC_GLOBAL);
        String s= FirebaseInstanceId.getInstance().getToken();


        TextView title = (TextView) findViewById(
                R.id.splash_welcome_text);
        CommonUtils.setRobotoThinFont(this, title);

        TextView desc = (TextView) findViewById(
                R.id.splash_desc_text);
        CommonUtils.setRobotoThinFont(this, desc);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        sharedPref = new SharedPref(this);
        Tools.initImageLoader(getApplicationContext());

        //parent_view.setBackgroundColor(sharedPref.getThemeColorInt());
        // for system bar in lollipop
        Tools.systemBarLolipop(this);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(AppConfig.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };

        displayFirebaseRegId();
        Timer RunSplash = new Timer();

        // Task to do when the timer ends
        TimerTask ShowSplash = new TimerTask() {
            @Override
            public void run() {
                // Close SplashScreenActivity.class
                finish();

                // Start MainActivity.class
                Intent myIntent = new Intent(ActivitySplash.this,
                        ActivityMain.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
            }
        };

        // Start the timer
        RunSplash.schedule(ShowSplash, Delay);
    }

    private void displayFirebaseRegId() {


        Log.e("DISPLAY", "Firebase reg id: " + sharedPref.getGCMRegId());


    }

    private void startProvisioningGcm(){
        // ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        if(sharedPref.isNeedRegisterGcm() && Tools.cekConnection(this)){
           /* mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // checking for type intent filter
                    if (intent.getAction().equals(AppConfig.REGISTRATION_COMPLETE)) {
                        // gcm successfully registered
                        // now subscribe to `global` topic to receive app wide notifications
                        FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.TOPIC_GLOBAL);

                      //  displayFirebaseRegId();

                    } else if (intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)) {
                        // new push notification is received

                        String message = intent.getStringExtra("message");

                        Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                       // txtMessage.setText(message);
                    }
                }
            };*/




        } else {
            try{
                // Show splash screen for 1 seconds
                // new Timer().schedule(task, 3000);

            }catch (Exception e){}
        }

    }

    private void startMainActivity() {
        // go to the main activity
        Intent i = new Intent(ActivitySplash.this, ActivityMain.class);
        startActivity(i);
        // kill current activity
        finish();
    }

    @Override
    protected void onResume() {
        //startProvisioningGcm();
     /*   if(!PermissionUtil.isAllPermissionGranted(this)){
           // showDialogPermission();
        }else{
           // startProvisioningGcm();
            // register GCM registration complete receiver
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(AppConfig.REGISTRATION_COMPLETE));

            // register new push message receiver
            // by doing this, the activity will be notified each time a new message arrives
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(AppConfig.PUSH_NOTIFICATION));

            // clear the notification area when the app is opened
            NotificationUtils.clearNotifications(getApplicationContext());
        }*/
        super.onResume();
    }

    private void showDialogPermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title_permission));
        builder.setMessage(getString(R.string.dialog_content_permission));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                PermissionUtil.goToPermissionSettingScreen(ActivitySplash.this);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Timer().schedule(task, 1000);
            }
        });
        builder.show();
    }
}
