package com.makienkovs.prikoli.ui.anekdot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.makienkovs.prikoli.Adapter;
import com.makienkovs.prikoli.App;
import com.makienkovs.prikoli.DBHandler;
import com.makienkovs.prikoli.PostModel;
import com.makienkovs.prikoli.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class NewanekdotFragment extends Fragment {

    private ListView lvNewanekdot;
    private ArrayList<PostModel> posts;
    private DBHandler dbHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_newanekdot, container, false);
        lvNewanekdot = root.findViewById(R.id.list_newanekdot);

        registerForContextMenu(lvNewanekdot);
        dbHandler = new DBHandler(getContext());

        readPosts();

        App.getApi().getData("new anekdot", 50).enqueue(new Callback<List<PostModel>>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<List<PostModel>> call, Response<List<PostModel>> response) {
                if (response.body() != null) {
                    ArrayList<PostModel> postsTemp = new ArrayList<>(response.body());
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
                        requireActivity().runOnUiThread(()->readPosts());
                    }).start();
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<PostModel>> call, Throwable t) {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        readPosts();
    }

    private void readPosts() {
        try {
            posts = dbHandler.readFromDB("new anekdot");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Adapter adapter = new Adapter(posts);
        lvNewanekdot.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}