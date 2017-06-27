package com.pejko.portal.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;


import com.pejko.portal.utils.Const;
import com.pejko.portal.utils.NetworkUtils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class GCMRegisterService extends IntentService
{


    public GCMRegisterService() {
        super("GCMRegisterService");

        System.out.println("###" + "this here");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        System.out.println("###" + "weeeee here");
        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            System.out.println("###" + "ower here");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String token = sharedPreferences.getString(Const.GCM_TOKEN, null);

            if (TextUtils.isEmpty(token))
                return;



            StringBuilder postBody = new StringBuilder();

              String body = token;






           HttpClient httpclient =  new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.sklenarstvohrinova.sk/test/rectoken.php");
            System.out.println("###" + "we here");

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("token", body));
                 System.out.println("###" + "MGFO " + nameValuePairs.toString());
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                System.out.println("###" + "omfg " + httppost.getEntity());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            // execute HTTP post request
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {

                    String responseStr = null;
                    try {
                        responseStr = EntityUtils.toString(resEntity).trim();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("###" +"Response: " +  responseStr);
                    System.out.println("###" +"entit " +  resEntity);

                    // you can add an if statement here and do other actions based on the response
                }






        }




    }

}
