package com.mba2dna.apps.EmploiNet.service;

/**
 * Created by BIDA on 11/26/2016.
 */
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mba2dna.apps.EmploiNet.data.AppConfig;
import com.mba2dna.apps.EmploiNet.data.SharedPref;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService{
    private static final String TAG = FirebaseInstanceIDService.class.getSimpleName();
    private SharedPref sharedPref;
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log.e(TAG, "sendRegistrationToServer: " + refreshedToken);
        sharedPref = new SharedPref(getBaseContext());
        // Saving reg id to shared preferences
        sharedPref.setGCMRegId(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(AppConfig.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
      //  Log.e(TAG, "sendRegistrationToServer: " + refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("fb_setting");
         String android_id = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        myRef.child(android_id).child("RegID").setValue(token);


    }


}
