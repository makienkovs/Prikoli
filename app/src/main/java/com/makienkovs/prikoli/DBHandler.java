package com.makienkovs.prikoli;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBHandler {

    private SQLiteDatabase db;

    public DBHandler(Context c) {
        DBHelper dbHelper = new DBHelper(c, DBHelper.DB_SQLITE, null, DBHelper.DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToDB(ArrayList<PostModel> posts) {
        for (int i = 0; i < posts.size(); i++) {
            PostModel p = posts.get(i);
            String post = p.getElementPureHtml();
            String name = p.getName();
            boolean favor = p.isFavor();
            long time = p.getTime();

            @SuppressLint("Recycle")
            Cursor c = db.query(DBHelper.MYTABLE,
                    new String[]{DBHelper.POST, DBHelper.NAME},
                    DBHelper.POST + " = ?",
                    new String[]{post},
                    null, null, null);

            if (c.getCount() == 0) {
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.POST, post);
                cv.put(DBHelper.NAME, name);
                cv.put(DBHelper.FAVOR, favor ? 1 : 0);
                cv.put(DBHelper.TIME, time);
                cv.put(DBHelper.ADDTIME, 0);
                db.insert(DBHelper.MYTABLE, null, cv);
                c.close();
            }
        }
    }

    public ArrayList<PostModel> readFromDB(String nameOfResource) {
        ArrayList<PostModel> posts = new ArrayList<>();
        @SuppressLint("Recycle")
        Cursor c = db.query(DBHelper.MYTABLE, null, null, null, null, null, null);
        if (c.moveToNext()) {
            int postIndex = c.getColumnIndex(DBHelper.POST);
            int nameIndex = c.getColumnIndex(DBHelper.NAME);
            int favorIndex = c.getColumnIndex(DBHelper.FAVOR);
            int timeIndex = c.getColumnIndex(DBHelper.TIME);
            do {
                PostModel p = new PostModel();
                String name = c.getString(nameIndex);
                int favor = c.getInt(favorIndex);
                if (name.equalsIgnoreCase(nameOfResource)) {
                    p.setElementPureHtml(c.getString(postIndex));
                    p.setName(name);
                    if (favor == 1) {
                        p.setFavor(true);
                    }
                    p.setTime(c.getLong(timeIndex));
                    posts.add(p);
                }
            } while (c.moveToNext());
        }
        c.close();
        posts.sort((o1, o2) -> {
            String time1 = String.valueOf(o1.getTime());
            String time2 = String.valueOf(o2.getTime());
            return time2.compareTo(time1);
        });
        return posts;
    }
}