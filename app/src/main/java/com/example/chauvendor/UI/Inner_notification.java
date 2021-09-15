package com.example.chauvendor.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chauvendor.Adapter.Notification_children_view;
import com.example.chauvendor.R;
import com.example.chauvendor.Running_Service.Pusher;
import com.example.chauvendor.util.UserLocation;
import com.example.chauvendor.util.utils;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Inner_notification extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private CollectionReference collectionReference;
    private DocumentReference doc;
    private Notification_children_view adapter;
    private ProgressDialog progressDialog;
    private UserLocation user;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView totals;
    private Button button;


    private List<String> list = new ArrayList<>(), token_gotten;
    private List<Map<String, Object>> list2 = new ArrayList<>();
    private Set<String> token_approved;


    private String TAG = "notification_inner_track", current_vendor_index;
    private boolean status = true;


    @Override
    protected void onResume() {
        super.onResume();
        token_gotten = new ArrayList<>();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_notification);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        totals = (TextView) findViewById(R.id.totals);
        button = (Button) findViewById(R.id.call_out_to_delivery_agent);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        new utils().bottom_nav(bottomNavigationView, this, new Bundle());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            api_call1(new utils().multi_call_method(getApplicationContext(), getString(R.string.CACHE_LIST_OF_VENDORS)));

        Log.d(TAG, "onCreate: " + getIntent().getStringExtra("docID"));

        doc = FirebaseFirestore.getInstance().collection(getString(R.string.Paid_Vendors_Brand_Section))
                .document("Orders")
                .collection(getIntent().getStringExtra("docs_key"))
                .document(getIntent().getStringExtra("docID"));


        button.setOnClickListener(o -> {
            progressD().show();
            CHECK_FOR_NEARBY_DELIVERIES_ON_CLICK(doc, 0);
        });
    }


    private void api_call1(List<String> strings) {
        for (int y = 0; y < strings.size(); y++) {
            current_vendor_index = strings.get(y);
            collectionReference = FirebaseFirestore.getInstance().collection(getString(R.string.USER_PAID_ORDES)).document("Orders").collection(current_vendor_index);
            collectionReference.get().addOnCompleteListener(n -> {
                if (n.isSuccessful()) {
                    for (QueryDocumentSnapshot x : n.getResult()) {
                        if (x.get("Cart_tracker").toString().equals(getIntent().getStringExtra("data_key"))) {
                            list2.add(new utils().map(x, 1, ""));
                            new utils().sum_quantity(x, getApplicationContext(), totals, 0, 0);
                            set_layout(list2);
                        }
                    }
                } else
                    new utils().message2("Error Occurred  " + n.getException(), this);
            });
        }
    }


    private void set_layout(List<Map<String, Object>> list2) {
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new Notification_children_view(getApplicationContext(), list2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }


    //--------------------------------------Check for nearby vendors -----------------------------------------//
    private Object CHECK_FOR_NEARBY_DELIVERIES_ON_CLICK(DocumentReference b, int a) {
        if (a == 1)
            b.update(new utils().maps()).addOnCompleteListener(u -> {
                if (u.isSuccessful()) {
                    progressDialog.dismiss();
                    new utils().message2("Sent out Request", this);
                } else
                    Log.d(TAG, "update:1 " + u.getException());
            });
        else
            b.get().addOnCompleteListener(n -> {
                if (n.isSuccessful()) {
                    if (!n.getResult().getBoolean("VStatus"))
                        PASS_ON(n.getResult().get("TimeStamp"));
                    else {
                        progressDialog.dismiss();
                        new utils().buildAlertMessageNoGps(this, 1, "Already Sent Request " + getString(R.string.app_name) + " is Searching for nearby Delivery Guys...");
                    }
                } else
                    Log.d(TAG, "update2: " + n.getException());

            });
        return null;
    }


    private void PASS_ON(Object timeStamp) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            user = new utils().GET_VENDOR_CACHED(getApplicationContext(), getString(R.string.VENDOR));
        FirebaseFirestore.getInstance().collection(getString(R.string.DELIVERY_LOCATION)).whereLessThan("bad", 10).get().addOnCompleteListener(i -> {
            if (i.isSuccessful()) {
                List<UserLocation> u = i.getResult().toObjects(UserLocation.class);
                for (UserLocation x : u) {
                    if (token_gotten.size() != 10) {
                        token_gotten = CHECK_FOR_NEARBY_DELIVERIES_RADIUS(x.getGeo_point().getLatitude(), x.getGeo_point().getLongitude(), Double.toHexString(user.getGeo_point().getLatitude()), Double.toHexString(user.getGeo_point().getLongitude()), x.getUser().getToken());
                    }
                }

                if (token_gotten != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        token_gotten.forEach(x -> token_approved = new HashSet<>(token_gotten));
                    MINER_OUT_INDEX_VENDOR(current_vendor_index, timeStamp);
                }
            } else
                new utils().message2("Error Getting " + i.getException(), this);

            Log.d(TAG, "GET_VENDOR_LOCATION: " + user.getGeo_point() + "");
        });
    }


    private void MINER_OUT_INDEX_VENDOR(String current_vendor_index, Object timeStamp) {
        FirebaseFirestore.getInstance().collection(getString(R.string.USER_LOCATION)).document(current_vendor_index)
                .get().addOnCompleteListener(w -> {
            if (w.isSuccessful()) {
                UserLocation user1 = w.getResult().toObject(UserLocation.class);
                assert user1 != null;
                SEND_NOTIFICATION(token_approved, user1.getUser().getUser_id(), user1.getUser().getPhone(), user1.getGeo_point(), user.getUser().getImg_url(), user.getUser().getPhone(), timeStamp);
            } else
                Log.d(TAG, "MINER_OUT_INDEX_VENDOR: " + w.getException());

        });

    }


    private List<String> CHECK_FOR_NEARBY_DELIVERIES_RADIUS(double latitude1, double longitude1, String latitude, String longtitude, String m) {

        if (latitude.length() != 0 | longtitude.length() != 0) {

            Location start_p = new Location("Point A");
            start_p.setLatitude(Double.parseDouble(latitude));
            start_p.setLongitude(Double.parseDouble(longtitude));

            Location stop_p = new Location("Point B");
            stop_p.setLatitude(latitude1);
            stop_p.setLongitude(longitude1);
            double final_result = start_p.distanceTo(stop_p);
            long vendor_location = Math.round(final_result);
            if (vendor_location < 3000)
                list.add(m);

        }
        return list;
    }


    private void SEND_NOTIFICATION(Set<String> token_approved, String user_id, String phone, GeoPoint geo_point, String img_url, String phone_no, Object timestamp) {

        for (String to : token_approved) {

            Map<String, Object> pay_load = new HashMap<>();
            pay_load.put("Vendor", "Vendor: " + user.getUser().getName());
            pay_load.put("Vendor_img_url", img_url);
            pay_load.put("Vendor_ID", FirebaseAuth.getInstance().getUid());
            pay_load.put("Client_ID", user_id);
            pay_load.put("Vendor_Phone", phone);
            pay_load.put("Order_id", getIntent().getStringExtra("data_key"));
            pay_load.put("Order_items", list2.size());
            pay_load.put("Vendor_business_D", user.getUser().getBusiness_details());
            pay_load.put("Pick_up_geo_point", user.getGeo_point());
            pay_load.put("Drop_off_geo_point", geo_point);
            pay_load.put("Drop_off_phone_no", phone_no);
            pay_load.put("Timestamp", timestamp);
            pay_load.put("doc_id_Gen", getIntent().getStringExtra("docID"));
            Pusher.pushrequest push = new Pusher.pushrequest(pay_load, to);
            try {
                Pusher.sendPush(push);
                CHECK_FOR_NEARBY_DELIVERIES_ON_CLICK(doc, 1);
            } catch (Exception ex) {
                Log.d(TAG, "SEND_NOTIFICATION: " + ex.toString());
            }
        }
    }
    //--------------------------------------eND Check for nearby vendors -----------------------------------------//


    public ProgressDialog progressD() {
        progressDialog = new ProgressDialog(Inner_notification.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return progressDialog;
    }

}