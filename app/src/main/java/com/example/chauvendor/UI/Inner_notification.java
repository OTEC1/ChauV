package com.example.chauvendor.UI;

import androidx.annotation.RequiresApi;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Set;

public class Inner_notification extends AppCompatActivity {

    private ProgressDialog pD;
    private UserLocation user;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView totals;
    private long h;
    private List<Map<String, Object>> list2 = new ArrayList<>();
    private String TAG = "notification_inner_track";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_notification);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        totals = (TextView) findViewById(R.id.totals);
        Button mreport = (Button) findViewById(R.id.report);
        Button button = (Button) findViewById(R.id.call_out_to_delivery_agent);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        new utils().bottom_nav(bottomNavigationView, this, progressBar);
        Constants.IMGURL = getIntent().getStringExtra("user_img_url");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            api_call1(new utils().multi_call_method(getApplicationContext(), getString(R.string.CACHE_LIST_OF_VENDORS)));

        button.setOnClickListener(o -> {
            Constants.notification_count = 0;
            progressD(this).show();
            doc(getIntent().getStringExtra("cart_tracker"), getIntent().getStringExtra("order_id"),getIntent().getStringExtra("item_count"),getIntent().getStringExtra("docs_id"),getIntent().getStringExtra("user_id"), new ArrayList<>(), 0, getApplicationContext(), totals.getText().toString());

        });

        mreport.setOnClickListener(h -> {
            startActivity(new Intent(getApplicationContext(), Issues_submit.class).putExtra("order_id", getIntent().getStringExtra("cart_tracker")));
        });
    }


    private void api_call1(List<String> strings) {
        for (int y = 0; y < strings.size(); y++) {
            String current_vendor_index = strings.get(y);
            FirebaseFirestore.getInstance().collection(getString(R.string.USER_PAID_ORDES)).document("Orders").collection(current_vendor_index)
                    .get().addOnCompleteListener(n -> {
                if (n.isSuccessful()) {
                    for (QueryDocumentSnapshot x : n.getResult()) {
                        if (x.get("Cart_tracker").toString().equals(getIntent().getStringExtra("cart_tracker"))) {
                            list2.add(new utils().map(x));
                            set_layout(list2, x.get("star_boi").toString());
                            if (!x.get("star_boi").toString().equals("Self serve"))
                                new utils().sum_quantity(x, getApplicationContext(), totals, 0, 0);
                            else {
                                h += Long.parseLong(x.get("food_price").toString());
                                totals.setText("" + h);
                            }
                        }
                    }
                } else
                    new utils().message2("Error Occurred  " + n.getException(), this);
            });
        }
    }


    private void set_layout(List<Map<String, Object>> list2, String h) {
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        Notification_children_view adapter = new Notification_children_view(getApplicationContext(), list2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }


    //----------------------CALLED IN BOTH CLASS 'INNER_NOTIFICATION & NOTIFICATION MAIN' ----------------//
    public void doc(String cartTracker, String order_id,String item_count,String docs_id,String user_id, ArrayList<Object> strings, int x, Context context, String total) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            user = new utils().GET_VENDOR_CACHED(context, context.getString(R.string.VENDOR));
        CHECK_FOR_NEARBY_DELIVERIES_ON_CLICK(FirebaseFirestore.getInstance().collection(context.getString(R.string.Paid_Vendors_Brand_Section)).document("Orders").collection(user_id).document(docs_id), x, strings, context, total, cartTracker,  item_count,docs_id,user_id,order_id);

    }


    //--------------------------------------Check for nearby vendors -----------------------------------------//
    private void CHECK_FOR_NEARBY_DELIVERIES_ON_CLICK(DocumentReference b, int a, ArrayList<Object> strings, Context context, String total, String cartTracker, String doc_id, String item_count, String user_id, String order_id) {
        if (a == 1)
            b.update(new utils().maps()).addOnCompleteListener(u -> {
                if (u.isSuccessful()) {
                    if (pD != null)
                        pD.dismiss();
                    Toast.makeText(context, "Sent out Request", Toast.LENGTH_SHORT).show();
                } else
                    Log.d(TAG, "Error occurred " + u.getException());
            });
        else
            b.get().addOnCompleteListener(n -> {
                if (n.isSuccessful()) {
                    if (!n.getResult().get("VStatus", Boolean.class))
                        PASS_ON(n.getResult().get("TimeStamp"), strings, total, context, cartTracker,  item_count,doc_id,user_id,order_id);
                    else {
                        if (pD != null) {
                            pD.dismiss();
                            new utils().buildAlertMessageNoGps(this, 1, "Already Sent Request " + context.getString(R.string.app_name) + " is Searching for nearby Delivery Guys...");
                        }else
                              new utils().buildAlertMessageNoGps(context, 1, "Already Sent Request " + context.getString(R.string.app_name) + " is Searching for nearby Delivery Guys...");

                        }
                } else
                    Log.d(TAG, "Error occurred: " + n.getException());

            });

    }


    private void PASS_ON(Object timeStamp, ArrayList<Object> strings, String total, Context context, String cartTracker, String doc_id, String item_count, String user_id,String order_id) {
        FirebaseFirestore.getInstance().collection(context.getString(R.string.DELIVERY_LOCATION)).get().addOnCompleteListener(i -> {
            if (i.isSuccessful()) {
                List<DocumentSnapshot> u = i.getResult().getDocuments();
                for (DocumentSnapshot x : u) {
                    Map<String, Object> use = (Map<String, Object>) x.getData().get("user");
                    GeoPoint geo = x.get("geo_point", GeoPoint.class);
                    if (use.get("token").toString().trim().length() > 0)
                        CHECK_FOR_NEARBY_DELIVERIES_RADIUS(geo.getLatitude(), geo.getLongitude(), Double.toHexString(user.getGeo_point().getLatitude()), Double.toHexString(user.getGeo_point().getLongitude()), use.get("token").toString(), timeStamp, strings, total, cartTracker, doc_id, context, item_count,user_id,order_id);
                }
            } else
                new utils().message2("Error Getting " + i.getException(), this);
        });
    }


    private void CHECK_FOR_NEARBY_DELIVERIES_RADIUS(double latitude1, double longitude1, String latitude, String longtitude, String m, Object timeStamp, ArrayList<Object> strings, String total, String cartTracker, String doc_id, Context context, String item_count,String user_id,String order_id) {

        if (latitude.length() != 0 | longtitude.length() != 0) {

            Location start_p = new Location("Point A");
            start_p.setLatitude(Double.parseDouble(latitude));
            start_p.setLongitude(Double.parseDouble(longtitude));

            Location stop_p = new Location("Point B");
            stop_p.setLatitude(latitude1);
            stop_p.setLongitude(longitude1);

            double final_result = start_p.distanceTo(stop_p);
            long vendor_location = Math.round(final_result);
            if (vendor_location < 10500)
                SUM(m, timeStamp, strings, total, cartTracker, doc_id, context, item_count,user_id,order_id);
            else if (vendor_location < 20500) {
                new utils().message("No Delivery close by increased search ", this);
                SUM(m, timeStamp, strings, total, cartTracker, doc_id, context, item_count,user_id,order_id);
            } else
                Toast.makeText(context, "No Delivery close by", Toast.LENGTH_SHORT).show();

        }

    }

    private void SUM(String m, Object timeStamp, ArrayList<Object> strings, String total, String cartTracker, String doc_id, Context context, String item_count,String user_id,String order_id) {
        strings.add(m);
        Set<Object> token_approved = new HashSet<>(strings);
        GET_PAYMENT_POINT_LOCATION(timeStamp, token_approved, total, cartTracker, doc_id, context, item_count,user_id,order_id);
    }


    private void GET_PAYMENT_POINT_LOCATION(Object timeStamp, Set<Object> data, String total, String cartTracker, String doc_id, Context context, String item_count,String user_id, String order_id) {
        FirebaseFirestore.getInstance().collection(context.getString(R.string.USER_LOCATION)).document(user_id).get().addOnCompleteListener(w -> {
            if (w.isSuccessful()) {
                UserLocation use = w.getResult().toObject(UserLocation.class);
                assert use != null;
                if (Constants.IMGURL != null)
                    SEND_NOTIFICATION(data, use.getUser().getUser_id(), user.getUser().getPhone(), use.getGeo_point(), user.getUser().getImg_url(), use.getUser().getPhone(), timeStamp, use.getUser().getName(), total, cartTracker, doc_id, item_count,order_id, context);
                else
                    GET_USER_IMG(use.getUser().getUser_id(), data, user.getUser().getPhone(), use.getGeo_point(), user.getUser().getImg_url(), use.getUser().getPhone(), timeStamp, use.getUser().getName(), total, cartTracker, doc_id,  item_count,order_id,context);

            } else
                Log.d(TAG, "GET_USER_DATA Error occurred " + w.getException());

        });

    }


    private void GET_USER_IMG(String user_id, Set<Object> token, String phone, GeoPoint geo_point, String img_url, String phone1, Object time, String name, String total, String cartTracker, String doc_id,  String item_count,String order_id,Context context) {
        FirebaseFirestore.getInstance().collection(context.getString(R.string.USER_REG)).document(user_id)
                .get().addOnCompleteListener(u -> {
            if (u.isSuccessful()) {
                Constants.IMGURL = u.getResult().get("img_url").toString();
                SEND_NOTIFICATION(token, user_id, phone, geo_point, img_url, phone1, time, name, total, cartTracker, doc_id, item_count,order_id, context);
            } else
                new utils().message2("Error occurred ", this);
        });
    }


    private void SEND_NOTIFICATION(Set<Object> token_approved, String user_id, String phone, GeoPoint geo_point, String img_url, String phone_no, Object timestamp, String name, String total, String cartTracker, String doc_id, String item_count,String order_id, Context context) {
        if (Constants.notification_count == 0)
            for (Object to : token_approved) {
                Log.d(TAG, "SEND_NOTIFICATION: "+to);
                Map<String, Object> pl = new HashMap<>();
                pl.put("Client_ID", user_id);
                pl.put("Client_name", name);
                pl.put("Vendor_Phone", phone);
                pl.put("doc_id_Gen", doc_id);
                pl.put("Order_id",  cartTracker);
                pl.put("Order_items", Integer.parseInt(item_count));
                pl.put("user_img_url", Constants.IMGURL);
                pl.put("Vendor", "Vendor: " + user.getUser().getName());
                pl.put("Vendor_img_url", img_url);
                pl.put("Vendor_ID", FirebaseAuth.getInstance().getUid());
                pl.put("Vendor_business_D", user.getUser().getBusiness_details());
                pl.put("Pick_up_geo_point", user.getGeo_point());
                pl.put("Drop_off_geo_point", geo_point);
                pl.put("Drop_off_phone_no", phone_no);
                pl.put("Timestamp", timestamp);
                pl.put("Total", total.replace("₦", ""));
                Pusher.pushrequest push = new Pusher.pushrequest(pl, to);
                try {
                    Pusher.sendPush(push);
                    Constants.notification_count++;
                    doc(cartTracker, order_id,item_count,doc_id,user_id, new ArrayList<>(), 1, context, total);
                    Log.d(TAG, "SEND_NOTIFICATION: ");
                } catch (Exception ex) {
                    Log.d(TAG, "SEND_NOTIFICATION Error occurred " + ex.toString());
                }
            }
    }


    //--------------------------------------eND Check for nearby vendors -----------------------------------------//


    public ProgressDialog progressD(AppCompatActivity compatActivity) {
        pD = new ProgressDialog(compatActivity);
        pD.show();
        pD.setContentView(R.layout.custom_progress);
        pD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return pD;
    }


}