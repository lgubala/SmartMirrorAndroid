package com.pejko.portal.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pejko.portal.entity.ModelBus;

import java.util.List;

public class BusDAO extends DAO<ModelBus>
{
    public static final String TABLE = "buses";

    private static final String COL_CITY = "city";
    private static final String COL_STATION = "station";
    private static final String COL_TIME = "time";
    private static final String COL_SYMBOLS = "symbols";

    public static final String[] ALL_COLUMNS = new String[] { COL_ID, COL_CITY, COL_STATION, COL_TIME, COL_SYMBOLS };

    public BusDAO(Storage storage)
    {
        super(storage);
    }

    @Override
    public void createTable(SQLiteDatabase db)
    {
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(TABLE);
        sb.append("(");
        sb.append(COL_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append(COL_CITY).append(" TEXT,");
        sb.append(COL_STATION).append(" TEXT,");
        sb.append(COL_TIME).append(" TEXT,");
        sb.append(COL_SYMBOLS).append(" TEXT");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    @Override
    protected void put(ContentValues values, ModelBus doctor)
    {
        values.put(COL_CITY, doctor.getCity());
        values.put(COL_STATION, doctor.getStation());
        values.put(COL_TIME, doctor.getTime());
        values.put(COL_SYMBOLS, doctor.getSymbol());
    }

    @Override
    protected ModelBus get(Cursor cursor)
    {
        ModelBus model = new ModelBus();

        model.setId(cint(cursor, COL_ID));
        model.setCity(cstring(cursor, COL_CITY));
        model.setStation(cstring(cursor, COL_STATION));
        model.setTime(cstring(cursor, COL_TIME));
        model.setSymbol(cstring(cursor, COL_SYMBOLS));

        String[] array = model.getTime().split(":");
        model.setHour(Integer.parseInt(array[0]));
        model.setMinute(Integer.parseInt(array[1]));

        return model;
    }

    @Override
    protected int insert(ModelBus doctor)
    {
        return super.insert(TABLE, doctor);
    }

    @Override
    protected int update(ModelBus doctor)
    {
        return super.update(TABLE, doctor);
    }

    @Override
    public int delete(ModelBus doctor)
    {
        return super.delete(TABLE, doctor);
    }

    @Override
    public ModelBus load(int id)
    {
        return super.select(TABLE, ALL_COLUMNS, id);
    }

    @Override
    public List<ModelBus> loadAll()
    {
        return super.selectAll(TABLE, ALL_COLUMNS);
    }

    @Override
    public boolean save(ModelBus doctor)
    {
        return super.save(TABLE, doctor);
    }

}
