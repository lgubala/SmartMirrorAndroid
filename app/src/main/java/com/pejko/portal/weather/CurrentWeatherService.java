package com.pejko.portal.weather;

import android.os.AsyncTask;
import android.util.Log;

import com.pejko.portal.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrentWeatherService {

    // Project Created by Ferdousur Rahman Shajib
    // www.androstock.com



    /*public static String setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }*/

    public static String setWeatherIcon(int actualId, boolean isDay){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            if(isDay) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }

    public interface AsyncResponse {
        void processFinish(ModelWeather modelWeather);
    }

    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

        public AsyncResponse delegate = null;//Call back interface

        public placeIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interfacethrough constructor
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonWeather = null;
            try {
                jsonWeather = getWeatherJSON(params[0], params[1]);
            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON results", e);
            }


            return jsonWeather;
        }

        @Override
        protected void onPostExecute(JSONObject jsonGlobal) {
            ModelWeather modelWeather = new ModelWeather();
            try {
                if(jsonGlobal != null){
                    // JSONObject weather = jsonGlobal.getJSONArray("weather").getJSONObject(0);
                    JSONObject cityObject = jsonGlobal.getJSONObject("city");

                    String name = cityObject.getString("name");

                    JSONObject coordObject = cityObject.getJSONObject("coord");
                    double lon = coordObject.getDouble("lon");
                    double lat = coordObject.getDouble("lat");

                    JSONObject listObject = jsonGlobal.getJSONArray("list").getJSONObject(0);

                    JSONObject jsonTemp = listObject.getJSONObject("temp");
                    double min = jsonTemp.getDouble("min");
                    double max = jsonTemp.getDouble("max");
                    double night = jsonTemp.getDouble("night");
                    double day = jsonTemp.getDouble("day");
                    double morn = jsonTemp.getDouble("morn");
                    double eve = jsonTemp.getDouble("eve");
                    System.out.println("### " + min + " " + max + " " + night + " " + day + " " + morn + " " + eve);

                    double pressure = listObject.getDouble("pressure");
                    double humidity = listObject.getDouble("humidity");

                    System.out.println("### " + pressure + " " + humidity);

                    JSONObject weatherObject = listObject.getJSONArray("weather").getJSONObject(0);
                    int id = weatherObject.getInt("id");
                    String icon = weatherObject.getString("icon");
                    System.out.println("### Id: " + id);

                    String iconText = setWeatherIcon(id, icon.contains("d"));

                    modelWeather.setMax(max);
                    modelWeather.setMin(min);
                    modelWeather.setIconText(iconText);

                }
            } catch (JSONException e) {
                //Log.e(LOG_TAG, "Cannot process JSON results", e);
                e.printStackTrace();
                System.out.println("###" + "frickin null");
            }

            delegate.processFinish(modelWeather);
        }
    }






    public static JSONObject getWeatherJSON(String lat, String lon){
        try {
            URL url = new URL(String.format(Const.URL_WEATHER_FORECAST, lat, lon));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", Const.WEATHER_API_KEY);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            return null;
        }
    }




}