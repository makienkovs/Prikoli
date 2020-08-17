package com.makienkovs.prikoli.ui.anekdot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.makienkovs.prikoli.Adapter;
import com.makienkovs.prikoli.DBHandler;
import com.makienkovs.prikoli.MainActivity;
import com.makienkovs.prikoli.PostModel;
import com.makienkovs.prikoli.R;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class NewstoryFragment extends Fragment {

    private ListView lvNewstory;
    private ArrayList<PostModel> posts;
    private DBHandler dbHandler;
    public static Observer<String> observer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_newstory, container, false);
        lvNewstory = root.findViewById(R.id.list_newstory);

        registerForContextMenu(lvNewstory);
        dbHandler = new DBHandler(getContext());

        readPosts();

        observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String s) {
                if (MainActivity.currentFragment.equalsIgnoreCase(getString(R.string.menu_newstory))) {
                    requireActivity().runOnUiThread(()->readPosts());
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        readPosts();
    }

    private void readPosts() {
        try {
            posts = dbHandler.readFromDB("new story");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Adapter adapter = new Adapter(posts);
        lvNewstory.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}