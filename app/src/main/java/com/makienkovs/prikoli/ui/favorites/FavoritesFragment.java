package com.makienkovs.prikoli.ui.favorites;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.makienkovs.prikoli.Adapter;
import com.makienkovs.prikoli.DBHelper;
import com.makienkovs.prikoli.PostModel;
import com.makienkovs.prikoli.R;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private ListView lvFavor;
    private ArrayList<PostModel> posts;
    private SQLiteDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
        lvFavor = root.findViewById(R.id.list_favor);

        registerForContextMenu(lvFavor);
        DBHelper dbHelper = new DBHelper(getContext(), DBHelper.DB_SQLITE, null, DBHelper.DB_VERSION);

        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        readPosts();
        return root;
    }

    private void readPosts() {
        try {
            posts = readFromDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Adapter adapter = new Adapter(posts);
        lvFavor.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private ArrayList<PostModel> readFromDB() {
        ArrayList<PostModel> posts = new ArrayList<>();
        @SuppressLint("Recycle")
        Cursor c = db.query(DBHelper.MYTABLE, null, null, null, null, null, null);
        if (c.moveToNext()) {
            int postIndex = c.getColumnIndex(DBHelper.POST);
            int nameIndex = c.getColumnIndex(DBHelper.NAME);
            int favorIndex = c.getColumnIndex(DBHelper.FAVOR);
            int addTimeIndex = c.getColumnIndex(DBHelper.ADDTIME);
            do {
                PostModel p = new PostModel();
                int favor = c.getInt(favorIndex);
                if (favor == 1) {
                    p.setElementPureHtml(c.getString(postIndex));
                    p.setName(c.getString(nameIndex));
                    p.setFavor(true);
                    p.setAddTime(c.getLong(addTimeIndex));
                    posts.add(p);
                }
            } while (c.moveToNext());
        }
        c.close();
        posts.sort((o1, o2) -> {
            String time1 = String.valueOf(o1.getAddTime());
            String time2 = String.valueOf(o2.getAddTime());
            return time2.compareTo(time1);
        });
        return posts;
    }
}