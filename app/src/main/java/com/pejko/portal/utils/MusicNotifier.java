package com.pejko.portal.utils;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicNotifier extends IntentService
{

    public  String artist;
    public  String song;
    public MusicNotifier() {
        super("MusicNotifier");


    }

    @Override
    protected void onHandleIntent(Intent intent) {



        if (intent.hasExtra("autor")){
             artist = intent.getStringExtra("autor");
             song = intent.getStringExtra("hudba");


        }

        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {






            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.sklenarstvohrinova.sk/test/recmusic.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("author", artist));
                nameValuePairs.add(new BasicNameValuePair("song", song));
                System.out.println("###" + " WE GOT " + artist + song);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));



                // Execute HTTP Post Request
                ResponseHandler<String> responseHandler=new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);

                //Just display the response back
                //displayToastMessage(responseBody);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }


        }




    }

}
