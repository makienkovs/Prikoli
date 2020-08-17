package com.makienkovs.prikoli;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.makienkovs.prikoli.ui.anekdot.NewaforizmFragment;
import com.makienkovs.prikoli.ui.anekdot.NewanekdotFragment;
import com.makienkovs.prikoli.ui.anekdot.NewstihiFragment;
import com.makienkovs.prikoli.ui.anekdot.NewstoryFragment;
import com.makienkovs.prikoli.ui.bashim.AbyssFragment;
import com.makienkovs.prikoli.ui.bashim.BashFragment;
import com.makienkovs.prikoli.ui.zadolbali.ZadolbaliFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class Downloader {
    private DBHandler dbHandler;
    private Context c;
    private final String[] resources = new String[] {"bash", "abyss", "zadolbali", "new aforizm", "new anekdot", "new stihi", "new story"};
    private boolean fail = false;

    public Downloader(Context c){
        this.c = c;
        dbHandler = new DBHandler(c);
    }

    private void download(String resourceName, int count, boolean removeFirst, boolean removeLast) {
        App.getApi().getData(resourceName, count).enqueue(new Callback<List<PostModel>>() {
            @SuppressLint("CheckResult")
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<List<PostModel>> call, Response<List<PostModel>> response) {
                if (response.body() != null) {
                    ArrayList<PostModel> postsTemp = new ArrayList<>(response.body());
                    if (removeFirst && postsTemp.size() > 0) {
                        postsTemp.remove(0);
                    }
                    if (removeLast && postsTemp.size() > 0) {
                        postsTemp.remove(postsTemp.size() - 1);
                    }
                    new Thread(()-> {
                        for (int i = postsTemp.size() - 1; i >= 0 ; i--) {
                            postsTemp.get(i).setTime(Calendar.getInstance().getTimeInMillis());
                            try {
                                TimeUnit.MILLISECONDS.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        dbHandler.writeToDB(postsTemp);
                        createObservables(resourceName);
                    }).start();
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<PostModel>> call, Throwable t) {
                if (!fail) {
                    fail = true;
                    Toast.makeText(c, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createObservables(String resourceName) {
        Observable<String> observable = Observable.just(resourceName);
        switch (resourceName) {
            case "bash": if (BashFragment.observer == null || !MainActivity.currentFragment.equalsIgnoreCase(c.getString(R.string.menu_bash))) return; observable.subscribe(BashFragment.observer); break;
            case "abyss": if (AbyssFragment.observer == null || !MainActivity.currentFragment.equalsIgnoreCase(c.getString(R.string.menu_abyss))) return; observable.subscribe(AbyssFragment.observer); break;
            case "zadolbali": if (ZadolbaliFragment.observer == null || !MainActivity.currentFragment.equalsIgnoreCase(c.getString(R.string.menu_zadolbali))) return; observable.subscribe(ZadolbaliFragment.observer); break;
            case "new aforizm": if (NewaforizmFragment.observer == null || !MainActivity.currentFragment.equalsIgnoreCase(c.getString(R.string.menu_newaforizm))) return; observable.subscribe(NewaforizmFragment.observer); break;
            case "new anekdot": if (NewanekdotFragment.observer == null || !MainActivity.currentFragment.equalsIgnoreCase(c.getString(R.string.menu_newanekdot))) return; observable.subscribe(NewanekdotFragment.observer); break;
            case "new stihi": if (NewstihiFragment.observer == null || !MainActivity.currentFragment.equalsIgnoreCase(c.getString(R.string.menu_newstihi))) return; observable.subscribe(NewstihiFragment.observer); break;
            case "new story": if (NewstoryFragment.observer == null || !MainActivity.currentFragment.equalsIgnoreCase(c.getString(R.string.menu_newstory))) return; observable.subscribe(NewstoryFragment.observer); break;
        }
    }

    public void downloadAll() {
        int COUNT = 50;
        try {
            download(resources[0], COUNT, true, false);
            download(resources[1], COUNT, false, false);
            download(resources[2], COUNT, false, false);
            download(resources[3], COUNT, false, false);
            download(resources[4], COUNT, false, false);
            download(resources[5], COUNT, false, false);
            download(resources[6], COUNT, false, true);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(c, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
}
