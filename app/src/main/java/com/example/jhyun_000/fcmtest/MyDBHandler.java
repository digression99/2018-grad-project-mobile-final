package com.example.jhyun_000.fcmtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jhyun_000 on 2018-03-02.
 */

public class MyDBHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "locationDB.db";
    public static final String DATABASE_TABLE = "locations";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//       //sqlite 자료형 integer, real(float, double), text, BLOB(binary data), null(값이 정의되지 않거나 존재하지 않음)
        String CREATE_TABLE = "create table if not exists " + DATABASE_TABLE + "(" + COLUMN_ID + " integer primary key autoincrement,"
                + COLUMN_LONGITUDE + " real," + COLUMN_LATITUDE + " real" + ")";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DATABASE_TABLE);
        onCreate(db);
    }


    public void addLocation(TrackLocation tracklocation) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LONGITUDE, tracklocation.getLongitude());
        values.put(COLUMN_LATITUDE, tracklocation.getLatitude());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE, null, values);
        db.close();
    }

    public void addLocation(double longitude, double latitude) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_LATITUDE, latitude);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE, null, values);
        db.close();
    }

    public Cursor findAll() {
        String query = "select * from " + DATABASE_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(query, null);
    }

    public void dropTable() {
        boolean result = false;
        String query = "DROP TABLE IF EXISTS " + DATABASE_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void deleteAll() {
        boolean result = false;
//        String query = "DELETE * FROM "+DATABASE_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL(query);
        db.delete(DATABASE_TABLE, null, null);
        db.close();
        //db.delete return int number of rows affected
    }

}
