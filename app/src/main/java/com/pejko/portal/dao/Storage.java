package com.pejko.portal.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.casperise.common.debug.Log;
import com.pejko.portal.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Database storage backed by SQLite.
 */
public class Storage extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "portal";
    private static final int DATABASE_VERSION = 1;

    private static BusDAO busDAO;
    private Context context;

    public Storage(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // super(context, Environment.getExternalStorageDirectory() + File.separator + DATABASE_NAME + ".db", null,
        // DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Initialization called after installation
     */
    public void applicationInstalled()
    {
        // No special code
    }

    /**
     * Initialization called each application start.
     */
    public void applicationStarted()
    {
        // No special code
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d("Database creation, version %d", DATABASE_VERSION);

        getBusDAO().createTable(db);

        importBus(db);
    }

    private void importBus(SQLiteDatabase db) {
        // load activity data
        InputStream inputStream = this.context.getResources().openRawResource(R.raw.buses);

        InputStreamReader ir = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(ir);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String[] commands = text.toString().split(";");

        // source: http://www.nutristrategy.com/activitylist4.htm calories per hour
        for (int i = 0; i < commands.length - 1; i++) {
            db.execSQL(commands[i] + ";");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d("Database upgrade %d -> %d", oldVersion, newVersion);
        if (oldVersion == 1 && newVersion == 2) {
            importBus(db);
        }
    }

    public BusDAO getBusDAO()
    {
        if (busDAO == null) {
            busDAO = new BusDAO(this);
        }
        return busDAO;
    }
}
