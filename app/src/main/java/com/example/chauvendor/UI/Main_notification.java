package com.example.chauvendor.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.chauvendor.Adapter.Notification_main_view;
import com.example.chauvendor.R;
import com.example.chauvendor.util.utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main_notification extends AppCompatActivity {


    //Next to work on
    private FirebaseFirestore mfirestore;
    private CollectionReference collectionReference;
    private RecyclerView recyclerView;
    private Notification_main_view adapter;
    private ProgressBar progressBar;
    private BottomNavigationView bottom_nav;
    private Bundle bundle = new Bundle();


    private List<String> list3;
    private List<Map<String, Object>> list2;


    private String TAG = "Main_notification_shit";
    private int i;


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Main_notification.class));
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_notification);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        bottom_nav = (BottomNavigationView) findViewById(R.id.bottomNav);
        progressBar = (ProgressBar) findViewById(R.id.progressBar6);
        mfirestore = FirebaseFirestore.getInstance();
        new utils().bottom_nav(bottom_nav, this, bundle);
        list3 = new ArrayList<>();
        list2 = new ArrayList<>();
        if (getIntent().getStringExtra("docs") != null) {
            list3.add(getIntent().getStringExtra("ID"));
            Check_for_vendor_id(list3, 1);
            Log.d(TAG, " DOWN");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Check_for_vendor_id(new utils().multi_call_method(getApplicationContext(), getString(R.string.CACHE_LIST_OF_VENDORS)), 0);
                Log.d(TAG, "UP");
            }

        }
    }


    private void Check_for_vendor_id(List<String> list, int os) {
        for (i = 0; i < list.size(); i++) {
            String a = list.get(i);
            collectionReference = mfirestore.collection(getString(R.string.Paid_Vendors_Brand_Section)).document("Orders").collection(a);
            collectionReference.get().addOnCompleteListener(n -> {
                if (n.isSuccessful()) {
                    for (QueryDocumentSnapshot x : n.getResult()) {
                        if (os == 0) {
                            if (Objects.requireNonNull(x.get("order_id")).toString().equals(FirebaseAuth.getInstance().getUid())) {
                                list2.add(new utils().map(x, 2, a));
                                if (i == list.size()) {
                                    set_layout(list2);
                                    Log.d(TAG, String.valueOf(list2));
                                }
                            }
                        } else if (os == 1) {
                            if (x.getId().equals(getIntent().getStringExtra("docs"))) {
                                list2.add(new utils().map(x, 2, a));
                                Log.d(TAG, a + "     " + x.getId() + "    " + getIntent().getStringExtra("docs") + "     " + getIntent().getStringExtra("docs") + "    " + list2);
                                set_layout(list2);
                            }
                        }
                    }
                } else
                    new utils().message2("Error Occurred  " + n.getException(), this);
            });
        }

    }


    private void set_layout(List<Map<String, Object>> list2) {
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new Notification_main_view(getApplicationContext(), list2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }
}