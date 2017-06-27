package com.pejko.portal.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.casperise.common.metrics.DateUtils;
import com.pejko.portal.entity.Entity;

import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Základná trieda pre Data Access Object pre entitu typu E.
 */
public abstract class DAO<E extends Entity>
{
    protected static final String COL_ID = "id";

    protected final Storage storage;

    /**
     * Konštruktor nad uložiskom.
     */
    public DAO(Storage storage)
    {
        this.storage = storage;
    }

    /**
     * Vráti z kurzora stringovú hodnotu podľa pomenovaného stĺpca.
     */
    protected String cstring(Cursor cursor, String col)
    {
        return cursor.getString(cursor.getColumnIndex(col));
    }

    /**
     * Vráti z kurzora celočíslenú hodnotu podľa pomenovaného stĺpca.
     */
    protected int cint(Cursor cursor, String col)
    {
        return Integer.parseInt(cursor.getString(cursor.getColumnIndex(col)));
    }

    /**
     * Vráti z kurzora reálne číslo podľa pomenovaného stĺpca.
     */
    protected float cfloat(Cursor cursor, String col)
    {
        return Float.parseFloat(cursor.getString(cursor.getColumnIndex(col)));
    }

    /**
     * Vráti z kurzora dátumovú hodnotu podľa pomenovaného stĺpca. Predpokladá formátovanie v stringu podľa ISO-8601.
     */
    protected Date cdate(Cursor cursor, String col)
    {
        //Log.d("str=%s", cursor.getString(cursor.getColumnIndex(col)));
        return DateUtils.parseISO8601(cursor.getString(cursor.getColumnIndex(col)));
    }

    /**
     * Vráti z kurzora bajty podľa pomenovaného stĺpca.
     */
    protected byte[] cblob(Cursor cursor, String col)
    {
        try {
            if (!cursor.isNull(cursor.getColumnIndex(col))) {
                return cursor.getBlob(cursor.getColumnIndex(col));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // ----- SQL dependencies, should be encapsulated by other class, but KISS -----

    /**
     * Abstraktná metóda, ktorá vytvorí tabuľku v cieľovej databáze. Kontrétne DAO si ju prepíše.
     */
    public abstract void createTable(SQLiteDatabase db);

    /**
     * Puts all entry properties that are serialized to DB table to the collection. ID field mustn't be put into the
     * collection! The method is used by default implementation of INSERTing or UPDATEing the entity.
     *
     * @param values
     * @param entity
     */
    protected abstract void put(ContentValues values, E entity);

    /**
     * Returns an entity from cursor row.
     *
     * @param cursor
     * @return
     */
    protected abstract E get(Cursor cursor);

    /**
     * INSERTs the entity into the table.
     *
     * @param entity
     * @return
     */
    protected abstract int insert(E entity);

    /**
     * UPDATEs the entity in the table.
     *
     * @param entity
     * @return
     */
    protected abstract int update(E entity);

    /**
     * DELETEs the entity from the table.
     *
     * @param entity
     * @return
     */
    protected abstract int delete(E entity);

    /**
     * Default implementation for INSERT. Can be used in derived classes.
     *
     * @param table  Entity's table name
     * @param entity Entity to be inserted
     * @return the rowId of newly inserted row or -1 if an errror occured
     */
    protected int insert(String table, E entity)
    {
        SQLiteDatabase db = storage.getWritableDatabase();

        ContentValues values = new ContentValues();
        put(values, entity);

        return (int) db.insert(table, null, values);
    }

    /**
     * Default implementation for SELECT limited by given ID. Can be used in derived class.
     *
     * @param table   Entity's table name
     * @param columns Array of all table column name
     * @param id      Entity's ID
     * @return Instance of the entity or null if the entity's ID was not found in the table
     */
    protected E select(String table, String[] columns, int id)
    {
        SQLiteDatabase db = storage.getReadableDatabase();

        Cursor cursor = db.query(table, columns, COL_ID + "=?", new String[]{
                String.valueOf(id)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            E entity = get(cursor);
            cursor.close();
            return entity;
        }

        cursor.close();

        return null;
    }

    /**
     * Default implementation for SELECT * without any limits. All entities will be given. Can be used in derived class.
     *
     * @param table   Entity's table name
     * @param columns Array of all table column name
     * @return List of entities
     */
    protected List<E> selectAll(String table, String[] columns)
    {
        SQLiteDatabase db = storage.getReadableDatabase();
        List<E> entities = new Vector<E>();

        Cursor cursor = db.query(table, columns, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                E entity = get(cursor);
                entities.add(entity);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return entities;
    }

    /**
     * Default implementation for SELECT FROM WHERE statement. Only matched entities will be given. C
     * an be used in derived class.
     *
     * @param table   Entity's table name
     * @param columns Array of all table column name
     * @return List of entities
     */
    protected List<E> select(String table, String[] columns, String where, String[] whereArgs)
    {
        SQLiteDatabase db = storage.getReadableDatabase();
        List<E> entities = new Vector<E>();

        Cursor cursor = db.query(table, columns, where, whereArgs, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                E entity = get(cursor);
                entities.add(entity);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return entities;
    }

    /**
     * Default implementation for UPDATE. Can be used in derived class.
     *
     * @param table  Entity's table name
     * @param entity Instance of the entity to be updated
     * @return Number of affected DB rows, should be 1 or 0.
     */
    protected int update(String table, E entity)
    {
        SQLiteDatabase db = storage.getWritableDatabase();

        ContentValues values = new ContentValues();
        put(values, entity);

        return db.update(table, values, COL_ID + " = ?", new String[]{String.valueOf(entity.getId())});
    }

    /**
     * Default implementation for DELETE. Can be used in derived class.
     *
     * @param table  Entity's table name
     * @param entity Instance of the entity to be updated
     * @return Number of rows deleted, 0 otherwise.
     */
    protected int delete(String table, E entity)
    {
        SQLiteDatabase db = storage.getWritableDatabase();
        return db.delete(table, COL_ID + " = ?", new String[]{String.valueOf(entity.getId())});
    }

    /**
     * Default implementation for DELETE ALL ROWS. Can be used in derived class.
     *
     * @param table  Entity's table name
     * @return Number of rows deleted, 0 otherwise.
     */
    public void deleteAllRows(String table)
    {
        SQLiteDatabase db = storage.getWritableDatabase();
        db.delete(table, null, null);
    }

    /**
     * Default implementation for saving the entity. Entity is inserted if it's newly created, otherwise is updated. ID
     * property can be modified.
     *
     * @param table  Entity's table name
     * @param entity Instance of the entity to be saved.
     * @return True if entity was successfully serialized into the DB, otherwise false.
     */
    protected boolean save(String table, E entity)
    {
        if (entity.getId() == Entity.NO_ID) {
            int id = insert(entity);
            if (id == -1) {
                return false;
            }
            entity.setId(id);
            return true;
        }
        return update(table, entity) == 1;
    }

    // ----- abstract storage methods -----

    /**
     * Load entity from the storage by given ID.
     *
     * @param id
     * @return
     */
    public abstract E load(int id);

    /**
     * Load all entities from the storage.
     *
     * @return
     */
    public abstract List<E> loadAll();

    /**
     * Saves entity to the database. Methods creates underlying DB record if the entity is new, otherwise updates
     * existing one. Entity's ID can be modified!
     *
     * @param entity
     * @return True if entity was successfully saved, otherwise false.
     */
    public abstract boolean save(E entity);

}
