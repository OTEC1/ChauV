package com.example.chauvendor.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.chauvendor.util.Constants;
import com.example.chauvendor.util.User;
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
    private Notification_children_view adapter;
    private ProgressDialog progressDialog;
    private UserLocation user1;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView totals;
    private Button button, mreport;


    private List<String> token_gotten;
    private List<Map<String, Object>> list2 = new ArrayList<>();
    private Set<String> token_approved;


    private String TAG = "notification_inner_track", current_vendor_index;
    private boolean status = true;


    @Override
    protected void onResume() {
        super.onResume();
        token_approved = new HashSet<>();
        token_gotten = new ArrayList<>();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_notification);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        totals = (TextView) findViewById(R.id.totals);
        mreport = (Button) findViewById(R.id.report);
        button = (Button) findViewById(R.id.call_out_to_delivery_agent);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        new utils().bottom_nav(bottomNavigationView, this, new Bundle());

        Constants.IMGURL = getIntent().getStringExtra("user_img_url");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            api_call1(new utils().multi_call_method(getApplicationContext(), getString(R.string.CACHE_LIST_OF_VENDORS)));

        Log.d(TAG, "onCreate: " + getIntent().getStringExtra("docID") + "    " + Constants.IMGURL);


        button.setOnClickListener(o -> {
            progressD().show();
            CHECK_FOR_NEARBY_DELIVERIES_ON_CLICK(doc(), 0);
            Constants.notification_count = 0;
        });

        mreport.setOnClickListener(h -> {
            startActivity(new Intent(getApplicationContext(), Issues_submit.class));
        });
    }

    private DocumentReference doc() {
        return FirebaseFirestore.getInstance().collection(getString(R.string.Paid_Vendors_Brand_Section))
                .document("Orders").collection(getIntent().getStringExtra("docs_key"))
                .document(getIntent().getStringExtra("docID"));
    }


    private void api_call1(List<String> strings) {
        for (int y = 0; y < strings.size(); y++) {
            current_vendor_index = strings.get(y);
            collectionReference = FirebaseFirestore.getInstance().collection(getString(R.string.USER_PAID_ORDES)).document("Orders").collection(current_vendor_index);
            collectionReference.get().addOnCompleteListener(n -> {
                if (n.isSuccessful()) {
                    for (QueryDocumentSnapshot x : n.getResult()) {
                        if (x.get("Cart_tracker").toString().equals(getIntent().getStringExtra("data_key"))) {
                            list2.add(new utils().map(x));
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
                    new utils().message("Sent out Request", this);
                } else
                    Log.d(TAG, "update:1 " + u.getException());
            });
        else
            b.get().addOnCompleteListener(n -> {
                if (n.isSuccessful()) {
                    if (!n.getResult().get("VStatus", Boolean.class))
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


    private void GET_USER_IMG(String user_id, Set<String> token, String phone, GeoPoint geo_point, String img_url, String phone1, Object time, String name) {
        Log.d(TAG, "GET_USER_IMG: ");
        FirebaseFirestore.getInstance().collection(getString(R.string.USER_REG)).document(user_id)
                .get().addOnCompleteListener(u -> {
            if (u.isSuccessful()) {
                Constants.IMGURL = u.getResult().get("img_url").toString();
                SEND_NOTIFICATION(token, user_id, phone, geo_point, img_url, phone1, time,name);
            } else
                new utils().message2("Error occurred ", this);
        });
    }


    private void PASS_ON(Object timeStamp) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            user1 = new utils().GET_VENDOR_CACHED(getApplicationContext(), getString(R.string.VENDOR));
        FirebaseFirestore.getInstance().collection(getString(R.string.DELIVERY_LOCATION)).get().addOnCompleteListener(i -> {
            if (i.isSuccessful()) {
                List<DocumentSnapshot> u = i.getResult().getDocuments();
                for (DocumentSnapshot x : u) {
                    Map<String, Object> user = (Map<String, Object>) x.getData().get("user");
                    GeoPoint geo = x.get("geo_point", GeoPoint.class);
                    if (user.get("token").toString().trim().length() > 0)
                        CHECK_FOR_NEARBY_DELIVERIES_RADIUS(geo.getLatitude(), geo.getLongitude(), Double.toHexString(user1.getGeo_point().getLatitude()), Double.toHexString(user1.getGeo_point().getLongitude()), user.get("token").toString(), timeStamp);
                    Log.d(TAG, "PASS_ON: " + geo + "  " + user1.getGeo_point());
                }
            } else
                new utils().message2("Error Getting " + i.getException(), this);
        });
    }


    private void CHECK_FOR_NEARBY_DELIVERIES_RADIUS(double latitude1, double longitude1, String latitude, String longtitude, String m, Object timeStamp) {

        if (latitude.length() != 0 | longtitude.length() != 0) {

            Location start_p = new Location("Point A");
            start_p.setLatitude(Double.parseDouble(latitude));
            start_p.setLongitude(Double.parseDouble(longtitude));

            Location stop_p = new Location("Point B");
            stop_p.setLatitude(latitude1);
            stop_p.setLongitude(longitude1);

            double final_result = start_p.distanceTo(stop_p);
            long vendor_location = Math.round(final_result);
            if (vendor_location < 5000)
                SUM(m, timeStamp);
            else if (vendor_location < 7000) {
                new utils().message("No Delivery close by increased search ", this);
                SUM(m, timeStamp);
            } else
                new utils().message2("No Delivery close by", this);


        }

    }

    private void SUM(String m, Object timeStamp) {
        token_gotten.add(m);
        token_approved = new HashSet<>(token_gotten);
        GET_USER_DATA(timeStamp, token_approved);
        Log.d(TAG, "CHECK_FOR_NEARBY_DELIVERIES_RADIUS: checked ");
    }


    private void GET_USER_DATA(Object timeStamp, Set<String> data) {
        FirebaseFirestore.getInstance().collection(getString(R.string.USER_LOCATION)).document(getIntent().getStringExtra("docs_key")).get().addOnCompleteListener(w -> {
            if (w.isSuccessful()) {
                UserLocation user1 = w.getResult().toObject(UserLocation.class);
                assert user1 != null;
                if (Constants.IMGURL != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        SEND_NOTIFICATION(data, user1.getUser().getUser_id(), user1.getUser().getPhone(), user1.getGeo_point(), new utils().GET_VENDOR_CACHED(getApplicationContext(), getString(R.string.VENDOR)).getUser().getImg_url(), user1.getUser().getPhone(), timeStamp,user1.getUser().getName());
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        GET_USER_IMG(user1.getUser().getUser_id(), data , user1.getUser().getPhone(), user1.getGeo_point(), new utils().GET_VENDOR_CACHED(getApplicationContext(), getString(R.string.VENDOR)).getUser().getImg_url(), user1.getUser().getPhone(), timeStamp,user1.getUser().getName());
                }
            } else
                Log.d(TAG, "MINER_OUT_INDEX_VENDOR: " + w.getException());

        });

    }


    private void SEND_NOTIFICATION(Set<String> token_approved, String user_id, String phone, GeoPoint geo_point, String img_url, String phone_no, Object timestamp,String name) {
        if (Constants.notification_count == 0)
            for (String to : token_approved) {
                Log.d(TAG, "SEND_NOTIFICATION: " + to);
                Map<String, Object> pay_load = new HashMap<>();
                pay_load.put("Client_ID", user_id);
                pay_load.put("Client_name", name);
                pay_load.put("Vendor_Phone", phone);
                pay_load.put("doc_id_Gen", getIntent().getStringExtra("docID"));
                pay_load.put("Order_id", getIntent().getStringExtra("data_key"));
                pay_load.put("Order_items", list2.size());
                pay_load.put("user_img_url", Constants.IMGURL);
                pay_load.put("Vendor", "Vendor: " + user1.getUser().getName());
                pay_load.put("Vendor_img_url", img_url);
                pay_load.put("Vendor_ID", FirebaseAuth.getInstance().getUid());
                pay_load.put("Vendor_business_D", user1.getUser().getBusiness_details());
                pay_load.put("Pick_up_geo_point", user1.getGeo_point());
                pay_load.put("Drop_off_geo_point", geo_point);
                pay_load.put("Drop_off_phone_no", phone_no);
                pay_load.put("Timestamp", timestamp);
                Pusher.pushrequest push = new Pusher.pushrequest(pay_load, to);
                try {
                    Pusher.sendPush(push);
                    CHECK_FOR_NEARBY_DELIVERIES_ON_CLICK(doc(), 1);
                    Constants.notification_count++;
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