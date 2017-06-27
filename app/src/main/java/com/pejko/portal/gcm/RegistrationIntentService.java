package com.pejko.portal.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;


import com.pejko.portal.R;
import com.pejko.portal.utils.Const;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;


/**
 * Created by Pejko on 11-May-16.
 */
public class RegistrationIntentService extends IntentService {


    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private SharedPreferences sharedPreferences;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]

            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);


            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);


            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean("sendTokenToServer", true).apply();
            System.out.println("### GCM Start service 3");
            // [END register_for_gcm]
        } catch (Exception e) {
            System.out.println("### GCM Exxxx" + e.getMessage());
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean("sendTokenToServer", false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent("registrationComplete");
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration newToken with any server-side account
     * maintained by your application.
     *
     * @param newToken The new newToken.
     */
    private void sendRegistrationToServer(String newToken) {
        String oldToken = sharedPreferences.getString(Const.GCM_OLD_TOKEN, null);
        //String oldToken = sharedPreferences.getString(Const.GCM_OLD_TOKEN, null);
        //String oldToken = "";
        System.out.println("### sendRegistrationToServer newToken " + newToken + " A OLDtOKEN " + oldToken);


        if (TextUtils.isEmpty(oldToken)) {
            System.out.println("### Register 1");
            sharedPreferences.edit()
                    .putString(Const.GCM_OLD_TOKEN, newToken)
                    .putString(Const.GCM_TOKEN, newToken)
                    .commit();
            registerToken();

        } else {
            if (!oldToken.equalsIgnoreCase(newToken)) {
                sharedPreferences.edit()
                        .putString(Const.GCM_OLD_TOKEN, oldToken)
                        .putString(Const.GCM_TOKEN, newToken)
                        .commit();
                System.out.println("### Register 2");
               // unregisterToken();
               registerToken();

            }

        }
        // Add custom implementation, as needed.
    }

    private void registerToken() {

            Intent intent = new Intent(this, GCMRegisterService.class);
            startService(intent);

    }
/*
    private void unregisterToken() {
        Intent intent = new Intent(this, GCMUnregisterService.class);
        startService(intent);
    }
*/
    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]




}
