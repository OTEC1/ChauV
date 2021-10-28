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
import com.example.chauvendor.util.Constants;
import com.example.chauvendor.util.utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Main_notification extends AppCompatActivity {


    //Next to work on
    private FirebaseFirestore mfirestore;
    private CollectionReference collectionReference;
    private RecyclerView recyclerView;
    private Notification_main_view adapter;
    private ProgressBar progressBar;
    private BottomNavigationView bottom_nav;
    private Bundle bundle = new Bundle();
    private ListIterator<Map<String, Object>> op;


    private List<Map<String, Object>> list2;
    private List<String> list3;
    private List<Map<String, Object>> list4;


    private String TAG = "Main_notification_shit";
    private int i;


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_notification);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        bottom_nav = (BottomNavigationView) findViewById(R.id.bottomNav);
        progressBar = (ProgressBar) findViewById(R.id.progressBar6);
        mfirestore = FirebaseFirestore.getInstance();
        new utils().bottom_nav(bottom_nav, this, progressBar);
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();
        Constants.IMGURL = getIntent().getStringExtra("user_img_url");



        if (getIntent().getStringExtra("docs") != null) {
            list3.add(getIntent().getStringExtra("ID"));
            Check_for_vendor_id(list3, 1);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Check_for_vendor_id(new utils().multi_call_method(getApplicationContext(), getString(R.string.CACHE_LIST_OF_VENDORS)), 0);
            }
        }

    }


    private void Check_for_vendor_id(List<String> list, int os) {
        for (i = 0; i < list.size(); i++) {
            String a = list.get(i);
            mfirestore.collection(getString(R.string.Paid_Vendors_Brand_Section)).document("Orders").collection(a).get().addOnCompleteListener(n -> {
                if (n.isSuccessful()) {
                    for (QueryDocumentSnapshot x : n.getResult()) {
                        if (os == 0) {
                            list2 = new ArrayList<>();
                            if (Objects.requireNonNull(x.get("order_id")).toString().equals(FirebaseAuth.getInstance().getUid())) {
                                list2.add(new utils().map_data(x, a));
                                op = list2.listIterator();
                                Check_to_popout(op);

                            }
                        } else if (os == 1) {
                            list2 = new ArrayList<>();
                            if (x.getId().equals(getIntent().getStringExtra("docs"))) {
                                list2.add(new utils().map_data(x, a));
                                    set_layout(list2);

                            }
                        }
                    }
                } else
                    new utils().message2("Error Occurred  " + n.getException(), this);
            });
        }

    }

    private void Check_to_popout(ListIterator<Map<String, Object>> op) {
        while (op.hasNext()) {
            Map<String, Object> map = op.next();
            list4.add(new utils().mapping(map));
            if (!op.hasNext() && list4.size() > 0) {
                set_layout(MapSort(list4));
            }
        }
    }


    public List<Map<String, Object>> MapSort(List<Map<String, Object>> mapping) {
        List<Map<String, Object>> h = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Collections.sort(mapping, new sortItems());
        for (Map<String, Object> c : mapping)
            h.add(c);
        return h;
    }


    static class sortItems implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            return Objects.requireNonNull(o1.get("TimeStamp")).toString().compareTo(Objects.requireNonNull(o2.get("TimeStamp")).toString());
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