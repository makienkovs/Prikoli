package com.makienkovs.prikoli;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class Adapter extends BaseAdapter {

    private ArrayList<PostModel> posts;
    final long[] eventTime = new long[1];

    public Adapter(ArrayList<PostModel> posts) {
        this.posts = posts;
    }

    @Override
    public int getCount() {
        if (posts == null) return 0;
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        if (posts == null) return null;
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "ClickableViewAccessibility"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, null);

        GestureDetector gd = new GestureDetector(parent.getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                eventTime[0] = e.getEventTime();
                addToFavor((PostModel) getItem(position), parent.getContext());
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                if (eventTime[0] == e.getEventTime()) {
                    share((PostModel) getItem(position), parent.getContext());
                }
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });


        convertView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                eventTime[0] = event.getEventTime();
            }
            return gd.onTouchEvent(event);
        });

        fillView(convertView, position);
        return convertView;
    }

    private void addToFavor(PostModel p, Context c) {
        String post = p.getElementPureHtml();
        String name = p.getName();
        boolean favor = p.isFavor();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.POST, post);
        cv.put(DBHelper.NAME, name);
        if (favor) {
            p.setFavor(false);
            cv.put(DBHelper.FAVOR, 0);
            cv.put(DBHelper.ADDTIME, 0);
            Toast.makeText(c, R.string.remove, Toast.LENGTH_SHORT).show();
        } else {
            p.setFavor(true);
            cv.put(DBHelper.FAVOR, 1);
            cv.put(DBHelper.ADDTIME, Calendar.getInstance().getTimeInMillis());
            Toast.makeText(c, R.string.add, Toast.LENGTH_SHORT).show();
        }

        SQLiteDatabase db;
        try {
            DBHelper dbHelper = new DBHelper(c, DBHelper.DB_SQLITE, null, DBHelper.DB_VERSION);
            db = dbHelper.getWritableDatabase();
            db.update(DBHelper.MYTABLE, cv, DBHelper.POST + " = ?", new String[]{post});
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(c, R.string.errorDB, Toast.LENGTH_SHORT).show();
        }
        this.notifyDataSetChanged();
    }

    private void share(PostModel p, Context c) {
        String output = c.getString(R.string.from) + "\n" + Html.fromHtml(p.getElementPureHtml(), Html.FROM_HTML_MODE_LEGACY);
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        share.putExtra(Intent.EXTRA_SUBJECT, R.string.share);
        share.putExtra(Intent.EXTRA_TEXT, output);
        c.startActivity(Intent.createChooser(share, c.getString(R.string.share)));
    }

    private void fillView(View v, int position) {
        final PostModel p = (PostModel) getItem(position);
        TextView post = v.findViewById(R.id.postitem_post);
        TextView name = v.findViewById(R.id.postitem_name);
        ImageView favor = v.findViewById(R.id.postitem_favor);
        post.setText(Html.fromHtml(p.getElementPureHtml(), Html.FROM_HTML_MODE_LEGACY));
        switch (p.getName()) {
            case "bash":
                name.setText(R.string.menu_bash);
                break;
            case "abyss":
                name.setText(R.string.menu_abyss);
                break;
            case "zadolbali":
                name.setText(R.string.menu_zadolbali);
                break;
            case "new anekdot":
                name.setText(R.string.menu_newanekdot);
                break;
            case "new story":
                name.setText(R.string.menu_newstory);
                break;
            case "new aforizm":
                name.setText(R.string.menu_newaforizm);
                break;
            case "new stihi":
                name.setText(R.string.menu_newstihi);
                break;
            default:
                name.setText("");
        }

        if (p.isFavor()) {
            favor.setVisibility(View.VISIBLE);
        } else {
            favor.setVisibility(View.INVISIBLE);
        }
    }
}
