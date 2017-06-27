/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pejko.portal.gcm;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.IntentCompat;
import android.view.KeyEvent;


import com.google.android.gms.gcm.GcmListenerService;
import com.pejko.portal.activities.MagicActivity;
import com.pejko.portal.activities.MainActivity;
import com.pejko.portal.activities.VideoActivity;
import com.pejko.portal.utils.Const;
import com.pejko.portal.utils.NotificationUtils;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private static final int GCM_NOTIF_ID = 10;

    public  String tovar;
    public String cena;
    public String extra;

    public  String tovar2;
    public String cena2;
    public String extra2;

    public  String tovar3;
    public String cena3;
    public String extra3;
    public String pole0;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");

        System.out.println("### " + TAG + " - " + from + " - " + message);

        String upMessage = "÷UP";
        String downMessage = "÷DW";
        String playMessage = "÷PL";
        String pauseMessage = "÷PA";
        String nextMessage = "÷NX";
        String prewMessage = "÷PR";
        String cennikMessage = "÷CN";
        String mirrorMessage = "÷ZR";
        String videoMessage = "÷VM";


        String pole1 = "÷P1";
        String pole2 = "÷P2";
        String pole3 = "÷P3";
        String pole = "÷P0";

        SharedPreferences sharedPreferences = getSharedPreferences("Portal", Context.MODE_PRIVATE);

        if (message.substring(0,3 ).equals(upMessage)){
            message = message.substring(3);
            sharedPreferences.edit()
                    .putString(Const.PREFS_TITLE, message)
                    .apply();
            MainActivity.getInstace().updateTheTextView(message);
        }
        else if (message.substring(0,3 ).equals(downMessage)) {
            message = message.substring(3);
            sharedPreferences.edit()
                    .putString(Const.PREFS_DESC, message)
                    .apply();
            MainActivity.getInstace().updateTheBotTextView(message);
        }
        else if (message.substring(0,3 ).equals(pole1)) {
            message = message.substring(3);
            String[] parts = message.split("\\^"); // String array, each element is text between ^
            tovar = parts[0];
            cena = parts[1];
            extra = parts[2];

            sharedPreferences.edit()
                    .putString(Const.PREFS_TOVAR_1, tovar)
                    .putString(Const.PREFS_CENA_1, cena)
                    .putString(Const.PREFS_EXTRA_1, extra)
                    .apply();

            System.out.println("###" + " c " + cena + " t " + tovar +  " e " + extra);
            if (MagicActivity.active) {
                startMagic();
            }
        }
        else if (message.substring(0,3 ).equals(pole2)) {
            message = message.substring(3);
            String[] parts = message.split("\\^"); // String array, each element is text between ^
            tovar2 = parts[0];
            cena2 = parts[1];
            extra2 = parts[2];

            sharedPreferences.edit()
                    .putString(Const.PREFS_TOVAR_2, tovar2)
                    .putString(Const.PREFS_CENA_2, cena2)
                    .putString(Const.PREFS_EXTRA_2, extra2)
                    .apply();

            System.out.println("###" + " c2 " + cena2 + " t2 " + tovar2 +  " e2 " + extra2);
            if (MagicActivity.active) {
                startMagic();
            }
        }

        else if (message.substring(0,3 ).equals(pole3)) {
            message = message.substring(3);
            String[] parts = message.split("\\^"); // String array, each element is text between ^
            tovar3 = parts[0];
            cena3 = parts[1];
            extra3 = parts[2];

            sharedPreferences.edit()
                    .putString(Const.PREFS_TOVAR_3, tovar3)
                    .putString(Const.PREFS_CENA_3, cena3)
                    .putString(Const.PREFS_EXTRA_3, extra3)
                    .apply();

            System.out.println("###" + " c3 " + cena3 + " t3 " + tovar3 +  " e2 " + extra3);
            if (MagicActivity.active) {
                startMagic();
            }
        }

        else if (message.substring(0,3 ).equals(pole)) {
            message = message.substring(3);
            pole0 = message;

            sharedPreferences.edit()
                    .putString(Const.PREFS_POLE, pole0)

                    .apply();

            System.out.println("###" + "pole " + pole0);
            if (MagicActivity.active) {
                startMagic();
            }
        }

        else if (message.substring(0,3 ).equals(pauseMessage)) {
            Intent i = new Intent("com.android.music.musicservicecommand");
            i.putExtra("command", "pause");
            sendBroadcast(i);
        }
        else if (message.substring(0,3 ).equals(playMessage)) {

            long eventtime = SystemClock.uptimeMillis();

            /*System.out.println("###" + "   cccconst is now " + Const.MUSIC_IS_ACTIVE);
           if (Const.MUSIC_IS_ACTIVE == true)
            {
                Intent i = new Intent("com.android.music.musicservicecommand");
                i.putExtra("command", "play");
                sendBroadcast(i);
            }else {

                Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                sendOrderedBroadcast(downIntent, null);
               Const.MUSIC_IS_ACTIVE = true;
            }*/

            boolean isRunning = isMusicRunning();
            System.out.println("###" + "   cccconst is now " + isRunning);
            if (isRunning)
            {
                Intent i = new Intent("com.android.music.musicservicecommand");
                i.putExtra("command", "play");
                sendBroadcast(i);
            }else {

                Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                sendOrderedBroadcast(downIntent, null);
               // Const.MUSIC_IS_ACTIVE = true;
            }
        }
        else if (message.substring(0,3 ).equals(nextMessage)) {
            Intent i = new Intent("com.android.music.musicservicecommand");

            i.putExtra("command", "next");
            sendBroadcast(i);
        }
        else if (message.substring(0,3 ).equals(prewMessage)) {
            Intent i = new Intent("com.android.music.musicservicecommand");
            i.putExtra("command", "previous");
            sendBroadcast(i);
        }
        else if (message.substring(0,3 ).equals(cennikMessage)) {
            startMagic();
        }
        else if (message.substring(0,3 ).equals(videoMessage)) {
            playVideo();
        }
        else if (message.substring(0,3 ).equals(mirrorMessage)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */



        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        NotificationUtils.sendNotification(getApplicationContext(), message);
        // [END_EXCLUDE]
    }

    private void startMagic() {
        Intent intent = new Intent(this, MagicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void playVideo (){
        /*String video_path = "https://www.youtube.com/watch?v=DH6d8-fTsww";
        Uri uri = Uri.parse(video_path);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/

        Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    // [END receive_message]

    private static String SERVICE_CMD_V1 = "com.sec.android.app.music";
    private static String SERVICE_CMD_V2 = "com.android.music";

    private boolean isMusicRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            System.out.println("### SERVICES: " + service.service.getClassName());
            /*if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }*/
            if (service.service.getClassName().contains(SERVICE_CMD_V1) ||
                    service.service.getClassName().contains(SERVICE_CMD_V2)) {
                return true;
            }
        }
        return false;
    }

}
