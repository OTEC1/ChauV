package com.example.chauvendor.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.chauvendor.Adapter.Notification_children_view;
import com.example.chauvendor.R;
import com.example.chauvendor.util.utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Inner_notification extends AppCompatActivity {

    private CollectionReference collectionReference;
    private RecyclerView recyclerView;
    private Notification_children_view adapter;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;


    private List<String> list = new ArrayList<>();
    private List<Map<String, Object>> list2 = new ArrayList<>();


    private String TAG = "notification_track";
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_notification);

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        new utils().bottom_nav(bottomNavigationView, this, new Bundle());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            api_call1(new utils().multi_call_method(getApplicationContext(), getString(R.string.CACHE_LIST_OF_VENDORS)));

    }

    private void api_call1(List<String> strings) {
        for (int y = 0; y < strings.size(); y++) {
            collectionReference = FirebaseFirestore.getInstance().collection(getString(R.string.USER_PAID_ORDES)).document("Orders").collection(strings.get(y));
            collectionReference.get().addOnCompleteListener(n -> {
                if (n.isSuccessful()) {
                    for (QueryDocumentSnapshot x : n.getResult()) {
                        if (new utils().TIME_FORMAT(x.get("TimeStamp").toString()).equals(getIntent().getStringExtra("data_key")))
                            list2.add(new utils().map(x, 1, ""));
                        if (i == list.size()) {
                            set_layout(list2);
                            Log.d(TAG, String.valueOf(list2));
                        }
                    }
                } else
                    new utils().message2("Error Occurred  " + n.getException(), this);
            });
        }
    }


    private void set_layout(List<Map<String, Object>> list2) {
        Log.d(TAG, "set_layout: 123");
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new Notification_children_view(getApplicationContext(), list2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }
}