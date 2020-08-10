package com.makienkovs.prikoli;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_SQLITE = "database.sqlite";
    public static final int DB_VERSION = 1;
    public static final String MYTABLE = "MYTABLE";
    public static final String POST = "POST";
    public static final String NAME = "NAME";
    public static final String FAVOR = "FAVOR";
    public static final String TIME = "TIME";
    public static final String ADDTIME = "ADDTIME";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MYTABLE (ID INTEGER PRIMARY KEY AUTOINCREMENT, POST TEXT, NAME TEXT, FAVOR INTEGER, TIME TEXT, ADDTIME TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS MYTABLE;");
        onCreate(db);
    }
}