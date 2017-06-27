package com.pejko.portal.activities;

/**
 * Created by Pejko on 27-May-16.
 */
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.pejko.portal.R;
import com.pejko.portal.utils.Const;
import com.pejko.portal.utils.FontUtils;
import com.pejko.portal.views.RotateLayout;

public class MagicActivity extends Activity {


    private RotateLayout ltRotate;

    private static TextView txtTitle;
    private static TextView txtTovar1;
    private static TextView txtTovar2;
    private static TextView txtTovar3;
    private static TextView txtCena1;
    private static TextView txtCena2;
    private static TextView txtCena3;
    private static TextView txtExtra1;
    private static TextView txtExtra2;
    private static TextView txtExtra3;
    private static MagicActivity ins;
    private static SharedPreferences sharedPreferences;

    public static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.magic);

        sharedPreferences = getSharedPreferences("Portal", Context.MODE_PRIVATE);

        findViews();
    }

    private void findViews() {
        txtTitle = (TextView) findViewById(R.id.txt_title);

        txtTovar1 = (TextView) findViewById(R.id.txt_tovar1);
        txtTovar2 = (TextView) findViewById(R.id.txt_tovar2);
        txtTovar3 = (TextView) findViewById(R.id.txt_tovar3);

        txtCena1 = (TextView) findViewById(R.id.txt_cena1);
        txtCena2 = (TextView) findViewById(R.id.txt_cena2);
        txtCena3 = (TextView) findViewById(R.id.txt_cena3);

        txtExtra1 = (TextView) findViewById(R.id.txt_extra1);
        txtExtra2 = (TextView) findViewById(R.id.txt_extra2);
        txtExtra3 = (TextView) findViewById(R.id.txt_extra3);

        txtTitle.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));

        txtTovar1.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));
        txtTovar2.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));
        txtTovar3.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));

        txtCena1.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));
        txtCena2.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));
        txtCena3.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));

        txtExtra1.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));
        txtExtra2.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));
        txtExtra3.setTypeface(FontUtils.getDefaultTypeface(getApplicationContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateExtraOne();
    }

    public static void updateExtraOne() {

        String prefsTitle = sharedPreferences.getString(Const.PREFS_POLE, "Nadpis");

        String prefsCena = sharedPreferences.getString(Const.PREFS_CENA_1, "CENA");
        String prefsExtra = sharedPreferences.getString(Const.PREFS_EXTRA_1, "EXTRA");
        String prefsTovar = sharedPreferences.getString(Const.PREFS_TOVAR_1, "TOVAR");


        String prefsCena2 = sharedPreferences.getString(Const.PREFS_CENA_2, "CENA");
        String prefsExtra2 = sharedPreferences.getString(Const.PREFS_EXTRA_2, "EXTRA");
        String prefsTovar2 = sharedPreferences.getString(Const.PREFS_TOVAR_2, "TOVAR");

        String prefsCena3 = sharedPreferences.getString(Const.PREFS_CENA_3, "CENA");
        String prefsExtra3 = sharedPreferences.getString(Const.PREFS_EXTRA_3, "EXTRA");
        String prefsTovar3 = sharedPreferences.getString(Const.PREFS_TOVAR_3, "TOVAR");

        txtTitle.setText(prefsTitle);

        txtTovar1.setText(prefsTovar);
        txtCena1.setText(prefsCena);
        txtExtra1.setText(prefsExtra);


        txtTovar2.setText(prefsTovar2);
        txtCena2.setText(prefsCena2);
        txtExtra2.setText(prefsExtra2);


        txtTovar3.setText(prefsTovar3);
        txtCena3.setText(prefsCena3);
        txtExtra3.setText(prefsExtra3);
    }

}



