package com.pejko.portal.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pejko.portal.R;
import com.pejko.portal.adapters.BusAdapter;
import com.pejko.portal.adapters.RssAdapter;
import com.pejko.portal.dao.Storage;
import com.pejko.portal.entity.ModelBus;
import com.pejko.portal.entity.ModelRss;
import com.pejko.portal.gcm.RegistrationIntentService;
import com.pejko.portal.interfaces.RssListener;
import com.pejko.portal.services.HeartBeatService;
import com.pejko.portal.tasks.RssTask;
import com.pejko.portal.utils.BusUtils;
import com.pejko.portal.utils.Const;
import com.pejko.portal.utils.FontUtils;
import com.pejko.portal.utils.MusicNotifier;
import com.pejko.portal.utils.NetworkUtils;
import com.pejko.portal.weather.CurrentWeatherService;
import com.pejko.portal.weather.ModelWeather;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final IntentFilter intentFilter;

    static {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    private ListView listNews, listBus;

    private List<ModelRss> itemListNews;
    private AsyncTask<String, Void, List<ModelRss>> rssTask;
    private TextView  txtDay, txtDate, txtTime;


    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public BroadcastReceiver mRegistrationBroadcastReceiver;
    public boolean isReceiverRegistered;

    public String artist;
    public String track;

    private static MainActivity ins;


    TextView weatherIcon, tempmax, tempmin;

    Typeface weatherFont;

    private SimpleDateFormat dayFormatter, dateFormatter, timeFormatter;
    private BusAdapter busAdapter;
    private ImageView busIco;
    private Storage storage;
    private AsyncTask<Void, Integer, ArrayList<ModelBus>> busTask;
    private RelativeLayout ltRoot;
    private TextView textMessage, textSubmessage;
    private AsyncTask<String, Void, JSONObject> weatherTask;

    private Handler handler = new Handler();
    private ModelWeather weather;

    public static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");
        ins = this;
        findViews();
        initData();
        setBuses();
        setListeners();

        initGcmAlarm();
        initGCM();

        //Animation rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotation);
        //LayoutAnimationController animController = new LayoutAnimationController(rotateAnim, 0);
        //ltRoot.setLayoutAnimation(animController);
    }

    private void setListeners() {
        ltRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initGcmAlarm() {
        Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent(MainActivity.this, HeartBeatService.class);
        PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, pintent);
    }

    private void startTasks() {
        handler.post(runnable);
        handler.postDelayed(runnableHour, Const.LOAD_ALL_DATA_INTERVAL);
    }

    private void stopTasks() {
        handler.removeCallbacks(runnable);
        handler.removeCallbacks(runnableHour);
    }

    private void setBuses() {
        busIco = (ImageView) findViewById(R.id.busIco);
        busAdapter = new BusAdapter(getApplicationContext());
        listBus.setAdapter(busAdapter);
    }

    private void reloadBusData() {
        if (isBusTaskRunning())
            return;

        busTask = new AsyncTask<Void, Integer, ArrayList<ModelBus>>(){
            @Override
            protected ArrayList<ModelBus> doInBackground(Void... params) {

                List<ModelBus> buses = storage.getBusDAO().loadAll();
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());

                Calendar calNow = Calendar.getInstance();
                int nowHour = calNow.get(Calendar.HOUR_OF_DAY);
                int nowMin = calNow.get(Calendar.MINUTE);
                int startPos = 0;

                for (int i = 0; i < buses.size(); i++) {
                    ModelBus bus = buses.get(i);
                    if ((nowHour < bus.getHour()) ||
                            (nowHour == bus.getHour() && nowMin < bus.getMinute())) {
                        startPos = i;
                        break;
                    }
                    startPos = i;
                }

                ArrayList<ModelBus> arrayBuses = new ArrayList<>();

                int dayCounter = 0;
                do {
                    for (int i = startPos; i < buses.size(); i++) {
                        ModelBus bus = buses.get(i);
                          if (TextUtils.isEmpty(bus.getSymbol())) {
                               arrayBuses.add(bus);
                        } else {
                            if (BusUtils.canAddBus(bus, calNow)) {
                                arrayBuses.add(bus);
                            }
                        }

                        if (arrayBuses.size() >= 5) {
                            break;
                        }
                    }
                    startPos = 0;

                    calNow.add(Calendar.DAY_OF_YEAR, +1);
                    calNow.set(Calendar.HOUR_OF_DAY, 0);
                    calNow.set(Calendar.MINUTE, 0);
                    calNow.set(Calendar.SECOND, 0);
                   // System.out.println("### PRESIEL SOM VSETKY V DNI " + formatter.format(calNow.getTime()));
                    dayCounter++;

                } while (arrayBuses.size() < 5 && dayCounter < 5);
              //  System.out.println("### BusEnd " + System.currentTimeMillis());

                return arrayBuses;
            }

            @Override
            protected void onPostExecute(ArrayList<ModelBus> array) {
                busAdapter.setData(array);
            }
        }.execute();
    }

    public static MainActivity  getInstace(){
        return ins;
    }




    private void initData() {
        dayFormatter = new SimpleDateFormat("EEEE", Locale.getDefault());
        dateFormatter = new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault());
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        storage = new Storage(getApplicationContext());

    }

    public boolean isTaskRunning() {
        if (rssTask != null && weatherTask != null) {
            if ((rssTask.getStatus() == AsyncTask.Status.RUNNING || rssTask.getStatus() == AsyncTask.Status.PENDING) &&
                    (weatherTask.getStatus() == AsyncTask.Status.RUNNING || weatherTask.getStatus() == AsyncTask.Status.PENDING)) {
                return true;
            }
        }
        return false;
    }

    private void setData(boolean forceDownload) {
        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            if (forceDownload || !isTaskRunning()) {
                rssTask = new RssTask(getApplicationContext(), Const.URL_RSS_GLOBAL, new RssListener() {
                    @Override
                    public void downloadFinished(List<ModelRss> rssItems) {
                        if (rssItems != null) {
                            itemListNews = rssItems;
                            RssAdapter adapter = new RssAdapter(getApplicationContext(), itemListNews);
                            listNews.setAdapter(adapter);

                            checkRssAndWeather();
                        }
                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                weatherTask = new CurrentWeatherService.placeIdTask(new CurrentWeatherService.AsyncResponse() {
                    public void processFinish(ModelWeather modelWeather) {

                        weather = modelWeather;
                        tempmax.setText(String.format("%.1f", modelWeather.getMax()) + "°C");
                        tempmin.setText(String.format("%.1f", modelWeather.getMin()) + "°C");
                        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                            weatherIcon.setText(Html.fromHtml(modelWeather.getIconText()));
                        }

                        checkRssAndWeather();
                    }
                }).execute(Const.HRINOVA_LAT, Const.HRINOVA_LON); //  weatherTask.execute("Latitude", "Longitude")
            }
        }
    }

    private void checkRssAndWeather() {
        if (weather != null && itemListNews != null && !itemListNews.isEmpty()) {
            handler.removeCallbacks(runnable);
        }
    }


    private void findViews() {

        ltRoot = (RelativeLayout) findViewById(R.id.ltRoot);
        listNews = (ListView) findViewById(R.id.listNews);
        listBus = (ListView) findViewById(R.id.listBus);

        txtDay = (TextView) findViewById(R.id.txtDay);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTime = (TextView) findViewById(R.id.txtTime);

        textMessage = (TextView) findViewById(R.id.txt_message);
        textSubmessage = (TextView) findViewById(R.id.txt_submessage);

        tempmax = (TextView)findViewById(R.id.temp_max);
        tempmin = (TextView)findViewById(R.id.temp_min);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);


        txtDay.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));
        txtDate.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));
        txtTime.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));


    }

    private final BroadcastReceiver timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            updateDateAndTime();
            reloadBusData();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        reloadBusData();
        updateDateAndTime();

        registerReceiver(timeChangedReceiver, intentFilter);
        registerMusicReceiver();

        registerGCMReceiver();
        setDefaultData();
        startTasks();
        //registerGCMReceiver();
        //initGCM();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTasks();
        active = false;
    }

    private void setDefaultData() {
        SharedPreferences sharedPreferences = getSharedPreferences("Portal", Context.MODE_PRIVATE);
        textMessage.setText(sharedPreferences.getString(Const.PREFS_TITLE, "Ahoj Pejko"));
        textSubmessage.setText(sharedPreferences.getString(Const.PREFS_DESC, "Dnes je pekny den"));
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(timeChangedReceiver);
            unregisterReceiver(mMusicReceiver);

            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
            isReceiverRegistered = false;
        } catch (Exception e) {}
        super.onPause();
    }

    private void updateDateAndTime() {
        Date dtNow = new Date();

        String day = dayFormatter.format(dtNow);
        txtDay.setText(day.substring(0, 1).toUpperCase() + day.substring(1));
        txtDate.setText(dateFormatter.format(dtNow));
        txtTime.setText(timeFormatter.format(dtNow));
    }

    public void updateTheTextView(final String t) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView textV1 = (TextView) findViewById(R.id.txt_message);
               // TextView textV2 = (TextView) findViewById(R.id.txt_submessage);
                textV1.setText(t);
              //  textV2.setText(p);
            }
        });
    }

    public void updateTheBotTextView(final String p) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
               // TextView textV1 = (TextView) findViewById(R.id.txt_message);
                 TextView textV2 = (TextView) findViewById(R.id.txt_submessage);
                //textV1.setText(t);
                  textV2.setText(p);
            }
        });
    }


    public void updatePlayerTextView(final String m, final String n) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView textV3 = (TextView) findViewById(R.id.music_txt_title);
               TextView textV4 = (TextView) findViewById(R.id.music_txt_author);


                textV3.setText(m);
                textV3.setSelected(true);
                textV3.requestFocus();
                textV4.setText(n);
                textV4.setSelected(true);
                textV4.requestFocus();

            }
        });
    }

    public void  updatePlayerOnNet(){
        Intent intent = new Intent(this, MusicNotifier.class);
        intent.putExtra("autor", artist);
        intent.putExtra("hudba", track );

        startService(intent);
    }

    public void initGCM() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //registerGCMReceiver();
            }
        };
        // Registering BroadcastReceiver


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            System.out.println("### Call Service from Activiyt");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }
    private void registerMusicReceiver() {
        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.togglepause");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged");
        iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        System.out.println("###" + "Asdfghj");
        registerReceiver(mMusicReceiver, iF);
    }


    private BroadcastReceiver mMusicReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            System.out.println("mIntentReceiver.onReceive "+ action + " / " + cmd);
            artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            track = intent.getStringExtra("track");
            System.out.println("Music" +artist+":"+album+":"+track);
            if (action.equals("com.android.music.playstatechanged")) {
                boolean playing = intent.getBooleanExtra("playing", false);
                int positionInMs = intent.getIntExtra("playbackPosition", 0);
                // Do something with extracted information
                ImageView playerImg  = (ImageView) findViewById(R.id.music_img_status);
                if (playing == false){

                    playerImg.setImageResource(R.drawable.ic_av_play_arrow);



                    System.out.println("###" + "pause");

                }else{
                    playerImg.setImageResource(R.drawable.ic_av_pause);
                    System.out.println("###" + "vyhrava");
                }
            }
            updatePlayerTextView(track, artist);
            updatePlayerOnNet();
        }
    };

    private void registerGCMReceiver(){
        if(!isReceiverRegistered) {
          System.out.println("### Register recreiveeer");
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Const.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
              //  System.out.println( "###" + "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (isBusTaskRunning()){
            busTask.cancel(true);
        }
        super.onDestroy();
    }

    private boolean isBusTaskRunning() {
        if (busTask != null) {
            if (busTask.getStatus() == AsyncTask.Status.PENDING || busTask.getStatus() == AsyncTask.Status.RUNNING) {
                return true;
            }
        }
        return false;
    }

    final Runnable runnable = new Runnable() {
        public void run() {
            setData(false);
            handler.postDelayed(this, Const.LOAD_DATA_INTERVAL);
        }
    };

    final Runnable runnableHour = new Runnable() {
        public void run() {
            setData(true);
            handler.postDelayed(this, Const.LOAD_ALL_DATA_INTERVAL);
        }
    };
}
